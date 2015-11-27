package com.pietrantuono.tests.implementations;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import ioio.lib.api.IOIO;
import android.app.Activity;

import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.tests.superclass.Test;
public class AwakeModeCurrentTest extends Test {
	@Override
	public void execute() {
		if(isinterrupted)return;
		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.DOWN);
		byte[] writebyte = new byte[] { 0x00, (byte) 210 }; // Value of 210 =
															// 3.3v
		byte[] readbyte = new byte[] {};
		if (IOIOUtils.getUtils().getMaster() != null)
			try {
				IOIOUtils.getUtils().getMaster().writeRead(0x60, false, writebyte, writebyte.length,
						readbyte, readbyte.length);
			} catch (Exception e1) {
				report(e1);
				activityListener.addFailOrPass(true, true, e1.toString());
				return;
			}
		float average = 0;
		try {
			average = ((Voltage.getVoltage(ioio, 42, 20) / (50 * 2)) * (float) 1e3);
		} catch (Exception e) {
			report(e);
			activityListener.addFailOrPass(true, true, e.toString());
			return;
		}
		float precisionfactor = 0.1f;
		if (30 * (1 - precisionfactor) < average
				&& average < 30 * (1 + precisionfactor)) {
			setSuccess(true);
			setValue(average);
			activityListener.addFailOrPass(true, true, df.format(average) + "mA",description);
		} else {
			setSuccess(false);
			setValue(average);
			activityListener.addFailOrPass(true, false, df.format(average) + "mA",description);
			//listner.setOverallFailure();
		}
		//activityListener.goAndExecuteNextTest();
	}
	public AwakeModeCurrentTest(Activity activity, IOIO ioio, String description) {
		super(activity, ioio, description, false, false);
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try {Voltage.interrupt();}catch (Exception e){;}
	}
}
