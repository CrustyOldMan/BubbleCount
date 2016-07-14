package ca.chrisbarrett.bubblecount.game;

import android.util.Log;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Abstract game engine. Games are based on the British Columbia school curriculum of 2007.
 * Fields are intentionally protected in order to allow faster access by derived classes.
 * <a href="https://www.bced.gov.bc.ca/irp/pdfs/mathematics/2007mathk7.pdf">https://www.bced.gov.bc.ca/irp/pdfs/mathematics/2007mathk7.pdf</a>
 *
 * @author Chris Barrett
 * @see
 * @since Jul 1, 2016
 */
public abstract class AbstractEngine implements GameEngine {

    protected static final String TAG = "GameEngine";
    protected static final Random rand = new Random(System.currentTimeMillis());
    protected String correctElement;
    protected String question;
    protected Set<String> incorrectElements;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCorrectElement () {
        return correctElement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getQuestion () {
        return question;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getIncorrectElements () {
        return incorrectElements;
    }

    /**
     * Helper method to generate an array of incorrectElements using a char range.
     * Values calculated will be between the min and max value specified for the size indicated.
     *
     * @param minValue minimum value of the Set
     * @param maxValue maximum value of the Set
     * @return Set of the inCorrectElements
     */
    protected Set<String> generateIncorrectElements (char minValue, char maxValue) {
        incorrectElements = new HashSet<>(maxValue - minValue);
        for (int i = minValue; i <= maxValue; i++) {
            incorrectElements.add("" + (char) i);
        }
        incorrectElements.remove(correctElement);
        Log.d(TAG, String.format("Question: %s, Answer: %s", question, correctElement));
        Log.d(TAG, incorrectElements.toString());
        return incorrectElements;
    }

    /**
     * Helper method to generate an Set of incorrectElements using an int range.
     * Values calculated will be between the minValue and maxValue indicated.
     *
     * @param minValue minimum value of the Set
     * @param maxValue maximum value of the Set
     * @return Set of the inCorrectElements
     */
    protected Set<String> generateIncorrectElements (int minValue, int maxValue) {
        incorrectElements = new HashSet<>(maxValue - minValue);
        for (int i = minValue; i <= maxValue; i++) {
            incorrectElements.add("" + i);
        }
        incorrectElements.remove(correctElement);
        Log.d(TAG, String.format("Question: %s, Answer: %s", question, correctElement));
        Log.d(TAG, incorrectElements.toString());
        return incorrectElements;
    }

}