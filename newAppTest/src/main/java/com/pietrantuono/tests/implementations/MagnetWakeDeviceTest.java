package com.pietrantuono.tests.implementations;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.tests.ErrorCodes;
import com.pietrantuono.tests.superclass.Test;
import com.pietrantuono.timelogger.TimeLogger;
import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.RxBleScanResult;
import com.polidea.rxandroidble.exceptions.BleScanException;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;
import rx.Observer;
import rx.Subscription;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MagnetWakeDeviceTest extends Test {
    private AnalogInput V_3V0_SW;
    private static final int PRE_WAKE_SCAN_DURATION_MILLIS = 5000;
    private static final int SCAN_DURATION_MILLIS = 5000;
    private static final int SCAN_RESTART_DURATION_MILLIS = SCAN_DURATION_MILLIS / 5;
    private static final int BATCH_SCAN_REPORT_DELAY_MILLIS = 0;
    private static final String OUI = "B0:B4:48";
    private boolean scanning = false;
    private Subscription scanSubscription;
    private RxBleClient rxBleBlient;
    private ArrayList<RxBleScanResult> preResults;
    private ArrayList<RxBleScanResult> postResults;
    private int postCount;
    private int preCount;
    private long scanStartNanos;
    private Object scanEndNanos;

    public MagnetWakeDeviceTest(Activity activity, IOIO ioio) {
        super(activity, ioio, "Wake Device", false, true, 0, 0, 0);
    }

    @Override
    public void execute() {
        startScan();
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
        scanning = true;
        preResults = new ArrayList<>();
        rxBleBlient = RxBleClient.create(PeriCoachTestApplication.getContext());
        /**
         * We start scanning as soon as we subscribe
         */
        scanStartNanos = SystemClock.elapsedRealtimeNanos();
        scanSubscription = rxBleBlient.scanBleDevices()
                .subscribe(new Observer<RxBleScanResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(RxBleScanResult rxBleScanResult) {
                        // You get the results here
                        preResults.add(rxBleScanResult);
                    }
                });
        /**
         * We unsubscribe after {@link PRE_WAKE_SCAN_DURATION_MILLIS}
         * The docs of rxandroidble say that when we unsubscribe the scan stops
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scanSubscription.unsubscribe();
                countPreWake();
            }
        }, PRE_WAKE_SCAN_DURATION_MILLIS);
    }


    private void countPreWake() {
        preCount = preResults.size();
        TimeLogger.log("Prewake Count - " + preCount + " ");
        if (IOIOUtils.getUtils().getEmag() != null) {
            IOIOUtils.getUtils().setEMag((Activity) activityListener, false);
        }
        if (isinterrupted) return;

        final long deviceWakeNanos = SystemClock.elapsedRealtimeNanos();
        if (isinterrupted) return;
        /**
         * We restart the scan
         */
        scanSubscription = rxBleBlient.scanBleDevices()
                .subscribe(new Observer<RxBleScanResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(RxBleScanResult rxBleScanResult) {
                        // You get the results here
                        postResults.add(rxBleScanResult);
                    }
                });

        /**
         * We stop again and check the results
         */
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                scanSubscription.unsubscribe();
                scanEndNanos = SystemClock.elapsedRealtimeNanos();
                checkResults();
            }
        }, SCAN_DURATION_MILLIS);
    }

    private void checkResults() {
        postCount = postResults.size();
        TimeLogger.log("Postwake Count - " + postCount + " ");
        if (postResults.isEmpty() && preResults.isEmpty()) {
            setErrorcode((long) ErrorCodes.WAKE_NO_BLE_RECORDS);
            TimeLogger.log("No BLE Records");
            activityListener.addFailOrPass(true, false, "", description);
            return;
        }
        if (postCount - preCount < 1) {
            setErrorcode((long) ErrorCodes.WAKE_NO_POST_EMAG_RECORDS);
            TimeLogger.log("No Post wake BLE Records");
            activityListener.addFailOrPass(true, false, "", description);
            return;
        }

        if (isNewDeviceSeenAfterWake() >= 1) {
            Success();
            activityListener.startPCBSleepMonitor();                        // Device woke, start monitoring incase it drops back to sleep
            activityListener.addFailOrPass(true, true, "", description);
        } else {
            setErrorcode((long) ErrorCodes.WAKE_BLE_RECORD_OUTSIDE_SCAN_WINDOW);
            TimeLogger.log("BLE Wake outside scan window");

            activityListener.addFailOrPass(true, false, "", description);
        }
    }

    private int isNewDeviceSeenAfterWake() {
        List<RxBleDevice> preWakeDevices = new ArrayList<>();
        List<RxBleDevice> postWakeDevices = new ArrayList<>();
        ArrayList<RxBleScanResult> results = new ArrayList<>();
        results.addAll(preResults);
        results.addAll(postResults);
        for (RxBleScanResult result : results) {
            Log.d(TAG, "result " + result);
            if (preWakeDevices == null || result.getTimestampNanos() < scanStartNanos) {
                if (!preWakeDevices.contains(result.getBleDevice())) {
                    Log.d(TAG, "PreWake Adding Device: " + result.getBleDevice());
                    preWakeDevices.add(result.getBleDevice());
                }
            } else {
                if (!(preWakeDevices.contains(result.getBleDevice()))) {
                    if (result.getTimestampNanos() < scanEndNanos) {
                        if (!postWakeDevices.contains(result.getBleDevice())) {
                            Log.d(TAG, "PostWake Adding Device: " + result.getBleDevice());
                            postWakeDevices.add(result.getBleDevice());
                        }
                    }
                }
            }
        }

        if (postWakeDevices.size() == 1) {
            if (!BluetoothAdapter.checkBluetoothAddress(postWakeDevices.get(0).getMacAddress()))
                return 0;

            activityListener.setMacAddress(postWakeDevices.get(0).getMacAddress());

            Log.d(TAG, "preWake Devices: " + preWakeDevices.size());
            Log.d(TAG, "postWake Devices: " + postWakeDevices.size());
            return postWakeDevices.size();
        }
        return 0;
    }


    @Override
    public void interrupt() {
        super.interrupt();
        try {
            V_3V0_SW.close();
        } catch (Exception e) {
        }
    }

}
