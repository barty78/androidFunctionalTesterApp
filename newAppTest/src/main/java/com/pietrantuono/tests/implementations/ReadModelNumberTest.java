package com.pietrantuono.tests.implementations;
import android.app.Activity;
import android.app.AlertDialog;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.tests.superclass.Test;

public class ReadModelNumberTest extends Test{
	private String modelnumber;
	public String getModelnumber() {
		return modelnumber;
	}

	/**
	 * IMPORTANT: Bluetooth must be open using
	 * {@link com.pietrantuono.tests.implementations.BluetoothConnectTest} 
	 * Do not execute this test before opening Bluetooth
	 */
	public ReadModelNumberTest(Activity activity) {
		super(activity, null, "Model Number Check", false, false, 0, 0, 0);
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		BTUtility btUtility=activityListener.getBtutility();
		if(btUtility==null) {report("BTUtility is null");return;}
		try {
			modelnumber = btUtility.getModelNumber();
		} catch (Exception e) {
		}
		if (modelnumber == null) {
			activityListener.addFailOrPass(true, false, "NULL", description);
		}
		else {
			if (modelnumber.equals(PeriCoachTestApplication.getGetFirmware().getModel())){
				Success();
				activityListener.addFailOrPass(true, true, modelnumber, description);
			} else {
				activityListener.addFailOrPass(true, false , modelnumber, description);
			}
		}
	}
	@Override
	public void interrupt() {
		super.interrupt();
	}
}
