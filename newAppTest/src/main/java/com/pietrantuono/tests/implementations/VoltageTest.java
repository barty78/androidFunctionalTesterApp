package com.pietrantuono.tests.implementations;
import ioio.lib.api.IOIO;

import android.app.Activity;
import android.util.Log;

import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.ioioutils.Voltage.Units;
import com.pietrantuono.tests.superclass.Test;
public 	class VoltageTest extends Test {
	private int pinNumber;
	private boolean isNominal = true;
	private float scaling;
	private Voltage.Units units;
	public void Units() {
	}

	/**
	 * @param activity			- Activity Instance
	 * @param ioio				- IOIO Instance
	 * @param pinNumber			- IOIO Pin Number
   	 * @param units				- Measurement Scale (V or mV)
	 * @param isNominal			- Applied Limits Type (Bounds / Nominal,Precision)
	 * @param limitParam1		- Limit Parameter 1 (Upper / Nominal)
	 * @param limitParam2		- Limit Parameter 2 (Lower / Precision)
	 * @param description		- Test Description
	 */

	public VoltageTest(Activity activity,IOIO ioio, int pinNumber, Units units, boolean isNominal, float limitParam1, float limitParam2, String description) {
		super(activity,ioio,description, false, true, limitParam1, limitParam2, 0);
		this.pinNumber = pinNumber;
		this.units = units;
		this.scaling = 1f;
		this.isNominal = isNominal;
		this.description=description;
	}

	/**
	 * @param activity			- Activity Instance
	 * @param ioio				- IOIO Instance
	 * @param pinNumber			- IOIO Pin Number
	 * @param scaling			- Scaling factor
	 * @param isNominal			- Applied Limits Type (Bounds / Nominal,Precision)
	 * @param limitParam1		- Limit Parameter 1 (Upper / Nominal)
	 * @param limitParam2		- Limit Parameter 2 (Lower / Precision)
	 * @param description		- Test Description
	 */

	public VoltageTest(Activity activity,IOIO ioio, int pinNumber, Units units, float scaling, boolean isNominal, float limitParam1, float limitParam2, String description) {
		super(activity,ioio,description, false, true, 0, 0, 0);
		this.pinNumber = pinNumber;
		this.units = units;
		this.scaling = scaling;
		this.isNominal = isNominal;
		this.limitParam1 = limitParam1;
		this.limitParam2 = limitParam2;
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
					.checkVoltage(ioio, pinNumber, scaling, isNominal, limitParam1, limitParam2);
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