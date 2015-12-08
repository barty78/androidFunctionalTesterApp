package hydrix.pfmat.generic;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketRx_SensorData extends Packet
{
	// Fields
	private int mRequestTimestamp = 0;
	private short mSensor0 = 0;
	private short mSensor1 = 0;
	private short mSensor2 = 0;
	
	// Construction
	public PacketRx_SensorData()
	{
		super(PFMAT.RX_SENSOR_DATA);
	}
	
	// Accessors
	public final int getRequestTimestamp() {return mRequestTimestamp;}
	public final short getSensor0() {return mSensor0;}
	public final short getSensor1() {return mSensor1;}
	public final short getSensor2() {return mSensor2;}
	
	// Serialization
	@Override
	protected boolean parsePayload(byte[] payload)
		{
		// We expect exactly 10 byte payload (32-bit RequestTimestamp and 3 x 16-bit Sensor)
		if ((payload == null) || (payload.length != 10))
			return false;
		
		// Wrap in ByteBuffer and specify byte order
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		// Grab the values
		mRequestTimestamp = buf.getInt();
		mSensor0 = buf.getShort();
		if (mSensor0 < PFMAT.MIN_SENSOR_VALUE || mSensor0 > PFMAT.MAX_SENSOR_VALUE)
			return false;
		mSensor1 = buf.getShort();
		if (mSensor1 < PFMAT.MIN_SENSOR_VALUE || mSensor1 > PFMAT.MAX_SENSOR_VALUE)
			return false;
		mSensor2 = buf.getShort();
		if (mSensor2 < PFMAT.MIN_SENSOR_VALUE || mSensor2 > PFMAT.MAX_SENSOR_VALUE)
			return false;
		
		// Sweet
		return true;
		}
}
