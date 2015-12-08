package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketTx_Sleep extends Packet
{
	// Fields
		protected short mWaitTime;
		
	// Construction
	public PacketTx_Sleep(short waitTime)
	{
		super(PFMAT.TX_SLEEP);
		mWaitTime = waitTime;
		
	}
	
	// Accessors
		public final short getWaitTime() {return mWaitTime;}
		
		// Serialization
		@Override
		protected byte[] buildPayload()
		{
			// Payload is 2 bytes... 2 bytes wait time
			byte[] payload = new byte [2];
			ByteBuffer buf = ByteBuffer.wrap(payload);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.putShort(mWaitTime);
			return payload;
		}
}
