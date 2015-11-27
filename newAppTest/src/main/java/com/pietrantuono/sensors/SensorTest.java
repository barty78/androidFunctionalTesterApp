package com.pietrantuono.sensors;

import java.lang.ref.WeakReference;

import com.pietrantuono.activities.MainActivity;
import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.tests.implementations.SensorTestWrapper;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import hydrix.pfmat.generic.Force;
import hydrix.pfmat.generic.SessionSamples;
import hydrix.pfmat.generic.TestLimits;

public class SensorTest {
	
	protected SensorsTestHelper sensorsTestHelper;
	protected TestLimits testLimits = null;
	protected WeakReference<Activity> activity = null;
	protected short voltage = -1;
	protected Boolean load = null;
	protected NewMSensorResult mSensorResult = null;
	protected Boolean stopped = false;
	public boolean isTest=false;

	public void setSensorsTestHelper(SensorsTestHelper sensorsTestHelper) {
		this.sensorsTestHelper = sensorsTestHelper;
	}

	

	public SensorTest(Activity activity, SensorTestWrapper wrapper) {
		Log.d("SensorTest", "constucor");
		this.activity = new WeakReference<Activity>(activity);
		this.mSensorResult=new NewMSensorResult(wrapper);
	
	}

	
	
	
	public void stop() {
		stopped = true;
		try {
			final LinearLayout layout = (LinearLayout) this.sensorsTestHelper.activityref.get().findViewById(R.id.sensorsreading);
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

	public SensorTest setVoltage(short voltage) {
		this.voltage = voltage;
		return this;
	}

	public SensorTest setLoad(Boolean load) {
		this.load = load;
		return this;
	}

	public SensorTest setTestLimits(TestLimits testLimits) {
		this.testLimits = testLimits;
		return this;
	}

	public void execute() {
		if (stopped)return;
		sensorsTestHelper.samplesref.clear();
		sensorsTestHelper.samplingSensor0=true;
		sensorsTestHelper.samplingSensor1=true;
		sensorsTestHelper.samplingSensor2=true;
		final LinearLayout layout = (LinearLayout) this.sensorsTestHelper.activityref.get().findViewById(R.id.sensorsreading);
		this.sensorsTestHelper.activityref.get().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				layout.setVisibility(View.VISIBLE);
				SensorTest.this.sensorsTestHelper.sensor1ref.setVisibility(View.VISIBLE);
				SensorTest.this.sensorsTestHelper.sensor2ref.setVisibility(View.VISIBLE);
				SensorTest.this.sensorsTestHelper.sensor0ref.setVisibility(View.VISIBLE);
			}
		});
		Log.d("SensorTest", "execute");
		if (this.testLimits == null) {
			Log.e(SensorsTestHelper.TAG, "You must set the test limits");
			((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test");
			return;
		}
		if (this.activity == null || activity == null) {
			Log.e(SensorsTestHelper.TAG, "You must set the activity");
			((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test");
			return;
		}
		if (this.voltage == -1) {
			Log.e(SensorsTestHelper.TAG, "You must set the voltage");
			((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test");
			return;
		}
		if (this.sensorsTestHelper.samplesref == null) {
			Log.e(SensorsTestHelper.TAG, "samplesref null?!");
			((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test");
			return;
		}
		if (this.sensorsTestHelper.samplesref == null) {
			Log.e(SensorsTestHelper.TAG, "samplesref null?!");
			((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test");
			return;
		}
		if (load == null) {
			Log.e(SensorsTestHelper.TAG, "load null?!");
			((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test");
			return;
		}
		if (mSensorResult == null) {
			Log.e(SensorsTestHelper.TAG, "mSensorResult null?!");
			((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test");
			return;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
		this.sensorsTestHelper.sendVoltage(voltage);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.sensorsTestHelper.samplesref.clear();
		Handler h = new Handler(Looper.getMainLooper());
		h.postDelayed(new Runnable() {
			@Override
			public void run() {
				endTest();
			}
		}, SensorsTestHelper.CALIBRATION_TIME_MS * 1);
	}

	public NewMSensorResult endTest() {
		if (stopped)return mSensorResult;
		final LinearLayout layout = (LinearLayout) this.sensorsTestHelper.activityref.get().findViewById(R.id.sensorsreading);
		this.sensorsTestHelper.activityref.get().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				layout.setVisibility(View.VISIBLE);
			}
		});
		if (stopped)
			return mSensorResult;
		Log.d("SensorTest", "endTest");
		if (this.sensorsTestHelper.samplesref == null) {
			Log.d(SensorsTestHelper.TAG, "samplesref == null " + (this.sensorsTestHelper.samplesref == null));
			((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test");
			return mSensorResult;
		}
		if (this.sensorsTestHelper.samplesref.mSamples == null) {
			Log.d(SensorsTestHelper.TAG, "samplesref.mSamples == null " + (this.sensorsTestHelper.samplesref.mSamples == null));
			((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test");
			return mSensorResult;
		}
		if (this.sensorsTestHelper.samplesref.mSamples == null) {
			Log.d(SensorsTestHelper.TAG, "Samples size = " + this.sensorsTestHelper.samplesref.mSamples.size());
			((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test");
			return mSensorResult;
		}
		if (this.sensorsTestHelper.samplesref == null || this.sensorsTestHelper.samplesref.mSamples == null
				|| this.sensorsTestHelper.samplesref.mSamples.size() < this.sensorsTestHelper.CALIBRATION_MIN_SAMPLES) {
			if (activity.get() != null && !activity.get().isFinishing()
					&& !((MainActivity) (activity.get())).isMainActivityBeingDestroyed())
				return mSensorResult;
			Toast.makeText(activity.get(),
					"Error taking measure, please check Bluetooth and PeriCoach device and restart test",
					Toast.LENGTH_LONG).show();
			((SensorTestCallback) (activity.get())).addFailOrPass(true, false, "", "Sensor test");
			return mSensorResult;
		}
		SessionSamples tempSamples = new SessionSamples(this.sensorsTestHelper.INITIAL_FREEMODE_SAMPLE_CAPACITY);
		int numberOfSamplesTocopy = this.sensorsTestHelper.samplesref.mSamples.size() < this.sensorsTestHelper.INITIAL_FREEMODE_SAMPLE_CAPACITY
				? this.sensorsTestHelper.samplesref.mSamples.size() : this.sensorsTestHelper.INITIAL_FREEMODE_SAMPLE_CAPACITY;
		for (int i = 0; i < numberOfSamplesTocopy; i++) {
			tempSamples.add(this.sensorsTestHelper.samplesref.mSamples.get(i).mTimeOffsetMS, this.sensorsTestHelper.samplesref.mSamples.get(i).mForce);
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
		Short max = mUserMaxForce.getLiteralSensor(0);
		Short min = mUserMinForce.getLiteralSensor(0);
		Short avrg = mUserBaseline.getLiteralSensor(0);
		mSensorResult.setSensor0avg(avrg);
		mSensorResult.setSensor0max(max);
		mSensorResult.setSensor0min(min);
		if (avrg <= testLimits.getUpperLimits().getLiteralSensor(0)
				&& avrg >= testLimits.getLowerLimits().getLiteralSensor(0)) {
			mSensorResult.setSensor0AvgPass(true);
		} else {
			mSensorResult.setSensor0AvgPass(false);
			mSensorResult.setTestsuccessful(false);
		}
		if (Math.abs(max - min) < testLimits.getStability()) {
			mSensorResult.setSensor0stabilitypass(true);
		} else {
			mSensorResult.setSensor0stabilitypass(false);
			mSensorResult.setTestsuccessful(false);
		}
		max = mUserMaxForce.getLiteralSensor(1);
		min = mUserMinForce.getLiteralSensor(1);
		avrg = mUserBaseline.getLiteralSensor(1);
		mSensorResult.setSensor1avg(avrg);
		mSensorResult.setSensor1max(max);
		mSensorResult.setSensor1min(min);
		if (avrg <= testLimits.getUpperLimits().getLiteralSensor(1)
				&& avrg >= testLimits.getLowerLimits().getLiteralSensor(1)) {
			mSensorResult.setSensor1AvgPass(true);
		} else {
			mSensorResult.setSensor1AvgPass(false);
			mSensorResult.setTestsuccessful(false);
		}
		if (Math.abs(max - min) < testLimits.getStability()) {
			mSensorResult.setSensor1stabilitypass(true);
		} else {
			mSensorResult.setSensor1stabilitypass(false);
			mSensorResult.setTestsuccessful(false);
		}
		max = mUserMaxForce.getLiteralSensor(2);
		min = mUserMinForce.getLiteralSensor(2);
		avrg = mUserBaseline.getLiteralSensor(2);
		mSensorResult.setSensor2avg(avrg);
		mSensorResult.setSensor2max(max);
		mSensorResult.setSensor2min(min);
		if (avrg <= testLimits.getUpperLimits().getLiteralSensor(2)
				&& avrg >= testLimits.getLowerLimits().getLiteralSensor(2)) {
			mSensorResult.setSensor2AvgPass(true);
		} else {
			mSensorResult.setSensor2AvgPass(false);
			mSensorResult.setTestsuccessful(false);
		}
		if (Math.abs(max - min) < testLimits.getStability()) {
			mSensorResult.setSensor2stabilitypass(true);
		} else {
			mSensorResult.setSensor2stabilitypass(false);
			mSensorResult.setTestsuccessful(false);
		}
		if (activity != null && activity != null)
			((SensorTestCallback) (activity.get())).onSensorTestCompleted(mSensorResult);
		if(!isTest)this.sensorsTestHelper.sendVoltage(this.sensorsTestHelper.NORMAL_VOLTAGE);
		if(!isTest)stop();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return mSensorResult;
	}

	public boolean getOverallResult() {
		return mSensorResult.isTestsuccessful();
	}

	public NewMSensorResult getSensorResult() {
		return mSensorResult;
	}

	public void setmSensorResult(NewMSensorResult mSensorResult) {
		this.mSensorResult = mSensorResult;
	}

	public void setTest(boolean isTest) {
		this.isTest = isTest;
	}
	
	public void setActivity(Activity activity) {
		this.activity = new WeakReference<Activity>(activity);
	}

}