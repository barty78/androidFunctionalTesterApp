package com.pietrantuono.sensors;

import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.constants.SensorResult;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.tests.ErrorCodes;
import com.pietrantuono.tests.implementations.SensorTestWrapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import customclasses.DebugHelper;
import hydrix.pfmat.generic.Device;
import hydrix.pfmat.generic.Force;
import hydrix.pfmat.generic.SessionSamples;

public class ClosedTest extends SensorTest implements OnDetectCallback {
    private boolean interrupted;
    private AlertDialog dialog;
    private boolean singleSensorTest;
    private int sensorToTest;
    private boolean hasPrompt;
    private String weight;

    public static final String title = "Closed Test";

    public ClosedTest(Activity activity, SensorTestWrapper wrapper, boolean singleSensorTest, int sensorToTest, boolean hasPrompt, String weight, float lowerLimit, float upperLimit, float varLimit) {
        super(activity, wrapper, lowerLimit, upperLimit, varLimit);
        this.singleSensorTest = singleSensorTest;
        this.sensorToTest = sensorToTest;
        this.hasPrompt = hasPrompt;
        this.weight = weight;
    }

    public ClosedTest setVoltage(short voltage) {
        this.voltage = voltage;
        return this;
    }

    public ClosedTest setLoad(Boolean load) {
        this.load = load;
        return this;
    }

    public void execute() {
        if (stopped) return;
        if (interrupted) {
            wrapper.setErrorcode((long) ErrorCodes.SENSOR_TEST_BT_CONNECTION_LOST);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Test interrupted", true, testToBeParsed);
            return;
        }
        sensorsTestHelper.setOnDetectCallback(this);
        sensorsTestHelper.closedtestsamples[SENSOR0].clear();
        sensorsTestHelper.closedtestsamples[SENSOR1].clear();
        sensorsTestHelper.closedtestsamples[SENSOR2].clear();
        Log.d("SensorTest", "execute");

        if (this.activity == null || activity == null) {
            Log.e(SensorsTestHelper.TAG, "You must set the activity");
            wrapper.setErrorcode((long) ErrorCodes.SENSOR_TEST_NO_ACTIVITY_SET);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Closed Sensor test", true, testToBeParsed);
            return;
        }
        if (this.voltage == -1) {
            Log.e(SensorsTestHelper.TAG, "You must set the voltage");
            wrapper.setErrorcode((long) ErrorCodes.SENSOR_TEST_NO_VOLTAGE_SET);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Closed ensor test", true, testToBeParsed);
            return;
        }
        if (this.sensorsTestHelper.samplesref == null) {
            Log.e(SensorsTestHelper.TAG, "samplesref null?!");
            wrapper.setErrorcode((long) ErrorCodes.SENSOR_TEST_SAMPLES_REF_NULL);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Closed Sensor test", true, testToBeParsed);
            return;
        }
        if (load == null) {
            Log.e(SensorsTestHelper.TAG, "load null?!");
            wrapper.setErrorcode((long) ErrorCodes.SENSOR_TEST_LOAD_NULL);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Closed Sensor test", true, testToBeParsed);
            return;
        }
        if (mSensorResult == null) {
            Log.e(SensorsTestHelper.TAG, "mSensorResult null?!");
            wrapper.setErrorcode((long) ErrorCodes.SENSOR_TEST_SENSOR_RESULT_NULL);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Closed Sensor test", true, testToBeParsed);
            return;
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!DebugHelper.isMaurizioDebug()) {
            Log.d(SensorsTestHelper.TAG, "Model Check - " + sensorsTestHelper.getModel() + " : " + NewDevice.V2);
            if (sensorsTestHelper.getModel().equals(NewDevice.V2)) {
                Log.d(SensorsTestHelper.TAG, "V2 Voltage Setting");
                try {
                    this.sensorsTestHelper.sendV2Voltage(voltage);
                } catch (Exception e) {
                    e.printStackTrace();
                    wrapper.setErrorcode((long) ErrorCodes.SENSORTEST_VOLTAGE_SETTING_FAILED);
                    ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test - Setting Voltages Failed", true, testToBeParsed);
                    return;
                }
            } else {
                Log.d(SensorsTestHelper.TAG, "V3 Voltage Setting");
                try {
                    this.sensorsTestHelper.sendAllVoltages(voltage, zeroVoltage);
                } catch (Exception e) {
                    e.printStackTrace();
                    wrapper.setErrorcode((long) ErrorCodes.SENSORTEST_VOLTAGE_SETTING_FAILED);
                    ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test - Setting Voltages Failed", true, testToBeParsed);
                    return;
                }
            }
        }

        sensorsTestHelper.getNewSessionPollingThreadref().start();
        this.sensorsTestHelper.samplesref.clear();
        if (this.sensorsTestHelper.activityref == null || this.sensorsTestHelper.activityref.get() == null)
            return;
        if (singleSensorTest) mSensorResult.singleSensorTest();
        if (!singleSensorTest || hasPrompt) {

            Builder builder = new Builder(this.sensorsTestHelper.activityref.get());
            builder.setTitle("Closed test");

            builder.setMessage("Place " + weight + "Test Weight on Sensor " + sensorToTest + " and press OK");
            builder.setPositiveButton("OK", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sensorsTestHelper.acceptData(true);
                    executeSensor();
                }
            });

            builder.setCancelable(false);
            dialog = builder.create();
            dialog.show();
            setSamplingSensor(NO_SENSORS);

//            setupDetection(sensorToTest);   //TODO - Wrap this in an option and implement timeout

        } else {
            sensorsTestHelper.acceptData(true);
            executeSensor();
        }
    }

    public SensorResult endTest() {
        if (stopped) return mSensorResult;
        if (interrupted) {
            wrapper.setErrorcode((long) ErrorCodes.SENSOR_TEST_BT_CONNECTION_LOST);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Test interrupted", true, testToBeParsed);
            return mSensorResult;
        }
        setSamplingSensor(NO_SENSORS);
        final CardView layout = (CardView) this.sensorsTestHelper.activityref.get()
                .findViewById(R.id.sensors);
        this.sensorsTestHelper.activityref.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.setVisibility(View.VISIBLE);
            }
        });
        if (stopped)
            return mSensorResult;
        Log.d("SensorTest", "endTest");

        checkSampleSize();

        if (singleSensorTest){
            evaluateSensorSamples(sensorToTest, this.sensorsTestHelper.closedtestsamples[sensorToTest]);
            boolean[] result = checkResult(mSensorResult.getSensor(sensorToTest));
            mSensorResult.setSensorAvgPass(sensorToTest, result[AVG_TEST]);
            mSensorResult.setSensorStabilityPass(sensorToTest, result[VAR_TEST]);
            if (!result[AVG_TEST] || !result[VAR_TEST]) mSensorResult.setTestsuccessful(false);

        } else {
            // Parse Sensor 0 Results
            evaluateSensorSamples(SENSOR0, this.sensorsTestHelper.closedtestsamples[SENSOR0]);
            boolean[] result = checkResult(mSensorResult.getSensor(SENSOR0));
            mSensorResult.setSensorAvgPass(SENSOR0, result[AVG_TEST]);
            mSensorResult.setSensorStabilityPass(SENSOR0, result[VAR_TEST]);
            if (!result[AVG_TEST] || !result[VAR_TEST]) mSensorResult.setTestsuccessful(false);

            // Parse Sensor 1 Results
            evaluateSensorSamples(SENSOR1, this.sensorsTestHelper.closedtestsamples[SENSOR1]);
            result = checkResult(mSensorResult.getSensor(SENSOR1));
            mSensorResult.setSensorAvgPass(SENSOR1, result[AVG_TEST]);
            mSensorResult.setSensorStabilityPass(SENSOR1, result[VAR_TEST]);
            if (!result[AVG_TEST] || !result[VAR_TEST]) mSensorResult.setTestsuccessful(false);

            // Parse Sensor 2 Results
            evaluateSensorSamples(SENSOR2, this.sensorsTestHelper.closedtestsamples[SENSOR2]);
            result = checkResult(mSensorResult.getSensor(SENSOR2));
            mSensorResult.setSensorAvgPass(SENSOR2, result[AVG_TEST]);
            mSensorResult.setSensorStabilityPass(SENSOR2, result[VAR_TEST]);
            if (!result[AVG_TEST] || !result[VAR_TEST]) mSensorResult.setTestsuccessful(false);
        }
        if (activity != null && activity != null && !isTest)
            ((SensorTestCallback) (activity.get())).onSensorTestCompleted(mSensorResult, testToBeParsed);
        if (!isTest && !DebugHelper.isMaurizioDebug()) try {
            this.sensorsTestHelper.sendVoltages(this.sensorsTestHelper.NORMAL_VOLTAGE, (short) 0);
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (!isTest) stop();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sensorsTestHelper.stop();
        return mSensorResult;
    }

    private void evaluateSensorSamples(int sensor, SessionSamples sessionsamples) {

        SessionSamples tempSamples = new SessionSamples(this.sensorsTestHelper.INITIAL_FREEMODE_SAMPLE_CAPACITY);
        int numberOfSamplesTocopy = sessionsamples.mSamples.size() < this.sensorsTestHelper.INITIAL_FREEMODE_SAMPLE_CAPACITY
                ? sessionsamples.mSamples.size()
                : this.sensorsTestHelper.INITIAL_FREEMODE_SAMPLE_CAPACITY;
        for (int i = 0; i < numberOfSamplesTocopy; i++) {
            tempSamples.add(sessionsamples.mSamples.get(i).mTimeOffsetMS,
                    sessionsamples.mSamples.get(i).mForce);
        }
        float[] sensors = {0.0f, 0.0f, 0.0f};

        int numSamples = tempSamples.mSamples.size();
        for (int i = 0; i < numSamples; i++) {
            Force force = tempSamples.mSamples.get(i).mForce;
            sensors[sensor] += force.getLiteralSensor(sensor);
        }

        mSensorResult.setTestsuccessful(true);
        mSensorResult.setSensor(sensor, tempSamples.getMinSampleSeen().getLiteralSensor(sensor),
                                                tempSamples.getMaxSampleSeen().getLiteralSensor(sensor),
                                                (short) (sensors[sensor] / numSamples));
    }

    private void setSamplingSensor(int sensor) {
        boolean sensors[] = {false, false, false};
        if (sensor != -1) sensors[sensor] = true;
        sensorsTestHelper.acceptData(sensors[SENSOR0] || sensors[SENSOR1] || sensors[SENSOR2]);
        sensorsTestHelper.samplingSensor0 = sensors[SENSOR0];
        sensorsTestHelper.samplingSensor1 = sensors[SENSOR1];
        sensorsTestHelper.samplingSensor2 = sensors[SENSOR2];
    }

    private void setupDetection(int sensor) {
        sensorsTestHelper.detectWeight = true;
        sensorsTestHelper.detectSensor = sensor;
    }

    private void displayDialog(final String msg) {
        Handler h = new Handler(Looper.getMainLooper());
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                setSamplingSensor(-1);
                AlertDialog.Builder builder = new Builder(ClosedTest.this.sensorsTestHelper.activityref.get());
                builder.setTitle(title);
                builder.setMessage(msg);
                builder.setPositiveButton("OK", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executeSensor();
                    }
                });
                builder.setCancelable(false);
                dialog = builder.create();
                dialog.show();

//                setupDetection(sensorToTest);             //TODO - Wrap this in an option and implement timeout

            }
        }, SensorsTestHelper.CALIBRATION_TIME_MS * 1);
    }

    private void executeSensor() {
        if (stopped) return;
        if (interrupted) {
            wrapper.setErrorcode((long) ErrorCodes.SENSOR_TEST_BT_CONNECTION_LOST);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Test interrupted", true, testToBeParsed);
            return;
        }
        setSamplingSensor(sensorToTest);
        setSensorCardViewProperties(sensorToTest);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.sensorsTestHelper.closedtestsamples[sensorToTest].clear();
        if (!singleSensorTest) {
            if (sensorToTest < 2) {
                sensorToTest++;
                displayDialog("Place " + weight + "Test Weight on Sensor " + sensorToTest + " and press OK");
            } else {
                Handler h = new Handler(Looper.getMainLooper());
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSamplingSensor(NO_SENSORS);
                        endTest();
                    }
                }, SensorsTestHelper.CALIBRATION_TIME_MS * 1);
            }
        } else {
            Handler h = new Handler(Looper.getMainLooper());
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setSamplingSensor(NO_SENSORS);
                    endTest();
                }
            }, SensorsTestHelper.CALIBRATION_TIME_MS * 1);
        }
    }

    public boolean getOverallResult() {
        return mSensorResult.isTestsuccessful();
    }

    public SensorResult getSensorResult() {
        return mSensorResult;
    }


    @Override
    public void interrupt() {
        interrupted = true;
        stop();
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onWeightDetected() {

    }
}