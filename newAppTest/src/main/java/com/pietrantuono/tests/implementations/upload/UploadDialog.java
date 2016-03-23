package com.pietrantuono.tests.implementations.upload;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
        donutProgress = (DonutProgress) v.findViewById(R.id.dialog_fragment_progress);
        textView = (TextView) v.findViewById(R.id.text);
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Firmware upload");
        return dialog;
    }

    public void reset() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textView != null) textView.setVisibility(View.INVISIBLE);
                if (result != null) result.setVisibility(View.INVISIBLE);
                if (donutProgress != null) donutProgress.setVisibility(View.INVISIBLE);
                if (donutProgress != null) donutProgress.setProgress(0);
            }
        });

    }

    public void setFail(final String text) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textView != null) textView.setVisibility(View.VISIBLE);
                if (textView != null) textView.setText(text);
                if (result != null) result.setVisibility(View.VISIBLE);
                if (donutProgress != null) donutProgress.setVisibility(View.INVISIBLE);
                if (result != null) result.setIcon(GoogleMaterial.Icon.gmd_cancel);
                if (result != null) result.setColor(Color.RED);
            }
        });
    }

    public void setPass() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textView != null) textView.setVisibility(View.INVISIBLE);
                if (result != null) result.setVisibility(View.VISIBLE);
                if (donutProgress != null) donutProgress.setVisibility(View.INVISIBLE);
                if (result != null) result.setIcon(GoogleMaterial.Icon.gmd_check_circle);
                if (result != null) result.setColor(Color.GREEN);
            }
        });

    }

    public void setProgress(final int progress) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textView != null) textView.setVisibility(View.INVISIBLE);
                if (result != null) result.setVisibility(View.INVISIBLE);
                if (donutProgress != null) donutProgress.setVisibility(View.VISIBLE);
                if (donutProgress != null) donutProgress.setProgress(progress);
            }
        });

    }

    public void setWait() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textView != null) textView.setVisibility(View.VISIBLE);
                if (textView != null) textView.setText("Please wait...");
                if (result != null) result.setVisibility(View.VISIBLE);
                if (donutProgress != null) donutProgress.setVisibility(View.INVISIBLE);
                if (result != null) result.setIcon(GoogleMaterial.Icon.gmd_hourglass_empty);
                if (result != null)
                    result.setColor(getActivity().getResources().getColor(R.color.primary));
            }
        });

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
