package com.storyvendingmachine.www.pp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class ExamViewActivityViewPagerAdaper extends FragmentStatePagerAdapter {
    int count;
    JSONArray jsonArray;
    String naviSelection;
    JSONArray examNoteJSONArray;
    public ExamViewActivityViewPagerAdaper(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(naviSelection.equals("1")){
            String[] note = null;
            Bundle bundle = currentQuestion(position);

            return ExamViewFragment.newInstance(position, bundle, note);
        }else{// navi selection "2" 일때이다
            String[] note = getJSONNote(position);
            Bundle bundle = currentQuestion(position);
            return ExamViewFragment.newInstance(position, bundle, note);
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return count;
    }


    public String[] getJSONNote(int position){

        String[] string_array = new String[examNoteJSONArray.length()];
//        Log.e("from adapter", String.valueOf(examNoteJSONArray.length()));


        for(int i = 0 ; i<examNoteJSONArray.length(); i++){
            try {
                String string = examNoteJSONArray.getJSONObject(i).getJSONArray("note").get(position).toString();// note
                String author = examNoteJSONArray.getJSONObject(i).getString("author"); //author;
//                JSONArray temp = examNoteJSONArray.getJSONArray(i);
//                String string = temp.getString(position);
//                Log.e("from adapter", string);
                if(string.equals("null") || string.trim().length() <= 0){
                    string_array[i] = "null";
                }else{
                    string_array[i] = author+"/////"+string;
                }
            } catch (JSONException e) {
//                e.printStackTrace();
                string_array[i] = "null";
//                Log.e("from adapter", "short on string");
            }
        }
        return string_array;
    }

    public Bundle currentQuestion(int position){
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            String exam_code = jsonObject.getString("exam_code");
            String exam_name = jsonObject.getString("exam_name");
            String published_year = jsonObject.getString("published_year");
            String published_round = jsonObject.getString("published_round");
            String subject_code = jsonObject.getString("subject_code");
            String subject_name = jsonObject.getString("subject_name");
            String question_number = jsonObject.getString("question_number");
            String question_question = jsonObject.getString("question_question");
            String question_answer = jsonObject.getString("question_answer");
            String correct_answer = jsonObject.getString("correct_answer");
            String question_Q_image = jsonObject.getString("question_Q_image");
            String question_A_image = jsonObject.getString("question_A_image");
            String example_exist = jsonObject.getString("example_exist");

//            String note = temp_json.getString(position);


            Bundle bundle = new Bundle();
            bundle.putString("exam_code", exam_code);
            bundle.putString("exam_name", exam_name);
            bundle.putString("published_year", published_year);
            bundle.putString("published_round", published_round);
            bundle.putString("subject_code", subject_code);
            bundle.putString("subject_name", subject_name);
            bundle.putString("question_number", question_number);
            bundle.putString("question_question", question_question);
            bundle.putString("question_answer", question_answer);
            bundle.putString("correct_answer", correct_answer);
            bundle.putString("question_Q_image", question_Q_image);
            bundle.putString("question_A_image", question_A_image);
            bundle.putString("example_exist", example_exist);

//            bundle.putString("note", note);
            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}