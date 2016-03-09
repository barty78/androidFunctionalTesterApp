package com.pietrantuono.tests.implementations.upload;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.pietrantuono.fragments.sequence.holders.UploadItemHolder;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.tests.superclass.Test;
import com.pietrantuono.uploadfirmware.DummyFirmWareUploader;

import java.io.OutputStream;

import ioio.lib.api.IOIO;

@SuppressWarnings("unused")
public class TestUploadFirmwareTest extends Test {
    private OutputStream TX;
    private DummyFirmWareUploader dummyFirmWareUploader;
    private Boolean initialised = true;
    private Boolean looping = true;
    private Boolean resetted = true;
    private AlertDialog alertDialog;
    boolean fileComparisonPassed = false;
    boolean fileMD5Passed = false;
    private int retries;
    private final AppCompatActivity activity;
    private Boolean loopback;
    private UploadDialog uploadDialog;

    public TestUploadFirmwareTest(AppCompatActivity activity, IOIO ioio, Boolean loopback) {
        super(activity, ioio, "Dummy Upload Firmware", false, true, 0, 0, 0);            // Blocking TEST, if fails - STOP
        this.activity = activity;
        this.loopback = loopback;
    }

    @Override
    public void execute() {
        if (isinterrupted) return;
        String version = PeriCoachTestApplication.getGetFirmware().getVersion();
        uploadDialog = (UploadDialog) activity.getSupportFragmentManager().findFragmentByTag(UploadDialog.TAG);
        if (uploadDialog == null) {
            uploadDialog = new UploadDialog();
        }
        uploadDialog.show(activity.getSupportFragmentManager(), UploadDialog.TAG);
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
                activityListener.goAndExecuteNextTest();

            }
        }, (3 + 3 + 5 + 3+2) * 1000);
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

    private void onInitialiseFailed() {
    }


    @Override
    public void interrupt() {
    }
}
