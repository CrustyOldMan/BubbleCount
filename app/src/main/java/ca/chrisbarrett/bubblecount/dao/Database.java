package ca.chrisbarrett.bubblecount.dao;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

import ca.chrisbarrett.bubblecount.dao.model.Game;
import ca.chrisbarrett.bubblecount.dao.model.Player;

/**
 * Helper class for the database. This class handles creation and upgrades of the database and
 * tables. Contract related material is available via the {@link Schema} class.
 * Database supports three tables:
 * <li>
 * <ul>{@link ca.chrisbarrett.bubblecount.dao.Schema.PlayerTable} for storing Player details</ul>
 * <ul>{@link ca.chrisbarrett.bubblecount.dao.Schema.GameTable} for storing Game details table</ul>
 * <ul>{@link ca.chrisbarrett.bubblecount.dao.Schema.GameTable} for storing GameResult details table</ul>
 * </li>
 *
 * @author Chris Barrett
 * @see
 * @since Jul 10, 2016
 */
public class Database {

    private static final String TAG = "AppDatabaseHelper";
    public static PlayerDao playerDao;
    public static GameDao gameDao;
    public static GameResultDao gameResultDao;
    private final Context context;
    private DatabaseHelper dbHelper;

    public Database (Context context) {
        this.context = context;
    }

    public Database open () throws SQLException {
        dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        playerDao = new PlayerDaoImpl(db);
        gameDao = new GameDaoImpl(db);
        gameResultDao = new GameResultDaoImpl(db);

        return this;
    }

    public void close () {
        dbHelper.close();
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper (Context context) {
            super(context, Schema.DB_NAME, null, Schema.DB_VERSION);
        }

        @Override
        public void onCreate (SQLiteDatabase db) {
            Log.d(TAG, "onCreate called.");
            db.execSQL(Schema.PlayerTable.PLAYER_TABLE_CREATE);
            db.execSQL(Schema.GameTable.GAME_TABLE_CREATE);
            db.execSQL(Schema.GameResultTable.GAMERESULT_TABLE_CREATE);
            Log.d(TAG, "Tables have been created.");
            // The placeHolder must always be first. This will be used when no other Player has been assigned.
            // Also used as the foreign key for GameResults when the Player is deleted.
            Player placeHolder = new Player(1, "No Player Choosen", 9999, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()));
            PlayerDao playerDao = new PlayerDaoImpl(db);
            boolean result = playerDao.insertPlayer(placeHolder);
            Log.d(TAG, String.format("Add Default Player has%ssucceeded.", result ? " " : " not "));
            // Inserts the known games into the database
            // Note: A placeholder is used here, too. This one will be used to create a Random game from an age approriate list.
            Game game1 = new Game(1, "Random Bubble Game", "", 0);
            Game game2 = new Game("Bubble Number Count", "ca.chrisbarrett.bubblecount.game.CountGameEngine", 0);
            Game game3 = new Game("Bubble Letter Count", "ca.chrisbarrett.bubblecount.game.AlphabetGameEngine", 0);
            GameDao gameDao = new GameDaoImpl(db);
            result = gameDao.insertGame(game1, game2, game3);
            Log.d(TAG, String.format("Add Games has%ssucceeded.", result ? " " : " not "));
        }

        @Override
        public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(Schema.GameResultTable.GAMERESULT_TABLE_DELETE);
            db.execSQL(Schema.PlayerTable.PLAYER_TABLE_DELETE);
            db.execSQL(Schema.GameTable.GAME_TABLE_DELETE);
        }
    }
}




