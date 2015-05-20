package gov.sahir.alphalist.Database;

import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Sahir on 5/12/2015.
 */
public class ListTable {

    // database columns
    public static final String TABLE_LIST = "list";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DONE = "done";

    // create table
    private static final String CREATE_DATABASE = "create table "
            + TABLE_LIST
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_DESCRIPTION + " text not null, "
            + COLUMN_DONE + " boolean not null check (" + COLUMN_DONE + " in (0,1) )"
            + ");";

    private static HashSet<String> VALID_COLUMNS;

    static {
        String[] validColumns = { TABLE_LIST, COLUMN_ID, COLUMN_DESCRIPTION, COLUMN_DONE };
        VALID_COLUMNS = new HashSet<String>(Arrays.asList(validColumns));
    }

    public static void onCreate( SQLiteDatabase database) {
        database.execSQL(CREATE_DATABASE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        String DROP_TABLE = "DROP TABLE IF EXISTS";
        database.execSQL(DROP_TABLE + " " + TABLE_LIST);
        onCreate(database);
    }

    public static void validateColumns(String[] columns) {
        if(columns != null) {
            if(!VALID_COLUMNS.containsAll(new HashSet<String>(Arrays.asList(columns))))
                throw new IllegalArgumentException("Unknown columns");
        }
    }
}
