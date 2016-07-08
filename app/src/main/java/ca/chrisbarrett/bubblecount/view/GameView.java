package ca.chrisbarrett.bubblecount.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import ca.chrisbarrett.bubblecount.game.CountGameEngine;
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
 * <li>{@link GameView#SPRITE_COUNT} defines the number of Bubbles that will be drawn in the
 * Sprite area of the screen. The actual implementation, however, maybe less if the available
 * screen dimensions are to hold all the Sprites.</li>
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
    public static final int SPRITE_COUNT = 30;
    public static final int SPRITE_PLACEMENT_ATTEMPTS = 5;
    private static final String TAG = "GameView";
    public final int PLAYER_AGE_TEST = 5;
    private Thread gameThread = null;
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Paint textPaint;
    private Paint drawPaint;
    private float screenWidth;
    private float gameAreaHeight;
    private float textAreaHeight;
    private String gameEngineQuestion = "";
    private String gameEngineAnswer = "";
    ;
    private List<Sprite> sprites = new ArrayList<>(SPRITE_COUNT);
    private int bubblesRemaining;
    private Bitmap spriteImage;
    private boolean isPlaying;
    private int roundCount;


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
        spriteImage = BubbleSpriteCache.getSprite(context);
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
        gameAreaHeight = metrics.heightPixels * VERTICAL_DIVIDE_RATIO;
        textAreaHeight = metrics.heightPixels - gameAreaHeight;
        screenWidth = metrics.widthPixels;
        gameEngineQuestion = "";
        new PrepareRound().execute();
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
            canvas.drawLine(0, gameAreaHeight, screenWidth, gameAreaHeight, drawPaint);
            canvas.drawText(gameEngineQuestion, screenWidth / 2f, TextFormat
                    .verticalCenter(gameAreaHeight, textAreaHeight + gameAreaHeight - 50f,
                            textPaint), textPaint);
            for (Sprite sprite : sprites) {
                if (sprite.isVisible()) {
                    canvas.drawBitmap(sprite.getSpriteImage(), sprite.getX() - sprite.getRadius(), sprite
                            .getY() - sprite.getRadius(), drawPaint);

// TODO - Get the bubbles to animate using bubble_sprite2

                    canvas.drawText(sprite.getText(), sprite.getX(), TextFormat
                            .verticalCenter(sprite.getY() - BubbleSprite.RADIUS,
                                    sprite.getY() + BubbleSprite.RADIUS, textPaint), textPaint);
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
                        try {
                            if (gameEngineAnswer.equals(sprite.getText())) {
                                Toast.makeText(getContext(), "Win", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Unable to convert text to int: " + sprite.getText());
                        }
                    }
                }
        }
        return super.onTouchEvent(event);
    }

     protected String buildQuestion(String question, String answer){
       return "";
     }

    /**
     * Setups up the round in the background while the Round Dialogue is being displayed.
     * Positions the {@value GameView#SPRITE_COUNT} Sprites randomly in the defined area for
     * Sprites.
     * <p/>
     * Brute force is used to make sure the Sprites do not overlap. {@value GameView#SPRITE_PLACEMENT_ATTEMPTS}
     * attempts to  place the Sprite without overlap will be tried. By keeping the attempts low,
     * the perception of a random generation in numbers is achieved.
     */
    protected class PrepareRound extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Random rand = new Random();
            GameEngine gameEngine = new CountGameEngine(PLAYER_AGE_TEST);
            gameEngineAnswer = gameEngine.getAnswer();

            sprites.add(new BubbleSprite(spriteImage,
                    rand.nextInt(((int) screenWidth - (BubbleSprite.RADIUS * 2)
                    ) + 1) + BubbleSprite.RADIUS,
                    rand.nextInt(((int) gameAreaHeight - (BubbleSprite.RADIUS * 2)) + 1) + BubbleSprite.RADIUS,
                    BubbleSprite.RADIUS, "" + gameEngineAnswer));
            int x = 0;
            int y = 0;
            boolean overlap;
            for (int i = 0; i < SPRITE_COUNT - 1; i++) {
                int attemptCount = 0;
                do {
                    // don't duplicate the correct gameEngineAnswer bubble
                    if (gameEngineAnswer.equals(String.valueOf(i + 1))) {
                        attemptCount = SPRITE_PLACEMENT_ATTEMPTS + 1;
                        break;
                    }
                    attemptCount++;
                    overlap = false;
                    x = rand.nextInt(((int) screenWidth - (BubbleSprite.RADIUS * 2)) + 1) + BubbleSprite.RADIUS;
                    y = rand.nextInt(((int) gameAreaHeight - (BubbleSprite.RADIUS * 2)) + 1) + BubbleSprite.RADIUS;
                    for (Sprite sprite : sprites) {
                        if (sprite.isCollision(x, y, BubbleSprite.RADIUS * 2)) {
                            overlap = true;
                            break;
                        }
                    }
                } while (overlap && attemptCount <= SPRITE_PLACEMENT_ATTEMPTS);
                if (attemptCount <= SPRITE_PLACEMENT_ATTEMPTS) {
                    sprites.add(new BubbleSprite(spriteImage, x, y, BubbleSprite.RADIUS, "" + (i +
                            1)));
                }
            }
            return null;
        }
    }
}
