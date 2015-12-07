package hydrix.pfmat.generic;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketTx_GetSensorData extends Packet
{
	// Fields
	protected int mRequestTimestamp;
	
	// Construction
	public PacketTx_GetSensorData(int requestTimestamp)
	{
		super(PFMAT.TX_GET_SENSOR_DATA);
		mRequestTimestamp = requestTimestamp;
	}

	// Accessors
	public final int getRequestTimestamp() {return mRequestTimestamp;}
	
	// Serialization
	@Override
	protected byte[] buildPayload()
	{
		// Payload is simply the 32-bit request timestamp
		byte[] payload = new byte [4];
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(mRequestTimestamp);
		return payload;
	}
}
