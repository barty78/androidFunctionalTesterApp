package com.pietrantuono.activities.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SerialConsoleFragment.newInstance();
            case 1:
                return SequenceFragment.newInstance();
            case 2:
                return DevicesListFragment.newInstance();

            default:
                return SequenceFragment.newInstance();
        }
    }


    @Override
    public int getCount() {
        return 3;
    }
}
