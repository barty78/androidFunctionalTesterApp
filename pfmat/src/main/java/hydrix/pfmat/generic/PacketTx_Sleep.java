package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketTx_Sleep extends Packet
{
	// Fields
	private short mWaitTime;
	private byte mMode;
		
	// Construction
	public PacketTx_Sleep(byte mode, short waitTime)
	{
		super(PFMAT.TX_SLEEP);
		mWaitTime = waitTime;
		mMode = mode;
		
	}
	
	// Accessors
		public final short getWaitTime() {return mWaitTime;}
		public final byte getMode() {return mMode;}
		
		// Serialization
		@Override
		protected byte[] buildPayload()
		{
			// Payload is 3 bytes... 1 byte mode, 2 bytes wait time
			byte[] payload = new byte [3];
			ByteBuffer buf = ByteBuffer.wrap(payload);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.put(mMode);
			buf.putShort(mWaitTime);
			return payload;
		}
}
