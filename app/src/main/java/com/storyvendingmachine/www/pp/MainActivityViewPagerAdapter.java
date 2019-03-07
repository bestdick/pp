package com.storyvendingmachine.www.pp;

import android.support.design.widget.TabItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class MainActivityViewPagerAdapter extends FragmentStatePagerAdapter{
    String type;

    public MainActivityViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(type.equals("lawyer")){
            if(position==0){
                return LawHomeFragment.newInstance("null","null");
            }else if(position==1){
                return LawExamFragment.newInstance("one","two");
            }else{
                return LawStudyFragment.newInstance("null","null");
            }
        }else{
            if (position == 0)
                return MainFragment.newInstance();
            else if(position == 1)
                return TestFragment.newInstance();
            else if(position ==2){
                return FlashcardFragment.newInstance();
            }else  {
                return StatisticFragment.newInstance(null, null);
            }
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        if(type.equals("lawyer")){
            return 3;
        }else{
            return 4;
        }
    }



}