package com.pietrantuono.tests.implementations.steps;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.pietrantuono.tests.implementations.BluetoothConnectTest;
import com.pietrantuono.tests.superclass.Test;

public class SetSensorVoltagesStep extends Test implements Step {
    private short refVoltage;
    private short zeroVoltage;

    /**
     * IMPORTANT: Bluetooth must be open using
     * {@link BluetoothConnectTest}
     * Do not execute this step before opening Bluetooth
     *
     * @param refVoltage  , sensor gain voltage (0 to 255, -1 if not setting)
     * @param zeroVoltage , sensor zero offset voltage (0 to 255, -1 if not setting)
     * @param activity
     * @param description
     */
    public SetSensorVoltagesStep(Activity activity, short refVoltage, short zeroVoltage, String description) {
        super(activity, null, description, false, false, 0, 0, 0);
        this.istest = false;
        this.refVoltage = refVoltage;
        this.zeroVoltage = zeroVoltage;
    }

    @Override
    public void execute() {
        Log.d(TAG, "Executing: " + description);
        final HandlerThread handlerThread = new HandlerThread("SetSensorVoltagesStep");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        Log.d(TAG,"Looper of the HandlerThread ="+handlerThread.getLooper().toString());
        Log.d(TAG,"Looper of UI thread ="+ Looper.getMainLooper().toString());
        if (isinterrupted) return;
        Log.d(TAG, "Executing: " + description);
        if (getListener().getBtutility() == null) {
            report("BU utility is null");
            getListener().addFailOrPass(false, false, "BT ERROR");
            return;
        }
        if (refVoltage != -1) {
            try {
                getListener().getBtutility().setVoltage(refVoltage);
            } catch (Exception e) {
                getListener().addFailOrPass(true, false, "ERROR", "UUT Comms Fault");
                return;
            }
        }
        if (isinterrupted) return;
        if (zeroVoltage != -1) {
            try {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "setZeroVoltage " + zeroVoltage);
                        getListener().getBtutility().setZeroVoltage(zeroVoltage);
                    }
                }, 100);
            } catch (Exception e) {
                getListener().addFailOrPass(true, false, "ERROR", "UUT Comms Fault");
                return;
            }
        }
        if (isinterrupted) return;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getListener().getBtutility().pollSensor();
            }
        }, 1000);
        if (isinterrupted) return;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getListener().addFailOrPass(false, true, "");
            }
        }, 2000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handlerThread.quitSafely();
            }
        }, 3000);

        if (isinterrupted) return;

    }

}
