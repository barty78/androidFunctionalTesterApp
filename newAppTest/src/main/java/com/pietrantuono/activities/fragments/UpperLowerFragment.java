package com.pietrantuono.activities.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pietrantuono.pericoach.newtestapp.R;

import server.pojos.Test;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class UpperLowerFragment extends DialogFragment {
    private static final String UPPER = "upper";
    private static final String LOWER = "lower";
    private float upper;
    private float lower;

    public UpperLowerFragment() {  }

    public static UpperLowerFragment newInstance(Test testToBeParsed) {
        UpperLowerFragment upperLowerFragment= new UpperLowerFragment();
        Bundle bundle= new Bundle();
        bundle.putFloat(UPPER,testToBeParsed.getLimitParam1());
        bundle.putFloat(LOWER,testToBeParsed.getLimitParam2());
        upperLowerFragment.setArguments(bundle);
        return upperLowerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            upper=savedInstanceState.getFloat(UPPER,Float.MAX_VALUE);
            lower=savedInstanceState.getFloat(LOWER,Float.MAX_VALUE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater=getActivity().getLayoutInflater();
        View v =layoutInflater.inflate(R.layout.fragment_upper_lower_dialog, null);
        ((TextView)v.findViewById(R.id.lower)).setText(""+(lower!=Float.MAX_VALUE?lower:"ERROR"));
        ((TextView)v.findViewById(R.id.upper)).setText(""+(upper!=Float.MAX_VALUE?upper:"ERROR"));
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        return builder.create();
    }
}
