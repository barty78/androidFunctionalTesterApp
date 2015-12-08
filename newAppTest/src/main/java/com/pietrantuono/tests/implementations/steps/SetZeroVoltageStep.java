package com.pietrantuono.tests.implementations.steps;

import android.app.Activity;
import android.util.Log;

import com.pietrantuono.tests.implementations.BluetoothConnectTest;
import com.pietrantuono.tests.superclass.Test;

public class SetZeroVoltageStep extends Test {
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
    public SetZeroVoltageStep(Activity activity, short voltage, String description) {
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
           getListener().getBtutility().setZeroVoltage(voltage);
        } catch (Exception e) {
            getListener().addFailOrPass(true, false, "ERROR", "UUT Comms Fault");
            return;
        }

        if(isinterrupted)return;
        try {
            Thread.sleep(2 * 1000);
        } catch (Exception e) {
            getListener().addFailOrPass(true, false, "ERROR", "App Fault");

        }

        if(isinterrupted)return;

    }

}
