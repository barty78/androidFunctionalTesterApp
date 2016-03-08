package com.pietrantuono.activities.uihelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.pietrantuono.recordsdb.RecordsContract;
import com.pietrantuono.recordsdb.RecordsHelper;

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
public class SaveRecords {

    public static List<TestRecord> reconstrucRecords(Context context, Cursor testRecordCursor) {
        List<TestRecord> records = new ArrayList<>();
        RecordsHelper recordsHelper = RecordsHelper.get(context);
        while (testRecordCursor.moveToNext()) {
            TestRecord testRecord = new TestRecord();
            TestRecord record = new TestRecord();
            ContentValues values = new ContentValues();
            values.put("Barcode", testRecord.getBarcode());
            values.put("Duration", testRecord.getDuration());
            values.put("FixtureNo", testRecord.getFixtureNo());
            values.put("FWVer", testRecord.getFWVer());
            values.put("JobNo", testRecord.getJobNo());
            values.put("Model", testRecord.getModel());
            values.put("Result", testRecord.getResult());
            values.put("Serial", testRecord.getSerial());
            values.put("StartedAt", testRecord.getStartedAt());
            values.put("BT_Addr", testRecord.getBT_Addr());

            long recordId = recordsHelper.getWritableDatabase().insert(RecordsContract.TestRecords.TABLE, "BT_Addr", values);

            Readings readings = testRecord.getReadings();

            Sensors sensors = readings.getSensors();
            values = new ContentValues();
            values.put("Readings",recordId);
            long sensorsId = recordsHelper.getWritableDatabase().insert(RecordsContract.Sensors.TABLE, "S0", values);
            int length = sensors.getS0().getIDTest().size();
            for(int i=0;i<length;i++){
                values = new ContentValues();
                values.put("Avg",sensors.getS0().getAvg().get(i));
                values.put("IDTest", sensors.getS0().getIDTest().get(i));
                values.put("Min", sensors.getS0().getMin().get(i));
                values.put("Max", sensors.getS0().getMax().get(i));
                values.put("ErrorCode", sensors.getS0().getErrorCodes().get(i));
                values.put("Result",sensors.getS0().getResult().get(i));
                recordsHelper.getWritableDatabase().insert(RecordsContract.SingleS0.TABLE, "ErrorCode", values);
            }

            length = sensors.getS1().getIDTest().size();
            for(int i=0;i<length;i++){
                values = new ContentValues();
                values.put("Avg",sensors.getS1().getAvg().get(i));
                values.put("IDTest", sensors.getS1().getIDTest().get(i));
                values.put("Min", sensors.getS1().getMin().get(i));
                values.put("Max", sensors.getS1().getMax().get(i));
                values.put("ErrorCode", sensors.getS1().getErrorCodes().get(i));
                values.put("Result",sensors.getS1().getResult().get(i));
                recordsHelper.getWritableDatabase().insert(RecordsContract.SingleS1.TABLE, "ErrorCode", values);
            }

            length = sensors.getS1().getIDTest().size();
            for(int i=0;i<length;i++){
                values = new ContentValues();
                values.put("Avg",sensors.getS2().getAvg().get(i));
                values.put("IDTest", sensors.getS2().getIDTest().get(i));
                values.put("Min", sensors.getS2().getMin().get(i));
                values.put("Max", sensors.getS2().getMax().get(i));
                values.put("ErrorCode", sensors.getS2().getErrorCodes().get(i));
                values.put("Result",sensors.getS2().getResult().get(i));
                recordsHelper.getWritableDatabase().insert(RecordsContract.SingleS2.TABLE, "ErrorCode", values);
            }

            //String selection = RecordsContract.Sensors.COL_READINGS + "=?";
            //String[] selectionArgs = new String[]{"" + recordId};
            //Cursor sensorsCursor = recordsHelper.getWritableDatabase().query(RecordsContract.Sensors.TABLE, null, selection, selectionArgs, null, null, null);

            if (sensorsCursor.moveToFirst()) {
                long sensorsId = sensorsCursor.getLong(sensorsCursor.getColumnIndexOrThrow(RecordsContract.Sensors.ID));
                sensorsCursor.close();
                S0 s0 = new S0();
                selection = RecordsContract.Sensors0.ID + "=?";
                selectionArgs = new String[]{"" + sensorsId};
                Cursor s0Cursor = recordsHelper.getWritableDatabase().query(RecordsContract.SingleS0.TABLE, null, selection, selectionArgs, null, null, null);
                if (s0Cursor.moveToFirst()) {
                    List<Long> errorcodes = new ArrayList<>();
                    List<Long> avgs = new ArrayList<>();
                    List<Long> idtests = new ArrayList<>();
                    List<Long> maxs = new ArrayList<>();
                    List<Long> mins = new ArrayList<>();
                    List<Long> results = new ArrayList<>();

                    do {
                        errorcodes.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow("ErrorCode")));
                        avgs.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow("Avg")));
                        idtests.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow("IDTest")));
                        maxs.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow("Max")));
                        mins.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow("Min")));
                        results.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow("Result")));
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

                selection = RecordsContract.Sensors1.ID + "=?";
                selectionArgs = new String[]{"" + sensorsId};
                Cursor s1Cursor = recordsHelper.getWritableDatabase().query(RecordsContract.SingleS1.TABLE, null, selection, selectionArgs, null, null, null);
                if (s1Cursor.moveToFirst()) {
                    List<Long> errorcodes = new ArrayList<>();
                    List<Long> avgs = new ArrayList<>();
                    List<Long> idtests = new ArrayList<>();
                    List<Long> maxs = new ArrayList<>();
                    List<Long> mins = new ArrayList<>();
                    List<Long> results = new ArrayList<>();

                    do {
                        errorcodes.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow("ErrorCode")));
                        avgs.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow("Avg")));
                        idtests.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow("IDTest")));
                        maxs.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow("Max")));
                        mins.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow("Min")));
                        results.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow("Result")));
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

                selection = RecordsContract.Sensors2.ID + "=?";
                selectionArgs = new String[]{"" + sensorsId};
                Cursor s2Cursor = recordsHelper.getWritableDatabase().query(RecordsContract.SingleS2.TABLE, null, selection, selectionArgs, null, null, null);
                if (s2Cursor.moveToFirst()) {
                    List<Long> errorcodes = new ArrayList<>();
                    List<Long> avgs = new ArrayList<>();
                    List<Long> idtests = new ArrayList<>();
                    List<Long> maxs = new ArrayList<>();
                    List<Long> mins = new ArrayList<>();
                    List<Long> results = new ArrayList<>();

                    do {
                        errorcodes.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow("ErrorCode")));
                        avgs.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow("Avg")));
                        idtests.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow("IDTest")));
                        maxs.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow("Max")));
                        mins.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow("Min")));
                        results.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow("Result")));
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
            selection = RecordsContract.SingleTest.Test + "=?";
            selectionArgs = new String[]{"" + recordId};
            Cursor singleTestCursor = recordsHelper.getWritableDatabase().query(RecordsContract.SingleTest.TABLE, null, selection, selectionArgs, null, null, null);

            if (singleTestCursor.moveToNext()) {
                List<Long> errorcodes = new ArrayList<>();
                List<Long> testsIds = new ArrayList<>();
                List<Long> results = new ArrayList<>();
                List<Double> values = new ArrayList<>();

                do {
                    errorcodes.add(singleTestCursor.getLong(singleTestCursor.getColumnIndexOrThrow("ErrorCode")));
                    testsIds.add(singleTestCursor.getLong(singleTestCursor.getColumnIndexOrThrow("IDTest")));
                    results.add(singleTestCursor.getLong(singleTestCursor.getColumnIndexOrThrow("Result")));
                    values.add(singleTestCursor.getDouble(singleTestCursor.getColumnIndexOrThrow("Value")));

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
