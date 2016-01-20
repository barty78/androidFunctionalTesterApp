package com.pietrantuono.activities.fragments.sequence;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.pietrantuono.activities.fragments.ListItemClickListener;
import com.pietrantuono.activities.fragments.SensorItemClickListener;
import com.pietrantuono.activities.uihelper.ActivityCallback;
import com.pietrantuono.activities.uihelper.UIHelper;
import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.uploadfirmware.ProgressAndTextView;

import server.pojos.Test;

public class NewSequenceFragment extends Fragment {
    private SequenceFragmentCallback mListener;
    private NewSequenceInterface sequence;
    private Activity activity;
    private RecyclerView recyclerView;
    private SequenceAdapter adapter;

    public NewSequenceFragment() {
    }

    public static NewSequenceFragment newInstance() {
        NewSequenceFragment fragment = new NewSequenceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_sequence_fragment, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter=new SequenceAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SequenceFragmentCallback) {
            this.activity = (Activity) context;
            mListener = (SequenceFragmentCallback) context;
            mListener.registerSequenceFragment(NewSequenceFragment.this);
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.unregisterSequenceFragment();
        mListener = null;
        activity = null;
    }

    public synchronized ProgressAndTextView addTest(final Boolean istest, final Boolean success, String reading,
                                                          String otherreading, String description, boolean isSensorTest, Test testToBeParsed) {
        adapter.addTest(istest, success,reading, otherreading, description,  isSensorTest, testToBeParsed);
        return null;
    }

    public ProgressAndTextView addSensorTest(NewMSensorResult mSensorResult, Test testToBeParsed) {
        adapter.addSensorTest(mSensorResult, testToBeParsed);
        return null;
    }


    public synchronized ProgressAndTextView addUploadRow(final Boolean istest, final Boolean success, String description) {
        return adapter.addUploadRow(istest,success,description);
    }

    public void cleanUI() {}

    public void setSequence(NewSequenceInterface sequence) {
        this.sequence = sequence;
    }

    public NewSequenceInterface getSequence() {
        return sequence;
    }


    public interface SequenceFragmentCallback {
        void registerSequenceFragment(NewSequenceFragment sequenceFragment);

        void unregisterSequenceFragment();
    }
}
