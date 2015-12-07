package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketRx_Sleep extends Packet
{
	// Fields
	private short mWaitTime = PFMAT.VOLTAGE_FAILED;
	
	// Construction
	public PacketRx_Sleep()
	{
		super(PFMAT.RX_SLEEP);
	}
	
	// Accessors
	public final boolean sleepFailed() {return (mWaitTime == PFMAT.SLEEP_FAILED);}

	
	// Serialization
	@Override
	protected boolean parsePayload(byte[] payload)
	{
		// We expect exactly 2 byte payload (short waitTime)
		if ((payload == null) || (payload.length != 2))
			return false;
		
		// Wrap in ByteBuffer and specify byte order
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		// Grab the values
		mWaitTime = buf.getShort();
		if ((mWaitTime != PFMAT.SLEEP_FAILED) && (mWaitTime < -PFMAT.MAX_SLEEP_VALUE || mWaitTime > PFMAT.MAX_SLEEP_VALUE))
			return false;
		
		// Sweet
		return true;
	}
}