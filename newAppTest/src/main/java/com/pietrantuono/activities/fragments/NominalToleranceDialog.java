package com.pietrantuono.activities.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pietrantuono.pericoach.newtestapp.R;

import server.pojos.Test;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class NominalToleranceDialog extends DialogFragment {

    private static final String NOMINAL = "nominal";
    private static final String TOLERANCE = "tolerance";
    private float nominal;
    private float tolerance;

    public static NominalToleranceDialog newInstance(Test testToBeParsed) {
        NominalToleranceDialog nominalToleranceDialog=  new NominalToleranceDialog();
        Bundle bundle= new Bundle();
        bundle.putFloat(NOMINAL,testToBeParsed.getLimitParam1());
        bundle.putFloat(TOLERANCE,testToBeParsed.getLimitParam2());
        nominalToleranceDialog.setArguments(bundle);
        return nominalToleranceDialog;
    }

    public NominalToleranceDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            nominal = savedInstanceState.getFloat(NOMINAL, Float.MAX_VALUE);
            tolerance=savedInstanceState.getFloat(TOLERANCE,Float.MAX_VALUE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v=inflater.inflate(R.layout.fragment_nominaltolerance_fragment,null);
        ((TextView)v.findViewById(R.id.nominal)).setText(""+(nominal!=Float.MAX_VALUE?nominal:"ERROR"));
        ((TextView)v.findViewById(R.id.tolerance)).setText(""+(tolerance!=Float.MAX_VALUE?tolerance:"ERROR"));
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        return builder.create();
    }
}
