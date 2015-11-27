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


	public static void updateDbOpenTest(DBManager db, String mDeviceId,
			String mFirmwareVer, String mJobNo, int mTestType, String scancode,
			Activity activity, ClosedTestResult result) {

		Integer dev_id;
		// SessionRunner session = SessionRunner.getActiveSession();
		try {
			// If device doesn't exist in deviceRecords table, insert it.
			if (db.deviceExists(mDeviceId) == 0) {
				Log.d("SQL:", "DEVICE DOESN'T EXIST - INSERTING");
				dev_id = db.insertDeviceRecord(mDeviceId, null, mFirmwareVer);

				if (dev_id != -1) {
					// Just inserted device into deviceRecords table, so we
					// have the index already. Just insert it.
					// session.mTestResult.setDevId(dev_id);
					result.setDevId(dev_id);
					db.insertTestRecord(mJobNo, mTestType, scancode, result);
				}

			} else {
				Log.d("SQL:", "DEVICE ALREADY EXISTS");
				// Get the device index from deviceRecords table first, then
				// insert
				result.setDevId(db.deviceRecordIndex(mDeviceId));
				Log.d("FINAL RESULT: ", result.getResult().toString());

				db.insertTestRecord(mJobNo, mTestType, scancode, result);
			}
		} catch (Exception e) {
			AlertDialog errorBox = new AlertDialog.Builder(activity).create();
			errorBox.setCancelable(false);
			errorBox.setMessage(activity
					.getString(R.string.error_failed_to_save_file));
			errorBox.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			errorBox.show();
		}
		// Inform the user if we failed

	}


	
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
