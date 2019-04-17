package com.storyvendingmachine.www.pp;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.storyvendingmachine.www.pp.Allurl.url_getExamList;
import static com.storyvendingmachine.www.pp.ExamViewActivity.answer;
import static com.storyvendingmachine.www.pp.ExamViewActivity.eviewPager;
import static com.storyvendingmachine.www.pp.ExamViewActivity.navi_selection;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_nickname;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;

/**
 * Created by symoo on 2018-02-13.
 */

public class ExamViewFragment extends Fragment implements Html.ImageGetter{
    ProgressBar pb;
    View rootView;
    final int NOTE_REQUEST_CODE = 40001;
    int page;
    Bundle bundle;
    String[] note;


    String exam_code, exam_name, published_year, published_round;

    TextView test_textView;

    public static ExamViewFragment newInstance(int count, Bundle bundle, String[] note) {
        ExamViewFragment fragment = new ExamViewFragment();
        Bundle args = new Bundle();
        args.putInt("page", count);
        args.putBundle("bundle", bundle);

        args.putStringArray("note", note);// working on it

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
         page = getArguments().getInt("page");
         bundle =getArguments().getBundle("bundle");
         note = getArguments().getStringArray("note");// working on it
         answer.add(page, -1);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_exam_view, container, false);

        pb = (ProgressBar) rootView.findViewById(R.id.note_progressbar);
        exam_code = bundle.getString("exam_code");
        exam_name =bundle.getString("exam_name");
        published_year = bundle.getString("published_year");
        published_round = bundle.getString("published_round");
        String subject_code = bundle.getString("subject_code");
        String subject_name = bundle.getString("subject_name");
        String question_number = bundle.getString("question_number");

        String[] question_answer = bundle.getString("question_answer").split("##");
        String correct_answer = bundle.getString("correct_answer");
        String question_Q_image = bundle.getString("question_Q_image");
        String question_A_image = bundle.getString("question_A_image");
        String example_exist = bundle.getString("example_exist");
//        String note = bundle.getString("note");

        TextView exam_name_textView = (TextView) rootView.findViewById(R.id.exam_name_textView);
        TextView subject_textView = (TextView) rootView.findViewById(R.id.subject_textView);
        TextView question_textView = (TextView) rootView.findViewById(R.id.question_textView);
        TextView question_example_textView = (TextView) rootView.findViewById(R.id.question_example_textView);
        ImageView question_imageView = (ImageView) rootView.findViewById(R.id.question_imageView);
//        이미지 처리
        TextView answer_1_textView = (TextView) rootView.findViewById(R.id.answer_1_textView);
        TextView answer_2_textView =(TextView) rootView.findViewById(R.id.answer_2_textView);
        TextView answer_3_textView =(TextView) rootView.findViewById(R.id.answer_3_textView);
        TextView answer_4_textView =(TextView) rootView.findViewById(R.id.answer_4_textView);

        ConstraintLayout answer_1_conLayout = (ConstraintLayout) rootView.findViewById(R.id.answer_1_conLayout);
        ImageView answer_1_imageView = (ImageView) rootView.findViewById(R.id.answer_1_imageView);
        answer_1_imageView.setTag(1);
        ConstraintLayout answer_2_conLayout = (ConstraintLayout) rootView.findViewById(R.id.answer_2_conLayout);
        ImageView answer_2_imageView = (ImageView) rootView.findViewById(R.id.answer_2_imageView);
        answer_2_imageView.setTag(2);
        ConstraintLayout answer_3_conLayout = (ConstraintLayout) rootView.findViewById(R.id.answer_3_conLayout);
        ImageView answer_3_imageView = (ImageView) rootView.findViewById(R.id.answer_3_imageView);
        answer_3_imageView.setTag(3);
        ConstraintLayout answer_4_conLayout = (ConstraintLayout) rootView.findViewById(R.id.answer_4_conLayout);
        ImageView answer_4_imageView = (ImageView) rootView.findViewById(R.id.answer_4_imageView);
        answer_4_imageView.setTag(4);
//        이미지 처리

//        TextView note_textview= (TextView) rootView.findViewById(R.id.note);


        if(example_exist.equals("true")){
            String[] question_question = bundle.getString("question_question").split("##");
            String question_make = "[ "+(page+1)+ " ] " + question_question[0];
            Spanned question = Html.fromHtml(question_make, this, null);
            Spanned example = Html.fromHtml(question_question[1], this, null);


            test_textView = (TextView)  rootView.findViewById(R.id.question_textView);
//            test_textView.setText("[ "+(page+1)+ " ] " + question);
            test_textView.setText(question);
//            question_textView.setText("[ "+(page+1)+ " ] " + question);

//            test_textView = (TextView) rootView.findViewById(R.id.question_example_textView);
//            test_textView.setText(example);

            question_example_textView.setText(example);
//            question_example_textView.setText(question_question[1]);
        }else{
            String question_question = bundle.getString("question_question");
            String question_new = "[ "+(page+1)+ " ] " + question_question;
            Spanned question = Html.fromHtml(question_new, this, null);


            test_textView = (TextView)  rootView.findViewById(R.id.question_textView);
            test_textView.setText(question);

//            question_textView.setText(question);
            question_example_textView.setVisibility(View.GONE);
        }

        if(question_Q_image.equals("true")){
            String url = "http://www.joonandhoon.com/pp/PassPop/exam_images/"+exam_code+"/"+exam_code+"_"+published_year+"_"+published_round+"_"+subject_code+"_q_"+question_number+".PNG";
            getQuestionImage(question_imageView, url);
        }else{
            question_imageView.setVisibility(View.GONE);
        }

        if(question_A_image.equals("true")){
            answer_1_conLayout.setVisibility(View.VISIBLE);
            answer_2_conLayout.setVisibility(View.VISIBLE);
            answer_3_conLayout.setVisibility(View.VISIBLE);
            answer_4_conLayout.setVisibility(View.VISIBLE);
            for(int i = 1; i<=4; i++){
                String url = "http://www.joonandhoon.com/pp/PassPop/exam_images/"+exam_code+"/"+exam_code+"_"+published_year+"_"+published_round+"_"+subject_code+"_q_"+question_number+"_a_"+i+".PNG";
                ImageView imageView = (ImageView) rootView.findViewWithTag(i);
                getAnswerImage(imageView, url);
            }
            yes_image_navi_selection_function(rootView, answer_1_conLayout, answer_2_conLayout, answer_3_conLayout, answer_4_conLayout,
                    correct_answer, exam_code, exam_name, published_round, published_year);
        }else{
            answer_1_textView.setVisibility(View.VISIBLE);
            answer_2_textView.setVisibility(View.VISIBLE);
            answer_3_textView.setVisibility(View.VISIBLE);
            answer_4_textView.setVisibility(View.VISIBLE);

            String temp_one = "① "+question_answer[0];
            Spanned one = Html.fromHtml(temp_one);
            String temp_two = "② "+question_answer[1];
            Spanned two = Html.fromHtml(temp_two);
            String temp_three= "③ "+question_answer[2];
            Spanned three = Html.fromHtml(temp_three);
            String temp_four= "④ "+question_answer[3];
            Spanned four = Html.fromHtml(temp_four);

            answer_1_textView.setText(one);
            answer_2_textView.setText(two);
            answer_3_textView.setText(three);
            answer_4_textView.setText(four);

            no_image_navi_selection_function(rootView, answer_1_textView, answer_2_textView, answer_3_textView, answer_4_textView,
                    correct_answer, exam_code, exam_name, published_round, published_year);
        }

        exam_name_textView.setText(exam_name);
        subject_textView.setText(subject_name);

        return rootView;
    }

    public void yes_image_navi_selection_function(View rootView, ConstraintLayout answer_1_conLayout, ConstraintLayout answer_2_conLayout,ConstraintLayout answer_3_conLayout,ConstraintLayout answer_4_conLayout,
                                              String correct_answer, final String exam_code, final String exam_name, final String published_round, final String published_year){
        if(navi_selection.equals("1")){
            imageAnswerChoice(answer_1_conLayout, answer_2_conLayout, answer_3_conLayout, answer_4_conLayout);
            ConstraintLayout  ad_banner_layout = (ConstraintLayout) rootView.findViewById(R.id.ad_banner_layout);
            ad_banner_layout.setVisibility(View.INVISIBLE);
            LinearLayout commentlayout = (LinearLayout) rootView.findViewById(R.id.comment_layout);
            commentlayout.setVisibility(View.INVISIBLE);
            ConstraintLayout note_menu_layout = (ConstraintLayout) rootView.findViewById(R.id.note_menu_layout);
            note_menu_layout.setVisibility(View.INVISIBLE);
            ConstraintLayout fragment_exam_view = (ConstraintLayout) rootView.findViewById(R.id.fragment_exam_view);
            fragment_exam_view.setBackgroundColor(getResources().getColor(R.color.colorWhiteSmoke));
        }else{
            // navi selection == 2
            imageCorrectAnwerChoice(correct_answer, answer_1_conLayout, answer_2_conLayout, answer_3_conLayout, answer_4_conLayout);
            ExamNote(rootView);// 노트를 가져오는 function
//            ExamNoteRevise(rootView);
            TextView note_add_revise_button = (TextView) rootView.findViewById(R.id.note_add_revise_button);
            //로그인한상태와 안한상태를 말해줌ㅕ
            note_add_revise_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(LoginType.equals("null")){
                        //로그인하지 않았다면....
                        String message = "로그인을 하셔야 사용 할 수 있는 기능입니다.";
                        String positive_message = "확인";
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(message)
                                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .create()
                                .show();

                    }else {
                        //로그인 한 상태이라면....
                        Intent intent =new Intent(getActivity(), ExamNoteWriteActivity.class);
                        intent.putExtra("type", "note_write");
                        intent.putExtra("exam_major_type", "sugs_1001/gs_2001");
                        intent.putExtra("exam_code", exam_code);
                        intent.putExtra("exam_name", exam_name);
                        intent.putExtra("exam_placed_round", published_round);
                        intent.putExtra("exam_placed_year", published_year);
                        intent.putExtra("note_number", String.valueOf(page));
//                        startActivity(intent);
                        startActivityForResult(intent, NOTE_REQUEST_CODE);
                        slide_left_and_slide_in();
                    }

                }
            });

            note_refresh_button(rootView, exam_code, published_year, published_round);
        }
    }
    public void no_image_navi_selection_function(View rootView, TextView answer_1_textView, TextView answer_2_textView, TextView answer_3_textView, TextView answer_4_textView,
                                        String correct_answer, final String exam_code, final String exam_name, final String published_round, final String published_year){
        if(navi_selection.equals("1")){
            answerChoice(rootView, answer_1_textView, answer_2_textView, answer_3_textView, answer_4_textView);
            ConstraintLayout  ad_banner_layout = (ConstraintLayout) rootView.findViewById(R.id.ad_banner_layout);
            ad_banner_layout.setVisibility(View.INVISIBLE);
            LinearLayout commentlayout = (LinearLayout) rootView.findViewById(R.id.comment_layout);
            commentlayout.setVisibility(View.INVISIBLE);
            ConstraintLayout note_menu_layout = (ConstraintLayout) rootView.findViewById(R.id.note_menu_layout);
            note_menu_layout.setVisibility(View.INVISIBLE);
            ConstraintLayout fragment_exam_view = (ConstraintLayout) rootView.findViewById(R.id.fragment_exam_view);
            fragment_exam_view.setBackgroundColor(getResources().getColor(R.color.colorWhiteSmoke));
        }else{
            // navi selection == 2
            correctAnswerChoice(correct_answer, rootView, answer_1_textView, answer_2_textView, answer_3_textView, answer_4_textView);
            ExamNote(rootView);// 노트를 가져오는 function
//            ExamNoteRevise(rootView);
            TextView note_add_revise_button = (TextView) rootView.findViewById(R.id.note_add_revise_button);
            //로그인한상태와 안한상태를 말해줌ㅕ

            //로그인 한 상태이라면....
            note_add_revise_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(LoginType.equals("null")){
                        //로그인하지 않았다면....

                        String message = "로그인을 하셔야 사용 할 수 있는 기능입니다.";
                        String positive_message = "확인";
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(message)
                                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .create()
                                .show();

                    }else {
                        Intent intent =new Intent(getActivity(), ExamNoteWriteActivity.class);
                        intent.putExtra("type", "note_write");
                        intent.putExtra("exam_major_type", "sugs_1001/gs_2001");
                        intent.putExtra("exam_code", exam_code);
                        intent.putExtra("exam_name", exam_name);
                        intent.putExtra("exam_placed_round", published_round);
                        intent.putExtra("exam_placed_year", published_year);
                        intent.putExtra("note_number", String.valueOf(page));
//                        startActivity(intent);
                        startActivityForResult(intent, NOTE_REQUEST_CODE);
                        slide_left_and_slide_in();
                    }

                }
            });

            note_refresh_button(rootView, exam_code, published_year, published_round);
        }
    }



    public void imageCorrectAnwerChoice(String correct_answer, ConstraintLayout one, ConstraintLayout two, ConstraintLayout three, ConstraintLayout four){
        //this method is for when 기출 시험 공부에 사용된다
        if(correct_answer.equals("1")){
            one.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
        }else if(correct_answer.equals("2")){
            two.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
        }else if(correct_answer.equals("3")){
            three.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
        }else{
            //4번일때
            four.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
        }

    }
    public void correctAnswerChoice(String correct_answer, View rootview, TextView one, TextView two, TextView three, TextView four){
        //this method is for when 기출 시험 공부에 사용된다
        if(correct_answer.equals("1")){
            one.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
        }else if(correct_answer.equals("2")){
            two.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
        }else if(correct_answer.equals("3")){
            three.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
        }else{
            //4번일때
            four.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
        }

    }

    public void imageAnswerChoice(final ConstraintLayout one, final ConstraintLayout two, final ConstraintLayout three, final ConstraintLayout four){
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                answer.set(page, 1);
                eviewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(answer.get(page)));
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                answer.set(page, 2);
                eviewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(answer.get(page)));

            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                answer.set(page, 3);
                eviewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(answer.get(page)));
            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                answer.set(page, 4);
                eviewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(answer.get(page)));
            }
        });
    }
    public void answerChoice(View rootview, final TextView one, final TextView two, final TextView three, final TextView four){
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                answer.set(page, 1);
                eviewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(answer.get(page)));
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                answer.set(page, 2);
                eviewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(answer.get(page)));

            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                answer.set(page, 3);
                eviewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(answer.get(page)));
            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                answer.set(page, 4);
                eviewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(answer.get(page)));
            }
        });
    }

    //********************************************** all the method that are neccessary 기출 공부 a.k.a NOTE NOTE  under this line **********************************************
    public void global_note_refresh_process(final View rootView, final String exam_code, final String exam_placed_year, final String exam_placed_round, final ProgressBar pb){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/getNoteRefresh.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("exam note ::" , response);
                        try {
                            LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.comment_layout);
                            ll.removeAllViews();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("response");
                            ArrayList<String> list = new ArrayList<>();
                            for(int i = 0 ; i < jsonArray.length(); i++){
                                String note_note = jsonArray.getJSONObject(i).getString("note");
                                String author = jsonArray.getJSONObject(i).getString("author");
                                String upload_date = jsonArray.getJSONObject(i).getString("upload_date");
                                String upload_time = jsonArray.getJSONObject(i).getString("upload_time");
                                String author_thumbnail = jsonArray.getJSONObject(i).getString("user_thumbnail");
                                if(note_note.equals("null") || note_note.length() <=0){
                                    if(i==0){
                                        if(LoginType.equals("null") || G_user_id.equals("null")){
                                            View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
                                            TextView empty_textView = (TextView) exam_view_exam_note_empty.findViewById(R.id.empty_textView);
                                            empty_textView.setText("로그인 하시면 노트를 작성할 수 있습니다.");
                                            ll.addView(exam_view_exam_note_empty);
                                        }else{
                                            View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
                                            TextView empty_textView = (TextView) exam_view_exam_note_empty.findViewById(R.id.empty_textView);
                                            empty_textView.setText(G_user_nickname+" 님 노트를 작성해주세요!");
                                            ll.addView(exam_view_exam_note_empty);
                                        }
                                    }
                                }else {
                                    list.add(note[i]);
                                    View exam_view_exam_note = getLayoutInflater().inflate(R.layout.container_flashcard_comment, null);
                                    ImageView author_thumbnail_imageView = (ImageView) exam_view_exam_note.findViewById(R.id.author_imageView);
                                    TextView upload_textView = (TextView) exam_view_exam_note.findViewById(R.id.upload_textView);
                                    TextView author_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_author_textView);
                                    TextView note_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_textView);

                                    author_text_view.setText(author);
                                    note_text_view.setText(note_note);
                                    upload_textView.setText(upload_date+ "\n"+ upload_time);
                                    if(author_thumbnail.equals("null") || author_thumbnail==null || author_thumbnail.length()<=0){
                                        author_thumbnail_imageView.setBackground(getResources().getDrawable(R.drawable.thumbnail_outline));
                                        author_thumbnail_imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_empty_thumbnail));
                                    }else{
                                        getThumbnailImageForAuthor(author_thumbnail_imageView, author_thumbnail);
                                    }
                                    ll.addView(exam_view_exam_note);
                                }
                            }


                            if (list.size() == 0) {
                                View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
                                ll.addView(exam_view_exam_note_empty);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pb.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
//                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
//                        toast(message);
//                        getExamNameAndCode(); // 인터넷 에러가 났을시 다시 한번 시도한다.
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("exam_code", exam_code);
                params.put("exam_placed_year", exam_placed_year);
                params.put("exam_placed_round", exam_placed_round);
                params.put("question_number", String.valueOf(page));

                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void note_refresh_button(final View rootView, final String exam_code, final String exam_placed_year, final String exam_placed_round){
        TextView refresh_button = (TextView) rootView.findViewById(R.id.refresh_button);
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                    @Override
                    public void run() {
//                        pb.setVisibility(View.INVISIBLE);
                        global_note_refresh_process(rootView, exam_code, exam_placed_year, exam_placed_round, pb);

                    }
                }, 1500);

            }
        });
    }
    public void ExamNoteRevise(View rootView) {
        ArrayList<String> list = new ArrayList<>();
        LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.comment_layout);


        for (int i = 0; i < note.length; i++) {
                if (note[i].equals("null")) {
                    if(i==0){
                        View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
                        TextView empty_textView = exam_view_exam_note_empty.findViewById(R.id.empty_textView);
                        empty_textView.setText(G_user_nickname+" 님 노트를 작성해주세요!");
                        ll.addView(exam_view_exam_note_empty);
                    }
                } else {
                    list.add(note[i]);
                    String[] temp = note[i].split("/////");
                    String author = temp[0];
                    String author_thumbnail = temp[1];
                    String upload_date = temp[2];
                    String upload_time = temp[3];
                    String note_note = temp[4];
                    String author_id = temp[5];

//                View exam_view_exam_note = getLayoutInflater().inflate(R.layout.examview_exam_note_container, null);
//                TextView author_text_view = (TextView) exam_view_exam_note.findViewById(R.id.note_author_textView);
//                TextView note_text_view = (TextView) exam_view_exam_note.findViewById(R.id.each_note_textView);

                    View exam_view_exam_note = getLayoutInflater().inflate(R.layout.container_flashcard_comment, null);
                    ImageView author_thumbnail_imageView = (ImageView) exam_view_exam_note.findViewById(R.id.author_imageView);
                    TextView upload_textView = (TextView) exam_view_exam_note.findViewById(R.id.upload_textView);
                    TextView author_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_author_textView);
                    TextView note_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_textView);

                    author_text_view.setText(author);
                    note_text_view.setText(note_note);
                    upload_textView.setText(upload_date + "\n" + upload_time);
                    if (author_thumbnail.equals("null") || author_thumbnail == null || author_thumbnail.length() <= 0) {
                        author_thumbnail_imageView.setBackground(getResources().getDrawable(R.drawable.thumbnail_outline));
                        author_thumbnail_imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_empty_thumbnail));
                    } else {
                        getThumbnailImageForAuthor(author_thumbnail_imageView, author_thumbnail);
                    }
                    ll.addView(exam_view_exam_note);
                }

            }
            if (list.size() == 0) {
                View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
                ll.addView(exam_view_exam_note_empty);
            }
        }

    public void ExamNote(View rootView){
        ArrayList<String> list = new ArrayList<>();
        LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.comment_layout);

//        Log.e("length of note", String.valueOf(note.length));
        for(int i = 0 ; i<note.length; i++){
            if(note[i].equals("null")){
                if(i==0){
                    if(LoginType.equals("null") || G_user_id.equals("null")){
                        View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
                        TextView empty_textView = (TextView) exam_view_exam_note_empty.findViewById(R.id.empty_textView);
                        empty_textView.setText("로그인 하시면 노트를 작성할 수 있습니다.");
                        ll.addView(exam_view_exam_note_empty);
                    }else{
                        View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
                        TextView empty_textView = (TextView) exam_view_exam_note_empty.findViewById(R.id.empty_textView);
                        empty_textView.setText(G_user_nickname+" 님 노트를 작성해주세요!");
                        ll.addView(exam_view_exam_note_empty);
                    }
                }
            }else{
                list.add(note[i]);

                String[] temp = note[i].split("/////");
                String author =temp[0];
                String author_thumbnail = temp[1];
                String upload_date = temp[2];
                String upload_time = temp[3];
                String note_note = temp[4];

//                View exam_view_exam_note = getLayoutInflater().inflate(R.layout.examview_exam_note_container, null);
//                TextView author_text_view = (TextView) exam_view_exam_note.findViewById(R.id.note_author_textView);
//                TextView note_text_view = (TextView) exam_view_exam_note.findViewById(R.id.each_note_textView);

                View exam_view_exam_note = getLayoutInflater().inflate(R.layout.container_flashcard_comment, null);
                ImageView author_thumbnail_imageView = (ImageView) exam_view_exam_note.findViewById(R.id.author_imageView);
                TextView upload_textView = (TextView) exam_view_exam_note.findViewById(R.id.upload_textView);
                TextView author_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_author_textView);
                TextView note_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_textView);

                author_text_view.setText(author);
                note_text_view.setText(note_note);
                upload_textView.setText(upload_date+ "\n"+ upload_time);
                if(author_thumbnail.equals("null") || author_thumbnail==null || author_thumbnail.length()<=0){
                    author_thumbnail_imageView.setBackground(getResources().getDrawable(R.drawable.thumbnail_outline));
                    author_thumbnail_imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_empty_thumbnail));
                }else{
                    getThumbnailImageForAuthor(author_thumbnail_imageView, author_thumbnail);
                }
                ll.addView(exam_view_exam_note);
            }

        }
        if(list.size() == 0){
            View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
            ll.addView(exam_view_exam_note_empty);
        }
    }
    public void getThumbnailImageForAuthor(ImageView imageView, String url){
        Picasso.with(getActivity())
                .load(url)
                .transform(new CircleTransform())
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {
                        Log.e("load image", "fail to load images ");
                    }
                });
    }
    //********************************************** all the method that are neccessary for both 기출 // 기출 공부 under this line **********************************************
    public void getQuestionImage(ImageView imageView, String url){
        Picasso.with(getContext())
                .load(url)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {
                        Log.e("load image", "fail to load question images ");
                    }
                });
    }
    public void getAnswerImage(ImageView imageView, String url){
        Picasso.with(getContext())
                .load(url)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {
                        Log.e("load image", "fail to load answer images ");
                    }
                });
    }
    public void slide_left_and_slide_in(){//opening new activity
        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }

    //********************************************** all the override method goes under this line **********************************************
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("why not working?", "what the fuck");
        if (requestCode == NOTE_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                String upload_status = data.getStringExtra("upload_status");
                if(upload_status.equals("success")){
                    Log.e("why not working?", "what the fuck");
                    String page_number = data.getStringExtra("note_number");
                    global_note_refresh_process(rootView, exam_code, published_year, published_round, pb);
                }
            }else if(resultCode == RESULT_CANCELED){

            }
        }else{
        }

    }


    @Override
    public Drawable getDrawable(String source){
        LevelListDrawable d = new LevelListDrawable();
        Drawable empty = getResources().getDrawable(R.drawable.icon_empty_thumbnail);
        d.addLevel(0, 0 , empty);
        d.setBounds(0, 0 , empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

        new LoadImage().execute(source, d);

        return d;
    }

    class LoadImage extends AsyncTask<Object, Void, Bitmap> {
        private final static String TAG = "TestImageGetter";
        private LevelListDrawable mDrawable;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            Log.d(TAG, "doInBackground " + source);
            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(TAG, "onPostExecute drawable " + mDrawable);
            Log.d(TAG, "onPostExecute bitmap " + bitmap);
            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
                mDrawable.setBounds(0, 0,  bitmap.getWidth()*2, bitmap.getHeight()*2);
                mDrawable.setLevel(1);
                // i don't know yet a better way to refresh TextView
                // mTv.invalidate() doesn't work as expected
                CharSequence t = test_textView.getText();
                test_textView.setText(t);
            }
        }
    }
}
