package hydrix.pfmat.generic.receivetests;

/**
 * Created by mauriziopietrantuono.
 */
public class CRC {
    public static short crc16CCITT(byte[] bytes, int offset, int count)
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
