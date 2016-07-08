package ca.chrisbarrett.bubblecount.game;

import java.util.Random;

/**
 * Abstract game engine. Games are based on the British Columbia school curriculum of 2007.
 * Fields are intentionally protected in order to allow faster access by derived classes.
 *
 * @author Chris Barrett
 * @see <a href="https://www.bced.gov.bc.ca/irp/pdfs/mathematics/2007mathk7.pdf">https://www.bced.gov.bc.ca/irp/pdfs/mathematics/2007mathk7.pdf</a>
 * @since Jul 1, 2016
 */
public class AbstractEngine implements GameEngine {

    protected static final Random RAND = new Random();
    protected String answer;
    protected String question;

    public AbstractEngine(String answer, String question) {
        this.answer = answer;
        this.question = question;
    }

    @Override
    public String getAnswer() {
        return answer;
    }

    @Override
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public void setQuestion(String question) {
        this.question = question;
    }
}
