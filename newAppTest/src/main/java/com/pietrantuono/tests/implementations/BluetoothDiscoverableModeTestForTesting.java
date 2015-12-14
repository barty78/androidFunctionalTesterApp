package com.pietrantuono.tests.implementations;
import ioio.lib.api.exception.ConnectionLostException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import com.pietrantuono.btutility.DiscoverBroadcastReceiver;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;
import com.radiusnetworks.bluetooth.BluetoothCrashResolver;
public class BluetoothDiscoverableModeTestForTesting extends Test {
	private BluetoothAdapter adapter;
	private DiscoverBroadcastReceiver discoverBroadcastReceiver;
	private ProgressDialog dialog;
	private BluetoothCrashResolver bluetoothCrashResolver;
	private DiscoveryAsyncTask asyncTask;
	public BluetoothDiscoverableModeTestForTesting(Activity activity) {
		super(activity, null, "Check Bluetooth discover", false, false, 0, 0, 0);
		setIdTest(10);
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		try {
			IOIOUtils.getUtils().getSensor_Low().write(false);
		} catch (ConnectionLostException e) {
			report(e);
		}
		adapter = BluetoothAdapter.getDefaultAdapter();
		try {
			discoverBroadcastReceiver = new DiscoverBroadcastReceiver("6707433948538265066CFF49");//TODO put activityListener.getSerial() or "0" for testing
		} catch (Exception e) {
			report(e);
			activityListener.addFailOrPass(true,false, description);
			return;
		}
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		((Activity)activityListener).registerReceiver(discoverBroadcastReceiver, filter);
		if (adapter.isDiscovering()) {
			Log.e(TAG, "Discovery was already ongoing!");
			adapter.cancelDiscovery();
		}
		dialog = new ProgressDialog((Activity)activityListener);
		dialog.setTitle("Checking discoverable mode");
		dialog.setMessage("Checking...");
		((Activity)activityListener).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dialog.show();
			}
		});
		bluetoothCrashResolver = new BluetoothCrashResolver(((Activity)activityListener));
		bluetoothCrashResolver.start();
		adapter.startDiscovery();
		asyncTask = new DiscoveryAsyncTask();
		asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try{dialog.dismiss();} catch (Exception e){}
		try{adapter.cancelDiscovery();} catch (Exception e){}
		try{asyncTask.cancel(true);} catch (Exception e){}
		try{	
			((Activity)activityListener).unregisterReceiver(discoverBroadcastReceiver);
		} catch (Exception e){}
	}
	class DiscoveryAsyncTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			if(isinterrupted)return false;
			Boolean ok = discoverBroadcastReceiver.containsSerial();
			return ok;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			adapter.cancelDiscovery();
			dialog.dismiss();
			if(isinterrupted)return;
			setSuccess(result);
			((Activity)activityListener).unregisterReceiver(discoverBroadcastReceiver);
			activityListener.addFailOrPass(true, result,description);
			try {
				IOIOUtils.getUtils().getSensor_Low().write(true);
			} catch (ConnectionLostException e) {
				report(e);
			}
		}
	}
}
