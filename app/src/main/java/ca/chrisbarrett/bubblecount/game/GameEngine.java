package ca.chrisbarrett.bubblecount.game;

import java.util.Set;

/**
 * The GameFeed GameEngine interface. All concrete classes must implement.
 *
 * @author Chris Barrett
 * @since Jul 1, 2016
 */
public interface GameEngine {

    int DEFAULT_SIZE = 50;
    int DEFAULT_AGE = 5;

    /**
     * Gets the correctElement to the question
     *
     * @return the correctElement value
     */
    String getCorrectElement ();

    /**
     * Gets the incorrectElements as a Set for display
     *
     * @return a Set of incorrect elements
     */
    Set<String> getIncorrectElements ();

    /**
     * Gets the question as a String for display
     *
     * @return the question
     */
    String getQuestion ();

    /**
     * Helper method used to generate a new random question, correctElement, and Set of incorrectElements
     */
    void randomize();

}