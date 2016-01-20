package com.pietrantuono.activities.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pietrantuono.activities.fragments.sequence.NewSequenceFragment;
import com.pietrantuono.activities.fragments.sequence.SequenceFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DevicesListFragment.newInstance();
            case 1:
                return NewSequenceFragment.newInstance();
            case 2:
                return SerialConsoleFragment.newInstance();
            default:
                return NewSequenceFragment.newInstance();
        }
    }


    @Override
    public int getCount() {
        return 3;
    }
}
