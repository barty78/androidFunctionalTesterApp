package com.pietrantuono.recordsdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Iterator;

import server.pojos.records.Readings;
import server.pojos.records.Sensors;
import server.pojos.records.Test;
import server.pojos.records.TestRecord;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class NewRecordsSQLiteOpenHelper extends SQLiteOpenHelper {
    private final Context context;
    private static final String DB_NAME = "records";
    private static final int DB_VERSION = 1;
    private static NewRecordsSQLiteOpenHelper newRecordsSQLiteOpenHelper;

    private NewRecordsSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public synchronized static NewRecordsSQLiteOpenHelper getInstance(Context context) {
        if (newRecordsSQLiteOpenHelper == null) {
            newRecordsSQLiteOpenHelper = new NewRecordsSQLiteOpenHelper(context);
        }
        return newRecordsSQLiteOpenHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RecordsContract.TestRecords.CREATE_TABLE);
        db.execSQL(RecordsContract.Readings.CREATE_TABLE);
        db.execSQL(RecordsContract.Sensors.CREATE_TABLE);
        db.execSQL(RecordsContract.Sensors0.CREATE_TABLE);
        db.execSQL(RecordsContract.SingleS0.CREATE_TABLE);
        db.execSQL(RecordsContract.SingleS1.CREATE_TABLE);
        db.execSQL(RecordsContract.SingleS2.CREATE_TABLE);
        db.execSQL(RecordsContract.SingleTest.CREATE_TABLE);
        migrateOldData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void migrateOldData(SQLiteDatabase db) {
        OldRecordsHelper oldRecorsdHelper = OldRecordsHelper.get(context);
        Cursor oldRecords = oldRecorsdHelper.getWritableDatabase().query(RecordsContract.TestRecords.TABLE, null, null, null, null, null, null);
        ArrayList<TestRecord> testRecords = (ArrayList<TestRecord>) RecordsProcessor.reconstructRecords(context, oldRecords, oldRecorsdHelper);
        Iterator<TestRecord> iterator = testRecords.iterator();
        while (iterator.hasNext()) {
            saveRecord(context, iterator.next(), db);
        }
    }

    private long saveRecord(Context context, TestRecord testRecord, SQLiteDatabase db) {
        TestRecord record = new TestRecord();
        ContentValues values = new ContentValues();
        values.put(RecordsContract.TestRecords.BARCODE, testRecord.getBarcode());
        values.put(RecordsContract.TestRecords.DURATION, testRecord.getDuration());
        values.put(RecordsContract.TestRecords.FIXTURE_N, testRecord.getFixtureNo());
        values.put(RecordsContract.TestRecords.FMWVER, testRecord.getFWVer());
        values.put(RecordsContract.TestRecords.JOB_NO, testRecord.getJobNo());
        values.put(RecordsContract.TestRecords.MODEL, testRecord.getModel());
        values.put(RecordsContract.TestRecords.RESULT, testRecord.getResult());
        values.put(RecordsContract.TestRecords.SERIAL, testRecord.getSerial());
        values.put(RecordsContract.TestRecords.STARTED, testRecord.getStartedAt());
        values.put(RecordsContract.TestRecords.BT_ADDR, testRecord.getBT_Addr());

        long recordId = -1;
        recordId = db.insert(RecordsContract.TestRecords.TABLE, RecordsContract.TestRecords.BT_ADDR, values);

        Readings readings = testRecord.getReadings();
        if (readings != null) {
            Sensors sensors = readings.getSensors();
            if (sensors != null) {
                values = new ContentValues();
                values.put(RecordsContract.Sensors.COL_READINGS, recordId);
                long sensorsId = db.insert(RecordsContract.Sensors.TABLE, RecordsContract.Sensors.S0, values);
                if (sensors.getS0() != null && sensors.getS0().getIDTest() != null) {
                    int length = sensors.getS0().getIDTest().size();
                    for (int i = 0; i < length; i++) {
                        values = new ContentValues();
                        values.put(RecordsContract.SingleS0.AVG, sensors.getS0().getAvg().get(i));
                        values.put(RecordsContract.SingleS0.IDTEST, sensors.getS0().getIDTest().get(i));
                        values.put(RecordsContract.SingleS0.MIN, sensors.getS0().getMin().get(i));
                        values.put(RecordsContract.SingleS0.MAX, sensors.getS0().getMax().get(i));
                        values.put(RecordsContract.SingleS0.ERROR_CODE, sensors.getS0().getErrorCodes().get(i));
                        values.put(RecordsContract.SingleS0.RESULT, sensors.getS0().getResult().get(i));
                        values.put(RecordsContract.SingleS0.S0, sensorsId);
                        db.insert(RecordsContract.SingleS0.TABLE, RecordsContract.SingleS0.ERROR_CODE, values);
                    }
                }
                if (sensors.getS1() != null && sensors.getS1().getIDTest() != null) {
                    int length = sensors.getS1().getIDTest().size();
                    for (int i = 0; i < length; i++) {
                        values = new ContentValues();
                        values.put(RecordsContract.SingleS1.AVG, sensors.getS1().getAvg().get(i));
                        values.put(RecordsContract.SingleS1.IDTEST, sensors.getS1().getIDTest().get(i));
                        values.put(RecordsContract.SingleS1.MIN, sensors.getS1().getMin().get(i));
                        values.put(RecordsContract.SingleS1.MAX, sensors.getS1().getMax().get(i));
                        values.put(RecordsContract.SingleS1.ERROR_CODE, sensors.getS1().getErrorCodes().get(i));
                        values.put(RecordsContract.SingleS1.RESULT, sensors.getS1().getResult().get(i));
                        values.put(RecordsContract.SingleS1.S1, sensorsId);
                        db.insert(RecordsContract.SingleS1.TABLE, RecordsContract.SingleS1.ERROR_CODE, values);
                    }
                }
                if (sensors.getS2() != null && sensors.getS2().getIDTest() != null) {
                    int length = sensors.getS1().getIDTest().size();
                    for (int i = 0; i < length; i++) {
                        values = new ContentValues();
                        values.put(RecordsContract.SingleS2.AVG, sensors.getS2().getAvg().get(i));
                        values.put(RecordsContract.SingleS2.IDTEST, sensors.getS2().getIDTest().get(i));
                        values.put(RecordsContract.SingleS2.MIN, sensors.getS2().getMin().get(i));
                        values.put(RecordsContract.SingleS2.MAX, sensors.getS2().getMax().get(i));
                        values.put(RecordsContract.SingleS2.ERROR_CODE, sensors.getS2().getErrorCodes().get(i));
                        values.put(RecordsContract.SingleS2.RESULT, sensors.getS2().getResult().get(i));
                        values.put(RecordsContract.SingleS2.S2, sensorsId);
                        db.insert(RecordsContract.SingleS2.TABLE, RecordsContract.SingleS2.ERROR_CODE, values);
                    }
                }

                Test test = testRecord.getReadings().getTest();
                if (test != null && test.getIDTest() != null) {
                    int length = test.getIDTest().size();
                    for (int i = 0; i < length; i++) {
                        values = new ContentValues();
                        values.put(RecordsContract.SingleTest.RESULT, test.getResult().get(i));
                        values.put(RecordsContract.SingleTest.ERRORCODE, test.getErrorCode().get(i));
                        values.put(RecordsContract.SingleTest.IDTEST, test.getIDTest().get(i));
                        values.put(RecordsContract.SingleTest.VALUE, test.getValue().get(i));
                        values.put(RecordsContract.SingleTest.TEST, recordId);
                        db.insert(RecordsContract.SingleTest.TABLE, RecordsContract.SingleTest.ERRORCODE, values);
                    }
                }

            }
        }


        return recordId;
    }
}
