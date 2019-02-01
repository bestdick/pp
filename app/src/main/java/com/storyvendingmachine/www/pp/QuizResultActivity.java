package com.storyvendingmachine.www.pp;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_name;

public class QuizResultActivity extends AppCompatActivity {
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);
        toolbar();
        initializer();


    }

    public void initializer(){
        progressBar = (ProgressBar) findViewById(R.id.QuizResultProgressBar);
        progressbar_visible();
        Intent intent = getIntent();
        String isNew = intent.getStringExtra("isNew");
            if(isNew.equals("new")){
                // new
                String quiz_date = intent.getStringExtra("quiz_date");
                String currecnt_time = intent.getStringExtra("current_time");
                String temp_jsonArray_string = intent.getStringExtra("json_array");
                newQuizData(temp_jsonArray_string, quiz_date, currecnt_time);
                Log.e("new json_array", temp_jsonArray_string);
            }else{
                //old
                String key = intent.getStringExtra("key");
                getOldQuizData(key);
                Log.e("old json_array", key);
            }
    }

    // new quiz result retrieve
    public void newQuizData(String temp_jsonArray_str, String quiz_date, String current_time){
        LinearLayout result_container = (LinearLayout) findViewById(R.id.result_container);

        TextView quiz_title_textView = (TextView) findViewById(R.id.quiz_title_textView);
        TextView quiz_took_date_textView = (TextView) findViewById(R.id.quiz_took_date_textView);
        TextView exam_name_textView = (TextView) findViewById(R.id.exam_name_textView);
        TextView percent_textView = (TextView) findViewById(R.id.percent_textView);
        TextView fraction_textView = (TextView) findViewById(R.id.fraction_textView);
        ProgressBar percent_bar = (ProgressBar) findViewById(R.id.percent_bar);

        quiz_title_textView.setText(period_between_date(quiz_date)+" 퀴즈 결과");
        quiz_took_date_textView.setText(current_time);
        exam_name_textView.setText(exam_selection_name);

        int total_questions = 0;
        int total_correct = 0;
            try {
                JSONArray jsonArray = new JSONArray(temp_jsonArray_str);
                total_questions = jsonArray.length();
                for(int i = 0 ; i< jsonArray.length(); i++) {
                    String primary_key = jsonArray.getJSONObject(i).getString("primary_key");
                    String exam_code = jsonArray.getJSONObject(i).getString("exam_code");
                    String exam_name = jsonArray.getJSONObject(i).getString("exam_name");
                    String exam_placed_year = jsonArray.getJSONObject(i).getString("exam_placed_year");
                    String exam_placed_round = jsonArray.getJSONObject(i).getString("exam_placed_round");
                    String subject_code = jsonArray.getJSONObject(i).getString("subject_code");
                    String subject_name = jsonArray.getJSONObject(i).getString("subject_name");
                    String question_number = jsonArray.getJSONObject(i).getString("question_number");
                    String question_question = jsonArray.getJSONObject(i).getString("question_question");
                    String question_answer = jsonArray.getJSONObject(i).getString("question_answer");
                    String correct_answer = jsonArray.getJSONObject(i).getString("correct_answer");
                    String question_image_exist = jsonArray.getJSONObject(i).getString("question_image_exist");
                    String answer_image_exist = jsonArray.getJSONObject(i).getString("answer_image_exist");
                    String example_exist = jsonArray.getJSONObject(i).getString("example_exist");
                    String user_answer = jsonArray.getJSONObject(i).getString("user_answer");

                    int int_correct_answer = Integer.parseInt(correct_answer);
                    int int_user_answer = Integer.parseInt(user_answer);
                    if(int_correct_answer == int_user_answer){
                        total_correct++;
                    }

                    create_quiz_result_view( i,  primary_key, exam_code, exam_name, exam_placed_year,  exam_placed_round,  subject_code,  subject_name,  question_number,  question_question,  question_answer,  correct_answer,
                            question_image_exist,  answer_image_exist,  example_exist,  user_answer, result_container);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        String percent_str = decimal_two_digis(String.valueOf(total_correct), String.valueOf(total_questions));
        String fraction_str = total_correct+" / "+total_questions;
        percent_textView.setText(percent_str+"%");
        fraction_textView.setText(fraction_str);
        percent_bar.setProgress((int) Float.parseFloat(percent_str));
        progressbar_invisible();
    }
    // old quiz result retrieve
    public void getOldQuizData(final String key){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/getOldQuizData.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("selected quiz" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")){
                                LinearLayout result_container = (LinearLayout) findViewById(R.id.result_container);

                                JSONObject response_object = jsonObject.getJSONObject("response");
                                String quiz_date = response_object.getString("quiz_date");
                                String exam_code = response_object.getString("exam_code");
                                String exam_name = response_object.getString("exam_name");
                                String count_correct = response_object.getString("count_correct");
                                String count_total = response_object.getString("count_total");
                                String quiz_took_date = response_object.getString("quiz_took_date");
                                String quiz_took_time = response_object.getString("quiz_took_time");

                                TextView quiz_title_textView = (TextView) findViewById(R.id.quiz_title_textView);
                                TextView quiz_took_date_textView = (TextView) findViewById(R.id.quiz_took_date_textView);
                                TextView exam_name_textView = (TextView) findViewById(R.id.exam_name_textView);
                                TextView percent_textView = (TextView) findViewById(R.id.percent_textView);
                                TextView fraction_textView = (TextView) findViewById(R.id.fraction_textView);
                                ProgressBar percent_bar = (ProgressBar) findViewById(R.id.percent_bar);


                                String percent = decimal_two_digis(count_correct, count_total);

                                quiz_title_textView.setText(period_between_date(quiz_date)+" 퀴즈 결과");
                                exam_name_textView.setText(exam_name);
                                quiz_took_date_textView.setText(quiz_took_date+" "+quiz_took_time);
                                percent_textView.setText(percent+" %");
                                fraction_textView.setText(count_correct+" / "+count_total);
                                percent_bar.setProgress((int) Float.parseFloat(percent));

                                String temp = Html.fromHtml((String) response_object.getString("json_data")).toString();
                                JSONArray jsonArray = new JSONArray(temp);

                                for(int i = 0 ; i<jsonArray.length(); i++){
                                    String key = jsonArray.getJSONObject(i).getString("primary_key");
                                    String exam_placed_year = jsonArray.getJSONObject(i).getString("exam_placed_year");
                                    String exam_placed_round = jsonArray.getJSONObject(i).getString("exam_placed_round");
                                    String subject_code = jsonArray.getJSONObject(i).getString("subject_code");
                                    String subject_name = jsonArray.getJSONObject(i).getString("subject_name");
                                    String question_number = jsonArray.getJSONObject(i).getString("question_number");
                                    String question_question = jsonArray.getJSONObject(i).getString("question_question");
                                    String question_answer = jsonArray.getJSONObject(i).getString("question_answer");
                                    String correct_answer = jsonArray.getJSONObject(i).getString("correct_answer");
                                    String question_image_exist = jsonArray.getJSONObject(i).getString("question_image_exist");
                                    String answer_image_exist = jsonArray.getJSONObject(i).getString("answer_image_exist");
                                    String example_exist = jsonArray.getJSONObject(i).getString("example_exist");
                                    String user_answer = jsonArray.getJSONObject(i).getString("user_answer");

                                    create_quiz_result_view( i,  key, exam_code, exam_name, exam_placed_year,  exam_placed_round,  subject_code,  subject_name,  question_number,  question_question,  question_answer,  correct_answer,
                                             question_image_exist,  answer_image_exist,  example_exist,  user_answer, result_container);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressbar_invisible();
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
                params.put("key", key);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    // new old both use method under this
    public void create_quiz_result_view(int position, String key, String exam_code, String exam_name,  String exam_placed_year, String exam_placed_round, String subject_code, String subject_name, String question_number, String question_question, String question_answer, String correct_answer,
                                        String question_image_exist, String answer_image_exist, String example_exist, String user_answer, LinearLayout container){
        View each_problem_container = getLayoutInflater().inflate(R.layout.container_quiz_result_each_problem, null);
        TextView exam_name_year_round_textView = (TextView) each_problem_container.findViewById(R.id.exam_name_year_round_textView);
        TextView subject_name_number_textView = (TextView) each_problem_container.findViewById(R.id.subject_name_number_textView);
        TextView question_textView = (TextView) each_problem_container.findViewById(R.id.question_textView);
        TextView question_example_textView = (TextView) each_problem_container.findViewById(R.id.question_example_textView);
        ImageView question_imageView = (ImageView) each_problem_container.findViewById(R.id.question_imageView);
        ImageView icon_correct_incorrect_imageView = (ImageView) each_problem_container.findViewById(R.id.icon_correct_incorrect_imageView);



        exam_name_year_round_textView.setText(exam_placed_year+"년 "+exam_placed_round+"회 "+exam_name);
        subject_name_number_textView.setText(subject_name+" ["+question_number+"]");
        if(question_image_exist.equals("true") && example_exist.equals("true")){

        }else if(question_image_exist.equals("true") && (example_exist.equals("false") || example_exist.equals("null"))){
            question_example_textView.setVisibility(View.GONE); // 보기 텍스트뷰를 안보이게 하는 것

            String url = "http://www.joonandhoon.com/pp/PassPop/exam_images/"+exam_code+"/"+exam_code+"_"+exam_placed_year+"_"+exam_placed_round+"_"+subject_code+"_q_"+question_number+".PNG";
            question_textView.setText("["+(position+1)+"]"+ Html.fromHtml((String) question_question).toString());
            getQuestionImage(question_imageView, url);
        }else if(question_image_exist.equals("false") && example_exist.equals("true")){
            question_imageView.setVisibility(View.GONE);// 문제 이미지뷰 안보이게 하기

            String[] temp = question_question.split("#_SPLIT_#");
            String question = Html.fromHtml((String) temp[0]).toString().replaceAll("<br>", "\n");
            String example =  Html.fromHtml((String) temp[1]).toString().replaceAll("<br>", "\n");
            question_textView.setText("["+(position+1)+"]"+question);
            question_example_textView.setText(example);
        }else{
            // question image and example does not exist
            question_example_textView.setVisibility(View.GONE); // 보기 텍스트뷰를 안보이게 하는 것
            question_imageView.setVisibility(View.GONE);// 문제 이미지뷰 안보이게 하기
            question_textView.setText("["+(position+1)+"]"+ Html.fromHtml((String) question_question).toString());
        }

        if(answer_image_exist.equals("true")){
            // 답에 이미지가 존재할 경우
            //                이미지가 존재하는 답일때
            ConstraintLayout answer_1_conLayout =(ConstraintLayout) each_problem_container.findViewById(R.id.answer_1_conLayout);
            ImageView answer_1_imageView =(ImageView) each_problem_container.findViewById(R.id.answer_1_imageView);
            TextView image_correct_or_incorrect_1_textView = (TextView) each_problem_container.findViewById(R.id.image_correct_or_incorrect_1_textView);
            ConstraintLayout answer_2_conLayout =(ConstraintLayout) each_problem_container.findViewById(R.id.answer_2_conLayout);
            ImageView answer_2_imageView =(ImageView) each_problem_container.findViewById(R.id.answer_2_imageView);
            TextView image_correct_or_incorrect_2_textView = (TextView) each_problem_container.findViewById(R.id.image_correct_or_incorrect_2_textView);
            ConstraintLayout answer_3_conLayout =(ConstraintLayout) each_problem_container.findViewById(R.id.answer_3_conLayout);
            ImageView answer_3_imageView =(ImageView) each_problem_container.findViewById(R.id.answer_3_imageView);
            TextView image_correct_or_incorrect_3_textView = (TextView) each_problem_container.findViewById(R.id.image_correct_or_incorrect_3_textView);
            ConstraintLayout answer_4_conLayout =(ConstraintLayout) each_problem_container.findViewById(R.id.answer_4_conLayout);
            ImageView answer_4_imageView =(ImageView) each_problem_container.findViewById(R.id.answer_4_imageView);
            TextView image_correct_or_incorrect_4_textView = (TextView) each_problem_container.findViewById(R.id.image_correct_or_incorrect_4_textView);

            answer_1_conLayout.setVisibility(View.VISIBLE);
            answer_2_conLayout.setVisibility(View.VISIBLE);
            answer_3_conLayout.setVisibility(View.VISIBLE);
            answer_4_conLayout.setVisibility(View.VISIBLE);

            answer_1_imageView.setTag(1);
            answer_2_imageView.setTag(2);
            answer_3_imageView.setTag(3);
            answer_4_imageView.setTag(4);

            for(int k = 1; k<=4; k++){
                String url = "http://www.joonandhoon.com/pp/PassPop/exam_images/"+exam_code+"/"+exam_code+"_"+exam_placed_year+"_"+exam_placed_round+"_"+subject_code+"_q_"+question_number+"_a_"+k+".PNG";
                ImageView imageView = (ImageView) each_problem_container.findViewWithTag(k);
                getAnswerImage(imageView, url);
            }

            answer_choice( answer_1_conLayout,  answer_2_conLayout,  answer_3_conLayout,  answer_4_conLayout,
                    image_correct_or_incorrect_1_textView,  image_correct_or_incorrect_2_textView,  image_correct_or_incorrect_3_textView,  image_correct_or_incorrect_4_textView,  correct_answer,  user_answer, icon_correct_incorrect_imageView);
        }else{
            //답에 이미지가 존재하지 않을경우
            //  이미지가 존재하지 않는 답일때
            ConstraintLayout no_image_answer_1_conLayout = (ConstraintLayout) each_problem_container.findViewById(R.id.no_image_answer_1_conLayout);
            TextView answer_1_textView = (TextView) each_problem_container.findViewById(R.id.answer_1_textView);
            TextView correct_or_incorrect_1_textView = (TextView) each_problem_container.findViewById(R.id.correct_or_incorrect_1_textView);

            ConstraintLayout no_image_answer_2_conLayout = (ConstraintLayout) each_problem_container.findViewById(R.id.no_image_answer_2_conLayout);
            TextView answer_2_textView = (TextView) each_problem_container.findViewById(R.id.answer_2_textView);
            TextView correct_or_incorrect_2_textView = (TextView) each_problem_container.findViewById(R.id.correct_or_incorrect_2_textView);

            ConstraintLayout no_image_answer_3_conLayout = (ConstraintLayout) each_problem_container.findViewById(R.id.no_image_answer_3_conLayout);
            TextView answer_3_textView = (TextView) each_problem_container.findViewById(R.id.answer_3_textView);
            TextView correct_or_incorrect_3_textView = (TextView) each_problem_container.findViewById(R.id.correct_or_incorrect_3_textView);

            ConstraintLayout no_image_answer_4_conLayout = (ConstraintLayout) each_problem_container.findViewById(R.id.no_image_answer_4_conLayout);
            TextView answer_4_textView = (TextView) each_problem_container.findViewById(R.id.answer_4_textView);
            TextView correct_or_incorrect_4_textView = (TextView) each_problem_container.findViewById(R.id.correct_or_incorrect_4_textView);

            String[] answer = question_answer.split("##");

            no_image_answer_1_conLayout.setVisibility(View.VISIBLE);
            no_image_answer_2_conLayout.setVisibility(View.VISIBLE);
            no_image_answer_3_conLayout.setVisibility(View.VISIBLE);
            no_image_answer_4_conLayout.setVisibility(View.VISIBLE);

            answer_1_textView.setText("①"+answer[0]);
            answer_2_textView.setText("②"+answer[1]);
            answer_3_textView.setText("③"+answer[2]);
            answer_4_textView.setText("④"+answer[3]);

            answer_choice( no_image_answer_1_conLayout,  no_image_answer_2_conLayout,  no_image_answer_3_conLayout,  no_image_answer_4_conLayout,
                     correct_or_incorrect_1_textView,  correct_or_incorrect_2_textView,  correct_or_incorrect_3_textView,  correct_or_incorrect_4_textView,  correct_answer,  user_answer, icon_correct_incorrect_imageView);
        }

        container.addView(each_problem_container);
    }
    public void answer_choice(ConstraintLayout one_conLayout, ConstraintLayout two_conLayout, ConstraintLayout three_conLayout, ConstraintLayout four_conLayout,
                              TextView one_textView, TextView two_textView, TextView three_textView, TextView four_textView, String correct_answer, String user_answer, ImageView imageView){

        int c_answer = Integer.parseInt(correct_answer);
        int u_answer =Integer.parseInt(user_answer);

            if(c_answer == u_answer) {
                if(c_answer == 1){
                    one_conLayout.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
                    one_textView.setVisibility(View.VISIBLE);
                }else if(c_answer == 2){
                    two_conLayout.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
                    two_textView.setVisibility(View.VISIBLE);
                }else if(c_answer == 3){
                    three_conLayout.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
                    three_textView.setVisibility(View.VISIBLE);
                }else if(c_answer == 4){
                    four_conLayout.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
                    four_textView.setVisibility(View.VISIBLE);
                }
            }else{
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_incorrect));
                if(c_answer == 1){
                    one_conLayout.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
                    one_textView.setVisibility(View.VISIBLE);
                }else if(c_answer == 2){
                    two_conLayout.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
                    two_textView.setVisibility(View.VISIBLE);
                }else if(c_answer == 3){
                    three_conLayout.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
                    three_textView.setVisibility(View.VISIBLE);
                }else if(c_answer == 4){
                    four_conLayout.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
                    four_textView.setVisibility(View.VISIBLE);
                }

                if(u_answer == 1){
                    one_conLayout.setBackground(getResources().getDrawable(R.drawable.exam_result_user_answer_choice));
                    one_textView.setVisibility(View.VISIBLE);
                    one_textView.setText("오답");
                }else if(u_answer == 2){
                    two_conLayout.setBackground(getResources().getDrawable(R.drawable.exam_result_user_answer_choice));
                    two_textView.setVisibility(View.VISIBLE);
                    two_textView.setText("오답");
                }else if(u_answer == 3){
                    three_conLayout.setBackground(getResources().getDrawable(R.drawable.exam_result_user_answer_choice));
                    three_textView.setVisibility(View.VISIBLE);
                    three_textView.setText("오답");
                }else if(u_answer == 4){
                    four_conLayout.setBackground(getResources().getDrawable(R.drawable.exam_result_user_answer_choice));
                    four_textView.setVisibility(View.VISIBLE);
                    four_textView.setText("오답");
                }
            }

    }






    private void toolbar(){
        Toolbar tb = (Toolbar) findViewById(R.id.QuizResultToolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
    }


    public void getQuestionImage(ImageView imageView, String url){
        Picasso.with(QuizResultActivity.this)
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
        if(url.length() <=0 || url == null || url.isEmpty()){

        }else{
            Picasso.with(QuizResultActivity.this)
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
    public String decimal_two_digis(String count_correct, String count_total){
        double correct = Float.parseFloat(count_correct);
        double total = Float.parseFloat(count_total);
        double percent = (correct/total)*100;
        String str_percent = new DecimalFormat("##.##").format(percent);
        return str_percent;
    }
    public String period_between_date(String date){
        String temp = "";
        for(int i = 0 ; i < date.length(); i++){
            if(i == 3 || i ==5){
                temp += date.charAt(i)+".";
            }else {
                temp += date.charAt(i);
            }
        }
        return temp;
    }
    public void progressbar_visible(){
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void progressbar_invisible(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
//        getMenuInflater().inflate(R.menu.exam_answer_sheet, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_right_bit,R.anim.slide_out);// first entering // second exiting
    }

}
