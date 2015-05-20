package gov.sahir.alphalist.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Currency;

import gov.sahir.alphalist.Database.AlphaListDBHelper;
import gov.sahir.alphalist.Database.ListTable;

/**
 * Created by Sahir on 5/13/2015.
 */
public class ListContentProvider extends ContentProvider{

    private AlphaListDBHelper database;

    private static final String AUTHORITY = "gov.sahir.alphalist.provider";
    private static final String BASE_PATH = "todos";
    private static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/todos";
    private static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/todo";
    private static final String CONTENT_URI_PREFIX = "content://" + AUTHORITY + "/" + BASE_PATH + "/";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final int TODOS = 1;
    public static final int TODO_ID = 2;

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, TODOS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TODO_ID);
    }

    @Override
    public boolean onCreate() {
        database = new AlphaListDBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
        ListTable.validateColumns(columns);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ListTable.TABLE_LIST);

        switch(sURIMatcher.match(uri)) {
            case TODOS:
                break;
            case TODO_ID:
                queryBuilder.appendWhere(ListTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, columns, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = database.getWritableDatabase();
        long id = 0;

        switch(sURIMatcher.match(uri)) {
            case TODOS:
                id = db.insert(ListTable.TABLE_LIST, null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(CONTENT_URI_PREFIX + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = database.getWritableDatabase();
        int deletedRows = 0;

        switch(sURIMatcher.match(uri)) {
            case TODOS:
                deletedRows = db.delete(ListTable.TABLE_LIST, selection, selectionArgs);
                break;
            case TODO_ID:
                String id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection)) {
                    deletedRows = db.delete(ListTable.TABLE_LIST, ListTable.COLUMN_ID + "=" + id, null);
                }
                else {
                    deletedRows = db.delete(ListTable.TABLE_LIST, ListTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = database.getWritableDatabase();
        int updatedRows = 0;

        switch(sURIMatcher.match(uri)) {
            case TODOS:
                updatedRows = db.update(ListTable.TABLE_LIST, contentValues, selection, selectionArgs);
                break;
            case TODO_ID:
                String id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection)) {
                    updatedRows = db.update(ListTable.TABLE_LIST, contentValues, ListTable.COLUMN_ID + "=" + id, null);
                }
                else {
                    updatedRows = db.update(ListTable.TABLE_LIST, contentValues, ListTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }
}
