package com.pietrantuono.tests.implementations;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.tests.ErrorCodes;
import com.pietrantuono.tests.superclass.Test;
import com.pietrantuono.timelogger.TimeLogger;
import com.polidea.rxandroidble.RxBleClient;
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
import android.util.Log;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MagnetWakeDeviceTest extends Test {
    private AnalogInput V_3V0_SW;

    private static final int PRE_WAKE_SCAN_DURATION_MILLIS = 3000;
    private static final int SCAN_DURATION_MILLIS = 3000;
    private Subscription scanSubscription;
    private RxBleClient rxBleClient;
    private boolean awake = false;
    private ArrayList<String> macAddresses = new ArrayList<>();

    private Handler wakeScanTimeoutHandler;

    public MagnetWakeDeviceTest(Activity activity, IOIO ioio) {
        super(activity, ioio, "Wake Device", false, true, 0, 0, 0);
    }

    @Override
    public void execute() {

        if (isinterrupted) return;
        if (IOIOUtils.getUtils().getEmag() != null) {
            IOIOUtils.getUtils().setEMag((Activity) activityListener, false);
        }
        if (PeriCoachTestApplication.getCurrentJob().getTesttypeId() != 1) {
            IOIOUtils.getUtils().setServo(ioio, 1500);
            rxBleClient = RxBleClient.create(PeriCoachTestApplication.getContext());
            /**
             * We start scanning as soon as we subscribe
             */
            scanSubscription = rxBleClient.scanBleDevices()
                    .subscribe(new Observer<RxBleScanResult>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(RxBleScanResult rxBleScanResult) {
                            TimeLogger.log("Scan Result - " + rxBleScanResult.getBleDevice().getMacAddress());

                            if (awake) {
                                if (!macAddresses.contains(rxBleScanResult.getBleDevice().getMacAddress())) {
                                    TimeLogger.log("Device Found Postwake - " + rxBleScanResult.getBleDevice().getMacAddress());
                                    if (BluetoothAdapter.checkBluetoothAddress(rxBleScanResult.getBleDevice().getMacAddress())) {
                                        activityListener.setMacAddress(rxBleScanResult.getBleDevice().getMacAddress());
                                        if (wakeScanTimeoutHandler != null)
                                            wakeScanTimeoutHandler.removeCallbacksAndMessages(null);
                                        scanSubscription.unsubscribe();
                                        Success();
                                        activityListener.startPCBSleepMonitor();                        // Device woke, start monitoring incase it drops back to sleep
                                        activityListener.addFailOrPass(true, true, "", description);
                                    }
                                }
                            } else {
                                if (macAddresses == null || !macAddresses.contains(rxBleScanResult.getBleDevice().getMacAddress())) {
                                    macAddresses.add(rxBleScanResult.getBleDevice().getMacAddress());
                                    TimeLogger.log("Device Added Prewake - " + rxBleScanResult.getBleDevice().getMacAddress());
                                }
                            }
                        }
                    });
            /**
             * We unsubscribe after {@link PRE_WAKE_SCAN_DURATION_MILLIS}
             * The docs of rxandroidble say that when we unsubscribe the scan stops
             */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    TimeLogger.addSplit("Wake - Magnet Off");
                    if (IOIOUtils.getUtils().getEmag() != null) {
                        IOIOUtils.getUtils().setEMag((Activity) activityListener, false);
                    }
                    IOIOUtils.getUtils().setServo(ioio, 500);
                    awake = true;
                    if (isinterrupted) return;
                    wakeScanTimeoutHandler = new Handler();
                    wakeScanTimeoutHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TimeLogger.log("Scan Timeout!");
                            scanSubscription.unsubscribe();
                            activityListener.addFailOrPass(true, false, "", description);
                            return;
                        }
                    }, SCAN_DURATION_MILLIS);
                }
            }, PRE_WAKE_SCAN_DURATION_MILLIS);

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    DecimalFormat df = new DecimalFormat("##.##");
                    df.setRoundingMode(RoundingMode.DOWN);

                    if (IOIOUtils.getUtils().getEmag() != null) {
                        IOIOUtils.getUtils().setEMag((Activity) activityListener, true);
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
            }, 5000);
        }
        return;
    }

    private void onScanFailure(Throwable throwable) {

        if (throwable instanceof BleScanException) {
            handleBleScanException((BleScanException) throwable);
        }
    }

    private void handleBleScanException(BleScanException bleScanException) {

        switch (bleScanException.getReason()) {
            case BleScanException.BLUETOOTH_NOT_AVAILABLE:
                Toast.makeText(PeriCoachTestApplication.getContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
                break;
            case BleScanException.BLUETOOTH_DISABLED:
                Toast.makeText(PeriCoachTestApplication.getContext(), "Enable bluetooth and try again", Toast.LENGTH_SHORT).show();
                break;
            case BleScanException.LOCATION_PERMISSION_MISSING:
                Toast.makeText(PeriCoachTestApplication.getContext(),
                        "On Android 6.0 location permission is required. Implement Runtime Permissions", Toast.LENGTH_SHORT).show();
                break;
            case BleScanException.LOCATION_SERVICES_DISABLED:
                Toast.makeText(PeriCoachTestApplication.getContext(), "Location services needs to be enabled on Android 6.0", Toast.LENGTH_SHORT).show();
                break;
            case BleScanException.BLUETOOTH_CANNOT_START:
            default:
                Toast.makeText(PeriCoachTestApplication.getContext(), "Unable to start scanning", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        scanSubscription.unsubscribe();
        try {
            V_3V0_SW.close();
        } catch (Exception e) {
        }
    }
}
