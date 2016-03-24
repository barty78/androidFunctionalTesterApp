package com.pietrantuono.tests.implementations;

import android.app.Activity;

import com.pietrantuono.tests.superclass.Test;

import ioio.lib.api.IOIO;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class BLEWakeTest extends Test {
    /**
     * @param activity
     * @param ioio
     * @param description
     * @param isSensorTest
     * @param isBlockingTest
     * @param limitParam1
     * @param limitParam2
     * @param limitParam3
     */
    protected BLEWakeTest(Activity activity, IOIO ioio, String description, Boolean isSensorTest, Boolean isBlockingTest, float limitParam1, float limitParam2, float limitParam3) {
        super(activity, ioio, description, isSensorTest, isBlockingTest, limitParam1, limitParam2, limitParam3);
    }

    @Override
    public void execute() {

    }
}
