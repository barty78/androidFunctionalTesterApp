package analytica.pericoach.android;

import java.lang.reflect.Method;

import com.pietrantuono.pericoach.newtestapp.R;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.content.Intent;
import hydrix.pfmat.generic.Device;

@SuppressWarnings("unused")
class PFMATDevice
{
	// Members
	private static Device mDevice = null;

	// Bluetooth comms
	public static final void specifyDevice(BluetoothDevice device)
	{
		// Clean up any previous device state
		disconnect();
		
		// Create a new device instance
		mDevice = new BTDevice(device);
	}
	
	// TCP comms
	public static final void specifyDevice(String ipAddress, short port)
	{
		// Clean up any previous device state
		disconnect();
		
		// Create a new device instance
		mDevice = new TCPDevice(ipAddress, port);
	}
	
	public static Device getDevice() {return mDevice;}
	
	public static void connect(Context context, String successIntent, String failIntent)
	{
		// Device must have been previously specified!
		if (mDevice == null)
		{
			// Tell the user
			AlertDialog errorBox = new AlertDialog.Builder(context).create();
			errorBox.setCancelable(false);
			errorBox.setMessage(context.getString(R.string.btinfo_connection_failed_no_device));
			errorBox.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()	{public void onClick(DialogInterface dialog, int which) {dialog.dismiss();}});
			errorBox.show();
			
			// Notify of failed connection
			context.sendBroadcast(new Intent(failIntent));
			return;
		}
		
		@SuppressWarnings("unused")
		final class ConnectTask extends AsyncTask<Void, Void, Boolean>
		{
			static private final long DEVICE_INFO_TIMEOUT_MS = 3000;
			final Context mContext;
			final String mSuccessIntent;
			final String mFailIntent;
			ProgressDialog mHourglass = null;
			public ConnectTask(Context context, String successIntent, String failIntent)
			{
				mContext = context;
				mSuccessIntent = successIntent;
				mFailIntent = failIntent;
			}
			protected void onPreExecute()
			{
				// Display an hourglass
				mHourglass = new ProgressDialog(mContext);
				mHourglass.setMessage(mContext.getString(R.string.btinfo_connecting));
				mHourglass.setIndeterminate(true);
				mHourglass.setCancelable(false);
				mHourglass.show();
			}
        	protected Boolean doInBackground(Void... args)
        	{
        		// Connect the device
        		if (!mDevice.connect())
        			return false;
        		
        		// Connected... but wait until we see device information / battery information before we let the user proceed with the connection
        		boolean seenInfo = false;
        		long connectTimeMS = System.currentTimeMillis();
        		while (!seenInfo && (System.currentTimeMillis() - connectTimeMS < DEVICE_INFO_TIMEOUT_MS))
        		{
        			// Let's make our determination on both firmware version and battery percent - both need to be seen from device
        			Device.Information info = mDevice.getInformation();
        			if ((info != null) && (info.mBatteryPercent != Device.BATTERY_UNKNOWN) && (info.mFirmwareVersion != Device.FIRMWARE_VERSION_UNKNOWN))
        				seenInfo = true;
        			else
        			{
        				// Haven't received the info yet, have a wee breather then try again soon
        				try {Thread.sleep(100);} catch (InterruptedException e) {}        				
        			}
        		}
        		
        		// If we didn't see the info then undo the connection and fail, even though the connection itself was successful
        		if (!seenInfo)
        		{
        			mDevice.disconnect();
        			return false;
        		}

        		// Connected and have seen device information and battery status packets
        		return true;
        	}
        	protected void onPostExecute(Boolean result)
        	{
        		// Hide the hourglass
        		mHourglass.dismiss();

        		// Inform the user if we failed
        		if (!result)
        		{	
        			/*
        			AlertDialog errorBox = new AlertDialog.Builder(mContext).create();
        			errorBox.setCancelable(false);
        			errorBox.setMessage(mContext.getString(R.string.btinfo_connection_failed));
        			errorBox.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()	{public void onClick(DialogInterface dialog, int which) {dialog.dismiss();}});
        			errorBox.show();
					*/
        			NotificationCompat.Builder builder = new NotificationCompat.Builder(
        					mContext)
    				.setSmallIcon(
    						R.drawable.icon)
    						.setContentTitle(mContext.getString(R.string.btinfo_connection_title))
    						.setContentText(mContext.getString(R.string.btinfo_connection_content));
    				builder.setTicker(mContext.getString(R.string.btinfo_connection_ticker));
    				PendingIntent contentIntent = PendingIntent.getActivity(
    						mContext, 0, new Intent(),
    						PendingIntent.FLAG_UPDATE_CURRENT);
    				builder.setDefaults(Notification.DEFAULT_ALL);
    				builder.setContentIntent(contentIntent);
    				NotificationManager manager = (NotificationManager) mContext
    						.getSystemService(Context.NOTIFICATION_SERVICE);
    				manager.notify(1, builder.build());
        			
        			
        			// Notify of failed connection
        			mContext.sendBroadcast(new Intent(mFailIntent));
        		}
        		// Notify of successful connection
        		else
        			mContext.sendBroadcast(new Intent(mSuccessIntent));
        	}
		}

		
		// Attempt the connection, then return result to caller
		ConnectTask task = new ConnectTask(context, successIntent, failIntent);
		task.execute((Void[])null);
	}
	
	public static void disconnect()
	{
		if (mDevice != null)
			mDevice.disconnect();
	}
	
 }
