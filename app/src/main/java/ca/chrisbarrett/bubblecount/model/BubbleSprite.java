package ca.chrisbarrett.bubblecount.model;

/**
 * Created by chrisbarrett on 2016-06-29.
 */
public class BubbleSprite implements Sprite{

    private boolean isVisible;
    private float x;
    private float y;
    private float radius;

    public BubbleSprite(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        isVisible = true;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    @Override
    public void update() {
        // TODO
    }
}
