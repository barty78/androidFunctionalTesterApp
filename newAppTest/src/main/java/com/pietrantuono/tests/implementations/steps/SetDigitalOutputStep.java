package com.pietrantuono.tests.implementations.steps;

import android.app.Activity;
import android.util.Log;

import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.implementations.AccelerometerSelfTest;
import com.pietrantuono.tests.superclass.SimpleAsyncTask;
import com.pietrantuono.tests.superclass.Test;

import ioio.lib.api.exception.ConnectionLostException;

public class SetDigitalOutputStep extends Test implements Step{
    private final int pinNumber;
    private boolean value;

    /**
     * @param pinNumber   , IOIO pin number to set value of
     * @param value       , value to set pinNumber to
     * @param activity
     * @param description
     */
    public SetDigitalOutputStep(Activity activity, int pinNumber, boolean value, String description) {
        super(activity, null, description, false, false, 0, 0, 0);
        this.istest = false;
        this.pinNumber = pinNumber;
        this.value = value;
    }

    @Override
    public void execute() {
        new SetDigitalOutputStepAsyncTask().executeParallel();
    }

    private class SetDigitalOutputStepAsyncTask extends SimpleAsyncTask{
        @Override
        protected Void doInBackground(Void... params) {
            if(isinterrupted)return null;
            Log.d(TAG, "Step Executing: " + description);

            if(IOIOUtils.getUtils().getDigitalOutput(pinNumber) != null) {
                try {
                    IOIOUtils.getUtils().getDigitalOutput(pinNumber).write(value ^= true);
                } catch (ConnectionLostException e) {
                    getListener().addFailOrPass(false, false, "IOIO Error", description);
                    e.printStackTrace();
                }
            }

            getListener().addFailOrPass(false, true, "", description);

            return null;

        }
    }

}
