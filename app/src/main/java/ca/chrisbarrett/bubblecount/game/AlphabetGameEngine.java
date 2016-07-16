package ca.chrisbarrett.bubblecount.game;

import ca.chrisbarrett.bubblecount.util.Values;

/**
 * Concrete class of the GameEngine to generate a letter finding game. GameFeed will generate a letter.
 * The question generated will display four leading letters.
 * <p/>
 * For example, if the correctElement generated is "E", the question will be "B  C  D  ?"
 * <p/>
 * <a href="https://www.bced.gov.bc.ca/irp/curric_grade_packages/grkcurric_req.pdf">https://www.bced.gov.bc.ca/irp/curric_grade_packages/grkcurric_req.pdf</a>
 *
 * @author Chris Barrett
 * @see
 * @since Jul 1, 2016
 */
public class AlphabetGameEngine extends AbstractEngine {

    public static final int ALPHABET_SIZE = 26;
    public static final int MIN_VALUE = 'A';
    public static final int MAX_VALUE = 'Z';
    private char maxValue = 'Z';
    private char minValue = 'A';


    /**
     * Default Constructor. Generates using the {@link AbstractEngine#DEFAULT_AGE}
     */
    public AlphabetGameEngine () {
        this(DEFAULT_AGE);
    }

    /**
     * Constructor. Accepts the age of the child, and adjusts the range of values accordingly.
     * Once the values have been assigned, the constructor calls {@link #randomize()}
     *
     * @param age
     */
    public AlphabetGameEngine (int age) {
        maxValue = 'Z';
        minValue = 'A';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void randomize () {
        char charAnswer = (char) (Values.RANDOM.nextInt(maxValue - minValue - 2) + minValue + 3);
        StringBuilder build = new StringBuilder(9).append('?');
        for (int i = charAnswer - 1; i > charAnswer - 4; i--) {
            build.insert(0, (char) i + "  ");
        }
        correctElement = "" + charAnswer;
        question = build.toString();
        generateIncorrectElements(minValue, maxValue);
    }
}