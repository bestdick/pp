package com.storyvendingmachine.www.pp;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.storyvendingmachine.www.pp.Allurl.url_getExamList;
import static com.storyvendingmachine.www.pp.ExamViewActivity.answer;
import static com.storyvendingmachine.www.pp.ExamViewActivity.eviewPager;
import static com.storyvendingmachine.www.pp.ExamViewActivity.navi_selection;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;

/**
 * Created by symoo on 2018-02-13.
 */

public class ExamViewFragment extends Fragment {

    int page;
    Bundle bundle;
    String[] note;



    public static ExamViewFragment newInstance(int count, Bundle bundle, String[] note) {
        ExamViewFragment fragment = new ExamViewFragment();
        Bundle args = new Bundle();
        args.putInt("page", count);
        args.putBundle("bundle", bundle);
        args.putStringArray("note", note);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
         page = getArguments().getInt("page");
         bundle =getArguments().getBundle("bundle");
         note = getArguments().getStringArray("note");

         answer.add(page, -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exam_view, container, false);

        final String exam_code = bundle.getString("exam_code");
        final String exam_name =bundle.getString("exam_name");
        final String published_year = bundle.getString("published_year");
        final String published_round = bundle.getString("published_round");
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
            question_textView.setText("[ "+(page+1)+ " ] " + question_question[0]);
            question_example_textView.setText(question_question[1]);
        }else{
            String question_question = bundle.getString("question_question");
            question_textView.setText("[ "+(page+1)+ " ] " + question_question);
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

            answer_1_textView.setText("①."+question_answer[0]);
            answer_2_textView.setText("②."+question_answer[1]);
            answer_3_textView.setText("③."+question_answer[2]);
            answer_4_textView.setText("④."+question_answer[3]);

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
                        Intent intent =new Intent(getActivity().getApplicationContext(), ExamNoteWriteActivity.class);
                        intent.putExtra("type", "note_write");
                        intent.putExtra("exam_code", exam_code);
                        intent.putExtra("exam_name", exam_name);
                        intent.putExtra("exam_placed_round", published_round);
                        intent.putExtra("exam_placed_year", published_year);
                        intent.putExtra("note_number", String.valueOf(page));
                        startActivity(intent);
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
                        Intent intent =new Intent(getActivity().getApplicationContext(), ExamNoteWriteActivity.class);
                        intent.putExtra("type", "note_write");
                        intent.putExtra("exam_code", exam_code);
                        intent.putExtra("exam_name", exam_name);
                        intent.putExtra("exam_placed_round", published_round);
                        intent.putExtra("exam_placed_year", published_year);
                        intent.putExtra("note_number", String.valueOf(page));
                        startActivity(intent);
                    }

                }
            });

            note_refresh_button(rootView, exam_code, published_year, published_round);
        }
    }

    public void note_refresh_button(final View rootView, final String exam_code, final String exam_placed_year, final String exam_placed_round){
        TextView refresh_button = (TextView) rootView.findViewById(R.id.refresh_button);
        final ProgressBar pb = (ProgressBar) rootView.findViewById(R.id.note_progressbar);
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

                            for(int i = 0 ; i < jsonArray.length(); i++){

                                String note = jsonArray.getJSONObject(i).getString("note");
                                String author = jsonArray.getJSONObject(i).getString("author");
                                if(note.equals("null") || note.length() <=0){

                                }else {
                                    View exam_view_exam_note = getLayoutInflater().inflate(R.layout.examview_exam_note_container, null);
                                    TextView author_text_view = (TextView) exam_view_exam_note.findViewById(R.id.note_author_textView);
                                    TextView note_text_view = (TextView) exam_view_exam_note.findViewById(R.id.each_note_textView);

                                    author_text_view.setText(author);
                                    note_text_view.setText(note);

                                    ll.addView(exam_view_exam_note);
                                }
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


                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void ExamNote(View rootView){
        ArrayList<String> list = new ArrayList<>();
        LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.comment_layout);


        for(int i = 0 ; i<note.length; i++){
            if(note[i].equals("null")){

            }else{
                list.add(note[i]);

                String author = note[i].split("/////")[0];
                String note_note = note[i].split("/////")[1];

                View exam_view_exam_note = getLayoutInflater().inflate(R.layout.examview_exam_note_container, null);
                TextView author_text_view = (TextView) exam_view_exam_note.findViewById(R.id.note_author_textView);
                TextView note_text_view = (TextView) exam_view_exam_note.findViewById(R.id.each_note_textView);

                author_text_view.setText(author);
                note_text_view.setText(note_note);

                ll.addView(exam_view_exam_note);
            }

        }
        if(list.size() == 0){
            View exam_view_exam_note_empty = getLayoutInflater().inflate(R.layout.examview_exam_note_empty_container, null);
            ll.addView(exam_view_exam_note_empty);
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

}
