package ca.chrisbarrett.bubblecount.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.chrisbarrett.bubblecount.dao.model.Game;
import ca.chrisbarrett.bubblecount.dao.model.GameResult;
import ca.chrisbarrett.bubblecount.dao.model.Player;
import ca.chrisbarrett.bubblecount.game.GameEngine;

/**
 * Helper class for the database. This class handles creation and upgrades of the database and
 * tables. Contract related material is available via the {@link ca.chrisbarrett.bubblecount.dao
 * .AppDatabaseHelper.Feeder} inner class, which has it's own inner classes corresponding to each
 * table:
 * <li>
 * <ul>{@link Feeder.PlayerFeed} PlayerFeed table</ul>
 * <ul>{@link Feeder.GameFeed} GameFeed table</ul>
 * <ul>{@link Feeder.GameResultFeed} GameResultFeed table</ul>
 * </li>
 *
 * @author Chris Barrett
 * @see
 * @since Jul 10, 2016
 */
public class AppDatabaseHelper extends SQLiteOpenHelper implements AppDatabase {

    public final static int DB_VERSION = 1;     // current database version
    private static final String TAG = "AppDatabaseHelper";

    //
    // LifeCycles Events Begin Here
    //

    /**
     * Constructor for the AppDatabaseHelper. Creates a database, if required and checks for
     * upgrade/downgrade requirements
     *
     * @param context
     */
    public AppDatabaseHelper(Context context) {
        super(context, Feeder.DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Attempting to create database tables...");
        try {
            Log.d(TAG, Feeder.PlayerFeed.SQL_CREATE_ENTRIES);
            db.execSQL(Feeder.PlayerFeed.SQL_CREATE_ENTRIES);
            Log.d(TAG, Feeder.GameFeed.SQL_CREATE_ENTRIES);
            db.execSQL(Feeder.GameFeed.SQL_CREATE_ENTRIES);
            Log.d(TAG, Feeder.GameResultFeed.SQL_CREATE_ENTRIES);
            db.execSQL(Feeder.GameResultFeed.SQL_CREATE_ENTRIES);
            insertGamesAtCreation(db);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Feeder.PlayerFeed.SQL_DELETE_ENTRIES);
        db.execSQL(Feeder.GameFeed.SQL_DELETE_ENTRIES);
        db.execSQL(Feeder.GameResultFeed.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    //
    //  Helper methods begin here
    //

    /**
     * {@inheritDoc}
     **/
    @Override
    public Player getPlayerById(SQLiteDatabase db, long id) {
        Log.d(TAG, "Retrieving Player " + id);
        Player player = null;
        Cursor cursor = null;
        try {
            cursor = db.query(Feeder.PlayerFeed.TABLE_NAME,
                    new String[]{Feeder.PlayerFeed._ID, Feeder.PlayerFeed.COLUMN_NAME,
                            Feeder.PlayerFeed.COLUMN_YEAR_OF_BIRTH, Feeder.PlayerFeed.COLUMN_CREATED_ON,
                            Feeder.PlayerFeed.COLUMN_SYNCED_ON},
                    Feeder.PlayerFeed._ID + "+ ?",
                    new String[]{Long.toString(id)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                player = new Player(cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        Feeder.dateFromDatabaseFormat(cursor.getString(3)),
                        Feeder.dateFromDatabaseFormat(cursor.getString(4)));
            }
        } catch (SQLException e) {
            Log.e(TAG, "getPlayerById caused: " + e.getMessage());
        } finally {
            cursor.close();
        }
        Log.d(TAG, "Returning Player: " + player.toString());
        return player;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public GameResult getGameResultById(SQLiteDatabase db, long id) {
        Log.d(TAG, "Retrieving GameResult " + id);
        GameResult gameResult = null;
        Cursor cursor = null;
        try {
            cursor = db.query(Feeder.GameResultFeed.TABLE_NAME,
                    new String[]{Feeder.GameResultFeed._ID, Feeder.GameResultFeed.COLUMN_TIME_RESULT,
                            Feeder.GameResultFeed.COLUMN_CREATED_ON, Feeder.GameResultFeed.COLUMN_SYNCED_ON,
                            Feeder.GameResultFeed.COLUMN_FK_PLAYER_ID,Feeder.GameResultFeed.COLUMN_FK_GAME_ID},
                    Feeder.GameResultFeed._ID + "+ ?",
                    new String[]{Long.toString(id)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                gameResult = new GameResult(cursor.getLong(0),
                        cursor.getInt(1),
                        Feeder.dateFromDatabaseFormat(cursor.getString(2)),
                        Feeder.dateFromDatabaseFormat(cursor.getString(3)),
                        cursor.getLong(4),
                        cursor.getLong(5));
            }
        } catch (SQLException e) {
            Log.e(TAG, "getGameResultById caused: " + e.getMessage());
        } finally {
            cursor.close();
        }
        Log.d(TAG, "Returning GameResult: " + gameResult.toString());
        return gameResult;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public Game getGameById(SQLiteDatabase db, long id) {
        Log.d(TAG, "Retrieving Game " + id);
        Game game = null;
        Cursor cursor = null;
        try {
            cursor = db.query(Feeder.GameFeed.TABLE_NAME,
                    new String[]{Feeder.GameFeed._ID, Feeder.GameFeed.COLUMN_DISPLAY_NAME,
                            Feeder.GameFeed.COLUMN_CLASSPATH_NAME, Feeder.GameFeed.COLUMN_MINIMUM_AGE},
                    Feeder.GameFeed._ID + "+ ?",
                    new String[]{Long.toString(id)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                game = new Game(cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3));
            }
        } catch (SQLException e) {
            Log.e(TAG, "getGameById caused: " + e.getMessage());
        } finally {
            cursor.close();
        }
        Log.d(TAG, "Returning Game: " + game.toString());
        return game;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public List<Game> getAllGames(SQLiteDatabase db) {
        Log.d(TAG, "Retrieving All Games");
        List<Game> games = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(Feeder.GameFeed.TABLE_NAME,
                    new String[]{Feeder.GameFeed._ID, Feeder.GameFeed.COLUMN_DISPLAY_NAME,
                            Feeder.GameFeed.COLUMN_CLASSPATH_NAME, Feeder.GameFeed.COLUMN_MINIMUM_AGE},
                    null, null, null, null, null);
            cursor.moveToFirst();
            do {
                games.add(new Game(cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3)));
            } while (cursor.moveToNext());
        } catch (SQLException e) {
            Log.e(TAG, "getAllGames caused: " + e.getMessage());
        } finally {
            cursor.close();
        }
        Log.d(TAG, "Returning Games: " + games.toString());
        return games;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public List<Player> getAllPlayers(SQLiteDatabase db) {
        Log.d(TAG, "Retrieving All Players");
        List<Player> players = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(Feeder.PlayerFeed.TABLE_NAME,
                    new String[]{Feeder.PlayerFeed._ID, Feeder.PlayerFeed.COLUMN_NAME,
                            Feeder.PlayerFeed.COLUMN_YEAR_OF_BIRTH, Feeder.PlayerFeed.COLUMN_CREATED_ON,
                            Feeder.PlayerFeed.COLUMN_SYNCED_ON},
                    null, null, null, null, null);
            cursor.moveToFirst();
            do {
                players.add(new Player(cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        Feeder.dateFromDatabaseFormat(cursor.getString(3)),
                        Feeder.dateFromDatabaseFormat(cursor.getString(4))));
            } while (cursor.moveToNext());
        } catch (SQLException e) {
            Log.e(TAG, "getAllPlayers caused: " + e.getMessage());
        } finally {
            cursor.close();
        }
        Log.d(TAG, "Returning Players: " + players.toString());
        return players;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public List<GameResult> getAllGamesResultsForPlayer(SQLiteDatabase db, long playerId) {
        Log.d(TAG, "Retrieving GameResults for Player " + playerId);
        List<GameResult> results = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(Feeder.GameResultFeed.TABLE_NAME,
                    new String[]{Feeder.GameResultFeed._ID, Feeder.GameResultFeed.COLUMN_TIME_RESULT,
                            Feeder.GameResultFeed.COLUMN_CREATED_ON, Feeder.GameResultFeed.COLUMN_SYNCED_ON,
                            Feeder.GameResultFeed.COLUMN_FK_PLAYER_ID,Feeder.GameResultFeed.COLUMN_FK_GAME_ID},
                    Feeder.GameResultFeed.COLUMN_FK_PLAYER_ID + "+ ?",
                    new String[]{Long.toString(playerId)},
                    null, null, null);

            cursor.moveToFirst();
            do{
                results.add(new GameResult(cursor.getLong(0),
                        cursor.getInt(1),
                        Feeder.dateFromDatabaseFormat(cursor.getString(2)),
                        Feeder.dateFromDatabaseFormat(cursor.getString(3)),
                        cursor.getLong(4),
                        cursor.getLong(5)));
            }while(cursor.moveToNext());
        } catch (SQLException e) {
            Log.e(TAG, "getAllGamesResultsForPlayer caused: " + e.getMessage());
        } finally {
            cursor.close();
        }
        Log.d(TAG, "Returning GameResults: " + results.toString());
        return results;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void updatePlayer(SQLiteDatabase db, Player player) {

    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void deletePlayer(SQLiteDatabase db, Player player) {

    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public long insertGameResult(SQLiteDatabase db, GameResult result) {
        Log.d(TAG, "Inserting to database GameResult: " + result.toString());
        ContentValues values = new ContentValues();
        values.put(Feeder.GameResultFeed.COLUMN_TIME_RESULT, result.getTimeResult());
        values.put(Feeder.GameResultFeed.COLUMN_CREATED_ON, Feeder.dateToDatabaseFormat(result.getCreatedOn()));
        values.put(Feeder.GameResultFeed.COLUMN_SYNCED_ON, Feeder.dateToDatabaseFormat(result.getSyncedOn()));
        values.put(Feeder.GameResultFeed.COLUMN_FK_GAME_ID, result.getGameId());
        values.put(Feeder.GameResultFeed.COLUMN_FK_PLAYER_ID, result.getPlayerId());
        long id = db.insert(Feeder.GameResultFeed.TABLE_NAME, null, values);
        Log.d(TAG, "Success! Created GameResult in record location: " + id);
        return id;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public long insertPlayer(SQLiteDatabase db, Player player) {
        Log.d(TAG, "Inserting to database Player: " + player.toString());
        ContentValues values = new ContentValues();
        values.put(Feeder.PlayerFeed.COLUMN_NAME, player.getName());
        values.put(Feeder.PlayerFeed.COLUMN_YEAR_OF_BIRTH, player.getYearOfBirth());
        values.put(Feeder.PlayerFeed.COLUMN_CREATED_ON, Feeder.dateToDatabaseFormat(player.getCreatedOn()));
        values.put(Feeder.PlayerFeed.COLUMN_SYNCED_ON, Feeder.dateToDatabaseFormat(player.getSyncedOn()));
        long id = db.insert(Feeder.PlayerFeed.TABLE_NAME, null, values);
        Log.d(TAG, "Success! Created Player in record location: " + id);
        return id;
    }


    /**
     * Inserts a Game to the database. As Games can only be inserted into the database during {@link #onCreate(SQLiteDatabase)}
     * or {@link #onUpgrade(SQLiteDatabase, int, int)}, method is protected.
     *
     * @param db
     * @param game
     * @return
     */
    protected long insertGame(SQLiteDatabase db, Game game) {
        Log.d(TAG, "Inserting to database Game: " + game.toString());
        ContentValues values = new ContentValues();
        values.put(Feeder.GameFeed.COLUMN_DISPLAY_NAME, game.getDisplayName());
        values.put(Feeder.GameFeed.COLUMN_CLASSPATH_NAME, game.getClassPathName());
        values.put(Feeder.GameFeed.COLUMN_MINIMUM_AGE, game.getMinimumAge());
        long id = db.insert(Feeder.GameFeed.TABLE_NAME, null, values);
        Log.d(TAG, "Success! Created Game in record location: " + id);
        return id;
    }

    /**
     * Inserts the known Game objects into the database at database creation.
     *
     * @param db
     */
    protected void insertGamesAtCreation(SQLiteDatabase db) {
        Log.d(TAG, "insertGamesAtCreation attempting to store known Games.");
        Game game = new Game("Letter Sequence", "ca.chrisbarrett.bubblecount.game.AlphabetGameEngine", GameEngine.NO_MINIMUM_AGE);
        insertGame(db, game);
        game = new Game("Number Sequence", "ca.chrisbarrett.bubblecount.game.CountGameEngine", GameEngine.NO_MINIMUM_AGE);
        insertGame(db, game);
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
        private static final String REFERENCES = " REFERENCES ";
        private static final String COMMA_SEP = ",";
        private static final String FOREIGN_KEY = " FOREIGN KEY ( ";

        private Feeder() {
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
        public static Date dateFromDatabaseFormat(String input) {
            SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_DATE_TIME);
            Date date = null;
            try {
                date = formatter.parse(input);
            } catch (NullPointerException e) {
                Log.e(TAG, "String parameter is null.");
                throw new IllegalArgumentException("String parameter is null.");
            } catch (ParseException e) {
                String message = String.format("dateFromDatabaseFormat String parameter '%s' caused: %s", input, e.getMessage());
                Log.e(TAG, message);
                return null;
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
        public static String dateToDatabaseFormat(Date date) {
            SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_DATE_TIME);
            String string = null;
            try {
                string = formatter.format(date);
            } catch (NullPointerException e) {
                Log.e(TAG, "dateToDatabaseFormat Date parameter is null.");
                return "";
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
                            PlayerFeed.COLUMN_CREATED_ON + TEXT_TYPE + COMMA_SEP +
                            PlayerFeed.COLUMN_SYNCED_ON + TEXT_TYPE +
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
        public static abstract class GameResultFeed implements BaseColumns {

            public static final String TABLE_NAME = "game_result";
            public static final String COLUMN_TIME_RESULT = "time_result";
            public static final String COLUMN_CREATED_ON = "created_on";
            public static final String COLUMN_SYNCED_ON = "synced_on";
            public static final String COLUMN_FK_PLAYER_ID = "fk_player_id";
            public static final String COLUMN_FK_GAME_ID = "fk_game_id";

            private static final String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + GameResultFeed.TABLE_NAME + " (" +
                            GameResultFeed._ID + " INTEGER PRIMARY KEY," +
                            GameResultFeed.COLUMN_TIME_RESULT + INTEGER_TYPE + COMMA_SEP +
                            GameResultFeed.COLUMN_CREATED_ON + TEXT_TYPE + COMMA_SEP +
                            GameResultFeed.COLUMN_SYNCED_ON + TEXT_TYPE + COMMA_SEP +
                            GameResultFeed.COLUMN_FK_PLAYER_ID + INTEGER_TYPE + COMMA_SEP +
                            GameResultFeed.COLUMN_FK_GAME_ID + INTEGER_TYPE + COMMA_SEP +
                            FOREIGN_KEY + GameResultFeed.COLUMN_FK_PLAYER_ID + BRACKET_CLOSE +
                            REFERENCES + PlayerFeed.TABLE_NAME + BRACKET_OPEN + PlayerFeed._ID + BRACKET_CLOSE + COMMA_SEP +
                            FOREIGN_KEY + GameResultFeed.COLUMN_FK_GAME_ID + BRACKET_CLOSE +
                            REFERENCES + GameFeed.TABLE_NAME + BRACKET_OPEN + GameFeed._ID + BRACKET_CLOSE +
                            " )";

            private static final String SQL_DELETE_ENTRIES =
                    "DROP TABLE IF EXISTS " + GameResultFeed.TABLE_NAME;
        }
    }
}
