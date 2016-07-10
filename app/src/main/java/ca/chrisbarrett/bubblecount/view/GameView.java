package ca.chrisbarrett.bubblecount.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import ca.chrisbarrett.bubblecount.model.BubbleSprite;
import ca.chrisbarrett.bubblecount.model.Sprite;
import ca.chrisbarrett.bubblecount.utilities.BubbleFontCache;
import ca.chrisbarrett.bubblecount.utilities.BubbleSpriteCache;
import ca.chrisbarrett.bubblecount.utilities.PaintCache;
import ca.chrisbarrett.bubblecount.utilities.TextFormat;

/**
 * The View for the Game, extending {@link  android.view.SurfaceView}. This View will run on a
 * separate Thread from the UI.
 * <p/>
 * <ol>
 * <li>Bubbles are randomly drawn on the Game area of the screen. The actual implementation,
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

    private Thread gameThread = null;

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
    private OnGameViewListener gameCallBack;

    private volatile boolean isRunning;
    private boolean isVisible;
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
        textPaint.setTypeface(BubbleFontCache.getFont(context));
        drawPaint = PaintCache.getDrawablePainter();
        try {
            gameCallBack = (OnGameViewListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnGameViewListener");
        }
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
        checkGameState();
    }

    /**
     * Must be called by the calling Activity or Fragment to begin the level.
     */
    public void onStart () {
        gameThread = new Thread(this);
        gameThread.start();
        isRunning = true;
        isVisible = true;
        startTime = System.currentTimeMillis();
    }

    /**
     * Thread runner method. Updates and then draws drawables
     */
    @Override
    public void run () {
        Log.d(TAG, "Runner monitoring isRunning: " + isRunning);
        while (isRunning) {
            update();
            if (isVisible) {
                draw();
            }
        }
    }

    /**
     * Updates the drawables
     */
    protected void update () {
        for (Sprite sprite : sprites) {
            sprite.update();
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
                if (isLevelFinished) {
                    checkGameState();
                }
        }
        return super.onTouchEvent(event);
    }

    /**
     * Helper method to setup a new round, if the round count is less than {@link #MAX_ROUNDS}
     * rounds. A modal window will appear for
     */
    protected void checkGameState () {
        long endTime = System.currentTimeMillis();
        onPause();
        isVisible = false;
        totalTime += (endTime - startTime);
        onPause();
        if (levelCount == 0) {
            levelCount++;
            gameCallBack.onGameStart();
            prepareNewLevel();
        } else  if (levelCount < MAX_ROUNDS) {
            levelCount++;
            gameCallBack.onNewLevel(levelCount);
            prepareNewLevel();
        } else {
            gameCallBack.onGameEnd(totalTime);
        }
    }


    /**
     * Sets up the level in the background. Positions the Sprites randomly in the defined area for
     * Sprites.
     * <p/>
     * Brute force is used to make sure the Sprites do not overlap. {@value GameView#SPRITE_PLACEMENT_ATTEMPTS}
     * attempts to  place the Sprite without overlap will be tried. By keeping the attempts low,
     * the perception of a random generation in numbers is achieved.
     * <p/>
     * The total number of Sprites will be the lesser of the size of {@link GameEngine#getFiller
     * ()} and {@link #MAX_SPRITES}
     */
    protected void prepareNewLevel () {
        GameEngine gameEngine = new AlphabetGameEngine();
        gameEngineAnswer = gameEngine.getAnswer();
        gameEngineQuestion = gameEngine.getQuestion();
        sprites = new HashSet();
        Bitmap spriteImage = BubbleSpriteCache.getSprite(context);

        // Always insert the first bubble as it as the correct answer
        sprites.add(new BubbleSprite(spriteImage, screenWidth, gameAreaHeight,
                gameEngineAnswer));

        boolean isSpriteOverlap;
        Iterator<String> filler = gameEngine.getFiller().iterator();
        while (filler.hasNext() && sprites.size() < MAX_SPRITES) {
            Sprite newBubbleSprite;
            int creationAttempt = 0;
            do {
                isSpriteOverlap = false;
                newBubbleSprite = new BubbleSprite(spriteImage, screenWidth, gameAreaHeight, null);
                for (Sprite sprite : sprites) {
                    if (sprite.isCollision(newBubbleSprite)) {
                        isSpriteOverlap = true;
                        creationAttempt++;
                    }
                }
            } while (isSpriteOverlap && creationAttempt < SPRITE_PLACEMENT_ATTEMPTS);
            if (creationAttempt < SPRITE_PLACEMENT_ATTEMPTS) {
                newBubbleSprite.setText(filler.next());
                sprites.add(newBubbleSprite);
            }
        }
        Log.d(TAG, "Preparing Round is complete.");
    }

    /**
     * Activities and Fragments should implement the OnGameViewListener. Game context material is
     * available through the listener.
     */
    public interface OnGameViewListener {

        /**
         * onNewLevel is called when a new level is about to begin
         *
         * @param level that is about to begin
         */
        void onNewLevel (int level);

        /**
         * onGameStart is called when a new game is about to begin
         */
        void onGameStart ();

        /**
         * gameEnd is called when a game has ended
         *
         * @param time the total time in milliseconds that the game took
         */
        void onGameEnd (long time);
    }


}

