package hydrix.pfmat.generic;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketTx_GetAccelData extends Packet
{
	// Fields
	protected int mRequestTimestamp;

	// Construction
	public PacketTx_GetAccelData(int requestTimestamp)
	{
		super(PFMAT.TX_GET_ACCEL_DATA);
		mRequestTimestamp = requestTimestamp;
	}

	// Accessors
	public final int getRequestTimestamp() {return mRequestTimestamp;}

	// Serialization
	@Override
	protected byte[] buildPayload()
	{
		// Payload is simply the 32-bit request timestamp
		byte[] payload = new byte [5];
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(mRequestTimestamp);
		buf.put(PFMAT.SENSORS_AND_ACCEL);
		return payload;
	}
}
