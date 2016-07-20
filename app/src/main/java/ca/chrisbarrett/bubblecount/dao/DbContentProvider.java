package ca.chrisbarrett.bubblecount.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Abstraction of the CRUD that all models must implement
 *
 * @author Chris Barrett
 * @see
 * @since Jul 10, 2016
 */
public abstract class DbContentProvider {


    protected Cursor cursor;
    protected ContentValues initialValues;
    private SQLiteDatabase db;

    public DbContentProvider (SQLiteDatabase db) {
        this.db = db;
    }

    public int delete (String tableName, String selection,
                       String[] selectionArgs) {
        return db.delete(tableName, selection, selectionArgs);
    }

    public long insert (String tableName, ContentValues values) {
        return db.insert(tableName, null, values);
    }

    protected abstract <T> T cursorToEntity (Cursor cursor);

    public Cursor query (String tableName, String[] columns,
                         String selection, String[] selectionArgs, String sortOrder) {

        final Cursor cursor = db.query(tableName, columns,
                selection, selectionArgs, null, null, sortOrder);

        return cursor;
    }

    public Cursor query (String tableName, String[] columns,
                         String selection, String[] selectionArgs, String sortOrder,
                         String limit) {

        return db.query(tableName, columns, selection,
                selectionArgs, null, null, sortOrder, limit);
    }

    public Cursor query (String tableName, String[] columns,
                         String selection, String[] selectionArgs, String groupBy,
                         String having, String orderBy, String limit) {

        return db.query(tableName, columns, selection,
                selectionArgs, groupBy, having, orderBy, limit);
    }

    public int update (String tableName, ContentValues values,
                       String selection, String[] selectionArgs) {
        return db.update(tableName, values, selection,
                selectionArgs);
    }

    public Cursor rawQuery (String sql, String[] selectionArgs) {
        return db.rawQuery(sql, selectionArgs);
    }

    public ContentValues getContentValues () {
        return initialValues;
    }

}

