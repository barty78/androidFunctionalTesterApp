package com.pietrantuono.activities.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by mauriziopietrantuono on 07/01/16.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        switch (position){
            case 0:
                 fragment = (Fragment)SequenceFragment.newInstance();
                break;
            case 1:
                fragment = (Fragment)SequenceFragment.newInstance();
                break;
            default:
                fragment = (Fragment)SequenceFragment.newInstance();
                break;
        }
        return (Fragment)SequenceFragment.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "foo";
    }

    @Override
    public int getCount() {
        return 1;
    }
}
