package com.pietrantuono.tests.implementations.steps;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.implementations.BluetoothConnectTest;
import com.pietrantuono.tests.superclass.Test;

import ioio.lib.api.exception.ConnectionLostException;

public class SetDigitalOutputStep extends Test {
    private int pinNumber;
    private boolean value;

    /**
     * @param pinNumber   , IOIO pin number to set value of
     * @param value       , value to set pinNumber to
     * @param activity
     * @param description
     */
    public SetDigitalOutputStep(Activity activity, int pinNumber, boolean value, String description) {
        super(activity, null, description, false, false);
        this.istest = false;
        this.pinNumber = pinNumber;
        this.value = value;
    }

    @Override
    public void execute() {
        if(isinterrupted)return;
        Log.d(TAG, "Step Executing: " + description);

        if(IOIOUtils.getUtils().getDigitalOutput(pinNumber) != null) {
            try {
                IOIOUtils.getUtils().getDigitalOutput(pinNumber).write(value);
            } catch (ConnectionLostException e) {
                getListener().addFailOrPass(false, false, "IOIO Error");
                e.printStackTrace();
            }
        }

        getListener().addFailOrPass(false, true, "");

        if(isinterrupted)return;

    }

}
