package ca.chrisbarrett.bubblecount.model;

/**
 * All drawable sprites must implement
 *
 * @author Chris Barrett
 * @since Jun 29, 2016
 */
public interface Sprite {

    /**
     * Gets the X coordinate of the Sprite
     *
     * @return the X coordinate
     */
    float getX();

    /**
     * Sets the X coordinate of the Sprite
     *
     * @param x the X coordinate value
     */
    void setX(float x);

    /**
     * Gets the Y coordinate of the Sprite
     *
     * @return the Y coordinate
     */
    float getY();

    /**
     * Sets the Y coordinate of the Sprite
     *
     * @param y the Y coordinate value
     */
    void setY(float y);

    /**
     * Gets the radius of the Sprite
     *
     * @return radius
     */
    float getRadius();

    /**
     * Sets the radius of the Sprite
     *
     * @param radius of the Sprite
     */
    void setRadius(float radius);

    /**
     * Gets the text of the Sprite
     *
     * @return String object containing text. This object can be null.
     */
    String getText();

    /**
     * Sets the text of the Sprite
     *
     * @param text of the Sprite
     */
    void setText(String text);

    /**
     * Checks if the Sprite is visible
     *
     * @return true if the Sprite is visible, false if not visible
     */
    boolean isVisible();

    /**
     * Sets the visible state of the Sprite
     *
     * @param isVisible is set to true if visible, false if not visible
     */
    void setVisible(boolean isVisible);

    /**
     * Checks to see if the Sprite has collided with another Sprite object
     *
     * @param sprite Sprite object to be checked against
     * @return true if the Sprites have collided
     */
    boolean isCollision(Sprite sprite);

    /**
     * Checks to see if the Sprite has collided with a specific point at a specific distance
     *
     * @param x the X coordinate of the touch
     * @param y the Y coordinate of the touch
     * @return true if the Sprite has been touched, false if it has not been touched
     */
    boolean isCollision(float x, float y);

    /**
     * Checks to see if the Sprite has collided with a specific point at a specific distance
     *
     * @param x        the X coordinate of the point
     * @param y        the Y coordinate of the point
     * @param distance the maximum distance allowed between the X,Y of the point and the X,Y of
     *                 the BubbleSprite
     * @return true if there is an overlap, false if not
     */
    boolean isCollision(float x, float y, float distance);

    /**
     * Updates the Sprite, such as with motion. Usually called prior to a draw event.
     */
    void update();

}
