package com.pietrantuono.activities.fragments.sequence;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pietrantuono.constants.NewMSensorResult;
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

    public static final int TEST=0;
    public static final int STEP=1;
    public static final int SENSOR_TEST=2;
    public static final int FW_UPLOAD=3;

    @IntDef({TEST, STEP, FW_UPLOAD,SENSOR_TEST})
    public @interface Type {  }

    @Nullable
    @Override
    public SequenceItemHolder onCreateViewHolder(ViewGroup parent, @Type int viewType) {
        if(viewType==TEST){
            View v = layoutInflater.inflate(R.layout.new_sequence_row_item, parent, false);
            return new SequenceItemHolder.TesteItemHolder(v);
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

    public void addTest(Boolean istest, Boolean success, String reading, String otherreading, String description, boolean isSensorTest, Test testToBeParsed) {
        SequenceRowElement.TestRowElement testRowElement= new SequenceRowElement.TestRowElement( istest, success, reading, otherreading, description, isSensorTest,testToBeParsed);
        items.add(testRowElement);
        notifyItemInserted(items.size());
    }

    public void addSensorTest(NewMSensorResult mSensorResult, Test testToBeParsed) {

    }

    public ProgressAndTextView addUploadRow(Boolean istest, Boolean success, String description) {
        return null;
    }

    public SequenceAdapter(FragmentActivity activity) {
        items= new ArrayList<>();
        layoutInflater=activity.getLayoutInflater();
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


}
