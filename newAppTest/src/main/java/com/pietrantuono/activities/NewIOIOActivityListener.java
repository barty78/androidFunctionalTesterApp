package com.pietrantuono.activities;

import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.tests.superclass.Test;
import com.pietrantuono.uploadfirmware.ProgressAndTextView;

public interface NewIOIOActivityListener {


    void goAndExecuteNextTest();

    void addView(String label, String text, boolean goAndExecuteNextTest);

    void addView(String label, String text, int color, boolean goAndExecuteNextTest);

    void setStatusMSG(String serial, Boolean success);

    public ProgressAndTextView addFailOrPass(Boolean istest, Boolean success, String reading, String otherreading, String description, server.pojos.Test testToBeParsed);

    public ProgressAndTextView addFailOrPass(final Boolean istest, final Boolean success, String reading, String description, server.pojos.Test testToBeParsed);

    public ProgressAndTextView addFailOrPass(final Boolean istest, final Boolean success, String description, server.pojos.Test testToBeParsed);

    public ProgressAndTextView addFailOrPass(String otherreadig, final Boolean istest, final Boolean success, String description);

    public ProgressAndTextView addFailOrPass(final Boolean istest, final Boolean success, String reading, String description);

    public ProgressAndTextView addFailOrPass(final Boolean istest, final Boolean success, String reading, String description, boolean isSensorTest,server.pojos.Test testToBeParsed);

    public ProgressAndTextView addFailOrPass(final Boolean istest, final Boolean success, String reading);

    public void setSerial(String serial);

    public void setMacAddress(String address);

    public String getSerial();

    public BTUtility getBtutility();

    public void setBtutility(BTUtility btutility);

    public void setSerialBT(String serial, Boolean success);

    ProgressAndTextView createUploadProgress(boolean b, boolean c, String description);

    public void onCurrentSequenceEnd();

    void setResult(boolean success);

    String getMac();

    void setBarcode(String barcode);

    String getBarcode();


}