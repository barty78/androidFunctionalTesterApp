package com.pietrantuono.fragments.sequence;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pietrantuono.fragments.devices.CursorRecyclerAdapter;
import com.pietrantuono.fragments.sequence.holders.SensorItemHolder;
import com.pietrantuono.fragments.sequence.holders.SequenceItemHolder;
import com.pietrantuono.fragments.sequence.holders.TestItemHolder;
import com.pietrantuono.fragments.sequence.holders.UploadItemHolder;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.sequencedb.SequenceContracts;
import com.pietrantuono.tests.implementations.upload.UploadTestCallback;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class SequenceCursorRecyclerAdapter extends CursorRecyclerAdapter<SequenceItemHolder> {
    private final Context context;
    private final LayoutInflater layoutInflater;
    public static final int TEST = 0;
    public static final int STEP = 1;
    public static final int SENSOR_TEST = 2;
    public static final int FW_UPLOAD = 3;
    private UploadTestCallback callback;

    @IntDef({TEST, STEP, FW_UPLOAD, SENSOR_TEST})
    public @interface Type {
    }

    public SequenceCursorRecyclerAdapter(Cursor cursor, Context context, UploadTestCallback callback) {
        super(cursor);
        this.context = context;
        this.callback=callback;
        layoutInflater = ((Activity)context).getLayoutInflater();
        setHasStableIds(true);
    }

    @Override
    public void onBindViewHolderCursor(SequenceItemHolder holder, Cursor cursor) {
        holder.setIsRecyclable(false);
        holder.setData(cursor);
        if((holder instanceof UploadItemHolder) && callback!=null) {

            callback.onViewHolderReady((UploadItemHolder) holder);
        }
    }

    @Override
    public SequenceItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    @Type
    public synchronized int getItemViewType(int position) {
        Cursor c=getCursor();
        if(c==null || c.getCount()<=0)return TEST;
        int currentPosition = c.getPosition();
        c.moveToPosition(position);
        if(c.getInt(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_IS_NORMAL_TEST))==1){
            c.moveToPosition(currentPosition);
            return TEST;
        }
        if(c.getInt(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_IS_SENSOR_TEST))==1){
            c.moveToPosition(currentPosition);
            return SENSOR_TEST;
        }
        if(c.getInt(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_IS_UPLOAD_TEST))==1){
            c.moveToPosition(currentPosition);
            return FW_UPLOAD;
        }
        return TEST;
    }


}
