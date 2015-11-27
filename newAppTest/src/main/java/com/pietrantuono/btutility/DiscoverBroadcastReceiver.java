package com.pietrantuono.btutility;


import java.util.concurrent.Callable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DiscoverBroadcastReceiver extends BroadcastReceiver {
	private String serial;
	private Boolean found;
	private static ExecutorService executor = Executors.newFixedThreadPool(1);
	private static final String TAG = "DiscoverBroadcastReceiver";
	private Future<Boolean> future;

	public DiscoverBroadcastReceiver(String serial)
			throws OuuuuuchSerialNotValidException {
		if (serial == null || serial.isEmpty()) {
			throw new OuuuuuchSerialNotValidException();
			// return contains;
		}
		this.serial = serial;
		found = false;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		// When discovery finds a device
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			// float a=1/0;
			found = true;
			
			// Get the BluetoothDevice object from the Intent
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			Log.e(TAG, "found!!!! "+device.getName());
			// Add the name and address to an array adapter to show in a
			// ListView
			if (device.getName().toLowerCase().contains(serial.toLowerCase()))
				found = true;

		}
	}

	public Boolean containsSerial() {
		Callable<Boolean> getfound = new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {

				while (!found && !Thread.currentThread().isInterrupted()) {
					if (Thread.currentThread().isInterrupted())	return false;
					Thread.sleep(1000);
					Log.d(TAG, "sleep");
				}
				Log.e(TAG, "returning found");
				return found;

			}

		};

		future = executor.submit(getfound);

		try {
			found = future.get( 60 * 1000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		future.cancel(true);
		return found;
	}

	@SuppressWarnings("serial")
	private class OuuuuuchSerialNotValidException extends Exception {

		@Override
		public String toString() {
			return "Ouuch!!!!! That hurts! Serial is null or empty";
		}

	}
	

}
