package ca.chrisbarrett.bubblecount.game;

import java.util.HashSet;

/**
 * Concrete class of the GameEngine to generate a letter finding game. Game will generate a letter.
 * The question generated will display four leading letters.
 * <p/>
 * For example, if the answer generated is "E", the question will be "B  C  D  ?"
 *
 * @author Chris Barrett
 * @see <a href="https://www.bced.gov.bc.ca/irp/curric_grade_packages/grkcurric_req.pdf">https://www.bced.gov.bc.ca/irp/curric_grade_packages/grkcurric_req.pdf</a>
 * @since Jul 1, 2016
 */
public class AlphabetGameEngine extends AbstractEngine {

    public AlphabetGameEngine() {
        this(5);
    }

    public AlphabetGameEngine(int age) {
        char charAnswer = (char) (rand.nextInt((int) 'Z' - (int) 'C') + (int) 'D');

        StringBuilder build = new StringBuilder(9).append('?');
        for (int i = charAnswer - 1; i > charAnswer - 4; i--) {
            build.insert(0, (char) i + "  ");
        }
        answer = String.valueOf(charAnswer);
        question = build.toString();
        generateFiller();
    }

    /**
     * Helper method to generate an array of content containing all letters of
     * the alphabet accept the answer value
     */
    protected void generateFiller() {
        filler = new HashSet<String>(23);
        char charAnswer = answer.charAt(0);
        for (int i = 0; i < 26; i++) {
            if ((char) i + 'A' != charAnswer) {
                filler.add(String.valueOf((char) (i + 'A')));
            }
        }
    }

}