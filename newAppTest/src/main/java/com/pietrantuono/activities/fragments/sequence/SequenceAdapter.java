package com.pietrantuono.activities.fragments.sequence;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.uploadfirmware.ProgressAndTextView;

import java.util.ArrayList;

import server.pojos.Test;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class SequenceAdapter extends RecyclerView.Adapter<SequenceItemHolder> {
    private ArrayList<SequenceRowElement.RowElement> items;
    private LayoutInflater layoutInflater;
    private Context context;

    public static final int TEST=0;
    public static final int STEP=1;
    public static final int SENSOR_TEST=2;
    public static final int FW_UPLOAD=3;

    public void clear() {
        items.clear();
    }

    @IntDef({TEST, STEP, FW_UPLOAD,SENSOR_TEST})
    public @interface Type {  }

    public SequenceAdapter(FragmentActivity activity, Context context) {
        this.context = context;
        items= new ArrayList<>();
        layoutInflater=activity.getLayoutInflater();
    }

    @Nullable
    @Override
    public SequenceItemHolder onCreateViewHolder(ViewGroup parent, @Type int viewType) {
        if(viewType==TEST){
            View v = layoutInflater.inflate(R.layout.new_sequence_row_item, parent, false);
            return new TestItemHolder(v,context);
        }
        if(viewType==SENSOR_TEST){
            View v = layoutInflater.inflate(R.layout.new_sensor_summary_row_item, parent, false);
            return new SensorItemHolder(v,context);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(SequenceItemHolder holder, int position) {
        holder.setData(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    @Type
    public int getItemViewType(int position) {
        SequenceRowElement.RowElement item = items.get(position);
        if(item instanceof SequenceRowElement.TestRowElement)return TEST;
        if(item instanceof SequenceRowElement.SensorTestRowElement) return SENSOR_TEST;
        if(item instanceof SequenceRowElement.UploadRowElement)return FW_UPLOAD;
        return TEST;
    }

    public void addTest(Boolean istest, Boolean success, String reading, String otherreading, String description, boolean isSensorTest, Test testToBeParsed, NewSequenceInterface sequence) {
        SequenceRowElement.TestRowElement testRowElement= new SequenceRowElement.TestRowElement( istest, success, reading, otherreading, description, isSensorTest,testToBeParsed,sequence);
        items.add(testRowElement);
        notifyItemInserted(items.size());
    }

    public void addSensorTest(NewMSensorResult mSensorResult, Test testToBeParsed,NewSequenceInterface sequence) {
        SequenceRowElement.SensorTestRowElement sensorTestRowElement= new SequenceRowElement.SensorTestRowElement(mSensorResult,testToBeParsed,sequence);
        items.add(sensorTestRowElement);
        notifyItemInserted(items.size());
    }

    public ProgressAndTextView addUploadRow(Boolean istest, Boolean success, String description,NewSequenceInterface sequence) {
        return null;
    }




}
