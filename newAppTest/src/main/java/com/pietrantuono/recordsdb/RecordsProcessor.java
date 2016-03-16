package com.pietrantuono.recordsdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.pietrantuono.recordsdb.RecordsContract;
import com.pietrantuono.recordsdb.OldRecordsHelper;

import java.util.ArrayList;
import java.util.List;

import server.pojos.records.Readings;
import server.pojos.records.S0;
import server.pojos.records.S1;
import server.pojos.records.S2;
import server.pojos.records.Sensors;
import server.pojos.records.Test;
import server.pojos.records.TestRecord;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class RecordsProcessor {

    public static long saveRecord(Context context, TestRecord testRecord, SQLiteOpenHelper helper) {
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
        values.put(RecordsContract.TestRecords.UPLOADED, 0);

        long recordId = -1;
        recordId = helper.getWritableDatabase().insert(RecordsContract.TestRecords.TABLE, RecordsContract.TestRecords.BT_ADDR, values);

        Readings readings = testRecord.getReadings();

        Sensors sensors = readings.getSensors();
        values = new ContentValues();
        values.put(RecordsContract.Sensors.COL_READINGS, recordId);
        long sensorsId = helper.getWritableDatabase().insert(RecordsContract.Sensors.TABLE, RecordsContract.Sensors.S0, values);
        if (sensors != null) {
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
                helper.getWritableDatabase().insert(RecordsContract.SingleS0.TABLE, RecordsContract.SingleS0.ERROR_CODE, values);
            }

            length = sensors.getS1().getIDTest().size();
            for (int i = 0; i < length; i++) {
                values = new ContentValues();
                values.put(RecordsContract.SingleS1.AVG, sensors.getS1().getAvg().get(i));
                values.put(RecordsContract.SingleS1.IDTEST, sensors.getS1().getIDTest().get(i));
                values.put(RecordsContract.SingleS1.MIN, sensors.getS1().getMin().get(i));
                values.put(RecordsContract.SingleS1.MAX, sensors.getS1().getMax().get(i));
                values.put(RecordsContract.SingleS1.ERROR_CODE, sensors.getS1().getErrorCodes().get(i));
                values.put(RecordsContract.SingleS1.RESULT, sensors.getS1().getResult().get(i));
                values.put(RecordsContract.SingleS1.S1, sensorsId);
                helper.getWritableDatabase().insert(RecordsContract.SingleS1.TABLE, RecordsContract.SingleS1.ERROR_CODE, values);
            }

            length = sensors.getS1().getIDTest().size();
            for (int i = 0; i < length; i++) {
                values = new ContentValues();
                values.put(RecordsContract.SingleS2.AVG, sensors.getS2().getAvg().get(i));
                values.put(RecordsContract.SingleS2.IDTEST, sensors.getS2().getIDTest().get(i));
                values.put(RecordsContract.SingleS2.MIN, sensors.getS2().getMin().get(i));
                values.put(RecordsContract.SingleS2.MAX, sensors.getS2().getMax().get(i));
                values.put(RecordsContract.SingleS2.ERROR_CODE, sensors.getS2().getErrorCodes().get(i));
                values.put(RecordsContract.SingleS2.RESULT, sensors.getS2().getResult().get(i));
                values.put(RecordsContract.SingleS2.S2, sensorsId);
                helper.getWritableDatabase().insert(RecordsContract.SingleS2.TABLE, RecordsContract.SingleS2.ERROR_CODE, values);
            }
        }
        Test test = testRecord.getReadings().getTest();
        if (test != null) {
            int length = test.getIDTest().size();
            for (int i = 0; i < length; i++) {
                values = new ContentValues();
                values.put(RecordsContract.SingleTest.RESULT, test.getResult().get(i));
                values.put(RecordsContract.SingleTest.ERRORCODE, test.getErrorCode().get(i));
                values.put(RecordsContract.SingleTest.IDTEST, test.getIDTest().get(i));
                values.put(RecordsContract.SingleTest.VALUE, test.getValue().get(i));
                values.put(RecordsContract.SingleTest.TEST, recordId);
                helper.getWritableDatabase().insert(RecordsContract.SingleTest.TABLE, RecordsContract.SingleTest.ERRORCODE, values);
            }
        }
        return recordId;
    }


    public static List<TestRecord> reconstructRecords(Context context, Cursor testRecordCursor, SQLiteOpenHelper helper) {
        List<TestRecord> records = new ArrayList<>();
        while (testRecordCursor.moveToNext()) {
            TestRecord testRecord = new TestRecord();
            TestRecord record = new TestRecord();
            record.setBarcode(testRecordCursor.getLong(testRecordCursor.getColumnIndexOrThrow(RecordsContract.TestRecords.BARCODE)));
            record.setDuration(testRecordCursor.getString(testRecordCursor.getColumnIndexOrThrow(RecordsContract.TestRecords.DURATION)));
            record.setFixtureNo(testRecordCursor.getString(testRecordCursor.getColumnIndexOrThrow(RecordsContract.TestRecords.FIXTURE_N)));
            record.setFWVer(testRecordCursor.getString(testRecordCursor.getColumnIndexOrThrow(RecordsContract.TestRecords.FMWVER)));
            record.setJobNo(testRecordCursor.getLong(testRecordCursor.getColumnIndexOrThrow(RecordsContract.TestRecords.JOB_NO)));
            record.setModel(testRecordCursor.getLong(testRecordCursor.getColumnIndexOrThrow(RecordsContract.TestRecords.MODEL)));
            record.setResult(testRecordCursor.getLong(testRecordCursor.getColumnIndexOrThrow(RecordsContract.TestRecords.RESULT)));
            record.setSerial(testRecordCursor.getString(testRecordCursor.getColumnIndexOrThrow(RecordsContract.TestRecords.SERIAL)));
            record.setStartedAt(testRecordCursor.getString(testRecordCursor.getColumnIndexOrThrow(RecordsContract.TestRecords.STARTED)));
            record.setBT_Addr(testRecordCursor.getString(testRecordCursor.getColumnIndexOrThrow(RecordsContract.TestRecords.BT_ADDR)));
            record.setID(testRecordCursor.getLong(testRecordCursor.getColumnIndexOrThrow(RecordsContract.TestRecords.ID)));
            long recordId = testRecordCursor.getLong(testRecordCursor.getColumnIndexOrThrow(RecordsContract.TestRecords.ID));
            Readings readings = new Readings();

            Sensors sensors = new Sensors();
            String selection = RecordsContract.Sensors.COL_READINGS + "=?";
            String[] selectionArgs = new String[]{"" + recordId};
            Cursor sensorsCursor = helper.getWritableDatabase().query(RecordsContract.Sensors.TABLE, null, selection, selectionArgs, null, null, null);

            if (sensorsCursor.moveToFirst()) {
                long sensorsId = sensorsCursor.getLong(sensorsCursor.getColumnIndexOrThrow(RecordsContract.Sensors.ID));
                sensorsCursor.close();
                S0 s0 = new S0();
                selection = RecordsContract.SingleS0.S0 + " = ?";
                selectionArgs = new String[]{"" + sensorsId};
                Cursor s0Cursor = helper.getWritableDatabase().query(RecordsContract.SingleS0.TABLE, null, selection, selectionArgs, null, null, null);
                if (s0Cursor.moveToFirst()) {
                    List<Long> errorcodes = new ArrayList<>();
                    List<Long> avgs = new ArrayList<>();
                    List<Long> idtests = new ArrayList<>();
                    List<Long> maxs = new ArrayList<>();
                    List<Long> mins = new ArrayList<>();
                    List<Long> results = new ArrayList<>();

                    do {
                        errorcodes.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow(RecordsContract.SingleS0.ERROR_CODE)));
                        avgs.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow(RecordsContract.SingleS0.AVG)));
                        idtests.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow(RecordsContract.SingleS0.IDTEST)));
                        maxs.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow(RecordsContract.SingleS0.MAX)));
                        mins.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow(RecordsContract.SingleS0.MIN)));
                        results.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow(RecordsContract.SingleS0.RESULT)));
                    }

                    while (s0Cursor.moveToNext());

                    s0.setResult(results);
                    s0.setMin(mins);
                    s0.setMax(maxs);
                    s0.setIDTest(idtests);
                    s0.setAvg(avgs);
                    s0.setErrorCodes(errorcodes);
                    sensors.setS0(s0);
                    s0Cursor.close();
                }

                S1 s1 = new S1();

                selection = RecordsContract.SingleS1.S1 + " = ?";
                selectionArgs = new String[]{"" + sensorsId};
                Cursor s1Cursor = helper.getWritableDatabase().query(RecordsContract.SingleS1.TABLE, null, selection, selectionArgs, null, null, null);
                if (s1Cursor.moveToFirst()) {
                    List<Long> errorcodes = new ArrayList<>();
                    List<Long> avgs = new ArrayList<>();
                    List<Long> idtests = new ArrayList<>();
                    List<Long> maxs = new ArrayList<>();
                    List<Long> mins = new ArrayList<>();
                    List<Long> results = new ArrayList<>();

                    do {
                        errorcodes.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow(RecordsContract.SingleS1.ERROR_CODE)));
                        avgs.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow(RecordsContract.SingleS1.AVG)));
                        idtests.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow(RecordsContract.SingleS1.IDTEST)));
                        maxs.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow(RecordsContract.SingleS1.MAX)));
                        mins.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow(RecordsContract.SingleS1.MIN)));
                        results.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow(RecordsContract.SingleS1.RESULT)));
                    }

                    while (s1Cursor.moveToNext());

                    s1.setResult(results);
                    s1.setMin(mins);
                    s1.setMax(maxs);
                    s1.setIDTest(idtests);
                    s1.setAvg(avgs);
                    s1.setErrorCodes(errorcodes);
                    sensors.setS1(s1);
                    s1Cursor.close();
                }

                sensors.setS1(s1);

                S2 s2 = new S2();

                selection = RecordsContract.SingleS2.S2 + " = ?";
                selectionArgs = new String[]{"" + sensorsId};
                Cursor s2Cursor = helper.getWritableDatabase().query(RecordsContract.SingleS2.TABLE, null, selection, selectionArgs, null, null, null);
                if (s2Cursor.moveToFirst()) {
                    List<Long> errorcodes = new ArrayList<>();
                    List<Long> avgs = new ArrayList<>();
                    List<Long> idtests = new ArrayList<>();
                    List<Long> maxs = new ArrayList<>();
                    List<Long> mins = new ArrayList<>();
                    List<Long> results = new ArrayList<>();

                    do {
                        errorcodes.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow(RecordsContract.SingleS2.ERROR_CODE)));
                        avgs.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow(RecordsContract.SingleS2.AVG)));
                        idtests.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow(RecordsContract.SingleS2.IDTEST)));
                        maxs.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow(RecordsContract.SingleS2.MAX)));
                        mins.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow(RecordsContract.SingleS2.MIN)));
                        results.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow(RecordsContract.SingleS2.RESULT)));
                    }

                    while (s2Cursor.moveToNext());

                    s2.setResult(results);
                    s2.setMin(mins);
                    s2.setMax(maxs);
                    s2.setIDTest(idtests);
                    s2.setAvg(avgs);
                    s2.setErrorCodes(errorcodes);
                    sensors.setS2(s2);
                    s2Cursor.close();
                }

                sensors.setS2(s2);

                readings.setSensors(sensors);
            }

            Test test = new Test();
            selection = RecordsContract.SingleTest.TEST + "=?";
            selectionArgs = new String[]{"" + recordId};
            Cursor singleTestCursor = helper.getWritableDatabase().query(RecordsContract.SingleTest.TABLE, null, selection, selectionArgs, null, null, null);

            if (singleTestCursor.moveToNext()) {
                List<Long> errorcodes = new ArrayList<>();
                List<Long> testsIds = new ArrayList<>();
                List<Long> results = new ArrayList<>();
                List<Double> values = new ArrayList<>();

                do {
                    errorcodes.add(singleTestCursor.getLong(singleTestCursor.getColumnIndexOrThrow(RecordsContract.SingleTest.ERRORCODE)));
                    testsIds.add(singleTestCursor.getLong(singleTestCursor.getColumnIndexOrThrow(RecordsContract.SingleTest.IDTEST)));
                    results.add(singleTestCursor.getLong(singleTestCursor.getColumnIndexOrThrow(RecordsContract.SingleTest.RESULT)));
                    values.add(singleTestCursor.getDouble(singleTestCursor.getColumnIndexOrThrow(RecordsContract.SingleTest.VALUE)));

                }
                while (singleTestCursor.moveToNext());

                test.setResult(results);
                test.setIDTest(testsIds);
                test.setValue(values);
                test.setErrorCode(errorcodes);
            }
            singleTestCursor.close();

            readings.setTest(test);

            record.setReadings(readings);

            records.add(record);
        }
        return records;
    }
}
