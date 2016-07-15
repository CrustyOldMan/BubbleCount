package ca.chrisbarrett.bubblecount.util;


/**
 * A placeholder for shared Values information, such as Preference, Extra tags
 *
 * @author Chris Barrett
 * @see
 * @since Jul 10, 2016
 */
public final class Values {

    public final static int NO_ID = -1;         // placeholder

    private Values (){}

    /**
     * Contains tags used during Extras
     */
    public static abstract class  Extra {
        public static final String GAME_SELECTOR = "GAME_SELECTOR";


    }

    /**
     * Contains tags used during startActivityForResult ResultRequest values
     */
    public static abstract class ResultRequest {
        public static final int ACTIVITY_GAME = 2000;       // Used for starting GameActivity
        public static final int ACTIVITY_SETTINGS = 1000;   // Used for starting SettingsActivity
    }
}
