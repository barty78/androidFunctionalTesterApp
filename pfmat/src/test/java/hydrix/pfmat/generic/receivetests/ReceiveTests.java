package hydrix.pfmat.generic.receivetests;

import junit.framework.TestCase;

import org.mockito.Mockito;

import java.io.InputStream;
import java.io.OutputStream;

import hydrix.pfmat.generic.mocks.DeviceImplementation;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by mauriziopietrantuono
 */
public class ReceiveTests extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception { }


    public void testRead() throws Exception {
        InputStream inputStream= Mockito.mock(InputStream.class);
        OutputStream outputStream=Mockito.mock(OutputStream.class);
        DeviceImplementation device= new DeviceImplementation(inputStream,outputStream);
        device.connect();
        Thread.sleep(200);
        verify(inputStream).read(any(byte[].class));
    }
    public void testReadResponse() throws Exception {
        InputStream inputStream= Mockito.mock(InputStream.class);
        final byte[] response = new byte[1024];
        OutputStream outputStream=Mockito.mock(OutputStream.class);
        DeviceImplementation device= new DeviceImplementation(inputStream,outputStream);
        device.connect();
        Thread.sleep(200);
        verify(inputStream).read(response);
    }

    public void testReadResponseLitmus() throws Exception {
        InputStream inputStream= Mockito.mock(InputStream.class);
        final byte[] response = new byte[1024];
        OutputStream outputStream=Mockito.mock(OutputStream.class);
        DeviceImplementation device= new DeviceImplementation(inputStream,outputStream);
        device.connect();
        Thread.sleep(200);
        final byte[] litmus = new byte[1024];
        for(int i=0;i<1024;i++)litmus[i]=1;
        verify(inputStream,times(0)).read(litmus);
    }
}