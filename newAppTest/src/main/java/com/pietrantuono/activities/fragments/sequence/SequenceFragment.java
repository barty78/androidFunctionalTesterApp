package com.pietrantuono.activities.fragments.sequence;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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

public class SequenceFragment extends Fragment {
    private SequenceFragmentCallback mListener;
    private LinearLayout ll;
    private NewSequenceInterface sequence;
    private ScrollView scrollView;
    private Activity activity;
    private View v;

    public SequenceFragment() {
    }

    public static SequenceFragment newInstance() {
        SequenceFragment fragment = new SequenceFragment();
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
        View v = inflater.inflate(R.layout.sequence_fragment, container, false);
        ll = (LinearLayout) v.findViewById(R.id.ll);
        scrollView = (ScrollView)v.findViewById(R.id.scroll);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SequenceFragmentCallback) {
            this.activity=(Activity)context;
            mListener = (SequenceFragmentCallback) context;
            mListener.registerSequenceFragment(SequenceFragment.this);
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.unregisterSequenceFragment();
        mListener = null;
        activity=null;
    }

    public synchronized ProgressAndTextView addFailOrPass(final Boolean istest, final Boolean success, String reading,
                                                          String otherreading, String description, boolean isSensorTest,server.pojos.Test testToBeParsed) {

        UIHelper.ActivityUIHelperCallback callback = (UIHelper.ActivityUIHelperCallback) mListener;
        if (istest) {
            try {
                callback.getResults().get(callback.getIterationNumber()).get(sequence.getCurrentTestNumber())
                        .setTestsuccessful(success);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final ProgressAndTextView progressandtextview = new ProgressAndTextView(null, null);
        LayoutInflater inflater = activity.getLayoutInflater();

        if (istest)
            v = inflater.inflate(R.layout.sensorsummaryrowitem, null);
        else
            v = inflater.inflate(R.layout.voltagerowitem, null);
        TextView number = (TextView) v.findViewById(R.id.testSeqNum);
        TextView text = (TextView) v.findViewById(R.id.testName);
        TextView passfail = (TextView) v.findViewById(R.id.testResultIndText);
        TextView readingtextView = (TextView) v.findViewById(R.id.reading);
        if (reading != null && !reading.isEmpty())
            readingtextView.setText(reading);
        else
            readingtextView.setText("");
        ProgressBar progress = (ProgressBar) v.findViewById(R.id.testResultInd);
        progressandtextview.setProgress(progress);
        progressandtextview.setTextView(passfail);
        try {
            number.setText("" + (sequence.getCurrentTestNumber() + 1));
        } catch (Exception e) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
            Crashlytics.logException(e);
        }
        try {
            String Testdescription = description;
            if (Testdescription == null)
                Testdescription = sequence.getCurrentTestDescription();

            if (otherreading == null)
                text.setText(Testdescription);
            else
                text.setText(Testdescription + " " + otherreading);
        } catch (Exception e) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
            Crashlytics.logException(e);
        }
        if (success) {
            if (istest)
                passfail.setText("PASS");
            else
                passfail.setText("DONE");
            Resources res = activity.getResources();
            Drawable background = null;
            if (istest)
                background = res.getDrawable(R.drawable.greenprogress);
            else
                background = res.getDrawable(R.drawable.blueprogress);
            progress.setProgressDrawable(background);

        } else {
            passfail.setText("FAIL");
            Resources res = activity.getResources();
            Drawable background = res.getDrawable(R.drawable.redprogress);
            progress.setProgressDrawable(background);
        }
        if(!isSensorTest)v.setOnClickListener(new ListItemClickListener((AppCompatActivity)getActivity(),testToBeParsed));
        else{v.setOnClickListener(new SensorItemClickListener((AppCompatActivity) getActivity(), testToBeParsed));}
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final ViewTreeObserver observer = ll.getViewTreeObserver();
                observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        observer.removeOnPreDrawListener(this);
                        if (activity == null || activity.isFinishing()) return true;
                        ((ActivityCallback) activity).goAndExecuteNextTest();
                        return true;

                    }
                });

                ll.addView(v);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });


            }
        });

        return progressandtextview;
    }

    public void addSensorTestCompletedRow(NewMSensorResult mSensorResult, Test testToBeParsed) {
        if (activity == null || activity.isFinishing())
            return;
        int RED = activity.getResources().getColor(R.color.dark_red);
        int GREEN = activity.getResources().getColor(R.color.dark_green);
        LayoutInflater inflater = activity.getLayoutInflater();
        v = inflater.inflate(R.layout.sensors_summary_row_item, null);
        v.setOnClickListener(new SensorItemClickListener((AppCompatActivity)getActivity(),testToBeParsed));
        TextView testName = (TextView) v.findViewById(R.id.testName);
        testName.setText(mSensorResult.getDescription());
        TextView testSeqNum = (TextView) v.findViewById(R.id.testSeqNum);
        testSeqNum.setText("" + (sequence.getCurrentTestNumber() + 1));
        TextView avg0 = (TextView) v.findViewById(R.id.avg0);
        if (mSensorResult.getSensor0AvgPass())
            avg0.setTextColor(GREEN);
        else
            avg0.setTextColor(RED);
        avg0.setText(Short.toString(mSensorResult.getSensor0avg()));

        TextView avg1 = (TextView) v.findViewById(R.id.avg1);
        if (mSensorResult.getSensor1AvgPass())
            avg1.setTextColor(GREEN);
        else
            avg1.setTextColor(RED);
        avg1.setText(Short.toString(mSensorResult.getSensor1avg()));

        TextView avg2 = (TextView) v.findViewById(R.id.avg2);
        if (mSensorResult.getSensor2AvgPass())
            avg2.setTextColor(GREEN);
        else
            avg2.setTextColor(RED);
        avg2.setText(Short.toString(mSensorResult.getSensor2avg()));

        TextView passfail = (TextView) v.findViewById(R.id.pass_or_fail_avg_text);
        ProgressBar progress = (ProgressBar) v.findViewById(R.id.pass_or_fail_avg_indicator);
        if (mSensorResult.getSensor0AvgPass() && mSensorResult.getSensor1AvgPass()
                && mSensorResult.getSensor2AvgPass()) {
            passfail.setText("PASS");
            Resources res = activity.getResources();
            Drawable background = null;
            background = res.getDrawable(R.drawable.greenprogress);
            progress.setProgressDrawable(background);

        } else {
            passfail.setText("FAIL");
            Resources res = activity.getResources();
            Drawable background = res.getDrawable(R.drawable.redprogress);
            progress.setProgressDrawable(background);
        }

        TextView stability0 = (TextView) v.findViewById(R.id.stability0);
        if (mSensorResult.getSensor0stabilitypass())
            stability0.setTextColor(GREEN);
        else
            stability0.setTextColor(RED);
        stability0.setText("" + (mSensorResult.getSensor0max() - mSensorResult.getSensor0min() > 0
                ? mSensorResult.getSensor0max() - mSensorResult.getSensor0min() : (short) 0));

        TextView stability1 = (TextView) v.findViewById(R.id.stability1);
        if (mSensorResult.getSensor1stabilitypass())
            stability1.setTextColor(GREEN);
        else
            stability1.setTextColor(RED);
        stability1.setText("" + (mSensorResult.getSensor1max() - mSensorResult.getSensor1min() > 0
                ? mSensorResult.getSensor1max() - mSensorResult.getSensor1min() : (short) 0));

        TextView stability2 = (TextView) v.findViewById(R.id.stability2);
        if (mSensorResult.getSensor2stabilitypass())
            stability2.setTextColor(GREEN);
        else
            stability2.setTextColor(RED);
        stability2.setText("" + (mSensorResult.getSensor2max() - mSensorResult.getSensor2min() > 0
                ? mSensorResult.getSensor2max() - mSensorResult.getSensor2min() : (short) 0));

        TextView pass_or_fail_stability_text = (TextView) v.findViewById(R.id.pass_or_fail_stability_text);
        ProgressBar pass_or_fail_stability_indicator = (ProgressBar) v
                .findViewById(R.id.pass_or_fail_stability_indicator);
        if (mSensorResult.getSensor0stabilitypass() && mSensorResult.getSensor1stabilitypass()
                && mSensorResult.getSensor2stabilitypass()) {
            pass_or_fail_stability_text.setText("PASS");
            Resources res = activity.getResources();
            Drawable background = null;
            background = res.getDrawable(R.drawable.greenprogress);
            pass_or_fail_stability_indicator.setProgressDrawable(background);

        } else {
            pass_or_fail_stability_text.setText("FAIL");
            Resources res = activity.getResources();
            Drawable background = res.getDrawable(R.drawable.redprogress);
            pass_or_fail_stability_indicator.setProgressDrawable(background);
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ll.addView(v);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

            }
        });
    }




    public synchronized ProgressAndTextView createUploadProgress(final Boolean istest, final Boolean success,String description) {
        UIHelper.ActivityUIHelperCallback callback = (UIHelper.ActivityUIHelperCallback) activity;
        if (istest) {
            try {
                callback.getResults().get(callback.getIterationNumber()).get(sequence.getCurrentTestNumber())
                        .setTestsuccessful(success);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final ProgressAndTextView progressandtextview = new ProgressAndTextView(null, null);
        LayoutInflater inflater = activity.getLayoutInflater();

        if (istest)
            v = inflater.inflate(R.layout.sensorsummaryrowitem, null);
        else
            v = inflater.inflate(R.layout.voltagerowitem, null);
        TextView number = (TextView) v.findViewById(R.id.testSeqNum);
        TextView text = (TextView) v.findViewById(R.id.testName);
        TextView passfail = (TextView) v.findViewById(R.id.testResultIndText);
        TextView readingtextView = (TextView) v.findViewById(R.id.reading);
        readingtextView.setText("");
        ProgressBar progress = (ProgressBar) v.findViewById(R.id.testResultInd);
        progressandtextview.setProgress(progress);
        progressandtextview.setTextView(passfail);
        progressandtextview.setDescriptionTextView(text);
        try {
            number.setText("" + (sequence.getCurrentTestNumber() + 1));
        } catch (Exception e) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
            Crashlytics.logException(e);
        }
        try {
            String Testdescription = description;
            if (Testdescription == null)
                Testdescription = sequence.getCurrentTestDescription();


            text.setText(Testdescription);
        } catch (Exception e) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
            Crashlytics.logException(e);
        }
        if (success) {
            if (istest)
                passfail.setText("PASS");
            else
                passfail.setText("DONE");
            Resources res = activity.getResources();
            Drawable background = null;
            if (istest)
                background = res.getDrawable(R.drawable.greenprogress);
            else
                background = res.getDrawable(R.drawable.blueprogress);
            progress.setProgressDrawable(background);

        } else {
            passfail.setText("FAIL");
            Resources res = activity.getResources();
            Drawable background = res.getDrawable(R.drawable.redprogress);
            progress.setProgressDrawable(background);
        }
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ll.addView(v);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

            }
        });
        return progressandtextview;
    }

    public void cleanUI() {
        ll.removeAllViews();
    }


    public void setSequence(NewSequenceInterface sequence) {
        this.sequence = sequence;
    }

    public NewSequenceInterface getSequence() {
        return sequence;
    }


    public interface SequenceFragmentCallback {
        void registerSequenceFragment(SequenceFragment sequenceFragment);

        void unregisterSequenceFragment();
    }
}
