package com.pietrantuono.tests.implementations;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

import android.app.Activity;
import android.util.Log;

import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.tests.superclass.Test;
public 	class VoltageTest extends Test {
	private int pinNumber;
	private float limit;
	private float precision;
	private float scaling;

	/**
	 * @param activity			- Activity Instance
	 * @param ioio				- IOIO Instance
	 * @param pinNumber			- IOIO Pin Number
	 * @param limit				- Nominal Voltage
	 * @param precision			- Voltage Precision
	 * @param description		- Test Description
	 */

	public VoltageTest(Activity activity,IOIO ioio, int pinNumber, float limit, float precision, String description) {
		super(activity,ioio,description, false, true);
		this.pinNumber = pinNumber;
		this.scaling = 1f;
		this.limit = limit;
		this.precision = precision;
		this.description=description;
	}

	/**
	 * @param activity			- Activity Instance
	 * @param ioio				- IOIO Instance
	 * @param pinNumber			- IOIO Pin Number
	 * @param scaling			- Scaling factor
	 * @param limit				- Nominal Voltage
	 * @param precision			- Voltage Precision
	 * @param description		- Test Description
	 */

	public VoltageTest(Activity activity,IOIO ioio, int pinNumber, Boolean isBlocking, float scaling, float limit, float precision, String description) {
		super(activity,ioio,description, false, true);
		this.isBlockingTest = isBlocking;
		this.pinNumber = pinNumber;
		this.scaling = scaling;
		this.limit = limit;
		this.precision = precision;
		this.description=description;
	}

	@Override
	public void execute() {
		if(isinterrupted)return;
		Log.d(TAG, "Test Starting: " + description);
		
		Voltage.Result result = null;

		try {
			Thread.sleep(1 * 1000);
		} catch (Exception e) {
			activityListener.addFailOrPass(true,false, description);
			report(e);
		}

		try {
			result = Voltage
					.checkVoltage(ioio, pinNumber, scaling, limit, precision);
		} catch (Exception e) {
			report(e);
		}

		if (activityListener == null
				|| ((Activity) activityListener).isFinishing())
			return;
		if (result == null) {
			activityListener.addFailOrPass(true, false, "ERROR", description);
			return;
		}
		if (result.isSuccess()) {
			Success();
			activityListener.addFailOrPass(true, true, result.getReading(), description);
		} else {
			activityListener.addFailOrPass(true, false, result.getReading(), description);
		}
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try {Voltage.interrupt();}catch(Exception e){}
	}
}