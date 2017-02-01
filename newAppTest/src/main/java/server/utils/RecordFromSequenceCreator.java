package server.utils;

import java.util.ArrayList;
import java.util.List;

import server.pojos.Device;
import server.pojos.records.Readings;
import server.pojos.records.Sensor;
import server.pojos.records.Sensors;
import server.pojos.records.Test;
import server.pojos.records.TestRecord;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.constants.SequenceInterface;
import com.pietrantuono.tests.implementations.SensorTestWrapper;


public class RecordFromSequenceCreator {

    private final static int ZERO = 0;
    private final static int ONE = 1;
    private final static int TWO = 2;

    public static TestRecord createRecordFromSequence(SequenceInterface sequence, Device device) {
        if (sequence == null)
            return null;
        if (sequence.getSequence() == null
                || sequence.getSequence().size() <= 0)
            return null;
        TestRecord record = new TestRecord();
        record.setBarcode(device.getBarcode() != null ? Long.parseLong(device.getBarcode()) : 0);
        record.setDuration("" + sequence.getDuration());
        record.setFixtureNo(PeriCoachTestApplication.getFixtureIdintification());
        record.setFWVer(device.getFwver() != null ? device.getFwver() : "");
        record.setJobNo(sequence.getJobNo());
        record.setModel(device.getModel() != null ? Long.parseLong(device.getModel()) : 0);
        record.setResult(sequence.getOverallResult());
        record.setSerial(device.getSerial() != null ? device.getSerial() : "");
        record.setStartedAt(sequence.getStartTime());
        record.setBT_Addr(device.getBt_addr() != null ? device.getBt_addr() : "");
        Readings readings = createReadings(sequence);
        record.setReadings(readings);
        return record;
    }

    private static Readings createReadings(SequenceInterface sequence) {
        Readings readings = new Readings();
        Sensors sensors = createSensors(sequence);
        readings.setSensors(sensors);
        Test test = createTest(sequence);
        readings.setTest(test);
        return readings;
    }

    private static Sensors createSensors(SequenceInterface sequence) {
        if (!containsSensorsTests(sequence)) return null;
        if (!sensorsTestsWereExecuted(sequence)) return null;
        Sensors sensors = new Sensors();
        for (int i = 0; i < 3; i++) {
            sensors.setSensor(i, createSensor(i, sequence));
        }
        return sensors;
    }

    private static Sensor createSensor(int sensorNumber, SequenceInterface sequence) {
        Sensor sensor = new Sensor();
        if (!containsSensorsTests(sequence)) return null;
        List<Long> ids = new ArrayList<Long>();
        List<Long> avgs = new ArrayList<Long>();
        List<Long> maxs = new ArrayList<Long>();
        List<Long> mins = new ArrayList<Long>();
        List<Long> results = new ArrayList<Long>();
        List<Long> ecs = new ArrayList<Long>();
        for (int i = 0; i < sequence.getSequence().size(); i++) {
            if (sequence.getSequence().get(i) instanceof SensorTestWrapper) {
                SensorTestWrapper sensorTestWrapper = (SensorTestWrapper) sequence
                        .getSequence().get(i);
                String[] parts = sensorTestWrapper.getDescription().split(",");
                if (parts[0].length() != 19 || Character.digit(parts[0].charAt(7), 10) == sensorNumber) {
                    ids.add(getIDTest(sensorTestWrapper));
                    avgs.add(getAverage(sensorTestWrapper, sensorNumber));
                    maxs.add(getMax(sensorTestWrapper, sensorNumber));
                    mins.add(getMin(sensorTestWrapper, sensorNumber));
                    ecs.add(getErrorCode(sensorTestWrapper));
                    results.add(getResult(sensorTestWrapper, sensorNumber));
                }
            }
        }
        sensor.setIDTest(ids);
        sensor.setAvg(avgs);
        sensor.setMax(maxs);
        sensor.setMin(mins);
        sensor.setResult(results);
        sensor.setErrorCodes(ecs);
        return sensor;
    }

    private static Long getIDTest(SensorTestWrapper sensorTestWrapper) {
        return sensorTestWrapper.getIdTest();
    }

    private static Long getAverage(SensorTestWrapper sensorTestWrapper, int sensorNumber) {
        return (long)(sensorTestWrapper.getSensorTest().getSensorResult().getSensorAvg(sensorNumber));
    }

    private static Long getMin(SensorTestWrapper sensorTestWrapper, int sensorNumber) {
        return (long)(sensorTestWrapper.getSensorTest().getSensorResult().getSensorMin(sensorNumber));
    }

    private static Long getMax(SensorTestWrapper sensorTestWrapper, int sensorNumber) {
        return (long)(sensorTestWrapper.getSensorTest().getSensorResult().getSensorMax(sensorNumber));
    }

    private static Long getResult(SensorTestWrapper sensorTestWrapper, int sensorNumber) {
        Boolean pass = sensorTestWrapper.getSensorTest()
                .getSensorResult().getSensorAvgPass(sensorNumber);
        Boolean pass2 = sensorTestWrapper.getSensorTest()
                .getSensorResult().getSensorStabilityPass(sensorNumber);
        if (pass && pass2)
            return (long) 1;
        else
            return (long) 0;
    }

    private static Long getErrorCode(SensorTestWrapper sensorTestWrapper) {
        return sensorTestWrapper.getErrorCode();
    }

    private static Test createTest(SequenceInterface sequence) {
        Test test = new Test();
        test.setIDTest(getTestIdsOfTests(sequence));
        test.setResult(getResultOfTests(sequence));
        test.setValue(getValueOfTests(sequence));
        test.setErrorCode(getErrorCodeOfTests(sequence));
        return test;
    }

    private static List<Double> getValueOfTests(SequenceInterface sequence) {
        List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < sequence.getSequence().size(); i++) {
            if (!(sequence.getSequence().get(i) instanceof SensorTestWrapper))
                result.add(sequence.getSequence().get(i).getValue());
        }
        return result;
    }

    private static List<Long> getResultOfTests(SequenceInterface sequence) {
        List<Long> result = new ArrayList<Long>();
        for (int i = 0; i < sequence.getSequence().size(); i++) {
            if ((sequence.getSequence().get(i) instanceof SensorTestWrapper)) continue;
            if (sequence.getSequence().get(i).isSuccess())
                result.add(1l);
            else
                result.add(0l);
        }
        return result;
    }

    private static List<Long> getTestIdsOfTests(SequenceInterface sequence) {
        List<Long> result = new ArrayList<Long>();
        for (int i = 0; i < sequence.getSequence().size(); i++) {
            if (!(sequence.getSequence().get(i) instanceof SensorTestWrapper))
                result.add(sequence.getSequence().get(i).getIdTest());
        }
        return result;
    }

    private static List<Long> getErrorCodeOfTests(SequenceInterface sequence) {
        List<Long> result = new ArrayList<Long>();
        for (int i = 0; i < sequence.getSequence().size(); i++) {
            if ((sequence.getSequence().get(i) instanceof SensorTestWrapper)) continue;
            if (sequence.getSequence().get(i).getErrorCode() != 0)
                result.add(sequence.getSequence().get(i).getErrorCode());
            else result.add(null);
        }
        return result;
    }

    private static boolean containsSensorsTests(SequenceInterface sequence) {
        boolean result = false;
        for (int i = 0; i < sequence.getSequence().size(); i++) {
            if (sequence.getSequence().get(i) instanceof SensorTestWrapper) return true;
        }
        return result;
    }

    private static boolean sensorsTestsWereExecuted(SequenceInterface sequence) {
        boolean result = false;
        for (int i = 0; i < sequence.getSequence().size(); i++) {
            if (sequence.getSequence().get(i) instanceof SensorTestWrapper) {
                if (((SensorTestWrapper) sequence.getSequence().get(i)).executed) result = true;
            }
        }
        return result;
    }
}