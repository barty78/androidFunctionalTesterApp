package com.pietrantuono.pericoachengineering.util;




import java.lang.reflect.Method;











import com.pietrantuono.pericoach.newtestapp.R;






import analytica.pericoach.android.ClosedTestResult;
import analytica.pericoach.android.DBManager;
import android.app.Activity;
import android.app.AlertDialog;



import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;






import android.util.Log;




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
