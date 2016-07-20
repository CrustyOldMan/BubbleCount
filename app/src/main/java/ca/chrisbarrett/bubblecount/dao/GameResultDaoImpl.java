package ca.chrisbarrett.bubblecount.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.chrisbarrett.bubblecount.dao.model.GameResult;

/**
 * Created by chrisbarrett on 2016-07-18.
 */
public class GameResultDaoImpl extends DbContentProvider implements GameResultDao {

    public static final int ALL_ENTRIES = 1;
    public static final int USER_ENTRIES = 0;

    private static final String TAG = "GameResultDaoImpl";
    private static final String SELECT_BY_ID = Schema.GameResultTable._ID + " = ? ";

    /**
     * Default Constructor
     *
     * @param db
     */
    public GameResultDaoImpl (SQLiteDatabase db) {
        super(db);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected GameResult cursorToEntity (Cursor cursor) {
        Log.d(TAG, "cursorToEntity called.");
        GameResult gameResult = new GameResult();
        if (cursor != null) {
            gameResult.setId(cursor.getLong(cursor.getColumnIndex(Schema.GameResultTable._ID)));
            gameResult.setTimeResult(cursor.getInt(cursor.getColumnIndex(Schema.GameResultTable.COLUMN_TIME_RESULT)));
            gameResult.setGameId(cursor.getInt(cursor.getColumnIndex(Schema.GameResultTable.COLUMN_FK_GAME_ID)));
            gameResult.setPlayerId(cursor.getInt(cursor.getColumnIndex(Schema.GameResultTable.COLUMN_FK_PLAYER_ID)));
            gameResult.setCreatedOn(new Date(cursor.getLong(cursor.getColumnIndex(Schema.GameResultTable.COLUMN_CREATED_ON))));
            gameResult.setSyncedOn(new Date(cursor.getLong(cursor.getColumnIndex(Schema.GameResultTable.COLUMN_SYNCED_ON))));
        }
        Log.d(TAG, "Retrieved Game:" + gameResult.toString());
        return gameResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameResult selectGameResultById (long gameResultId) {
        Log.d(TAG, "selectGameResultById attempted to locate gameId: " + gameResultId);
        final String[] selectionArgs = new String[]{String.valueOf(gameResultId)};

        GameResult gameResult = new GameResult();
        cursor = super.query(Schema.GameResultTable.TABLE_NAME,
                Schema.GameResultTable.COLUMNS,
                SELECT_BY_ID,
                selectionArgs,
                Schema.GameResultTable._ID
        );
        if (cursor != null && cursor.moveToFirst()) {
            gameResult = cursorToEntity(cursor);
        }
        cursor.close();
        Log.d(TAG, "Returning: " + gameResult.toString());
        return gameResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GameResult> selectGameResultByPlayerId (long playerId) {
        Log.d(TAG, "selectGameResultByPlayerId called.");
        final String[] selectionArgs = new String[]{String.valueOf(playerId)};
        List<GameResult> gameResults = new ArrayList<>();
        cursor = super.query(
                Schema.GameResultTable.TABLE_NAME,
                Schema.GameResultTable.COLUMNS,
                Schema.GameResultTable.COLUMN_FK_PLAYER_ID + " = ? ",
                selectionArgs,
                Schema.GameResultTable._ID);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                GameResult gameResult = cursorToEntity(cursor);
                gameResults.add(gameResult);
            } while (cursor.moveToNext());
            cursor.close();
        }
        Log.d(TAG, "Returning " + gameResults.size() + " GameResults.");
        return gameResults;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GameResult> selectGameResultByGameId (long gameId) {
        Log.d(TAG, "selectGameResultByGameId called.");
        final String[] selectionArgs = new String[]{String.valueOf(gameId)};
        List<GameResult> gameResults = new ArrayList<>();
        cursor = super.query(
                Schema.GameResultTable.TABLE_NAME,
                Schema.GameResultTable.COLUMNS,
                Schema.GameResultTable.COLUMN_FK_GAME_ID + " = ? ",
                selectionArgs,
                Schema.GameResultTable._ID);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                GameResult gameResult = cursorToEntity(cursor);
                gameResults.add(gameResult);
            } while (cursor.moveToNext());
            cursor.close();
        }
        Log.d(TAG, "Returning " + gameResults.size() + " GameResults.");
        return gameResults;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GameResult> selectAllGameResults () {
        Log.d(TAG, "selectAllGameResults called.");
        List<GameResult> gameResults = new ArrayList<>();
        cursor = super.query(
                Schema.GameResultTable.TABLE_NAME,
                Schema.GameResultTable.COLUMNS,
                null, null,
                Schema.GameResultTable._ID);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                GameResult gameResult = cursorToEntity(cursor);
                gameResults.add(gameResult);
            } while (cursor.moveToNext());
            cursor.close();
        }
        Log.d(TAG, "Returning " + gameResults.size() + " GameResults.");
        return gameResults;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insertGameResult (GameResult... gameResults) {
        Log.d(TAG, "insertGame attempting to insert " + gameResults.length + " GameResult.");
        int insertions = 0;
        for (GameResult gameResult : gameResults) {
            setContentValues(gameResult);
            try {
                long id = super.insert(Schema.GameResultTable.TABLE_NAME, getContentValues());
                Log.d(TAG, "Inserted GameResult into id: " + id);
                if (id > 0) {
                    ++insertions;
                }
            } catch (SQLiteConstraintException e) {
                Log.w("Database", e.getMessage());
            }
        }
        return gameResults.length == insertions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateGameResult (GameResult gameResult) {
        Log.d(TAG, "updateGameResult updating: " + gameResult.toString());
        final String[] selectionArgs = new String[]{String.valueOf(gameResult.getId())};
        setContentValues(gameResult);
        try {
            return super.update(
                    Schema.GameResultTable.TABLE_NAME,
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
    public boolean deleteGameResultById (long gameResultId) {
        Log.d(TAG, "deleteGameResultById attempted to locate gameId: " + gameResultId);
        final String[] selectionArgs = new String[]{String.valueOf(gameResultId)};
        try {
            return super.delete(
                    Schema.GameResultTable.TABLE_NAME,
                    SELECT_BY_ID,
                    selectionArgs) > 0;
        } catch (SQLiteConstraintException e) {
            Log.w("Database", e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to set the content values from a GameResult object
     *
     * @param gameResult
     */
    protected void setContentValues (GameResult gameResult) {
        initialValues = new ContentValues();
        initialValues.put(Schema.GameResultTable.COLUMN_CREATED_ON, gameResult.getCreatedOn().getTime());
        initialValues.put(Schema.GameResultTable.COLUMN_FK_PLAYER_ID, gameResult.getPlayerId());
        initialValues.put(Schema.GameResultTable.COLUMN_FK_GAME_ID, gameResult.getGameId());
        initialValues.put(Schema.GameResultTable.COLUMN_TIME_RESULT, gameResult.getTimeResult());
        if (gameResult.getSyncedOn() == null) {
            initialValues.put(Schema.GameResultTable.COLUMN_SYNCED_ON, 0);
        } else {
            initialValues.put(Schema.GameResultTable.COLUMN_SYNCED_ON, gameResult.getSyncedOn().getTime());
        }
    }
}
