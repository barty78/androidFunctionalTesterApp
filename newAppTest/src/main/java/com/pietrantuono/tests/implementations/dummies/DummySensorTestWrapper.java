package com.pietrantuono.tests.implementations.dummies;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.pietrantuono.tests.implementations.SensorTestWrapper;

import ioio.lib.api.IOIO;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class DummySensorTestWrapper extends SensorTestWrapper {
    private boolean shouldFail;
    DummySensorTest dummySensorTest;

    public DummySensorTestWrapper(boolean isClosedTest, @NonNull Activity activity, IOIO ioio, int TestLimitIndex, float lowerLimit, float upperLimit, float varLimit, String description) {
        super(isClosedTest, activity, ioio, TestLimitIndex, lowerLimit, upperLimit, varLimit, description);
        dummySensorTest= new DummySensorTest(activity,this,0,0,0);
    }

    @Override
    public void execute() {
        dummySensorTest.shuouldFail(shouldFail);
        dummySensorTest.execute();
    }


    public void shuouldFail(boolean isSuccessful) {
        this.shouldFail = isSuccessful;
    }
    @Override
    public void setTestToBeParsed(server.pojos.Test testToBeParsed) {
        dummySensorTest.setTestToBeParsed(testToBeParsed);
    }
}
