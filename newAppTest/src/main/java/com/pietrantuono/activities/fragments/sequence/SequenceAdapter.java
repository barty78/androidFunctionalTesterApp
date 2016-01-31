package com.pietrantuono.activities.fragments.sequence;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pietrantuono.activities.fragments.sequence.holders.SensorItemHolder;
import com.pietrantuono.activities.fragments.sequence.holders.SequenceItemHolder;
import com.pietrantuono.activities.fragments.sequence.holders.TestItemHolder;
import com.pietrantuono.activities.fragments.sequence.holders.UploadItemHolder;
import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.tests.implementations.upload.UploadTestCallback;

import java.util.ArrayList;

import hugo.weaving.DebugLog;
import server.pojos.Test;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class SequenceAdapter extends RecyclerView.Adapter<SequenceItemHolder> {
    private final String TAG = getClass().getSimpleName();
    private ArrayList<SequenceRowElement.RowElement> items;
    private LayoutInflater layoutInflater;
    private Context context;

    public static final int TEST = 0;
    public static final int STEP = 1;
    public static final int SENSOR_TEST = 2;
    public static final int FW_UPLOAD = 3;
    private UploadTestCallback callback;

    public void clear() {
        items.clear();
        this.callback = null;
    }


    @IntDef({TEST, STEP, FW_UPLOAD, SENSOR_TEST})
    public @interface Type {
    }

    public SequenceAdapter(FragmentActivity activity, Context context) {
        this.context = context;
        items = new ArrayList<>();
        layoutInflater = activity.getLayoutInflater();
        setHasStableIds(true);
    }


    @Nullable
    @Override
    public SequenceItemHolder onCreateViewHolder(ViewGroup parent, @Type int viewType) {
        if (viewType == TEST) {
            View v = layoutInflater.inflate(R.layout.new_sequence_row_item, parent, false);
            return new TestItemHolder(v, context);
        }
        if (viewType == SENSOR_TEST) {
            View v = layoutInflater.inflate(R.layout.new_sensor_summary_row_item, parent, false);
            return new SensorItemHolder(v, context);
        }
        if (viewType == FW_UPLOAD) {
            View v = layoutInflater.inflate(R.layout.new_upload_row_item, parent, false);
            return new UploadItemHolder(v, context);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(SequenceItemHolder holder, int position) {
        Log.d(TAG, holder.toString());
        Log.d(TAG, items.get(position).toString());
        holder.setData(items.get(position), position);
        if((holder instanceof UploadItemHolder) && callback!=null) {
            callback.onViewHolderReady((UploadItemHolder) holder);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    @Type
    public int getItemViewType(int position) {
        SequenceRowElement.RowElement item = items.get(position);
        if (item instanceof SequenceRowElement.TestRowElement) return TEST;
        if (item instanceof SequenceRowElement.SensorTestRowElement) return SENSOR_TEST;
        if (item instanceof SequenceRowElement.UploadRowElement) return FW_UPLOAD;
        return TEST;
    }

    public void addTest(Boolean istest, Boolean success, String reading, String otherreading, String description, boolean isSensorTest, Test testToBeParsed, NewSequenceInterface sequence) {
        SequenceRowElement.TestRowElement testRowElement = new SequenceRowElement.TestRowElement(istest, success, reading, otherreading, description, isSensorTest, testToBeParsed, sequence);
        items.add(testRowElement);
        notifyItemInserted(items.size());
    }

    public void addSensorTest(NewMSensorResult mSensorResult, Test testToBeParsed, NewSequenceInterface sequence) {
        SequenceRowElement.SensorTestRowElement sensorTestRowElement = new SequenceRowElement.SensorTestRowElement(mSensorResult, testToBeParsed, sequence);
        items.add(sensorTestRowElement);
        notifyItemInserted(items.size());
    }

    public void addUploadRow(Boolean istest, Boolean success, String description, NewSequenceInterface sequence, UploadTestCallback callback) {
        this.callback = callback;
        SequenceRowElement.UploadRowElement uploadRowElement= new SequenceRowElement.UploadRowElement(description,istest,success,sequence);
        items.add(uploadRowElement);
        notifyItemInserted(items.size());
    }


    @Override
    public long getItemId(int position) {
        return items.get(position).hashCode();
    }
}
