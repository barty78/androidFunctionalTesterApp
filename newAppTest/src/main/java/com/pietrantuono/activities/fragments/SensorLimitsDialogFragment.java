package com.pietrantuono.activities.fragments;


import android.app.AlertDialog;
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
public class SensorLimitsDialogFragment extends DialogFragment {

    private static final String MAX = "max";
    private static final String MIN = "min";
    private static final String VAR = "var";
    private Float max;
    private Float var;
    private Float min;

    public SensorLimitsDialogFragment() {
    }

    public static SensorLimitsDialogFragment newInstance(Test testToBeParsed) {
        SensorLimitsDialogFragment sensorLimitsDialogFragment= new SensorLimitsDialogFragment();
        Bundle bundle= new Bundle();
        bundle.putFloat(MAX,testToBeParsed.getLimitParam1());
        bundle.putFloat(MIN,testToBeParsed.getLimitParam2());
        bundle.putFloat(VAR,testToBeParsed.getLimitParam3());
        sensorLimitsDialogFragment.setArguments(bundle);
        return sensorLimitsDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            max=savedInstanceState.getFloat(MAX,Float.MAX_VALUE);
            min=savedInstanceState.getFloat(MIN,Float.MAX_VALUE);
            var=savedInstanceState.getFloat(VAR,Float.MAX_VALUE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater= getActivity().getLayoutInflater();
        View v=layoutInflater.inflate(R.layout.fragment_sensorlimits_dialog,null);
        ((TextView)v.findViewById(R.id.max)).setText(""+(max!=Float.MAX_VALUE?max:"ERROR"));
        ((TextView)v.findViewById(R.id.min)).setText(""+(min!=Float.MAX_VALUE?min:"ERROR"));
        ((TextView)v.findViewById(R.id.var)).setText(""+(var!=Float.MAX_VALUE?var:"ERROR"));
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        return builder.create();
    }
}
