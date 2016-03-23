package com.pietrantuono.sensors;

import hydrix.pfmat.generic.Device;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.pietrantuono.pericoach.newtestapp.R;

public class NewPFMATDevice
{
	// Members
	private static NewDevice mDevice = null;
	private static ConnectTask task=null;
	

	// Bluetooth comms
	public static final void specifyDevice(BluetoothDevice device)
	{
		// Clean up any previous device state
		disconnect();
		
		// Create a new device instance
		mDevice = new NewBTDevice(device);
	}
	/*
	// TCP comms
	public static final void specifyDevice(String ipAddress, short port)
	{
		// Clean up any previous device state
		disconnect();
		
		// Create a new device instance
		mDevice = new TCPDevice(ipAddress, port);
	}
	*/
	public static NewDevice getDevice() {return mDevice;}
	
	public static void connect(Context context, String successIntent, String failIntent)
	{
		// Device must have been previously specified!
		if (mDevice == null)
		{
			// Tell the user
			final AlertDialog errorBox = new AlertDialog.Builder(context).create();
			errorBox.setCancelable(false);
			errorBox.setMessage(context.getString(R.string.btinfo_connection_failed_no_device));
			errorBox.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			((Activity)context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					errorBox.show();
				}
			});
			
			// Notify of failed connection
			context.sendBroadcast(new Intent(failIntent));
			return;
		}
		
	

		
		// Attempt the connection, then return result to caller
		task = new ConnectTask(context, successIntent, failIntent);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,(Void[])null);
	}
	
	public static void disconnect()
	{	
		if (mDevice != null)mDevice.disconnect();
		if(task!=null)try {task.cancel(true);}catch(Exception e){e.printStackTrace();}
		
	}
	
	private static class ConnectTask extends AsyncTask<Void, Void, Boolean>
	{
		static private final long DEVICE_INFO_TIMEOUT_MS = 3000;
		@Override
		protected void onCancelled(Boolean result) {
			super.onCancelled(result);
			if(mHourglass!=null && mHourglass.isShowing())mHourglass.dismiss();
		}
		private final Context mContext;
		private final String mSuccessIntent;
		private final String mFailIntent;
		private ProgressDialog mHourglass = null;
		private ConnectTask(Context context, String successIntent, String failIntent)
			{
			mContext = context;
			mSuccessIntent = successIntent;
			mFailIntent = failIntent;
		}
		protected void onPreExecute()
			{	
			if(mContext==null)return;
			if(isCancelled())return ;
			// Display an hourglass
				((Activity)mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mHourglass = new ProgressDialog(mContext);
						mHourglass.setMessage(mContext.getString(R.string.btinfo_connecting));
						mHourglass.setIndeterminate(true);
						mHourglass.setCancelable(false);
						mHourglass.show();

					}
				});
		}

    	protected Boolean doInBackground(Void... args)
    	{	if(isCancelled())return false;
    		// Connect the device
    		if (!mDevice.connect())
    			return false;
    		
    		// Connected... but wait until we see device information / battery information before we let the user proceed with the connection
    		boolean seenInfo = false;
    		long connectTimeMS = System.currentTimeMillis();
    		while (!seenInfo && (System.currentTimeMillis() - connectTimeMS < DEVICE_INFO_TIMEOUT_MS) && !isCancelled())
    		{	if(isCancelled())return false;
    			// Let's make our determination on both firmware version and battery percent - both need to be seen from device
    			NewDevice.Information info = mDevice.getInformation();
    			if ((info != null) && (info.mBatteryPercent != Device.BATTERY_UNKNOWN) && (info.mFirmwareVersion != Device.FIRMWARE_VERSION_UNKNOWN))
    				seenInfo = true;
    			else
    			{	
    				// Haven't received the info yet, have a wee breather then try again soon
    				try {Thread.sleep(100);} catch (InterruptedException e) {}        				
    			}
    			if(isCancelled())return false;
    		}
    		
    		// If we didn't see the info then undo the connection and fail, even though the connection itself was successful
    		if (!seenInfo)
    		{
    			mDevice.disconnect();
    			return false;
    		}
    		if(isCancelled())return false;
    		// Connected and have seen device information and battery status packets
    		return true;
    	}
    	protected void onPostExecute(Boolean result)
    	{
    		// Hide the hourglass
    		if(isCancelled()){
    			mHourglass.dismiss();
    			return;
    		}
			((Activity)mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mHourglass.dismiss();
				}
			});


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
				if(!isCancelled())manager.notify(1, builder.build());
    			
    			
    			// Notify of failed connection
    			if(!isCancelled())mContext.sendBroadcast(new Intent(mFailIntent));
    		}
    		// Notify of successful connection
    		else
    			if(!isCancelled())mContext.sendBroadcast(new Intent(mSuccessIntent));
    	}
	}
	
 }
