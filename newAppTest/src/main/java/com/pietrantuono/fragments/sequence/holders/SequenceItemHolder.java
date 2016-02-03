package com.pietrantuono.fragments.sequence.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pietrantuono.fragments.sequence.SequenceRowElement;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public abstract class SequenceItemHolder extends RecyclerView.ViewHolder {
    public Context context;

    public SequenceItemHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
    }

    public abstract void setData(SequenceRowElement.RowElement rowElement, int position);


}
