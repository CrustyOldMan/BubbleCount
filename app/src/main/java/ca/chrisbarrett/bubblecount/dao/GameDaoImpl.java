package ca.chrisbarrett.bubblecount.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ca.chrisbarrett.bubblecount.dao.model.Game;

/**
 * Created by chrisbarrett on 2016-07-18.
 */
public class GameDaoImpl extends DbContentProvider implements GameDao {

    private static final String TAG = "GameDaoImpl";
    private static final String SELECT_BY_ID = Schema.GameTable._ID + " = ? ";

    /**
     * Default Constructor
     *
     * @param db
     */
    public GameDaoImpl (SQLiteDatabase db) {
        super(db);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Game cursorToEntity (Cursor cursor) {
        Log.d(TAG, "cursorToEntity called.");
        Game game = new Game();
        if (cursor != null) {
            game.setId(cursor.getLong(cursor.getColumnIndex(Schema.GameTable._ID)));
            game.setClassPathName(cursor.getString(cursor.getColumnIndex(Schema.GameTable.COLUMN_CLASSPATH_NAME)));
            game.setDisplayName(cursor.getString(cursor.getColumnIndex(Schema.GameTable.COLUMN_DISPLAY_NAME)));
            game.setMinimumAge(cursor.getInt(cursor.getColumnIndex(Schema.GameTable.COLUMN_MINIMUM_AGE)));
        }
        Log.d(TAG, "Retrieved Game:" + game.toString());
        return game;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Game selectGameById (long gameId) {
        Log.d(TAG, "selectGameById attempted to locate gameId: " + gameId);
        final String[] selectionArgs = new String[]{String.valueOf(gameId)};

        Game game = new Game();
        cursor = super.query(Schema.GameTable.TABLE_NAME,
                Schema.GameTable.COLUMNS,
                SELECT_BY_ID,
                selectionArgs,
                Schema.GameTable._ID
        );
        if (cursor != null && cursor.moveToFirst()) {
            game = cursorToEntity(cursor);
        }
        cursor.close();
        Log.d(TAG, "Returning: " + game.toString());
        return game;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Game> selectAllGames () {
        Log.d(TAG, "selectAllGames called.");
        final String selection = Schema.GameTable._ID + " <> ?";  // skip the "Random" game
        final String[] selectionArgs = new String[]{String.valueOf(1)};
        List<Game> games = new ArrayList<>();
        cursor = super.query(
                Schema.GameTable.TABLE_NAME,
                Schema.GameTable.COLUMNS,
                selection,
                selectionArgs,
                Schema.GameTable._ID);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Game game = cursorToEntity(cursor);
                games.add(game);

            } while (cursor.moveToNext());
            cursor.close();
        }
        Log.d(TAG, "Returning " + games.size() + " Games");
        return games;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insertGame (Game... games) {
        Log.d(TAG, "insertGame attempting to insert " + games.length + " Games.");
        int insertions = 0;
        for (Game game : games) {
            setContentValues(game);
            try {
                long id = super.insert(Schema.GameTable.TABLE_NAME, getContentValues());
                Log.d(TAG, "Inserted Game into id: " + id);
                if (id > 0) {
                    ++insertions;
                }
            } catch (SQLiteConstraintException e) {
                Log.w("Database", e.getMessage());
            }
        }
        return games.length == insertions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateGame (Game game) {
        Log.d(TAG, "updateGame updating: " + game.toString());
        final String[] selectionArgs = new String[]{String.valueOf(game.getId())};
        setContentValues(game);
        try {
            return super.update(
                    Schema.GameTable.TABLE_NAME,
                    getContentValues(),
                    SELECT_BY_ID,
                    selectionArgs) > 0;
        } catch (SQLiteConstraintException e) {
            Log.w("Database", e.getMessage());
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteGameById (long gameId) {
        Log.d(TAG, "deleteGameById attempted to locate gameId: " + gameId);
        final String[] selectionArgs = new String[]{String.valueOf(gameId)};
        try {
            return super.delete(
                    Schema.GameTable.TABLE_NAME,
                    SELECT_BY_ID,
                    selectionArgs) > 0;
        } catch (SQLiteConstraintException e) {
            Log.w("Database", e.getMessage());
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Cursor getCursorForAdapter (int entriesSelector) {
        if (USER_ENTRIES == entriesSelector) {
            return super.query(Schema.GameTable.TABLE_NAME,
                    new String[]{Schema.GameTable._ID,
                            Schema.GameTable.COLUMN_DISPLAY_NAME},
                    Schema.GameTable._ID + " <> ?",
                    new String[]{String.valueOf(1)},
                    null);
        }

        // If something other thank USER_ENTRIES is provided, default to ALL_ENTRIES
        return super.query(Schema.GameTable.TABLE_NAME,
                new String[]{Schema.GameTable._ID,
                        Schema.GameTable.COLUMN_DISPLAY_NAME},
                null, null, null);
    }

    /**
     * Helper method to set the content values from a Game object
     *
     * @param game
     */
    protected void setContentValues (Game game) {
        initialValues = new ContentValues();
        initialValues.put(Schema.GameTable.COLUMN_DISPLAY_NAME, game.getDisplayName());
        initialValues.put(Schema.GameTable.COLUMN_CLASSPATH_NAME, game.getClassPathName());
        initialValues.put(Schema.GameTable.COLUMN_MINIMUM_AGE, game.getMinimumAge());
    }
}
