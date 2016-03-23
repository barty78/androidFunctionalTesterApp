package com.pietrantuono.pericoachengineering.util;




import java.lang.reflect.Method;


import android.bluetooth.BluetoothDevice;


public class Utils {


	public static void unpairDevice(BluetoothDevice device) {
		try {
			Method m = device.getClass()
					.getMethod("removeBond", (Class[]) null);
			m.invoke(device, (Object[]) null);
		} catch (Exception e) {
			// Log.e(TAG, e.getMessage());
		}
	}
}
