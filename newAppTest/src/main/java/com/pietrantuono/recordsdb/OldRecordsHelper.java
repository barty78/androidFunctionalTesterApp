package com.pietrantuono.recordsdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
@Deprecated
public class OldRecordsHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "com.pietrantuono.pericoach.newtestapp.containsmac.db";
    private static final int DB_VERSION = 5;
    private static OldRecordsHelper oldRecordsHelper;

    private OldRecordsHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public synchronized static OldRecordsHelper get(Context context){
        if(oldRecordsHelper ==null){
            oldRecordsHelper = new OldRecordsHelper(context);
        }
        return oldRecordsHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
