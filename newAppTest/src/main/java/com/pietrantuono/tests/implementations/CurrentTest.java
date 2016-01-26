package com.pietrantuono.tests.implementations;
import ioio.lib.api.IOIO;
import android.app.Activity;
import android.util.Log;

import com.pietrantuono.ioioutils.Current;
import com.pietrantuono.ioioutils.Units;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;

import static com.pietrantuono.ioioutils.Current.*;

public class CurrentTest extends Test {
	private int pinNumber;
	private Boolean isNominal;
	private int gain = 50;
	private int Rshunt;
	private @Units
	int units;

	/**
	 * * Current Measurement Test Implementation
	 * 
	 * @param activity			- Activity Instance
	 * @param ioio				- IOIO Instance
	 * @param pinNumber			- IOIO Pin Number
	 * @param units				- Measurement Scale (nA, uA or mA)
	 * @param isNominal			- Applied Limits Type (Bounds / Nominal,Precision)
	 * @param limitParam1		- Limit Parameter 1 (Upper / Nominal)
	 * @param limitParam2		- Limit Parameter 2 (Lower / Precision)
	 * @param description		- Test Description
	 */
	public CurrentTest(Activity activity, IOIO ioio, int pinNumber, @Units int units, Boolean isNominal, float limitParam1, float limitParam2, String description) {
		super(activity, ioio, description, false, true, limitParam1, limitParam2, 0);
		this.pinNumber = pinNumber;
		Log.d(TAG, String.valueOf(units));
		this.units = units;
		this.isNominal = isNominal;
		this.description=description;
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		Log.d(TAG, "Test Starting: " + description);
//		if (!IOIOUtils.getUtils().setBattVoltage(ioio, 34, 2f, 3.7f)){
//			getListener().addFailOrPass(true, false, "Fixture Fault - Battery Voltage Setpoint not reached", testToBeParsed);
//			return;
//		}
//		byte[] writebyte = new byte[] { 0x00, (byte) 100 }; // Value of 210 =
//		byte[] readbyte = new byte[] {};
//		if (IOIOUtils.getUtils().getMaster() != null)
//			try {
//				IOIOUtils.getUtils().getMaster().writeRead(0x60, false, writebyte, writebyte.length,
//						readbyte, readbyte.length);
//			} catch (Exception e1) {
//				report(e1);
//				activityListener.addFailOrPass(true, false, "ERROR", "Fixture Fault",testToBeParsed);
//				return;
//			}
		Result result = null;

		switch(units){
			case Units.mA:
				Rshunt = 2;

				break;
			case Units.uA:
				Rshunt = 1002;
				break;
		}

		try {
			result =
					checkCurrent(ioio, pinNumber, gain, Rshunt, units, isNominal, limitParam1, limitParam2);
		} catch (Exception e) {
			report(e);
		}
		setValue(result.getReadingValue());
		if (activityListener == null
				|| ((Activity) activityListener).isFinishing())
			return;
		if (result == null) {
			activityListener.addFailOrPass(true, false, "ERROR", description,testToBeParsed);
			return;
		}
		if (result.isSuccess()) {
			Success();
			activityListener.addFailOrPass(true, true, result.getReading(),description,testToBeParsed);
		} else {
			activityListener
					.addFailOrPass(true, false, result.getReading(),description);
		}
		
		// If we are reading uA (Sleep) current, change back to mA range ready for next test step.
		if (units == Units.uA) {
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
