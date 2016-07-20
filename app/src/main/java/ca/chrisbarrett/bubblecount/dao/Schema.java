package ca.chrisbarrett.bubblecount.dao;

import android.provider.BaseColumns;

/**
 * This abstract class contains the schema for the databases.
 *
 * @author Chris Barrett
 * @see
 * @since Jul 10, 2016
 */
public abstract class Schema {

    // Package private for enhanced security.
    static final String DB_NAME = "bubble_count";
    static final int DB_VERSION = 1;

    // Convenience
    private static final String INTEGER_TYPE = " INTEGER ";
    private static final String TEXT_TYPE = " TEXT ";
    private static final String BRACKET_OPEN = " ( ";
    private static final String BRACKET_CLOSE = " ) ";
    private static final String REFERENCES = " REFERENCES ";
    private static final String COMMA_SEP = ",";
    private static final String FOREIGN_KEY = " FOREIGN KEY ";

    private Schema () {
        // to stop instantiation
    }

    /**
     * Feeds the Player table
     */
    public static abstract class PlayerTable implements BaseColumns {

        public static final String TABLE_NAME = "player";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_YEAR_OF_BIRTH = "year_of_birth";
        public static final String COLUMN_CREATED_ON = "created_on";
        public static final String COLUMN_SYNCED_ON = "synced_on";

        public static final String[] COLUMNS = new String[]{
                PlayerTable._ID,
                PlayerTable.COLUMN_NAME,
                PlayerTable.COLUMN_YEAR_OF_BIRTH,
                PlayerTable.COLUMN_CREATED_ON,
                PlayerTable.COLUMN_SYNCED_ON
        };

        static final String PLAYER_TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        PlayerTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        PlayerTable.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                        PlayerTable.COLUMN_YEAR_OF_BIRTH + INTEGER_TYPE + COMMA_SEP +
                        PlayerTable.COLUMN_CREATED_ON + INTEGER_TYPE + COMMA_SEP +
                        PlayerTable.COLUMN_SYNCED_ON + INTEGER_TYPE +
                        " )";

        static final String PLAYER_TABLE_DELETE =
                "DROP TABLE IF EXISTS " + PlayerTable.TABLE_NAME;
    }

    /**
     * Feeds the Game table
     */
    public static abstract class GameTable implements BaseColumns {

        public static final String TABLE_NAME = "game";
        public static final String COLUMN_DISPLAY_NAME = "display_name";
        public static final String COLUMN_CLASSPATH_NAME = "classpath_name";
        public static final String COLUMN_MINIMUM_AGE = "minimum_age";

        public static final String[] COLUMNS = new String[]{
                GameTable._ID,
                GameTable.COLUMN_DISPLAY_NAME,
                GameTable.COLUMN_CLASSPATH_NAME,
                GameTable.COLUMN_MINIMUM_AGE
        };

        static final String GAME_TABLE_CREATE =
                "CREATE TABLE " + GameTable.TABLE_NAME + " (" +
                        GameTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        GameTable.COLUMN_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP +
                        GameTable.COLUMN_CLASSPATH_NAME + TEXT_TYPE + COMMA_SEP +
                        GameTable.COLUMN_MINIMUM_AGE + INTEGER_TYPE +
                        " )";

        static final String GAME_TABLE_DELETE =
                "DROP TABLE IF EXISTS " + GameTable.TABLE_NAME;
    }


    /**
     * Feeds the GameResult table
     */
    public static abstract class GameResultTable implements BaseColumns {

        public static final String TABLE_NAME = "game_result";
        public static final String COLUMN_TIME_RESULT = "time_result";
        public static final String COLUMN_CREATED_ON = "created_on";
        public static final String COLUMN_SYNCED_ON = "synced_on";
        public static final String COLUMN_FK_PLAYER_ID = "fk_player_id";
        public static final String COLUMN_FK_GAME_ID = "fk_game_id";

        public static final String[] COLUMNS = new String[]{
                GameResultTable._ID,
                GameResultTable.COLUMN_TIME_RESULT,
                GameResultTable.COLUMN_CREATED_ON,
                GameResultTable.COLUMN_SYNCED_ON,
                GameResultTable.COLUMN_FK_PLAYER_ID,
                GameResultTable.COLUMN_FK_GAME_ID
        };

        static final String GAMERESULT_TABLE_CREATE =
                "CREATE TABLE " + GameResultTable.TABLE_NAME + " (" +
                        GameResultTable._ID + " INTEGER PRIMARY KEY," +
                        GameResultTable.COLUMN_TIME_RESULT + INTEGER_TYPE + COMMA_SEP +
                        GameResultTable.COLUMN_CREATED_ON + INTEGER_TYPE + COMMA_SEP +
                        GameResultTable.COLUMN_SYNCED_ON + INTEGER_TYPE + COMMA_SEP +
                        GameResultTable.COLUMN_FK_PLAYER_ID + INTEGER_TYPE + COMMA_SEP +
                        GameResultTable.COLUMN_FK_GAME_ID + INTEGER_TYPE + COMMA_SEP +
                        FOREIGN_KEY + BRACKET_OPEN + GameResultTable.COLUMN_FK_PLAYER_ID + BRACKET_CLOSE +
                        REFERENCES + PlayerTable.TABLE_NAME + BRACKET_OPEN + PlayerTable._ID + BRACKET_CLOSE + COMMA_SEP +
                        FOREIGN_KEY + BRACKET_OPEN + GameResultTable.COLUMN_FK_GAME_ID + BRACKET_CLOSE +
                        REFERENCES + GameTable.TABLE_NAME + BRACKET_OPEN + GameTable._ID + BRACKET_CLOSE +
                        " )";

        static final String GAMERESULT_TABLE_DELETE =
                "DROP TABLE IF EXISTS " + GameResultTable.TABLE_NAME;
    }

}
