package com.storyvendingmachine.www.pp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LawExamViewTypeAViewPagerAdapter extends FragmentStatePagerAdapter {
    int count;
    JSONArray jsonArray;
    String navi_selection;

    public LawExamViewTypeAViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = EachQuestionToBundle(position);
        return LawExamViewTypeAFragment.newInstance(bundle, position, navi_selection);
    }
    @Override
    public int getCount() {
        // Show 3 total pages.
        return count;
    }

    public Bundle EachQuestionToBundle(int position){
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            String primary_key = jsonObject.getString("primary_key");
            String exam_placed_year = jsonObject.getString("exam_placed_year");
            String book_type = jsonObject.getString("book_type");
            String major_type = jsonObject.getString("major_type");
            String major_type_kor = jsonObject.getString("major_type_kor");
            String minor_type = jsonObject.getString("minor_type");
            String minor_type_kor = jsonObject.getString("minor_type_kor");
            String question_number = jsonObject.getString("question_number");
            String question_context = jsonObject.getString("question_context");
            String question_example_1_exist = jsonObject.getString("question_example_1_exist");
            String question_example_1_context = jsonObject.getString("question_example_1_context");
            String question_example_2_exist = jsonObject.getString("question_example_2_exist");
            String question_example_2_context = jsonObject.getString("question_example_2_context");
            String answer_context = jsonObject.getString("answer_context");
            String correct_answer = jsonObject.getString("correct_answer");

//            String note = temp_json.getString(position);


            Bundle bundle = new Bundle();
            bundle.putString("primary_key", primary_key);
            bundle.putString("exam_placed_year", exam_placed_year);
            bundle.putString("book_type", book_type);
            bundle.putString("major_type", major_type);
            bundle.putString("major_type_kor", major_type_kor);
            bundle.putString("minor_type", minor_type);
            bundle.putString("minor_type_kor", minor_type_kor);
            bundle.putString("question_number", question_number);
            bundle.putString("question_context", question_context);
            bundle.putString("question_example_1_exist", question_example_1_exist);
            bundle.putString("question_example_1_context", question_example_1_context);
            bundle.putString("question_example_2_exist", question_example_2_exist);
            bundle.putString("question_example_2_context", question_example_2_context);
            bundle.putString("answer_context", answer_context);
            bundle.putString("correct_answer", correct_answer);

            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}