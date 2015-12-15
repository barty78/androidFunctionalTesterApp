package com.pietrantuono.activities;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.pericoach.newtestapp.syncadapter.StartSyncAdapterService;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.PhantomReference;
import java.util.List;

import server.MyDoubleTypeAdapter;
import server.MyIntTypeAdapter;
import server.MyLongTypeAdapter;
import server.pojos.records.TestRecord;
import server.utils.MyDatabaseUtils;

@SuppressWarnings("ucd")
public class SettingsActivity  extends PreferenceActivity {
    private final static String FILE_ALL_RECORDS="allrecords.txt";
    private final static String FILE_UNPROCESSED_RECORDS="unprocessedrecords.txt";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		OrientationUtils.setOrientation(SettingsActivity.this);
        addPreferencesFromResource(R.xml.settingsscreen);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        SwitchPreference switsh = (SwitchPreference) findPreference("use_default_url");
        switsh.setSummary(getResources().getString(R.string.default_url));
        EditTextPreference editTextPreference=(EditTextPreference) findPreference("custom_url");
        editTextPreference.setSummary(sp.getString("custom_url", getResources().getString(R.string.default_url)));
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
        if(records.size()<=0){
            Spannable title = new SpannableString("OK 0 records unprocessed");
            title.setSpan(new ForegroundColorSpan(Color.GREEN), 0, title.length(), 0);
            unprocessed.setTitle(title);
            unprocessed.setSummary("");
            unprocessed.setIcon(R.drawable.ic_ok);
            dowloadunprocessed.setEnabled(false);
        }
        else {
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
                dowloadAll();
                return false;
            }
        });
        dowloadunprocessed.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                downloadUnprocessed();
                return false;
            }
        });
    }

    private void downloadUnprocessed() {


    }

    private void dowloadAll() {
        List<TestRecord> records = new Select().from(TestRecord.class).execute();
        if(records==null || records.size()<=0){
            Toast.makeText(SettingsActivity.this,"No records found...",Toast.LENGTH_LONG).show();
            return;
        }
        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/records");
        dir.mkdirs();
        String suffix=""+System.currentTimeMillis();
        File file = new File (dir, FILE_ALL_RECORDS.concat(""+suffix));
        OutputStreamWriter outputStreamWriter = null;
        FileOutputStream fileOutputStream=null;
        try {
            fileOutputStream= new FileOutputStream(file);
            outputStreamWriter= new OutputStreamWriter(fileOutputStream);
        } catch (Exception e) {
            Toast.makeText(SettingsActivity.this,"Unable to create file",Toast.LENGTH_LONG).show();
            return;
        }

        for(int i=0;i<records.size();i++){
            TestRecord record=records.get(i);
            MyDatabaseUtils.RecontructRecord(record);
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .registerTypeAdapter(Long.class, new MyLongTypeAdapter())
                    .registerTypeAdapter(Double.class, new MyDoubleTypeAdapter())
                    .registerTypeAdapter(Integer.class, new MyIntTypeAdapter())
                    .create();
            String recordstring=gson.toJson(record, TestRecord.class);
            try {
                outputStreamWriter.write(recordstring);
            }
            catch (IOException e) {
                Toast.makeText(SettingsActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                Log.e("Exception", "File write failed: " + e.toString());
                Crashlytics.logException(e);
            }

        }
        try {
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(SettingsActivity.this,e.toString(),Toast.LENGTH_LONG).show();
            Log.e("Exception", "File close failed: " + e.toString());
            Crashlytics.logException(e);
        }
        File outFile = new File (dir, FILE_ALL_RECORDS.concat(""+suffix));
        Uri uri = Uri.fromFile(outFile);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent .setType("vnd.android.cursor.dir/email");
        String to[] = {"maurizio.pietrantuono@gmail.com"};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent .putExtra(Intent.EXTRA_STREAM, uri);

        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
        startActivity(Intent.createChooser(emailIntent , "Send email..."));
    }
}