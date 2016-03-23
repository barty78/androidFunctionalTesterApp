package com.pietrantuono.fragments.sequence.holders;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public abstract class SequenceItemHolder extends RecyclerView.ViewHolder {
    final Context context;

    SequenceItemHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
    }

    public abstract void setData(Cursor c);

}
