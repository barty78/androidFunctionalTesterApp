package com.pietrantuono.recordsdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class RecordsHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "com.pietrantuono.pericoach.newtestapp.containsmac.db";
    private static final int DB_VERSION = 5;
    private static RecordsHelper recordsHelper;

    private RecordsHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public synchronized static RecordsHelper get(Context context){
        if(recordsHelper==null){
            recordsHelper= new RecordsHelper(context);
        }
        return recordsHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
