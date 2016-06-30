package ca.chrisbarrett.bubblecount.model;

/**
 * Created by chrisbarrett on 2016-06-29.
 */
public interface Sprite {

    float getX();

    void setX(float x);

    float getY();

    void setY(float y);

    float getRadius();

    void setRadius(float radius);

    boolean isVisible();

    void setVisible(boolean isVisible);

    void update();
}
