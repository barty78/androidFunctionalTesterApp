package com.pietrantuono.fragments.sidePanel.holders;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public abstract class SidePanelItemHolder extends RecyclerView.ViewHolder {
    final Context context;

    SidePanelItemHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
    }

    public abstract void setData(Cursor c);

}
