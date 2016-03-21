package com.pietrantuono.tests.implementations;
import android.app.Activity;
import android.os.AsyncTask;

import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.tests.superclass.Test;
public class BluetoothConnectTest extends Test {
	private BTUtility btUtility;
	public BluetoothConnectTest(Activity activity) {
		super(activity, null, "Bluetooth Connect", false, false, 0, 0, 0);
	}
	@Override
	public void execute() {
		new BluetoothConnectTestAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try{btUtility.abort();}
		catch (Exception e){}
	}

	class BluetoothConnectTestAsyncTask extends AsyncTask<Void,Void,Void>{
		@Override
		protected Void doInBackground(Void... params) {
			if(isinterrupted)return null;
			btUtility = new BTUtility((Activity)activityListener, activityListener.getSerial(), // TODO
					// change
					// serial
					// goes activityListener.getSerial()
					// here
					// ""
					// for
					// testing
					activityListener.getMac());
			activityListener.setBtutility(btUtility);
			btUtility.connectProbeViaBT(BluetoothConnectTest.this);

			return null;
		}
	}

}
