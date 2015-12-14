package server.utils;

import java.util.ArrayList;
import java.util.List;

import server.pojos.records.Readings;
import server.pojos.records.S0;
import server.pojos.records.S1;
import server.pojos.records.S2;
import server.pojos.records.Sensors;
import server.pojos.records.Test;
import server.pojos.records.TestRecord;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.tests.implementations.GetBarcodeTest;
import com.pietrantuono.tests.implementations.GetDeviceSerialTest;
import com.pietrantuono.tests.implementations.ReadModelNumberTest;
import com.pietrantuono.tests.implementations.SensorTestWrapper;

public class TestFromSequenceCreator {

	public static void createRecordFromSequence(NewSequenceInterface sequence) {
		if (sequence == null)
			return;
		if (sequence.getSequence() == null
				|| sequence.getSequence().size() <= 0)
			return;
		TestRecord record = new TestRecord();
		record.setBarcode(getBarcode(sequence));
		record.setDuration("" + sequence.getDuration());
		record.setFixtureNo(PeriCoachTestApplication.getFixtureIdintification());// TODO
																					// implement
		record.setFWVer(PeriCoachTestApplication.getGetFirmware().getVersion());
		record.setJobNo(sequence.getJobNo());
		record.setModel(getModel(sequence));
		record.setResult(sequence.getOverallResult());
		record.setSerial(getSerial(sequence));
		record.setStartedAt(sequence.getStartTime());
		record.setLog(sequence.isLog());
		Readings readings = createReadings(sequence);
		record.setReadings(readings);// TODO implement
		MyDatabaseUtils.ProcessAndSaveRecords(record);
	}

	private static Readings createReadings(NewSequenceInterface sequence) {
		Readings readings = new Readings();
		Sensors sensors = createSensors(sequence);
		readings.setSensors(sensors);
		Test test = createTest(sequence);
		readings.setTest(test);
		return readings;
	}

	private static Sensors createSensors(NewSequenceInterface sequence) {
		Sensors sensors = new Sensors();
		sensors.setS0(createS0(sequence));
		sensors.setS1(createS1(sequence));
		sensors.setS2(createS2(sequence));
		return sensors;
	}

	private static S0 createS0(NewSequenceInterface sequence) {
		S0 s0 = new S0();
		s0.setIDTest(getIdsOfSensorsTests(sequence));
		s0.setAvg(getAverage(sequence, SensorNumber.ZERO));
		s0.setMax(getMax(sequence, SensorNumber.ZERO));
		s0.setMin(getMin(sequence, SensorNumber.ZERO));
		s0.setResult(getResultOfSensorsTest(sequence, SensorNumber.ZERO));
		return s0;
	}

	private static S1 createS1(NewSequenceInterface sequence) {
		S1 s1 = new S1();
		s1.setIDTest(getIdsOfSensorsTests(sequence));
		s1.setAvg(getAverage(sequence, SensorNumber.ONE));
		s1.setMax(getMax(sequence, SensorNumber.ONE));
		s1.setMin(getMin(sequence, SensorNumber.ONE));
		s1.setResult(getResultOfSensorsTest(sequence, SensorNumber.ONE));
		return s1;
	}

	private static S2 createS2(NewSequenceInterface sequence) {
		S2 s2 = new S2();
		s2.setIDTest(getIdsOfSensorsTests(sequence));
		s2.setAvg(getAverage(sequence, SensorNumber.TWO));
		s2.setMax(getMax(sequence, SensorNumber.TWO));
		s2.setMin(getMin(sequence, SensorNumber.TWO));
		s2.setResult(getResultOfSensorsTest(sequence, SensorNumber.TWO));
		return s2;
	}

	private static long getBarcode(NewSequenceInterface sequence) {
		long barcode = 0;
		GetBarcodeTest barcodeTest = null;
		for (int i = 0; i < sequence.getSequence().size(); i++) {
			if (sequence.getSequence().get(i) instanceof GetBarcodeTest)
				barcodeTest = (GetBarcodeTest) sequence.getSequence().get(i);
		}
		if (barcodeTest == null)
			return barcode;
		try {
			barcode = Long.parseLong(barcodeTest.getBarcode());
		} catch (Exception e) {
		}
		return barcode;

	}

	private static long getModel(NewSequenceInterface sequence) {
		long model = 0;
		ReadModelNumberTest modelNumberTest = null;
		for (int i = 0; i < sequence.getSequence().size(); i++) {
			if (sequence.getSequence().get(i) instanceof ReadModelNumberTest)
				modelNumberTest = (ReadModelNumberTest) sequence.getSequence()
						.get(i);
		}
		if (modelNumberTest == null)
			return model;
		try {
			model = Long.parseLong(modelNumberTest.getModelnumber());
		} catch (Exception e) {
		}
		return model;

	}

	private static String getSerial(NewSequenceInterface sequence) {
		String serial = "";
		GetDeviceSerialTest deviceSerialTest = null;
		for (int i = 0; i < sequence.getSequence().size(); i++) {
			if (sequence.getSequence().get(i) instanceof GetDeviceSerialTest)
				deviceSerialTest = (GetDeviceSerialTest) sequence.getSequence()
						.get(i);
		}
		if (deviceSerialTest == null)
			return serial;
		serial = deviceSerialTest.getSerial();
		return serial;

	}

	static enum SensorNumber {
		ZERO, ONE, TWO
	}

	private static List<Long> getAverage(NewSequenceInterface sequence,
			SensorNumber sensorNumber) {
		List<Long> result = new ArrayList<Long>();
		for (int i = 0; i < sequence.getSequence().size(); i++) {
			if (sequence.getSequence().get(i) instanceof SensorTestWrapper) {
				SensorTestWrapper sensorTestWrapper = (SensorTestWrapper) sequence
						.getSequence().get(i);
				switch (sensorNumber) {
				case ZERO:
					result.add((long) sensorTestWrapper.getSensorTest()
							.getSensorResult().getSensor0avg());
					break;
				case ONE:
					result.add((long) sensorTestWrapper.getSensorTest()
							.getSensorResult().getSensor1avg());
					break;
				case TWO:
					result.add((long) sensorTestWrapper.getSensorTest()
							.getSensorResult().getSensor2avg());
					break;
				default:
					break;
				}

			}

		}
		return result;
	}

	private static List<Long> getMin(NewSequenceInterface sequence,
			SensorNumber number) {
		List<Long> result = new ArrayList<Long>();
		for (int i = 0; i < sequence.getSequence().size(); i++) {
			if (sequence.getSequence().get(i) instanceof SensorTestWrapper) {
				SensorTestWrapper sensorTestWrapper = (SensorTestWrapper) sequence
						.getSequence().get(i);
				switch (number) {
				case ZERO:
					result.add((long) sensorTestWrapper.getSensorTest()
							.getSensorResult().getSensor0min());
					break;

				case ONE:
					result.add((long) sensorTestWrapper.getSensorTest()
							.getSensorResult().getSensor1min());
					break;

				case TWO:
					result.add((long) sensorTestWrapper.getSensorTest()
							.getSensorResult().getSensor2min());
					break;

				default:
					break;
				}

			}

		}
		return result;
	}

	private static List<Long> getMax(NewSequenceInterface sequence,
			SensorNumber sensorNumber) {
		List<Long> result = new ArrayList<Long>();
		for (int i = 0; i < sequence.getSequence().size(); i++) {
			if (sequence.getSequence().get(i) instanceof SensorTestWrapper) {
				SensorTestWrapper sensorTestWrapper = (SensorTestWrapper) sequence
						.getSequence().get(i);
				switch (sensorNumber) {
				case ZERO:
					result.add((long) sensorTestWrapper.getSensorTest()
							.getSensorResult().getSensor0max());
					break;

				case ONE:
					result.add((long) sensorTestWrapper.getSensorTest()
							.getSensorResult().getSensor1max());
					break;

				case TWO:
					result.add((long) sensorTestWrapper.getSensorTest()
							.getSensorResult().getSensor2max());
					break;

				default:
					break;
				}

			}

		}
		return result;
	}

	private static List<Long> getResultOfSensorsTest(NewSequenceInterface sequence,
			SensorNumber sensorNumber) {
		List<Long> result = new ArrayList<Long>();
		for (int i = 0; i < sequence.getSequence().size(); i++) {
			if (sequence.getSequence().get(i) instanceof SensorTestWrapper) {
				SensorTestWrapper sensorTestWrapper = (SensorTestWrapper) sequence
						.getSequence().get(i);
				switch (sensorNumber) {
				case ZERO:
					Boolean pass = sensorTestWrapper.getSensorTest()
							.getSensorResult().getSensor0AvgPass();
					Boolean pass2 = sensorTestWrapper.getSensorTest()
							.getSensorResult().getSensor0stabilitypass();
					if (pass && pass2)
						result.add((long) 1);
					else
						result.add((long) 0);
					break;

				case ONE:
					pass = sensorTestWrapper.getSensorTest().getSensorResult()
							.getSensor1AvgPass();
					pass2 = sensorTestWrapper.getSensorTest().getSensorResult()
							.getSensor1stabilitypass();
					if (pass && pass2)
						result.add((long) 1);
					else
						result.add((long) 0);
					break;

				case TWO:
					pass = sensorTestWrapper.getSensorTest().getSensorResult()
							.getSensor2AvgPass();
					pass2 = sensorTestWrapper.getSensorTest().getSensorResult()
							.getSensor2stabilitypass();
					if (pass && pass2)
						result.add((long) 1);
					else
						result.add((long) 0);
					break;

				default:
					break;
				}

			}

		}
		return result;
	}

	private static List<Long> getIdsOfSensorsTests(NewSequenceInterface sequence) {
		List<Long> result = new ArrayList<Long>();
		for (int i = 0; i < sequence.getSequence().size(); i++) {
			if (sequence.getSequence().get(i) instanceof SensorTestWrapper) {
				SensorTestWrapper sensorTestWrapper = (SensorTestWrapper) sequence
						.getSequence().get(i);
				result.add(sensorTestWrapper.getIdTest());

			}

		}
		return result;
	}

	private static Test createTest(NewSequenceInterface sequence) {
		Test test = new Test();
		test.setIDTest(getTestIdsOfTests(sequence));
		test.setResult(getResultOfTests(sequence));
		test.setValue(getValueOfTests(sequence));
		return test;
	}

	private static List<Double> getValueOfTests(NewSequenceInterface sequence) {
		List<Double> result = new ArrayList<Double>();
		for (int i = 0; i < sequence.getSequence().size(); i++) {
				result.add(sequence.getSequence().get(i).getValue());
		}
		return result;
	}

	private static List<Long> getResultOfTests(NewSequenceInterface sequence) {
		List<Long> result = new ArrayList<Long>();
		for (int i = 0; i < sequence.getSequence().size(); i++) {
				if (sequence.getSequence().get(i).isSuccess())
					result.add(1l);
				else
					result.add(0l);
		}
		return result;
	}

	private static List<Long> getTestIdsOfTests(NewSequenceInterface sequence) {
		List<Long> result = new ArrayList<Long>();
		for (int i = 0; i < sequence.getSequence().size(); i++) {
				result.add(sequence.getSequence().get(i).getIdTest());
		}
		return result;
	}

}
