package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.kakao.auth.Session;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;

public class ExamResultActivity extends AppCompatActivity implements RewardedVideoAdListener {
    final int ORDER_FIRST_TIME = 1;
    final int ORDER_NOT_FIRST_TIME = -1;

    ProgressBar progressBar;
    LinearLayout progressBarBackground;
//    LinearLayout linearLayout;//ExamResultActivity total container ::: must add in here at the end
    LinearLayout linearLayout_inner; // ExamResultActivity inner container :::
    LinearLayout linearLayout_inner_second;

    String exam_name, exam_code, published_year, published_round, refresh_upload_prevent, date_user_took_exam, time_user_took_exam;

    TextView wrong_question_textView, correct_color_textView, incorrect_color_textView;
    private RewardedVideoAd mRewardedVideoAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_result);
        toolbar();
        initializer();

        if(LoginType.equals("kakao") || LoginType.equals("normal")){
            //로그인 상태
            Intent getIntent = getIntent();
            String identifier = getIntent.getStringExtra("from");
            if(identifier.equals("StatisticFragment")){
                //StatisticFragment 에서 받은 intent
                Log.e("from see", "from see more");
                exam_code = getIntent.getStringExtra("exam_code");
                exam_name = getIntent.getStringExtra("exam_name");
                published_year = getIntent.getStringExtra("exam_placed_year");
                published_round = getIntent.getStringExtra("exam_placed_round");
                date_user_took_exam = getIntent.getStringExtra("date_user_took_exam");
                time_user_took_exam = getIntent.getStringExtra("time_user_took_exam");

                TextView title_textView = (TextView) findViewById(R.id.title_textView);
                title_textView.setText(published_year+" 년 "+ published_round + " 회 "+exam_name);
                progressbar_visible();// 동그라미
                fromStatisticFragment_getSelectedExamResult_fromDatabase(exam_code, published_year, published_round, date_user_took_exam, time_user_took_exam);

            }else{
                //ExamViewActivity 에서 받은 intent
                String ExamResult = getIntent.getStringExtra("ExamResult"); //ExamResult will be sent to server and calculate
                exam_code = getIntent.getStringExtra("exam_code");
                exam_name = getIntent.getStringExtra("exam_name");
                published_year = getIntent.getStringExtra("published_year");
                published_round = getIntent.getStringExtra("published_round");
                refresh_upload_prevent = getIntent.getStringExtra("refresh_upload_prevent");

                TextView title_textView = (TextView) findViewById(R.id.title_textView);
                title_textView.setText(published_year+" 년 "+ published_round + " 회 "+exam_name);

                progressbar_visible();// 동그라미
                sendExamResultToServerForCalculator(ExamResult, exam_code, published_year, published_round, ORDER_FIRST_TIME, null);
            }
        }else{
            //로그인을 안한 상태
            Intent getIntent = getIntent();
            String ExamResult = getIntent.getStringExtra("ExamResult"); //ExamResult will be sent to server and calculate
            exam_code = getIntent.getStringExtra("exam_code");
            exam_name = getIntent.getStringExtra("exam_name");
            published_year = getIntent.getStringExtra("published_year");
            published_round = getIntent.getStringExtra("published_round");
            refresh_upload_prevent = getIntent.getStringExtra("refresh_upload_prevent");

            TextView title_textView = (TextView) findViewById(R.id.title_textView);
            title_textView.setText(published_year+" 년 "+ published_round + " 회 "+exam_name);

            progressbar_visible();// 동그라미
            sendExamResultToServerForCalculator(ExamResult, exam_code, published_year, published_round, ORDER_FIRST_TIME, null);
        }
    }
    public void fromStatisticFragment_getSelectedExamResult_fromDatabase(final String exam_code, final String exam_placed_year, final String exam_placed_round, final String date_user_took_exam, final String time_user_took_exam){
        RequestQueue queue = Volley.newRequestQueue(ExamResultActivity.this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/getfromStatisticFragment_getSelectedExamResult_fromDatabase.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")) {
                                String json_date_string = jsonObject.getString("response");
                                String rewarded = jsonObject.getString("rewarded");
                                sendExamResultToServerForCalculator(json_date_string, exam_code, exam_placed_year, exam_placed_round, ORDER_NOT_FIRST_TIME, rewarded);
                                Log.e("what", response);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
//                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
//                        toast(message);
//                        getExamNameAndCode(input_exam_name); // 인터넷 에러가 났을시 다시 한번 시도한다.
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
                params.put("date_user_took_exam", date_user_took_exam);
                params.put("time_user_took_exam", time_user_took_exam);
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    private void toolbar(){
        Toolbar tb = (Toolbar) findViewById(R.id.exam_result_toolbar);
        tb.setElevation(5);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리

    }
    private void initializer(){
        progressBar = (ProgressBar) findViewById(R.id.exam_result_progress_bar);
        progressBarBackground = (LinearLayout) findViewById(R.id.progress_bar_background);
        linearLayout_inner = (LinearLayout) findViewById(R.id.exam_result_inner_container);
        linearLayout_inner_second = (LinearLayout) findViewById(R.id.exam_result_inner_second_container);

        wrong_question_textView = (TextView) findViewById(R.id.wrong_question_textView);
        correct_color_textView = (TextView) findViewById(R.id.correct_color_textView);
        incorrect_color_textView = (TextView) findViewById(R.id.incorrect_color_textView);
        wrong_question_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "죄송합니다...\n틀린 문제를 보기 위해 보상 광고를 시청해주셔야 합니다ㅜ_ㅜ서버유지비 때문에...";
                String positive_message = "광고 시청";
                String negative_message = "시청 안함";
                wrongQuestionAdReward_notifier(message, positive_message, negative_message);
            }
        });
        refresh_upload_prevent="null";
        date_user_took_exam="null";
        time_user_took_exam="null";
    }
    private void rewardAdInitialize(){
//          MobileAds.initialize(this, "ca-app-pub-9203333069147351~3494839374");
        //tester
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        //tester
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
    }
    private void loadRewardedVideoAd() {
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("C646CB521B367AC2D90D66C8B4A3EECE")
//                .build();
//        mRewardedVideoAd.loadAd("ca-app-pub-9203333069147351/2841207026", adRequest); // device tester

//        mRewardedVideoAd.loadAd("ca-app-pub-9203333069147351/2841207026",
//                new AdRequest.Builder().build());
//        google tester
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }
    public void wrongQuestionAdReward_notifier(String message, String positive_message, String negative_message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressbar_visible();
                        rewardAdInitialize();
                    }
                })
                .setNegativeButton(negative_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }


    public int eachSubjectPassFail(int correct, int incorrect){
        int total = correct+incorrect;
        float min = (float) correct/total;
//        Log.e("minimum", String.valueOf(min)+"/"+total+"/"+correct);
        if(min >= .40){
            //pass
            return 1;
        }else{
            //fail
            return -1;
        }
    }
    public void highlight_user_and_correct_answer(TextView one, TextView two, TextView three, TextView four, int correct_answer, int user_answer){
        if(correct_answer == 1){
            one.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
        }else if(correct_answer == 2){
            two.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
        }else if(correct_answer == 3){
            three.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
        }else if(correct_answer == 4){
            four.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
        }

        if(user_answer == 1){
            one.setBackground(getResources().getDrawable(R.drawable.exam_result_user_answer_choice));
        }else if(user_answer == 2){
            two.setBackground(getResources().getDrawable(R.drawable.exam_result_user_answer_choice));
        }else if(user_answer == 3){
            three.setBackground(getResources().getDrawable(R.drawable.exam_result_user_answer_choice));
        }else if(user_answer == 4){
            four.setBackground(getResources().getDrawable(R.drawable.exam_result_user_answer_choice));
        }else{

        }
    }
    public void higlight_user_and_correct_answer_image(ConstraintLayout one, ConstraintLayout two, ConstraintLayout three, ConstraintLayout four, int correct_answer, int user_answer){
        if(correct_answer == 1){
            one.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
        }else if(correct_answer == 2){
            two.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
        }else if(correct_answer == 3){
            three.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
        }else if(correct_answer == 4){
            four.setBackground(getResources().getDrawable(R.drawable.exam_result_correct_answer_choice));
        }

        if(user_answer == 1){
            one.setBackground(getResources().getDrawable(R.drawable.exam_result_user_answer_choice));
        }else if(user_answer == 2){
            two.setBackground(getResources().getDrawable(R.drawable.exam_result_user_answer_choice));
        }else if(user_answer == 3){
            three.setBackground(getResources().getDrawable(R.drawable.exam_result_user_answer_choice));
        }else if(user_answer == 4){
            four.setBackground(getResources().getDrawable(R.drawable.exam_result_user_answer_choice));
        }else{

        }
    }

    public void sendExamResultToServerForCalculator(final String ExamResult, final String exam_code, final String published_year, final String published_round, final int order, final String rewarded){

        RequestQueue queue = Volley.newRequestQueue(ExamResultActivity.this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/CalculateResult.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("exam result json", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")){
                                //when access valid
                                JSONArray jsonArray = jsonObject.getJSONArray("response");
                                int[] pass_fail_array = new int[jsonArray.length()];
                                int total_correct = 0;
                                int total_questions = 0;
                                    for(int i = 0 ; i<jsonArray.length(); i++){
                                        View view =View.inflate(ExamResultActivity.this, R.layout.exam_subject_result_element, null);

                                        TextView subject_name_textView = (TextView) view.findViewById(R.id.subject_name_textView);
                                        TextView subject_score_textView = (TextView) view.findViewById(R.id.subject_score_textView);

                                        String subject_code = jsonArray.getJSONObject(i).getString("subject_code");
                                        String subject_name = jsonArray.getJSONObject(i).getString("subject_name");
                                        String correct_count = jsonArray.getJSONObject(i).getString("correct_count");
                                        String incorrect_count = jsonArray.getJSONObject(i).getString("incorrect_count");



                                        int correct_count_int = Integer.parseInt(correct_count);
                                        int incorrect_count_int = Integer.parseInt(incorrect_count);

                                        subject_name_textView.setText(subject_name);
                                        subject_score_textView.setText(correct_count+" / "+ (correct_count_int+incorrect_count_int));

                                        total_correct+=correct_count_int;

                                        total_questions+=correct_count_int;
                                        total_questions+=incorrect_count_int;

                                        int PassFail = eachSubjectPassFail(correct_count_int, incorrect_count_int); // 1 is pass , -1 is fail
                                        pass_fail_array[i] = PassFail;
                                        linearLayout_inner.addView(view);

                                        JSONArray compared_array = jsonArray.getJSONObject(i).getJSONArray("compared");
                                        JSONArray question_number = jsonArray.getJSONObject(i).getJSONArray("question_number");
                                        JSONArray question_array = jsonArray.getJSONObject(i).getJSONArray("question_array");
                                        JSONArray answer_array = jsonArray.getJSONObject(i).getJSONArray("answer_array");
                                        JSONArray correct_answer = jsonArray.getJSONObject(i).getJSONArray("correct_answer");
                                        JSONArray user_answer = jsonArray.getJSONObject(i).getJSONArray("user_answer");
                                        JSONArray question_image = jsonArray.getJSONObject(i).getJSONArray("question_image");
                                        JSONArray answer_image = jsonArray.getJSONObject(i).getJSONArray("answer_image");
                                        JSONArray example_exist = jsonArray.getJSONObject(i).getJSONArray("example_exist");
                                        for(int j = 0; j<compared_array.length(); j++){
                                            String compared = compared_array.getString(j);// identify correct or incorrect
                                            if(compared.equals("incorrect")){
                                                View view_incorrect =View.inflate(ExamResultActivity.this, R.layout.exam_result_wrong_question_element, null);
                                                TextView subject_name_inner_textView = view_incorrect.findViewById(R.id.subject_name_textView);
                                                TextView exam_name_inner_textView = view_incorrect.findViewById(R.id.exam_name_textView);
                                                TextView question_textView = view_incorrect.findViewById(R.id.question_textView);
                                                TextView question_example_textView = view_incorrect.findViewById(R.id.question_example_textView);
                                                ImageView question_imageView = view_incorrect.findViewById(R.id.question_imageView);

                                                TextView answer_1_textView = view_incorrect.findViewById(R.id.answer_1_textView);
                                                TextView answer_2_textView = view_incorrect.findViewById(R.id.answer_2_textView);
                                                TextView answer_3_textView = view_incorrect.findViewById(R.id.answer_3_textView);
                                                TextView answer_4_textView = view_incorrect.findViewById(R.id.answer_4_textView);

                                                ConstraintLayout answer_1_conLayout = (ConstraintLayout) view_incorrect.findViewById(R.id.answer_1_conLayout);
                                                ConstraintLayout answer_2_conLayout = (ConstraintLayout) view_incorrect.findViewById(R.id.answer_2_conLayout);
                                                ConstraintLayout answer_3_conLayout = (ConstraintLayout) view_incorrect.findViewById(R.id.answer_3_conLayout);
                                                ConstraintLayout answer_4_conLayout = (ConstraintLayout) view_incorrect.findViewById(R.id.answer_4_conLayout);



                                                if(example_exist.getString(j).equals("true")){
                                                    String[] question_question = question_array.getString(j).split("##");
                                                    question_textView.setText("[ "+(j+1)+ " ] " + question_question[0].replace("<br>", "\n"));
                                                    question_example_textView.setText(question_question[1].replace("<br>", "\n"));
                                                }else{
                                                    String question_question = question_array.getString(j);
                                                    question_textView.setText("[ "+(j+1)+ " ] " + question_question);
                                                    question_example_textView.setVisibility(View.GONE);
                                                }

                                                if(question_image.getString(j).equals("true")){
                                                    String url = "http://www.joonandhoon.com/pp/PassPop/exam_images/"+exam_code+"/"+exam_code+"_"+published_year+"_"+published_round+"_"+subject_code+"_q_"+question_number.getString(j)+".PNG";
                                                    getQuestionImage(question_imageView, url);
                                                }else{
                                                    question_imageView.setVisibility(View.GONE);
                                                }

                                                String c_answer = correct_answer.getString(j);
                                                String u_answer = user_answer.getString(j);

                                                int c_answer_int = Integer.parseInt(c_answer);
                                                int u_answer_int = Integer.parseInt(u_answer);

                                                if(answer_image.getString(j).equals("true")){
                                                    answer_1_conLayout.setVisibility(View.VISIBLE);
                                                    answer_2_conLayout.setVisibility(View.VISIBLE);
                                                    answer_3_conLayout.setVisibility(View.VISIBLE);
                                                    answer_4_conLayout.setVisibility(View.VISIBLE);

                                                    ImageView answer_1_imageView = (ImageView) view_incorrect.findViewById(R.id.answer_1_imageView);
                                                    answer_1_imageView.setTag(1);
                                                    ImageView answer_2_imageView = (ImageView) view_incorrect.findViewById(R.id.answer_2_imageView);
                                                    answer_2_imageView.setTag(2);
                                                    ImageView answer_3_imageView = (ImageView) view_incorrect.findViewById(R.id.answer_3_imageView);
                                                    answer_3_imageView.setTag(3);
                                                    ImageView answer_4_imageView = (ImageView) view_incorrect.findViewById(R.id.answer_4_imageView);
                                                    answer_4_imageView.setTag(4);

                                                    for(int k = 1; k<=4; k++){
                                                        String url = "http://www.joonandhoon.com/pp/PassPop/exam_images/"+exam_code+"/"+exam_code+"_"+published_year+"_"+published_round+"_"+subject_code+"_q_"+question_number.getString(j)+"_a_"+k+".PNG";
                                                        ImageView imageView = (ImageView) view_incorrect.findViewWithTag(k);
                                                        getAnswerImage(imageView, url);
                                                    }
                                                    higlight_user_and_correct_answer_image(answer_1_conLayout, answer_2_conLayout, answer_3_conLayout, answer_4_conLayout, c_answer_int, u_answer_int);
                                                }else{
                                                    answer_1_textView.setVisibility(View.VISIBLE);
                                                    answer_2_textView.setVisibility(View.VISIBLE);
                                                    answer_3_textView.setVisibility(View.VISIBLE);
                                                    answer_4_textView.setVisibility(View.VISIBLE);

                                                    String[] answer = answer_array.getString(j).split("##");
                                                    answer_1_textView.setText("①."+answer[0]);
                                                    answer_2_textView.setText("②."+answer[1]);
                                                    answer_3_textView.setText("③."+answer[2]);
                                                    answer_4_textView.setText("④."+answer[3]);


                                                     highlight_user_and_correct_answer(answer_1_textView, answer_2_textView,
                                                        answer_3_textView, answer_4_textView, c_answer_int, u_answer_int);
                                                }
                                                subject_name_inner_textView.setText(subject_name);
                                                exam_name_inner_textView.setText(exam_name);
                                                linearLayout_inner_second.addView(view_incorrect);
                                            }

                                        }
                                    }

                                    View view =View.inflate(ExamResultActivity.this, R.layout.exam_subject_result_element, null);

                                    TextView subject_name_textView = (TextView) view.findViewById(R.id.subject_name_textView);
                                    TextView subject_score_textView = (TextView) view.findViewById(R.id.subject_score_textView);

                                    subject_name_textView.setText("전체");
                                    subject_score_textView.setText(total_correct+" / "+total_questions);
                                    linearLayout_inner.addView(view);

                                    float pass_fail = (float) total_correct/total_questions;
                                    if(pass_fail>= 0.6){
                                       //total pass
                                        int result = 1;
                                        for(int i = 0 ; i<pass_fail_array.length; i++){
                                            if(pass_fail_array[i]==-1){
                                                result = -1;
                                            }
                                        }
                                        if(result == 1){
                                            // subject all pass
                                            TextView fail_textView = new TextView(ExamResultActivity.this);
                                            fail_textView.setText("합격");
                                            fail_textView.setTextColor(getResources().getColor(R.color.colorWhite));
                                            fail_textView.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                                            fail_textView.setPadding(20,20,20,20);
                                            fail_textView.setGravity(Gravity.CENTER);
                                            linearLayout_inner.addView(fail_textView);
                                        }else{
                                            // subject not pass
                                            TextView fail_textView = new TextView(ExamResultActivity.this);
                                            fail_textView.setText("불합격");
                                            fail_textView.setTextColor(getResources().getColor(R.color.colorWhite));
                                            fail_textView.setBackgroundColor(getResources().getColor(R.color.colorRed));
                                            fail_textView.setPadding(20,20,20,20);
                                            fail_textView.setGravity(Gravity.CENTER);
                                            linearLayout_inner.addView(fail_textView);
                                        }

                                    }else{
                                        //total fail which mean fail
                                        TextView fail_textView = new TextView(ExamResultActivity.this);
                                        fail_textView.setText("불합격");
                                        fail_textView.setTextColor(getResources().getColor(R.color.colorWhite));
                                        fail_textView.setBackgroundColor(getResources().getColor(R.color.colorRed));
                                        fail_textView.setPadding(20,20,20,20);
                                        fail_textView.setGravity(Gravity.CENTER);
                                        linearLayout_inner.addView(fail_textView);
                                    }

                                    if(order == ORDER_FIRST_TIME){
                                        checkIfRewarded();
                                    }else{
                                        // order == -1 일때....
                                        if(rewarded.equals("true")){
                                            linearLayout_inner_second.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    progressbar_invisible();
                            }else{
                                //when access invalid
                                Toast.makeText(ExamResultActivity.this, "access::"+access, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
//                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
//                        toast(message);
//                        getExamNameAndCode(input_exam_name); // 인터넷 에러가 났을시 다시 한번 시도한다.
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("result_data", ExamResult);
                return params;
            }
        };
        queue.add(stringRequest);
    }


    public void getQuestionImage(ImageView imageView, String url){
        Picasso.with(ExamResultActivity.this)
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
            Picasso.with(ExamResultActivity.this)
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

    public void progressbar_visible(){
        progressBar.setVisibility(View.VISIBLE);
        progressBarBackground.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void progressbar_invisible(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.INVISIBLE);
        progressBarBackground.setVisibility(View.GONE);
    }

    public void calculateExamResult(String ExamResult){
        JSONArray temp_array = new JSONArray();
        try {
            JSONArray jsonArray = new JSONArray(ExamResult);
            for(int i = 0; i<jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String subject_name = object.getString("subject_name");
                String compared = object.getString("compared");



                int index = search(temp_array, subject_name);
                Log.e("subject name compared", subject_name+"//"+compared+"//"+Integer.toString(index));
                if(index == -1){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("subject_name", subject_name);
                    JSONArray temp = new JSONArray();
                    temp.put(compared);
                    jsonObject.put("compared", temp);
                    temp_array.put(jsonObject);
                }else{
                    temp_array.getJSONObject(index).getJSONArray("compared").put(compared);
                }

            }

            Log.e("json array length :::", Integer.toString(temp_array.length()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public int search(JSONArray jsonArray, String input){
        for(int i = 0 ; i<jsonArray.length(); i++){
            try {
                String exam_name = jsonArray.getJSONObject(i).getString("subject_name");
                if(exam_name.equals(input)){
                    return i;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public void checkIfRewarded(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/checkIfRewarded.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("reward check ::" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                String reward_response = jsonObject.getString("response");
                                if(reward_response.equals("true")){
                                    linearLayout_inner_second.setVisibility(View.VISIBLE);
                                }
                            }else if(access_token.equals("invalid")){

                            }else{

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                params.put("published_year", published_year);
                params.put("published_round", published_round);
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                params.put("refresh_upload_prevent", refresh_upload_prevent);

                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void updateRewardTrue(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/updateRewardTrue.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("reward check ::" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){

                            }else if(access_token.equals("invalid")){

                            }else{

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                params.put("published_year", published_year);
                params.put("published_round", published_round);
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                params.put("refresh_upload_prevent", refresh_upload_prevent);
                params.put("date_user_took_exam", date_user_took_exam);
                params.put("time_user_took_exam", time_user_took_exam);

                return params;
            }
        };
        queue.add(stringRequest);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_right_bit,R.anim.slide_out);// first entering // second exiting
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
//        if(id == R.id.answer_sheet_menu) {
//
//        }
        return super.onOptionsItemSelected(item);
    }

    //안드로이드 앱 자체의 수기주기에 대하여 이 리워드 비디오를 시작하고 없앤다.
    @Override
    public void onResume() {
//        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
//        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
//        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }
    //안드로이드 앱 자체의 수기주기에 대하여 이 리워드 비디오를 시작하고 없앤다.

    /////// 보상 광고 에 필요한 override
    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();
        // Reward the user.
        linearLayout_inner_second.setVisibility(View.VISIBLE);
        updateRewardTrue();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad-"+String.valueOf(errorCode), Toast.LENGTH_SHORT).show();
        progressbar_invisible();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
        if (mRewardedVideoAd.isLoaded()) {
            progressbar_invisible();
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {
        Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
    }
    /////// 보상 광고 에 필요한 override
}
