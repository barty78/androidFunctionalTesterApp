package com.pietrantuono.activities;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.BuildConfig;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.recordsdb.RecordsContract;
import com.pietrantuono.recordsdb.RecordsHelper;
import com.pietrantuono.recordsyncadapter.StartSyncAdapterService;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import server.MyDoubleTypeAdapter;
import server.MyIntTypeAdapter;
import server.MyLongTypeAdapter;
import server.pojos.records.Readings;
import server.pojos.records.S0;
import server.pojos.records.S1;
import server.pojos.records.S2;
import server.pojos.records.Sensors;
import server.pojos.records.Test;
import server.pojos.records.TestRecord;
import server.utils.MyDatabaseUtils;

@SuppressWarnings("ucd")
public class SettingsActivity extends PreferenceActivity {
    private final static String FILE_ALL_RECORDS = "allrecords";
    private final static String FILE_UNPROCESSED_RECORDS = "unprocessedrecords";
    private static final String FILE_LOGS = "logs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OrientationUtils.setOrientation(SettingsActivity.this);
        addPreferencesFromResource(R.xml.settingsscreen);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        SwitchPreference switsh = (SwitchPreference) findPreference("use_default_url");
        if (BuildConfig.DEBUG) {
            switsh.setSummary(getResources().getString(R.string.default_dev_url));
        } else {
            switsh.setSummary(getResources().getString(R.string.default_prod_url));
        }
        EditTextPreference editTextPreference = (EditTextPreference) findPreference("custom_url");
        if (BuildConfig.DEBUG) {
            editTextPreference.setSummary(sp.getString("custom_url", getResources().getString(R.string.default_dev_url)));
        } else {
            editTextPreference.setSummary(sp.getString("custom_url", getResources().getString(R.string.default_prod_url)));
        }
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        sharedPref.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (!key.equals(getResources().getString(R.string.reverse_orientation))) return;
                OrientationUtils.setOrientation(SettingsActivity.this);
            }
        });
        Preference unprocessed = (Preference) findPreference(getResources().getString(R.string.unprocessed));
        Preference dowloadunprocessed = (Preference) findPreference(getResources().getString(R.string.download_unprocessed));
        List<Model> records = new Select().from(TestRecord.class).where("uploaded = ?", false).execute();
        if (records.size() <= 0) {
            Spannable title = new SpannableString("UP TO DATE");
            title.setSpan(new ForegroundColorSpan(Color.GREEN), 0, title.length(), 0);
            unprocessed.setTitle(title);
            unprocessed.setSummary("");
            unprocessed.setIcon(R.drawable.ic_ok);
            dowloadunprocessed.setEnabled(false);
        } else {
            Spannable title = new SpannableString("WARNING !!! " + records.size() + " RECORDS UNPROCESSED!");
            title.setSpan(new ForegroundColorSpan(Color.RED), 0, title.length(), 0);
            unprocessed.setTitle(title);
            unprocessed.setSummary("CLICK TO RETRY");
            unprocessed.setIcon(R.drawable.ic_warning);
            dowloadunprocessed.setEnabled(true);
        }
        unprocessed.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(SettingsActivity.this, StartSyncAdapterService.class);
                startService(intent);
                return true;
            }
        });
        Preference dowloadall = (Preference) findPreference(getResources().getString(R.string.download_all));
        dowloadall.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                dowloadAll(SettingsActivity.this);
                return false;
            }
        });
        dowloadunprocessed.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                downloadUnprocessed(SettingsActivity.this);
                return false;
            }
        });
        findPreference(getResources().getString(R.string.get_logs)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getLogs();
                return false;
            }
        });

    }

    private void downloadUnprocessed(Context context) {
        List<TestRecord> records = new Select().from(TestRecord.class).where("uploaded = ?", false).execute();
        RecordsHelper recordsHelper = RecordsHelper.get(context);
        String selection="uploaded = ?";
        String[] selectionArgs= new String[]{"1"};
        Cursor testRecordCursor = recordsHelper.getWritableDatabase().query(RecordsContract.TestRecords.TABLE, null, selection, selectionArgs, null, null, null);
        records=reconstrucRecords(context,testRecordCursor);
        if (records == null || records.size() <= 0) {
            Toast.makeText(SettingsActivity.this, "No records found...", Toast.LENGTH_LONG).show();
            return;
        }
        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/records");
        dir.mkdirs();
        long time = System.currentTimeMillis();
        String suffix = "" + time + ".txt";
        File file = new File(dir, FILE_UNPROCESSED_RECORDS.concat("" + suffix));
        OutputStreamWriter outputStreamWriter = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        } catch (Exception e) {
            Toast.makeText(SettingsActivity.this, "Unable to create file", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < records.size(); i++) {
            TestRecord record = records.get(i);
            MyDatabaseUtils.RecontructRecord(record);
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .registerTypeAdapter(Long.class, new MyLongTypeAdapter())
                    .registerTypeAdapter(Double.class, new MyDoubleTypeAdapter())
                    .registerTypeAdapter(Integer.class, new MyIntTypeAdapter())
                    .create();
            String recordstring = gson.toJson(record, TestRecord.class);
            try {
                outputStreamWriter.write(recordstring + "\n");
            } catch (IOException e) {
                Toast.makeText(SettingsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                Log.e("Exception", "File write failed: " + e.toString());
                Crashlytics.logException(e);
            }


        }
        try {
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(SettingsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            Log.e("Exception", "File close failed: " + e.toString());
            Crashlytics.logException(e);
        }
        File outFile = new File(dir, FILE_UNPROCESSED_RECORDS.concat("" + suffix));
        Uri uri = Uri.fromFile(outFile);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"pbartlett@analyticamedical.com", "maurizio.pietrantuono@gmail.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd MMMM yyyy");
        Date date = new Date(time);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Unprocessed records " + sdf.format(date));
        startActivity(Intent.createChooser(emailIntent, "Send data..."));
    }


    public static void dowloadAll(Context context) {
        List<TestRecord> records = getAllRecords(context);
        if (records == null || records.size() <= 0) {
            Toast.makeText(context, "No records found...", Toast.LENGTH_LONG).show();
            return;
        }
        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/records");
        dir.mkdirs();
        long time = System.currentTimeMillis();
        String suffix = "" + time + ".txt";
        File file = new File(dir, FILE_ALL_RECORDS.concat("" + suffix));
        OutputStreamWriter outputStreamWriter = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        } catch (Exception e) {
            Toast.makeText(context, "Unable to create file", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < records.size(); i++) {
            TestRecord record = records.get(i);
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .registerTypeAdapter(Long.class, new MyLongTypeAdapter())
                    .registerTypeAdapter(Double.class, new MyDoubleTypeAdapter())
                    .registerTypeAdapter(Integer.class, new MyIntTypeAdapter())
                    .create();
            String recordstring = gson.toJson(record, TestRecord.class);
            try {
                outputStreamWriter.write(recordstring + "\n");
            } catch (IOException e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                Log.e("Exception", "File write failed: " + e.toString());
                Crashlytics.logException(e);
            }

        }
        try {
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            Log.e("Exception", "File close failed: " + e.toString());
            Crashlytics.logException(e);
        }
        File outFile = new File(dir, FILE_ALL_RECORDS.concat("" + suffix));
        Uri uri = Uri.fromFile(outFile);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"pbartlett@analyticamedical.com", "maurizio.pietrantuono@gmail.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd MMMM yyyy");
        Date date = new Date(time);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "All records " + sdf.format(date));
        context.startActivity(Intent.createChooser(emailIntent, "Send data..."));
    }


    private void foo() {

    }

    private void getLogs() {
        List<TestRecord> records = new Select().from(TestRecord.class).execute();
        if (records == null || records.size() <= 0) {
            Toast.makeText(SettingsActivity.this, "No records found...", Toast.LENGTH_LONG).show();
            return;
        }
        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/logs");
        dir.mkdirs();
        long time = System.currentTimeMillis();
        String suffix = "" + time + ".txt";
        File file = new File(dir, FILE_LOGS.concat("" + suffix));
        OutputStreamWriter outputStreamWriter = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        } catch (Exception e) {
            Toast.makeText(SettingsActivity.this, "Unable to create file", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
                outputStreamWriter.write(line + "\n");
            }

        } catch (IOException e) {
        }
        try {
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(SettingsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            Log.e("Exception", "File close failed: " + e.toString());
            Crashlytics.logException(e);
        }
        File outFile = new File(dir, FILE_LOGS.concat("" + suffix));
        Uri uri = Uri.fromFile(outFile);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"pbartlett@analyticamedical.com", "maurizio.pietrantuono@gmail.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd MMMM yyyy");
        Date date = new Date(time);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App logs " + sdf.format(date));
        startActivity(Intent.createChooser(emailIntent, "Send data..."));
    }


    public static List<TestRecord> getAllRecords(Context context) {
        RecordsHelper recordsHelper = RecordsHelper.get(context);
        Cursor testRecordCursor = recordsHelper.getWritableDatabase().query(RecordsContract.TestRecords.TABLE, null, null, null, null, null, null);
        return reconstrucRecords(context,testRecordCursor);
    }

    public static List<TestRecord> reconstrucRecords(Context context,Cursor testRecordCursor) {
        List<TestRecord> records = new ArrayList<>();
        RecordsHelper recordsHelper = RecordsHelper.get(context);
        while (testRecordCursor.moveToNext()) {
            TestRecord testRecord = new TestRecord();
            TestRecord record = new TestRecord();
            record.setBarcode(testRecordCursor.getLong(testRecordCursor.getColumnIndexOrThrow("Barcode")));
            record.setDuration(testRecordCursor.getString(testRecordCursor.getColumnIndexOrThrow("Duration")));
            record.setFixtureNo(testRecordCursor.getString(testRecordCursor.getColumnIndexOrThrow("FixtureNo")));
            record.setFWVer(testRecordCursor.getString(testRecordCursor.getColumnIndexOrThrow("FWVer")));
            record.setJobNo(testRecordCursor.getLong(testRecordCursor.getColumnInprettydexOrThrow("JobNo")));
            record.setModel(testRecordCursor.getLong(testRecordCursor.getColumnIndexOrThrow("Model")));
            record.setResult(testRecordCursor.getLong(testRecordCursor.getColumnIndexOrThrow("Result")));
            record.setSerial(testRecordCursor.getString(testRecordCursor.getColumnIndexOrThrow("Serial")));
            record.setStartedAt(testRecordCursor.getString(testRecordCursor.getColumnIndexOrThrow("StartedAt")));
            record.setBT_Addr(testRecordCursor.getString(testRecordCursor.getColumnIndexOrThrow("BT_Addr")));

            long recordId = testRecordCursor.getLong(testRecordCursor.getColumnIndexOrThrow(RecordsContract.TestRecords.ID));
            Readings readings = new Readings();

            Sensors sensors = new Sensors();
            String selection = RecordsContract.Sensors.COL_READINGS + "=?";
            String[] selectionArgs = new String[]{"" + recordId};
            Cursor sensorsCursor = recordsHelper.getWritableDatabase().query(RecordsContract.Sensors.TABLE, null, selection, selectionArgs, null, null, null);

            if (sensorsCursor.moveToFirst()) {
                long sensorsId = sensorsCursor.getLong(sensorsCursor.getColumnIndexOrThrow(RecordsContract.Sensors.ID));
                sensorsCursor.close();
                S0 s0 = new S0();
                selection = RecordsContract.Sensors0.ID + "=?";
                selectionArgs = new String[]{"" + sensorsId};
                Cursor s0Cursor = recordsHelper.getWritableDatabase().query(RecordsContract.SingleS0.TABLE, null, selection, selectionArgs, null, null, null);
                if (s0Cursor.moveToFirst()) {
                    List<Long> errorcodes = new ArrayList<>();
                    List<Long> avgs = new ArrayList<>();
                    List<Long> idtests = new ArrayList<>();
                    List<Long> maxs = new ArrayList<>();
                    List<Long> mins = new ArrayList<>();
                    List<Long> results = new ArrayList<>();

                    do {
                        errorcodes.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow("ErrorCode")));
                        avgs.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow("Avg")));
                        idtests.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow("IDTest")));
                        maxs.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow("Max")));
                        mins.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow("Min")));
                        results.add(s0Cursor.getLong(s0Cursor.getColumnIndexOrThrow("Result")));
                    }

                    while (s0Cursor.moveToNext());

                    s0.setResult(results);
                    s0.setMin(mins);
                    s0.setMax(maxs);
                    s0.setIDTest(idtests);
                    s0.setAvg(avgs);
                    s0.setErrorCodes(errorcodes);
                    sensors.setS0(s0);
                    s0Cursor.close();
                }

                S1 s1 = new S1();

                selection = RecordsContract.Sensors1.ID + "=?";
                selectionArgs = new String[]{"" + sensorsId};
                Cursor s1Cursor = recordsHelper.getWritableDatabase().query(RecordsContract.SingleS1.TABLE, null, selection, selectionArgs, null, null, null);
                if (s1Cursor.moveToFirst()) {
                    List<Long> errorcodes = new ArrayList<>();
                    List<Long> avgs = new ArrayList<>();
                    List<Long> idtests = new ArrayList<>();
                    List<Long> maxs = new ArrayList<>();
                    List<Long> mins = new ArrayList<>();
                    List<Long> results = new ArrayList<>();

                    do {
                        errorcodes.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow("ErrorCode")));
                        avgs.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow("Avg")));
                        idtests.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow("IDTest")));
                        maxs.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow("Max")));
                        mins.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow("Min")));
                        results.add(s1Cursor.getLong(s1Cursor.getColumnIndexOrThrow("Result")));
                    }

                    while (s1Cursor.moveToNext());

                    s1.setResult(results);
                    s1.setMin(mins);
                    s1.setMax(maxs);
                    s1.setIDTest(idtests);
                    s1.setAvg(avgs);
                    s1.setErrorCodes(errorcodes);
                    sensors.setS1(s1);
                    s1Cursor.close();
                }

                sensors.setS1(s1);

                S2 s2 = new S2();

                selection = RecordsContract.Sensors2.ID + "=?";
                selectionArgs = new String[]{"" + sensorsId};
                Cursor s2Cursor = recordsHelper.getWritableDatabase().query(RecordsContract.SingleS2.TABLE, null, selection, selectionArgs, null, null, null);
                if (s2Cursor.moveToFirst()) {
                    List<Long> errorcodes = new ArrayList<>();
                    List<Long> avgs = new ArrayList<>();
                    List<Long> idtests = new ArrayList<>();
                    List<Long> maxs = new ArrayList<>();
                    List<Long> mins = new ArrayList<>();
                    List<Long> results = new ArrayList<>();

                    do {
                        errorcodes.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow("ErrorCode")));
                        avgs.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow("Avg")));
                        idtests.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow("IDTest")));
                        maxs.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow("Max")));
                        mins.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow("Min")));
                        results.add(s2Cursor.getLong(s2Cursor.getColumnIndexOrThrow("Result")));
                    }

                    while (s2Cursor.moveToNext());

                    s2.setResult(results);
                    s2.setMin(mins);
                    s2.setMax(maxs);
                    s2.setIDTest(idtests);
                    s2.setAvg(avgs);
                    s2.setErrorCodes(errorcodes);
                    sensors.setS2(s2);
                    s2Cursor.close();
                }

                sensors.setS2(s2);

                readings.setSensors(sensors);
            }

            Test test = new Test();
            selection = RecordsContract.SingleTest.Test + "=?";
            selectionArgs = new String[]{"" + recordId};
            Cursor singleTestCursor = recordsHelper.getWritableDatabase().query(RecordsContract.SingleTest.TABLE, null, selection, selectionArgs, null, null, null);

            if (singleTestCursor.moveToNext()) {
                List<Long> errorcodes = new ArrayList<>();
                List<Long> testsIds = new ArrayList<>();
                List<Long> results = new ArrayList<>();
                List<Double> values = new ArrayList<>();

                do {
                    errorcodes.add(singleTestCursor.getLong(singleTestCursor.getColumnIndexOrThrow("ErrorCode")));
                    testsIds.add(singleTestCursor.getLong(singleTestCursor.getColumnIndexOrThrow("IDTest")));
                    results.add(singleTestCursor.getLong(singleTestCursor.getColumnIndexOrThrow("Result")));
                    values.add(singleTestCursor.getDouble(singleTestCursor.getColumnIndexOrThrow("Value")));

                }
                while (singleTestCursor.moveToNext());

                test.setResult(results);
                test.setIDTest(testsIds);
                test.setValue(values);
                test.setErrorCode(errorcodes);
            }
            singleTestCursor.close();

            readings.setTest(test);

            record.setReadings(readings);

            records.add(record);
        }

        return records;
    }

}