package com.pietrantuono.fragments;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.pietrantuono.pericoach.newtestapp.R;


/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class ActionModecallback implements ActionMode.Callback {
    private final String TAG = getClass().getSimpleName();
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
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(menu.findItem(R.id.spinner));
        ArrayAdapter<CharSequence> adapter =  new ArrayAdapter<CharSequence>(context,android.R.layout.simple_spinner_item, new String[]{"SELECT SORTING ORDER","Sort by result","Sort by barcode"});//ArrayAdapter.createFromResource(this,R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Selected " + position);
                if(position==1)callback.sortByResult();
                if(position==2)callback.sortByBarcode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Switch aSwitch= (Switch) MenuItemCompat.getActionView(menu.findItem(R.id.currentjobonly));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)callback.currentJobOnly(true);
                else callback.currentJobOnly(false);
            }
        });

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


    public interface Callback {

        void sortByResult();

        void sortByBarcode();

        void currentJobOnly(boolean onlycurrent);
    }

}
