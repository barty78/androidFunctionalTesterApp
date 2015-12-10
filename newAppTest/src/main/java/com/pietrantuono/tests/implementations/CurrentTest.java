package com.pietrantuono.tests.implementations;
import ioio.lib.api.IOIO;
import android.app.Activity;
import android.util.Log;

import com.pietrantuono.ioioutils.Current;
import com.pietrantuono.ioioutils.Current.Scale;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;

import static com.pietrantuono.ioioutils.Current.*;
import static com.pietrantuono.ioioutils.Current.Scale.*;

public class CurrentTest extends Test {
	private int pinNumber;
	private Boolean isUpperLower;
	private float limitParam1, limitParam2;
	private int gain = 50;
	private int Rshunt;
	private Scale scale;
	public void Scale() {
	}
	/**
	 * * Current Measurement Test Implementation
	 * 
	 * @param activity			- Activity Instance
	 * @param ioio				- IOIO Instance
	 * @param pinNumber			- IOIO Pin Number
	 * @param scale				- Measurement Scale (nA, uA or mA)
	 * @param isUpperLower		- Applied Limits Type (Bounds / Nominal,Precision)
	 * @param limitParam1		- Limit Parameter 1 (Upper / Nominal)
	 * @param limitParam2		- Limit Parameter 2 (Lower / Precision)
	 * @param description		- Test Description
	 */
	public CurrentTest(Activity activity, IOIO ioio, int pinNumber, Scale scale, Boolean isUpperLower, float limitParam1, float limitParam2, String description) {
		super(activity, ioio, description, false, true);
		this.pinNumber = pinNumber;
//		this.gain = gain;
//		this.Rshunt = Rshunt;
		this.scale = scale;
		this.isUpperLower = isUpperLower;
		this.limitParam1 = limitParam1;
		this.limitParam2 = limitParam2;
		this.scale = scale;
		this.description=description;
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		Log.d(TAG, "Test Starting: " + description);
		byte[] writebyte = new byte[] { 0x00, (byte) 100 }; // Value of 210 =
		byte[] readbyte = new byte[] {};
		if (IOIOUtils.getUtils().getMaster() != null)
			try {
				IOIOUtils.getUtils().getMaster().writeRead(0x60, false, writebyte, writebyte.length,
						readbyte, readbyte.length);
			} catch (Exception e1) {
				report(e1);
				activityListener.addFailOrPass(true, false, "ERROR", "Fixture Fault");
				return;
			}
		Result result = null;

		switch(scale){
			case mA:
				Rshunt = 2;
				break;
			case uA:
				Rshunt = 1002;
				break;
		}

		try {
			result =
					checkCurrent(ioio, pinNumber, gain, Rshunt, scale, isUpperLower, limitParam1, limitParam2);
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
			activityListener.addFailOrPass(true, true, result.getReading(),description);
		} else {
			activityListener
					.addFailOrPass(true, false, result.getReading(),description);
		}
		
		// If we are reading uA (Sleep) current, change back to mA range ready for next test step.
		if (scale == scale.uA) {
			IOIOUtils.getUtils().setIrange((Activity)activityListener, false);
		}
		IOIOUtils.getUtils().toggleTrigger((Activity)activityListener);
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try {Current.interrupt();}catch (Exception e){;}
	}
}
