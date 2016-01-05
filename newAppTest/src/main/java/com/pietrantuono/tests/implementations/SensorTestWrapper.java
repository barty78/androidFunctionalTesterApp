package com.pietrantuono.tests.implementations;

import ioio.lib.api.IOIO;
import android.app.Activity;
import android.util.Log;

import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.sensors.ClosedTest;
import com.pietrantuono.sensors.SensorTest;
import com.pietrantuono.sensors.SensorsTestHelper;
import com.pietrantuono.tests.superclass.Test;

public class SensorTestWrapper extends Test {
	private int TestLimitIndex=0;
	private short voltage;
	private SensorTest sensorTest;
	private SensorsTestHelper helper;
	private Boolean load;

	/**
	 * Creates a sensor test.
	 * IMPORTANT: Bluetooth must be open using
	 * {@link com.pietrantuono.tests.implementations.BluetoothConnectTest} 
	 * Do not execute this test before opening Bluetooth
	 *  @param description  : Description of test, sensor test must contain words (NO LOAD or LOADED), and (GAIN @ x), where x is voltage from 0 to 255. Used as parameters for test.
	 *  @param TestLimitIndex : index of the test limit to be used in the test
	 * @param lowerLimit
	 * @param upperLimit
	 * @param varLimit
	 */
	public SensorTestWrapper(boolean isClosedTest, Activity activity, IOIO ioio, int TestLimitIndex, float lowerLimit, float upperLimit, float varLimit, String description) {
		super(activity, ioio, description, true, false, lowerLimit, upperLimit, varLimit);
		this.TestLimitIndex=TestLimitIndex;
		this.load = null;
		if(description.contains("LOADED")){
			this.load = true;
		} else if (description.contains("NO LOAD")) {
			this.load = false;
		}
		Log.d(TAG, "Sensor Load is " + this.load);
		this.voltage = -1;
		if (description != null && description.contains("GAIN @")){
			this.voltage = Short.valueOf(description.substring(description.indexOf("@") + 2, description.length()));
		}
		Log.d(TAG, "Sensor Voltage is " + this.voltage);

		if(isClosedTest){sensorTest=new ClosedTest(activity,SensorTestWrapper.this, lowerLimit, upperLimit, varLimit);}
		else {sensorTest=new SensorTest(activity,SensorTestWrapper.this, lowerLimit, upperLimit, varLimit);}
	}

	@Override
	public void execute() {
		if(isinterrupted)return;
		helper = new SensorsTestHelper((Activity)activityListener,activityListener.getBtutility(),ioio);
		sensorTest.setSensorsTestHelper(helper);
		IOIOUtils.getUtils().stopUartThread();
//		IOIOUtils.getUtils().closeUart((Activity)activityListener);
		sensorTest.execute();
	}


	public SensorTest getSensorTest() {
		return sensorTest;
	}


	@Override
	public void interrupt() {
		super.interrupt();
		try {sensorTest.stop();}catch (Exception e){}
		try {helper.stop();}catch (Exception e){}
	}


	@Override
	public boolean isSuccess() {
		return sensorTest.getOverallResult();
	}


	public short getVoltage() {
		return voltage;
	}

	public Boolean getLoad() {
		return load;
	}
}
