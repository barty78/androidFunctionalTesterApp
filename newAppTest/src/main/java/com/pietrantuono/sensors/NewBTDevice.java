package com.pietrantuono.sensors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

class NewBTDevice extends NewDevice
{
	private final int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	
	// Singleton instance for connected device
	private final static UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Well-known SPP UUID

	// Members
	private final BluetoothDevice mBTDevice;
	private BluetoothSocket mSocket = null;
	private Boolean stopped=false;

	// Construction
	NewBTDevice(BluetoothDevice btDevice)
	{
		// Cache these details for later use in connectSpecific
		super();
		mBTDevice = btDevice;
	}

	@SuppressWarnings("unused")
	public final BluetoothDevice getBluetoothDeviceInfo() {return mBTDevice;}
	
	@Override
	protected boolean connectSpecific()
	{
		// Make sure we were created with a BT device specified
		if (mBTDevice == null)
			return false;
		try
		{
			// Cancel any discovery that is currently in progress...
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			if (adapter == null)
				return false;
			adapter.cancelDiscovery();
			if(stopped) return false;
			// Create and connect socket with the known shared UUID
			if (currentapiVersion >= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){
				mSocket = mBTDevice.createInsecureRfcommSocketToServiceRecord(mUUID);
			} else{
				mSocket = mBTDevice.createRfcommSocketToServiceRecord(mUUID);
			}
			if(stopped) return false;
			mSocket.connect();
			if(stopped) return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (mSocket != null)
			{
				try	{mSocket.close();}
				catch (IOException closeEx) {}
			}
			mSocket = null;
			return false;
		}
		
		// Connected
		return true;
	}

	@Override
	protected void disconnectSpecific()
	{
		if (mSocket != null)
		{
			try
			{
				mSocket.close();
				unpairDevice(mBTDevice);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			mSocket = null;
		}
	}
	
	@Override
	protected InputStream getInputStream()
	{
		if (mSocket != null)
		{
			try
			{
				return mSocket.getInputStream();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	protected OutputStream getOutputStream()
	{
		if (mSocket != null)
		{
			try
			{
				return mSocket.getOutputStream();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	public String getDeviceId()
	{
		return mBTDevice.getAddress();
	}
	
	@Override
	protected void onConnectionLost()
	{
		// TODO: Implement this post-trial... here we'll broadcast an intent back to the UI thread, so it can call disconnect and let the user know that the connection has been lost
	}
	
	private static void unpairDevice(BluetoothDevice btDevice) {
	   	try {
	   	    Method m = btDevice.getClass()
	   	        .getMethod("removeBond", (Class[]) null);
	   	    m.invoke(btDevice, (Object[]) null);
	   	} catch (Exception e) {
	   	    //Log.e(TAG, e.getMessage());
	   	}
	}

	@Override
	public void stop() {
		stopped=true;		
	}
	
	
}
