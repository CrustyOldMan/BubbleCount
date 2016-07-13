package ca.chrisbarrett.bubblecount.game;

import java.util.LinkedHashSet;

/**
 * The GameFeed GameEngine interface. All concrete classes must implement.
 *
 * @author Chris Barrett
 * @since Jul 1, 2016
 */
public interface GameEngine {

    int ALPHABET_SIZE = 26;
    int DEFAULT_SIZE = 50;
    int DEFAULT_AGE = 5;

    /**
     * Gets the answer to the question
     *
     * @return the answer value
     */
    String getAnswer ();

    /**
     * Gets the question as a String for display
     *
     * @return the question
     */
    String getQuestion ();


    /**
     * Generates the elements of the appropriate type for the Bubbles.
     * The size will be {@link #DEFAULT_SIZE}. The answer value will always be in the first element
     * position.
     *
     * @return elements in an order maintained LinkedHashSet.
     */
    LinkedHashSet<String> getElements ();

}