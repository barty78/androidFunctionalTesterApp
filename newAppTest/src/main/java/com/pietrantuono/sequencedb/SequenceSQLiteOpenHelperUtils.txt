package com.pietrantuono.sequencedb;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import server.pojos.records.Readings;
import server.pojos.records.S0;
import server.pojos.records.S1;
import server.pojos.records.S2;
import server.pojos.records.Sensors;
import server.pojos.records.SingleS0;
import server.pojos.records.SingleS1;
import server.pojos.records.SingleS2;
import server.pojos.records.SingleTest;
import server.pojos.records.Test;
import server.pojos.records.TestRecord;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class SequenceSQLiteOpenHelperUtils {
    static long saveRecord(SQLiteDatabase db, TestRecord singleRecord) {
        ContentValues contentvalues = new ContentValues();
        contentvalues.put(SequenceContracts.Records.RECORDS_BARCODE, singleRecord.getBarcode());
        contentvalues.put(SequenceContracts.Records.RECORDS_DURATION, singleRecord.getDuration() != null ? singleRecord.getDuration() : "");
        contentvalues.put(SequenceContracts.Records.RECORDS_FIXTURE_NUMBER, singleRecord.getDuration() != null ? singleRecord.getDuration() : "");
        contentvalues.put(SequenceContracts.Records.RECORDS_FW_VERSION, singleRecord.getFWVer() != null ? singleRecord.getFWVer() : "");
        contentvalues.put(SequenceContracts.Records.RECORDS_JOB_NUMBER, singleRecord.getJobNo());
        contentvalues.put(SequenceContracts.Records.RECORDS_MODEL, singleRecord.getModel());
        contentvalues.put(SequenceContracts.Records.RECORDS_RESULT, singleRecord.getResult());
        contentvalues.put(SequenceContracts.Records.RECORDS_SERIAL, singleRecord.getSerial() != null ? singleRecord.getSerial() : "");
        contentvalues.put(SequenceContracts.Records.RECORDS_STARTED_AT, singleRecord.getStartedAt() != null ? singleRecord.getStartedAt() : "");
        contentvalues.put(SequenceContracts.Records.RECORDS_BT_MAC, singleRecord.getBT_Addr() != null ? singleRecord.getBT_Addr() : "");
        return db.insert(SequenceContracts.Records.TABLE_RECORDS, SequenceContracts.Records.RECORDS_BARCODE, contentvalues);
    }

    static void saveReadings(SequenceSQLiteOpenHelper sequenceSQLiteOpenHelper, SQLiteDatabase db, long recordId, TestRecord singleRecord) {
        Readings readings = new Select().from(Readings.class).where("TestRecord = ?", singleRecord.getId()).executeSingle();
        if (readings != null) {
            Test test = new Select().from(Test.class).where("Readings = ?", readings.getId()).executeSingle();
            if (test != null) saveTests(db, recordId, test);
        }
        if (readings != null) {
            Sensors sensors = new Select().from(Sensors.class).where("Readings = ?", readings.getId()).executeSingle();
            if (sensors != null) gatAndSaveSensors(sequenceSQLiteOpenHelper, db, recordId, sensors);
        }
    }

    private static void gatAndSaveSensors(SequenceSQLiteOpenHelper sequenceSQLiteOpenHelper, SQLiteDatabase db, long recordId, Sensors sensors) {
        if (sensors != null && sensors.getId() != null) {
            S0 s0 = new Select().from(S0.class).where("Sensors = ?", sensors.getId()).executeSingle();
            sensors.setS0(s0);

            S1 s1 = new Select().from(S1.class).where("Sensors = ?", sensors.getId()).executeSingle();
            sensors.setS1(s1);

            S2 s2 = new Select().from(S2.class).where("Sensors = ?", sensors.getId()).executeSingle();
            sensors.setS2(s2);

            if (s0 != null && s0.getId() != null) {
                List<SingleS0> singleS0s = new Select().from(SingleS0.class).where("S0 = ?", s0.getId()).execute();
                List<Long> idstestsS0 = new ArrayList<Long>();
                List<Long> maxS0 = new ArrayList<Long>();
                List<Long> minS0 = new ArrayList<Long>();
                List<Long> avgS0 = new ArrayList<Long>();
                List<Long> resultS0 = new ArrayList<Long>();
                for (int i = 0; i < singleS0s.size(); i++) {
                    idstestsS0.add(singleS0s.get(i).getIDTest());
                    maxS0.add(singleS0s.get(i).getMax());
                    minS0.add(singleS0s.get(i).getMin());
                    avgS0.add(singleS0s.get(i).getAvg());
                    resultS0.add(singleS0s.get(i).getResult());
                }
                s0.setIDTest(idstestsS0);
                s0.setResult(resultS0);
                s0.setAvg(avgS0);
                s0.setMax(maxS0);
                s0.setMin(minS0);
                s0.setResult(resultS0);
            }
            if (s1 != null && s1.getId() != null) {
                List<SingleS1> singleS1s = new Select().from(SingleS1.class).where("S1 = ?", s1.getId()).execute();
                List<Long> idstestsS1 = new ArrayList<Long>();
                List<Long> maxS1 = new ArrayList<Long>();
                List<Long> minS1 = new ArrayList<Long>();
                List<Long> avgS1 = new ArrayList<Long>();
                List<Long> resultS1 = new ArrayList<Long>();
                for (int i = 0; i < singleS1s.size(); i++) {
                    idstestsS1.add(singleS1s.get(i).getIDTest());
                    maxS1.add(singleS1s.get(i).getMax());
                    minS1.add(singleS1s.get(i).getMin());
                    avgS1.add(singleS1s.get(i).getAvg());
                    resultS1.add(singleS1s.get(i).getResult());
                }
                s1.setIDTest(idstestsS1);
                s1.setResult(resultS1);
                s1.setAvg(avgS1);
                s1.setMax(maxS1);
                s1.setMin(minS1);
                s1.setResult(resultS1);
            }
            if (s2 != null && s2.getId() != null) {
                List<SingleS2> singleS2s = new Select().from(SingleS2.class).where("S2 = ?", s2.getId()).execute();
                List<Long> idstestsS2 = new ArrayList<Long>();
                List<Long> maxS2 = new ArrayList<Long>();
                List<Long> minS2 = new ArrayList<Long>();
                List<Long> avgS2 = new ArrayList<Long>();
                List<Long> resultS2 = new ArrayList<Long>();
                for (int i = 0; i < singleS2s.size(); i++) {
                    idstestsS2.add(singleS2s.get(i).getIDTest());
                    maxS2.add(singleS2s.get(i).getMax());
                    minS2.add(singleS2s.get(i).getMin());
                    avgS2.add(singleS2s.get(i).getAvg());
                    resultS2.add(singleS2s.get(i).getResult());
                }
                s2.setIDTest(idstestsS2);
                s2.setResult(resultS2);
                s2.setAvg(avgS2);
                s2.setMax(maxS2);
                s2.setMin(minS2);
                s2.setResult(resultS2);
            }
            //readingss.setSensors(sensors);
            saveSensors(sequenceSQLiteOpenHelper, db, recordId, sensors);
        }
        //record.setReadings(readingss);
        //return record;
    }

    private static void saveSensors(SequenceSQLiteOpenHelper sequenceSQLiteOpenHelper, SQLiteDatabase db, long recordId, Sensors sensors) {
        if (sensors == null) return;
        S0 s0 = sensors.getS0();
        S1 s1 = sensors.getS1();
        S2 s2 = sensors.getS2();
        int lenght = getmaxLength(s0, s1, s2);
        for (int i = 0; i < lenght; i++) {
            long s0Avg=0;
            long s0Min=0;
            long s0Max=0;
            long s0Result=0;

            long s1Avg=0;
            long s1Min=0;
            long s1Max=0;
            long s1Result=0;

            long s2Avg=0;
            long s2Min=0;
            long s2Max=0;
            long s2Result=0;

            if (s0 != null) {
                if (s0.getAvg() != null && s0.getAvg().size() > 0) {
                    try { s0Avg=s0.getAvg().get(i);
                    } catch (Exception e){}
                }
                if (s0.getMax() != null && s0.getMax().size() > 0) {
                    try { s0Max=s0.getMax().get(i);
                    } catch (Exception e){}
                }
                if (s0.getMin() != null && s0.getMin().size() > 0) {
                    try { s0Min=s0.getMin().get(i);
                    } catch (Exception e){}
                }
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(SequenceContracts.SensorResults.TABLE_SENSOR_RESULTS_FOREIGN_KEY_ID_OF_TEST,recordId);
            contentValues.put(SequenceContracts.SensorResults.SENSOR_RESULTS_S0_AVG,s0Avg);
            contentValues.put(SequenceContracts.SensorResults.SENSOR_RESULTS_S0_MAX,s0Max);
            contentValues.put(SequenceContracts.SensorResults.SENSOR_RESULTS_S0_MIN,s0Min);

            contentValues.put(SequenceContracts.SensorResults.SENSOR_RESULTS_S1_AVG,s1Avg);
            contentValues.put(SequenceContracts.SensorResults.SENSOR_RESULTS_S1_MAX,s1Max);
            contentValues.put(SequenceContracts.SensorResults.SENSOR_RESULTS_S1_MIN,s1Min);

            contentValues.put(SequenceContracts.SensorResults.SENSOR_RESULTS_S2_AVG,s2Avg);
            contentValues.put(SequenceContracts.SensorResults.SENSOR_RESULTS_S2_MAX,s2Max);
            contentValues.put(SequenceContracts.SensorResults.SENSOR_RESULTS_S2_MIN,s2Min);

            db.insert(SequenceContracts.SensorResults.TABLE_SENSOR_RESULTS, SequenceContracts.SensorResults.SENSOR_RESULTS_S0_AVG, contentValues);
        }
    }

    private static int getmaxLength(S0 s0, S1 s1, S2 s2) {
        List<Long> idTest = new ArrayList<>(0);
        List<Long> avg = new ArrayList<>(0);
        List<Long> max = new ArrayList<>(0);
        List<Long> min = new ArrayList<>(0);
        List<Long> result = new ArrayList<>(0);
        List<Long> idTest1 = new ArrayList<>(0);
        List<Long> avg1 = new ArrayList<>(0);
        List<Long> max1 = new ArrayList<>(0);
        List<Long> min1 = new ArrayList<>(0);
        List<Long> result1 = new ArrayList<>(0);
        List<Long> idTest2 = new ArrayList<>(0);
        List<Long> avg2 = new ArrayList<>(0);
        List<Long> max2 = new ArrayList<>(0);
        List<Long> min2 = new ArrayList<>(0);
        List<Long> result2 = new ArrayList<>(0);
        if (s0 != null) {
            idTest = s0.getIDTest();
            avg = s0.getAvg();
            max = s0.getMax();
            min = s0.getMin();
            result = s0.getResult();
        }
        if (s1 != null) {
            idTest1 = s1.getIDTest();
            avg1 = s1.getAvg();
            max1 = s1.getMax();
            min1 = s1.getMin();
            result1 = s1.getResult();
        }
        if (s2 != null) {
            idTest2 = s2.getIDTest();
            avg2 = s2.getAvg();
            max2 = s2.getMax();
            min2 = s2.getMin();
            result2 = s2.getResult();
        }
        int size = idTest.size();
        int size1 = avg.size();
        int size2 = max.size();
        int size3 = min.size();
        int size4 = result.size();
        int size5 = idTest1.size();
        int size6 = avg1.size();
        int size7 = max1.size();
        int size8 = min1.size();
        int size9 = result1.size();
        int size10 = idTest2.size();
        int size11 = avg2.size();
        int size12 = max2.size();
        int size13 = min2.size();
        int size14 = result2.size();

        return Math.max(size1, Math.max(size1, Math.max(size2, Math.max(size3, Math.max(size4, Math.max(size5, Math.max(size6, Math.max(size6, Math.max(size7, Math.max(size8, Math.max(size9, Math.max(size10, Math.max(size11, Math.max(size12, Math.max(size13, size14)))))))))))))));

    }

    private static void saveTests(SQLiteDatabase db, long recordId, Test test) {
        if (test != null && test.getId() != null) {
            List<SingleTest> tests = new Select().from(SingleTest.class).where("Test = ?", test.getId()).execute();
            for (int i = 0; i < tests.size(); i++) {
                SingleTest singleTest = tests.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(SequenceContracts.Tests.TABLE_TESTS_FOREIGN_KEY_ID_OF_RECORD, recordId);
                contentValues.put(SequenceContracts.Tests.TABLE_TESTS_TEST_ID, singleTest.getIDTest());
                contentValues.put(SequenceContracts.Tests.TABLE_TESTS_VALUE, singleTest.getValue());
                contentValues.put(SequenceContracts.Tests.TABLE_TESTS_RESULT, singleTest.getResult());
                db.insert(SequenceContracts.Tests.TABLE_TESTS, SequenceContracts.Tests.TABLE_TESTS_NAME, contentValues);
            }

        }

    }

    static void migrateOldData(SequenceSQLiteOpenHelper sequenceSQLiteOpenHelper, SQLiteDatabase db) {
        List<TestRecord> records = new Select().from(TestRecord.class).execute();
        if (records == null || records.size() <= 0) return;
        for (int i = 0; i < records.size(); i++) {
            TestRecord singleRecord = records.get(i);
            if (singleRecord == null) continue;
            long recordId = saveRecord(db, singleRecord);
            if (recordId != -1) {
                saveReadings(sequenceSQLiteOpenHelper, db, recordId, singleRecord);
            }
        }
    }
}
