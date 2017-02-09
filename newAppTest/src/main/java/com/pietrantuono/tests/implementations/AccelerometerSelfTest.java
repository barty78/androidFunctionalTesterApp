package com.pietrantuono.tests.implementations;


import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.pericoach.newtestapp.BuildConfig;
import com.pietrantuono.tests.superclass.Test;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import ioio.lib.api.IOIO;
public class AccelerometerSelfTest extends Test {
	private int retries = 0;
	private final String TAG=getClass().getSimpleName();
	public AccelerometerSelfTest(Activity activity, IOIO ioio) {
		super(activity, ioio, "Accelerometer Self-TEST", false, false, 0, 0, 0);
	}
	@Override
	public void execute() {
		Executed();
		new AccelerometerSelfTestAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private class AccelerometerSelfTestAsyncTask extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			if (IOIOUtils.getUtils().getUutMode(getActivity()) == IOIOUtils.Mode.bootloader) {
				IOIOUtils.getUtils().modeApplication((Activity) activityListener);
			}
			if(isinterrupted)return null;
			Log.d(TAG, TAG+" "+IOIOUtils.getUtils().getUartLog().toString());
			if (IOIOUtils.getUtils().getUartLog().indexOf("MPU6500 Self-Test Passed!") != -1) {
				Success();
				activityListener.addFailOrPass(true, true, "", description);
				return null;
			} else {
				if (retries >= 3){
					Log.d(TAG, TAG+" "+IOIOUtils.getUtils().getUartLog().toString());
					setSuccess(false);
					activityListener.addFailOrPass(true, false, "", description);
					return null;
				}
				onAttemptFailed();
			}
			return null;
		}
	}

	private void onAttemptFailed() {
		final Activity activity=(Activity)activityListener;
		if(activity==null || activity.isFinishing())return;
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (BuildConfig.DEBUG) {
					Toast.makeText(activity, "Accelerometer Self-Test Failed, " + String.valueOf(3 - retries) + " Attempts Remaining",
							Toast.LENGTH_SHORT).show();
				}
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

}
