package ca.chrisbarrett.bubblecount.model;

/**
 * Concrete implementation of the Sprite for drawing Bubbles on the screen.
 *
 * @author Chris Barrett
 * @see ca.chrisbarrett.bubblecount.model.Sprite
 * @since Jun 29, 2016
 */
public class BubbleSprite implements Sprite {

    private boolean isVisible;
    private float x;
    private float y;
    private float radius;
    private String text;

    /**
     * Constructor to generate a BubbleSprite without text
     *
     * @param x      the X coordinate center of the BubbleSprite
     * @param y      the Y coordinate center of the BubbleSprite
     * @param radius the radius of the BubbleSprite
     */
    public BubbleSprite(float x, float y, float radius) {
        this(x, y, radius, null);
    }

    /**
     * Constructor generates a BubbleSprite with text
     *
     * @param x      the X coordinate center of the BubbleSprite
     * @param y      the Y coordinate center of the BubbleSprite
     * @param radius the radius of the BubbleSprite
     * @param text   the text that is associated with the the BubbleSprite
     */
    public BubbleSprite(float x, float y, float radius, String text) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        isVisible = true;
        this.text = text;
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
    public void update() {

    }

    @Override
    public boolean isCollision(float x, float y, float distance) {
        double spread = Math.sqrt((this.x - x) * (this.x - x) +
                ((this.y - y) * (this.y - y)));
        return spread <= distance;
    }
}
