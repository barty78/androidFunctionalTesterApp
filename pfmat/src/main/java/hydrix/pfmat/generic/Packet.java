package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Packet
{
	private static final int FRAME_SIZE = 6; // bytes
	private static final byte MAGIC_PREFIX = (byte)1; // ASCII SOH
	private static final byte MAGIC_SUFFIX = (byte)26; // ASCII EOF
	private byte mPacketType;
	
	// Return value for peekStream
	public static enum PeekResult {STREAM_CORRUPT, STREAM_INCOMPLETE, STREAM_COMPLETE};
	public static class PeekInfo
	{
		// Members
		private final PeekResult mResult;
		private final byte mPacketType;
		private final short mPacketSize;
		
		// Construction
		public PeekInfo(PeekResult result, byte packetType, short packetSize)
		{
			mResult = result;
			mPacketType = packetType;
			mPacketSize = packetSize;
		}
		
		// Accessors
		public final PeekResult getResult() {return mResult;}
		public final byte getPacketType() {return mPacketType;}
		public final short getPacketSize() {return mPacketSize;}
	}
	
	// Construction
	protected Packet(byte packetType)
	{
		mPacketType = packetType;
	}

	// Accessors
	public final byte getPacketType() {return mPacketType;}
	
	// Optional overrides (most packet types will only override one or the other depending on tx vs rx)
	protected byte[] buildPayload() {return null;}
	protected boolean parsePayload(byte[] payload) {return true;}
	
	// Serialization
	public final byte[] toStream()
	{
		// Get the packet-specific payload
		byte[] payload = buildPayload();
		
		// Allocate byte buffer for entire packet, wrap it in a ByteBuffer, and specify network byte order for contents
		int packetSize = FRAME_SIZE;
		if (payload != null)
			packetSize += payload.length;
		byte[] packet = new byte [packetSize];
		ByteBuffer buf = ByteBuffer.wrap(packet);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		// Populate the frame header
		buf.put(MAGIC_PREFIX);
		buf.put(mPacketType);
		
		// Variable length payload
		if (payload == null || payload.length == 0)
			buf.put((byte)0);
		else
		{
			buf.put((byte)payload.length);
			buf.put(payload);
		}
		
		// CRC checksum
		int checksum = crc16CCITT(packet, 0, buf.position());
		buf.putShort((short)checksum);
		
		// Trailer magic
		buf.put(MAGIC_SUFFIX);
		return packet;
	}
	
	public static PeekInfo peekStream(byte[] stream, int streamOffset, int streamLength)
	{
		// Payload is optional but at minimum we need at least the frame header
		if (stream == null || streamLength < FRAME_SIZE)
			return new PeekInfo(PeekResult.STREAM_INCOMPLETE, (byte)0, (short)0);
		
		// Wrap in network byte order buffer
		ByteBuffer buf = ByteBuffer.wrap(stream, streamOffset, streamLength);
		buf.order(ByteOrder.LITTLE_ENDIAN);

		// Check for magic
		byte prefix = buf.get();
		if (prefix != MAGIC_PREFIX)
			return new PeekInfo(PeekResult.STREAM_CORRUPT, (byte)0, (short)0);
		
		// Next field is packet type.. we won't validate the value here, just return it
		byte packetType = buf.get();
	
		// Next is the payload length
		byte payloadLength = buf.get();
		if (payloadLength < 0)
			return new PeekInfo(PeekResult.STREAM_CORRUPT, (byte)0, (short)0);
		
		// Now that we know the payload size we can figure out the packet size
		short packetSize = (short)(FRAME_SIZE + payloadLength);
		
		// Return whether or not it's present in the stream in its entirety
		return new PeekInfo((streamLength >= packetSize) ? PeekResult.STREAM_COMPLETE : PeekResult.STREAM_INCOMPLETE,
				packetType, packetSize);
	}
	
	// Deserialization
	public final boolean fromStream(byte[] stream, int streamOffset, int packetLength)
	{
		// Empty payload is valid, but at minimum we need all frame header fields
		if (packetLength < FRAME_SIZE)
			return false;
		
		// Wrap in a network byte order ByteBuffer to access members
		ByteBuffer buf = ByteBuffer.wrap(stream, streamOffset, packetLength);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		// Check for magic
		byte prefix = buf.get();
		if (prefix != MAGIC_PREFIX)
			return false;
		
		// Next field is packet type.. make sure the caller created the right type of class!
		byte packetType = buf.get();
		if (packetType != mPacketType)
			return false;
		
		// Next is the payload length
		byte payloadLength = buf.get();
		
		// Now that we have the payload length we can do a precise expectation of the packet size
		int expectedPacketSize = FRAME_SIZE + payloadLength;
		if (packetLength != expectedPacketSize)
			return false;
		
		// Grab the payload if present
		if (payloadLength > 0)
		{
			byte[] payload = new byte [payloadLength];
			buf.get(payload, 0, payloadLength);
			
			// Packet-specific payload extraction
			if (!parsePayload(payload))
				return false;
		}
		
		// Calculate the expected checksum up to and including the payload
		// what's in the packet
		short expectedChecksum = crc16CCITT(stream, streamOffset, buf.position() - streamOffset);
		short checksum = buf.getShort();
		if (checksum != expectedChecksum)
			return false;
		
		// Finally we expect a trailer byte
		byte trailer = buf.get();
		if (trailer != MAGIC_SUFFIX)
			return false;
		
		// Packet and it's payload are valid, phew
		return true;
	}
	
	// CCITT CRC-16 calculation
	private static short crc16CCITT(byte[] bytes, int offset, int count)
	{
        int crc = 0xFFFF;          // initial value
        int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12)
        for (int index = 0; index < count; index++)
        {
        	byte b = bytes[offset + index];
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit)
                	crc ^= polynomial;
             }
        }
        return (short)(crc & 0xFFFF);
	}
}

