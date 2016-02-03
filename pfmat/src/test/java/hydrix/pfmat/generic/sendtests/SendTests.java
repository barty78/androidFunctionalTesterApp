package hydrix.pfmat.generic.sendtests;

import junit.framework.TestCase;

import java.io.InputStream;
import java.io.OutputStream;

import hydrix.pfmat.generic.mocks.DeviceImplementation;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by mauriziopietrantuono .
 */
public class SendTests extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception { }

    public void testConnect() throws Exception {
        InputStream inputStream= mock(InputStream.class);
        OutputStream outputStream= mock(OutputStream.class);
        DeviceImplementation device= new DeviceImplementation(inputStream,outputStream);
        device.connect();
        Thread.sleep(200);//Lag for rec thread to start
        verify(outputStream, times(1)).write(any(byte[].class));//1 time, only connection (default requests device details)
    }

    public void testGetBatteryStatus() throws Exception {
        InputStream inputStream= mock(InputStream.class);
        OutputStream outputStream= mock(OutputStream.class);
        DeviceImplementation device= new DeviceImplementation(inputStream,outputStream);
        device.connect();
        Thread.sleep(200);
        device.sendGetBatteryStatus();
        verify(outputStream, times(2)).write(any(byte[].class));//2 times: device details + batt status
    }

    public void testSendGetSensorData() throws Exception {
        InputStream inputStream= mock(InputStream.class);
        OutputStream outputStream= mock(OutputStream.class);
        DeviceImplementation device= new DeviceImplementation(inputStream,outputStream);
        device.connect();
        Thread.sleep(200);
        device.sendGetSensorData(0);
        verify(outputStream, times(2)).write(any(byte[].class));//Sending timestamp as well
    }
    public void testSendGetDeviceDetails() throws Exception {
        InputStream inputStream= mock(InputStream.class);
        OutputStream outputStream= mock(OutputStream.class);
        DeviceImplementation device= new DeviceImplementation(inputStream,outputStream);
        device.connect();
        Thread.sleep(200);
        device.sendGetDeviceDetails();
        verify(outputStream, times(2)).write(any(byte[].class));
    }
}