package com.pietrantuono.sensors;

import hydrix.pfmat.generic.Force;
import hydrix.pfmat.generic.SessionSamples;
import ioio.lib.api.IOIO;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pietrantuono.activities.NewIOIOActivityListener;
import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.pericoach.newtestapp.R;

public class SensorsTestHelper implements OnSampleCallback {
	WeakReference<Activity> activityref = null;
	TextView sensor0ref = null;
	TextView sensor1ref = null;
	TextView sensor2ref = null;
	private NewSessionPollingThread newSessionPollingThreadref = null;
	SessionSamples samplesref = null;
	SessionSamples closedtestsamplesrefsensor0 = null;
	SessionSamples closedtestsamplesrefsensor1 = null;
	SessionSamples closedtestsamplesrefsensor2 = null;
	private final int SAMPLING_HZ = 10;
	static final String TAG = "SensorsTestHelper";
	final static int INITIAL_FREEMODE_SAMPLE_CAPACITY = 512;
	@SuppressWarnings("ucd")
	static final int CALIBRATION_TIME_MS = 1500; // 1.5 seconds
	@SuppressWarnings("ucd")
	final short NORMAL_VOLTAGE = 127;
	@SuppressWarnings("ucd")
	final int CALIBRATION_MIN_SAMPLES = 10;
	boolean samplingSensor0=true;
	boolean samplingSensor1=true;
	boolean samplingSensor2=true;
	private boolean acceptdata;
	SimpleDateFormat simpleDateFormat= new SimpleDateFormat(" HH:mm:ss.SSS");

	public SensorsTestHelper(Activity activity, BTUtility btUtil, IOIO ioio) {
		Log.d(TAG, "Constructor");
		final LinearLayout layout = (LinearLayout) activity.findViewById(R.id.sensorsreading);
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
		this.closedtestsamplesrefsensor0= new SessionSamples(INITIAL_FREEMODE_SAMPLE_CAPACITY);
		this.closedtestsamplesrefsensor1= new SessionSamples(INITIAL_FREEMODE_SAMPLE_CAPACITY);
		this.closedtestsamplesrefsensor2= new SessionSamples(INITIAL_FREEMODE_SAMPLE_CAPACITY);
		NewPFMATDevice.getDevice().setCallback(this);
		this.newSessionPollingThreadref = new NewSessionPollingThread(NewPFMATDevice.getDevice(),
				System.currentTimeMillis(), 1000 / SAMPLING_HZ);
		this.newSessionPollingThreadref.start();
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
		if (closedtestsamplesrefsensor0 != null &&samplingSensor0)
			closedtestsamplesrefsensor0.add(requestTimestampMS, new Force(sensor0, sensor1, sensor2));
		if (closedtestsamplesrefsensor1 != null && samplingSensor1)
			closedtestsamplesrefsensor1.add(requestTimestampMS, new Force(sensor0, sensor1, sensor2));
		if (closedtestsamplesrefsensor2 != null && samplingSensor2)
			closedtestsamplesrefsensor2.add(requestTimestampMS, new Force(sensor0, sensor1, sensor2));

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (samplingSensor0) sensor0tv.setText(Short.toString(sensor0));
				if (samplingSensor1) sensor1tv.setText(Short.toString(sensor1));
				if (samplingSensor2) sensor2tv.setText(Short.toString(sensor2));
			}
		});
	}

	public void stop() {
		Log.d(TAG, "stop");
		try {
			final LinearLayout layout = (LinearLayout) activityref.get().findViewById(R.id.sensorsreading);
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

	void sendVoltages(final Short voltage, final Short zerovoltage) throws Exception{
//		Handler handler = new Handler();
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

//		Byte sensor = (byte) (0 & 0xFF);
//		NewPFMATDevice.getDevice().sendRefVoltage(sensor, voltage);
//		handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				Byte sensor = (byte) (1 & 0xFF);
//				NewPFMATDevice.getDevice().sendRefVoltage(sensor, voltage);
//			}
//		}, 50);
//		handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				Byte sensor = (byte) (2 & 0xFF);
//				NewPFMATDevice.getDevice().sendRefVoltage(sensor, voltage);
//			}
//		}, 100);
//
//		handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				Byte sensor = (byte) (0 & 0xFF);
//				NewPFMATDevice.getDevice().sendZeroVoltage(sensor, zerovoltage);
//			}
//		}, 150);
//
//		handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				Byte sensor = (byte) (1 & 0xFF);
//				NewPFMATDevice.getDevice().sendZeroVoltage(sensor, zerovoltage);
//			}
//		}, 200);
//		handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				Byte sensor = (byte) (2 & 0xFF);
//				NewPFMATDevice.getDevice().sendZeroVoltage(sensor, zerovoltage);
//			}
//		}, 250);
	}

	public void accetpData(boolean accept){
		this.acceptdata=accept;
	}

	/**
	 * Should work, tested in another context, will not work if both sendAllVoltages and callback are executed on the same thread
	 * but that should not be the case
	 */
	public void sendAllVoltages(final short[] refVoltages, final short[] zeroVoltages, int timeOutInMills) {
		((NewIOIOActivityListener) (activityref.get())).getBtutility().sendAllVoltages(refVoltages,zeroVoltages,timeOutInMills);
	}


}
