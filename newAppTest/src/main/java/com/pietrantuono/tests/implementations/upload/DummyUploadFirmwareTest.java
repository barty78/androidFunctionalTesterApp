package com.pietrantuono.tests.implementations.upload;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.pietrantuono.fragments.sequence.holders.UploadItemHolder;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;
import com.pietrantuono.uploadfirmware.DummyFirmWareUploader;
import com.pietrantuono.uploadfirmware.DummyFirmWareUploader.UploaderListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ioio.lib.api.IOIO;

public class DummyUploadFirmwareTest extends Test {
    private final AppCompatActivity activity;
    private InputStream RX;
    private OutputStream TX;
    private DummyFirmWareUploader dummyFirmWareUploader;
    private Boolean initialised = true;
    private Boolean looping = true;
    private Boolean resetted = true;
    private int retries;
    private Boolean loopback;
    private UploadDialog uploadDialog;

    public DummyUploadFirmwareTest(AppCompatActivity activity, IOIO ioio, Boolean loopback) {
        super(activity, ioio, "Dummy Upload Firmware", false, true, 0, 0, 0);            // Blocking TEST, if fails - STOP
        this.loopback = loopback;
        this.activity=activity;
    }

    @Override
    public void execute() {
        if (isinterrupted) return;
        String version = PeriCoachTestApplication.getGetFirmware().getVersion();
        if (isinterrupted) return;
        uploadDialog = (UploadDialog) activity.getSupportFragmentManager().findFragmentByTag(UploadDialog.TAG);
        if (uploadDialog == null) {
            uploadDialog = new UploadDialog();
        }
        uploadDialog.show(activity.getSupportFragmentManager(), UploadDialog.TAG);
        start();
    }

    private void start() {

        ((Activity) activityListener).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uploadDialog.reset();
            }
        });
        if (IOIOUtils.getUtils().getIOIOUart() != null) {
            RX = IOIOUtils.getUtils().getIOIOUart().getInputStream();// Pin 14
            TX = IOIOUtils.getUtils().getIOIOUart().getOutputStream();// Pin 13
        }

        dummyFirmWareUploader = new DummyFirmWareUploader(TX, RX, (Activity) activityListener,
                 activityListener, ioio, loopback,uploadDialog);


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
//						initialised = firmWareUploader.deviceInit();

                        Log.d(TAG, "Initialised = " + initialised);
                        if (!initialised) {
                            resetted = false;
                            if (retries <= 2) onInitialiseFailed();    //issueAlert();
                        }
                    }
                }
                if (retries >= 3) {
                    setSuccess(false);
                    ((Activity) activityListener).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            uploadDialog.setFail("");
                            activityListener.goAndExecuteNextTest();
                        }
                    });
                    return;
                }
                dummyFirmWareUploader.getInfo();
                dummyFirmWareUploader.upload(new UploaderListener() {
                    @Override
                    public void onUploadCompleted(final boolean success) {
                        setSuccess(success);
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
                        if (!success) {
                            ((Activity) activityListener).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    uploadDialog.setFail("");
                                    activityListener.onUploadTestFinished(true,success,description,"");
                                }
                            });
                            try {
                                Thread.sleep(2 * 1000 + 500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        ((Activity) activityListener).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activityListener.onUploadTestFinished(true,success,description,"");
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
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "INIT FAILED, " + String.valueOf(3 - retries) + " Attempts Remaining",
                        Toast.LENGTH_SHORT).show();
                resetted = true;
                initialised = false;
                retries++;
            }
        });
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            dummyFirmWareUploader.stop();
        } catch (Exception e) {
        }
    }
}
