package com.pietrantuono.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pietrantuono.pericoach.newtestapp.R;

public class SerialConsoleFragment extends Fragment implements SerialConsoleFragmentCallback {
    private TextView textView;
    private ScrollView scrollView;
    private Activity activity;
    private CardView card;

    public SerialConsoleFragment() {
    }

    public static SerialConsoleFragment newInstance() {
        return new SerialConsoleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
        if(activity!=null){((SerialConsoleFragmentCallback)activity).setCallback(SerialConsoleFragment.this);}
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.serial_console_fragment, container, false);
        textView = (TextView) v.findViewById(R.id.text);
        scrollView = (ScrollView) v.findViewById(R.id.scroll);
        card=(CardView) v.findViewById(R.id.card);
        return v;
    }


    @Override
    public void updateUI(final String text) {
        if (activity == null) return;

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                card.setVisibility(View.VISIBLE);
                String currentText=textView.getText().toString();
                if(currentText.length()>100*1000)currentText="";
//                String newText=currentText + text;
                currentText += text;
//                textView.setText(newText);
                textView.setText(currentText);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void clearSerialConsole(){
        if (activity == null) return;
        textView.setText("");
    }

    @Override
    public void setCallback(SerialConsoleFragmentCallback callback) {/*Nothing to do here*/}

    @Override
    public void removeCallback() {/*Nothing to do here*/}

}
