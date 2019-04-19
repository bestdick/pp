package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.storyvendingmachine.www.pp.ExamViewActivity.answer;
import static com.storyvendingmachine.www.pp.ExamViewActivity.ExamView_progressBar;
import static com.storyvendingmachine.www.pp.ExamViewActivity.navi_selection;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_nickname;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;


public class LawExamViewTypeAFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    LinearLayout comment_layout;

    boolean isPersonalNoteExist;
    String personal_note_show;
    ProgressBar note_progressbar;


    final int NOTE_REQUEST_CODE = 40001;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    // TODO: Rename and change types of parameters
    private Bundle mParam1;
    private int mParam2;
    private String mParam3; // navi_selection
    private  ArrayList<Bundle>  mParam4;
    private Bundle mParam5;

    public static LawExamViewTypeAFragment newInstance(Bundle param1, int param2, String param3, ArrayList<Bundle> param4, Bundle param5) {
//        mParam4 = param4;
        LawExamViewTypeAFragment fragment = new LawExamViewTypeAFragment();
        Bundle args = new Bundle();
        args.putBundle(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putParcelableArrayList(ARG_PARAM4, param4);
        args.putBundle(ARG_PARAM5, param5);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getBundle(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getParcelableArrayList(ARG_PARAM4);
            mParam5 = getArguments().getBundle(ARG_PARAM5);
            if(mParam3.equals("1")){
                answer.add(mParam2, -1);
            }
        }


//        personal_note_show=null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_exam_view_type_a, container, false);
        if(mParam2 == mParam1.size()){
         progressbar_invisible();
        }
        intialize_elements(rootview);

        return rootview;
    }

    public void intialize_elements(View rootview){

        TextView question_textView = (TextView) rootview.findViewById(R.id.question_textView);
        TextView example_1_textView = (TextView) rootview.findViewById(R.id.example_1_textView);
        TextView example_2_textView = (TextView) rootview.findViewById(R.id.example_2_textView);

        ConstraintLayout answer_1_conLayout = (ConstraintLayout) rootview.findViewById(R.id.answer_1_conLayout);
        ConstraintLayout answer_2_conLayout = (ConstraintLayout) rootview.findViewById(R.id.answer_2_conLayout);
        ConstraintLayout answer_3_conLayout = (ConstraintLayout) rootview.findViewById(R.id.answer_3_conLayout);
        ConstraintLayout answer_4_conLayout = (ConstraintLayout) rootview.findViewById(R.id.answer_4_conLayout);
        ConstraintLayout answer_5_conLayout = (ConstraintLayout) rootview.findViewById(R.id.answer_5_conLayout);

        TextView answer_number_1 = (TextView) rootview.findViewById(R.id.answer_number_1);
        TextView answer_number_2 = (TextView) rootview.findViewById(R.id.answer_number_2);
        TextView answer_number_3 = (TextView) rootview.findViewById(R.id.answer_number_3);
        TextView answer_number_4 = (TextView) rootview.findViewById(R.id.answer_number_4);
        TextView answer_number_5 = (TextView) rootview.findViewById(R.id.answer_number_5);

        TextView answer_1_textView = (TextView) rootview.findViewById(R.id.answer_1_textView);
        TextView answer_2_textView = (TextView) rootview.findViewById(R.id.answer_2_textView);
        TextView answer_3_textView = (TextView) rootview.findViewById(R.id.answer_3_textView);
        TextView answer_4_textView = (TextView) rootview.findViewById(R.id.answer_4_textView);
        TextView answer_5_textView = (TextView) rootview.findViewById(R.id.answer_5_textView);

        String question_number = mParam1.getString("question_number");
        String question_context = mParam1.getString("question_context");
        String question_example_1_exist =  mParam1.getString("question_example_1_exist");
        String question_example_1_context = mParam1.getString("question_example_1_context");
        String question_example_2_exist =  mParam1.getString("question_example_2_exist");
        String question_example_2_context = mParam1.getString("question_example_2_context");
        String answer_context = mParam1.getString("answer_context");
        String correct_answer = mParam1.getString("correct_answer");


        make_question_and_example( question_textView,  example_1_textView,  example_2_textView,
                 question_context,  question_example_1_exist,  question_example_1_context,
                question_example_2_exist,  question_example_2_context);
        make_answer_choice( answer_context,  answer_1_textView,  answer_2_textView,  answer_3_textView,
                 answer_4_textView,  answer_5_textView);
        if(navi_selection.equals("1")){
            //시험 응시
            select_answer_choice( answer_1_conLayout,  answer_2_conLayout,  answer_3_conLayout,  answer_4_conLayout,  answer_5_conLayout,
                    answer_number_1,answer_number_2,answer_number_3,answer_number_4,answer_number_5,
                    answer_1_textView,answer_2_textView,answer_3_textView,answer_4_textView,answer_5_textView);
        }else{
            //시험 공부 with notes
            select_answer_previously( answer_1_conLayout,  answer_2_conLayout,  answer_3_conLayout,  answer_4_conLayout,  answer_5_conLayout,
                    answer_number_1,answer_number_2,answer_number_3,answer_number_4,answer_number_5,
                    answer_1_textView,answer_2_textView,answer_3_textView,answer_4_textView,answer_5_textView, correct_answer);

            ConstraintLayout exam_note_container = (ConstraintLayout) rootview.findViewById(R.id.exam_note_container);
                TextView refresh_button = (TextView) rootview.findViewById(R.id.refresh_button);
                TextView note_add_revise_button = (TextView) rootview.findViewById(R.id.note_add_revise_button);
                note_progressbar = (ProgressBar) rootview.findViewById(R.id.note_progressbar);
            comment_layout = (LinearLayout) rootview.findViewById(R.id.comment_layout);

            exam_note_container.setVisibility(View.VISIBLE);
            comment_layout.setVisibility(View.VISIBLE);

     //       String note = mParam5.getString("note");
      //      personal_note_show = Html.fromHtml(note).toString().replace("<br>", "\n");

            personal_note();
            all_notes();
            note_controller(refresh_button, note_add_revise_button);
        }
    }

    public void make_question_and_example(TextView question_textView, TextView example_1_textView, TextView example_2_textView,
                                          String question_context, String question_example_1_exist, String question_example_1_context,
                                          String question_example_2_exist, String question_example_2_context){
        if(question_example_1_exist.equals("true") && question_example_2_exist.equals("true")){
//            Spanned html_question = Html.fromHtml(question_context);
//            Spanned html_example_1 = Html.fromHtml(question_example_1_context);
//            Spanned html_example_2 = Html.fromHtml(question_example_2_context);
            String str_question = "[ "+(mParam2+1) + " ] "+question_context.replace("<br>","\n");
            String str_example_1 = question_example_1_context.replace("<br>","\n");
            String str_example_2 = question_example_2_context.replace("<br>","\n");

            question_textView.setText(str_question);
            example_1_textView.setText(str_example_1);
            example_2_textView.setText(str_example_2);

        }else if(question_example_1_exist.equals("true") && question_example_2_exist.equals("false")){
//            Spanned html_question = Html.fromHtml(question_context);
//            Spanned html_example_1 = Html.fromHtml(question_example_1_context);
            String str_question = "[ "+(mParam2+1)+" ] "+question_context.replace("<br>","\n");
            String str_example_1 = question_example_1_context.replace("<br>","\n");

            question_textView.setText(str_question);
            example_1_textView.setText(str_example_1);
            example_2_textView.setVisibility(View.GONE);
        }else {
//            question_example_1_exist.equals("false") || question_example_2_exist.equals("false")
//            Spanned html_question = Html.fromHtml(question_context);
            String str_question = "[ "+(mParam2+1)+ " ] "+question_context.replace("<br>","\n");

            question_textView.setText(str_question);
            example_1_textView.setVisibility(View.GONE);
            example_2_textView.setVisibility(View.GONE);
        }
    }
    public void make_answer_choice(String answer_context, TextView answer_1_textView, TextView answer_2_textView, TextView answer_3_textView,
                                   TextView answer_4_textView, TextView answer_5_textView){
        String[] answer_array = answer_context.split("##");
        String answer_1 = answer_array[0];
        String answer_2 = answer_array[1];
        String answer_3 = answer_array[2];
        String answer_4 = answer_array[3];
        String answer_5 = answer_array[4];

        answer_1_textView.setText(answer_1);
        answer_2_textView.setText(answer_2);
        answer_3_textView.setText(answer_3);
        answer_4_textView.setText(answer_4);
        answer_5_textView.setText(answer_5);

    }

    public void select_answer_choice(ConstraintLayout a_1_c, ConstraintLayout a_2_c, ConstraintLayout a_3_c, ConstraintLayout a_4_c, ConstraintLayout a_5_c,
                                     final TextView a_n_1, final TextView a_n_2, final TextView a_n_3, final TextView a_n_4, final TextView a_n_5,
                                     TextView a_c_1, TextView a_c_2, TextView a_c_3, TextView a_c_4, TextView a_c_5){
        a_1_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer.set(mParam2, 1);
                a_n_1.setTextColor(getResources().getColor(R.color.colorWhite));
                a_n_1.setBackground(getResources().getDrawable(R.drawable.answer_selected_container_law));
                a_n_2.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_2.setBackground(null);
                a_n_3.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_3.setBackground(null);
                a_n_4.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_4.setBackground(null);
                a_n_5.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_5.setBackground(null);
            }
        });
        a_2_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer.set(mParam2, 2);
                a_n_2.setTextColor(getResources().getColor(R.color.colorWhite));
                a_n_2.setBackground(getResources().getDrawable(R.drawable.answer_selected_container_law));
                a_n_1.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_1.setBackground(null);
                a_n_3.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_3.setBackground(null);
                a_n_4.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_4.setBackground(null);
                a_n_5.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_5.setBackground(null);
            }
        });
        a_3_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer.set(mParam2, 3);
                a_n_3.setTextColor(getResources().getColor(R.color.colorWhite));
                a_n_3.setBackground(getResources().getDrawable(R.drawable.answer_selected_container_law));
                a_n_2.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_2.setBackground(null);
                a_n_1.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_1.setBackground(null);
                a_n_4.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_4.setBackground(null);
                a_n_5.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_5.setBackground(null);
            }
        });
        a_4_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer.set(mParam2, 4);
                a_n_4.setTextColor(getResources().getColor(R.color.colorWhite));
                a_n_4.setBackground(getResources().getDrawable(R.drawable.answer_selected_container_law));
                a_n_2.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_2.setBackground(null);
                a_n_3.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_3.setBackground(null);
                a_n_1.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_1.setBackground(null);
                a_n_5.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_5.setBackground(null);
            }
        });
        a_5_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer.set(mParam2, 5);
                a_n_5.setTextColor(getResources().getColor(R.color.colorWhite));
                a_n_5.setBackground(getResources().getDrawable(R.drawable.answer_selected_container_law));
                a_n_2.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_2.setBackground(null);
                a_n_3.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_3.setBackground(null);
                a_n_4.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_4.setBackground(null);
                a_n_1.setTextColor(getResources().getColor(R.color.colorBlack));
                a_n_1.setBackground(null);
            }
        });
    }


    // LAW 시험 공부 with NOTE -------------------------
    public void select_answer_previously(ConstraintLayout a_1_c, ConstraintLayout a_2_c, ConstraintLayout a_3_c, ConstraintLayout a_4_c, ConstraintLayout a_5_c,
                                         final TextView a_n_1, final TextView a_n_2, final TextView a_n_3, final TextView a_n_4, final TextView a_n_5,
                                         TextView a_c_1, TextView a_c_2, TextView a_c_3, TextView a_c_4, TextView a_c_5, String correct_answer){
        switch (correct_answer){
            case "1":
                a_n_1.setTextColor(getResources().getColor(R.color.colorWhite));
                a_n_1.setBackground(getResources().getDrawable(R.drawable.answer_selected_container_law));
                break;
            case "2":
                a_n_2.setTextColor(getResources().getColor(R.color.colorWhite));
                a_n_2.setBackground(getResources().getDrawable(R.drawable.answer_selected_container_law));
                break;
            case "3":
                a_n_3.setTextColor(getResources().getColor(R.color.colorWhite));
                a_n_3.setBackground(getResources().getDrawable(R.drawable.answer_selected_container_law));
                break;
            case "4":
                a_n_4.setTextColor(getResources().getColor(R.color.colorWhite));
                a_n_4.setBackground(getResources().getDrawable(R.drawable.answer_selected_container_law));
                break;
            default:
                a_n_5.setTextColor(getResources().getColor(R.color.colorWhite));
                a_n_5.setBackground(getResources().getDrawable(R.drawable.answer_selected_container_law));
                break;
        }
    }


    public void note_controller(TextView refresh_button, TextView note_add_revise_button){
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                note_progressbar.setVisibility(View.VISIBLE);
                noteRefresh(  mParam1.getString("exam_placed_year"),    mParam1.getString("major_type"),   mParam1.getString("minor_type"),   String.valueOf(mParam2));
            }
        });

        note_add_revise_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginType.equals("null")){
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
                }else{
                        Intent intent =new Intent(getActivity(), ExamNoteWriteActivity.class);
                        intent.putExtra("type", "note_write");
                        intent.putExtra("exam_major_type", "lawyer");
                        intent.putExtra("exam_placed_year", mParam1.getString("exam_placed_year"));
                        intent.putExtra("major_type", mParam1.getString("major_type"));
                        intent.putExtra("minor_type", mParam1.getString("minor_type"));
                        intent.putExtra("isNoteExist", isPersonalNoteExist);
                        intent.putExtra("note", personal_note_show);
                        intent.putExtra("note_number", String.valueOf(mParam2));
                        startActivityForResult(intent, NOTE_REQUEST_CODE);
                        slide_left_and_slide_in();
                }
            }
        });
    }

    public void all_notes( ){
        if(mParam4==null){
            // 노트가 없을떄
            View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
            TextView empty_textView = (TextView) exam_view_exam_note_empty.findViewById(R.id.empty_textView);
            empty_textView.setText("공개된 노트가 없습니다");
            comment_layout.addView(exam_view_exam_note_empty);
        }else{
            // 노트가 존재할떄
            for(int i = 0 ; i < mParam4.size(); i++){
                String author_id = mParam4.get(i).getString("user_id");
                String author_nickname = mParam4.get(i).getString("user_nickname");
                String author_thumbnail = mParam4.get(i).getString("user_thumbnail");
                String exam_placed_year = mParam4.get(i).getString("exam_placed_year");
                String major_type = mParam4.get(i).getString("major_type");
                String minor_type = mParam4.get(i).getString("minor_type");
                String note = mParam4.get(i).getString("note");
                String note_show = Html.fromHtml(note).toString().replace("<br>", "\n");
                String upload_date = mParam4.get(i).getString("upload_date");
                String upload_time = mParam4.get(i).getString("upload_time");

                View exam_view_exam_note = getLayoutInflater().inflate(R.layout.container_flashcard_comment, null);
                ImageView author_thumbnail_imageView = (ImageView) exam_view_exam_note.findViewById(R.id.author_imageView);
                TextView upload_textView = (TextView) exam_view_exam_note.findViewById(R.id.upload_textView);
                TextView author_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_author_textView);
                TextView note_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_textView);

                author_text_view.setText(author_nickname);
                upload_textView.setText(upload_date);
                getThumbnailImageForAuthor(author_thumbnail_imageView, author_thumbnail);
                note_text_view.setText(note_show);
                comment_layout.addView(exam_view_exam_note);
            }
        }
    }
    public void personal_note( ){
        if(LoginType.equals("null") || G_user_id.equals("null")){
            //guest
            View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
            TextView empty_textView = (TextView) exam_view_exam_note_empty.findViewById(R.id.empty_textView);
            empty_textView.setText("로그인 하시면 노트를 작성할 수 있습니다.");
            comment_layout.addView(exam_view_exam_note_empty);
        }else{
            isPersonalNoteExist = mParam5.getBoolean("isExist");
            if(isPersonalNoteExist){
                View exam_view_exam_note = getLayoutInflater().inflate(R.layout.container_flashcard_comment, null);
                ImageView author_thumbnail_imageView = (ImageView) exam_view_exam_note.findViewById(R.id.author_imageView);
                TextView upload_textView = (TextView) exam_view_exam_note.findViewById(R.id.upload_textView);
                TextView author_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_author_textView);
                TextView note_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_textView);
                TextView isPublic_textView = (TextView) exam_view_exam_note.findViewById(R.id.isPublic_textView);

                String author_id = mParam5.getString("author_id");
                String author_nickname = mParam5.getString("author_nickname");
                String author_thumbnail = mParam5.getString("user_thumbnail");
                String note = mParam5.getString("note");

                String isPublic = mParam5.getString("isPublic");
                String upload_date = mParam5.getString("upload_date");
                String upload_time = mParam5.getString("upload_time");

                if(isPublic.equals("public")){
                    //공개
                    isPublic_textView.setText("공개");
                }else{
                    //비공개
                    isPublic_textView.setText("비공개");
                }
                personal_note_show  = Html.fromHtml(note).toString().replace("<br>", "\n");
                note_text_view.setText(personal_note_show);
                author_text_view.setText(author_nickname);
                upload_textView.setText(upload_date);
                getThumbnailImageForAuthor(author_thumbnail_imageView, author_thumbnail);

                comment_layout.addView(exam_view_exam_note);
            }else{
                View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
                TextView empty_textView = (TextView) exam_view_exam_note_empty.findViewById(R.id.empty_textView);
                empty_textView.setText(G_user_nickname+" 님 노트를 작성해주세요!");
                comment_layout.addView(exam_view_exam_note_empty);
            }
        }

    }

    public void noteRefresh(final String exam_placed_year, final String major_type, final String minor_type, final String question_number){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://www.joonandhoon.com/pp/passpop_law/android/server/getExamList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("exam note refresh::" , response);
                        try {
                            comment_layout.removeAllViews();

                            JSONObject jsonObject = new JSONObject(response);
                            if(LoginType.equals("null") || G_user_id.equals("null")){
                                //guest
                                View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
                                TextView empty_textView = (TextView) exam_view_exam_note_empty.findViewById(R.id.empty_textView);
                                empty_textView.setText("로그인 하시면 노트를 작성할 수 있습니다.");
                                comment_layout.addView(exam_view_exam_note_empty);
                            }else{
                                JSONObject response1  = jsonObject.getJSONObject("response1");
                                isPersonalNoteExist = response1.getBoolean("isExist");
                                if(isPersonalNoteExist){
                                    View exam_view_exam_note = getLayoutInflater().inflate(R.layout.container_flashcard_comment, null);
                                    ImageView author_thumbnail_imageView = (ImageView) exam_view_exam_note.findViewById(R.id.author_imageView);
                                    TextView upload_textView = (TextView) exam_view_exam_note.findViewById(R.id.upload_textView);
                                    TextView author_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_author_textView);
                                    TextView note_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_textView);
                                    TextView isPublic_textView = (TextView) exam_view_exam_note.findViewById(R.id.isPublic_textView);

                                    String author_id = response1.getString("user_id");
                                    String author_nickname = response1.getString("user_nickname");
                                    String author_thumbnail = response1.getString("user_thumbnail");
                                    String note = response1.getString("note");

                                    String isPublic = response1.getString("isPublic");
                                    String upload_date = response1.getString("upload_date");
                                    String upload_time = response1.getString("upload_time");

                                    if(isPublic.equals("public")){
                                        //공개
                                        isPublic_textView.setText("공개");
                                    }else{
                                        //비공개
                                        isPublic_textView.setText("비공개");
                                    }
                                    personal_note_show  = Html.fromHtml(note).toString().replace("<br>", "\n");
                                    note_text_view.setText(personal_note_show);
                                    author_text_view.setText(author_nickname);
                                    upload_textView.setText(upload_date);
                                    getThumbnailImageForAuthor(author_thumbnail_imageView, author_thumbnail);

                                    comment_layout.addView(exam_view_exam_note);
                                }else{
                                    View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
                                    TextView empty_textView = (TextView) exam_view_exam_note_empty.findViewById(R.id.empty_textView);
                                    empty_textView.setText(G_user_nickname+" 님 노트를 작성해주세요!");
                                    comment_layout.addView(exam_view_exam_note_empty);
                                }
                            }
                            JSONArray response2  = jsonObject.getJSONArray("response2");
                            if(response2.length() == 0){
                                // 노트가 없을떄
                                View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
                                TextView empty_textView = (TextView) exam_view_exam_note_empty.findViewById(R.id.empty_textView);
                                empty_textView.setText("공개된 노트가 없습니다");
                                comment_layout.addView(exam_view_exam_note_empty);
                            }else{
                                for(int i = 0 ; i < response2.length(); i++){
                                    String author_id = response2.getJSONObject(i).getString("user_id");
                                    String author_nickname = response2.getJSONObject(i).getString("user_nickname");
                                    String author_thumbnail = response2.getJSONObject(i).getString("user_thumbnail");
                                    String note = response2.getJSONObject(i).getString("note");
                                    String note_show = Html.fromHtml(note).toString().replace("<br>", "\n");
                                    String upload_date = response2.getJSONObject(i).getString("upload_date");
                                    String upload_time = response2.getJSONObject(i).getString("upload_time");

                                    View exam_view_exam_note = getLayoutInflater().inflate(R.layout.container_flashcard_comment, null);
                                    ImageView author_thumbnail_imageView = (ImageView) exam_view_exam_note.findViewById(R.id.author_imageView);
                                    TextView upload_textView = (TextView) exam_view_exam_note.findViewById(R.id.upload_textView);
                                    TextView author_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_author_textView);
                                    TextView note_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_textView);

                                    author_text_view.setText(author_nickname);
                                    upload_textView.setText(upload_date);
                                    getThumbnailImageForAuthor(author_thumbnail_imageView, author_thumbnail);
                                    note_text_view.setText(note_show);
                                    comment_layout.addView(exam_view_exam_note);
                                }
                            }
                            note_progressbar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("volley error", error.toString());
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
                params.put("type", "note_refresh");
                params.put("exam_placed_year", exam_placed_year);
                params.put("major_type", major_type);
                params.put("minor_type", minor_type);
                params.put("question_number", question_number);

                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void study_note(LinearLayout comment_layout){
        //Log.e("notes", mParam4.get(mParam2).getString("isExist"));
        int total_length = mParam4.size();
        if(total_length == 0){
            View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
            TextView empty_textView = (TextView) exam_view_exam_note_empty.findViewById(R.id.empty_textView);
            empty_textView.setText(G_user_nickname+" 님 노트를 작성해주세요!");
            comment_layout.addView(exam_view_exam_note_empty);
        }else{
            for(int i = 0 ; i<total_length; i++){
                String isExist = mParam4.get(i).getString("isExist");
                if(i == 0){
                    if(isExist.equals("true")){
                        String note = mParam4.get(i).getString("note");
                        if(note.equals("null")){
                            View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
                            TextView empty_textView = (TextView) exam_view_exam_note_empty.findViewById(R.id.empty_textView);
                            empty_textView.setText(G_user_nickname+" 님 노트를 작성해주세요!");
                            comment_layout.addView(exam_view_exam_note_empty);
                        }else{
                            View exam_view_exam_note = getLayoutInflater().inflate(R.layout.container_flashcard_comment, null);
                            ImageView author_thumbnail_imageView = (ImageView) exam_view_exam_note.findViewById(R.id.author_imageView);
                            TextView upload_textView = (TextView) exam_view_exam_note.findViewById(R.id.upload_textView);
                            TextView author_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_author_textView);
                            TextView note_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_textView);

                            String author = mParam4.get(i).getString("user_nickname");
                            String author_thumbnail = mParam4.get(i).getString("user_thumbnail");
                            String upload_date =  mParam4.get(i).getString("upload_date");

                            author_text_view.setText(author);
                            note_text_view.setText(note);
                            upload_textView.setText(upload_date);
                            getThumbnailImageForAuthor(author_thumbnail_imageView, author_thumbnail);

                            comment_layout.addView(exam_view_exam_note);
                        }
                    }else if(isExist.equals("false")){
                        View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
                        TextView empty_textView = (TextView) exam_view_exam_note_empty.findViewById(R.id.empty_textView);
                        empty_textView.setText(G_user_nickname+" 님 노트를 작성해주세요!");
                        comment_layout.addView(exam_view_exam_note_empty);
                    }else{
                        // guest
                        View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
                        TextView empty_textView = (TextView) exam_view_exam_note_empty.findViewById(R.id.empty_textView);
                        empty_textView.setText("로그인 하시면 노트를 작성할 수 있습니다.");
                        comment_layout.addView(exam_view_exam_note_empty);
                    }
                }else{
                    String note = mParam4.get(i).getString("note");
                    if(note.equals("null")){

                    }else{
                        View exam_view_exam_note = getLayoutInflater().inflate(R.layout.container_flashcard_comment, null);
                        ImageView author_thumbnail_imageView = (ImageView) exam_view_exam_note.findViewById(R.id.author_imageView);
                        TextView upload_textView = (TextView) exam_view_exam_note.findViewById(R.id.upload_textView);
                        TextView author_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_author_textView);
                        TextView note_text_view = (TextView) exam_view_exam_note.findViewById(R.id.comment_textView);

                        String author = mParam4.get(i).getString("user_nickname");
                        String author_thumbnail = mParam4.get(i).getString("user_thumbnail");
                        String upload_date =  mParam4.get(i).getString("upload_date");

                        author_text_view.setText(author);
                        note_text_view.setText(note);
                        upload_textView.setText(upload_date);
                        getThumbnailImageForAuthor(author_thumbnail_imageView, author_thumbnail);

                        comment_layout.addView(exam_view_exam_note);
                    }
                }
            }
        }
    }

    public void progressbar_visible(){
        ExamView_progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void progressbar_invisible(){
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        ExamView_progressBar.setVisibility(View.GONE);
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
    public void slide_left_and_slide_in(){//opening new activity
        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOTE_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                String upload_status = data.getStringExtra("upload_status");
                if(upload_status.equals("success")){
                    Log.e("why not working?", "what the fuck");
                    String exam_placed_year = data.getStringExtra("exam_placed_year");
                    String major_type = data.getStringExtra("major_type");
                    String minor_type = data.getStringExtra("minor_type");
                    String question_number = data.getStringExtra("note_number");

                    Log.e("page :", question_number);
                    noteRefresh(  exam_placed_year,   major_type,   minor_type,   question_number);
                }
            }else if(resultCode == RESULT_CANCELED){

            }
        }else{
        }

    }
}
