package com.pietrantuono.activities.fragments;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.pietrantuono.pericoach.newtestapp.R;


/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class ActionModecallback implements ActionMode.Callback {
    private Context context;
    private Callback callback;

    public ActionModecallback(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner=(Spinner) item.getActionView();
        ArrayAdapter<CharSequence> adapter =  new ArrayAdapter<CharSequence>(context,android.R.layout.simple_spinner_item, new String[]{"Sort by result","Sort by barcode"});//ArrayAdapter.createFromResource(this,R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner.setAdapter(adapter);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }


    interface Callback {

    }

}
