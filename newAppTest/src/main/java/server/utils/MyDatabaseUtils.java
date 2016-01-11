package server.utils;

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

import com.activeandroid.Model;
import com.activeandroid.query.Select;

public class MyDatabaseUtils {

    static void ProcessAndSaveRecords(TestRecord record) {
        record.save();
        record.getReadings().setForeignkey(record);
        record.getReadings().save();
        record.getReadings().getTest().setForeginkey(record.getReadings());
        record.getReadings().getTest().save();
        if (record.getReadings().getSensors() != null) {
            record.getReadings().getSensors().setForeignkey(record.getReadings());
        }
        if (record.getReadings().getSensors() != null) {
            record.getReadings().getSensors().save();
        }
        if (record.getReadings().getSensors() != null) {
            record.getReadings().getSensors().getS0().setForeignkey(record.getReadings().getSensors());
        }
        if (record.getReadings().getSensors() != null) {
            record.getReadings().getSensors().getS0().save();
        }

        if (record.getReadings().getSensors() != null)
            record.getReadings().getSensors().getS1().setForeignkey(record.getReadings().getSensors());
        if (record.getReadings().getSensors() != null)
            record.getReadings().getSensors().getS1().save();

        if (record.getReadings().getSensors() != null) {
            record.getReadings().getSensors().getS2().setForeignkey(record.getReadings().getSensors());
        }
        if (record.getReadings().getSensors() != null) {
            record.getReadings().getSensors().getS2().save();
        }

        for (int i = 0; i < record.getReadings().getTest().getIDTest().size(); i++) {
            SingleTest singleTest = new SingleTest();
            singleTest.setIDTest(record.getReadings().getTest().getIDTest().get(i));
            singleTest.setResult(record.getReadings().getTest().getResult().get(i));
            singleTest.setValue(record.getReadings().getTest().getValue().get(i));
            singleTest.setForeignkey(record.getReadings().getTest());
            singleTest.save();
        }
        if (record.getReadings().getSensors() == null) return;
        for (int j = 0; j < record.getReadings().getSensors().getS0().getIDTest().size(); j++) {
            SingleS0 singleS0 = new SingleS0();
            singleS0.setAvg(record.getReadings().getSensors().getS0().getAvg().get(j));
            singleS0.setIDTest(record.getReadings().getSensors().getS0().getIDTest().get(j));
            singleS0.setMax(record.getReadings().getSensors().getS0().getMax().get(j));
            singleS0.setMin(record.getReadings().getSensors().getS0().getMin().get(j));
            singleS0.setResult(record.getReadings().getSensors().getS0().getResult().get(j));
            singleS0.setForeignkey(record.getReadings().getSensors().getS0());
            singleS0.save();
        }

        for (int j = 0; j < record.getReadings().getSensors().getS0().getIDTest().size(); j++) {
            SingleS1 singleS1 = new SingleS1();
            singleS1.setAvg(record.getReadings().getSensors().getS1().getAvg().get(j));
            singleS1.setIDTest(record.getReadings().getSensors().getS1().getIDTest().get(j));
            singleS1.setMax(record.getReadings().getSensors().getS1().getMax().get(j));
            singleS1.setMin(record.getReadings().getSensors().getS1().getMin().get(j));
            singleS1.setForeignkey(record.getReadings().getSensors().getS1());
            singleS1.setResult(record.getReadings().getSensors().getS1().getResult().get(j));
            singleS1.save();
        }
        for (int j = 0; j < record.getReadings().getSensors().getS0().getIDTest().size(); j++) {
            SingleS2 singleS2 = new SingleS2();
            singleS2.setAvg(record.getReadings().getSensors().getS2().getAvg().get(j));
            singleS2.setIDTest(record.getReadings().getSensors().getS2().getIDTest().get(j));
            singleS2.setMax(record.getReadings().getSensors().getS2().getMax().get(j));
            singleS2.setMin(record.getReadings().getSensors().getS2().getMin().get(j));
            singleS2.setForeignkey(record.getReadings().getSensors().getS2());
            singleS2.setResult(record.getReadings().getSensors().getS2().getResult().get(j));
            singleS2.save();
        }

    }

    public static TestRecord RecontructRecord(TestRecord record) {
        Readings readingss = new Select().from(Readings.class).where("TestRecord = ?", record.getId()).executeSingle();

        Test test = new Select().from(Test.class).where("Readings = ?", readingss.getId()).executeSingle();
        readingss.setTest(test);

        if (test != null && test.getId() != null) {
            List<SingleTest> singleTests = new Select().from(SingleTest.class).where("Test = ?", test.getId()).execute();
            List<Long> ids = new ArrayList<Long>();
            List<Long> results = new ArrayList<Long>();
            List<Double> values = new ArrayList<Double>();
            for (int i = 0; i < singleTests.size(); i++) {
                ids.add(singleTests.get(i).getIDTest());
                results.add(singleTests.get(i).getResult());
                values.add(singleTests.get(i).getValue());
            }
            test.setIDTest(ids);
            test.setResult(results);
            test.setValue(values);
        }

        Sensors sensors = new Select().from(Sensors.class).where("Readings = ?", readingss.getId()).executeSingle();
        if (sensors != null && sensors.getId() != null) {
            S0 s0 = new Select().from(S0.class).where("Sensors = ?", sensors.getId()).executeSingle();
            List<Model> foo = new Select().from(S0.class).execute();
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
            readingss.setSensors(sensors);
        }
        record.setReadings(readingss);

        return record;

    }

    public static void deteteRecod(TestRecord record) {
        Readings readingss = new Select().from(Readings.class).where("TestRecord = ?", record.getId()).executeSingle();

        Test test = new Select().from(Test.class).where("Readings = ?", readingss.getId()).executeSingle();

        Sensors sensors = new Select().from(Sensors.class).where("Readings = ?", readingss.getId()).executeSingle();

        S0 s0 = new Select().from(S0.class).where("Sensors = ?", readingss.getId()).executeSingle();

        S1 s1 = new Select().from(S1.class).where("Sensors = ?", readingss.getId()).executeSingle();

        S2 s2 = new Select().from(S2.class).where("Sensors = ?", readingss.getId()).executeSingle();

        List<SingleTest> singleTests = new Select().from(SingleTest.class).where("Test = ?", test.getId()).execute();


        List<SingleS0> singleS0s = new Select().from(SingleS0.class).where("S0 = ?", s0.getId()).execute();


        List<SingleS1> singleS1s = new Select().from(SingleS1.class).where("S1 = ?", s1.getId()).execute();

        List<SingleS2> singleS2s = new Select().from(SingleS2.class).where("S2 = ?", s2.getId()).execute();
        for (SingleS2 ss2 : singleS2s) ss2.delete();

        for (SingleS1 ss1 : singleS1s) ss1.delete();

        for (SingleS0 ss0 : singleS0s) ss0.delete();

        for (SingleTest st : singleTests) st.delete();

        s1.delete();

        s2.delete();

        s0.delete();

        sensors.delete();

        test.delete();

        readingss.delete();

        record.delete();
    }


    public static void updateRecordRecod(TestRecord record) {
        Readings readingss = new Select().from(Readings.class).where("TestRecord = ?", record.getId()).executeSingle();

        Test test = new Select().from(Test.class).where("Readings = ?", readingss.getId()).executeSingle();

        Sensors sensors = new Select().from(Sensors.class).where("Readings = ?", readingss.getId()).executeSingle();

        S0 s0 = new Select().from(S0.class).where("Sensors = ?", readingss.getId()).executeSingle();

        S1 s1 = new Select().from(S1.class).where("Sensors = ?", readingss.getId()).executeSingle();

        S2 s2 = new Select().from(S2.class).where("Sensors = ?", readingss.getId()).executeSingle();

        List<SingleTest> singleTests = new Select().from(SingleTest.class).where("Test = ?", test.getId()).execute();


        List<SingleS0> singleS0s = new Select().from(SingleS0.class).where("S0 = ?", s0.getId()).execute();


        List<SingleS1> singleS1s = new Select().from(SingleS1.class).where("S1 = ?", s1.getId()).execute();

        List<SingleS2> singleS2s = new Select().from(SingleS2.class).where("S2 = ?", s2.getId()).execute();
        for (SingleS2 ss2 : singleS2s) ss2.delete();

        for (SingleS1 ss1 : singleS1s) ss1.delete();

        for (SingleS0 ss0 : singleS0s) ss0.delete();

        for (SingleTest st : singleTests) st.delete();

        s1.delete();

        s2.delete();

        s0.delete();

        sensors.delete();

        test.delete();

        readingss.delete();

        record.delete();
    }

}