package ca.chrisbarrett.bubblecount;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ca.chrisbarrett.bubblecount.game.AlphabetGameEngine;
import ca.chrisbarrett.bubblecount.game.GameEngine;
import ca.chrisbarrett.bubblecount.service.BackgroundMusicManager;
import ca.chrisbarrett.bubblecount.util.FontCache;
import ca.chrisbarrett.bubblecount.util.PaintCache;
import ca.chrisbarrett.bubblecount.util.SpriteCache;
import ca.chrisbarrett.bubblecount.util.TextFormat;
import ca.chrisbarrett.bubblecount.view.game.model.BubbleSprite;
import ca.chrisbarrett.bubblecount.view.game.model.Sprite;

/**
 * A helper Activity that starts and stops the {@link ca.chrisbarrett.bubblecount.GameActivity.GameView}
 *
 * @author Chris Barrett
 * @see android.support.v7.app.AppCompatActivity
 * @since Jun 26, 2016
 */
public class GameActivity extends AppCompatActivity implements BackgroundMusicManager.OnBackgroundMusicListener {

    private static final String TAG = "GameActivity";
    private static final BackgroundMusicManager MUSIC_MANAGER = BackgroundMusicManager.getInstance();

    private static boolean isContinueMusic;
    private boolean isMusicOn;
    private GameView gameView;
    private volatile boolean isRunning; // used to control the game Thread. volatile to make sure there is no thread issues.
    private Thread gameThread = null;

    //
    // LifeCycles Events Begin Here
    //

    /**
     * Creates a gameView object and assigns to the View. Also sets up the MUSIC_MANAGER
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);
        MUSIC_MANAGER.setOnBackgroundMusicListener(this);
    }

    /**
     * Makes a request to load the music and checks to see if the music is on.
     */
    @Override
    protected void onResume () {
        super.onResume();
        isContinueMusic = false;
        gameView.onResume();
        MUSIC_MANAGER.initialize(this, R.raw.background);
        isMusicOn = PreferenceManager.getDefaultSharedPreferences(this).
                getBoolean(getString(R.string.pref_music_is_on_key),
                        getResources().getBoolean(R.bool.pref_music_is_on_default));
    }

    /**
     * Checks to see if this is a return to the calling Activity. If not, shuts down the MUSIC_MANAGER
     */
    @Override
    protected void onPause () {
        super.onPause();
        gameView.onPause();
        if (!isContinueMusic) {
            Log.d(TAG, "onPause called and releasing MUSIC_MANAGER.");
            MUSIC_MANAGER.musicRelease();
        }
    }

    /**
     * Makes sure triggering the back button does not accidentally shut down the music if playing
     */
    @Override
    public void onBackPressed () {
        super.onBackPressed();
        Log.d(TAG, "Back button pressed. Making sure isContinueMusic continues.");
        isContinueMusic = true;
    }

    //
    // Listeners begin here
    //

    /**
     * Starts the music when the MUSIC_MANAGER advises the music is ready
     */
    @Override
    public void onMusicReady () {
        if (isMusicOn && !MUSIC_MANAGER.isPlaying()) {
            Log.d(TAG, "Calling for music to start.");
            MUSIC_MANAGER.musicStart();
        }
    }


    //
    //  Inner classes begin here
    //

    /**
     * The View for the GameFeed, extending {@link  android.view.SurfaceView}. This View will run on a
     * separate Thread from the UI.
     * <p/>
     * <ol>
     * <li>Bubbles are randomly drawn on the GameFeed area of the screen. The actual implementation,
     * however, maybe less if the available  screen dimensions are to hold all the Sprites.</li>
     * <li>{@link GameView#VERTICAL_DIVIDE_RATIO} defines the ratio between the Sprite area and the
     * Text area. Text area is used to hold a questionText, or statement to be displayed to the player -
     * such as "1 + 1 = ?" or "1, 2, 3, ?" </li>
     * </ol>
     *
     * @author Chris Barrett
     * @see android.view.SurfaceView;
     * @since Jun 26, 2016
     */
    public class GameView extends SurfaceView implements Runnable {

        public static final float VERTICAL_DIVIDE_RATIO = 0.8f;
        public static final int BACKGROUND_COLOR = Color.BLACK;
        public static final int SPRITE_PLACEMENT_ATTEMPTS = 5;
        public static final int MAX_SPRITES = 20;
        public static final int MAX_ROUNDS = 5;
        private static final String TAG = "GameView";

        private Context context;
        private SurfaceHolder surfaceHolder;
        private Canvas canvas;
        private Paint textPaint;
        private Paint drawPaint;

        private float screenWidth;
        private float gameAreaHeight;
        private float textAreaHeight;

        private String gameEngineQuestion;
        private String gameEngineAnswer;

        private volatile boolean isRunning;
        private Set<Sprite> sprites;

        private int levelCount;
        private long totalTime;
        private long startTime;

        /**
         * Default constructor when inflated programmatically
         *
         * @param context Context on which the GameView is displayed
         */
        public GameView (Context context) {
            this(context, null);
        }


        /**
         * Default constructor when inflated from XML file
         *
         * @param context Context on which the GameView is displayed
         * @param attrs   optional attributes provided by the XML file
         */
        public GameView (Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        /**
         * Default constructor when inflated from XML file in API 11+
         *
         * @param context  Context on which the GameView is displayed
         * @param attrs    optional attributes provided by the XML file
         * @param defStyle optional defined theme styles provided by XML file
         */
        public GameView (Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            Log.d(TAG, "Instantiating GameView");
            this.context = context;
            surfaceHolder = getHolder();
            textPaint = PaintCache.getTextPainter();
            textPaint.setTypeface(FontCache.getFont(context));
            drawPaint = PaintCache.getDrawablePainter();
        }

        /**
         * Must be called when the calling Activity or Fragment calls onPause. Method shuts down the
         * game thread.
         */
        public void onPause () {
            Log.d(TAG, "GameView onPause called");
            isRunning = false;
            if (gameThread != null) {
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        /**
         * Must be called when the calling Activity or Fragment calls onResume. Method checks the
         * screen dimensions and starts a new thread to run the game.
         */
        public void onResume () {
            Log.d(TAG, "GameView onResume called");
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            screenWidth = metrics.widthPixels;
            float screenHeight = metrics.heightPixels;
            gameAreaHeight = screenHeight * VERTICAL_DIVIDE_RATIO;
            textAreaHeight = screenHeight - gameAreaHeight;
            levelCount = 0;
            sprites = new HashSet();
            gameThread = new Thread(this);
            gameThread.start();
            isRunning = true;
        }

        /**
         * Thread runner method. Updates and then draws drawables
         */
        @Override
        public void run () {
            Log.d(TAG, "Runner monitoring isRunning: " + isRunning);
            while (isRunning) {
                update();
                draw();
            }
        }

        /**
         * Updates the drawables
         */
        protected void update () {
            Iterator<Sprite> sprite = sprites.iterator();
            while (sprite.hasNext()) {
                sprite.next().update();
            }
        }

        /**
         * Draws the drawables
         */
        protected void draw () {
            if (surfaceHolder.getSurface().isValid()) {
                canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(BACKGROUND_COLOR);
                canvas.drawText(gameEngineQuestion, screenWidth / 2f, TextFormat
                        .verticalCenter(gameAreaHeight, textAreaHeight + gameAreaHeight,
                                textPaint), textPaint);
                Iterator<Sprite> sprite = sprites.iterator();
                while (sprite.hasNext()) {
                    sprite.next().draw(canvas, drawPaint, textPaint);
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }

        @Override
        public boolean onTouchEvent (MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    boolean isLevelFinished = false;
                    float x = event.getX();
                    float y = event.getY();
                    Iterator<Sprite> itSprite = sprites.iterator();
                    while (itSprite.hasNext()) {
                        Sprite sprite = itSprite.next();
                        if (sprite.isCollision(x, y)) {
                            if (gameEngineAnswer.equals(sprite.getText())) {
                                isLevelFinished = true;
                                break;
                            } else {
                                itSprite.remove();
                            }
                        }
                    }

            }
            return super.onTouchEvent(event);
        }


        /**
         * Sets up the level in the background. Positions the Sprites randomly in the defined area for
         * Sprites.
         * <p/>
         * Brute force is used to make sure the Sprites do not overlap. {@value GameView#SPRITE_PLACEMENT_ATTEMPTS}
         * attempts to  place the Sprite without overlap will be tried. By keeping the attempts low,
         * the perception of a random generation in numbers is achieved.
         * <p/>
         * The total number of Sprites will be the lesser of the size of {@link GameEngine#getIncorrectElements()}
         * and {@link #MAX_SPRITES}
         */
        protected void prepareNewLevel () {
            GameEngine gameEngine = new AlphabetGameEngine();
            gameEngine.randomize();
            gameEngineAnswer = gameEngine.getCorrectElement();
            gameEngineQuestion = gameEngine.getQuestion();
            Set<Sprite> tempSprites = new HashSet();
            Bitmap spriteImage = SpriteCache.getSprite(context);

            // Always insert the first bubble as it as the correct correctElement
            tempSprites.add(new BubbleSprite(spriteImage, screenWidth, gameAreaHeight,
                    gameEngineAnswer));

            boolean isSpriteOverlap;
            Iterator<String> filler = gameEngine.getIncorrectElements().iterator();
            while (filler.hasNext() && tempSprites.size() < MAX_SPRITES) {
                Sprite newBubbleSprite;
                int creationAttempt = 0;
                do {
                    isSpriteOverlap = false;
                    newBubbleSprite = new BubbleSprite(spriteImage, screenWidth, gameAreaHeight, null);
                    for (Sprite sprite : tempSprites) {
                        if (sprite.isCollision(newBubbleSprite)) {
                            isSpriteOverlap = true;
                            creationAttempt++;
                        }
                    }
                } while (isSpriteOverlap && creationAttempt < SPRITE_PLACEMENT_ATTEMPTS);
                if (creationAttempt < SPRITE_PLACEMENT_ATTEMPTS) {
                    newBubbleSprite.setText(filler.next());
                    tempSprites.add(newBubbleSprite);
                }
            }
            sprites.clear();
            sprites.addAll(tempSprites);
            Log.d(TAG, "Preparing Round is complete.");
            startTime = System.currentTimeMillis();
        }
    }
}