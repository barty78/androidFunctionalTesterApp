package com.pietrantuono.tests.implementations.upload;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.tests.superclass.Test;
import com.pietrantuono.uploadfirmware.FirmWareUploader;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import ioio.lib.api.IOIO;

@SuppressWarnings("ALL")
public class UploadFirmwareTestTest extends Test {
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

    public UploadFirmwareTestTest(Activity activity, IOIO ioio) {
        super(activity, ioio, "Upload Firmware", false, true, 0, 0, 0);            // Blocking TEST, if fails - STOP
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
        uploadDialog.setWait();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                reset();
            }
        }, 3 * 1000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doProgress();
            }
        }, (3 + 3) * 1000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setFail();
            }
        }, (3 + 3 + 5) * 1000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setPass();
            }
        }, (3 + 3 + 5 + 3) * 1000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                uploadDialog.dismiss();
                activityListener.onUploadTestFinished(true, success, description, "Fail reason");

            }
        }, (3 + 3 + 5 + 3 + 2) * 1000);
    }

    public void reset() {
        uploadDialog.reset();
    }

    public void setFail() {
        uploadDialog.setFail("");
    }

    public void setPass() {
        uploadDialog.setPass();
    }

    private void doProgress() {
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                for (int i = 1; i <= 100; i++) {
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                    }
                    publishProgress(i);
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                uploadDialog.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                }
                uploadDialog.setPass();
            }
        }.execute();
    }

    public void setProgress(int progress) {
        uploadDialog.setProgress(progress);
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
