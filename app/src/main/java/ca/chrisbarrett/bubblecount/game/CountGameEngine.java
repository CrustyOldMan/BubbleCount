package ca.chrisbarrett.bubblecount.game;

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

    public CountGameEngine(int age) {
        super("10","7  8  9");
    }
}
