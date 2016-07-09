package ca.chrisbarrett.bubblecount.game;

import java.util.Calendar;
import java.util.Random;
import java.util.Set;

/**
 * Abstract game engine. Games are based on the British Columbia school curriculum of 2007.
 * Fields are intentionally protected in order to allow faster access by derived classes.
 *
 * @author Chris Barrett
 * @see <a href="https://www.bced.gov.bc.ca/irp/pdfs/mathematics/2007mathk7.pdf">https://www.bced.gov.bc.ca/irp/pdfs/mathematics/2007mathk7.pdf</a>
 * @since Jul 1, 2016
 */
public abstract class AbstractEngine implements GameEngine {

    protected static Random rand = new Random(Calendar.getInstance().getTimeInMillis());
    protected String answer;
    protected String question;
    protected Set<String> filler;

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

    @Override
    public Set<String> getFiller() {
        return filler;
    }

    @Override
    public void setFiller(Set<String> filler) {
        this.filler = filler;
    }

}