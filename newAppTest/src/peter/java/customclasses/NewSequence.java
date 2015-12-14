package customclasses;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.pietrantuono.activities.MainActivity;
import com.pietrantuono.constants.NewMResult;
import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.ioioutils.Current;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.tests.implementations.AccelerometerSelfTest;
import com.pietrantuono.tests.implementations.BatteryLevelUUTVoltageTest;
import com.pietrantuono.tests.implementations.BluetoothConnectTestForTesting;
import com.pietrantuono.tests.implementations.ChargeLedCheckTest;
import com.pietrantuono.tests.implementations.ChargingTest;
import com.pietrantuono.tests.implementations.CurrentTest;
import com.pietrantuono.tests.implementations.GetDeviceSerialTest;
import com.pietrantuono.tests.implementations.LedCheckTest;
import com.pietrantuono.tests.implementations.MagnetWakeDeviceTest;
import com.pietrantuono.tests.implementations.ReadDeviceInfoSerialNumberTest;
import com.pietrantuono.tests.implementations.ReadFirmwareversionTest;
import com.pietrantuono.tests.implementations.ReadModelNumberTest;
import com.pietrantuono.tests.implementations.SensorTestWrapper;
import com.pietrantuono.tests.implementations.UploadFirmwareTest;
import com.pietrantuono.tests.implementations.VoltageTest;
import com.pietrantuono.tests.implementations.steps.SetDigitalOutputStep;
import com.pietrantuono.tests.implementations.steps.SetSensorVoltagesStep;
import com.pietrantuono.tests.superclass.Test;

import server.pojos.Job;
import ioio.lib.api.IOIO;

public class NewSequence implements NewSequenceInterface {
	private List<Test> sequence = null;
	private volatile int currentStepNumber = -1;
	private volatile Test currentStep = null;
	private long starttime = 0;
	private long endtime = 0;
	private long jobNo = 0;
	private boolean log = true;
	private Job job;

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#Next()
	 */

	@Override
	public synchronized void Next() {
		try {
		} catch (Exception e) {
		}
		currentStepNumber++;
		currentStep = sequence.get(currentStepNumber);

	}

	@Override
	public void executeCurrentTest() {
		currentStep.execute();
	}

	@Override
	public Boolean isSequenceStarted() {
		if (currentStepNumber == -1)
			return false;
		else
			return true;
	}
	public synchronized int getCurrentTestNumber() {
		try {
		} catch (Exception e) {
		}
		return currentStepNumber;
	}

	@Override
	public synchronized Test getCurrentTest() {
		currentStep = sequence.get(currentStepNumber);
		return currentStep;
	}

	@Override
	public synchronized Test getNextTest() {
		return sequence.get(currentStepNumber + 1);
	}

	private synchronized int getNexttTestNumber() {
		return currentStepNumber + 1;
	}

	public synchronized String getCurrentTestNumberAsString() {
		if (currentStepNumber >= 0)
			return Integer.toString(getCurrentTestNumber());
		else
			return Integer.toString(0);
	}

	@Override
	public synchronized String getCurrentTestDescription() {
		if (currentStepNumber >= 0)
			return getCurrentTest().getDescription();
		else
			return sequence.get(0).getDescription();
	}

	@Override
	public synchronized String getNextTestDescription() throws Exception {
		if (currentStepNumber >= 0 && currentStepNumber<sequence.size()-1)
			return getNextTest().getDescription();
		else
			return sequence.get(1).getDescription();
	}

	@Override
	public synchronized void reset() {
		currentStepNumber = -1;
		// currentStep=sequence.get(currentStepNumber);
	}

	public synchronized String getNexttTestNumberAsAString() {
		if (currentStepNumber >= 0 && currentStepNumber<sequence.size()-1)
			return Integer.toString(getNexttTestNumber());
		else
			return ("" + 1);
	}

	public ArrayList<NewMResult> getEmptyResultsList() {
		ArrayList<NewMResult> results = new ArrayList<NewMResult>();
		for (int i = 0; i < sequence.size(); i++) {
			if (!sequence.get(i).isSensorTest())
				results.add(new NewMResult(sequence.get(i)));
			// else if(sequence.get(i) instanceof
			// ClosedTestWrapper)results.add(new
			// ClosedTestResult(sequence.get(i)));
			else
				results.add(new NewMSensorResult(sequence.get(i)));
		}
		return results;
	}

	@Override
	public int getNumberOfSteps() {
		return sequence.size();
	}

	@Override
	public Boolean isSequenceEnded() {
		return currentStepNumber >= sequence.size() - 1;
	}


	@Override
	public void stopAll(MainActivity mainActivity) {
		if (sequence == null || sequence.size() <= 0)
			return;
		mainActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				for (Test test : sequence) {
					try {
						test.interrupt();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

	@Override
	public List<Test> getSequence() {
		return sequence;
	}

	@Override
	public long getDuration() {
		return endtime - starttime;
	}

	@Override
	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}

	@Override
	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}

	@Override
	public long getJobNo() {
		return jobNo;
	}

	@Override
	public void setJobNo(long jobNo) {
		this.jobNo = jobNo;
	}

	@Override
	public String getStartTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss.SSS");
		return dateFormat.format(starttime);
	}

	@Override
	public long getOverallResult() {
		long overallresult = 1;
		for (int i = 0; i < sequence.size(); i++) {
			if (sequence.get(i).isSuccess())
				overallresult = 0;
		}

		return overallresult;
	}

	/**
	 * ATTENTION!!! CREATES RANDOM SEQUENCE, FOR TEST ONLY!
	 * 
	 * @param activity
	 * @param ioio
	 */
	public NewSequence(Activity activity, IOIO ioio, Job job) {
		setLog(false);
		this.job = job;

		sequence = new ArrayList<Test>();

		// Dummy Test Setup
		for (int i = 0; i < 10; i++) {
			//sequence.add(new DummyTest(activity, "Dummy Test "+i,  false, true));
		}
		
		// OLD TESTS
		//sequence.add(new VoltageTest(activity, ioio, 40, 3.0f, 0.1f, "Voltage Measurement - V_3V0"));
		// sequence.add(new VoltageTest(activity, ioio, 38, 0f, 0.01f, "Voltage
		// Measurement - DC_PRES (5V_DC Off)"));
		// sequence.add(new VoltageTest(activity, ioio, 38, 3f, 0.3f, true,
		// false, false, false, "Voltage Measurement - DC_PRES (5V_DC On)"));
		// sequence.add(new LedCheckTest(activity, "Pink", "Pink LED Check"));
//		 sequence.add(new Charge_termination_test(activity, ioio, "Battery
//		 Charge Termination Test"));
		// sequence.add(new VoltageTest(activity, ioio, 37, 2f, 0.2f, true,
		// true, false, null, "Voltage Measurement - Sleep Mode (BAT_MON)"));
		// //sequence.add(new UploadFirmwareTest(activity, ioio));
		// sequence.add(new VoltageTest(activity, ioio, 40, 3.0f, 0.1f, false,
		// null, true, false, "Voltage Measurement - Sleep Mode (V_3V0) "));
		// sequence.add(new VoltageTest(activity, ioio, 39, 0f, 0.1f, "Voltage
		// Measurement - Sleep Mode (V_3V0_SW)"));
		// sequence.add(new VoltageTest(activity, ioio, 44, 0f, 0.1f, "Voltage
		// Measurement - Sleep Mode (V_1V8)"));
		// sequence.add(new VoltageTest(activity, ioio, 32, 0f, 0.1f, "Voltage
		// Measurement - Sleep Mode (V_REF_AN)"));
		// //sequence.add(new GetDeviceSerialTest(activity, ioio));
		// //sequence.add(new WakeDeviceTest(activity, ioio));
		// sequence.add(new LedCheckTest(activity, "Green", "Green LED Check"));
		// sequence.add(new AwakeModeCurrentTest(activity, ioio, "Current
		// Measurement - Awake Mode"));
		// sequence.add(new VoltageTest(activity, ioio, 40, 3f, 0.01f, "Voltage
		// Measurement - Awake Mode (V_3V0)"));
		// sequence.add(new VoltageTest(activity, ioio, 39, 3f, 0.1f, "Voltage
		// Measurement - Awake Mode (V_3V0_SW)"));
		// sequence.add(new VoltageTest(activity, ioio, 44, 1.8f, 0.1f, "Voltage
		// Measurement - Awake Mode (V_1V8)"));
		// sequence.add(new VoltageTest(activity, ioio, 33, 3f, 0.01f, "Voltage
		// Measurement - Awake Mode (V_BT)"));
		// sequence.add(new VoltageTest(activity, ioio, 32, 1.5f, 0.2f, "Voltage
		// Measurement - Awake Mode (V_REF_AN)"));
		// sequence.add(new BluetoothDiscoverableModeTestForTesting(activity));

		// DIAG TESTS
//		sequence.add(new UartLoopbackTest(activity, ioio));

		// NEW TESTS
//		sequence.add(new GetBarcodeTest(activity,ioio,job));

		sequence.add(new CurrentTest(activity, ioio, 42, Current.Units.uA, false, 100f, 0f,
				"Current Measurement - UUT Unprogrammed"));

		sequence.add(new VoltageTest(activity, ioio, 44, Voltage.Units.V, true, 3.1f, 0.2f,
				"Voltage Measurement - V_3V1"));
		sequence.add(new VoltageTest(activity, ioio, 40, Voltage.Units.V, true, 1.8f, 0.2f,
				"Voltage Measurement - V_1V8"));
		sequence.add(new VoltageTest(activity, ioio, 38, Voltage.Units.V, true, 0f, 0.1f,
				"Voltage Measurement - DC_PRES (5V_DC Off)"));
		sequence.add(new SetDigitalOutputStep(activity, IOIOUtils.Outputs._5V_DC.getValue(), false,
				"Set 5VDC to On"));
		sequence.add(new VoltageTest(activity, ioio, 38, Voltage.Units.V, true, 1.6f, 0.1f,
				"Voltage Measurement - DC_PRES (5V_DC On)"));
		sequence.add(new SetDigitalOutputStep(activity, IOIOUtils.Outputs._5V_DC.getValue(), true,
				"Set 5VDC to Off"));
//		sequence.add(new ChargingTest(activity, ioio,
//				"Battery Charging Test"));

//		sequence.add(new ChargeLedCheckTest(activity, ioio, "Pink", "Pink LED Check"));
		sequence.add(new UploadFirmwareTest(activity, ioio));

		sequence.add(new GetDeviceSerialTest(activity, ioio));
		sequence.add(new AccelerometerSelfTest(activity, ioio));

//		sequence.add(new VoltageTest(activity, ioio, 39, Voltage.Units.V, true, 0f, 0.1f,
//				"Voltage Measurement - Sleep Mode (V_1V8_SW)"));
//		sequence.add(new VoltageTest(activity, ioio, 33, Voltage.Units.V, true, 0f, 0.1f,
//				"Voltage Measurement - Sleep Mode (V_BT)"));

		sequence.add(new MagnetWakeDeviceTest(activity, ioio));

		sequence.add(new CurrentTest(activity, ioio, 42, Current.Units.mA, true, 30f, 0.2f,
				"Current Measurement - Awake"));
		sequence.add(new VoltageTest(activity, ioio, 44, Voltage.Units.V, true, 3.1f, 0.1f,
				"Voltage Measurement - Awake Mode (V_3V1)"));
		sequence.add(new VoltageTest(activity, ioio, 39, Voltage.Units.V, true, 1.8f, 0.1f,
				"Voltage Measurement - Awake Mode (V_1V8_SW)"));
		sequence.add(new VoltageTest(activity, ioio, 33, Voltage.Units.V, true, 3.3f, 0.1f,
				"Voltage Measurement - Awake Mode (V_BT)"));
		sequence.add(new VoltageTest(activity, ioio, 32, Voltage.Units.V, -1f, true,  -1.4f, 0.1f,
				"Voltage Measurement - Awake Mode (V_REF_AN)"));
		sequence.add(new VoltageTest(activity, ioio, 41, Voltage.Units.V, -1f, true, -0f, 0.2f,
				"Voltage Measurement - Awake Mode (V_ZERO_AN)"));

//		sequence.add(new VoltageTest(activity, ioio, 31, true, -6.0f, 0.2f,
//				"Voltage Measurement - Awake Mode (-6V_RAIL)"));


		sequence.add(new BluetoothConnectTestForTesting(activity));
		sequence.add(new LedCheckTest(activity, "Green", "Green LED Check"));
		sequence.add(new LedCheckTest(activity, "Blue", "Blue LED Check"));
		sequence.add(new CurrentTest(activity, ioio, 42, Current.Units.mA, true, 35f, 0.2f,
				"Current Measurement - BT Connected"));
//		sequence.add(new VoltageTest(activity, ioio, 32, Voltage.Units.V, true, -1f, -1.4f, 0.1f,
//				"Voltage Measurement - BT Connected Mode (V_REF_AN)"));

		sequence.add(new ReadDeviceInfoSerialNumberTest(activity));
		sequence.add(new ReadModelNumberTest(activity));
		sequence.add(new ReadFirmwareversionTest(activity));

		sequence.add(new BatteryLevelUUTVoltageTest(activity, 15, 0.5f,
				"Battery Level - UUT voltage @ 3.5V", 100));
		sequence.add(new BatteryLevelUUTVoltageTest(activity, 85, 0.15f,
				"Battery Level - UUT voltage @ 4.1V", 15));

		sequence.add(new SetSensorVoltagesStep(activity, (short)0, (short)0,  "Set GAIN -> 0, ZERO -> 0"));
		sequence.add(new VoltageTest(activity, ioio, 32, Voltage.Units.V, -2f,  true, 0f, 0.1f,
				"Voltage Measurement(V_REF_AN)"));
		sequence.add(new SetSensorVoltagesStep(activity, (short)30, (short)0,  "Set GAIN -> 30, ZERO -> 0"));
		sequence.add(new VoltageTest(activity, ioio, 32, Voltage.Units.V, -2f,  true, -0.3f, 0.1f,
				"Voltage Measurement(V_REF_AN)"));
		sequence.add(new SetSensorVoltagesStep(activity, (short)0, (short)30,  "Set GAIN -> 0, ZERO -> 30"));
		sequence.add(new VoltageTest(activity, ioio, 32, Voltage.Units.V, -2f, true, -0.3f, 0.1f,
				"Voltage Measurement(V_REF_AN)"));
		sequence.add(new SetSensorVoltagesStep(activity, (short)255, (short)0, "Set GAIN -> 255, ZERO -> 0"));
		sequence.add(new VoltageTest(activity, ioio, 32, Voltage.Units.V, -2f, true, -3f, 0.1f,
				"Voltage Measurement(V_REF_AN)"));
		sequence.add(new SetSensorVoltagesStep(activity, (short)0, (short)255, "Set GAIN -> 0, ZERO -> 255"));
		sequence.add(new VoltageTest(activity, ioio, 32, Voltage.Units.V, -2f, true, -3f, 0.1f,
				"Voltage Measurement(V_REF_AN)"));
		sequence.add(new SetSensorVoltagesStep(activity, (short)255, (short)255, "Set GAIN -> 255, ZERO -> 255"));
		sequence.add(new VoltageTest(activity, ioio, 32, Voltage.Units.V, -2f, true, -6f, 0.1f,
				"Voltage Measurement(V_REF_AN)"));
		sequence.add(new SetSensorVoltagesStep(activity, (short)127, (short)0, "Set GAIN -> 127, ZERO -> 0"));

		sequence.add(new SensorTestWrapper(false, activity, ioio,
				"Sensor Input Test, NO LOAD, GAIN @ 127", 0, false, (short) 127));
		sequence.add(new SensorTestWrapper(false, activity, ioio,
				"Sensor Input Test, LOADED, GAIN @ 127", 1, true, (short) 127));
		sequence.add(new SensorTestWrapper(false, activity, ioio,
				"Sensor Input Test, LOADED, GAIN @ 25", 1, true, (short) 25));
		sequence.add(new SensorTestWrapper(false, activity, ioio,
				"Sensor Input Test, LOADED, GAIN @ 230", 1, true, (short) 230));
//		sequence.add(new SensorTestWrapper(false, activity, ioio, "Sensor Input Test, LOADED, GAIN @ 127", 2, true,
//				(short) 127));
//		sequence.add(new SensorTestWrapper(false, activity, ioio, "Sensor Input Test, LOADED, GAIN @ 127", 3, true,
//				(short) 127));

	}
	@Override
	public boolean isLog() {
		return log;
	}

	@Override
	public void setLog(boolean log) {
		this.log = log;
	}

	@Override
	public void addTest(Test test) {
		// TODO Auto-generated method stub
		
	}
	
}
