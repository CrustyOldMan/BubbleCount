package ca.chrisbarrett.bubblecount.util;


import java.util.Random;

/**
 * A placeholder for shared Values information, such as Preference, Extra tags
 *
 * @author Chris Barrett
 * @see
 * @since Jul 10, 2016
 */
public final class Values {

    public static final Random RANDOM = new Random();

    private Values (){}

    /**
     * Contains tags used during Extras
     */
    public static abstract class  Extra {
        public static final String GAME_ID_SELECTOR = "GAME_ID_SELECTOR";
        public static final String PLAYER_ID_SELECTOR = "PLAYER_ID_SELECTOR";

    }

    /**
     * Contains tags used during startActivityForResult ResultRequest values
     */
    public static abstract class ResultRequest {
        public static final int ACTIVITY_GAME = 2000;       // Used for starting GameActivity
        public static final int ACTIVITY_SETTINGS = 1000;   // Used for starting SettingsActivity
    }
}
