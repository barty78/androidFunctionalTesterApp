package com.pietrantuono.activities.fragments.sequence.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pietrantuono.activities.fragments.sequence.SequenceRowElement;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public abstract class SequenceItemHolder extends RecyclerView.ViewHolder {
    public Context context;

    public SequenceItemHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
    }

    public abstract void setData(SequenceRowElement.RowElement rowElement);

    public class SensorItemHolder extends SequenceItemHolder {
        public SensorItemHolder(View v, Context context) {
            super(v,context);
        }

        @Override
        public void setData(SequenceRowElement.RowElement rowElement) {

        }
    }
}
