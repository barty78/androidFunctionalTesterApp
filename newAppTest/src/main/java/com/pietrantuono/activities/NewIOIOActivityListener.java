package com.pietrantuono.activities;

import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.tests.superclass.Test;
import com.pietrantuono.uploadfirmware.ProgressAndTextView;

public interface NewIOIOActivityListener {
	
	
	void goAndExecuteNextTest();
	void addView(String label,String text, boolean goAndExecuteNextTest);
	void addView(String label,String text, int color, boolean goAndExecuteNextTest);
	void setStatusMSG(String serial, Boolean success);
	public ProgressAndTextView addFailOrPass(final Boolean istest, final Boolean success, String description);
	@SuppressWarnings("ucd")
	public ProgressAndTextView addFailOrPass(String otherreadig,final Boolean istest, final Boolean success, String description); 
	public ProgressAndTextView addFailOrPass(final Boolean istest, final Boolean success,String rading, String description);
	public void setSerial(String serial);
	public void setMacAddress(String address);
	public String getSerial();
	public BTUtility getBtutility();
	public void setBtutility(BTUtility btutility);
	@SuppressWarnings("ucd")
	public void setSerialBT(String serial, Boolean success);
	@SuppressWarnings("ucd")
	ProgressAndTextView createUploadProgress(boolean b, boolean c, String description);
	public void onCurrentSequenceEnd();

	void setResult(boolean success);
}