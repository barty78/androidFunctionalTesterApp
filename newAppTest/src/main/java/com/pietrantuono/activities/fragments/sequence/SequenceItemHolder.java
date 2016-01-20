package com.pietrantuono.activities.fragments.sequence;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsImageView;
import com.pietrantuono.pericoach.newtestapp.R;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public abstract class SequenceItemHolder extends RecyclerView.ViewHolder {

    public SequenceItemHolder(View itemView) {
        super(itemView);
    }

    public void setData(SequenceRowElement.RowElement rowElement) {
    }

    public static class TesteItemHolder extends SequenceItemHolder {
        private TextView testSeqNum;
        private TextView testName;
        private TextView reading;
        private IconicsImageView progresscontainer;

        public TesteItemHolder(View itemView) {
            super(itemView);
            testSeqNum = (TextView) itemView.findViewById(R.id.testSeqNum);
            testName = (TextView) itemView.findViewById(R.id.testName);
            reading = (TextView) itemView.findViewById(R.id.reading);
            progresscontainer = (IconicsImageView) itemView.findViewById(R.id.progresscontainer);
        }

        @Override
        public void setData(SequenceRowElement.RowElement rowElement) {
        }
    }
}
