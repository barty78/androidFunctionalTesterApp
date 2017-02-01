package com.pietrantuono.constants;

import com.pietrantuono.tests.superclass.Test;

@SuppressWarnings("unused")
public class Result {
	private Test test = null;
	private Boolean issuccesfull=false;
	private String description;

	public Test getTestype() {
		return test;
	}

	public Result(Test test)  {
		this.test = test;
	}

	public void setTestype(Test test) {
		this.test = test;
	}

	public String getDescription() {
		return description;
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

	public void setDescription(String description) {
		this.description = description;
	}
}
