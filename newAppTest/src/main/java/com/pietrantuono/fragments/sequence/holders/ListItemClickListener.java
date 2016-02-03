package com.pietrantuono.fragments.sequence.holders;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pietrantuono.fragments.NominalToleranceDialog;
import com.pietrantuono.fragments.UpperLowerFragment;

import server.pojos.Test;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class ListItemClickListener implements View.OnClickListener {
    private static final String NOMINALTOLERANCE_DIALOG = "nominal_tolerance";
    private static final String UPPER_LOWER_DIALOG = "upper_lower";
    private final AppCompatActivity appCompatActivity;
    private final Test testToBeParsed;

    public ListItemClickListener(AppCompatActivity appCompatActivity, Test testToBeParsed) {
        this.appCompatActivity = appCompatActivity;
        this.testToBeParsed = testToBeParsed;
    }

    @Override
    public void onClick(View v) {
        if(testToBeParsed==null)return;
        if(testToBeParsed.getIsNominal()==0)showUpperLower();
        if(testToBeParsed.getIsNominal()==1)showNominalTolerance();
    }

    private void showNominalTolerance() {
        NominalToleranceDialog dialogFragment = NominalToleranceDialog.newInstance(testToBeParsed);
        dialogFragment.show(appCompatActivity.getSupportFragmentManager(), NOMINALTOLERANCE_DIALOG);
    }

    private void showUpperLower() {
        UpperLowerFragment upperLowerFragment=UpperLowerFragment.newInstance(testToBeParsed);
        upperLowerFragment.show(appCompatActivity.getSupportFragmentManager(), UPPER_LOWER_DIALOG);
    }
}
