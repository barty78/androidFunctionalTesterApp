package com.pietrantuono.tests.implementations;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.tests.superclass.Test;

public class ReadFirmwareversionTest extends Test {
	private String firmwarever;
	private BTUtility btUtility;

	/**
	 * IMPORTANT: Bluetooth must be open using
	 * {@link com.pietrantuono.tests.implementations.BluetoothConnectTest} Do
	 * not execute this test before opening Bluetooth
	 */
	public ReadFirmwareversionTest(Activity activity) {
		super(activity, null, "Firmware Version Check", false, false, 0, 0, 0);
	}

	@Override
	public void execute() {
		if (isinterrupted)
			return;
		final Handler handler = new Handler();
		if (isinterrupted){
			activityListener.addFailOrPass("", true, false,
					description);
			return;}
		btUtility = activityListener.getBtutility();
		if (btUtility == null) {
			report("BTUtility is null");
			activityListener.addFailOrPass("", true, false,
					description);
			return;
		}
		try {
			firmwarever = btUtility.getFirmWareVersion();
		} catch (Exception e) {
		}
		if (firmwarever == null) {
			activityListener.addFailOrPass(true, false, "NULL",
					description);
		}

		else {
			if (firmwarever.equals(PeriCoachTestApplication.getGetFirmware().getVersion())) {
				Success();
				activityListener.addFailOrPass(true, true, firmwarever, description);
			} else {
				activityListener.addFailOrPass(true, false, firmwarever, description);
			}
		}

	}

	public String getVersion() {
		return firmwarever;
	}


	@Override
	public void interrupt() {
		super.interrupt();
	}
}