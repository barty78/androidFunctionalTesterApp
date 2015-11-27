package com.pietrantuono.tests.implementations;

import java.util.ArrayList;

import hydrix.pfmat.generic.TestLimits;
import ioio.lib.api.IOIO;
import android.app.Activity;

import com.pietrantuono.constants.LimitsProvider;
import com.pietrantuono.sensors.ClosedTest;
import com.pietrantuono.sensors.SensorTest;
import com.pietrantuono.sensors.SensorsTestHelper;
import com.pietrantuono.tests.superclass.Test;

public class SensorTestWrapper extends Test {
	private int TestLimitIndex=0;
	private Boolean isLoad;
	private short voltage;
	private SensorTest sensorTest;
	private SensorsTestHelper helper;
	private ArrayList<TestLimits> limits;
	/**
	 * Creates a sensor test.
	 * IMPORTANT: Bluetooth must be open using
	 * {@link com.pietrantuono.tests.implementations.BluetoothConnectTest} 
	 * Do not execute this test before opening Bluetooth
	 * 
	 * @param TestLimitIndex: index of the test limit to be used in the test 
	 */
	public SensorTestWrapper(boolean isClosedTest,Activity activity, IOIO ioio, String description, int TestLimitIndex, Boolean isload, short voltage) {
		super(activity, ioio, description, true, false);
		this.TestLimitIndex=TestLimitIndex;
		this.isLoad=isload;
		this.voltage=voltage;
		limits=LimitsProvider.getTestLimits();
		if(isClosedTest){sensorTest=new ClosedTest(activity,SensorTestWrapper.this);}
		else {sensorTest=new SensorTest(activity,SensorTestWrapper.this);}
	}


	@Override
	public void execute() {
		if(isinterrupted)return;
		helper = new SensorsTestHelper((Activity)activityListener,activityListener.getBtutility(),ioio);
		sensorTest.setSensorsTestHelper(helper);
		sensorTest.setLoad(this.isLoad).setTestLimits(limits.get(TestLimitIndex)).setVoltage(this.voltage);
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
	
	
}
