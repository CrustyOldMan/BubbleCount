package ca.chrisbarrett.bubblecount.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.chrisbarrett.bubblecount.dao.model.Player;

/**
 * Created by chrisbarrett on 2016-07-18.
 */
public class PlayerDaoImpl extends DbContentProvider implements PlayerDao {

    public static final int ALL_ENTRIES = 1;
    public static final int USER_ENTRIES = 0;

    private static final String TAG = "PlayerDaoImpl";
    private static final String SELECT_BY_ID = Schema.PlayerTable._ID + " = ? ";

    /**
     * Default Constructor
     *
     * @param db
     */
    public PlayerDaoImpl (SQLiteDatabase db) {
        super(db);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Player cursorToEntity (Cursor cursor) {
        Log.d(TAG, "cursorToEntity called.");
        Player player = new Player();
        if (cursor != null) {
            player.setId(cursor.getLong(cursor.getColumnIndex(Schema.PlayerTable._ID)));
            player.setName(cursor.getString(cursor.getColumnIndex(Schema.PlayerTable.COLUMN_NAME)));
            player.setYearOfBirth(cursor.getInt(cursor.getColumnIndex(Schema.PlayerTable.COLUMN_YEAR_OF_BIRTH)));
            player.setCreatedOn(new Date(cursor.getLong(cursor.getColumnIndex(Schema.PlayerTable.COLUMN_CREATED_ON))));
            player.setSyncedOn(new Date(cursor.getLong(cursor.getColumnIndex(Schema.PlayerTable.COLUMN_SYNCED_ON))));
        }
        Log.d(TAG, "Retrieved Game:" + player.toString());
        return player;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player selectPlayerById (long playerId) {
        Log.d(TAG, "selectPlayerById attempted to locate playerId: " + playerId);
        final String[] selectionArgs = new String[]{String.valueOf(playerId)};

        Player player = new Player();
        cursor = super.query(Schema.PlayerTable.TABLE_NAME,
                Schema.PlayerTable.COLUMNS,
                SELECT_BY_ID,
                selectionArgs,
                Schema.PlayerTable._ID
        );
        if (cursor != null && cursor.moveToFirst()) {
            player = cursorToEntity(cursor);
        }
        cursor.close();
        Log.d(TAG, "Returning: " + player.toString());
        return player;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Player> selectAllPlayers () {
        Log.d(TAG, "selectAllPlayers called.");
        final String selection = Schema.PlayerTable._ID + " <> ?";  // skip the "None" player
        final String[] selectionArgs = new String[]{String.valueOf(1)};
        List<Player> players = new ArrayList<>();
        cursor = super.query(
                Schema.PlayerTable.TABLE_NAME,
                Schema.PlayerTable.COLUMNS,
                selection,
                selectionArgs,
                Schema.PlayerTable._ID);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Player player = cursorToEntity(cursor);
                players.add(player);
            } while (cursor.moveToNext());
            cursor.close();
        }
        Log.d(TAG, "Returning " + players.size() + " Players");
        return players;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insertPlayer (Player... players) {
        Log.d(TAG, "insertGame attempting to insert " + players.length + " Players.");
        int insertions = 0;
        for (Player player : players) {
            setContentValues(player);
            try {
                long id = super.insert(Schema.PlayerTable.TABLE_NAME, getContentValues());
                Log.d(TAG, "Inserted Player into id: " + id);
                if (id > 0) {
                    ++insertions;
                }
            } catch (SQLiteConstraintException e) {
                Log.w("Database", e.getMessage());
            }
        }
        return players.length == insertions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updatePlayer (Player player) {
        Log.d(TAG, "updatePlayer updating: " + player.toString());
        final String[] selectionArgs = new String[]{String.valueOf(player.getId())};
        setContentValues(player);
        try {
            return super.update(
                    Schema.PlayerTable.TABLE_NAME,
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
    public boolean deletePlayerById (long playerId) {
        Log.d(TAG, "deletePlayerById attempted to locate gameId: " + playerId);
        final String[] selectionArgs = new String[]{String.valueOf(playerId)};
        try {
            return super.delete(
                    Schema.PlayerTable.TABLE_NAME,
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
            return super.query(Schema.PlayerTable.TABLE_NAME,
                    new String[]{Schema.PlayerTable._ID,
                            Schema.PlayerTable.COLUMN_NAME},
                    Schema.PlayerTable._ID + " <> ?",
                    new String[]{String.valueOf(1)},
                    null);
        }

        // If something other thank USER_ENTRIES is provided, default to ALL_ENTRIES
        return super.query(Schema.PlayerTable.TABLE_NAME,
                new String[]{Schema.PlayerTable._ID,
                        Schema.PlayerTable.COLUMN_NAME},
                null, null, null);
    }

    /**
     * Helper method to set the content values from a Player object
     *
     * @param player
     */
    protected void setContentValues (Player player) {
        initialValues = new ContentValues();
        initialValues.put(Schema.PlayerTable.COLUMN_NAME, player.getName());
        initialValues.put(Schema.PlayerTable.COLUMN_YEAR_OF_BIRTH, player.getYearOfBirth());
        initialValues.put(Schema.PlayerTable.COLUMN_CREATED_ON, player.getCreatedOn().getTime());
        if (player.getSyncedOn() == null) {
            initialValues.put(Schema.PlayerTable.COLUMN_SYNCED_ON, 0);
        } else {
            initialValues.put(Schema.PlayerTable.COLUMN_SYNCED_ON, player.getSyncedOn().getTime());
        }
    }
}

