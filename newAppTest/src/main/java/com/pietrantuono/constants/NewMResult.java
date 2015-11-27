package com.pietrantuono.constants;

import com.pietrantuono.tests.superclass.Test;

public class NewMResult {
	private Test test = null;
	private Boolean issuccesfull=false;
	
	public Test getTestype() {
		return test;
	}

	public NewMResult(Test test)  {
		this.test = test;
	}

	public void setTestype(Test test) {
		this.test = test;
	}

	public String getDescription() {
		return test.getDescription();
	}

	public Boolean isTest() {
		return test.isTest();
	}

	

	public Boolean isTestsuccessful() {
		return issuccesfull;
	}

	public void setTestsuccessful(Boolean istestsuccessful) {
		issuccesfull=istestsuccessful;
	}
	public Boolean isSensorTest(){
		return test.isSensorTest();
	}
}
