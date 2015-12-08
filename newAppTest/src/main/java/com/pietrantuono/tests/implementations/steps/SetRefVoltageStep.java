package com.pietrantuono.tests.implementations.steps;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.implementations.BluetoothConnectTest;
import com.pietrantuono.tests.superclass.Test;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class SetRefVoltageStep extends Test {
    private short voltage;

    /**
     * IMPORTANT: Bluetooth must be open using
     * {@link BluetoothConnectTest}
     * Do not execute this step before opening Bluetooth
     *
     * @param voltage
     * @param activity
     * @param description
     */
    public SetRefVoltageStep(Activity activity, short voltage, String description) {
        super(activity, null, description, false, false);
        this.voltage = voltage;
    }

    @Override
    public void execute() {
        if(isinterrupted)return;
        Log.d(TAG, "Test Starting: " + description);
        if(getListener().getBtutility()==null){
            report("BU utility is null");
            getListener().addFailOrPass(false, false, "BT ERROR");
            return;}

        try {
           getListener().getBtutility().setVoltage(voltage);
        } catch (Exception e) {
            getListener().addFailOrPass(true, false, "ERROR", "UUT Comms Fault");
            return;
        }

        if(isinterrupted)return;
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getListener().addFailOrPass(false, true, "");
            }
        }, 2000);

        if(isinterrupted)return;

    }

}
