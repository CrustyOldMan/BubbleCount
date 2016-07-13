package ca.chrisbarrett.bubblecount.game;

import android.util.Log;

import java.util.LinkedHashSet;

/**
 * Concrete class of the GameFeed GameEngine to generate a counting game. GameFeed will generate a number
 * between the range appropriate for the player's age. A question for display between range will also be generated. The question generated will display fouÒr leading numbers.
 * <p/>
 * For example, if the age is 5 or less, the range is (1 - 10). An answer will be generated in the
 * range of 5 - 10. If the number is 10, the question will be "7, 8, 9, ?"
 * <p/>
 * <a href="https://www.bced.gov.bc.ca/irp/curric_grade_packages/grkcurric_req.pdf">https://www.bced.gov.bc.ca/irp/curric_grade_packages/grkcurric_req.pdf</a>
 *
 * @author Chris Barrett
 * @see
 * @since Jul 1, 2016
 */
public class CountGameEngine extends AbstractEngine {

    /**
     * Default Constructor. Generates using the {@link AbstractEngine#DEFAULT_AGE}
     */
    public CountGameEngine () {
        this(DEFAULT_AGE);
    }

    /**
     * Constructor. Accepts the age of the child, and adjusts the range of values accordingly.
     *
     * @param age
     */
    public CountGameEngine (int age) {
        int max = 10;
        int min = 1;

        int intAnswer = rand.nextInt(max - min - 2) + min + 3;
        StringBuilder build = new StringBuilder(9).append('?');
        for (int i = intAnswer - 1; i > intAnswer - 4; i--) {
            build.insert(0, i + "  ");
        }

        answer = "" + intAnswer;
        question = build.toString();
        elements = generateElements();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LinkedHashSet<String> generateElements () {
        LinkedHashSet<String> temp = new LinkedHashSet<>(DEFAULT_SIZE);
        temp.add(answer);
        int intAnswer = Integer.parseInt(answer);
        for (int i = 1; i < DEFAULT_SIZE; i++) {
            temp.add("" + i);
        }
        Log.d(TAG, temp.toString());
        return temp;
    }
}
