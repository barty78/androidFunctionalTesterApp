package com.pietrantuono.activities;

import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.tests.implementations.upload.UploadTestCallback;

public interface NewIOIOActivityListener {


    void goAndExecuteNextTest();

    void addView(String label, String text, boolean goAndExecuteNextTest);

    void addView(String label, String text, int color, boolean goAndExecuteNextTest);

    void setStatusMSG(String serial, Boolean success);

    public void addFailOrPass(final Boolean istest, final Boolean success, String reading, String description, server.pojos.Test testToBeParsed);

    public void addFailOrPass(final Boolean istest, final Boolean success, String description, server.pojos.Test testToBeParsed);

    public void addFailOrPass(String otherreadig, final Boolean istest, final Boolean success, String description);

    public void addFailOrPass(final Boolean istest, final Boolean success, String reading, String description);


    public void addFailOrPass(final Boolean istest, final Boolean success, String reading);

    public void setSerial(String serial);

    public void setMacAddress(String address);

    public String getSerial();

    public BTUtility getBtutility();

    public void setBtutility(BTUtility btutility);

    public void setSerialBT(String serial, Boolean success);

    public void onUploadTestFinished(boolean istest, boolean success, String description,String failReason);

    public void onCurrentSequenceEnd();

    public void startPCBSleepMonitor();

    String getMac();

    void setBarcode(String barcode);

    String getBarcode();


}