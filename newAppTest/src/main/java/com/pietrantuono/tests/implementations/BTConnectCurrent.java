package com.pietrantuono.tests.implementations;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import ioio.lib.api.IOIO;
import android.app.Activity;

import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.tests.superclass.Test;
public class BTConnectCurrent extends Test {
	private float average = 0;
	public BTConnectCurrent(Activity activity, IOIO ioio) {
		super(activity, ioio, "Current Measurement - Bluetooth Connected", false, false, 0, 0, 0);
		setIdTest(12);
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.DOWN);
		try {
			average = ((Voltage.getVoltage(ioio, 42, 20,100) / (50 * 2)) * (float) 1e3);
		} catch (Exception e) {
			report(e);
			activityListener.addFailOrPass(true, false, e.toString());
			return;
		}
		setValue(average);
		float precisionfactor = 0.1f;
		if (30 * (1 - precisionfactor) < average
				&& average < 30 * (1 + precisionfactor)) {
			setSuccess(true);
			activityListener.addFailOrPass(true, true, df.format(average) + "mA",description);
		} else {
			setSuccess(false);
			activityListener.addFailOrPass(true, false, df.format(average) + "mA",description);
		}
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try{Voltage.interrupt();}catch(Exception e){}
	}
}
