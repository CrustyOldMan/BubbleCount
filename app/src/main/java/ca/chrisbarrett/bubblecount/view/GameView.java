package ca.chrisbarrett.bubblecount.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import ca.chrisbarrett.bubblecount.model.Sprite;
import ca.chrisbarrett.bubblecount.view.utilities.BubbleFont;

/**
 * Created by chrisbarrett on 2016-06-29.
 */
public class GameView extends SurfaceView implements Runnable {

    protected static final float VERTICAL_DIVIDE_RATION = 0.8f;
    protected static final int BACKGROUND_COLOR = Color.BLACK;
    protected static final int SPRITE_SIZE = 10;

    private static final String TAG = "GameView";
    private Thread gameThread = null;
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Paint paint;

    private boolean isPlaying;
    private int screenWidth;
    private int screenHeight;
    private static Typeface typeface;

    private Sprite[] sprites = new Sprite[SPRITE_SIZE];

    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTypeface(BubbleFont.getFont(context));
        paint.setTextAlign(Paint.Align.CENTER);
        isPlaying = true;
    }

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

    public void onResume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
        }
    }

    protected void update() {
        for (int i = 0; i < sprites.length; i++){
            sprites[i].update();
        }
    }

    protected void draw() {
        screenWidth = getWidth();
        screenHeight = getHeight();
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            canvas.drawColor(BACKGROUND_COLOR);
            canvas.drawLine(0, screenHeight * VERTICAL_DIVIDE_RATION, screenWidth, screenHeight * VERTICAL_DIVIDE_RATION, paint);
            paint.setTextSize(120);

            paint.setStyle(Paint.Style.FILL);
            canvas.drawText("Placeholder", screenWidth / 2, screenHeight * 0.9f, paint);

            paint.setStyle(Paint.Style.STROKE);
            for (Sprite sprite : sprites) {
                if (sprite.isVisible()) {
                    canvas.drawCircle(sprite.getX(), sprite.getY(), sprite.getRadius(), paint);
                }
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }


}
