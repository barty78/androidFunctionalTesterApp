package com.pietrantuono.fragments.devices;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pietrantuono.pericoach.newtestapp.R;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class MyRecyclerCursorAdapter extends CursorRecyclerAdapter<DevicesCursorHolder> {
    private final Context context;



    public MyRecyclerCursorAdapter(Context context,Cursor cursor) {
        super(cursor);
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public void onBindViewHolderCursor(DevicesCursorHolder holder, Cursor cursor) {
        holder.setData(cursor, context);
    }

    @Override
    public DevicesCursorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.new_device_row, parent, false);
        return new DevicesCursorHolder(v);
    }
}
