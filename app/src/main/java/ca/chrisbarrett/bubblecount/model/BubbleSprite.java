package ca.chrisbarrett.bubblecount.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.Random;

import ca.chrisbarrett.bubblecount.utilities.TextFormat;

/**
 * Concrete implementation of the Sprite for drawing Bubbles on the screen.
 *
 * @author Chris Barrett
 * @see ca.chrisbarrett.bubblecount.model.Sprite
 * @since Jun 29, 2016
 */
public class BubbleSprite implements Sprite {

    public static final float DEFAULT_RADIUS = 100;
    public static final float MAX_SPEED = 5;
    public static final float MIN_SPEED = 2;

    private static final Random rand = new Random(System.currentTimeMillis());
    private static final String TAG = "BubbleSprite";

    private Bitmap spriteImage;
    private int frameHeight;
    private int frameWidth;
    private float screenWidth;
    private float screenHeight;
    private Rect whatToDraw;
    private RectF whereToDraw;

    private float x;
    private float y;
    private float xSpeed;
    private float ySpeed;
    private float radius;
    private String text;
    private boolean isVisible;

    /**
     * Constructor to generate a BubbleSprite without text
     *
     * @param screenWidth  the width of the screen
     * @param screenHeight the height of the screen
     * @param text         the text that is associated with the the BubbleSprite
     */
    public BubbleSprite (Bitmap spriteImage, float screenWidth, float screenHeight, String text) {
        this.text = text;
        this.radius = DEFAULT_RADIUS;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.spriteImage = spriteImage;
        this.frameHeight = spriteImage.getHeight();
        this.frameWidth = spriteImage.getWidth();

        // Determine a random position on the game area
        this.x = (float) rand.nextInt((int) (screenWidth - (radius * 2))) + radius;
        this.y = (float) rand.nextInt((int) (screenHeight - (radius * 2))) + radius;

        this.whatToDraw = new Rect(0, 0, frameWidth, frameHeight);
        this.whereToDraw = new RectF(x - radius, y - radius, x + radius, y + radius);

        // Log.i(TAG, "Created: " + this.toString());
    }

    @Override
    public float getX () {
        return x;
    }

    @Override
    public void setX (float x) {
        this.x = x;
    }

    @Override
    public float getY () {
        return y;
    }

    @Override
    public void setY (float y) {
        this.y = y;

    }

    @Override
    public float getRadius () {
        return radius;
    }

    @Override
    public void setRadius (float radius) {
        this.radius = radius;
    }

    @Override
    public String getText () {
        return text;
    }

    @Override
    public void setText (String text) {
        this.text = text;

    }

    @Override
    public boolean isCollision (Sprite sprite) {
        float distance = distance(sprite.getX(), sprite.getY(), this.x, this.y);
        return distance < radius * 2;
    }

    @Override
    public boolean isCollision (float x, float y) {
        float distance = distance(x, y, this.x, this.y);
        Log.d(TAG, String.format("'%s'(%f,%f) to (%f,%f) = %f vs %f. Collision? %b",
                this.text, this.x, this.y, x, y, radius * 2, distance, distance < radius * 2));
        return distance < radius;
    }

    /**
     * Helper method to determine the distance between two points
     * @param x1 x coordinate of the first point
     * @param y1 y coordinate of the first point
     * @param x2 x coordinate of the second point
     * @param y2 y coordinate of the second point
     * @return the distance as a float value
     */
    protected float distance (float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
    }

    @Override
    public Rect getWhatToDraw () {
        return whatToDraw;
    }

    @Override
    public RectF getWhereToDraw () {
        return whereToDraw;
    }

    @Override
    public Bitmap getSpriteImage () {
        return spriteImage;
    }

    @Override
    public void setSpriteImage (Bitmap spriteImage) {
        this.spriteImage = spriteImage;
    }

    @Override
    public boolean update () {

        whereToDraw.set(x - radius, y - radius, x + radius, y + radius);
        return false;
    }

    @Override
    public void draw (Canvas canvas, Paint drawPaint, Paint textPaint) {
        canvas.drawBitmap(spriteImage, whatToDraw, whereToDraw, drawPaint);
        canvas.drawText(text, x, TextFormat.verticalCenter(y - radius, y + radius, textPaint), textPaint);

    }

    @Override
    public String toString () {
        return "BubbleSprite{" +
                "screenWidth=" + screenWidth +
                ", screenHeight=" + screenHeight +
                ", x=" + x +
                ", y=" + y +
                ", xSpeed=" + xSpeed +
                ", ySpeed=" + ySpeed +
                ", radius=" + radius +
                ", text='" + text + '\'' +
                '}';
    }
}

