package com.pietrantuono.activities.fragments;


import android.support.v7.app.AlertDialog;
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
    private static final String DESCRIPTION = "description";
    private float upper;
    private float lower;
    private String description;

    public UpperLowerFragment() {  }

    public static UpperLowerFragment newInstance(Test testToBeParsed) {
        UpperLowerFragment upperLowerFragment= new UpperLowerFragment();
        Bundle bundle= new Bundle();
        bundle.putFloat(UPPER,testToBeParsed.getLimitParam1());
        bundle.putFloat(LOWER, testToBeParsed.getLimitParam2());
        bundle.putString(DESCRIPTION,testToBeParsed.getName()!=null?testToBeParsed.getName():"");
        upperLowerFragment.setArguments(bundle);
        return upperLowerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            upper=getArguments().getFloat(UPPER, Float.MAX_VALUE);
            lower=getArguments().getFloat(LOWER, Float.MAX_VALUE);
            description=getArguments().getString(DESCRIPTION);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater=getActivity().getLayoutInflater();
        View v =layoutInflater.inflate(R.layout.fragment_upper_lower_dialog, null);
        ((TextView)v.findViewById(R.id.lower)).setText(""+(lower!=Float.MAX_VALUE?lower:"ERROR"));
        ((TextView)v.findViewById(R.id.upper)).setText("" + (upper != Float.MAX_VALUE ? upper:"ERROR"));
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setTitle(description!=null?description:"");
        return builder.create();
    }
}
