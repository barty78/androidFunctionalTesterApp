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
	private Boolean scaled = false;
	private float scaling;
	private Boolean writetoDC5 = false;
	private Boolean whatToWriteToDC5 = null;
	private Boolean writeToreset = false;
	private Boolean whatToWriteToreset = null;

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
		this.limit = limit;
		this.precision = precision;
		this.description=description;
	}

	/**
	 * @param activity			- Activity Instance
	 * @param ioio				- IOIO Instance
	 * @param pinNumber			- IOIO Pin Number
	 * @param scaled			- Enable scaling
	 * @param scaling			- Scaling factor
	 * @param limit				- Nominal Voltage
	 * @param precision			- Voltage Precision
	 * @param description		- Test Description
	 */

	public VoltageTest(Activity activity,IOIO ioio, int pinNumber, Boolean scaled, float scaling, float limit, float precision, String description) {
		super(activity,ioio,description, false, true);
		this.pinNumber = pinNumber;
		this.scaled = scaled;
		this.scaling = scaling;
		this.limit = limit;
		this.precision = precision;
		this.description=description;
	}

	/**
	 * @param activity				- Activity Instance
	 * @param ioio					- IOIO Instance
	 * @param pinNumber				- IOIO Pin Number
	 * @param limit					- Nominal Voltage
	 * @param precision				- Voltage Precision
	 * @param writetoDC5
	 * @param whatToWriteToDC5
	 * @param writeToreset
	 * @param whatToWriteToreset
	 * @param description			- Test Description
	 */

	public VoltageTest(Activity activity,IOIO ioio,int pinNumber, float limit, float precision,
			Boolean writetoDC5, Boolean whatToWriteToDC5,Boolean writeToreset,Boolean whatToWriteToreset, String description) {
		super(activity,ioio,description, false, true);
		this.pinNumber = pinNumber;
		this.limit = limit;
		this.precision = precision;
		this.writetoDC5 = writetoDC5;
		this.whatToWriteToDC5 = whatToWriteToDC5;
		this.description=description;
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		Log.d(TAG, "Test Starting: " + description);

		if (writetoDC5) {
			try {
				IOIOUtils.getUtils().get_5V_DC().write(whatToWriteToDC5);
			} catch (Exception e) {
				report(e);
				activityListener.addFailOrPass(true,false, "Fixture Fault");
				return;
			}
		}
		if (writeToreset) {
			try {
				IOIOUtils.getUtils().getReset().write(whatToWriteToreset);
			} catch (Exception e) {
				report(e);
				activityListener.addFailOrPass(true,false, "Fixture Fault");
				return;
			}
		}
		
		Voltage.Result result = null;

		try {
			Thread.sleep(1 * 1000);
		} catch (Exception e) {
			activityListener.addFailOrPass(true,false, description);
			report(e);
		}

		try {
			result = Voltage
					.checkVoltage(ioio, pinNumber, scaled, scaling, limit, precision);
		} catch (Exception e) {
			report(e);
		}

		try {
			IOIOUtils.getUtils().get_5V_DC().write(true);
		} catch (Exception e) {
			report(e);
		}

		IOIOUtils.getUtils().toggleTrigger((Activity) activityListener);

		if (activityListener == null
				|| ((Activity) activityListener).isFinishing())
			return;
		if (result == null) {
			activityListener.addFailOrPass(true, false, "ERROR", description);
			//activityListener.goAndExecuteNextTest();
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