package hydrix.pfmat.generic.receivetests;

import junit.framework.TestCase;

import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import hydrix.pfmat.generic.mocks.DeviceImplementation;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * Created by mauriziopietrantuono
 */
public class ReceivePacketsTests extends TestCase {

    private boolean success;

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception { }

    public void testReadResponse() throws Exception {
        int size=16;
        final byte[] response =new byte[1024];
        response[0]=(byte)1;                //Magic number
        response[1]=0x20;                   //Packet type RX_SENSOR_DATA
        int paylodLength=size-6;            //FRAME_SIZE=6
        response[2]=(byte)paylodLength;     //Payload length
        short checksum=CRC.crc16CCITT(response,0,3+paylodLength);//Checksum
        response[3+paylodLength]=(byte)checksum;
        InputStream spyInputStream= new InputStream() {
            @Override
            public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
                success =true;
                for(int i=0;i<response.length;i++){buffer[i]=response[i];}
                return response.length;
            }

            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        OutputStream outputStream=Mockito.mock(OutputStream.class);
        DeviceImplementation device= new DeviceImplementation(spyInputStream,outputStream);
        device.connect();
        Thread.sleep(20*60*1000);
        assertTrue(success);
    }

}