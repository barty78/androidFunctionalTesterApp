package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketRx_SetAccelConfig extends Packet
{
	// Fields

		private short mConfigStatus = PFMAT.NACK;

		// Construction
		public PacketRx_SetAccelConfig()
		{
			super(PFMAT.RX_ACCEL_CONFIG);
		}
		
		// Accessors
		public final boolean ConfigFailed() {return (mConfigStatus == PFMAT.NACK);}
		
		// Serialization
		@Override
		protected boolean parsePayload(byte[] payload)
		{
			// We expect exactly 1 byte payload (byte sensor index, byte voltage)
			if ((payload == null) || (payload.length != 1))
				return false;
			
			// Wrap in ByteBuffer and specify byte order
			ByteBuffer buf = ByteBuffer.wrap(payload);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			
			// Grab the values
			mConfigStatus = buf.get();

			if (mConfigStatus != PFMAT.ACK) return false;
			
			// Sweet
			return true;
		}
}