package com.pietrantuono.activities.fragments.sequence;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;
import com.pietrantuono.pericoach.newtestapp.R;

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

    public static class TesteItemHolder extends SequenceItemHolder {
        private TextView testSeqNum;
        private TextView testName;
        private TextView reading;
        private IconicsImageView result;

        public TesteItemHolder(View itemView, Context context) {
            super(itemView, context);
            testSeqNum = (TextView) itemView.findViewById(R.id.testSeqNum);
            testName = (TextView) itemView.findViewById(R.id.testName);
            reading = (TextView) itemView.findViewById(R.id.reading);
            result = (IconicsImageView) itemView.findViewById(R.id.result);
        }

        @Override
        public void setData(SequenceRowElement.RowElement rowElement) {
            if (!(rowElement instanceof SequenceRowElement.TestRowElement))
                throw new RuntimeException("Wrong adata " + Log.getStackTraceString(new Exception()));
            SequenceRowElement.TestRowElement testRowElement = (SequenceRowElement.TestRowElement) rowElement;

            testSeqNum.setText("" + testRowElement.getSequence().getCurrentTestNumber()+1);
            testName.setText(testRowElement.getDescription() != null ? testRowElement.getDescription() : "NO DESCRIPTION");
            reading.setText(testRowElement.getReading() != null ? testRowElement.getReading() : "");
            if (testRowElement.isSuccess()) {
                result.setIcon(GoogleMaterial.Icon.gmd_done);
                if(testRowElement.istest()){result.setColor(Color.GREEN);}
                else {result.setColor(context.getResources().getColor(R.color.primary));}
            } else {
                result.setIcon(GoogleMaterial.Icon.gmd_close);
                if (testRowElement.istest()){result.setColor(Color.RED);}
                else {result.setColor(context.getResources().getColor(R.color.primary));}
            }

        }
    }
}
