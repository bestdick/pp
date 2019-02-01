package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
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
import org.w3c.dom.Text;

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

    String identifier;
    String exam_name, exam_code, published_year, published_round, refresh_upload_prevent, date_user_took_exam, time_user_took_exam;

    TextView wrong_question_textView;
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
            identifier = getIntent.getStringExtra("from");
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
                                sendExamResultToServerForCalculator(Html.fromHtml((String) json_date_string).toString(), exam_code, exam_placed_year, exam_placed_round, ORDER_NOT_FIRST_TIME, rewarded);
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
//                        TextView temp_textView = (TextView) findViewById(R.id.temp_textView);
//                        temp_textView.setText(response);
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
                                        View result_prob_view = View.inflate(ExamResultActivity.this, R.layout.container_exam_result_subject_title, null);

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
                                        //                                        여기까지은 시험 결과 단순한 표를 나타낸다.
                                        // 여기서부터는 모든 문제를 채점 그리고 보여주기
                                        TextView subject_title_textView = (TextView) result_prob_view.findViewById(R.id.subject_titleTextView);
                                        final LinearLayout problem_result_container = (LinearLayout) result_prob_view.findViewById(R.id.problem_result_container);
                                        subject_title_textView.setText(subject_name);
                                        subject_title_textView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                int visiblity = problem_result_container.getVisibility();
                                                if(visiblity == View.GONE || visiblity == View.INVISIBLE){
                                                    problem_result_container.setVisibility(View.VISIBLE);
                                                }else{
                                                    problem_result_container.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                        linearLayout_inner_second.addView(result_prob_view);



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
                                            String question_number1 = question_number.getString(j);
                                            String question_question = question_array.getString(j);
                                            String question_answer = answer_array.getString(j);
                                            String correct_answer1 = correct_answer.getString(j);
                                            String user_answer1 = user_answer.getString(j);
                                            String question_image_exist = question_image.getString(j);
                                            String answer_image_exist = answer_image.getString(j);
                                            String example_exist1 = example_exist.getString(j);

                                            create_quiz_result_view( j,  null, exam_code, exam_name, published_year,  published_round,
                                                    subject_code,  subject_name,  question_number1,  question_question,  question_answer,
                                                    correct_answer1, question_image_exist,  answer_image_exist,  example_exist1,
                                                    user_answer1, problem_result_container);

                                        }
                                    }



//                                     여기는 전체 합격 여부 판단.......
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
                                progressbar_invisible();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressbar_invisible();
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
                        progressbar_invisible();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("result_data", TextUtils.htmlEncode(ExamResult));
                return params;
            }
        };
        queue.add(stringRequest);
    }
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

//            String[] temp = question_question.split("#_SPLIT_#");
            String[] temp = question_question.split("##");
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
        if(LoginType.equals("kakao") || LoginType.equals("normal")){
            if(identifier.equals("StatisticFragment")){
            }else{
                setResult(RESULT_OK);
            }
        }
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
