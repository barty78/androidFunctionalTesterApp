package com.pietrantuono.tests.implementations;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;
import ioio.lib.api.IOIO;
public class AccelerometerSelfTest extends Test {
	private static ExecutorService executor = Executors.newFixedThreadPool(1);
	private int retries = 0;
	private AlertDialog alertDialog;
	private String TAG=getClass().getSimpleName();
	public AccelerometerSelfTest(Activity activity, IOIO ioio) {
		super(activity, ioio, "Accelerometer Self-Test", false, false, 0, 0, 0);
	}
	@Override
	public void execute() {
		IOIOUtils.getUtils().modeApplication((Activity)activityListener);
		if(isinterrupted)return;
		Log.d(TAG, TAG+" "+IOIOUtils.getUtils().getUartLog().toString());
		if (IOIOUtils.getUtils().getUartLog().indexOf("MPU6500 Self-Test Passed!") != -1) {
			Success();
			activityListener.addFailOrPass(true, true, "");
			return;
		} else {
			if (retries >= 3){
				Log.d(TAG, TAG+" "+IOIOUtils.getUtils().getUartLog().toString());
				setSuccess(false);
				activityListener.addFailOrPass(true, false, "");
				return;
			}
			onAttemptFailed();
		}
		return;
	}
	private void onAttemptFailed() {
		final Activity activity=(Activity)activityListener;
		if(activity==null || activity.isFinishing())return;
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, "Accelerometer Self-Test Failed, " + String.valueOf(3 - retries) + " Attempts Remaining",
						Toast.LENGTH_SHORT).show();
				retries++;
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				execute();
			}
		});
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try{alertDialog.dismiss();}catch (Exception e){e.printStackTrace();}
		try{executor.shutdownNow();}catch (Exception e){e.printStackTrace();}
	}
}
