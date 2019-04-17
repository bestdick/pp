package com.storyvendingmachine.www.pp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.storyvendingmachine.www.pp.ExamViewActivity.note_array;

public class LawExamViewTypeAViewPagerAdapter extends FragmentStatePagerAdapter {
    int count;
    JSONArray jsonArray;
    String navi_selection;
    JSONObject userPersonalNotes;
    JSONArray allNotes;


    public LawExamViewTypeAViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if(navi_selection.equals("1")){
            Bundle bundle = EachQuestionToBundle(position);
            return LawExamViewTypeAFragment.newInstance(bundle, position, navi_selection, null, null);
        }else{
            // navi_selection.equals("2");;
            //ArrayList<Bundle> notelist = Note(position);
            Bundle bundle = EachQuestionToBundle(position);
//            return LawExamViewTypeAFragment.newInstance(bundle, position, navi_selection, Note(position));
            return LawExamViewTypeAFragment.newInstance(bundle, position, navi_selection, all_note(position), personal_note(position));
        }
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


    public Bundle personal_note(int position){
        try {
            String author_id = userPersonalNotes.getString("user_id");
            String author_nickname = userPersonalNotes.getString("user_nickname");
            String user_thumbnail = userPersonalNotes.getString("user_thumbnail");
            String primary_key = userPersonalNotes.getString("primary_key");
            String exam_placed_year = userPersonalNotes.getString("exam_placed_year");
            String major_type = userPersonalNotes.getString("major_type");
            String minor_type = userPersonalNotes.getString("minor_type");

            JSONArray note = userPersonalNotes.getJSONArray("note");
            for(int i = 0 ; i < note.length(); i++){
                int question_number = Integer.parseInt(note.getJSONObject(i).getString("number"));
                if(question_number == position){
                    String this_note = note.getJSONObject(i).getString("note");
                    String isPublic = note.getJSONObject(i).getString("isPublic");
                    String upload_date = note.getJSONObject(i).getString("upload_date");
                    String upload_time = note.getJSONObject(i).getString("upload_time");


                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isExist", true);
                    bundle.putString("author_id", author_id);
                    bundle.putString("author_nickname", author_nickname);
                    bundle.putString("user_thumbnail", user_thumbnail);
                    bundle.putString("note", this_note);
                    bundle.putString("isPublic", isPublic);
                    bundle.putString("upload_date", upload_date);
                    bundle.putString("upload_time", upload_time);
                    return bundle;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isExist", false);
            return bundle;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("isExist", false);
        return bundle;
    }

    public ArrayList<Bundle> all_note(int position){

        for(int i = 0 ; i < allNotes.length(); i++){
            try {
                int question_number = Integer.parseInt(allNotes.getJSONObject(i).getString("question_number"));
                if(position == question_number){
                    ArrayList<Bundle> notes = new ArrayList<Bundle>();

                    JSONArray thisPositionNotes = allNotes.getJSONObject(i).getJSONArray("note_with_info");
                    for(int j = 0 ; j < thisPositionNotes.length(); j++){
                        Bundle bundle = new Bundle();
                        String author_login_type = thisPositionNotes.getJSONObject(j).getString("login_type");
                        String author_user_id = thisPositionNotes.getJSONObject(j).getString("user_id");
                        String author_user_nickname = thisPositionNotes.getJSONObject(j).getString("user_nickname");
                        String author_user_thumbnail = thisPositionNotes.getJSONObject(j).getString("user_thumbnail");
                        String primary_key = thisPositionNotes.getJSONObject(j).getString("primary_key");
                        String exam_placed_year = thisPositionNotes.getJSONObject(j).getString("exam_placed_year");
                        String major_type = thisPositionNotes.getJSONObject(j).getString("major_type");
                        String minor_type = thisPositionNotes.getJSONObject(j).getString("minor_type");
                        String note = thisPositionNotes.getJSONObject(j).getString("note");
                        String isPublic = thisPositionNotes.getJSONObject(j).getString("isPublic");
                        String upload_date = thisPositionNotes.getJSONObject(j).getString("upload_date");
                        String upload_time = thisPositionNotes.getJSONObject(j).getString("upload_time");


                        bundle.putString("login_type" , author_login_type);
                        bundle.putString("user_id", author_user_id);
                        bundle.putString("primary_key", primary_key);
                        bundle.putString("user_nickname", author_user_nickname);
                        bundle.putString("user_thumbnail", author_user_thumbnail);

                        bundle.putString("exam_placed_year", exam_placed_year);
                        bundle.putString("major_type", major_type);
                        bundle.putString("minor_type", minor_type);
                        bundle.putString("note", note);
                        bundle.putString("isPublic", isPublic);
                        bundle.putString("upload_date", upload_date);
                        bundle.putString("upload_time", upload_time);
                        notes.add(bundle);
                    }
                    return notes;
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }

        }
        return null;
    }
//    public ArrayList<Bundle> Note(int position){
//        // this note function is only used when navi_selection == 2;
//        int note_length = notejsonArray.length();
//        ArrayList<Bundle> notelist = new ArrayList<Bundle>();
//        try {
//            for(int i = 0; i < note_length; i++){
//                String isExist = notejsonArray.getJSONObject(i).getString("existance");
//                if(i == 0){
//                    if(isExist.equals("exist")){
//                        // current login user had wrote note on this selected year exam NOTICE: NOT THIS NUMBER!!! TOTAL NOTE!
//                        String user_nickname = notejsonArray.getJSONObject(i).getString("user_nickname");
//                        String user_thumbnail = notejsonArray.getJSONObject(i).getString("user_thumbnail");
//                        JSONArray tempJSONArray = notejsonArray.getJSONObject(i).getJSONArray("note");
//                        String note = note_json(tempJSONArray, position);
//
//                        Bundle bundle = new Bundle();
//                        bundle.putString("isExist", "true");
//                        bundle.putString("user_nickname", user_nickname);
//                        bundle.putString("user_thumbnail", user_thumbnail);
//                        bundle.putString("note", note);
//                        notelist.add(bundle);
////                        note_array[position] = note;
//                    }else if(isExist.equals("not_exist")){
//                        // 없을떄
//                        Bundle bundle = new Bundle();
//                        bundle.putString("isExist", "false");
//                        notelist.add(bundle);
////                        note_array[position] = "null";
//                    }else{
//                        //if isExist == guest
//                        Bundle bundle = new Bundle();
//                        bundle.putString("isExist", "guest");
//                        notelist.add(bundle);
//                    }
//                }else{
//                    String login_type = notejsonArray.getJSONObject(i).getString("login_type");
//                    String author_id = notejsonArray.getJSONObject(i).getString("user_id");
//                    String author_nickname = notejsonArray.getJSONObject(i).getString("user_nickname");
//                    String author_thumbnail = notejsonArray.getJSONObject(i).getString("user_thumbnail");
//                    String primary_key = notejsonArray.getJSONObject(i).getString("user_thumbnail");
//                    JSONArray tempJSONArray = notejsonArray.getJSONObject(i).getJSONArray("note");
//                    String exam_placed_year = notejsonArray.getJSONObject(i).getString("exam_placed_year");
//                    String major_exam_type = notejsonArray.getJSONObject(i).getString("major_exam_type");
//                    String major_type = notejsonArray.getJSONObject(i).getString("major_type");
//                    String minor_type = notejsonArray.getJSONObject(i).getString("minor_type");
//                    String isPublic = notejsonArray.getJSONObject(i).getString("isPublic");
//                    String upload_date = notejsonArray.getJSONObject(i).getString("upload_date");
//                    String upload_time = notejsonArray.getJSONObject(i).getString("upload_time");
//
//                    String note = note_json(tempJSONArray, position);
//
//                    Bundle bundle = new Bundle();
//                    bundle.putString("isExist", "true");
//                    bundle.putString("login_type" , login_type);
//                    bundle.putString("user_id", author_id);
//                    bundle.putString("primary_key", primary_key);
//                    bundle.putString("user_nickname", author_nickname);
//                    bundle.putString("user_thumbnail", author_thumbnail);
//
//                    bundle.putString("exam_placed_year", exam_placed_year);
//                    bundle.putString("major_type", major_type);
//                    bundle.putString("minor_type", minor_type);
//
//                    bundle.putString("note", note);
//                    bundle.putString("isPublic", isPublic);
//                    bundle.putString("upload_date", upload_date);
//                    bundle.putString("upload_time", upload_time);
//                    notelist.add(bundle);
//                }
//            }
//            return notelist;
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return notelist;
//        }
//    }
    public String note_json(JSONArray notes, int position){
        try {
            if(notes.length() > position){
                String note = notes.getString(position);
                //Log.e("note length", String.valueOf(position)+"//"+String.valueOf(notes.length()) + "// "+ note);
                return note;
            }else{
                return "note_not_exist";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "note_not_exist";
        }
    }
}