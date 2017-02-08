package com.pietrantuono.sequencedb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class SequenceSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sequence_database";
    private static final int DATABASE_VERSION = 2;
    @SuppressWarnings("unused")
    private final String TAG = getClass().getSimpleName();
    private static SequenceSQLiteOpenHelper instance;

    public static synchronized SequenceSQLiteOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SequenceSQLiteOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    private SequenceSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SequenceContracts.Records.CREATE_TABLES);
        db.execSQL(SequenceContracts.Tests.CREATE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading Sequence DB to version " + DATABASE_VERSION);
        db.execSQL(SequenceContracts.Records.UPGRADE_ADD_APPVERSION);
    }


}
