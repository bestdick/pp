package com.storyvendingmachine.www.pp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class FlashCardViewActivityViewPagerAdapter extends FragmentStatePagerAdapter {
    int count;
    ArrayList<String> flashcard_array;
    String exam_name;
    String subject_name;
    boolean solo_page;
    String flashcard_or_folder;
    public FlashCardViewActivityViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        int length_of_flashcard = flashcard_array.size();
        String temr_and_def = flashcard_array.get(position);
            return FlashCardViewFragment.newInstance(position, temr_and_def, exam_name, subject_name, solo_page, flashcard_or_folder, length_of_flashcard);

    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return count;
    }



}