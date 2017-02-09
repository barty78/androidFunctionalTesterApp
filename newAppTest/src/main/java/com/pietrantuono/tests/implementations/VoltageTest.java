package com.pietrantuono.tests.implementations;
import ioio.lib.api.IOIO;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.ioioutils.Units;
import com.pietrantuono.tests.superclass.Test;

public 	class VoltageTest extends Test {
	private final int pinNumber;
	private boolean isNominal = true;
	private final float scaling;

	/**
	 * @param activity			- Activity Instance
	 * @param ioio				- IOIO Instance
	 * @param pinNumber			- IOIO Pin Number
	 * @param units				- Measurement Scale (V or mV)
	 * @param isNominal			- Applied Limits Type (Bounds / Nominal,Precision)
	 * @param limitParam1		- Limit Parameter 1 (Upper / Nominal)
	 * @param limitParam2		- Limit Parameter 2 (Lower / Precision)
	 * @param description		- TEST Description
	 */

	public VoltageTest(Activity activity,IOIO ioio, int pinNumber, @Units int units, boolean isBlocking, boolean isNominal, float limitParam1, float limitParam2, String description) {
		super(activity,ioio,description, false, isBlocking, limitParam1, limitParam2, 0);
		this.pinNumber = pinNumber;
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
	 * @param description		- TEST Description
	 */

	public VoltageTest(Activity activity,IOIO ioio, int pinNumber, @Units int units, boolean isBlocking, float scaling, boolean isNominal, float limitParam1, float limitParam2, String description) {
		super(activity,ioio,description, false, isBlocking, limitParam1, limitParam2, 0);
		this.pinNumber = pinNumber;
		this.scaling = scaling;
		this.isNominal = isNominal;
		this.description=description;
	}

	@Override
	public void execute() {
		new VoltageTestAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void interrupt() {
		super.interrupt();
		Executed();
		try {Voltage.interrupt();}catch (Exception e){
		}
	}

	private class VoltageTestAsyncTask extends AsyncTask<Void,Void,Void> {
		@Override
		protected Void doInBackground(Void... params) {
			if (isinterrupted) return null;
			Log.d(TAG, "TEST Starting: " + description);
			if (pinNumber == 32) {
				String string = "V_REF_AN Voltage TEST (" + System.currentTimeMillis() + ")\n";
				IOIOUtils.getUtils().appendUartLog((Activity) activityListener, string.getBytes(), string.getBytes().length);
			}
			Voltage.Result result = null;
			try {
				Thread.sleep(1 * 1000);
			} catch (Exception e) {
				activityListener.addFailOrPass(true, false, description, testToBeParsed);
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
				return null;
			if (result == null) {
				activityListener.addFailOrPass(true, false, "ERROR", description, testToBeParsed);
				return null;
			}
			setValue(result.getReadingValue());
			if (pinNumber == 32) {
				String string = "V_REF_AN Measurement: " + result.getReadingValue() + "V\n";
				IOIOUtils.getUtils().appendUartLog((Activity) activityListener, string.getBytes(), string.getBytes().length);
			}
			if (result.isSuccess()) {
				Success();
				activityListener.addFailOrPass(true, true, result.getReading(), description, testToBeParsed);
			} else {
				activityListener.addFailOrPass(true, false, result.getReading(), description, testToBeParsed);
			}
			return null;
		}
	}
}