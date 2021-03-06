package com.pietrantuono.sensors;

import android.util.Log;

import com.pietrantuono.tests.implementations.BatteryLevelUUTVoltageTest;

import hydrix.pfmat.generic.AllVoltageObserver;
import hydrix.pfmat.generic.CalibrationObserver;
import hydrix.pfmat.generic.DeviceRecvStream;
import hydrix.pfmat.generic.Motion;
import hydrix.pfmat.generic.PFMAT;
import hydrix.pfmat.generic.Packet;
import hydrix.pfmat.generic.PacketHandler;
import hydrix.pfmat.generic.PacketRx_AccelData;
import hydrix.pfmat.generic.PacketRx_BatteryStatus;
import hydrix.pfmat.generic.PacketRx_ConfigData;
import hydrix.pfmat.generic.PacketRx_Data;
import hydrix.pfmat.generic.PacketRx_DeviceDetails;
import hydrix.pfmat.generic.PacketRx_SensorData;
import hydrix.pfmat.generic.PacketRx_SetAllVoltage;
import hydrix.pfmat.generic.PacketRx_SetRefVoltage;

import hydrix.pfmat.generic.PacketTx_GetBatteryStatus;
import hydrix.pfmat.generic.PacketTx_GetDeviceDetails;
import hydrix.pfmat.generic.PacketTx_GetSensorData;
import hydrix.pfmat.generic.PacketTx_SetAccelConfig;
import hydrix.pfmat.generic.PacketTx_SetAllVoltage;
import hydrix.pfmat.generic.PacketTx_SetConfig;
import hydrix.pfmat.generic.PacketTx_SetRefVoltage;
import hydrix.pfmat.generic.PacketTx_SetZeroVoltage;
import hydrix.pfmat.generic.PacketTx_Sleep;
import hydrix.pfmat.generic.Quaternion;
import hydrix.pfmat.generic.RefVoltageObserver;


import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

// TODO: Change mDisconnecting to a reference count instead of flag, so there's no race condition between disconnect() and the recv thread checking the flag on the way out
// Not critical because the implementation of onConnectionLost happens to do nothing, but this needs to be fixed post-trial in conjunction with implementing the user notification

@SuppressWarnings("unused")
public abstract class NewDevice {
    private final static short BATTERY_UNKNOWN = -1;

    private final static int FIRMWARE_VERSION_UNKNOWN = -1;
    private final static int DEVICE_INFORMATION_TIMEOUT = 10000; // We need to see a Device Information packet within 10 seconds of connection or we disconnect
    private final static int DEVICE_INFORMATION_REQUEST_FREQ_MS = 2000; // Try every 2 seconds
    private final static int BATTERY_POLL_FREQ_MS = 60000; // Per minute
    private final String TAG = getClass().getSimpleName();

    public final static String V2 = "0188";
    public final static String V3 = "0198";

    // Members
    private final Information mInfo = new Information();
    private InputStream mInputStream = null;
    private OutputStream mOutputStream = null;
    private DevicePacketHandler mPacketHandler = null;
    private DeviceRecvThread mRecvThread = null;
    private DeviceMonitorThread mMonitorThread = null;
    private CalibrationObserver mCalibrationObserver = null;
    private RefVoltageObserver mRefVoltageObserver = null;
    private AllVoltageObserver mAllVoltageObserver = null;
    private volatile boolean mDisconnecting = false;
    private volatile boolean mSeenDeviceInformation = false;
    private OnSampleCallback sampleCallback = null;
    private WeakReference<OnSampleCallback> weakReference = null;
    private BatteryLevelUUTVoltageTest.Callback callback;
    private AllSensorsCallback allSensorsCallback;

    public class Information {
        public String mSerialNumber = "";
        public String mModel = "";
        public int mFirmwareVersion = FIRMWARE_VERSION_UNKNOWN;
        public short mBatteryPercent = BATTERY_UNKNOWN;
    }

    // Construction
    public NewDevice() {
    }

    final boolean connect() {
        // Make sure we start with a clean slate
        disconnect();

        // Attempt to connect the underlying media
        if (!connectSpecific())
            return false;
        mInputStream = getInputStream();
        mOutputStream = getOutputStream();

        // Create packet handler and receive thread
        mPacketHandler = new DevicePacketHandler();
        mRecvThread = new DeviceRecvThread();
        mRecvThread.start();

        // Once these are ready create another thread to monitor the device information/battery
        mMonitorThread = new DeviceMonitorThread();
        mMonitorThread.start();
        return true;
    }

    public final void setCalibrationObserver(CalibrationObserver calibrationObserver) {
        // Simply take a reference to calibration observer (this can be null)
        mCalibrationObserver = calibrationObserver;
    }

    public final void setRefVoltageObserver(RefVoltageObserver refVoltageObserver) {
        // Simply take a reference to refVoltage observer (this can be null)
        mRefVoltageObserver = refVoltageObserver;
    }

    public final void setAllVoltageObserver(AllVoltageObserver allVoltageObserver) {
        mAllVoltageObserver = allVoltageObserver;
    }

    public final void disconnect() {
        // Flag that we're disconnecting so the receive thread doesn't think that the connection has been lost by the input stream failing
        mDisconnecting = true;

        // Stop monitoring
        if (mMonitorThread != null) {
            mMonitorThread.cancel();
            mMonitorThread = null;
        }

        // Disconnect the actual connection
        disconnectSpecific();

        // Recv thread should drop out of its own accord once the underlying input stream is broken
        mRecvThread = null;
        mPacketHandler = null;

        // Misc cleanup
        mInputStream = null;
        mOutputStream = null;
        mCalibrationObserver = null;
        mSeenDeviceInformation = false;

        // Clear the info
        mInfo.mSerialNumber = "";
        mInfo.mModel = "";
        mInfo.mFirmwareVersion = FIRMWARE_VERSION_UNKNOWN;
        mInfo.mBatteryPercent = BATTERY_UNKNOWN;

        // No longer disconnecting
        mDisconnecting = false;
    }

    public final boolean isConnected() {
        return (mInputStream != null && mOutputStream != null);
    }

    // Return a copy of latest information for ease of thread synchronisation
    public final Information getInformation() {
        synchronized (mInfo) {
            Information copy = new Information();
            copy.mSerialNumber = new String(mInfo.mSerialNumber);
            copy.mModel = new String(mInfo.mModel);
            copy.mFirmwareVersion = mInfo.mFirmwareVersion;
            copy.mBatteryPercent = mInfo.mBatteryPercent;
            return copy;
        }
    }

    // Sending requests/commands to device
    private final boolean sendGetDeviceDetails() {
        return sendPacket(new PacketTx_GetDeviceDetails());
    }

    public final boolean sendGetBatteryStatus(BatteryLevelUUTVoltageTest.Callback callback) {
        this.callback = callback;
        return sendPacket(new PacketTx_GetBatteryStatus());
    }

    public final boolean sendGetSensorData(int requestTimestamp) {
        return sendPacket(new PacketTx_GetSensorData(requestTimestamp));
    }

    public final boolean sendRefVoltage(byte sensorIndex, short refVoltage) {
        return sendPacket(new PacketTx_SetRefVoltage(sensorIndex, refVoltage));
    }

    public final boolean sendZeroVoltage(byte sensorIndex, short zeroVoltage) {
        return sendPacket(new PacketTx_SetZeroVoltage(sensorIndex, zeroVoltage));
    }

    public final boolean sendGetData(byte config, byte sampleRate, byte sensorTestFlag) {
        return sendPacket(new PacketTx_SetConfig(config, sampleRate, sensorTestFlag));
    }

    public final boolean sendAccelConfig(byte accelFSR, short gyroFSR) {
        return sendPacket(new PacketTx_SetAccelConfig(accelFSR, gyroFSR));
    }

    public final boolean sendAllVoltages(short[] refVoltages, short[] zeroVoltages, AllSensorsCallback callback) throws InvalidVoltageException {
        if (refVoltages.length == 3 && zeroVoltages.length == 3) {
            allSensorsCallback=callback;
            return sendPacket(new PacketTx_SetAllVoltage(refVoltages, zeroVoltages));
        } else {
            throw new InvalidVoltageException("Invalid voltages");
        }
    }

    @SuppressWarnings("ucd")
    public final boolean sendSleep(byte mode, short waitTime) {
        return sendPacket(new PacketTx_Sleep(mode, waitTime));
    }

    public final boolean sendConfig(byte dataConfig, byte sampleRate, byte sensorTestModeFlag) {
        return sendPacket(new PacketTx_SetConfig(dataConfig, sampleRate, sensorTestModeFlag));
    }

    private boolean sendPacket(Packet packet) {
        if (mOutputStream == null)
            return false;

        // Serialize the packet to a byte stream
        byte[] stream = packet.toStream();
        if (stream == null)
            return false;

        // We'll write synchronously in the caller's thread context... documentation says this is generally fine (only an issue if peer falls too far behind
        // and the intermediate buffers get full locally
        try {
            synchronized (mOutputStream) {
                mOutputStream.write(stream);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private class DeviceRecvThread extends Thread {
        private final int READ_BUFSIZE = 1024;
        private long now = System.currentTimeMillis();


        // Thread func
        public void run() {
            DeviceRecvStream recvStream = new DeviceRecvStream(mPacketHandler);
            byte[] readBuffer = new byte[READ_BUFSIZE];
            int bytesRead;

            // read() is a blocking call so we just run a simple loop.  When the application closes the underlying socket the read() call
            // will fail with an IOException, causing this thread to exit gracefully
            while (true) {
                if (System.currentTimeMillis() > (now + 5000)) {
                    Log.d(TAG, "DeviceRecvThread is running: ");
                    now = System.currentTimeMillis();
                }

                try {
                    bytesRead = mInputStream.read(readBuffer);
                    if (bytesRead < 1) {
                        // InputStream.read returns -1 when the end of stream has been seen... notify the main thread so it can call disconnect (if disconnection wasn't initiated locally)
                        if (!mDisconnecting)
                            onConnectionLost();
                        break;
                    }
                    if (!recvStream.feed(readBuffer, bytesRead)) {
                        // This indicates an invalid packet has been received (or size of readBuffer exceeds buffer in recvStream). Notify main thread so it can call disconnect
                        if (!mDisconnecting)
                            onConnectionLost();
                        break;
                    }
                } catch (Exception e) {
                    if (!mDisconnecting)
                        onConnectionLost();
                    break;
                }
            }
        }
    }

    private class DeviceMonitorThread extends Thread {
        // Members
        private final Object mWakeEvent;
        private volatile boolean mCancel;

        // Construction
        public DeviceMonitorThread() {
            mWakeEvent = new Object();
            mCancel = false;
        }

        // Thread func
        public void run() {
            // When we first connect we're interested in getting device information back (both for the sake of knowing the info, and to decide
            // that it's even a functional PFMAT device that we've connected to
            long startTimeMS = System.currentTimeMillis();
            while (!mCancel && !mSeenDeviceInformation && (int) (System.currentTimeMillis() - startTimeMS) < DEVICE_INFORMATION_TIMEOUT) {
                // Send request for information
                sendGetDeviceDetails();

                // Wait for a while before resending.. wait will be interrupted by the wake event if we recv information meantime
                int timeoutRemainingMS = java.lang.Math.max(0, DEVICE_INFORMATION_TIMEOUT - (int) (System.currentTimeMillis() - startTimeMS));
                int waitMS = java.lang.Math.min(timeoutRemainingMS, DEVICE_INFORMATION_REQUEST_FREQ_MS);
                try {
                    synchronized (mWakeEvent) {
                        mWakeEvent.wait(waitMS);
                    }
                } catch (InterruptedException e) {
                }
            }

            // By now we need to have seen device information
            if (!mSeenDeviceInformation) {
                // Get the application to call disconnect
                if (!mDisconnecting)
                    onConnectionLost();
                return;
            }

            // Right, from here on we're just polling at a fixed rate for battery info
            while (!mCancel) {
                // Send request for battery status
                sendGetBatteryStatus(callback);

                // Wait for a period of time before polling again (keeping an eye on stop event)
                try {
                    synchronized (mWakeEvent) {
                        mWakeEvent.wait(BATTERY_POLL_FREQ_MS);
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        public void cancel() {
            // Signal the cancel event, which will cause the worker thread to drop out
            mCancel = true;
            synchronized (mWakeEvent) {
                mWakeEvent.notify();
            }
        }

        public void wake() {
            synchronized (mWakeEvent) {
                mWakeEvent.notify();
            }
        }
    }

    private class DevicePacketHandler extends PacketHandler {
        // This function is called in the context of the comms recv thread...
        @Override
        protected void handlePacket(Packet packet) {
            Log.d(TAG,"handlePacket"+ packet.toStream());
            if (packet != null) {
                switch (packet.getPacketType()) {
                    case PFMAT.RX_SENSOR_DATA: {
                        Log.d(TAG,"handlePacket: got sensors data");
                        PacketRx_SensorData data = (PacketRx_SensorData) packet;
                        onSensorData(data.getRequestTimestamp(), data.getSensor0(), data.getSensor1(), data.getSensor2());
                        break;
                    }

                    case PFMAT.RX_ACCEL_DATA: {
//                        Log.d(TAG,"handlePacket: got sensors data");
                        PacketRx_AccelData data = (PacketRx_AccelData) packet;
                        onSensorData(data.getRequestTimestamp(), data.getmSensor0(), data.getmSensor1(), data.getmSensor2());
                        onMotionData(data.getRequestTimestamp(), data.getAccel(), data.getGyro(), data.getQuat());
                        break;
                    }

                    case PFMAT.RX_CONFIG_DATA: {
//                        Log.d(TAG, "handlePacket: got config data");
                        PacketRx_ConfigData data = (PacketRx_ConfigData) packet;
                        break;
                    }

                    case PFMAT.RX_BATTERY_STATUS: {
                        Log.d(TAG,"handlePacket: got battery data");
                        PacketRx_BatteryStatus data = (PacketRx_BatteryStatus) packet;
                        onBatteryStatus(data.getBatteryPercent());
                        break;
                    }
                    case PFMAT.RX_DEVICE_DETAILS: {
                        Log.d(TAG,"handlePacket: got device data");
                        PacketRx_DeviceDetails data = (PacketRx_DeviceDetails) packet;
                        onDeviceDetails(data.getSerialNumber(), data.getModel(), data.getFirmwareVersion());
                        break;
                    }
//				case PFMAT.RX_CALIBRATED_SENSOR:
//				{
//					PacketRx_CalibratedSensor data = (PacketRx_CalibratedSensor)packet;
//					onCalibratedSensor(data.getSensorIndex(), !data.calibrationFailed(), data.getCalibratedOffset());
//					break;
//				}
                    case PFMAT.RX_REF_VOLTAGE: {
                        Log.d(TAG,"handlePacket: got voltage data");
                        PacketRx_SetRefVoltage data = (PacketRx_SetRefVoltage) packet;
                        onRefVoltage(data.getSensorIndex(), data.getRefVoltage());
                        break;
                    }

                    case PFMAT.RX_ALL_VOLTAGE: {
                        Log.d(TAG,"handlePacket: got all voltages data");
//                        onAllVoltage(data.VoltageFailed());
                        onAllVoltage();
                        break;
                    }
                    case PFMAT.RX_DATA: {
//                        Log.d(TAG, "handlePacket: got data packet");
                        PacketRx_Data data = (PacketRx_Data) packet;
                        handleData(data);
//                        onData(data.getmSensor0(), data.getmSensor1(), data.getmSensor2(), data.getBattery());
//                        onSensorData(0, data.getmSensor0(), data.getmSensor1(), data.getmSensor2());
//                        onMotionData(0, data.getAccel(), data.getGyro(), data.getmQuat());
                        break;
                    }
                    default:
                        Log.d(TAG,"handlePacket: we don't know this packet");
                        // Quietly ignore unimplemented packet types at this stage
                        break;
                }
            }
        }
    }

    private final void handleData(PacketRx_Data data) {
        Motion.Acceleration acc = new Motion.Acceleration(0,0,0);
        Motion.Rotation gy = new Motion.Rotation(0,0,0);
        Quaternion qt = new Quaternion(0,0,0,0);
        Short s0 = 0;
        Short s1 = 0;
        Short s2 = 0;
        Byte rssi = 0;
        Byte batt = 0;
        if ((data.getConfig() & PFMAT.BATTERY) != 0) {
            batt = data.getBattery();
        }
        if ((data.getConfig() & PFMAT.RSSI) != 0) {
            rssi = data.getRssi();
        }
        if ((data.getConfig() & PFMAT.SENSORS) != 0) {
            s0 = data.getmSensor0();
            s1 = data.getmSensor1();
            s2 = data.getmSensor2();
        }
        if ((data.getConfig() & PFMAT.ACCEL) != 0) {
            acc = data.getAccel();
        }
        if ((data.getConfig() & PFMAT.GYRO) != 0) {
            gy = data.getGyro();
        }
        if ((data.getConfig() & PFMAT.QUAT) != 0) {
            qt = data.getmQuat();
        }
        onData(s0, s1, s2, (batt.intValue()));
        onMotionData(0, acc, gy, qt);
    }

    private final void onData(short sensor0, short sensor1, short sensor2, int batteryLevel) {

        if (weakReference != null && weakReference.get() != null) {
            weakReference.get().onDataSample(0, sensor0, sensor1, sensor2, batteryLevel);
        } else {
            Log.d(TAG, "weakReference is null!!!");
        }
    }

    protected final void onMotionData(int requestTimestampMS, Motion.Acceleration accel, Motion.Rotation gyro, Quaternion quat)
    {
        if (weakReference != null && weakReference.get() != null)
            weakReference.get().onAccelSample(requestTimestampMS, accel, gyro, quat);
        else Log.d(TAG, "weakReference is null!!!");
    }

    private final void onSensorData(int requestTimestampMS, short sensor0, short sensor1, short sensor2) {
        // requestTimestampMS is already relative to the start of the session, not an absolute value, so pass it straight through
        Log.d("DATA", "On Sensor Data");
        if (weakReference != null && weakReference.get() != null)
            weakReference.get().onSample(requestTimestampMS, sensor0, sensor1, sensor2);
        else Log.d(TAG, "weakReference is null!!!");
    }

    private final void onBatteryStatus(short batteryPercent) {
        synchronized (mInfo) {
            mInfo.mBatteryPercent = batteryPercent;
            if (callback != null) {
                callback.onResultReceived(mInfo.mBatteryPercent);
                callback = null;
            }
        }
    }

    private final void onDeviceDetails(String serialNumber, String model, int firmwareVersion) {
        synchronized (mInfo) {
            mInfo.mSerialNumber = serialNumber;
            mInfo.mModel = model;
            mInfo.mFirmwareVersion = firmwareVersion;
        }

        // Notify the monitor thread that the information has turned up
        mSeenDeviceInformation = true;
        mMonitorThread.wake();
    }

    private final void onCalibratedSensor(byte sensorIndex, boolean calibrationSucceesful, float calibratedOffset) {
        // We're not particularly interested in this packet type... just dish off the notification to the application
        if (mCalibrationObserver != null)
            mCalibrationObserver.onCalibratedSensor(sensorIndex, calibrationSucceesful, calibratedOffset);
    }

    private final void onRefVoltage(byte sensorIndex, short refVoltage) {
        if (mRefVoltageObserver != null)
            mRefVoltageObserver.onRefVoltage(sensorIndex, refVoltage);
    }

//    private final void onAllVoltage(boolean ack) {
//        if (mAllVoltageObserver != null)
//            mAllVoltageObserver.onAllVoltage(ack);
//    }

    private final void onAllVoltage() {
        if(allSensorsCallback!=null){
            allSensorsCallback.onAllVoltageResponseReceived();
            allSensorsCallback=null;
        }
    }

    public void setCallback(OnSampleCallback callback) {
        weakReference = new WeakReference<OnSampleCallback>(callback);
    }

    // Mandatory overrides
    protected abstract boolean connectSpecific();

    protected abstract void disconnectSpecific();

    protected abstract InputStream getInputStream();

    protected abstract OutputStream getOutputStream();

    public abstract String getDeviceId();

    protected abstract void onConnectionLost();

    public abstract void stop();


    public class InvalidVoltageException extends Exception{
        public InvalidVoltageException(String detailMessage) {
            super(detailMessage);
        }
    }
}
