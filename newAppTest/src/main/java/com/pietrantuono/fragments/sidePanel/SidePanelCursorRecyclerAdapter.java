package com.pietrantuono.fragments.sidePanel;

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
import com.pietrantuono.fragments.sidePanel.holders.SidePanelItemHolder;
import com.pietrantuono.fragments.sidePanel.holders.ValueItemHolder;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.sequencedb.SequenceContracts;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class SidePanelCursorRecyclerAdapter extends CursorRecyclerAdapter<SidePanelItemHolder> {
    private final Context context;
    private final LayoutInflater layoutInflater;
    private static final int VALUE = 0;
    private static final int PROGRESS = 1;
    private static final int MSG = 2;

    @IntDef({VALUE, PROGRESS, MSG})
    public @interface Type {
    }

    public SidePanelCursorRecyclerAdapter(Cursor cursor, Context context) {
        super(cursor);
        this.context = context;

        layoutInflater = ((Activity)context).getLayoutInflater();
        setHasStableIds(true);
    }

    @Override
    public void onBindViewHolderCursor(SidePanelItemHolder holder, Cursor cursor) {
        holder.setIsRecyclable(false);
        holder.setData(cursor);

    }

    @Override
    public SidePanelItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VALUE) {
            View v = layoutInflater.inflate(R.layout.side_panel_item, parent, false);
            return new ValueItemHolder(v, context);
        }
        return null;
    }

    @Override
    @Type
    public synchronized int getItemViewType(int position) {
        Cursor c=getCursor();
        if(c==null || c.getCount()<=0)return VALUE;
        int currentPosition = c.getPosition();
        c.moveToPosition(position);
        if(c.getInt(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_IS_NORMAL_TEST))==1){
            c.moveToPosition(currentPosition);
            return VALUE;
        }
        return VALUE;
    }
}
