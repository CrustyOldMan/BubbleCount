package ca.chrisbarrett.bubblecount.game;

import java.util.Set;

/**
 * The Game GameEngine interface. All concrete classes must implement.
 *
 * @author Chris Barrett
 * @since Jul 1, 2016
 */
public interface GameEngine {

    int DEFAULT_SIZE = 50;

    /**
     * Gets the answer to the question
     *
     * @return the answer value
     */
    String getAnswer();

    /**
     * Sets the answer to the question
     *
     * @param answer
     *            the answer to be stored
     */
    void setAnswer(String answer);

    /**
     * Gets the question as a String for display
     *
     * @return the question
     */
    String getQuestion();

    /**
     * Sets the question
     *
     * @param question
     *            the question to be stored
     */
    void setQuestion(String question);

    /**
     * Gets generated filler content of the appropriate type for other Bubbles.
     * The size will be {@link #DEFAULT_SIZE}.
     *
     * @return the generated filler content as a String[]
     */
    Set<String> getFiller();

    /**
     * Sets the filler content
     *
     * @param filler the filler content to be stored
     */
    void setFiller(Set<String> filler);
}