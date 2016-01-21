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

    public class SensorItemHolder extends SequenceItemHolder {
        public SensorItemHolder(View v, Context context) {
            super(v,context);
        }

        @Override
        public void setData(SequenceRowElement.RowElement rowElement) {

        }
    }
}
