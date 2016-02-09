package com.pietrantuono.fragments.devices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pietrantuono.pericoach.newtestapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class MySpinnerAdapter extends BaseAdapter {

    private ArrayList<String> items;

    public MySpinnerAdapter() {
         items= new ArrayList<>();
        items.add("Order by result");
        items.add("Order by barcode");
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        TextView v= (TextView) layoutInflater.inflate(R.layout.spinner_item,parent,false);
        v.setText(items.get(position));
        return v;
    }
}
