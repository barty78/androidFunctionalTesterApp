package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class BigPacket extends Packet {


    protected BigPacket(byte packetType) {
        super(packetType);
        FRAME_SIZE=12;
    }

    @Override
    public byte[] toStream() {
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
}
