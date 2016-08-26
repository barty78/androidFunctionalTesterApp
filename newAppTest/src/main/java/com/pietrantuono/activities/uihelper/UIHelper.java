package com.pietrantuono.activities.uihelper;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.pietrantuono.devicesprovider.DevicesContentProvider;
import com.pietrantuono.fragments.PagerAdapter;
import com.pietrantuono.fragments.sequence.NewSequenceFragment;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.constants.NewMResult;
import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.recordsdb.NewRecordsSQLiteOpenHelper;
import com.pietrantuono.recordsdb.RecordsContract;
import com.pietrantuono.sequencedb.SequenceContracts;
import com.pietrantuono.sequencedb.SequenceProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import analytica.pericoach.android.Contract;
import server.pojos.Job;

public class UIHelper {

    private final Activity activity;
    private NewSequenceInterface sequence;
    private static final String TAG = "UIHelper";
    private static NewSequenceFragment sequenceFragment;

    public UIHelper(Activity activity, NewSequenceInterface sequence) {
        this.activity = activity;
        this.sequence = sequence;
        setOverallFailOrPass(false, "");
        setupViewpager(activity);
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
    }

    private void setupViewpager(Activity activity) {
        ViewPager viewPager = (ViewPager) activity.findViewById(R.id.pager);
        AppCompatActivity appcompat = (AppCompatActivity) activity;
        viewPager.setAdapter(new PagerAdapter(appcompat.getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount());
        viewPager.setCurrentItem(1);
    }

    public void setupChronometer(Activity activity) {
        Chronometer cronometer = (Chronometer) activity.findViewById(R.id.chronometer);
        cronometer.setOnChronometerTickListener(new UIHelper.MyOnChronometerTickListener());

    }

    public void startChronometer(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Chronometer cronometer = (Chronometer) activity.findViewById(R.id.chronometer);
                cronometer.setBase(SystemClock.elapsedRealtime());
                cronometer.start();
            }
        });
        Log.d(TAG, "Chronometer started");
    }

    public void stopChronometer(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Chronometer cronometer = (Chronometer) activity.findViewById(R.id.chronometer);
                cronometer.stop();
            }
        });
        Log.d(TAG, "Chronometer stopped");
    }

    public void registerSequenceFragment(NewSequenceFragment sequenceFragment) {
        this.sequenceFragment = sequenceFragment;
    }

    public void unregisterSequenceFragment() {
        this.sequenceFragment = null;
    }

       public void updateStats(Job job, AppCompatActivity activity) {
        ContentResolver resolver = activity.getContentResolver();
        NewRecordsSQLiteOpenHelper newRecordsSQLiteOpenHelper = NewRecordsSQLiteOpenHelper.getInstance(PeriCoachTestApplication.getContext());
        Job primaryJob = PeriCoachTestApplication.getPrimaryJob();

        long numberOfDevices = job.getQuantity();

        if (primaryJob != null || job.getPrimaryJobId() != 0) {
//        String selection = Contract.DevicesColumns.DEVICES_JOB_ID + "= " + primaryJob.getId() +
//                " AND " + "(" + Contract.DevicesColumns.DEVICES_EXEC_TESTS + " & " + job.getTesttypeId() + ") = " + job.getTesttypeId();
//        Cursor c = resolver.query(DevicesContentProvider.CONTENT_URI, null, selection, null, null);
//        int numberOfDevices = c.getCount();
//        c.close();

            String selection = Contract.DevicesColumns.DEVICES_JOB_ID + "= " + primaryJob.getId() +
                    " AND " + "(" + Contract.DevicesColumns.DEVICES_EXEC_TESTS + " & " + job.getTesttypeId() + ") = " + job.getTesttypeId() +
                    " AND " + "(" + Contract.DevicesColumns.DEVICES_STATUS + " & " + job.getTesttypeId() + ") = " + job.getTesttypeId();
            Cursor c = resolver.query(DevicesContentProvider.CONTENT_URI, null, selection, null, null);
            int numberOfDevicesPassed = c.getCount();
            c.close();

            selection = Contract.DevicesColumns.DEVICES_JOB_ID + "= " + primaryJob.getId() +
                    " AND " + "(" + Contract.DevicesColumns.DEVICES_EXEC_TESTS + " & " + job.getTesttypeId() + ") = " + job.getTesttypeId() +
                    " AND " + "(" + Contract.DevicesColumns.DEVICES_STATUS + " & " + job.getTesttypeId() + ") = 0";
            c = resolver.query(DevicesContentProvider.CONTENT_URI, null, selection, null, null);

            int numberOfDevicesFailed = c.getCount();
            c.close();

            long count = 0;
            long sum = 0L;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            selection = RecordsContract.TestRecords.JOB_NO + "= " + job.getJobno();
            try {
                c = newRecordsSQLiteOpenHelper.getReadableDatabase().query(RecordsContract.TestRecords.TABLE,
                        new String[]{RecordsContract.TestRecords.DURATION},
                        selection, null, null, null, null);

                count = c.getCount();
                while(c.moveToNext()) {
                    try {
                        sum += sdf.parse(c.getString(0)).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLiteException e) {
            }
            
            if (count > 0) {
                sdf = new SimpleDateFormat("mm:ss");
                Date avgTestTime = new Date(sum / count);
                ((TextView) activity.findViewById(R.id.avg_time)).setText("" + sdf.format(avgTestTime));
            } else {
                ((TextView) activity.findViewById(R.id.avg_time)).setText("00:00");

            }
            c.close();

            c = newRecordsSQLiteOpenHelper.getReadableDatabase().query(RecordsContract.TestRecords.TABLE, null, null, null, null, null, null);
            int testCount = c.getCount();
            c.close();

            ((TextView) activity.findViewById(R.id.num_of_devices)).setText("" + numberOfDevices);
            ((TextView) activity.findViewById(R.id.devices_passed)).setText("" + numberOfDevicesPassed);
            ((TextView) activity.findViewById(R.id.devices_failed)).setText("" + numberOfDevicesFailed);
            ((TextView) activity.findViewById(R.id.test_count)).setText("" + testCount);
            ((DonutProgress) activity.findViewById(R.id.progress_stats)).setProgress((int) ((numberOfDevicesPassed / (float) numberOfDevices) * 100));
        }
    }

    public void setRecordId(long recordId) {
        if (sequenceFragment != null) sequenceFragment.forceLoaderUpdate(recordId);
    }

    public interface ActivityUIHelperCallback {
        ArrayList<ArrayList<NewMResult>> getResults();

        int getIterationNumber();

        void clearSerialConsole();
    }

    private static class MyOnChronometerTickListener implements OnChronometerTickListener {
        @Override
        public void onChronometerTick(Chronometer cArg) {
            long time = SystemClock.elapsedRealtime() - cArg.getBase();
            int h = (int) (time / 3600000);
            int m = (int) (time - h * 3600000) / 60000;
            int s = (int) (time - h * 3600000 - m * 60000) / 1000;
            String mm = m < 10 ? "0" + m : m + "";
            String ss = s < 10 ? "0" + s : s + "";
            cArg.setText(mm + ":" + ss);
        }
    }

    public void setJobId(AppCompatActivity activity, final String jobnumber) {
        if (PeriCoachTestApplication.getIsRetestAllowed()) {
            activity.setTitle("Job " + jobnumber + " (Retests)");
        } else {
            activity.setTitle("Job " + jobnumber + " (No Retests)");
        }
        TextView textView = (TextView) activity.findViewById(R.id.jobNum);
        textView.setText(jobnumber);
    }

    public void setConnected(final boolean conn) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (conn) {
                    setStatusMSG("READY\n LOAD UUT", true);
                } else {
                    setStatusMSG("FIXTURE\n NOT READY", false);
                }
            }
        });
    }

    public synchronized void addView(final String label, final String text, boolean goAndExecuteNextTest) {
        addView(label, text, 0, goAndExecuteNextTest);
    }


    public synchronized void addView(final String label, final String text, final int color, final boolean goAndExecuteNextTest) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final LinearLayout layout = (LinearLayout) activity.findViewById(R.id.barcode_and_serial);
                LayoutInflater inflater = activity.getLayoutInflater();
                View view = inflater.inflate(R.layout.add_view, null);
                TextView labeltv = (TextView) view.findViewById(R.id.label);
                TextView texttv = (TextView) view.findViewById(R.id.text);
                if (label != null)
                    labeltv.setText(label);
                if (text != null)
                    texttv.setText(text);

                else
                    texttv.setTextColor(color);
                final ViewTreeObserver observer = layout.getViewTreeObserver();
                observer.addOnPreDrawListener(new OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        observer.removeOnPreDrawListener(this);
                        if (goAndExecuteNextTest)
                            ((ActivityCallback) activity).goAndExecuteNextTest();
                        return true;
                    }
                });
                layout.addView(view);
            }
        });
    }

    public synchronized void setStatusMSG(final String message, final Boolean success) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = (TextView) activity.findViewById(R.id.teststatusmsg);
                tv.setText(message);
                if (success == null) {
                    tv.setTextColor(activity.getResources().getColor(R.color.list_highlight_color));
                } else {
                    if (success) {
                        tv.setTextColor(Color.GREEN);
                    } else {
                        tv.setTextColor(Color.RED);
                    }
                }
            }
        });
    }

    public synchronized void addFailOrPass(final Boolean istest, final Boolean success, String reading,
                                           String otherreading, String description, boolean issensortest, long recordId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_IS_NORMAL_TEST, istest ? 1 : 0);
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_RESULT, success ? 1 : 0);
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_READING, reading != null ? reading : "");
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_OTHER_READING, otherreading != null ? otherreading : "");
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_NAME, description != null ? description : "");
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_IS_SENSOR_TEST, issensortest ? 1 : 0);
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_TIME_INSERTED, System.currentTimeMillis());
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_FOREIGN_KEY_ID_OF_RECORD, recordId);
        activity.getContentResolver().insert(SequenceProvider.TESTS_CONTENT_URI, contentValues);
    }

    public void setOverallFailOrPass(final Boolean show, final String barcode) {
        final ActivityUIHelperCallback callback = (ActivityUIHelperCallback) activity;
        Boolean success = true;
        if (callback.getIterationNumber() >= 0)
            for (int i = 0; i <= sequence.getNumberOfSteps() - 1; i++) {
                if (!sequence.getSequence().get(i).isTest()) continue;
                if (sequence.getSequence().get(i).isSensorTest()) {
                    ArrayList<NewMResult> currentResults = callback.getResults().get(callback.getIterationNumber());
                    if (currentResults == null || currentResults.size() <= 0) {
                        success = false;
                        continue;
                    }
                    NewMResult currentResult = currentResults.get(i);
                    if (currentResult == null) {
                        success = false;
                        continue;
                    }
                    if (!currentResult.isTestsuccessful()) success = false;
                } else {
                    if (!sequence.getSequence().get(i).isSuccess()) success = false;
                }
            }

        if (sequenceFragment != null) {

            if (show) sequenceFragment.setOverallFailOrPass(success, barcode);
        }

    }

    public void setCurrentAndNextTaskinUI() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView currenttask = (TextView) activity.findViewById(R.id.currenttask);
                TextView nexttask = (TextView) activity.findViewById(R.id.nexttask);
                String currentstepdesc = null;
                String currentstenumber = null;
                try {
                    currentstepdesc = sequence.getCurrentTestDescription();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    currentstenumber = "" + (sequence.getCurrentTestNumber() + 1);

                } catch (Exception e) {
                    e.printStackTrace();

                }
                if (currentstepdesc == null)
                    currenttask.setText("");
                else {
                    if (currentstenumber == null)
                        currenttask.setText(currentstepdesc);
                    else
                        currenttask.setText(currentstenumber + " " + currentstepdesc);
                }
                String nexttaskdescription = null;
                String nexttasknumber = null;
                try {
                    nexttaskdescription = sequence.getNextTestDescription();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    nexttasknumber = "" + (sequence.getCurrentTestNumber() + 2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (nexttaskdescription == null)
                    nexttask.setText("");
                else {
                    if (nexttasknumber == null)
                        nexttask.setText(nexttaskdescription);
                    else
                        nexttask.setText(nexttasknumber + " " + nexttaskdescription);
                }
            }
        });
    }

    public void addSensorTestCompletedRow(NewMSensorResult mSensorResult, long recordId) {
        if(mSensorResult==null)return;
        ContentValues contentValues = new ContentValues();
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_IS_NORMAL_TEST, 0);
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_NAME, mSensorResult.getDescription() != null ? mSensorResult.getDescription() : "");
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_IS_SENSOR_TEST,  1 );
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_FOREIGN_KEY_ID_OF_RECORD, recordId);
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_TIME_INSERTED, System.currentTimeMillis());

        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_S0_AVG, mSensorResult.getSensor0avg());
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_S0_MAX, mSensorResult.getSensor0max());
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_S0_MIN, mSensorResult.getSensor0min());
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_S0_AVG_PASS, mSensorResult.getSensor0AvgPass()==true?1:0);
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_S0_STABILITY_PASS, mSensorResult.getSensor0stabilitypass()==true?1:0);

        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_S1_AVG, mSensorResult.getSensor1avg());
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_S1_MAX, mSensorResult.getSensor1max());
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_S1_MIN, mSensorResult.getSensor1min());
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_S1_AVG_PASS, mSensorResult.getSensor1AvgPass()==true?1:0);
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_S1_STABILITY_PASS, mSensorResult.getSensor1stabilitypass()==true?1:0);

        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_S2_AVG, mSensorResult.getSensor2avg());
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_S2_MAX, mSensorResult.getSensor2max());
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_S2_MIN, mSensorResult.getSensor2min());
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_S2_AVG_PASS, mSensorResult.getSensor2AvgPass()==true?1:0);
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_S2_STABILITY_PASS, mSensorResult.getSensor2stabilitypass()==true?1:0);

        activity.getContentResolver().insert(SequenceProvider.TESTS_CONTENT_URI, contentValues);
    }

    /**
     * Does nothing
     */
    public void setSequence(NewSequenceInterface sequence) {
        this.sequence = sequence;

    }

    public void onUploadTestFinished(final Boolean success, String description, long recordId, String failReason) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_READING, failReason != null ? failReason : "");
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_IS_NORMAL_TEST, 0);
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_IS_SENSOR_TEST,  0);
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_IS_UPLOAD_TEST,  1);
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_RESULT, success ? 1 : 0);
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_NAME, description != null ? description : "");
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_TIME_INSERTED, System.currentTimeMillis());
        contentValues.put(SequenceContracts.Tests.TABLE_TESTS_FOREIGN_KEY_ID_OF_RECORD, recordId);
        activity.getContentResolver().insert(SequenceProvider.TESTS_CONTENT_URI, contentValues);
    }

    public void cleanUI(final Activity activity) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                setStatusMSG("", true);
                setOverallFailOrPass(false, "");
                Chronometer cronometer = (Chronometer) activity.findViewById(R.id.chronometer);
                cronometer.setBase(SystemClock.elapsedRealtime());
                cronometer.setText("00:00");
                LinearLayout layout = (LinearLayout) activity.findViewById(R.id.barcode_and_serial);
                layout.removeAllViews();
                if (sequenceFragment != null) sequenceFragment.cleanUI();
                TextView currenttask = (TextView) activity.findViewById(R.id.currenttask);
                currenttask.setText("");
                TextView nexttask = (TextView) activity.findViewById(R.id.nexttask);
                nexttask.setText("");
                final ActivityUIHelperCallback activityUIHelperCallback = (ActivityUIHelperCallback) activity;
                activityUIHelperCallback.clearSerialConsole();
            }
        });
    }

    public void removeOverallFailOrPass() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (sequenceFragment != null) sequenceFragment.removeOverallFailOrPass();

            }
        });
    }

    public void playSound(Activity activity) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(activity.getApplicationContext(), notification);
        r.play();
    }
}
