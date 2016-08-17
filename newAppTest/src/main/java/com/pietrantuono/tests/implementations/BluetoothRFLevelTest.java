package com.pietrantuono.tests.implementations;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.tests.superclass.Test;
import com.pietrantuono.timelogger.TimeLogger;
import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.RxBleScanResult;
import com.polidea.rxandroidble.exceptions.BleScanException;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;
import rx.Observer;
import rx.Subscription;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BluetoothRFLevelTest extends Test {

    private static final int SCAN_DURATION_MILLIS = 2000;
    private Subscription scanSubscription;
    private RxBleClient rxBleClient;
    private int scanCount = 0;
    private int sum = 0;
    private int limit;

    public BluetoothRFLevelTest(Activity activity, IOIO ioio, int limit) {
        super(activity, ioio, "Bluetooth Signal Level", false, true, 0, 0, 0);
        this.limit = limit;
    }

    @Override
    public void execute() {

        if (isinterrupted) return;
        if (activityListener.getMac() == null) {
            activityListener.addFailOrPass(true, false, "No Address", description);
            return;
        }
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
                        if (isinterrupted) return;

                        if (rxBleScanResult.getBleDevice().getMacAddress().equalsIgnoreCase(activityListener.getMac())) {
                            TimeLogger.log("Device Found - " + rxBleScanResult.getBleDevice().getMacAddress() + " - " + rxBleScanResult.getRssi() + "db");
                            sum += rxBleScanResult.getRssi();
                            scanCount++;
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
                if (isinterrupted) return;
                TimeLogger.log("Scanning Stopped" + " | Sum = " + sum + " | Count = " + scanCount + " | Limit = " + limit);
                scanSubscription.unsubscribe();
                if (scanCount > 0 && (sum/scanCount) >= limit) {
                    Success();
                    activityListener.addFailOrPass(true, true, String.valueOf(sum/scanCount) + "dB", description);
                } else {
                    if (scanCount > 0) {
                        activityListener.addFailOrPass(true, false, String.valueOf(sum/scanCount) + "dB", description);
                    } else {
                        activityListener.addFailOrPass(true, false, "No Records", description);
                    }
                }
                return;
            }
        }, SCAN_DURATION_MILLIS);
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
    }
}
