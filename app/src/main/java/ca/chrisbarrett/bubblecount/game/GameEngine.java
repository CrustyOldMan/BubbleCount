package ca.chrisbarrett.bubblecount.game;

/**
 * The Game GameEngine interface. All concrete classes must implement.
 *
 * @author Chris Barrett
 * @since Jul 1, 2016
 */
public interface GameEngine {

    /**
     * Gets the answer to the question
     *
     * @return the answer value
     */
    String getAnswer();

    /**
     * Sets the answer to the question
     *
     * @param answer the answer to be stored
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
     * @param question the question to be stored
     */
    void setQuestion(String question);
}
