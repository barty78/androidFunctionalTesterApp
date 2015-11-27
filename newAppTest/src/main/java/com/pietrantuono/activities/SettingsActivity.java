package com.pietrantuono.activities;

import com.pietrantuono.pericoach.newtestapp.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

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
				if(!key.equals(getResources().getString(R.string.reverse_orientation)))return;
				OrientationUtils.setOrientation(SettingsActivity.this);
			}
		});
    }
}