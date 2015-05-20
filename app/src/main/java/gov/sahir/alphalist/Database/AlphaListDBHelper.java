package gov.sahir.alphalist.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sahir on 5/12/2015.
 */
public class AlphaListDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alphalist.db";
    private static final int DATABASE_VERSION = 1;

    public AlphaListDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        ListTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        ListTable.onUpgrade(database, oldVersion, newVersion);
    }
}
