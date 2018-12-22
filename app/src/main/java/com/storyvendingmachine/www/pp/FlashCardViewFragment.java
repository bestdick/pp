package com.storyvendingmachine.www.pp;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.pp.ExamViewActivity.answer;
import static com.storyvendingmachine.www.pp.ExamViewActivity.navi_selection;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;

/**
 * Created by symoo on 2018-02-13.
 */

public class FlashCardViewFragment extends Fragment {

    int page;
    String term_and_def, flashcard_exam_name, flashcard_subject_name, flashcard_or_folder;
    boolean solo_page;



    public static FlashCardViewFragment newInstance(int count, String term_and_def, String exam_name, String subject_name, boolean solo_page, String flashcard_or_folder) {
        FlashCardViewFragment fragment = new FlashCardViewFragment();
        Bundle args = new Bundle();
        args.putInt("page", count);
        args.putString("term_and_def", term_and_def);
        args.putString("flashcard_exam_name", exam_name);
        args.putString("flashcard_subject_name", subject_name);
        args.putBoolean("solo_page", solo_page);
        args.putString("flashcard_or_folder", flashcard_or_folder);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
         page = getArguments().getInt("page");
         term_and_def =getArguments().getString("term_and_def");
         flashcard_exam_name = getArguments().getString("flashcard_exam_name");
         flashcard_subject_name = getArguments().getString("flashcard_subject_name");
         solo_page = getArguments().getBoolean("solo_page");
         flashcard_or_folder = getArguments().getString("flashcard_or_folder");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(page%2 == 0){
            //cover flashcard
            View rootView = inflater.inflate(R.layout.fragment_flashcard_view, container, false);

            ConstraintLayout outer_container_layout = (ConstraintLayout) rootView.findViewById(R.id.outer_container_layout);
            TextView term_and_def_textView = (TextView) rootView.findViewById(R.id.term_and_def_textView);
            TextView page_textView = (TextView) rootView.findViewById(R.id.flashcard_page_textView);
            TextView exam_name_textView = (TextView) rootView.findViewById(R.id.exam_name_textView);

            page_textView.setText(String.valueOf((page/2)+1));
            term_and_def_textView.setText(term_and_def);
            exam_name_textView.setText(flashcard_exam_name + " "+ flashcard_subject_name);

            onLongPress(outer_container_layout);
            return rootView;
        }else{
            //back flashcard
            View rootView = inflater.inflate(R.layout.fragment_flashcard_view_odd, container, false);

            ConstraintLayout outer_container_layout = (ConstraintLayout) rootView.findViewById(R.id.outer_container_layout);
            TextView term_and_def_textView = (TextView) rootView.findViewById(R.id.term_and_def_textView);
            term_and_def_textView.setText(term_and_def);

            onLongPress(outer_container_layout);
            return rootView;
        }

    }

    public void onLongPress(ConstraintLayout constraintLayout){

        if(flashcard_or_folder.equals("flashcard")){
            if(solo_page == true){
                constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Toast.makeText(getActivity().getApplicationContext(), "롱 프레스", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext().getApplicationContext(), FlashcardSoloViewActivity.class);
                        intent.putExtra("solo_page", solo_page);
                        intent.putExtra("flashcard_or_folder", flashcard_or_folder);
                        startActivity(intent);
                        return false;
                    }
                });
            }
        }else{
            if(solo_page == true){
                constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Toast.makeText(getActivity().getApplicationContext(), "롱 프레스", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext().getApplicationContext(), FlashcardSoloViewActivity.class);
                        intent.putExtra("solo_page", solo_page);
                        intent.putExtra("flashcard_or_folder", flashcard_or_folder);
                        startActivity(intent);
                        return false;
                    }
                });
            }
        }

    }


    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
//        testYearOrderList.clear();
//        getExamList(navi_selection);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//         getActivity().getMenuInflater().inflate(R.menu.exam_answer_sheet, menu);
         super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


}
