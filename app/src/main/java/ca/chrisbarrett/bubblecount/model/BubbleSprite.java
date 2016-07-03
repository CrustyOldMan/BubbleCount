package ca.chrisbarrett.bubblecount.model;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Concrete implementation of the Sprite for drawing Bubbles on the screen.
 *
 * @author Chris Barrett
 * @see ca.chrisbarrett.bubblecount.model.Sprite
 * @since Jun 29, 2016
 */
public class BubbleSprite implements Sprite {

    public static final int RADIUS = 100;
    protected static final int TOTAL_FRAME_COUNT = 3;
    protected static final int EXPLODED_FRAME_START = 2;

    private boolean isVisible;
    private boolean isExplode;
    private float x;
    private float y;
    private float radius;
    private String text;

    private Bitmap spriteImage;
    private int currentFrame;
    private int frameHeight;
    private int frameWidth;
    private Rect whatToDraw;
    private RectF whereToDraw;

    /**
     * Constructor to generate a BubbleSprite without text
     *
     * @param x      the X coordinate center of the BubbleSprite
     * @param y      the Y coordinate center of the BubbleSprite
     * @param radius the radius of the BubbleSprite
     */
    public BubbleSprite(Bitmap spriteImage, float x, float y, float radius) {
        this(spriteImage, x, y, radius, null);
    }

    /**
     * Constructor generates a BubbleSprite with text
     *
     * @param x      the X coordinate center of the BubbleSprite
     * @param y      the Y coordinate center of the BubbleSprite
     * @param radius the radius of the BubbleSprite
     * @param text   the text that is associated with the the BubbleSprite
     */
    public BubbleSprite(Bitmap spriteImage, float x, float y, float radius, String text) {
        this.currentFrame = 0;
        this.spriteImage = spriteImage;
        this.frameHeight = spriteImage.getHeight();
        this.frameWidth = spriteImage.getWidth();
        this.whatToDraw = new Rect(0, 0, frameWidth / 3, frameHeight);
        this.whereToDraw = new RectF(x - radius, y - radius, radius * 2, radius * 2);
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.text = text;
        this.isVisible = true;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public float getRadius() {
        return radius;
    }

    @Override
    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    @Override
    public boolean isExplode() {
        return isExplode;
    }

    @Override
    public void setExplode(boolean isExplode) {
        this.isExplode = isExplode;
    }

    @Override
    public Rect getWhatToDraw() {
        return whatToDraw;
    }

    @Override
    public RectF getWhereToDraw() {
        return whereToDraw;
    }

    @Override
    public void setSpriteImage(Bitmap spriteImage) {
        this.spriteImage = spriteImage;
    }

    @Override
    public Bitmap getSpriteImage() {
        return spriteImage;
    }

    @Override
    public String getText() {
        if (text == null) {
            return "";
        } else {
            return text;
        }
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean isCollision(float x, float y) {
        return isCollision(x, y, radius);
    }

    @Override
    public boolean isCollision(Sprite sprite) {
        return sprite != null && sprite instanceof BubbleSprite && isCollision(sprite.getX(),
                sprite.getY(), radius * 2);
    }

    @Override
    public boolean isCollision(float x, float y, float distance) {
        double spread = Math.sqrt((this.x - x) * (this.x - x) +
                ((this.y - y) * (this.y - y)));
        return spread <= distance;
    }

    @Override
    public void update() {
        if (isExplode) {
            whatToDraw.set(currentFrame * frameWidth, 0, currentFrame * frameWidth + frameWidth,
                    frameHeight);
        }
    }
}
