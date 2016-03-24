package com.pietrantuono.btutility;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.pietrantuono.activities.ActivtyWrapper;
import com.pietrantuono.activities.NewIOIOActivityListener;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.pericoach.newtestapp.BuildConfig;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.pericoachengineering.util.Utils;
import com.pietrantuono.sensors.AllSensorsCallback;
import com.pietrantuono.sensors.NewDevice;
import com.pietrantuono.sensors.NewPFMATDevice;
import com.pietrantuono.tests.superclass.Test;
import com.radiusnetworks.bluetooth.BluetoothCrashResolver;

import analytica.pericoach.android.ConnectDeviceItem;
import analytica.pericoach.android.DBManager;
import analytica.pericoach.android.Type;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;

import customclasses.DebugHelper;
import hydrix.pfmat.generic.Device;

public class BTUtility {
    private final String TAG = getClass().getSimpleName();
    private ActivtyWrapper callback = null;

    private WeakReference<Activity> activityRef;
    private final BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayList<ConnectDeviceItem> mListItems = null;
    private final BTBroadcastReceiver mBTReceiver = new BTBroadcastReceiver();
    private final ConnectReceiver mConnectReceiver = new ConnectReceiver();
    private static final String INTENT_CONNECT_FAILED = "INTENT_CONNECT_FAILED";
    private static final String INTENT_CONNECT_SUCCEEDED = "INTENT_CONNECT_SUCCEEDED";
    private ProgressDialog progressdialog;
    private String mFirmwareVer;
    private static final String DISCONNECTED = "disconnected";
    private String mDeviceId;
    private String macaddress = null;
    private String scancode = "";
    // private IOIOActivityListener ioioActivityListener;
    private String model = null;
    private BluetoothCrashResolver bluetoothCrashResolver;
    private Boolean isstopped = false;
    private Test bluetoothConnectTest;
    private int retries = 0;

    @SuppressWarnings("unused")
    private static Activity activity;
    private ExecutorService executor;
    private boolean interrupt = false;


    private class ConnectReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(INTENT_CONNECT_FAILED)) {
                onConnectFailed();
            } else if (intent.getAction().equals(INTENT_CONNECT_SUCCEEDED)) {
                onConnectSucceeded();
            }
        }
    }

    private class BTBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "BT Device Found - " + device.getAddress());
                if (device != null) {
                    if (bluetoothCrashResolver != null)
                        bluetoothCrashResolver
                                .notifyScannedDevice(device, null);
                    onDiscoverDevice(device);
                }
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "Device Disconnected - " + device.getName());
                //TODO - If test is running, we need to stop.
                callback.onPCBConnectionLostRestartSequence();
            }
            // If we aren't connected to a device and discovery finished, kick
            // off a new discovery.
            else if (mListItems.size() == 0) {
                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent
                        .getAction())) {
                    if (progressdialog != null)
                        progressdialog.dismiss();
                    startDiscovery();
                }
            }
        }
    }

    public BTUtility(Activity activity1, String scancode,
                     String macaddress) {
        if (isstopped)
            return;
        this.activityRef = new WeakReference<Activity>(activity1);
        if (PeriCoachTestApplication.getCurrentJob().getTesttypeId() == 1) {
            this.scancode = scancode;
            this.macaddress = macaddress;
        }
        final Activity activity = activityRef.get();
        if (activity == null)
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isstopped)
                    return;
                progressdialog = new ProgressDialog(activity);
                progressdialog.setTitle("Connecting PCB via Bluetooth");
                progressdialog.setMessage("Looking for PCBs, please wait");
            }
        });
        executor = Executors.newFixedThreadPool(1);
    }

    private void onConnectFailed() {
        retries++;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activityRef.get());
        boolean usemac = sharedPref.getBoolean(activityRef.get().getResources().getString(R.string.use_mac), false);
        if (!usemac && retries < 3) {
            startDiscovery();
            return;
        }
        if (usemac && retries < 3) {
            connectUsingMac();
            return;
        }
        ((NewIOIOActivityListener) activityRef.get()).addFailOrPass(
                false, false, "CONNECT FAILED", bluetoothConnectTest.getDescription(), bluetoothConnectTest.testToBeParsed);
    }


    public void connectProbeViaBT(Test bluetoothConnectTest) {
        if (isstopped)
            return;
        this.bluetoothConnectTest = bluetoothConnectTest;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activityRef.get());
        boolean usemac = sharedPref.getBoolean(activityRef.get().getResources().getString(R.string.use_mac), false);
        if (usemac) {
            connectUsingMac();
            return;
        }
        mListItems = new ArrayList<ConnectDeviceItem>();
        // Register for BT device discovery broadcast events
        IntentFilter eventFilter = new IntentFilter();
        eventFilter.addAction(BluetoothDevice.ACTION_FOUND);
        eventFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        eventFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        eventFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        Activity activity = activityRef.get();
        if (activity == null)
            return;
        activity.registerReceiver(mBTReceiver, eventFilter);
        // Register for connection results
        IntentFilter connectFilter = new IntentFilter();
        connectFilter.addAction(INTENT_CONNECT_FAILED);
        connectFilter.addAction(INTENT_CONNECT_SUCCEEDED);
        // connectFilter.addAction(INTENT_REFRESH);
        activity.registerReceiver(mConnectReceiver, connectFilter);
        // Populate the ConnectDeviceItemList so that we can check/unpair
        // any existing devices - shouldn't be any
        Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
            for (BluetoothDevice device : pairedDevices)
                if (device.getName() != null
                        && (device.getName().contains("PeriCoach"))) {
                    mListItems.add(new ConnectDeviceItem(Type.DEVICE, device
                            .getName(), device, R.drawable.device));
                }
        removeDevicesFromList(true, true);
        startDiscovery();
    }

    private void removeDevicesFromList(boolean removeBondedDevices,
                                       boolean unpair) {
        if (mListItems == null || mListItems.size() <= 0) return;
        // Clear devices from the adapter... we always leave item0, which is the
        // Refresh option
        for (int i = mListItems.size() - 1; i == 0; i--) {
            if (unpair) {
                Log.d("BT:", "Unpair - " + mListItems.get(i).getName());
                Utils.unpairDevice(mListItems.get(i).getDevice());
            }
            if (removeBondedDevices
                    || mListItems.get(i).getDevice().getBondState() != BluetoothDevice.BOND_BONDED) {
                Log.d("BT:", "List Remove - " + mListItems.get(i).getName());
                mListItems.remove(i);
            }
        }
    }

    private final void startDiscovery() {
        if (isstopped)
            return;
        Activity activity = activityRef.get();
        if (activity == null || activity.isFinishing())
            return;
        NewPFMATDevice.disconnect();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressdialog.show();
            }
        });
        if (activityRef.get() != null) {
            bluetoothCrashResolver = new BluetoothCrashResolver(
                    activityRef.get());
//            bluetoothCrashResolver.start();
        }
        mBTAdapter.startDiscovery();
        Log.d(TAG, "Scancode is " + scancode);
    }

    private final void onDiscoverDevice(BluetoothDevice device) {
        if (isstopped)
            return;
        Log.d("BT ADDR:", device.getAddress());
        Log.d("BT NAME:", device.getName());
        if (device.getName() != null) {

            if (device.getName() != ""
                    && (device.getName().contains("PeriCoach-" + scancode))) {
                mListItems.add(new ConnectDeviceItem(Type.DEVICE, device
                        .getName(), device, R.drawable.device));
                mBTAdapter.cancelDiscovery();
                // Attempt to connect to specified device. This happens
                // asynchronously, and we'll end up with onConnectSucceeded or
                // onConnectFailed called when it's complete
                Activity activity = activityRef.get();
                if (activity == null)
                    return;
                mBTAdapter.cancelDiscovery();
                NewPFMATDevice.specifyDevice(device);
                mBTAdapter.cancelDiscovery();
                NewPFMATDevice.connect(activity, INTENT_CONNECT_SUCCEEDED,
                        INTENT_CONNECT_FAILED);
                if (progressdialog != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressdialog.dismiss();
                        }
                    });
                }
            }
        }
    }

    private void connectUsingMac() {
        if (isstopped)
            return;
//		mListItems = new ArrayList<ConnectDeviceItem>();
//
//		Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
//		if (pairedDevices.size() > 0)
//			for (BluetoothDevice device : pairedDevices)
//				if (device.getName() != null
//						&& (device.getName().contains("PeriCoach"))) {
//					mListItems.add(new ConnectDeviceItem(Type.DEVICE, device
//							.getName(), device, R.drawable.device));
//				}
//		removeDevicesFromList(true, true);
        IntentFilter connectFilter = new IntentFilter();
        connectFilter.addAction(INTENT_CONNECT_FAILED);
        connectFilter.addAction(INTENT_CONNECT_SUCCEEDED);
        // connectFilter.addAction(INTENT_REFRESH);
        activityRef.get().registerReceiver(mConnectReceiver, connectFilter);
        BluetoothDevice device = null;
        if (BuildConfig.FLAVOR.equalsIgnoreCase("maurizio")) {
            device = mBTAdapter.getRemoteDevice("00:17:E9:C0:82:EE");
        } else {
            device = mBTAdapter.getRemoteDevice(macaddress);
        }
        NewPFMATDevice.specifyDevice(device);
        NewPFMATDevice.connect(activityRef.get(), INTENT_CONNECT_SUCCEEDED,
                INTENT_CONNECT_FAILED);

    }


    private void onConnectSucceeded() {
        if (isstopped)
            return;
        if (progressdialog != null) {
            Activity activity = activityRef.get();
            if (activity == null)
                return;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressdialog.dismiss();
                }
            });
        }
        NewDevice device = NewPFMATDevice.getDevice();
        NewDevice.Information info = (device == null || !device.isConnected()) ? null
                : NewPFMATDevice.getDevice().getInformation();
        // Firmware version
        if (info == null
                || info.mFirmwareVersion == Device.FIRMWARE_VERSION_UNKNOWN)
            mFirmwareVer = DISCONNECTED;
        else {
            Object[] args = new Object[4];
            args[0] = new Integer(info.mFirmwareVersion >> 24);
            args[1] = new Integer((info.mFirmwareVersion >> 16) & 0xFF);
            args[2] = new Integer((info.mFirmwareVersion >> 8) & 0xFF);
            args[3] = new Integer(info.mFirmwareVersion & 0xFF);
            mFirmwareVer = String.format("%d.%d.%d.%d", args);
        }
        mDeviceId = (info == null || info.mSerialNumber == null || info.mSerialNumber
                .length() == 0) ? DISCONNECTED : info.mSerialNumber;
        model = (info == null || info.mModel == null || info.mModel.length() == 0) ? null
                : info.mModel;
//		checkDeviceID(mDeviceId);

        start();
    }


    private void start() {
        if (isstopped)
            return;
        if (bluetoothConnectTest != null) bluetoothConnectTest.setSuccess(true);

        if ((NewIOIOActivityListener) activityRef.get() == null)
            return;
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                insertDeviceAndScancode(mDeviceId, scancode);
                ((NewIOIOActivityListener) activityRef.get()).addFailOrPass(
                        true, true, "Connected", bluetoothConnectTest.getDescription(), bluetoothConnectTest.testToBeParsed);
                //((NewIOIOActivityListener) activityRef.get()).goAndExecuteNextTest();
            }
        });
    }

    private Boolean insertDeviceAndScancode(String devid, String scancode) {
        if (isstopped)
            return false;
        Boolean inserted = false;
        if (scancode == null || scancode.isEmpty())
            scancode = "";
        final Activity activity = activityRef.get();
        if (activity == null)
            return false;
        DBManager db = (new DBManager(activity));
        db.insertDeviceID(devid);
        db.insertScancode(scancode);
        return inserted;
    }

    public String getSerial() {
        String serial = null;
        NewDevice device = NewPFMATDevice.getDevice();
        NewDevice.Information info = (device == null || !device.isConnected()) ? null
                : NewPFMATDevice.getDevice().getInformation();
        if (info != null)
            serial = info.mSerialNumber;
        return serial;
    }

    public String getModelNumber() {
        return model;
    }

    public String getFirmWareVersion() {
        return mFirmwareVer;
    }

    public short requestBatteryLevelAndWait() {
        if (isstopped)
            return 0;
        NewDevice device = NewPFMATDevice.getDevice();
        device.sendGetBatteryStatus(null);
        try {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        NewDevice.Information info = (device == null || !device.isConnected()) ? null
                : NewPFMATDevice.getDevice().getInformation();
        if (info != null) {
            Log.d("BATTERY LEVEL", String.valueOf(info.mBatteryPercent));
            device = null;
            return info.mBatteryPercent;
        }
        return -1;
    }


    private void stopBTDiscovery() {
        if (mBTAdapter != null && mBTAdapter.isDiscovering())
            mBTAdapter.cancelDiscovery();
        Activity activity = activityRef.get();
        if (activity == null)
            return;
        try {
            activity.unregisterReceiver(mBTReceiver);
        } catch (Exception e) {
        }
        try {
            activity.unregisterReceiver(mConnectReceiver);
        } catch (Exception e) {
        }
    }

    public void pollSensor() throws Exception {
        int curPos = IOIOUtils.getUtils().getUartLog().length();
        NewPFMATDevice.getDevice().sendGetSensorData(0);
        if (!getAckOrTimeout(200, "DAC", curPos)) throw new Exception("Polling failed.");
    }

    public void setZeroVoltage(final Short voltage) throws Exception {

        // If job testtype is open, we can use the uart for ACK of packets
        if (PeriCoachTestApplication.getCurrentJob().getTesttypeId() == 1) {
            Byte sensor = (byte) (0 & 0xFF);
            Log.d("SENSOR", "Setting sensor " + sensor + " zero to " + voltage);
            int curPos = IOIOUtils.getUtils().getUartLog().length();
            NewPFMATDevice.getDevice().sendZeroVoltage(sensor, voltage);
            if (!getAckOrTimeout(200, "S0 ZERO SET to", curPos))
                throw new Exception("Zero Setting failed.");

            sensor = (byte) (1 & 0xFF);
            Log.d("SENSOR", "Setting sensor " + sensor + " zero to " + voltage);
            curPos = IOIOUtils.getUtils().getUartLog().length();
            NewPFMATDevice.getDevice().sendZeroVoltage(sensor, voltage);
            if (!getAckOrTimeout(200, "S1 ZERO SET to", curPos))
                throw new Exception("Zero Setting failed.");

            sensor = (byte) (2 & 0xFF);
            Log.d("SENSOR", "Setting sensor " + sensor + " zero to " + voltage);
            curPos = IOIOUtils.getUtils().getUartLog().length();
            NewPFMATDevice.getDevice().sendZeroVoltage(sensor, voltage);
            if (!getAckOrTimeout(200, "S2 ZERO SET to", curPos))
                throw new Exception("Zero Setting failed.");
        } else {
            // We need to use another method of ACK for packets..  Perhaps we need a new packet type in probe which does send back ACK
        }
    }

    public void setVoltage(final Short voltage) throws Exception {
        if (DebugHelper.isMaurizioDebug()) return;
        // If job testtype is open, we can use the uart for ACK of packets
        if (PeriCoachTestApplication.getCurrentJob().getTesttypeId() == 1) {
            Byte sensor = (byte) (0 & 0xFF);
            Log.d("SENSOR", "Setting sensor " + sensor + " to " + voltage);
            int curPos = IOIOUtils.getUtils().getUartLog().length();
            NewPFMATDevice.getDevice().sendRefVoltage(sensor, voltage);
            if (!getAckOrTimeout(200, "S0 VOLTAGE SET to", curPos))
                throw new Exception("Setting failed.");

            sensor = (byte) (1 & 0xFF);
            Log.d("SENSOR", "Setting sensor " + sensor + " to " + voltage);
            curPos = IOIOUtils.getUtils().getUartLog().length();
            NewPFMATDevice.getDevice().sendRefVoltage(sensor, voltage);
            if (!getAckOrTimeout(200, "S1 VOLTAGE SET to", curPos))
                throw new Exception("Setting failed.");

            sensor = (byte) (2 & 0xFF);
            Log.d("SENSOR", "Setting sensor " + sensor + " to " + voltage);
            curPos = IOIOUtils.getUtils().getUartLog().length();
            NewPFMATDevice.getDevice().sendRefVoltage(sensor, voltage);
            if (!getAckOrTimeout(200, "S2 VOLTAGE SET to", curPos))
                throw new Exception("Setting failed.");
        } else {
            // We need to use another method of ACK for packets..  Perhaps we need a new packet type in probe which does send back ACK
        }
    }


    public void sendAllVoltages(final short[] refVoltages, final short[] zeroVoltages, int timeOutInMills) throws TimeoutException, NewDevice.InvalidVoltageException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            NewPFMATDevice.getDevice().sendAllVoltages(refVoltages, zeroVoltages, new AllSensorsCallback() {
                @Override
                public void onAllVoltageResponseReceived() {
                    countDownLatch.countDown();
                    Log.d(TAG, "onAllVoltage , ACK");
                }

                @Override
                public void onError() {
                    countDownLatch.countDown();
                    Log.d(TAG, "onAllVoltage , ERROR ");
                }
            });
        } catch (NewDevice.InvalidVoltageException e) {
            e.printStackTrace();
            countDownLatch.countDown();
            Log.d(TAG, "Exception in  sendAllVoltages = " + e.toString());
            throw e;
        }
        try {
            boolean notTimedout = countDownLatch.await(timeOutInMills, TimeUnit.MILLISECONDS);
            Log.d(TAG,"Timed out = "+notTimedout);
            if(!notTimedout)throw new TimeoutException("Set all voltages timed out");
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        return;


    }

    private boolean getAckOrTimeout(int timeout, final String msg, final int curPos) {
        interrupt = false;
        Callable<Integer> integerCallable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                while ((IOIOUtils.getUtils().getUartLog().substring(curPos).indexOf(msg) == -1 && !interrupt)) {
                }
                return 0;
            }
        };
        int pos = -1;
        Future<Integer> future = executor.submit(integerCallable);
        try {
            pos = future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Log.d(TAG, "getAckOrTimeout timed out");
            return false;
        }

        if (pos == -1) {
            interrupt = true;
            return false;
        } else {
            Log.d(TAG, "Found - " + msg);
            return true;
        }
    }


    public void abort() {
        Log.d("BTUtility", "abort");

        isstopped = true;
        stopBTDiscovery();
        if (NewPFMATDevice.getDevice() != null) {
            try {
                setVoltage((short) 127);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (NewPFMATDevice.getDevice() != null) {
            NewPFMATDevice.getDevice().disconnect();
            NewPFMATDevice.getDevice().stop();
        }
        if (progressdialog != null && progressdialog.isShowing()
                && activityRef.get() != null) {
            activityRef.get().runOnUiThread(new Runnable() {
                public void run() {
                    progressdialog.dismiss();
                }
            });
        }
        try {
            IOIOUtils.getUtils().getSensor_High().write(true);
        } catch (Exception e) {
        }
        removeDevicesFromList(true, true);
        try {
            activityRef.get().unregisterReceiver(mBTReceiver);
        } catch (Exception e) {
        }
        try {
            activityRef.get().unregisterReceiver(mConnectReceiver);
        } catch (Exception e) {
        }
    }


    public void stop() {
        isstopped = true;
        if (NewPFMATDevice.getDevice() != null) {
            try {
                //setVoltage((short) 127);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            IOIOUtils.getUtils().getSensor_High().write(true);
        } catch (Exception e) {
        }
        HandlerThread handlerThread = new HandlerThread("Sensor test handler thread");
        handlerThread.start();

        Handler handler = new Handler(handlerThread.getLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NewPFMATDevice.getDevice() != null) {
                    NewPFMATDevice.getDevice().sendSleep((byte)0, (short) 100);
                }
            }
        }, 200);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NewPFMATDevice.getDevice() != null) {
                    NewPFMATDevice.getDevice().disconnect();
                }
            }
        }, 300);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                removeDevicesFromList(true, true);
            }
        }, 400);
        if (progressdialog != null && progressdialog.isShowing()
                && activityRef.get() != null) {
            activityRef.get().runOnUiThread(new Runnable() {
                public void run() {
                    progressdialog.dismiss();
                }
            });
        }
        stopBTDiscovery();
    }

    private final static BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                BTUtility.activity.unregisterReceiver(mReceiver);
                if (device == null) return;
                if (device.getName() == null) return;
                if (!device.getName().toLowerCase().contains("ioio")) return;
                if (device.getAddress() == null) return;
                PeriCoachTestApplication.setIOIOAddress(device.getAddress());
            }
        }
    };

    public static void unregisterIOIOAddressREceiver() {
        if (BTUtility.activity == null) return;
        try {
            activity.unregisterReceiver(mReceiver);
        } catch (Exception e) {
        }
    }
}
