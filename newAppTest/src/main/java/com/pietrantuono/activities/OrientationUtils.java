package com.pietrantuono.activities;

import com.pietrantuono.pericoach.newtestapp.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;

public class OrientationUtils {
	public static void setOrientation(Activity activity){
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        String key=activity.getResources().getString(R.string.reverse_orientation);
		if(sharedPref.getBoolean(key, false))activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
		else activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

}
