package com.pietrantuono.fragments.devices;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

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
    public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        final Switch aSwitch = (Switch) MenuItemCompat.getActionView(menu.findItem(R.id.currentjobonly));
        aSwitch.setText("Current job only");
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    callback.currentJobOnly(true);
                    aSwitch.setText("Current job only");
                    mode.invalidate();
                }
                else {
                    callback.currentJobOnly(false);
                    aSwitch.setText("All jobs");
                    mode.invalidate();
                }
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
        if (item.getItemId() == R.id.click) {
            showPopup(item);
            return true;
        }
        return false;
    }

    private void showPopup(MenuItem item) {
        PopupMenu popup = new PopupMenu(context, ((Activity)context).findViewById(item.getItemId()));
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.sort_by_barcode:
                        callback.sortByBarcode();
                        return true;
                    case R.id.sort_by_result:
                        callback.sortByResult();
                        return true;
                }
                return true;
            }
        });
        popup.show();
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
