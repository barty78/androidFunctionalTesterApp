package com.pietrantuono.activities;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.pericoach.newtestapp.syncadapter.StartSyncAdapterService;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import java.util.List;

import server.pojos.records.TestRecord;

@SuppressWarnings("ucd")
public class SettingsActivity  extends PreferenceActivity {
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
        Preference etp = (Preference) findPreference(getResources().getString(R.string.unprocessed));
        List<Model> records = new Select().from(TestRecord.class).execute();
        if(records.size()<=0){
            etp.setTitle("0 records unprocessed");
            etp.setSummary("");
        }
        else {
            etp.setTitle("WARNING!!! " + records.size() + " records unprocessed!");
            etp.setSummary("Click to retry");
        }
        etp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent=new Intent(SettingsActivity.this,StartSyncAdapterService.class);
                startService(intent);
                return true;
            }
        });

    }
}