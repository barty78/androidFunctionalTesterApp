package com.pietrantuono.tests.implementations;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;

import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.pericoach.newtestapp.BuildConfig;
import com.pietrantuono.tests.superclass.Test;
public class BluetoothConnectTestForTesting extends Test {
	private BTUtility btUtility;
	private String mac = "";
	private String serial = "";

	public BluetoothConnectTestForTesting(Activity activity) {
		super(activity, null, "Bluetooth Connect", false, true, 0, 0, 0);
	}
	@Override
	public void execute() {
		new BluetoothConnectTestForTestingAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try{btUtility.abort();}
		catch (Exception e){}
	}

	class BluetoothConnectTestForTestingAsyncTask extends AsyncTask<Void,Void,Void>{
		@Override
		protected Void doInBackground(Void... params) {
			if(isinterrupted)return null;

			if (BuildConfig.DEBUG) {
				switch (BuildConfig.FLAVOR) {
					case "peter":
						serial = activityListener.getSerial();
						mac = activityListener.getMac();
//					mac = "00:17:E9:C2:D9:94";
						break;
					case "maurizio":
						serial = "6707433948538265066CFF49";
						mac = "00:17:E9:C0:82:EE";
						break;
				}
				btUtility = new BTUtility((Activity) activityListener,
						serial,
						activityListener, mac);

			} else {
				btUtility = new BTUtility((Activity) activityListener,
						activityListener.getSerial(), activityListener,
						activityListener.getMac());

			}
			activityListener.setBtutility(btUtility);
			btUtility.connectProbeViaBT(BluetoothConnectTestForTesting.this);
			return null;
		}
	}
}
