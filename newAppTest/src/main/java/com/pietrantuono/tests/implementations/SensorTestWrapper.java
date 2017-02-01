package com.pietrantuono.tests.implementations;

import ioio.lib.api.IOIO;

import android.app.Activity;
import android.util.Log;

import com.pietrantuono.sensors.ClosedTest;
import com.pietrantuono.sensors.SensorTest;
import com.pietrantuono.sensors.SensorsTestHelper;
import com.pietrantuono.tests.superclass.SimpleAsyncTask;
import com.pietrantuono.tests.superclass.Test;

public class SensorTestWrapper extends Test {
    private boolean isClosedTest;
    @SuppressWarnings("unused")
    private int TestLimitIndex = 0;
    private short voltage;
    private short zeroVoltage;
    private final SensorTest sensorTest;
    private SensorsTestHelper helper;
    private Boolean load;
    private Boolean hasPrompt;
    private Boolean singleSensorTest = false;
    private int sensorToTest = 0;
    private String weight = "";
    public boolean executed =false;

    /**
     * Creates a sensor test.
     * IMPORTANT: Bluetooth must be open using
     * {@link com.pietrantuono.tests.implementations.BluetoothConnectTest}
     * Do not execute this test before opening Bluetooth
     *
     * @param description    : Description of test, sensor test must contain words (NO LOAD or LOADED), and (GAIN @ x), where x is voltage from 0 to 255. Used as parameters for test.
     * @param TestLimitIndex : index of the test limit to be used in the test
     * @param lowerLimit
     * @param upperLimit
     * @param varLimit
     */
    public SensorTestWrapper(boolean isClosedTest, Activity activity, IOIO ioio, int TestLimitIndex, float lowerLimit, float upperLimit, float varLimit, boolean hasPrompt, String description) {
        super(activity, ioio, description, true, false, lowerLimit, upperLimit, varLimit);
        this.TestLimitIndex = TestLimitIndex;
        this.load = null;
        this.hasPrompt = hasPrompt;

        final String[] parts = description.split(",");
        // Check description to see if this sensor test is an individual sensor test
        // DB Description should be "Sensor 0 Input Test, LOADED, GAIN/ZERO @ 127/0"
        if (parts[0].length() == 19){
            singleSensorTest = true;
            sensorToTest = Character.digit(parts[0].charAt(7), 10);
        }
        if (parts[1].contains("LOADED")) {
            this.isClosedTest = isClosedTest;
            this.load = true;
        } else if (parts[1].contains("NO LOAD")) {
            this.isClosedTest = false;
            this.load = false;
        } else if (parts[1].length() >= 8 && parts[1].contains(" LOAD")) {
            this.isClosedTest = isClosedTest;
            this.load = true;
            weight = parts[1].substring(1, parts[1].indexOf("g") + 1) + " ";
        }

        Log.d(TAG, "Sensor Load is " + this.load);
        this.voltage = -1;
        this.zeroVoltage = -1;

        if (description != null && description.contains("GAIN/ZERO @")) {
            String tmp = description.substring(description.indexOf("GAIN/ZERO @") + 12, description.length());
            this.voltage = Short.valueOf(tmp.substring(0, tmp.indexOf("/")));
            this.zeroVoltage = Short.valueOf(tmp.substring(tmp.indexOf("/") + 1, tmp.length()));
        }

        Log.d(TAG, "Sensor Gain Voltage is " + this.voltage);
        Log.d(TAG, "Sensor Zero Voltage is " + this.zeroVoltage);

        if (this.isClosedTest) {
            sensorTest = new ClosedTest(activity, SensorTestWrapper.this, singleSensorTest, sensorToTest, hasPrompt, weight, lowerLimit, upperLimit, varLimit);
        } else {
            sensorTest = new SensorTest(activity, SensorTestWrapper.this, lowerLimit, upperLimit, varLimit);
        }
    }

    @Override
    public void execute() {
        executed=true;
        if (!isClosedTest) {
            new SensorTestWrapperAsyncTask().executeParallel();
        } else {
            ((Activity) activityListener).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    helper = new SensorsTestHelper((Activity) activityListener);
                    sensorTest.setSensorsTestHelper(helper);
                    sensorTest.execute();
                }
            });
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            sensorTest.interrupt();
        } catch (Exception e) {
        }
        try {
            helper.stop();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean isSuccess() {
        return sensorTest.getOverallResult();
    }

    public short getVoltage() {
        return voltage;
    }

    public short getZeroVoltage() {
        return zeroVoltage;
    }

    public Boolean getLoad() {
        return load;
    }

    public SensorTest getSensorTest() {
        return sensorTest;
    }

    @Override
    public void setTestToBeParsed(server.pojos.Test testToBeParsed) {
        super.setTestToBeParsed(testToBeParsed);
        sensorTest.setTestToBeParsed(testToBeParsed);
    }

    private class SensorTestWrapperAsyncTask extends SimpleAsyncTask {
        @Override
        protected Void doInBackground(Void... params) {
            if (isinterrupted) return null;
            helper = new SensorsTestHelper((Activity) activityListener);
            sensorTest.setSensorsTestHelper(helper);
            sensorTest.execute();
            return null;
        }
    }


}
