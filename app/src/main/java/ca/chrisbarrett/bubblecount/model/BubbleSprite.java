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
     * Default constructor
     *
     * @param x      the X coordinate center of the BubbleSprite
     * @param y      the Y coordinate center of the BubbleSprite
     * @param radius the radius of the BubbleSprite
     */
    public BubbleSprite(float x, float y, float radius) {
        this(x, y, radius, null);
    }

    /**
     * Default constructor
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
    public boolean isTouched(float x, float y) {
        return isOverlap(x, y, radius);
    }

    @Override
    public boolean isCollision(Sprite sprite) {
        return sprite != null && isOverlap(sprite.getX(), sprite.getY(), radius * 2);
    }

    @Override
    public void update() {

    }

    /**
     * Helper method to determine if the trigger is inside or outside of the BubbleSprite
     *
     * @param x        the X coordinate of the trigger
     * @param y        the Y coordinate of the trigger
     * @param distance the maximum distance allowed between the X,Y of the trigger and the X,Y of
     *                 the BubbleSprite
     * @return true if there is an overlap, false if not
     */
    protected boolean isOverlap(float x, float y, float distance) {
        double spread = Math.sqrt((this.x - x) * (this.x - x) +
                ((this.y - y) * (this.y - y)));
        return spread <= distance;
    }
}
