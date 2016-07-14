package ca.chrisbarrett.bubblecount.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.chrisbarrett.bubblecount.dao.model.Game;
import ca.chrisbarrett.bubblecount.dao.model.GameResult;
import ca.chrisbarrett.bubblecount.dao.model.Player;
import ca.chrisbarrett.bubblecount.game.GameEngine;

/**
 * Helper class for the database. This class handles creation and upgrades of the database and
 * tables. Contract related material is available via the {@link ca.chrisbarrett.bubblecount.dao
 * .DatabaseHelper.Feeder} inner class, which has it's own inner classes corresponding to each
 * table:
 * <li>
 * <ul>{@link Feeder.PlayerFeed} PlayerFeed table</ul>
 * <ul>{@link Feeder.GameFeed} GameFeed table</ul>
 * <ul>{@link Feeder.GameResultFeeder} GameResultFeeder table</ul>
 * </li>
 *
 * @author Chris Barrett
 * @see
 * @since Jul 10, 2016
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public final static int DB_VERSION = 1;     // current database version
    private static final String TAG = "DatabaseHelper";

    //
    // LifeCycles Events Begin Here
    //

    /**
     * Constructor for the DatabaseHelper. Creates a database, if required and checks for
     * upgrade/downgrade requirements
     *
     * @param context
     */
    public DatabaseHelper (Context context) {
        super(context, Feeder.DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        Log.d(TAG, "Attempting to create database tables...");
        try {
            Log.d(TAG, Feeder.PlayerFeed.SQL_CREATE_ENTRIES);
            insertPlayersAtCreation(db);
            db.execSQL(Feeder.PlayerFeed.SQL_CREATE_ENTRIES);
            Log.d(TAG, Feeder.GameFeed.SQL_CREATE_ENTRIES);
            db.execSQL(Feeder.GameFeed.SQL_CREATE_ENTRIES);
            insertGamesAtCreation(db);
            Log.d(TAG, Feeder.GameResultFeeder.SQL_CREATE_ENTRIES);
            db.execSQL(Feeder.GameResultFeeder.SQL_CREATE_ENTRIES);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    //
    //  Helper methods begin here
    //


    /**
     * This method creates Game objects in the database. As Games cannot be created, updated or
     * deleted by the user, this is is protected.
     *
     * @param db
     * @param game
     */
    protected void insertGame (SQLiteDatabase db, Game game) {
        ContentValues values = new ContentValues();
        values.put(Feeder.GameFeed.COLUMN_DISPLAY_NAME, game.getDisplayName());
        values.put(Feeder.GameFeed.COLUMN_CLASSPATH_NAME, game.getClassPathName());
        values.put(Feeder.GameFeed.COLUMN_MINIMUM_AGE, game.getMinimumAge());
        db.insert(Feeder.GameFeed.TABLE_NAME, null, values);
    }

    /**
     * This method creates GameResult objects in the database
     *
     * @param db
     * @param result
     */
    public void insertGameResult (SQLiteDatabase db, GameResult result) {
        ContentValues values = new ContentValues();
        values.put(Feeder.GameResultFeeder.COLUMN_TIME_RESULT, result.getTimeResult());
        values.put(Feeder.GameResultFeeder.COLUMN_CREATED_ON, Feeder.dateToDatabaseFormat(result.getCreatedOn()));
        values.put(Feeder.GameResultFeeder.COLUMN_SYNCED_ON, Feeder.dateToDatabaseFormat(result.getSyncedOn()));
        values.put(Feeder.GameResultFeeder.COLUMN_FK_GAME_ID, result.getGameId());
        values.put(Feeder.GameResultFeeder.COLUMN_FK_PLAYER_ID, result.getPlayerId());
        db.insert(Feeder.GameResultFeeder.TABLE_NAME, null, values);
    }

    /**
     * This method created Player objects in the database
     *
     * @param db
     * @param player
     */
    public void insertPlayer (SQLiteDatabase db, Player player) {
        ContentValues values = new ContentValues();
        values.put(Feeder.PlayerFeed.COLUMN_NAME, player.getName());
        values.put(Feeder.PlayerFeed.COLUMN_YEAR_OF_BIRTH, player.getYearOfBirth());
        values.put(Feeder.PlayerFeed.COLUMN_CREATED_ON, Feeder.dateToDatabaseFormat(player.getCreatedOn()));
        values.put(Feeder.PlayerFeed.COLUMN_SYNCED_ON, Feeder.dateToDatabaseFormat(player.getSyncedOn()));
        db.insert(Feeder.PlayerFeed.TABLE_NAME, null, values);

    }

    /**
     * Inserts the known Game objects into the database at database creation.
     *
     * @param db
     */
    protected void insertGamesAtCreation (SQLiteDatabase db) {
        Game game = new Game("Letter Sequence", "ca.chrisbarrett.bubblecount.game.AlphabetGameEngine", GameEngine.NO_MINIMUM_AGE);
        insertGame(db, game);
        game = new Game("Number Sequence", "ca.chrisbarrett.bubblecount.game.CountGameEngine", GameEngine.NO_MINIMUM_AGE);
        insertGame(db, game);
    }

    /**
     * Inserts a test Player into the database at database creation.
     *
     * @param db
     */
    protected void insertPlayersAtCreation (SQLiteDatabase db) {
        Player player = new Player("Chris", 1974);
        insertPlayer(db, player);
    }


    //
    // Inner classes begin here
    //

    /**
     * A class containing the database contract details
     *
     * @author Chris Barrett
     * @see
     * @since Jul 10, 2016
     */
    public static final class Feeder {

        // Defines the date/time format used in the database
        public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
        // Helpers for created SQL statements
        private static final String DB_NAME = "bubble_count";
        private static final String BLOB_TYPE = " BLOB ";
        private static final String INTEGER_TYPE = " INTEGER ";
        private static final String REAL_TYPE = " REAL ";
        private static final String TEXT_TYPE = " TEXT ";        // also used for dates
        private static final String BRACKET_OPEN = " ( ";
        private static final String BRACKET_CLOSE = " ) ";
        private static final String REFERENCES = " ) REFERENCES ( ";
        private static final String COMMA_SEP = ",";
        private static final String FOREIGN_KEY = " FOREIGN KEY ( ";

        private Feeder () {
            // to stop instantiation
        }

        /**
         * Helper method to convert dates stored in the database using {@link #FORMAT_DATE_TIME}
         * format into java.util.Date objects
         *
         * @param input the string to be converted
         * @return a well-formed Date object
         * @throws IllegalArgumentException
         */
        public static Date dateFromDatabaseFormat (String input) throws
                IllegalArgumentException {
            SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_DATE_TIME);
            Date date = null;
            try {
                date = formatter.parse(input);
            } catch (NullPointerException e) {
                Log.e(TAG, "String parameter is null.");
                throw new IllegalArgumentException("String parameter is null.");
            } catch (ParseException e) {
                String message = String.format("String parameter %s caused: %s", input, e.getMessage());
                Log.e(TAG, message);
                throw new IllegalArgumentException(message);
            }
            return date;
        }

        /**
         * Helper method to convert {@link Date} objects into the format used in the database, as
         * referenced by {@link #FORMAT_DATE_TIME}.
         *
         * @param date the input date to be converted
         * @return a well-formed String ready for storage in the database
         * @throws IllegalArgumentException
         */
        public static String dateToDatabaseFormat (Date date) throws IllegalArgumentException {
            SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_DATE_TIME);
            String string = null;
            try {
                string = formatter.format(date);
            } catch (NullPointerException e) {
                Log.e(TAG, "Date parameter is null.");
                throw new IllegalArgumentException("Date parameter is null.");
            }
            return string;
        }

        /**
         * Feeds the Player table
         */
        public static abstract class PlayerFeed implements BaseColumns {

            public static final String TABLE_NAME = "player";
            public static final String COLUMN_NAME = "name";
            public static final String COLUMN_YEAR_OF_BIRTH = "year_of_birth";
            public static final String COLUMN_CREATED_ON = "created_on";
            public static final String COLUMN_SYNCED_ON = "synced_on";

            private static final String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + PlayerFeed.TABLE_NAME + " (" +
                            PlayerFeed._ID + " INTEGER PRIMARY KEY," +
                            PlayerFeed.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                            PlayerFeed.COLUMN_YEAR_OF_BIRTH + INTEGER_TYPE + COMMA_SEP +
                            PlayerFeed.COLUMN_CREATED_ON + TEXT_TYPE +
                            PlayerFeed.COLUMN_SYNCED_ON + TEXT_TYPE + COMMA_SEP +
                            " )";

            private static final String SQL_DELETE_ENTRIES =
                    "DROP TABLE IF EXISTS " + PlayerFeed.TABLE_NAME;
        }

        /**
         * Feeds the Game table
         */
        public static abstract class GameFeed implements BaseColumns {

            public static final String TABLE_NAME = "game";
            public static final String COLUMN_DISPLAY_NAME = "display_name";
            public static final String COLUMN_CLASSPATH_NAME = "classpath_name";
            public static final String COLUMN_MINIMUM_AGE = "minimum_age";

            private static final String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + GameFeed.TABLE_NAME + " (" +
                            GameFeed._ID + " INTEGER PRIMARY KEY," +
                            GameFeed.COLUMN_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP +
                            GameFeed.COLUMN_CLASSPATH_NAME + TEXT_TYPE + COMMA_SEP +
                            GameFeed.COLUMN_MINIMUM_AGE + INTEGER_TYPE +
                            " )";

            private static final String SQL_DELETE_ENTRIES =
                    "DROP TABLE IF EXISTS " + GameFeed.TABLE_NAME;
        }


        /**
         * Feeds the GameResult table
         */
        public static abstract class GameResultFeeder implements BaseColumns {

            public static final String TABLE_NAME = "game_result";
            public static final String COLUMN_TIME_RESULT = "time_result";
            public static final String COLUMN_CREATED_ON = "created_on";
            public static final String COLUMN_SYNCED_ON = "synced_on";
            public static final String COLUMN_FK_PLAYER_ID = "fk_player_id";
            public static final String COLUMN_FK_GAME_ID = "fk_game_id";

            private static final String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + GameResultFeeder.TABLE_NAME + " (" +
                            GameResultFeeder._ID + " INTEGER PRIMARY KEY," +
                            GameResultFeeder.COLUMN_TIME_RESULT + INTEGER_TYPE + COMMA_SEP +
                            GameResultFeeder.COLUMN_CREATED_ON + TEXT_TYPE + COMMA_SEP +
                            GameResultFeeder.COLUMN_SYNCED_ON + TEXT_TYPE + COMMA_SEP +
                            GameResultFeeder.COLUMN_FK_PLAYER_ID + INTEGER_TYPE + COMMA_SEP +
                            GameResultFeeder.COLUMN_FK_GAME_ID + INTEGER_TYPE + COMMA_SEP +
                            FOREIGN_KEY + GameResultFeeder.COLUMN_FK_PLAYER_ID +
                            REFERENCES + PlayerFeed._ID + BRACKET_CLOSE + COMMA_SEP +
                            FOREIGN_KEY + GameResultFeeder.COLUMN_FK_GAME_ID +
                            REFERENCES + GameFeed._ID + BRACKET_CLOSE +
                            " )";

            private static final String SQL_DELETE_ENTRIES =
                    "DROP TABLE IF EXISTS " + GameResultFeeder.TABLE_NAME;
        }
    }
}
