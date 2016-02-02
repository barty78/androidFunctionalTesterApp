package hydrix.pfmat.generic;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketRx_BatteryStatus extends Packet
{
	private final byte MIN_BATTERY_PERCENT = 0;
	private final byte MAX_BATTERY_PERCENT = 100;
	
	// Fields
	private byte mBatteryPercent;
	
	// Construction
	public PacketRx_BatteryStatus()
	{
		super(PFMAT.RX_BATTERY_STATUS);
	}
	
	// Accessors
	public final byte getBatteryPercent() {return mBatteryPercent;}
	
	// Serialization
	@Override
	protected boolean parsePayload(byte[] payload)
		{
		// We expect exactly 1 byte payload (8-bit battery percentage)
		if ((payload == null) || (payload.length != 1))
			return false;
		
		// Wrap in ByteBuffer and specify byte order
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		// Grab the values
		mBatteryPercent = buf.get();
		if (mBatteryPercent < MIN_BATTERY_PERCENT || mBatteryPercent > MAX_BATTERY_PERCENT)
			return false;
		
		// Sweet
		return true;
		}
}
