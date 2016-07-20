package ca.chrisbarrett.bubblecount.view.game.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import ca.chrisbarrett.bubblecount.util.SpriteCache;
import ca.chrisbarrett.bubblecount.util.TextFormat;
import ca.chrisbarrett.bubblecount.util.Values;

/**
 * Concrete implementation of the Sprite for drawing Bubbles on the screen.
 *
 * @author Chris Barrett
 * @see ca.chrisbarrett.bubblecount.view.game.model.Sprite
 * @since Jun 29, 2016
 */
public class BubbleSprite implements Sprite {

    // TODO - This Class is big and nasty and needs to be refactored

    public static final float DEFAULT_RADIUS = 100;
    public static final int MAX_SPEED = 6;
    public static final int MIN_SPEED = 3;

    private static final String TAG = "BubbleSprite";

    private SpriteImage spriteImage;
    private float screenWidth;
    private float screenHeight;
    private RectF whereToDraw;

    private float x;
    private float y;
    private float xSpeed;
    private float ySpeed;
    private float radius;
    private String text;
    private boolean isVisible;
    private boolean isSquish;

    /**
     * Constructor to generate a BubbleSprite without text
     *
     * @param screenWidth  the width of the screen
     * @param screenHeight the height of the screen
     * @param text         the text that is associated with the the BubbleSprite
     */
    public BubbleSprite (Context context, float screenWidth, float screenHeight, String text) {
        SpriteImage spriteImage = new SpriteImage(SpriteCache.getSprite(context), 6);
        this.text = text;
        this.radius = DEFAULT_RADIUS;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.spriteImage = spriteImage;

        // Determine a random position on the game area
        this.x = (float) Values.RANDOM.nextInt((int) (screenWidth - (radius * 2))) + radius;
        this.y = (float) Values.RANDOM.nextInt((int) (screenHeight - (radius * 2))) + radius;
        this.xSpeed = (float) Values.RANDOM.nextInt(MAX_SPEED - MIN_SPEED) + MIN_SPEED;
        this.ySpeed = (float) Values.RANDOM.nextInt(MAX_SPEED - MIN_SPEED) + MIN_SPEED;

        isSquish = Values.RANDOM.nextBoolean();

        this.whereToDraw = new RectF(x - radius, y - radius, x + radius, y + radius);

        // Set to visible by default
        isVisible = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getX () {
        return x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setX (float x) {
        this.x = x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getY () {
        return y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setY (float y) {
        this.y = y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getYSpeed () {
        return ySpeed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setYSpeed (float x) {
        this.ySpeed = ySpeed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getXSpeed () {
        return xSpeed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setXSpeed (float x) {
        this.xSpeed = xSpeed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getRadius () {
        return radius;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRadius (float radius) {
        this.radius = radius;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText () {
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setText (String text) {
        this.text = text;
    }

    @Override
    public boolean getVisibility () {
        return isVisible;
    }

    /**
     * {@inheritDoc}
     *
     * @param isVisible
     */
    @Override
    public void setVisibility (boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rect getWhatToDraw () {
        return spriteImage.getWhatToDraw();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RectF getWhereToDraw () {
        return whereToDraw;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bitmap getSpriteImage () {
        return spriteImage.getImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSpriteImage (SpriteImage spriteImage) {
        this.spriteImage = spriteImage;
    }

    @Override
    public void animate () {
        spriteImage.animate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCollision (Sprite sprite) {
        float distance = distance(sprite.getX(), sprite.getY(), this.x, this.y);
        return !this.equals(sprite) && distance < radius + sprite.getRadius();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCollision (float x, float y) {
        float distance = distance(x, y, this.x, this.y);
        //       Log.d(TAG, String.format("'%s'(%f,%f) to (%f,%f) = %f vs %f. Collision? %b",  this.text, this.x, this.y, x, y, radius * 2, distance, distance < radius * 2));
        return distance < radius;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void touched (Sprite sprite) {
        xSpeed = (xSpeed * (1f - 1f) + (2f * 1f * sprite.getXSpeed())) / (1f + 1f);
        ySpeed = (ySpeed * (1f - 1f) + (2f * 1f * sprite.getYSpeed())) / (1f + 1f);
        sprite.setXSpeed((sprite.getXSpeed() * (1f - 1f) + (2f * 1f * this.xSpeed)) / (1f + 1f));
        sprite.setYSpeed((sprite.getYSpeed() * (1f - 1f) + (2f * 1f * this.ySpeed)) / (1f + 1f));
        this.x = x + xSpeed;
        this.y = y + ySpeed;
        sprite.setX(sprite.getX() + sprite.getXSpeed());
        sprite.setY(sprite.getY() + sprite.getYSpeed());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update () {
        whereToDraw.set(x - radius, y - radius, x + radius, y + radius);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw (Canvas canvas, Paint drawPaint, Paint textPaint) {
        if (isVisible) {
            canvas.drawBitmap(spriteImage.getImage(), spriteImage.getWhatToDraw(), whereToDraw, drawPaint);
            canvas.drawText(text, x, TextFormat.verticalCenter(y - radius, y + radius, textPaint), textPaint);
        }
    }

    //
    // Helper methods begin here
    //

    /**
     * Method to determine the distance between two points
     *
     * @param x1 x coordinate of the first point
     * @param y1 y coordinate of the first point
     * @param x2 x coordinate of the second point
     * @param y2 y coordinate of the second point
     * @return the distance as a float value
     */
    protected float distance (float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
    }


    /**
     * This method moves the bubble_sprite around the screen
     */
    protected void moveIt () {
        x = x + xSpeed;
        y = y + ySpeed;
        if (x < 0 + radius || x > screenWidth - radius) {
            xSpeed = -xSpeed;
        }
        if (y < 0 + radius || y > screenHeight - radius) {
            ySpeed = -ySpeed;
        }
    }

    /**
     * {@inheritDoc}
     */
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
                ", isVisible=" + isVisible +
                '}';
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BubbleSprite that = (BubbleSprite) o;

        if (Float.compare(that.x, x) != 0) return false;
        if (Float.compare(that.y, y) != 0) return false;
        if (Float.compare(that.xSpeed, xSpeed) != 0) return false;
        if (Float.compare(that.ySpeed, ySpeed) != 0) return false;
        if (Float.compare(that.radius, radius) != 0) return false;
        if (whereToDraw != null ? !whereToDraw.equals(that.whereToDraw) : that.whereToDraw != null)
            return false;
        return !(text != null ? !text.equals(that.text) : that.text != null);

    }

    @Override
    public int hashCode () {
        int result = whereToDraw != null ? whereToDraw.hashCode() : 0;
        result = 31 * result + (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (xSpeed != +0.0f ? Float.floatToIntBits(xSpeed) : 0);
        result = 31 * result + (ySpeed != +0.0f ? Float.floatToIntBits(ySpeed) : 0);
        result = 31 * result + (radius != +0.0f ? Float.floatToIntBits(radius) : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}

