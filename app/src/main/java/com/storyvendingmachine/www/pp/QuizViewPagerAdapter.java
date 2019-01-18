package com.storyvendingmachine.www.pp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.storyvendingmachine.www.pp.MainFragment.quizUserSelectedAnswers;

public class QuizViewPagerAdapter extends FragmentStatePagerAdapter {
    String from;
    int count;
    JSONArray jsonArray;
    String today;
    public QuizViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {

        if(position==(count-1)){
//            Bundle bundle = jsonArrayToBundle();
            return QuizTodaySubmitResultFragment.newInstance(jsonArray.toString(), today);
        }else{
            quizUserSelectedAnswers.add(position, -1);
            Bundle bundle = temp(position);
            return QuizTodayDialogFragment.newInstance(bundle, String.valueOf(position));
        }


    }
    @Override
    public int getCount() {
        // Show 3 total pages.
        return count;
    }

    public Bundle jsonArrayToBundle(){
        Bundle bundle = new Bundle();
        try{
            for(int i = 0 ; i<jsonArray.length(); i++){
                Bundle inner_bundle = new Bundle();
                JSONObject object = jsonArray.getJSONObject(i);
                String primary_key = object.getString("primary_key");
                String exam_code = object.getString("exam_code");
                String exam_name = object.getString("exam_name");
                String exam_placed_year = object.getString("exam_placed_year");
                String exam_placed_round = object.getString("exam_placed_round");
                String subject_code = object.getString("subject_code");
                String subject_name = object.getString("subject_name");
                String question_number = object.getString("question_number");
                String question_question = object.getString("question_question");
                String question_answer = object.getString("question_answer");
                String correct_answer = object.getString("correct_answer");
                String question_image_exist = object.getString("question_image_exist");
                String answer_image_exist = object.getString("answer_image_exist");
                String example_exist = object.getString("example_exist");


                inner_bundle.putString("primary_key", primary_key);
                inner_bundle.putString("exam_code", exam_code);
                inner_bundle.putString("exam_name", exam_name);
                inner_bundle.putString("exam_placed_year", exam_placed_year);
                inner_bundle.putString("exam_placed_round", exam_placed_round);
                inner_bundle.putString("subject_code", subject_code);
                inner_bundle.putString("subject_name", subject_name);
                inner_bundle.putString("question_number", question_number);
                inner_bundle.putString("question_question", question_question);
                inner_bundle.putString("question_answer", question_answer);
                inner_bundle.putString("correct_answer", correct_answer);
                inner_bundle.putString("question_image_exist", question_image_exist);
                inner_bundle.putString("answer_image_exist", answer_image_exist);
                inner_bundle.putString("example_exist", example_exist);
                bundle.putBundle(String.valueOf(i), inner_bundle);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bundle;
    }


    public Bundle temp(int j){
        Bundle inner_bundle = new Bundle();
            try {
                JSONObject object = jsonArray.getJSONObject(j);
                String primary_key = object.getString("primary_key");
                String exam_code = object.getString("exam_code");
                String exam_name = object.getString("exam_name");
                String exam_placed_year = object.getString("exam_placed_year");
                String exam_placed_round = object.getString("exam_placed_round");
                String subject_code = object.getString("subject_code");
                String subject_name = object.getString("subject_name");
                String question_number = object.getString("question_number");
                String question_question = object.getString("question_question");
                String question_answer = object.getString("question_answer");
                String correct_answer = object.getString("correct_answer");
                String question_image_exist = object.getString("question_image_exist");
                String answer_image_exist = object.getString("answer_image_exist");
                String example_exist = object.getString("example_exist");


                inner_bundle.putString("primary_key", primary_key);
                inner_bundle.putString("exam_code", exam_code);
                inner_bundle.putString("exam_name", exam_name);
                inner_bundle.putString("exam_placed_year", exam_placed_year);
                inner_bundle.putString("exam_placed_round", exam_placed_round);
                inner_bundle.putString("subject_code", subject_code);
                inner_bundle.putString("subject_name", subject_name);
                inner_bundle.putString("question_number", question_number);
                inner_bundle.putString("question_question", question_question);
                inner_bundle.putString("question_answer", question_answer);
                inner_bundle.putString("correct_answer", correct_answer);
                inner_bundle.putString("question_image_exist", question_image_exist);
                inner_bundle.putString("answer_image_exist", answer_image_exist);
                inner_bundle.putString("example_exist", example_exist);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        return inner_bundle;
    }

}
