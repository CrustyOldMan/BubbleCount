package ca.chrisbarrett.bubblecount.game;

import java.util.HashSet;

/**
 * Concrete class of the Game GameEngine to generate a counting game. Game will generate a number
 * between the range appropriate for the player's age. A question for display between range will also be generated. The question generated will display fouÒr leading numbers.
 * <p/>
 * For example, if the age is 5 or less, the range is (1 - 10). An answer will be generated in the
 * range of 5 - 10. If the number is 10, the question will be "7, 8, 9, ?"
 *
 * @author Chris Barrett
 * @see <a href="https://www.bced.gov.bc.ca/irp/pdfs/mathematics/2007mathk7.pdf">https://www.bced.gov.bc.ca/irp/pdfs/mathematics/2007mathk7.pdf</a>
 * @since Jul 1, 2016
 */
public class CountGameEngine extends AbstractEngine {

    public CountGameEngine() {
        this(5);
    }

    public CountGameEngine(int age) {
        StringBuilder build = new StringBuilder(9).append('?');
        int intAnswer = rand.nextInt(10 - 3) + 4;
        for (int i = intAnswer - 1; i > intAnswer - 4; i--) {
            build.insert(0, i + "  ");
        }
        answer = "" + intAnswer;
        question = build.toString();
        generateFiller();
    }

    /**
     * Helper method to generate an array of content containing numbers near the
     * answer. Selection will be {@link #DEFAULT_SIZE} size.
     */
    protected void generateFiller() {
        filler = new HashSet<String>(DEFAULT_SIZE);
        int intAnswer = Integer.parseInt(answer);
        for (int i = 1; i < DEFAULT_SIZE; i++) {
            if (i != intAnswer) {
                filler.add("" + i);
            }
        }
    }

}
