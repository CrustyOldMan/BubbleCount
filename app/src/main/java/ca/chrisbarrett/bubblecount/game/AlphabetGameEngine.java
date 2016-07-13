package ca.chrisbarrett.bubblecount.game;

import android.util.Log;

import java.util.LinkedHashSet;

/**
 * Concrete class of the GameEngine to generate a letter finding game. GameFeed will generate a letter.
 * The question generated will display four leading letters.
 * <p/>
 * For example, if the answer generated is "E", the question will be "B  C  D  ?"
 * <p/>
 * <a href="https://www.bced.gov.bc.ca/irp/curric_grade_packages/grkcurric_req.pdf">https://www.bced.gov.bc.ca/irp/curric_grade_packages/grkcurric_req.pdf</a>
 *
 * @author Chris Barrett
 * @see
 * @since Jul 1, 2016
 */
public class AlphabetGameEngine extends AbstractEngine {

    /**
     * Default Constructor. Generates using the {@link AbstractEngine#DEFAULT_AGE}
     */
    public AlphabetGameEngine () {
        this(DEFAULT_AGE);
    }

    /**
     * Constructor. Accepts the age of the child, and adjusts the range of values accordingly.
     * For this type of game, however, age is not important. The range will always be 'A' to 'Z'
     *
     * @param age
     */
    public AlphabetGameEngine (int age) {
        int max = 'Z';
        int min = 'A';

        char charAnswer = (char) (rand.nextInt(max - min - 2) + min + 3);
        StringBuilder build = new StringBuilder(9).append('?');
        for (int i = charAnswer - 1; i > charAnswer - 4; i--) {
            build.insert(0, (char) i + "  ");
        }
        answer = "" + charAnswer;
        question = build.toString();
        elements = generateElements();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LinkedHashSet<String> generateElements () {
        LinkedHashSet<String> temp = new LinkedHashSet<>(ALPHABET_SIZE);
        temp.add(answer);
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            temp.add("" + (char) (i + 'A'));
        }
        Log.d(TAG, temp.toString());
        return temp;
    }
}