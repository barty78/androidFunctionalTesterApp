package com.pietrantuono.tests.superclass;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.pietrantuono.activities.NewIOIOActivityListener;

import java.util.ArrayList;

import customclasses.DebugHelper;
import ioio.lib.api.IOIO;

public abstract class Test {
	public String description;
	public boolean isSensorTest=false;
	public boolean istest=true;
	private boolean isBlockingTest=false;
	public boolean isinterrupted=false;
	public NewIOIOActivityListener activityListener;
	public IOIO ioio;
	public String TAG=getClass().getSimpleName();
	public boolean active=true;
	public boolean success=false;
	private long IdTest=0;
	private double value;
	private Long errorcode=0l;//Defalt value

	//Limits variables
	public boolean isNominal;
	@SuppressWarnings("unused")
	public float limitParam1, limitParam2,limitParam3;
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
	public Test(Activity activity, IOIO ioio, String description, Boolean isSensorTest,
				Boolean isBlockingTest, float limitParam1, float limitParam2, float limitParam3) {
		this.description = description;
		this.isSensorTest = isSensorTest;
		this.isBlockingTest = isBlockingTest;
		this.activityListener=(NewIOIOActivityListener)activity;
		this.ioio=ioio;
		this.limitParam1=limitParam1;
		this.limitParam2=limitParam2;
		this.limitParam3=limitParam3;
	}

	
	public void interrupt(){
		isinterrupted=true;
	}
		
	public Boolean isInterupted(){return isinterrupted;}
	
	public void report (final Exception e){
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
	public void report (String msg){
		Crashlytics.log(msg);
		Log.d(TAG,msg);
		if (activityListener==null) return;
		Activity activity=(Activity)activityListener;
		if(activity==null || activity.isFinishing())return;
		Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
		
	}
	public Activity getActivity(){return ((Activity)this.activityListener);}
	
	public NewIOIOActivityListener getListener(){return this.activityListener;}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public void Success() {
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
