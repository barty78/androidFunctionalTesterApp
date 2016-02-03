package hydrix.pfmat.generic;

import junit.framework.TestCase;

import org.mockito.Mockito;

import java.io.InputStream;
import java.io.OutputStream;

import hydrix.pfmat.generic.mocks.DeviceImplementation;

/**
 * Created by mauriziopietrantuono
 */
public class ConnectTests extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception { }

    public void testConnectNull() throws Exception {
        DeviceImplementation device= new DeviceImplementation(null,null);
        device.connect();
    }

    public void testConnectNullIsConnected() throws Exception {
        DeviceImplementation device= new DeviceImplementation(null,null);
        device.connect();
        assertTrue(!device.isConnected());//Not connected because streams are null
    }
    public void testConnectNotNullIsConnected() throws Exception {
        InputStream inputStream= Mockito.mock(InputStream.class);
        OutputStream outputStream=Mockito.mock(OutputStream.class);
        DeviceImplementation device= new DeviceImplementation(inputStream,outputStream);
        device.connect();
        assertTrue(device.isConnected());//Connected because streams are not null
    }
}