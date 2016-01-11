package com.pietrantuono.activities.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<Holder> {

    public RecyclerAdapter(Context context) {
        this.context = context;
    }

    private Context context;

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
