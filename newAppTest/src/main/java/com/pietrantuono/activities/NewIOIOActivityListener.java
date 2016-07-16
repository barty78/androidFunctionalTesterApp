package com.pietrantuono.activities;

import com.pietrantuono.btutility.BTUtility;

import server.pojos.Device;

public interface NewIOIOActivityListener {


    void goAndExecuteNextTest();

    void addView(String label, String text, int color, boolean goAndExecuteNextTest);

    void setStatusMSG(String serial, Boolean success);

    void addFailOrPass(final Boolean istest, final Boolean success, String reading, String description, server.pojos.Test testToBeParsed);

    void addFailOrPass(final Boolean istest, final Boolean success, String description, server.pojos.Test testToBeParsed);

    void addFailOrPass(String otherreadig, final Boolean istest, final Boolean success, String description);

    void addFailOrPass(final Boolean istest, final Boolean success, String reading, String description);

    void addFailOrPass(final Boolean istest, final Boolean success, String reading);

    void setSerial(String serial);

    void setMacAddress(String address);

    String getSerial();

    BTUtility getBtutility();

    void setBtutility(BTUtility btutility);

    void onUploadTestFinished(boolean istest, boolean success, String description, String failReason);

    void onCurrentSequenceEnd();

    void startPCBSleepMonitor();

    String getMac();

    void setBarcode(String barcode);

    String getBarcode();

    void setSequenceDevice(Device device);

    Device getSequenceDevice();
}