package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class BigPacket extends Packet {
    private static final int BIG_FRAME_SIZE = 12;

    protected BigPacket(byte packetType) {
        super(packetType);
    }

    @Override
    public byte[] toStream() {
        // Get the packet-specific payload
        byte[] payload = buildPayload();
        // Allocate byte buffer for entire packet, wrap it in a ByteBuffer, and specify network byte order for contents
        int packetSize = BIG_FRAME_SIZE;
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

    // Deserialization
    @Override
    public final boolean fromStream(byte[] stream, int streamOffset, int packetLength)
    {
        // Empty payload is valid, but at minimum we need all frame header fields
        if (packetLength < BIG_FRAME_SIZE)
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
        int expectedPacketSize = BIG_FRAME_SIZE + payloadLength;
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
}
