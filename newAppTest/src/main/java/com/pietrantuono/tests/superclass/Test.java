package com.pietrantuono.tests.superclass;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.pietrantuono.activities.IOIOActivityListener;

import customclasses.DebugHelper;
import ioio.lib.api.IOIO;

public abstract class Test {
	protected String description;
	private boolean isSensorTest=false;
	protected boolean istest=true;
	private boolean isBlockingTest=false;
	protected boolean isinterrupted=false;
	protected final IOIOActivityListener activityListener;
	protected final IOIO ioio;
	protected final String TAG=getClass().getSimpleName();
	private boolean active=true;
	protected boolean success=false;
	private long IdTest=0;
	private double value;
	private Long errorcode=0l;//Defalt value

	//Limits variables
	private boolean isNominal;
	@SuppressWarnings("unused")
	public final float limitParam1;
	@SuppressWarnings("unused")
	protected final float limitParam2;
	@SuppressWarnings("unused")
	private final float limitParam3;
	public server.pojos.Test testToBeParsed;


	public abstract void execute();
	public String getDescription(){return description;}
	public void setDescription(String description){this.description=description;}
	public Boolean isSensorTest(){return isSensorTest;}
	public boolean isTest() {return istest;}
	public void setIsTest(boolean isTest) {this.istest = isTest;}
	public boolean isActive() {	return active;}
	public void setActive(boolean active) {this.active = active;}
	public void setBlockingTest(boolean isBlockingTest) {this.isBlockingTest = isBlockingTest;}
	public boolean isNominal() {return isNominal;}
	public void setNominal(boolean isNominal) {this.isNominal = isNominal;}

	/**
	 * @param description
	 * @param isSensorTest
	 * @param isBlockingTest
	 * @param limitParam1
	 * @param limitParam2
	 * @param limitParam3
	 */
	protected Test(Activity activity, IOIO ioio, String description, Boolean isSensorTest,
				   Boolean isBlockingTest, float limitParam1, float limitParam2, float limitParam3) {
		this.description = description;
		this.isSensorTest = isSensorTest;
		this.isBlockingTest = isBlockingTest;
		this.activityListener=(IOIOActivityListener)activity;
		this.ioio=ioio;
		this.limitParam1=limitParam1;
		this.limitParam2=limitParam2;
		this.limitParam3=limitParam3;
	}

	
	public void interrupt(){
		isinterrupted=true;
	}
		
	protected Boolean isInterupted(){return isinterrupted;}
	
	protected void report(final Exception e){
		final Activity activity=(Activity)activityListener;
		if(activity==null || activity.isFinishing())return;
		((Activity)activityListener).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Crashlytics.logException(e);
				Log.d(TAG, e.toString());
				if (activityListener == null) return;
				Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
			}
		});
		
	}
	protected void report(String msg){
		Crashlytics.log(msg);
		Log.d(TAG,msg);
		if (activityListener==null) return;
		Activity activity=(Activity)activityListener;
		if(activity==null || activity.isFinishing())return;
		Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
		
	}
	protected Activity getActivity(){return ((Activity)this.activityListener);}
	
	protected IOIOActivityListener getListener(){return this.activityListener;}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	protected void Success() {
		this.success = true;
	}
	public long getIdTest() {
		return IdTest;
	}
	public void setIdTest(long idTest) {
		IdTest = idTest;
	}
	
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}

	public Long getErrorCode() { return errorcode;}
	public void setErrorcode(Long errorcode) { this.errorcode = errorcode;}


	public void setTestToBeParsed(server.pojos.Test testToBeParsed) {
		this.testToBeParsed = testToBeParsed;
	}

	public Boolean isBlockingTest() {
		if(DebugHelper.isMaurizioDebug())return false;
		return isBlockingTest;
	}

}
