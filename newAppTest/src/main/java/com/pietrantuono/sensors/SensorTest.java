package com.pietrantuono.sensors;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeoutException;

import com.pietrantuono.activities.MainActivity;
import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.tests.ErrorCodes;
import com.pietrantuono.tests.implementations.SensorTestWrapper;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import customclasses.DebugHelper;
import hydrix.pfmat.generic.Force;
import hydrix.pfmat.generic.SessionSamples;
import server.pojos.Test;

@SuppressWarnings("unused")
public class SensorTest {

    private static final int DELAY = 1 * 1000;
    public final float lowerLimit;
    public final float upperLimit;
    public final float varLimit;
    public final SensorTestWrapper wrapper;
    protected SensorsTestHelper sensorsTestHelper;
    protected WeakReference<Activity> activity = null;
    protected short voltage = -1;
    protected short zeroVoltage = -1;
    protected Boolean load = null;
    protected NewMSensorResult mSensorResult = null;
    protected Boolean stopped = false;
    public boolean isTest = false;
    Test testToBeParsed;
    private boolean interrupted;
    int items[];

    public static final int NO_SENSORS = -1;
    public static final int SENSOR0 = 0;
    public static final int SENSOR1 = 1;
    public static final int SENSOR2 = 2;

    public static final int AVG_TEST = 0;
    public static final int VAR_TEST = 1;

    public void setSensorsTestHelper(SensorsTestHelper sensorsTestHelper) {
        this.sensorsTestHelper = sensorsTestHelper;
    }

    public SensorTest(Activity activity, SensorTestWrapper wrapper, float lowerLimit, float upperLimit, float varLimit) {
        Log.d("SensorTest", "constucor");
        this.activity = new WeakReference<Activity>(activity);
        this.mSensorResult = new NewMSensorResult(wrapper);
        this.mSensorResult.setDescription(wrapper.getDescription());
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.varLimit = varLimit;
        this.voltage = wrapper.getVoltage();
        this.zeroVoltage = wrapper.getZeroVoltage();
        this.load = wrapper.getLoad();
        this.wrapper = wrapper;
    }

    public void stop() {
        stopped = true;
        try {
            final CardView layout = (CardView) this.sensorsTestHelper.activityref.get().findViewById(R.id.sensors);
            this.sensorsTestHelper.activityref.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layout.setVisibility(View.INVISIBLE);
                    SensorTest.this.sensorsTestHelper.sensor0ref.setText("0");
                    SensorTest.this.sensorsTestHelper.sensor0ref.setText("0");
                    SensorTest.this.sensorsTestHelper.sensor0ref.setText("0");
                }
            });
        } catch (Exception e) {
        }
    }

    @SuppressWarnings("unused")
    public SensorTest setZeroVoltage(short voltage) {
        this.zeroVoltage = voltage;
        return this;
    }

    @SuppressWarnings("unused")
    public SensorTest setVoltage(short voltage) {
        this.voltage = voltage;
        return this;
    }

    @SuppressWarnings("unused")
    public SensorTest setLoad(Boolean load) {
        this.load = load;
        return this;
    }

    public boolean[] checkResult(short[] sensor) {
        boolean[] result = {false, false};
        if (sensor[NewMSensorResult.avg] <= upperLimit && sensor[NewMSensorResult.avg] >= lowerLimit) {
            result[AVG_TEST] = true;
        }
        if (Math.abs(sensor[NewMSensorResult.max] - sensor[NewMSensorResult.min]) < varLimit) {
            result[VAR_TEST] = true;
        }
        return result;
    }

    public void execute() {
        if (stopped) return;
        if (interrupted) {
            wrapper.setErrorcode((long) ErrorCodes.SENSOR_TEST_BT_CONNECTION_LOST);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test - BT connection lost", true, testToBeParsed);
            return;
        }
        sensorsTestHelper.samplesref.clear();
        sensorsTestHelper.samplingSensor0 = true;
        sensorsTestHelper.samplingSensor1 = true;
        sensorsTestHelper.samplingSensor2 = true;

        setSensorCardViewProperties(NO_SENSORS);

        Log.d("SensorTest", "execute");
        if (this.activity == null || activity == null) {
            Log.e(SensorsTestHelper.TAG, "You must set the activity");
            wrapper.setErrorcode((long) ErrorCodes.SENSORTEST_ACTIVITY_ERROR);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test - Activity Error", true, testToBeParsed);
            return;
        }
        if (this.voltage == -1) {
            Log.e(SensorsTestHelper.TAG, "You must set the voltage");
            wrapper.setErrorcode((long) ErrorCodes.SENSORTEST_NO_DRIVE_VOLTAGE_SET);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test - No Set Voltage", true, testToBeParsed);
            return;
        }
        if (this.zeroVoltage == -1) {
            Log.e(SensorsTestHelper.TAG, "You must set the zeroing voltage");
            wrapper.setErrorcode((long) ErrorCodes.SENSORTEST_NO_ZERO_VOLTAGE_SET);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test - No Set Zeroing Voltage", true, testToBeParsed
            );
            return;
        }
        if (load == null) {
            Log.e(SensorsTestHelper.TAG, "load null?!");
            wrapper.setErrorcode((long) ErrorCodes.SENSORTEST_NO_LOAD_SET);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test - No Load Set", true, testToBeParsed);
            return;
        }
        if (this.sensorsTestHelper.samplesref == null) {
            Log.e(SensorsTestHelper.TAG, "samplesref null?!");
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test - No Samples Ref", true, testToBeParsed);
            return;
        }
        if (mSensorResult == null) {
            Log.e(SensorsTestHelper.TAG, "mSensorResult null?!");
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test - No Sensor Result Object", true, testToBeParsed);
            return;
        }
//		try {
//			Thread.sleep(200);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
        if (load)
            try {
                IOIOUtils.getUtils().getSensor_High().write(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        else
            try {
                IOIOUtils.getUtils().getSensor_High().write(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        if (!DebugHelper.isMaurizioDebug()) {
            try {
//                this.sensorsTestHelper.sendVoltages(voltage, zeroVoltage);
                this.sensorsTestHelper.sendAllVoltages(voltage, zeroVoltage);
            } catch (TimeoutException | NewDevice.InvalidVoltageException e) {
                e.printStackTrace();
                wrapper.setErrorcode((long) ErrorCodes.SENSORTEST_VOLTAGE_SETTING_FAILED);
                ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test - Setting Voltages Failed", true, testToBeParsed);
                return;
            }
        }
        sensorsTestHelper.getNewSessionPollingThreadref().start();
        HandlerThread handlerThread = new HandlerThread("Sensor test handler thread");
        handlerThread.start();

        Handler h = new Handler(handlerThread.getLooper());


        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    NewPFMATDevice.getDevice().getOutputStream().flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sensorsTestHelper.acceptData(true);
                SensorTest.this.sensorsTestHelper.samplesref.clear();
                Log.d("execute", "executin first runnable");
            }
        }, DELAY);
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("execute", "executin first runnable");
                endTest();
            }
        }, (SensorsTestHelper.CALIBRATION_TIME_MS * 1) + DELAY);
    }

    public NewMSensorResult checkSampleSize() {
        if (this.sensorsTestHelper.samplesref == null) {
            Log.d(SensorsTestHelper.TAG, "samplesref == null " + (this.sensorsTestHelper.samplesref == null));
            wrapper.setErrorcode((long) ErrorCodes.SENSORTEST_INSUFFICIENT_SAMPLES);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test - No Samples", true, testToBeParsed);
            return mSensorResult;
        }

        if (this.sensorsTestHelper.samplesref.mSamples == null) {
            Log.d(SensorsTestHelper.TAG, "Samples size = " + this.sensorsTestHelper.samplesref.mSamples.size());
            wrapper.setErrorcode((long) ErrorCodes.SENSORTEST_INSUFFICIENT_SAMPLES);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test - No Samples", true, testToBeParsed);
            return mSensorResult;
        }

        if (this.sensorsTestHelper.samplesref == null || this.sensorsTestHelper.samplesref.mSamples == null
                || this.sensorsTestHelper.samplesref.mSamples.size() < this.sensorsTestHelper.CALIBRATION_MIN_SAMPLES) {

            if (activity.get() != null && !activity.get().isFinishing()
                    && !((MainActivity) (activity.get())).isMainActivityBeingDestroyed()) {
                wrapper.setErrorcode((long) ErrorCodes.SENSORTEST_INSUFFICIENT_SAMPLES);
                ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor TEST - Insufficient Samples", true, testToBeParsed);
                return mSensorResult;
            }
            Toast.makeText(activity.get(),
                    "Error taking measure, please check Bluetooth and PeriCoach device and restart test",
                    Toast.LENGTH_LONG).show();
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test", true, testToBeParsed);
            return mSensorResult;
        }
        return mSensorResult;
    }

    public NewMSensorResult endTest() {
        if (stopped) return mSensorResult;
        if (interrupted) {
            wrapper.setErrorcode((long) ErrorCodes.SENSOR_TEST_BT_CONNECTION_LOST);
            ((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test - BT connection lost", true, testToBeParsed);
            return mSensorResult;
        }
        sensorsTestHelper.acceptData(false);
        sensorsTestHelper.stop();

        final CardView layout = (CardView) this.sensorsTestHelper.activityref.get().findViewById(R.id.sensors);
        this.sensorsTestHelper.activityref.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.setVisibility(View.VISIBLE);
            }
        });

        if (stopped)
            return mSensorResult;
        Log.d("SensorTest", "endTest");

        checkSampleSize();  // Check we received sufficient samples

        SessionSamples tempSamples = new SessionSamples(this.sensorsTestHelper.INITIAL_FREEMODE_SAMPLE_CAPACITY);
        int numberOfSamplesTocopy = this.sensorsTestHelper.samplesref.mSamples.size() < this.sensorsTestHelper.INITIAL_FREEMODE_SAMPLE_CAPACITY
                ? this.sensorsTestHelper.samplesref.mSamples.size() : this.sensorsTestHelper.INITIAL_FREEMODE_SAMPLE_CAPACITY;
        for (int i = 0; i < numberOfSamplesTocopy; i++) {
            tempSamples.add(this.sensorsTestHelper.samplesref.mSamples.get(i).mTimeOffsetMS, this.sensorsTestHelper.samplesref.mSamples.get(i).mForce);
            int offset = this.sensorsTestHelper.samplesref.mSamples.get(i).mTimeOffsetMS;
            Force force = this.sensorsTestHelper.samplesref.mSamples.get(i).mForce;
            Log.d("DATA", "\t" + offset + "\t" + force.mSensor0 + "\t" + force.mSensor1 + "\t" + force.mSensor2);
        }
        float sensor0 = 0.0f;
        float sensor1 = 0.0f;
        float sensor2 = 0.0f;
        int numSamples = tempSamples.mSamples.size();
        for (int i = 0; i < numSamples; i++) {
            Force force = tempSamples.mSamples.get(i).mForce;
            sensor0 += force.mSensor0;
            sensor1 += force.mSensor1;
            sensor2 += force.mSensor2;
        }

        //
        Force mUserMaxForce = tempSamples.getMaxSampleSeen();
        Force mUserMinForce = tempSamples.getMinSampleSeen();
        Force mUserBaseline = new Force((short) (sensor0 / numSamples), (short) (sensor1 / numSamples),
                (short) (sensor2 / numSamples));
        mSensorResult.setTestsuccessful(true);
        // Parse Sensor 0
        mSensorResult.setSensor(SENSOR0, mUserMinForce.getLiteralSensor(SENSOR0), mUserMaxForce.getLiteralSensor(SENSOR0),
                mUserBaseline.getLiteralSensor(SENSOR0));

        boolean[] result = checkResult(mSensorResult.getSensor(SENSOR0));
        mSensorResult.setSensor0AvgPass(result[AVG_TEST]);
        mSensorResult.setSensor0stabilitypass(result[VAR_TEST]);
        if (!result[AVG_TEST] || !result[VAR_TEST]) mSensorResult.setTestsuccessful(false);
        // Parse Sensor 1
        mSensorResult.setSensor(SENSOR1, mUserMinForce.getLiteralSensor(SENSOR1), mUserMaxForce.getLiteralSensor(SENSOR1),
                mUserBaseline.getLiteralSensor(SENSOR1));

        result = checkResult(mSensorResult.getSensor(SENSOR1));
        mSensorResult.setSensor1AvgPass(result[AVG_TEST]);
        mSensorResult.setSensor1stabilitypass(result[VAR_TEST]);
        if (!result[AVG_TEST] || !result[VAR_TEST]) mSensorResult.setTestsuccessful(false);
        // Parse Sensor 2
        mSensorResult.setSensor(SENSOR2, mUserMinForce.getLiteralSensor(SENSOR2), mUserMaxForce.getLiteralSensor(SENSOR2),
                mUserBaseline.getLiteralSensor(SENSOR2));

        result = checkResult(mSensorResult.getSensor(SENSOR2));
        mSensorResult.setSensor2AvgPass(result[AVG_TEST]);
        mSensorResult.setSensor2stabilitypass(result[VAR_TEST]);
        if (!result[AVG_TEST] || !result[VAR_TEST]) mSensorResult.setTestsuccessful(false);
        if (activity != null && activity != null) {
            activity.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((SensorTestCallback) (activity.get())).onSensorTestCompleted(mSensorResult, testToBeParsed);
                }
            });
        }

        if (!isTest) stop();

        return mSensorResult;
    }

    public void setSensorCardViewProperties(int sensor) {
        items = new int[]{View.INVISIBLE, View.INVISIBLE, View.INVISIBLE};
        if (sensor > items.length) return;
        if (sensor != NO_SENSORS) items[sensor] = View.VISIBLE;
        final CardView layout = (CardView) this.sensorsTestHelper.activityref.get()
                .findViewById(R.id.sensors);
        this.sensorsTestHelper.activityref.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.setVisibility(View.VISIBLE);
                sensorsTestHelper.sensor0ref.setVisibility(items[SENSOR0]);
                sensorsTestHelper.sensor1ref.setVisibility(items[SENSOR1]);
                sensorsTestHelper.sensor2ref.setVisibility(items[SENSOR2]);
            }
        });
    }

    public boolean getOverallResult() {
        return mSensorResult.isTestsuccessful();
    }

    public NewMSensorResult getSensorResult() {
        return mSensorResult;
    }

    @SuppressWarnings("unused")
    public void setmSensorResult(NewMSensorResult mSensorResult) {
        this.mSensorResult = mSensorResult;
    }

    @SuppressWarnings("unused")
    public void setTest(boolean isTest) {
        this.isTest = isTest;
    }

    @SuppressWarnings("unused")
    public void setActivity(Activity activity) {
        this.activity = new WeakReference<Activity>(activity);
    }

    public void setTestToBeParsed(Test testToBeParsed) {
        this.testToBeParsed = testToBeParsed;
    }

    public void interrupt() {
        interrupted = true;
        stop();
    }
}