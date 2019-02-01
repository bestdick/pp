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


    public MainActivityViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

       if (position == 0)
           return MainFragment.newInstance();
       else if(position == 1)
           return TestFragment.newInstance();
       else if(position ==2){
           return FlashcardFragment.newInstance();
       }else  {
           return StatisticFragment.newInstance(null, null);
       }
//       }else {
//           return FlashcardFragment.newInstance();
//       }

    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 4;
    }



}