package ca.chrisbarrett.bubblecount.game;

import ca.chrisbarrett.bubblecount.util.Values;

/**
 * Concrete class of the GameFeed GameEngine to generate a counting game. GameFeed will generate a number
 * between the range appropriate for the player's age. A question for display between range will also be generated. The question generated will display fouÒr leading numbers.
 * <p/>
 * For example, if the age is 5 or less, the range is (1 - 10). An correctElement will be generated in the
 * range of 5 - 10. If the number is 10, the question will be "7, 8, 9, ?"
 * <p/>
 * <a href="https://www.bced.gov.bc.ca/irp/curric_grade_packages/grkcurric_req.pdf">https://www.bced.gov.bc.ca/irp/curric_grade_packages/grkcurric_req.pdf</a>
 *
 * @author Chris Barrett
 * @see
 * @since Jul 1, 2016
 */
public class CountGameEngine extends AbstractEngine {

    private int maxValue;
    private int minValue;

    /**
     * Default Constructor. Generates using the {@link AbstractEngine#DEFAULT_AGE}
     */
    public CountGameEngine () {
        this(DEFAULT_AGE);
    }

    /**
     * Constructor. Accepts the age of the child, and adjusts the range of values accordingly.
     * Once the values have been assigned, the constructor calls {@link #randomize()}
     *
     * @param age
     */
    public CountGameEngine (int age) {
        maxValue = 10;
        minValue = 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void randomize () {
        int intAnswer = Values.RANDOM.nextInt(maxValue - minValue - 2) + minValue + 3;
        StringBuilder build = new StringBuilder(9).append('?');
        for (int i = intAnswer - 1; i > intAnswer - 4; i--) {
            build.insert(0, i + "  ");
        }
        correctElement = "" + intAnswer;
        question = build.toString();
        generateIncorrectElements(minValue, maxValue * 3);
    }
}
