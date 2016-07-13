package ca.chrisbarrett.bubblecount.game;

import java.util.LinkedHashSet;
import java.util.Random;

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
    protected String answer;
    protected String question;
    protected LinkedHashSet<String> elements;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAnswer () {
        return answer;
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
    public LinkedHashSet<String> getElements () {
        return elements;
    }

    /**
     * Helper method to generate an array of elements. Subclasses must implement. The return should
     * contain the answer as the first element.
     *
     * @return an orderly Set with the answer as the first element
     */
    abstract protected LinkedHashSet<String> generateElements();

}