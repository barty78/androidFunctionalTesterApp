package analytica.pericoach.android;

import hydrix.pfmat.generic.Device;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class TCPDevice extends Device
{
	// Members
	private final String mServerIPAddress;
	private final short mServerPort;
	private Socket mSocket = null;

	// Construction
	@SuppressWarnings("unused")
	public TCPDevice(String ipAddress, short port)
	{
		// Cache these details for later use in connectSpecific
		mServerIPAddress = ipAddress;
		mServerPort = port;
	}
	
	@Override
	protected boolean connectSpecific()
	{
		// Make sure we were created with a remote address
		if (mServerIPAddress == null || mServerPort == 0)
			return false;
		try
		{
			// TCP connect
			InetSocketAddress addr = new InetSocketAddress(mServerIPAddress, mServerPort);
			mSocket = new Socket();
			mSocket.connect(addr, 5000);
		}
		catch (IOException e)
		{
			e.printStackTrace();
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
			}
			catch (IOException e)
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
		return "TCPTestVersion";
	}
	
	@Override
	protected void onConnectionLost()
	{
		// Don't worry about implementing this, as TCP isn't production comms
	}
}
