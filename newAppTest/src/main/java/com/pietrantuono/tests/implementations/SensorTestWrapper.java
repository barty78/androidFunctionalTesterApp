package com.pietrantuono.tests.implementations;

import ioio.lib.api.IOIO;
import android.app.Activity;

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
	 *  @param TestLimitIndex : index of the test limit to be used in the test
	 * @param lowerLimit
	 * @param upperLimit
	 * @param varLimit
	 */
	public SensorTestWrapper(boolean isClosedTest, Activity activity, IOIO ioio, String description, int TestLimitIndex, Boolean isload, short voltage, float lowerLimit, float upperLimit, float varLimit) {
		super(activity, ioio, description, true, false, lowerLimit, upperLimit, varLimit);
		this.TestLimitIndex=TestLimitIndex;
		this.load=isload;
		this.voltage=voltage;
		if(isClosedTest){sensorTest=new ClosedTest(activity,SensorTestWrapper.this, lowerLimit, upperLimit, varLimit);}
		else {sensorTest=new SensorTest(activity,SensorTestWrapper.this, lowerLimit, upperLimit, varLimit);}
	}

	@Override
	public void execute() {
		if(isinterrupted)return;
		helper = new SensorsTestHelper((Activity)activityListener,activityListener.getBtutility(),ioio);
		sensorTest.setSensorsTestHelper(helper);
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
