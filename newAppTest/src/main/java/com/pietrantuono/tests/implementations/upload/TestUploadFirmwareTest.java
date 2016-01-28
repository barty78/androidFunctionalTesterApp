package com.pietrantuono.tests.implementations.upload;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Handler;

import com.pietrantuono.activities.fragments.sequence.holders.UploadItemHolder;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.tests.superclass.Test;
import com.pietrantuono.uploadfirmware.DummyFirmWareUploader;

import java.io.InputStream;
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
    private Boolean loopback;
    public UploadItemHolder holder;

    public TestUploadFirmwareTest(Activity activity, IOIO ioio, Boolean loopback) {
        super(activity, ioio, "Dummy Upload Firmware", false, true, 0, 0, 0);            // Blocking Test, if fails - STOP
        this.loopback = loopback;
    }

    @Override
    public void execute() {
        if (isinterrupted) return;
        String version = PeriCoachTestApplication.getGetFirmware().getVersion();
        activityListener.createUploadProgress(false, true, description + " (Version: " + version + ")", new UploadTestCallback() {
            @Override
            public void onViewHolderReady(UploadItemHolder holder) {
                if (TestUploadFirmwareTest.this.holder == null) {
                    TestUploadFirmwareTest.this.holder = holder;
                    start();
                }
            }
        });
    }

    public void start() {
        holder.setWait();
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

    }

    public void reset() {
        holder.reset();
    }

    public void setFail() {
        holder.setFail("");
    }

    public void setPass() {
        holder.setPass();
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
                holder.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                }
                holder.setPass();
            }
        }.execute();
    }

    public void setProgress(int progress) {
        holder.setProgress(progress);
    }

    private void onInitialiseFailed() {
    }


    @Override
    public void interrupt() {
    }
}
