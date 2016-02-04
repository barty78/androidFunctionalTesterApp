package com.pietrantuono.activities.fragments.sequence;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pietrantuono.activities.uihelper.ActivityCallback;
import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.tests.implementations.upload.UploadTestCallback;

import server.pojos.Test;

public class NewSequenceFragment extends Fragment {
    private static final String TAG = "NewSequenceFragment";
    private SequenceFragmentCallback mListener;
    private NewSequenceInterface sequence;
    private Activity activity;
    private RecyclerView recyclerView;
    private SequenceAdapter adapter;
    private TextView success_failure_text;
    private LinearLayout success_failure_container;

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
        success_failure_text =(TextView)v.findViewById(R.id.text);
        success_failure_container=(LinearLayout)v.findViewById(R.id.success_failure_container);
        recyclerView.setAdapter(adapter);
        setContainerVisible();
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

    public synchronized void addSensorTest(NewMSensorResult mSensorResult, Test testToBeParsed) {
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
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    public void cleanUI() {
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    public void removeOverallFailOrPass(){
        success_failure_container.setVisibility(View.GONE);
    }

    public void setSequence(NewSequenceInterface sequence) {
        this.sequence = sequence;
    }

    public void setOverallFailOrPass(final boolean success) {
        success_failure_container.setVisibility(View.VISIBLE);
        success_failure_container.requestLayout();
        if (!success) {
            success_failure_container.setBackgroundColor(Color.RED);
            success_failure_text.setText("FAIL!");
        } else {
            success_failure_container.setBackgroundColor(Color.GREEN);
            success_failure_text.setText("PASS");
        }
    }

    private void setContainerVisible(){
        if(true)return;
        success_failure_container.getLayoutParams().height = 1;
        success_failure_container.setVisibility(View.VISIBLE);
        ValueAnimator animation = ValueAnimator.ofInt(0, getActivity().getResources().getDimensionPixelOffset(R.dimen.succes_conteiner_height));
        animation.setDuration(1000);
        animation.start();
        success_failure_container.getLayoutParams().height = (int) animation.getAnimatedValue();

    }

    private void setContainerInvisible(){}

    public interface SequenceFragmentCallback {
        void registerSequenceFragment(NewSequenceFragment sequenceFragment);
        void unregisterSequenceFragment();
    }

    private int dpToPx(int dp){
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}

