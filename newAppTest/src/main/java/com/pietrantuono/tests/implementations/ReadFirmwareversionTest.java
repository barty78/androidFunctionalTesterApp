package com.pietrantuono.tests.implementations;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.util.Log;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.tests.superclass.Test;

public class ReadFirmwareversionTest extends Test {
	private String firmwarever;
	private BTUtility btUtility;
	private AlertDialog alertDialog;

	/**
	 * IMPORTANT: Bluetooth must be open using
	 * {@link com.pietrantuono.tests.implementations.BluetoothConnectTest} Do
	 * not execute this test before opening Bluetooth
	 */
	public ReadFirmwareversionTest(Activity activity) {
		super(activity, null, "Firmware Version Check", false, false);
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
//			activityListener.addView("Firmware: ", "NOT AVAILABLE",false);
			activityListener.addFailOrPass(true, false, "NULL",
					description);
		}

		else {
			if (firmwarever.equals(PeriCoachTestApplication.getGetFirmware().getVersion())) {
				Success();
//				activityListener.addView("Firmware: ", firmwarever,false);
				activityListener.addFailOrPass(true, true, firmwarever, description);
			} else {
//				getListener().addView("Firmware: ", firmwarever,false);
				activityListener.addFailOrPass(true, false, firmwarever, description);
			}
//			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//			builder.setTitle("Please check firmware version");
//			builder.setMessage("Firmware version is: " + firmwarever);
//			builder.setPositiveButton("OK", new OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//
//					handler.post(new Runnable() {
//						@Override
//						public void run() {
//							Success();
//							activityListener.addView("Firmware: ", firmwarever,false);
//							activityListener.addFailOrPass(firmwarever, true,
//									true, description);
//						}
//					});
//
//				}
//			});
//			builder.setNegativeButton("Not OK", new OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					handler.post(new Runnable() {
//						@Override
//						public void run() {
//							getListener().addView("Firmware: ", firmwarever,false);
//							getListener().addFailOrPass(firmwarever, true,
//									false, description);
//						}
//					});
//
//				}
//			});
//			alertDialog=builder.create();
//			alertDialog.show();
		}
	}

	@Override
	public void interrupt() {
		super.interrupt();
		try {alertDialog.dismiss();}catch(Exception e){}
	}
}
