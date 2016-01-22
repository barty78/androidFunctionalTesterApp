package com.pietrantuono.tests.implementations.upload;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.pietrantuono.activities.fragments.sequence.holders.UlploadItemHolder;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.tests.superclass.Test;
import com.pietrantuono.uploadfirmware.DummyFirmWareUploader;
import com.pietrantuono.uploadfirmware.DummyFirmWareUploader.UploaderListener;
import com.pietrantuono.uploadfirmware.ProgressAndTextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ioio.lib.api.IOIO;

public class DummyUploadFirmwareTest extends Test {
    private InputStream RX;
    private OutputStream TX;
    private DummyFirmWareUploader dummyFirmWareUploader;
    private Boolean initialised = true;
    private Boolean looping = true;
    private Boolean resetted = true;
    private AlertDialog alertDialog;
    boolean fileComparisonPassed = false;
    boolean fileMD5Passed = false;
    private int retries;
    private Boolean loopback;
    public UlploadItemHolder holder;

    public DummyUploadFirmwareTest(Activity activity, IOIO ioio, Boolean loopback) {
        super(activity, ioio, "Dummy Upload Firmware", false, true, 0, 0, 0);            // Blocking Test, if fails - STOP
        this.loopback = loopback;
    }

    @Override
    public void execute() {
        if (isinterrupted) return;
        String version = PeriCoachTestApplication.getGetFirmware().getVersion();
        activityListener.createUploadProgress(false, true, description + " (Version: " + version + ")", new UploadTestCallback() {
            @Override
            public void onViewHolderReady(UlploadItemHolder holder) {
                DummyUploadFirmwareTest.this.holder = holder;
                start();
            }
        });
    }

    public void start() {

        ((Activity) activityListener).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                holder.reset();
            }
        });
        if (IOIOUtils.getUtils().getIOIOUart() != null) {
            RX = IOIOUtils.getUtils().getIOIOUart().getInputStream();// Pin 14
            TX = IOIOUtils.getUtils().getIOIOUart().getOutputStream();// Pin 13
        }

        dummyFirmWareUploader = new DummyFirmWareUploader(TX, RX, (Activity) activityListener,
                holder, activityListener, ioio, loopback);


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
                            holder.setFail();
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
                                    holder.setFail();
                                    activityListener.goAndExecuteNextTest();
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
                                activityListener.setResult(success);
                                activityListener.goAndExecuteNextTest();
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
        ;
        try {
            alertDialog.dismiss();
            ;
        } catch (Exception e) {
        }
        ;
    }
}
