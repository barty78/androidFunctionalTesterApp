package com.pietrantuono.tests.implementations;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.tests.ErrorCodes;
import com.pietrantuono.tests.superclass.Test;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MagnetWakeDeviceTest extends Test {
    private AnalogInput V_3V0_SW;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mScanner;
    private static final int PRE_WAKE_SCAN_DURATION_MILLIS = 3000;
    private static final int SCAN_DURATION_MILLIS = 3000;
    private static final int BATCH_SCAN_REPORT_DELAY_MILLIS = 0;
    private static final String OUI = "B0:B4:48";

    public MagnetWakeDeviceTest(Activity activity, IOIO ioio) {
        super(activity, ioio, "Wake Device", false, true, 0, 0, 0);
    }

    @Override
    public void execute() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        startScan();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            V_3V0_SW.close();
        } catch (Exception e) {
        }
    }

    private int isNewDeviceSeenAfterWake(Collection<ScanResult> results, long scanStartNanos, long scanEndNanos) {
        List<BluetoothDevice> preWakeDevices = new ArrayList<>();
        List<BluetoothDevice> postWakeDevices = new ArrayList<>();

        for (ScanResult result : results) {
            Log.d(TAG, "result " + result);
            if (preWakeDevices == null || result.getTimestampNanos() < scanStartNanos) {
                if (!preWakeDevices.contains(result.getDevice())) {
                    Log.d(TAG, "PreWake Adding Device: " + result.getDevice());
                    preWakeDevices.add(result.getDevice());
                }
            } else {
                if (!(preWakeDevices.contains(result.getDevice()))) {
                    if (result.getTimestampNanos() < scanEndNanos) {
                        if (!postWakeDevices.contains(result.getDevice())) {
                            Log.d(TAG, "PostWake Adding Device: " + result.getDevice());
                            postWakeDevices.add(result.getDevice());
                        }
                    }
                }
            }
        }

        if (postWakeDevices.size() == 1) {
            if (!BluetoothAdapter.checkBluetoothAddress(postWakeDevices.get(0).getAddress()))
                return 0;

            activityListener.setMacAddress(postWakeDevices.get(0).getAddress());

            Log.d(TAG, "preWake Devices: " + preWakeDevices.size());
            Log.d(TAG, "postWake Devices: " + postWakeDevices.size());
            return postWakeDevices.size();
        }
        return 0;
    }

    protected void startScan() {
        if (isinterrupted) return;
        if (IOIOUtils.getUtils().getEmag() != null) {
            IOIOUtils.getUtils().setEMag((Activity) activityListener, true);
        }
        if (PeriCoachTestApplication.getCurrentJob().getTesttypeId() != 1) {
            doInitialScan();
        } else {
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);

            if (IOIOUtils.getUtils().getEmag() != null) {
                IOIOUtils.getUtils().toggleEMag((Activity) activityListener);
            }
            if (isinterrupted) return;
            try {
                Thread.sleep(1 * 3000);
            } catch (Exception e) {
                getListener().addFailOrPass(true, false, "ERROR", "App Fault");

            }
            if (isinterrupted) return;
            try {
                Voltage.Result result = Voltage.checkVoltage(ioio, 33, 1f, true, 3.3f, 0.1f);
                setValue(result.getReadingValue());
                Log.d(TAG, "Result isSuccess = " + result.isSuccess());
                if (result.isSuccess()) {
                    Success();
                    activityListener.startPCBSleepMonitor();
                    activityListener.addFailOrPass(true, true, "", description);
                } else {
                    activityListener.addFailOrPass(true, false, "", description);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void doInitialScan() {
        final BleScanCallback regularLeScanCallback = new BleScanCallback();
        final long scanStartNanos = SystemClock.elapsedRealtimeNanos();
        mScanner.startScan(regularLeScanCallback);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                countPreWake(regularLeScanCallback, scanStartNanos);
            }
        }, PRE_WAKE_SCAN_DURATION_MILLIS);
    }

    private void countPreWake(final BleScanCallback regularLeScanCallback, final long scanStartNanos) {
        final int preCount = regularLeScanCallback.getScanResults().size();
        if (IOIOUtils.getUtils().getEmag() != null) {
            IOIOUtils.getUtils().setEMag((Activity) activityListener, false);
        }
        if (isinterrupted) return;

        final long deviceWakeNanos = SystemClock.elapsedRealtimeNanos();
        if (isinterrupted) return;

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan(regularLeScanCallback, scanStartNanos, preCount, deviceWakeNanos);
            }
        }, SCAN_DURATION_MILLIS);
    }

    private void stopScan(BleScanCallback regularLeScanCallback, long scanStartNanos, int preCount, long deviceWakeNanos) {
        mScanner.stopScan(regularLeScanCallback);
        long scanEndNanos = SystemClock.elapsedRealtimeNanos();
        Collection<ScanResult> scanResults = regularLeScanCallback.getScanResults();
        int postCount = regularLeScanCallback.getScanResults().size();
        if (scanResults.isEmpty()) {
            setErrorcode((long) ErrorCodes.WAKE_NO_BLE_RECORDS);
            activityListener.addFailOrPass(true, false, "", description);
            return;
        }
        if (postCount - preCount < 1) {
            setErrorcode((long) ErrorCodes.WAKE_NO_POST_EMAG_RECORDS);
            activityListener.addFailOrPass(true, false, "", description);
            return;
        }
        Log.d(TAG, "Start Scan Nanos: " + scanStartNanos);
        Log.d(TAG, "Device Wake Nanos: " + deviceWakeNanos);
        Log.d(TAG, "End Scan Nanos: " + scanEndNanos);
        Log.d(TAG, "PRE_WAKE Count: " + preCount);
        Log.d(TAG, "POST_WAKE Count: " + postCount);

        if (isNewDeviceSeenAfterWake(scanResults, deviceWakeNanos, scanEndNanos) >= 1) {
            Success();
            activityListener.startPCBSleepMonitor();                        // Device woke, start monitoring incase it drops back to sleep
            activityListener.addFailOrPass(true, true, "", description);
        } else {
            setErrorcode((long) ErrorCodes.WAKE_BLE_RECORD_OUTSIDE_SCAN_WINDOW);
            activityListener.addFailOrPass(true, false, "", description);
        }
    }

}
