package com.pietrantuono.tests.implementations.steps;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.pietrantuono.tests.implementations.BluetoothConnectTest;
import com.pietrantuono.tests.superclass.Test;

public class PauseStep extends Test {

    /**
     * IMPORTANT: Bluetooth must be open using
     * {@link BluetoothConnectTest}
     * Do not execute this step before opening Bluetooth
     *
     * @param activity
     * @param description
     */
    public PauseStep(Activity activity, String description) {
        super(activity, null, description, false, false);
    }

    @Override
    public void execute() {
        if(isinterrupted)return;

    }

}
