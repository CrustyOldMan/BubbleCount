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
import ca.chrisbarrett.bubblecount.util.FontCache;
import ca.chrisbarrett.bubblecount.util.PaintCache;
import ca.chrisbarrett.bubblecount.util.SpriteCache;
import ca.chrisbarrett.bubblecount.util.TextFormat;
import ca.chrisbarrett.bubblecount.view.game.model.BubbleSprite;
import ca.chrisbarrett.bubblecount.view.game.model.Sprite;

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

    private GameEngine gameEngine;
    private String gameEngineQuestion;
    private String gameEngineAnswer;
    private OnGameViewListener gameListener;

    private long startTime;
    private volatile boolean isRunning;
    private Set<Sprite> sprites;

    /**
     * Default constructor when inflated programmatically
     *
     * @param context Context on which the GameView is displayed
     */
    public GameView(Context context) {
        this(context, null);
    }


    /**
     * Default constructor when inflated from XML file
     *
     * @param context Context on which the GameView is displayed
     * @param attrs   optional attributes provided by the XML file
     */
    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Default constructor when inflated from XML file in API 11+
     *
     * @param context  Context on which the GameView is displayed
     * @param attrs    optional attributes provided by the XML file
     * @param defStyle optional defined theme styles provided by XML file
     */
    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(TAG, "Instantiating GameView");
        this.context = context;
        gameEngine = new AlphabetGameEngine();
        surfaceHolder = getHolder();
        textPaint = PaintCache.getTextPainter();
        textPaint.setTypeface(FontCache.getFont(context));
        drawPaint = PaintCache.getDrawablePainter();
        try {
            gameListener = (OnGameViewListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnGameViewListener");
        }
    }

    //
    // Life cycle events begin here
    //

    /**
     * Must be called when the calling Activity or Fragment calls onResume. Method checks the
     * screen dimensions and starts a new thread to run the game.
     */
    public void onResume() {
        Log.d(TAG, "GameView onResume called");
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        float screenHeight = metrics.heightPixels;
        gameAreaHeight = screenHeight * VERTICAL_DIVIDE_RATIO;
        textAreaHeight = screenHeight - gameAreaHeight;
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Must be called when the calling Activity or Fragment calls onPause. Method shuts down the
     * game thread.
     */
    public void onPause() {
        Log.d(TAG, "GameView onPause called");
        isRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Thread runner method. Prepares the game for display, records the start time of the game,
     * then loops as the game is running.
     */
    @Override
    public void run() {
        Log.d(TAG, "Setting up the game...");
        prepareGame();
        Log.d(TAG, "Run is Running: " + isRunning);
        startTime = System.currentTimeMillis();
        while (isRunning) {
            update();
            draw();
        }
        Log.d(TAG, "Closing down...");
        gameOver();
    }

    //
    // Helper methods begin here
    //

    /**
     * Monitors the screen for touches. When the screen is touched:
     * <ul>
     * <li>Triggering the correctAnswer will end the game running and fire the end game routine.</li>
     * <li>Triggering the incorrectAnswer will make that sprite invisible</li>
     * </ul>
     * <p/>
     * , the method checks to see if the
     * correct answer has been hit. If the correct answer has been  the isRunning value is changed to false in order to
     * trigger an endin in the game loop. If
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                boolean isGameFinished = false;
                float x = event.getX();
                float y = event.getY();
                Iterator<Sprite> spriteIterator = sprites.iterator();
                while (spriteIterator.hasNext()) {
                    Sprite sprite = spriteIterator.next();
                    if (sprite.isCollision(x, y)) {
                        if (gameEngineAnswer.equals(sprite.getText())) {
                            isRunning = false;
                            break;
                        } else {
                            sprite.setVisibility(false);
                        }
                    }
                }
        }
        return super.onTouchEvent(event);
    }


    /**
     * This method updates the drawables. As positions inside the Set are updated, an Iterator is used.
     * Generally, after updating, {@link #draw()} should be called.
     */
    protected void update() {
        Iterator<Sprite> spriteIterator = sprites.iterator();
        while (spriteIterator.hasNext()) {
            spriteIterator.next().update();
        }
    }

    /**
     * Draws the drawables. For sprites, a foreach loop is used as no changes to the sprite values
     * takes place. This method should be called after {@link #update()}.
     */
    protected void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(BACKGROUND_COLOR);
            canvas.drawText(gameEngineQuestion, screenWidth / 2f, TextFormat
                    .verticalCenter(gameAreaHeight, textAreaHeight + gameAreaHeight,
                            textPaint), textPaint);
            for (Sprite sprite : sprites) {
                sprite.draw(canvas, drawPaint, textPaint);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
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
     * The total number of Sprites will be the lesser of the size of {@link GameEngine#getCorrectElement()}
     * and {@link #MAX_SPRITES}
     */
    protected void prepareGame() {
        gameEngine.randomize();
        gameEngineAnswer = gameEngine.getCorrectElement();
        gameEngineQuestion = gameEngine.getQuestion();
        sprites = new HashSet();
        Bitmap bubbleSprite = SpriteCache.getSprite(context);

        // Always insert the first bubble as it as the correct answer
        sprites.add(new BubbleSprite(bubbleSprite, screenWidth, gameAreaHeight, gameEngineAnswer));

        boolean isSpriteOverlap;
        Iterator<String> iteratorElements = gameEngine.getIncorrectElements().iterator();
        while (iteratorElements.hasNext() && sprites.size() < MAX_SPRITES) {
            Sprite newSprite;
            int creationAttempt = 0;
            do {
                isSpriteOverlap = false;
                newSprite = new BubbleSprite(bubbleSprite, screenWidth, gameAreaHeight, null);
                for (Sprite sprite : sprites) {
                    if (sprite.isCollision(newSprite)) {
                        isSpriteOverlap = true;
                        creationAttempt++;
                    }
                }
            } while (isSpriteOverlap && creationAttempt < SPRITE_PLACEMENT_ATTEMPTS);
            if (creationAttempt < SPRITE_PLACEMENT_ATTEMPTS) {
                newSprite.setText(iteratorElements.next());
                sprites.add(newSprite);
            }
        }
        Log.d(TAG, "Preparing Round is complete.");
    }

    /**
     * Run when the game ends. Stores the time and fires the onGameEnd to the calling Activity.
     */
    protected void gameOver() {
        Log.d(TAG, "Game End called. Shutting down...");
        long totalTime = System.currentTimeMillis() - startTime;
        gameListener.onGameEnd(totalTime);
    }

    /**
     * Activities and Fragments are required to implement the OnGameViewListener.
     * Game context material is available through the listener.
     */
    public interface OnGameViewListener {

        /**
         * onGameEnd is called when a game has ended
         *
         * @param time the total time in milliseconds that the game took
         */
        void onGameEnd(long time);
    }
}

