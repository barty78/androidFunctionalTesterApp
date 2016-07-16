package com.pietrantuono.tests.implementations;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.tests.ErrorCodes;
import com.pietrantuono.tests.superclass.SimpleAsyncTask;
import com.pietrantuono.tests.superclass.Test;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

public class MagnetWakeDeviceTest extends Test {
    private AnalogInput V_3V0_SW;

    private BluetoothAdapter mBluetoothAdapter;
    private static final ScanFilter MANUFACTURER_DATA_FILTER =
            new ScanFilter.Builder().setManufacturerData(0x004C, new byte[0], new byte[0]).build();
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
        new MagnetWakeDeviceTestAsyncTask().executeParallel();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            V_3V0_SW.close();
        } catch (Exception e) {
        }
    }

//    @Override
//    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//        Log.d(TAG, "Device found");
//        try {
//            mBluetoothAdapter.stopLeScan(MagnetWakeDeviceTest.this);
//        } catch (Exception ignored) {
//        }
//    }

    // Helper class for BLE scan callback.
    private class BleScanCallback extends ScanCallback {

        private Set<ScanResult> mResults = new LinkedHashSet<ScanResult>();
        private List<ScanResult> mBatchScanResults = new ArrayList<ScanResult>();

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            boolean add = true;
            if (callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
//                if (result.getDevice().getAddress().contains(OUI)) {
//                    for (ScanResult res : mResults) {
//                        if (result.getDevice().getAddress().equalsIgnoreCase(res.getDevice().getAddress())) add = false;
//                    }
//                    if (add) {
//                        mResults.add(result);
//                        Log.d(TAG, result.toString());
//                    }
//                }

                mResults.add(result);
                Log.d(TAG, result.toString());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d(TAG, "Batch " + results.toString());
            mBatchScanResults = results;
        }

        // Clear regular and batch scan results.
        synchronized public void clear() {
            mResults.clear();
            mBatchScanResults.clear();
        }

        // Return regular BLE scan results accumulated so far.
        synchronized Collection<ScanResult> getScanResults() {
            return Collections.unmodifiableCollection(mResults);
        }

        // Return batch scan results.
        synchronized List<ScanResult> getBatchScanResults() {
            return Collections.unmodifiableList(mBatchScanResults);
        }
    }

    // Verify timestamp of all scan results are within [scanStartMillis, scanEndMillis].
    private boolean verifyTimestamp(Collection<ScanResult> results, long scanStartMillis,
                                    long scanEndMillis) {
        for (ScanResult result : results) {
            long timestampMillis = TimeUnit.NANOSECONDS.toMillis(result.getTimestampNanos());
            if ((timestampMillis <= scanStartMillis) || (timestampMillis >= scanEndMillis)) {
                Log.d(TAG, "Invalid timestamp - " + timestampMillis);
                setErrorcode((long) ErrorCodes.WAKE_BLE_RECORD_OUTSIDE_SCAN_WINDOW);
                return false;
            }
            Log.d(TAG, "ADV Scan Time: " + (timestampMillis - scanStartMillis));
        }
        return true;
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

        if(postWakeDevices.size() == 1) {
            if (!BluetoothAdapter.checkBluetoothAddress(postWakeDevices.get(0).getAddress()))return 0;

            activityListener.setMacAddress(postWakeDevices.get(0).getAddress());

            Log.d(TAG, "preWake Devices: " + preWakeDevices.size());
            Log.d(TAG, "postWake Devices: " + postWakeDevices.size());
            return postWakeDevices.size();
        }
    }

    // Put the current thread to sleep.
    private void sleep(int sleepMillis) {
        try {
            Thread.sleep(sleepMillis);
        } catch (InterruptedException e) {
            Log.e(TAG, "interrupted", e);
        }
    }


    private class MagnetWakeDeviceTestAsyncTask extends SimpleAsyncTask {
        @Override
        protected Void doInBackground(Void... params) {
            if (isinterrupted) return null;

            if (IOIOUtils.getUtils().getEmag() != null) {
                IOIOUtils.getUtils().setEMag((Activity) activityListener, true);
            }

            if (PeriCoachTestApplication.getCurrentJob().getTesttypeId() != 1) {
                ScanSettings batchScanSettings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .setReportDelay(BATCH_SCAN_REPORT_DELAY_MILLIS).build();
                BleScanCallback regularLeScanCallback = new BleScanCallback();
                BleScanCallback batchScanCallback = new BleScanCallback();
                long scanStartNanos = SystemClock.elapsedRealtimeNanos();
                mScanner.startScan(regularLeScanCallback);

//                mScanner.startScan(Collections.<ScanFilter>emptyList(), batchScanSettings, batchScanCallback);

                sleep(PRE_WAKE_SCAN_DURATION_MILLIS);

                int preCount = regularLeScanCallback.getScanResults().size();
//                int preCount = batchScanCallback.getBatchScanResults().size();

//                if (IOIOUtils.getUtils().getEmag() != null) {
//                    IOIOUtils.getUtils().toggleEMag((Activity) activityListener);
//                }
                if (IOIOUtils.getUtils().getEmag() != null) {
                    IOIOUtils.getUtils().setEMag((Activity) activityListener, false);
                }
                if (isinterrupted) return null;

                long deviceWakeNanos = SystemClock.elapsedRealtimeNanos();
                if (isinterrupted) return null;
                sleep(SCAN_DURATION_MILLIS);
                mScanner.stopScan(regularLeScanCallback);
//                mScanner.flushPendingScanResults(batchScanCallback);
//                mScanner.stopScan(batchScanCallback);
                long scanEndNanos = SystemClock.elapsedRealtimeNanos();
                Collection<ScanResult> scanResults = regularLeScanCallback.getScanResults();

//                List<ScanResult> results = batchScanCallback.getBatchScanResults();
                int postCount = regularLeScanCallback.getScanResults().size();
//                int postCount = batchScanCallback.getScanResults().size();
                if (scanResults.isEmpty()) {
                    setErrorcode((long) ErrorCodes.WAKE_NO_BLE_RECORDS);
                    activityListener.addFailOrPass(true, false, "", description);
                    return null;
                }

                if (postCount - preCount < 1) {
                    setErrorcode((long) ErrorCodes.WAKE_NO_POST_EMAG_RECORDS);
                    activityListener.addFailOrPass(true, false, "", description);
                    return null;
                }

                Log.d(TAG, "Start Scan Nanos: " + scanStartNanos);
                Log.d(TAG, "Device Wake Nanos: " + deviceWakeNanos);
                Log.d(TAG, "End Scan Nanos: " + scanEndNanos);
                Log.d(TAG, "PRE_WAKE Count: " + preCount);
                Log.d(TAG, "POST_WAKE Count: " + postCount);

//				if (verifyTimestamp(scanResults, deviceWakeMillis, scanEndMillis)) {
                if (isNewDeviceSeenAfterWake(scanResults, deviceWakeNanos, scanEndNanos) >= 1) {
                    Success();
                    activityListener.startPCBSleepMonitor();                        // Device woke, start monitoring incase it drops back to sleep
                    activityListener.addFailOrPass(true, true, "", description);
                } else {
                    setErrorcode((long) ErrorCodes.WAKE_BLE_RECORD_OUTSIDE_SCAN_WINDOW);
                    activityListener.addFailOrPass(true, false, "", description);
                }

            } else {
                DecimalFormat df = new DecimalFormat("##.##");
                df.setRoundingMode(RoundingMode.DOWN);

                if (IOIOUtils.getUtils().getEmag() != null) {
                    IOIOUtils.getUtils().toggleEMag((Activity) activityListener);
                }
                if (isinterrupted) return null;
                try {
                    Thread.sleep(1 * 3000);
                } catch (Exception e) {
                    getListener().addFailOrPass(true, false, "ERROR", "App Fault");

                }

                if (isinterrupted) return null;

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

            return null;
        }
    }
}
