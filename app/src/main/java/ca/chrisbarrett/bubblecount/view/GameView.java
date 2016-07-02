package ca.chrisbarrett.bubblecount.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import ca.chrisbarrett.bubblecount.game.CountGameEngine;
import ca.chrisbarrett.bubblecount.game.GameEngine;
import ca.chrisbarrett.bubblecount.model.BubbleSprite;
import ca.chrisbarrett.bubblecount.model.Sprite;
import ca.chrisbarrett.bubblecount.utilities.BubbleFontCache;
import ca.chrisbarrett.bubblecount.utilities.PaintCache;
import ca.chrisbarrett.bubblecount.utilities.TextFormat;

/**
 * The View for the Game, extending {@link  android.view.SurfaceView}. This View will run on a
 * separate Thread from the UI.
 * <p/>
 * <ol>
 * <li>{@link GameView#SPRITE_COUNT} defines the number of Bubbles that will be drawn in the
 * Sprite area of the screen. The actual implementation, however, maybe less if the available
 * screen dimensions are to hold all the Sprites.</li>
 * <li>{@link GameView#VERTICAL_DIVIDE_RATIO} defines the ratio between the Sprite area and the
 * Text area. Text area is used to hold a question, or statement to be displayed to the player -
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

    public static final int BUBBLE_RADIUS = 100;
    public static final int SPRITE_COUNT = 30;
    public static final int SPRITE_PLACEMENT_ATTEMPTS = 5;

    private static final String TAG = "GameView";
    private Thread gameThread = null;
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Paint textPaint;
    private Paint drawPaint;
    private float screenWidth;
    private float gameHeight;
    private float textHeight;

    private List<Sprite> sprites = new ArrayList<>(SPRITE_COUNT);
    private boolean isPlaying;
    private int roundCount;
    private int answer;
    private String question;

    /**
     * Default constructor when inflating from programmatically
     *
     * @param context Context on which the GameView is displayed
     */
    public GameView(Context context) {
        this(context, null);
    }

    /**
     * Default constructor when inflating from XML file
     *
     * @param context Context on which the GameView is displayed
     * @param attrs   optional attributes provided by the XML file
     */
    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Default constructor when inflating from XML file in API 11+
     *
     * @param context  Context on which the GameView is displayed
     * @param attrs    optional attributes provided by the XML file
     * @param defStyle optional defined theme styles provided by XML file
     */
    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        surfaceHolder = getHolder();
        textPaint = PaintCache.getTextPainter();
        textPaint.setTypeface(BubbleFontCache.getFont(context));
        drawPaint = PaintCache.getDrawablePainter();
        roundCount = 0;
        isPlaying = true;
    }

    /**
     * Must be called when the calling Activity or Fragment calls onPause. Method shuts down the
     * game thread.
     */
    public void onPause() {
        isPlaying = false;
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
    public void onResume() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        gameHeight = metrics.heightPixels * VERTICAL_DIVIDE_RATIO;
        textHeight = metrics.heightPixels - gameHeight;
        screenWidth = metrics.widthPixels;
        prepareRound();
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Thread runner method. Updates and then draws drawables
     */
    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
        }
    }


    /**
     * Updates the drawables
     */
    protected void update() {
        Iterator<Sprite> itSprite = sprites.iterator();
        Sprite sprite;
        while (itSprite.hasNext()) {
            sprite = itSprite.next();
            sprite.update();
        }
    }

    /**
     * Draws the drawables
     */
    protected void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(BACKGROUND_COLOR);
            canvas.drawLine(0, gameHeight, screenWidth, gameHeight, drawPaint);
            canvas.drawText(question, screenWidth / 2f, TextFormat
                    .verticalCenter(gameHeight, textHeight + gameHeight-50, textPaint), textPaint);

            for (Sprite sprite : sprites) {
                if (sprite.isVisible()) {
                    canvas.drawCircle(sprite.getX(), sprite.getY(), sprite.getRadius(), drawPaint);
                    canvas.drawText(sprite.getText(), sprite.getX(), TextFormat
                            .verticalCenter(sprite.getY() - BUBBLE_RADIUS,
                                    sprite.getY() + BUBBLE_RADIUS, textPaint), textPaint);
                }
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (Sprite sprite : sprites) {
                    if (sprite.isCollision(event.getX(), event.getY())) {
                        sprite.setVisible(false);
                    }
                }
        }
        return super.onTouchEvent(event);
    }

    /**
     * Setups up the round in the background while the Round Dialogue is being displayed.
     * Positions the {@value GameView#SPRITE_COUNT} Sprites randomly in the defined area for
     * Sprites.
     * <p/>
     * Brute force is used to make sure the Sprites do not overlap. {@value GameView#SPRITE_PLACEMENT_ATTEMPTS}
     * attempts to  place the Sprite without overlap will be tried. On the 11th attempt, the
     * Sprite will be skipped over.
     * TODO - ASynchTask this method
     */
    protected void prepareRound() {
        Random rand = new Random();
        GameEngine gameEngine = new CountGameEngine();
        question = gameEngine.getQuestion();
        answer = gameEngine.getAnswer();
        sprites.add(new BubbleSprite(
                rand.nextInt(((int) screenWidth - (BUBBLE_RADIUS * 2)) + 1) + BUBBLE_RADIUS,
                rand.nextInt(((int) gameHeight - (BUBBLE_RADIUS * 2)) + 1) + BUBBLE_RADIUS,
                BUBBLE_RADIUS, "" + answer));
        int x = 0;
        int y = 0;
        boolean overlap;
        for (int i = 0; i < SPRITE_COUNT - 1; i++) {
            int attemptCount = 0;
            do {
                // don't duplicate the correct answer bubble
                if (answer == i + 1) {
                    attemptCount = SPRITE_PLACEMENT_ATTEMPTS + 1;
                    break;
                }
                attemptCount++;
                overlap = false;
                x = rand.nextInt(((int) screenWidth - (BUBBLE_RADIUS * 2)) + 1) + BUBBLE_RADIUS;
                y = rand.nextInt(((int) gameHeight - (BUBBLE_RADIUS * 2)) + 1) + BUBBLE_RADIUS;
                for (Sprite sprite : sprites) {
                    if (sprite.isCollision(x, y, BUBBLE_RADIUS * 2)) {
                        overlap = true;
                        break;
                    }
                }
            } while (overlap && attemptCount <= SPRITE_PLACEMENT_ATTEMPTS);
            if (attemptCount <= SPRITE_PLACEMENT_ATTEMPTS) {
                sprites.add(new BubbleSprite(x, y, BUBBLE_RADIUS, "" + (i + 1)));
            }
        }
    }
}
