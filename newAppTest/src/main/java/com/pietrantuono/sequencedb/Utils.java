package com.pietrantuono.sequencedb;

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
public class Utils {

    public static TestRecord RecontructRecord(long recordId) {
        //Readings readingss = new Select().from(Readings.class).where("TestRecord = ?", record.getId()).executeSingle();
        TestRecord record=new TestRecord();
        Readings readingss= new Readings();

        //Test test = new Select().from(Test.class).where("Readings = ?", readingss.getId()).executeSingle();
        Test test=new Test();
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

        //Sensors sensors = new Select().from(Sensors.class).where("Readings = ?", readingss.getId()).executeSingle();
        Sensors sensors= new Sensors();
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
            readingss.setSensors(sensors);
        }
        record.setReadings(readingss);

        return record;

    }
}