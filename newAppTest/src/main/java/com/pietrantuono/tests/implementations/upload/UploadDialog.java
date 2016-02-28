package com.pietrantuono.tests.implementations.upload;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;
import com.pietrantuono.pericoach.newtestapp.R;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class UploadDialog extends DialogFragment {

    public static final java.lang.String TAG = "uplaod_dialog";
    private IconicsImageView result;
    private DonutProgress donutProgress;
    private TextView textView;

    public UploadDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
        result = (IconicsImageView) v.findViewById(R.id.dialog_fragment_result);
        donutProgress= (DonutProgress) v.findViewById(R.id.dialog_fragment_progress);
        textView=(TextView)v.findViewById(R.id.text);
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Firmware upload");
        return dialog;
    }

    public void reset() {
        textView.setVisibility(View.INVISIBLE);
        result.setVisibility(View.INVISIBLE);
        donutProgress.setVisibility(View.INVISIBLE);
        donutProgress.setProgress(0);
    }

    public void setFail(String text) {
        textView.setVisibility(View.VISIBLE);
        textView.setText(text);
        result.setVisibility(View.VISIBLE);
        donutProgress.setVisibility(View.INVISIBLE);
        result.setIcon(GoogleMaterial.Icon.gmd_cancel);
        result.setColor(Color.RED);
    }

    public void setPass() {
        textView.setVisibility(View.INVISIBLE);
        result.setVisibility(View.VISIBLE);
        donutProgress.setVisibility(View.INVISIBLE);
        result.setIcon(GoogleMaterial.Icon.gmd_check_circle);
        result.setColor(Color.GREEN);
    }

    public void setProgress(int progress) {
        textView.setVisibility(View.INVISIBLE);
        result.setVisibility(View.INVISIBLE);
        donutProgress.setVisibility(View.VISIBLE);
        donutProgress.setProgress(progress);
    }

    public void setWait() {
        textView.setVisibility(View.VISIBLE);
        textView.setText("Please wait...");
        result.setVisibility(View.VISIBLE);
        donutProgress.setVisibility(View.INVISIBLE);
        result.setIcon(GoogleMaterial.Icon.gmd_hourglass_empty);
        result.setColor(getActivity().getResources().getColor(R.color.primary));
    }

}
