package hydrix.pfmat.generic.mocks;

import java.io.InputStream;
import java.io.OutputStream;

import hydrix.pfmat.generic.Device;

/**
 * Created by mauriziopietrantuono
 */
public class DeviceImplementation extends Device {
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public DeviceImplementation(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    protected boolean connectSpecific() {
        return true;
    }

    @Override
    protected void disconnectSpecific() {  }

    @Override
    protected InputStream getInputStream() {
        return inputStream;
    }

    @Override
    protected OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public String getDeviceId() {
        return "deviceid";
    }

    @Override
    protected void onConnectionLost() {  }
}
