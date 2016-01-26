package com.pietrantuono.activities.fragments.sequence;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pietrantuono.activities.uihelper.ActivityCallback;
import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.tests.implementations.upload.UploadTestCallback;

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
        adapter = new SequenceAdapter(getActivity(), activity);
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

    public synchronized void addTest(final Boolean istest, final Boolean success, String reading,
                                     String otherreading, String description, boolean isSensorTest, Test testToBeParsed) {
        adapter.addTest(istest, success, reading, otherreading, description, isSensorTest, testToBeParsed, sequence);
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        Handler handler = new Handler(getActivity().getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((ActivityCallback) activity).goAndExecuteNextTest();
            }
        }, 100);
    }

    public void addSensorTest(NewMSensorResult mSensorResult, Test testToBeParsed) {
        adapter.addSensorTest(mSensorResult, testToBeParsed, sequence);
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        Handler handler = new Handler(getActivity().getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((ActivityCallback) activity).goAndExecuteNextTest();
            }
        }, 100);
    }

    public void addUploadRow(final Boolean istest, final Boolean success, String description, UploadTestCallback callback) {
        adapter.addUploadRow(istest, success, description, sequence, callback);
        recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
    }

    public void cleanUI() {
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    public void setSequence(NewSequenceInterface sequence) {
        this.sequence = sequence;
    }


    public interface SequenceFragmentCallback {
        void registerSequenceFragment(NewSequenceFragment sequenceFragment);

        void unregisterSequenceFragment();
    }
}
