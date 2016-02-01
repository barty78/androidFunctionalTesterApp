package com.pietrantuono.tests.implementations;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.tests.superclass.Test;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

public class MagnetWakeDeviceTest extends Test{
	private AnalogInput V_3V0_SW;

	public MagnetWakeDeviceTest(Activity activity, IOIO ioio) {
		super(activity, ioio, "Wake Device", false, true, 0, 0, 0);
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.DOWN);
		if(IOIOUtils.getUtils().getEmag() != null) {
			IOIOUtils.getUtils().toggleEMag((Activity)activityListener);
		}
		if(isinterrupted)return;
		try {
			Thread.sleep(1 * 1000);
		} catch (Exception e) {
			getListener().addFailOrPass(true, false, "ERROR", "App Fault");

		}
		if(isinterrupted)return;

		try {
			Voltage.Result result = Voltage.checkVoltage(ioio, 39, 1f, true, 1.8f, 0.1f );
			setValue(result.getReadingValue());
			Log.d(TAG, "Result isSuccess = " + result.isSuccess());
            if (result.isSuccess()){
				Success();
				activityListener.startPCBSleepMonitor();
				activityListener.addFailOrPass(true, true, "",  description);
			} else {
				activityListener.addFailOrPass(true, false, "",  description);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try {V_3V0_SW.close();}catch(Exception e){};
	}
}
