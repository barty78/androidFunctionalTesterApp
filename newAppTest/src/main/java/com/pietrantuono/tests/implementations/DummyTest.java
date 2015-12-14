package com.pietrantuono.tests.implementations;

import android.app.Activity;

import com.pietrantuono.tests.superclass.Test;

public class DummyTest extends Test {

	public DummyTest(Activity activity, String description, Boolean isBlockingTest, Boolean issuccess) {
		super(activity, null, description, false, isBlockingTest, 0, 0, 0);
		this.success = issuccess;
	}
	
	
	
	@Override
	public void execute() {
		try {
			Thread.sleep(1*1000);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		setSuccess(success);
		activityListener.addFailOrPass(true, success, "");
	}

}
