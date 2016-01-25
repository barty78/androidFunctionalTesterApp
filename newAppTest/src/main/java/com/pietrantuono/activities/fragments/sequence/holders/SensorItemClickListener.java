package com.pietrantuono.activities.fragments.sequence.holders;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pietrantuono.activities.fragments.SensorLimitsDialogFragment;

import server.pojos.Test;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class SensorItemClickListener implements View.OnClickListener {
    private static final String SENSOR_LIMITS_DIALOG_FRAGMENT = "sensor_limists_dialog_fragment";
    private final AppCompatActivity activity;
    private final Test testToBeParsed;

    public SensorItemClickListener(AppCompatActivity activity, Test testToBeParsed) {
        this.activity = activity;
        this.testToBeParsed = testToBeParsed;
    }

    @Override
    public void onClick(View v) {
        if(testToBeParsed==null)return;
        SensorLimitsDialogFragment sensorLimitsDialogFragmnet= SensorLimitsDialogFragment.newInstance(testToBeParsed);
        sensorLimitsDialogFragmnet.show(activity.getSupportFragmentManager(),SENSOR_LIMITS_DIALOG_FRAGMENT);
    }
}
