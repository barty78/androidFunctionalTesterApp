package com.pietrantuono.sensors;

import hydrix.pfmat.generic.Force;
import hydrix.pfmat.generic.Motion;
import hydrix.pfmat.generic.Quaternion;
import hydrix.pfmat.generic.SessionSamples;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pietrantuono.activities.NewIOIOActivityListener;
import com.pietrantuono.pericoach.newtestapp.R;

public class SensorsTestHelper implements OnSampleCallback {

	WeakReference<Activity> activityref = null;
	TextView sensor0ref = null;
	TextView sensor1ref = null;
	TextView sensor2ref = null;
	private NewSessionPollingThread newSessionPollingThreadref = null;
	SessionSamples samplesref = null;
	private SessionSamples samples = null;

	SessionSamples[] closedtestsamples = {null,null,null};

	private final int SAMPLING_HZ = 10;
	static final String TAG = "SensorsTestHelper";
	final static int INITIAL_FREEMODE_SAMPLE_CAPACITY = 512;
	@SuppressWarnings("ucd")
	static final int CALIBRATION_TIME_MS = 1500; // 1.5 seconds
	@SuppressWarnings("ucd")
	final short NORMAL_VOLTAGE = 127;
	@SuppressWarnings("ucd")
	final int CALIBRATION_MIN_SAMPLES = 10;
	final static int SENSOR0 = 0;
	final static int SENSOR1 = 1;
	final static int SENSOR2 = 2;
	boolean samplingSensor0=true;
	boolean samplingSensor1=true;
	boolean samplingSensor2=true;
	private boolean acceptdata;
	private final SimpleDateFormat simpleDateFormat= new SimpleDateFormat(" HH:mm:ss.SSS");

	public SensorsTestHelper(Activity activity) {
		Log.d(TAG, "Constructor");
		final CardView layout = (CardView) activity.findViewById(R.id.sensors);
//		final LinearLayout layout = (LinearLayout) activity.findViewById(R.id.sensors);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				layout.setVisibility(View.VISIBLE);
			}
		});
		this.activityref = new WeakReference<Activity>(activity);
		this.sensor0ref = (TextView) activity.findViewById(R.id.sensor0);
		this.sensor1ref = (TextView) activity.findViewById(R.id.sensor1);
		this.sensor2ref = (TextView) activity.findViewById(R.id.sensor2);
		this.samplesref = new SessionSamples(INITIAL_FREEMODE_SAMPLE_CAPACITY);
		this.closedtestsamples = new SessionSamples[]{this.samplesref, this.samplesref, this.samplesref};

		NewPFMATDevice.getDevice().setCallback(this);
		this.newSessionPollingThreadref = new NewSessionPollingThread(NewPFMATDevice.getDevice(),
				System.currentTimeMillis(), 1000 / SAMPLING_HZ);
	}

	public NewSessionPollingThread getNewSessionPollingThreadref() {
		return newSessionPollingThreadref;
	}

	@Override
	public synchronized void onDataSample(int sequenceNumber, final short sensor0, final short sensor1, final short sensor2, final int batteryLevel) {

	}

	@Override
	public synchronized void  onSample(int requestTimestampMS, final short sensor0, final short sensor1, final short sensor2) {
		if(!acceptdata) {
			Log.d(TAG,"NOT accetping data offset = "+requestTimestampMS+" time = "+simpleDateFormat.format(System.currentTimeMillis()) );
			return;}
		Log.d(TAG,"YES accepting datta offset = "+requestTimestampMS+" time = "+simpleDateFormat.format(System.currentTimeMillis()));
		Activity activity = activityref.get();
		SessionSamples samples = samplesref;
		final TextView sensor0tv = sensor0ref;
		final TextView sensor1tv = sensor1ref;
		final TextView sensor2tv = sensor2ref;
		if (activity == null || sensor0tv == null || sensor1tv == null || sensor2tv == null || samples == null)
			return;
		samples.add(requestTimestampMS, new Force(sensor0, sensor1, sensor2));
		if (closedtestsamples[0] != null &&samplingSensor0)
			closedtestsamples[0].add(requestTimestampMS, new Force(sensor0, sensor1, sensor2));
		if (closedtestsamples[1] != null &&samplingSensor0)
			closedtestsamples[1].add(requestTimestampMS, new Force(sensor0, sensor1, sensor2));
		if (closedtestsamples[2] != null &&samplingSensor0)
			closedtestsamples[2].add(requestTimestampMS, new Force(sensor0, sensor1, sensor2));

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (samplingSensor0) sensor0tv.setText(Short.toString(sensor0));
				if (samplingSensor1) sensor1tv.setText(Short.toString(sensor1));
				if (samplingSensor2) sensor2tv.setText(Short.toString(sensor2));
			}
		});
	}

	@Override
	public void onAccelSample(int requestTimestampMS, Motion.Acceleration accel, Motion.Rotation gyro, Quaternion quat) {
		Activity activity = activityref.get();
		samples = samplesref;

		Motion.VectorFloat v = getGravity(quat);
		final float[] ypr = getYawPitchRoll(quat, v);
		Motion.VectorInt16 linearAccel = getLinearAccel(new Motion.VectorInt16(accel.mAccelX, accel.mAccelY, accel.mAccelZ), v);
		Motion.VectorInt16 worldAccel = getLinearAccelInWorld(linearAccel, quat);
		Log.d("LINEAR ACCEL:", "X:" + linearAccel.mx + " , " + "Y:" + linearAccel.my + " , " + "Z:" + linearAccel.mz + " ");
		Log.d("LINEAR ACCEL WORLD:", "X:" + worldAccel.mx + " , " + "Y:" + worldAccel.my + " , " + "Z:" + worldAccel.mz + " ");
//		samples.add(requestTimestampMS, new Motion(accel, gyro, quat, ypr));

		//mPollingThread.ack();
//		activity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				DecimalFormat df = new DecimalFormat("#.#");
//				yawText.setText(String.format("%.1f", ypr[0]));
//				pitchText.setText(String.format("%.1f", ypr[1]));
//				rollText.setText(String.format("%.1f", ypr[2]));
//			}
//		});
//		Log.d(TAG, "SAMPLES SIZE - " + samplesref.getSamples().size());
	}

	private final Motion.VectorFloat getGravity(Quaternion q)
	{
		Motion.VectorFloat v = new Motion.VectorFloat();
		v.mx = 2 * (q.x * q.z - q.w * q.y);
		v.my = 2 * (q.w * q.x - q.y * q.z);
		v.mz = q.w * q.w - q.x * q.x - q.y * q.y + q.z * q.z;
		return v;
	}

	private final float[] getYawPitchRoll(Quaternion q, Motion.VectorFloat grav)
	{
		float[] ypr = new float[]{(float)0, (float)0, (float)0};

		ypr[0] = (float) (Math.atan2(2 * q.x * q.y - 2 * q.w * q.z, 2 * q.w * q.w + 2 * q.x * q.x - 1) * 180 / Math.PI);
		ypr[1] = (float) (Math.atan(grav.mx / Math.sqrt(grav.my * grav.my + grav.mz * grav.mz)) * 180 / Math.PI);
		// roll: (tilt left/right, about X axis)
		ypr[2] = (float) (Math.atan(grav.my / Math.sqrt(grav.mx * grav.mx + grav.mz * grav.mz)) * 180 / Math.PI);
		return ypr;
	}

	private final Motion.VectorInt16 getLinearAccel(Motion.VectorInt16 vraw, Motion.VectorFloat grav)
	{
		Motion.VectorInt16 v = new Motion.VectorInt16();
		v.mx = (int) (vraw.mx - grav.mx * 8192);
		v.my = (int) (vraw.my - grav.my * 8192);
		v.mz = (int) (vraw.mz - grav.mz * 8192);
		return v;
	}

	private final Motion.VectorInt16 getLinearAccelInWorld(Motion.VectorInt16 vreal, Quaternion q)
	{
		Motion.VectorInt16 v = vreal;
		v.rotate(q);
		return v;
	}

	public void stop() {
		Log.d(TAG, "stop");
		try {
//			final LinearLayout layout = (LinearLayout) activityref.get().findViewById(R.id.sensors);
			final CardView layout = (CardView) activityref.get().findViewById(R.id.sensors);
			activityref.get().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					layout.setVisibility(View.INVISIBLE);
					sensor0ref.setText("0");
					sensor0ref.setText("0");
					sensor0ref.setText("0");
				}
			});
		} catch (Exception e) {
		}
		try {
			newSessionPollingThreadref.cancel();
		} catch (Exception e) {
		}
	}

    void sendAllVoltages(final Short voltage, final Short zerovoltage) throws TimeoutException, NewDevice.InvalidVoltageException {
        short[] ref = new short[]{voltage, voltage, voltage};
        short[] zero = new short[]{zerovoltage, zerovoltage, zerovoltage};
        ((NewIOIOActivityListener) (activityref.get())).getBtutility().sendAllVoltages(ref, zero, 500);

    }

    void sendVoltages(final Short voltage, final Short zerovoltage) throws Exception {
        try {
            ((NewIOIOActivityListener) (activityref.get())).getBtutility().setVoltage(voltage);
        } catch (Exception e) {
            throw new Exception("Setting Failed.");
        }
        try {
            ((NewIOIOActivityListener) (activityref.get())).getBtutility().setZeroVoltage(zerovoltage);
        } catch (Exception e) {
            throw new Exception("Setting Failed.");
        }
    }

    public void acceptData(boolean accept) {
        this.acceptdata = accept;
    }

    /**
     * Should work, tested in another context, will not work if both sendAllVoltages and callback are executed on the same thread
     * but that should not be the case
     */
    public void sendAllVoltages(final short[] refVoltages, final short[] zeroVoltages, int timeOutInMills) throws Exception {
        ((NewIOIOActivityListener) (activityref.get())).getBtutility().sendAllVoltages(refVoltages, zeroVoltages, timeOutInMills);
    }


}
