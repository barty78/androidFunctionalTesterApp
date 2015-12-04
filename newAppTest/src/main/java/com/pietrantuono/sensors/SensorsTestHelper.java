package com.pietrantuono.sensors;

import hydrix.pfmat.generic.Force;
import hydrix.pfmat.generic.SessionSamples;
import ioio.lib.api.IOIO;
import java.lang.ref.WeakReference;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
	static final int CALIBRATION_TIME_MS = 2500; // 2 seconds
	@SuppressWarnings("ucd")
	final short NORMAL_VOLTAGE = 127;
	@SuppressWarnings("ucd")
	final int CALIBRATION_MIN_SAMPLES = 20;
	boolean samplingSensor0=true;
	boolean samplingSensor1=true;
	boolean samplingSensor2=true;
	private boolean acceptdata;

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

	public SensorsTestHelper() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public synchronized void  onSample(int requestTimestampMS, final short sensor0, final short sensor1, final short sensor2) {
		if(!acceptdata) {
			Log.d(TAG,"NOT accetping data offset = "+requestTimestampMS+" "+sensor0+" "+sensor1+" "+ " "+sensor2);
			return;}
		Log.d(TAG,"YES accepting datta offset = "+requestTimestampMS+" "+sensor0+" "+sensor1+" "+ " "+sensor2);
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
				if(samplingSensor0)sensor0tv.setText(Short.toString(sensor0));
				if(samplingSensor1)sensor1tv.setText(Short.toString(sensor1));
				if(samplingSensor2)sensor2tv.setText(Short.toString(sensor2));
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

	

	@SuppressWarnings("ucd")
	void sendVoltage(final Short voltage) {
		Handler handler = new Handler();
		Byte sensor = (byte) (0 & 0xFF);
		NewPFMATDevice.getDevice().sendRefVoltage(sensor, voltage);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Byte sensor = (byte) (1 & 0xFF);
				NewPFMATDevice.getDevice().sendRefVoltage(sensor, voltage);
			}
		}, 20);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Byte sensor = (byte) (2 & 0xFF);
				NewPFMATDevice.getDevice().sendRefVoltage(sensor, voltage);
			}
		}, 40);
	}

	

	public void setSamplesref(SessionSamples samplesref) {
		this.samplesref = samplesref;
	}

	public void setClosedtestsamplesrefsensor0(SessionSamples closedtestsamplesrefsensor0) {
		this.closedtestsamplesrefsensor0 = closedtestsamplesrefsensor0;
	}

	public void setClosedtestsamplesrefsensor1(SessionSamples closedtestsamplesrefsensor1) {
		this.closedtestsamplesrefsensor1 = closedtestsamplesrefsensor1;
	}

	public void setClosedtestsamplesrefsensor2(SessionSamples closedtestsamplesrefsensor2) {
		this.closedtestsamplesrefsensor2 = closedtestsamplesrefsensor2;
	}
	public void setActivityref(Activity activity) {
		this.activityref = new WeakReference<Activity>(activity);
	}

	public void accetpData(boolean accept){
		this.acceptdata=accept;
	}

}
