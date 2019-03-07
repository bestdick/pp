package com.storyvendingmachine.www.pp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by Administrator on 2019-02-21.
 */

public class LawStudySubClassViewPagerAdapter extends FragmentStatePagerAdapter {

    public LawStudySubClassViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.e("item pointer", Integer.toString(position));
        return LawStudySubClassFragment.newInstance(null, null);
    }
    @Override
    public int getCount() {
        // Show 3 total pages.
        return 1;
    }

}
