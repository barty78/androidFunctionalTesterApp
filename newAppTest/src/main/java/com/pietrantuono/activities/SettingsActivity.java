package com.pietrantuono.activities;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.BuildConfig;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.recordsdb.NewRecordsSQLiteOpenHelper;
import com.pietrantuono.recordsdb.RecordsContract;
import com.pietrantuono.recordsdb.RecordsProcessor;
import com.pietrantuono.recordsyncadapter.StartSyncAdapterService;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import server.MyDoubleTypeAdapter;
import server.MyIntTypeAdapter;
import server.MyLongTypeAdapter;
import server.pojos.records.TestRecord;

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
//            switsh.setSummary(getResources().getString(R.string.default_prod_url));
            switsh.setSummary(getResources().getString(R.string.default_productiontest_url));
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

        Preference version = findPreference("version");
        if (version != null) version.setSummary(PeriCoachTestApplication.getVersion());

        Preference deviceID = findPreference("deviceID");
        if (deviceID != null) deviceID.setSummary(PeriCoachTestApplication.getDeviceid());

        Preference ioioFirmwareVersion = findPreference("ioioFirmwareVer");
        if (ioioFirmwareVersion != null) ioioFirmwareVersion.setSummary(PeriCoachTestApplication.getIOIOFirmwareVersion());
        Preference ioioHardwareVersion = findPreference("ioioHardwareVer");
        if (ioioHardwareVersion != null) ioioHardwareVersion.setSummary(PeriCoachTestApplication.getIOIOHardwareVersion());
        Preference ioioLibraryVersion = findPreference("ioioLibraryVer");
        if (ioioLibraryVersion != null) ioioLibraryVersion.setSummary(PeriCoachTestApplication.getIOIOLibraryVersion());

        Preference unprocessed = (Preference) findPreference(getResources().getString(R.string.unprocessed));
        Preference dowloadunprocessed = (Preference) findPreference(getResources().getString(R.string.download_unprocessed));
        Preference copytosd = (Preference) findPreference("copy_to_sd");
        NewRecordsSQLiteOpenHelper newRecordsSQLiteOpenHelper = NewRecordsSQLiteOpenHelper.getInstance(SettingsActivity.this);
        String selection = RecordsContract.TestRecords.UPLOADED + " = ?";
        String[] selectionargs = new String[]{"0"};
        Cursor cursor = null;
        try {
            cursor = newRecordsSQLiteOpenHelper.getReadableDatabase().query(RecordsContract.TestRecords.TABLE, null, selection, selectionargs, null, null, null);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        if(cursor==null)return;
        //List<Model> records = new Select().from(TestRecord.class).where("uploaded = ?", false).execute();
        if (cursor.getCount() <= 0) {
            Spannable title = new SpannableString("UP TO DATE");
            title.setSpan(new ForegroundColorSpan(Color.GREEN), 0, title.length(), 0);
            unprocessed.setTitle(title);
            unprocessed.setSummary("");
            unprocessed.setIcon(R.drawable.ic_ok);
            dowloadunprocessed.setEnabled(false);
        } else {
            Spannable title = new SpannableString("WARNING !!! " + cursor.getCount() + " RECORDS UNPROCESSED!");
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
        copytosd.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                copyToSD(SettingsActivity.this);
                return false;
            }
        });
    }

    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        String version = pInfo.versionName;
        return version;
    }

    private void copyToSD(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/data/" + getPackageName() + "/databases/records";
                Log.d("Settings", "DB Path: " + currentDBPath);
                String backupDBPath = "records.db";
                Log.d("Settings", "Backup Path: " + backupDBPath);
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                } else {
                    Log.d("DB BACKUP", "Cant find DB");

                }
            } else {
                Log.d("DB BACKUP", "Cant write to location");
            }
        } catch (Exception e) {
            Log.d("DB BACKUP", "Backup Failed");
        }
    }

    private void downloadUnprocessed(Context context) {
        NewRecordsSQLiteOpenHelper newRecordsSQLiteOpenHelper = NewRecordsSQLiteOpenHelper.getInstance(context);
        String selection = "uploaded = ?";
        String[] selectionArgs = new String[]{"1"};
        Cursor testRecordCursor = newRecordsSQLiteOpenHelper.getWritableDatabase().query(RecordsContract.TestRecords.TABLE, null, selection, selectionArgs, null, null, null);
        List<TestRecord> records = RecordsProcessor.reconstructRecords(testRecordCursor, newRecordsSQLiteOpenHelper);
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
        OutputStreamWriter outputStreamWriter;
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        } catch (Exception e) {
            Toast.makeText(SettingsActivity.this, "Unable to create file", Toast.LENGTH_LONG).show();
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


    private static void dowloadAll(Context context) {
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
        OutputStreamWriter outputStreamWriter;
        FileOutputStream fileOutputStream;
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

    private void getLogs() {
        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/logs");
        dir.mkdirs();
        long time = System.currentTimeMillis();
        String suffix = "" + time + ".txt";
        File file = new File(dir, FILE_LOGS.concat("" + suffix));
        OutputStreamWriter outputStreamWriter;
        FileOutputStream fileOutputStream;
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
        String to[] = {"pbartlett@analyticamedical.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd MMMM yyyy");
        Date date = new Date(time);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App logs " + sdf.format(date));
        startActivity(Intent.createChooser(emailIntent, "Send data..."));
    }


    private static List<TestRecord> getAllRecords(Context context) {
        NewRecordsSQLiteOpenHelper newRecordsSQLiteOpenHelper = NewRecordsSQLiteOpenHelper.getInstance(context);
        Cursor testRecordCursor = newRecordsSQLiteOpenHelper.getWritableDatabase().query(RecordsContract.TestRecords.TABLE, null, null, null, null, null, null);
        return RecordsProcessor.reconstructRecords(testRecordCursor, newRecordsSQLiteOpenHelper);
    }

}