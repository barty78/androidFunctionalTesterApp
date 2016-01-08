package com.pietrantuono.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.pericoach.newtestapp.R;

public class SerialConsoleFragment extends Fragment {
    private TextView textView;
    private ScrollView scrollView;
    private Context context;

    public SerialConsoleFragment() {
    }
    public static SerialConsoleFragment newInstance() {
        SerialConsoleFragment fragment = new SerialConsoleFragment();
        return fragment;
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
        this.context=context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context=null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.serial_console_fragment, container, false);
        textView= (TextView) v.findViewById(R.id.text);
        scrollView = (ScrollView)v.findViewById(R.id.scroll);
        populateTextView();
        return v;
    }

    private void populateTextView() {
        textView.setText(IOIOUtils.getUtils().getUartLog());
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

}
