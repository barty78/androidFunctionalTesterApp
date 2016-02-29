package com.pietrantuono.tests.implementations.dummies;

import com.pietrantuono.sensors.SensorTest;
import com.pietrantuono.sensors.SensorTestCallback;

import server.pojos.Test;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class DummySensorTest extends SensorTest {
    private boolean shouldFail;
    private Test testToBeParsed;

    @Override
    public void execute() {
        if(shouldFail){
            mSensorResult.setTestsuccessful(false);
            ((SensorTestCallback) (activity.get())).onSensorTestCompleted(mSensorResult, testToBeParsed);

        }
        else {
            mSensorResult.setTestsuccessful(true);
            ((SensorTestCallback) (activity.get())).onSensorTestCompleted(mSensorResult, testToBeParsed);
        }
    }

    public void shuouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public void setTestToBeParsed(server.pojos.Test testToBeParsed) {
        this.testToBeParsed = testToBeParsed;
    }
}
