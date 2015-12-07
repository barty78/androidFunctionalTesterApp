package hydrix.pfmat.generic;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketRx_DeviceDetails extends Packet
{
	private final int SERIAL_LENGTH = 25;
	private final int MODEL_LENGTH = 16;
	
	// Fields
	private String mSerialNumber;
	private String mModel;
	private int mFirmwareVersion;
	private float mSensorCoefficient0;
	private float mSensorCoefficient1;
	private float mSensorCoefficient2;
	
	// Construction
	public PacketRx_DeviceDetails()
	{
		super(PFMAT.RX_DEVICE_DETAILS);
	}
	
	// Accessors
	public final String getSerialNumber() {return mSerialNumber;}
	public final String getModel() {return mModel;}
	public final int getFirmwareVersion() {return mFirmwareVersion;}
	public final float getSensorCoefficient0() {return mSensorCoefficient0;}
	public final float getSensorCoefficient1() {return mSensorCoefficient1;}
	public final float getSensorCoefficient2() {return mSensorCoefficient2;}
	
	// Serialization
	@Override
	protected boolean parsePayload(byte[] payload)
		{
		int index;
		boolean terminated;
		
		// We expect exact payload size (fixed serial number, fixed model, 4 byte firmware version, 4 byte sensor coefficients x 3)
		if ((payload == null) || (payload.length != SERIAL_LENGTH + MODEL_LENGTH + 4))
			return false;
		
		// Wrap in ByteBuffer and specify byte order
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.LITTLE_ENDIAN);

		// Grab the ASCII serial number
		terminated = false;
		mSerialNumber = "";
		for (index = 0; index < SERIAL_LENGTH; index++)
		{
			byte ascii = buf.get();
			if (ascii == 0)
			{
				terminated = true;
				index++;
				break;
			}
			mSerialNumber = mSerialNumber + Character.toString((char)ascii);
		}
		if (!terminated)
			return false; // We didn't see null terminator
		while (index < SERIAL_LENGTH)
		{
			buf.get(); // Skip past any remaining width beyond the null terminator
			index++;
		}
		
		// Repeat for model
		terminated = false;
		mModel = "";
		for (index = 0; index < MODEL_LENGTH; index++)
		{
			byte ascii = buf.get();
			if (ascii == 0)
			{
				terminated = true;
				index++;
				break;
			}
			mModel = mModel + Character.toString((char)ascii);
		}
		if (!terminated)
			return false; // We didn't see null terminator
		while (index < MODEL_LENGTH)
		{
			buf.get(); // Skip past any remaining width beyond the null terminator
			index++;
		}
		
		// Version
		mFirmwareVersion = buf.getInt();
		
		//mSensorCoefficient0 = buf.getFloat();
		//mSensorCoefficient1 = buf.getFloat();
		//mSensorCoefficient2 = buf.getFloat();
		
		
		// Sweet
		return true;
		}
}
