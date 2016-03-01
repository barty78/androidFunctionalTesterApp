package com.pietrantuono.tests.implementations.upload;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ioio.lib.api.IOIO;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.pietrantuono.fragments.sequence.holders.UploadItemHolder;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.pericoach.newtestapp.BuildConfig;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.tests.ErrorCodes;
import com.pietrantuono.tests.superclass.Test;
import com.pietrantuono.uploadfirmware.FirmWareUploader;
import com.pietrantuono.uploadfirmware.FirmWareUploader.UploaderListener;

@SuppressWarnings("ALL")
public class UploadFirmwareTest extends Test {
    private final AppCompatActivity activity;
    private InputStream RX;
    private OutputStream TX;
    private BufferedInputStream BRX;
    private FirmWareUploader firmWareUploader;
    private Boolean initialised = false;
    private Boolean looping = true;
    private Boolean resetted = true;
    private Boolean known = false;
    private AlertDialog alertDialog;
    boolean fileComparisonPassed = false;
    boolean fileMD5Passed = false;
    private int retries = 0;
    private UploadDialog uploadDialog;

    public UploadFirmwareTest(Activity activity, IOIO ioio) {
        super(activity, ioio, "Upload Firmware", false, true, 0, 0, 0);            // Blocking Test, if fails - STOP
        this.activity = (AppCompatActivity) activity;
    }

    @Override
    public void execute() {
        if (isinterrupted) return;
        String version = PeriCoachTestApplication.getGetFirmware().getVersion();
        uploadDialog = (UploadDialog) activity.getSupportFragmentManager().findFragmentByTag(UploadDialog.TAG);
        if (uploadDialog == null) {
            uploadDialog = new UploadDialog();
        }
        if (!uploadDialog.isAdded()) {
            uploadDialog.show(activity.getSupportFragmentManager(), UploadDialog.TAG);
            activity.getSupportFragmentManager().executePendingTransactions();
        }
        start();
    }

    public void start() {
        ((Activity) activityListener).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (uploadDialog != null) {
                    uploadDialog.setWait();
                }
            }
        });
        if (IOIOUtils.getUtils().getIOIOUart() != null) {
            RX = IOIOUtils.getUtils().getIOIOUart().getInputStream();// Pin 14
            BRX = new BufferedInputStream(RX);
            TX = IOIOUtils.getUtils().getIOIOUart().getOutputStream();// Pin 13
        }
        firmWareUploader = new FirmWareUploader(TX, RX, (Activity) activityListener,
                uploadDialog, activityListener, ioio);

        Log.e(TAG, "Initialization loop");
        Thread t = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                while (!initialised && looping && retries < 3) {
                    if (resetted) {
                        Log.d(TAG, "" + retries);
                        try {
                            TX.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        initialised = firmWareUploader.deviceInit();
                        Log.d(TAG, "Initialised = " + initialised);
                        if (!initialised) {
                            resetted = false;
                            if (retries <= 2) onInitialiseFailed();    //issueAlert();
                        }
                    }
                }
                if (retries >= 3) {
                    onFailure(ErrorCodes.FIRMWAREUPLOAD_INIT_FAILED, "ERROR: Device Init Failed");
                    return;
                }
                retries = 0;
                while (!known && retries < 3) {

                    known = firmWareUploader.getInfo();
                    if (retries <= 2) onGetInfoFailed();
                }

                if (retries >= 3) {
                    onFailure(firmWareUploader.getERRORCODE(), "ERROR: Get Device Info Failed");
                    return;
                }

                if (!firmWareUploader.massErase()) {
                    onFailure(firmWareUploader.getERRORCODE(), "ERROR: Erase Failed");
                    return;
                }

                if (!firmWareUploader.writeOptionBytes()) {
                    onFailure(firmWareUploader.getERRORCODE(), "ERROR: Option Bytes Write Failed");
                    return;
                }


//                    if (!firmWareUploader.deviceInit()) {
//                    onFailure(firmWareUploader.getERRORCODE(), "ERROR: Device Init Failed");
//                    return;
//                }

                firmWareUploader.upload(new UploaderListener() {
                    @Override
                    public void onUploadSuccess() {
                        setSuccess(true);
                        Log.d(TAG, "FW Upload Result is " + success);
                        try {
                            RX.close();
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                        }
                        try {
                            TX.close();
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                        }
                        ((Activity) activityListener).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activityListener.onUploadTestFinished(true, success, description,"");
                                if (uploadDialog != null) {
                                    uploadDialog.setPass();
                                    uploadDialog.dismiss();
                                }
                            }
                        });
                    }

                    @Override
                    public void onUploadFailure(final String error, int errorcode) {
                        setSuccess(false);
                        setErrorcode((long) errorcode);
                        Log.d(TAG, "FW Upload Result is " + success);
                        try {
                            RX.close();
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                        }
                        try {
                            TX.close();
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                        }

                        ((Activity) activityListener).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Resources res = ((Activity) activityListener).getResources();
                                Drawable background = res
                                        .getDrawable(R.drawable.redprogress);
                                if (uploadDialog != null) {
                                    uploadDialog.setFail(description + "\nERROR: " + error);
                                    uploadDialog.dismiss();
                                    activityListener.onUploadTestFinished(true, success, description,error);


                                }
                            }
                        });

                    }
                });
            }
        });
        t.start();
    }

    private void onInitialiseFailed() {
        final Activity activity = (Activity) activityListener;
        if (activity == null || activity.isFinishing()) return;
        IOIOUtils.getUtils().resetDevice((Activity) activityListener);
        if (BuildConfig.DEBUG) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "INIT FAILED, " + String.valueOf(3 - retries) + " Attempts Remaining",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        resetted = true;
        initialised = false;
        retries++;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void onFailure(final long error, final String msg) {
        setSuccess(false);
        ((Activity) activityListener).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Resources res = ((Activity) activityListener).getResources();
                Drawable background = res
                        .getDrawable(R.drawable.redprogress);
                if (uploadDialog != null) {
                    uploadDialog.setFail(description + "\n" + msg);
                    uploadDialog.dismiss();
                    setErrorcode(error);
                }
                String string = "ERROR CODE: " + error + "\n";
                IOIOUtils.getUtils().appendUartLog((Activity) activityListener, string.getBytes(), string.getBytes().length);
                activityListener.onUploadTestFinished(true, success, description,msg);

            }
        });
    }

    private void onGetInfoFailed() {
        final Activity activity = (Activity) activityListener;
        if (activity == null || activity.isFinishing()) return;
        retries++;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            firmWareUploader.stop();
        } catch (Exception e) {
        }
        ;
        try {
            alertDialog.dismiss();
            ;
        } catch (Exception e) {
        }
        ;
    }
}
