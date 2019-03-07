package com.storyvendingmachine.www.pp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by Administrator on 2019-02-20.
 */

public class LawExamSubClassViewPagerAdapter extends FragmentStatePagerAdapter {

    public LawExamSubClassViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.e("item pointer", Integer.toString(position));
        if (position == 0) {
            return LawExamSubClassFragment.newInstance("major_1001", null);
        } else if(position == 1){
            return LawExamSubClassFragment.newInstance("major_1002", null);
        }else{
            return LawExamSubClassFragment.newInstance("major_1003", null);
        }
    }
    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

}
