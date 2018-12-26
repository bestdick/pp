package com.storyvendingmachine.www.pp;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
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

public class ExamResultActivity extends AppCompatActivity {

    ProgressBar progressBar;
    LinearLayout progressBarBackground;
//    LinearLayout linearLayout;//ExamResultActivity total container ::: must add in here at the end
    LinearLayout linearLayout_inner; // ExamResultActivity inner container :::
    LinearLayout linearLayout_inner_second;

    String exam_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_result);
        toolbar();
        progressBar = (ProgressBar) findViewById(R.id.exam_result_progress_bar);
        progressBarBackground = (LinearLayout) findViewById(R.id.progress_bar_background);
        linearLayout_inner = (LinearLayout) findViewById(R.id.exam_result_inner_container);
        linearLayout_inner_second = (LinearLayout) findViewById(R.id.exam_result_inner_second_container);



        Intent getIntent = getIntent();
        String ExamResult = getIntent.getStringExtra("ExamResult"); //ExamResult will be sent to server and calculate
        String exam_code = getIntent.getStringExtra("exam_code");
        exam_name = getIntent.getStringExtra("exam_name");
        String published_year = getIntent.getStringExtra("published_year");
        String published_round = getIntent.getStringExtra("published_round");

        TextView title_textView = (TextView) findViewById(R.id.title_textView);
        title_textView.setText(published_year+" 년 "+ published_round + " 회 "+exam_name);

        progressbar_visible();// 동그라미
        sendExamResultToServerForCalculator(ExamResult, exam_code, published_year, published_round);
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
    public void sendExamResultToServerForCalculator(final String ExamResult, final String exam_code, final String published_year, final String published_round){

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

//                                                String question = question_array.getString(j);
//                                                String[] answer = answer_array.getString(j).split("##");
//                                                String c_answer = correct_answer.getString(j);
//                                                String u_answer = user_answer.getString(j);
//
//                                                int c_answer_int = Integer.parseInt(c_answer);
//                                                int u_answer_int = Integer.parseInt(u_answer);
//
                                                subject_name_inner_textView.setText(subject_name);
                                                exam_name_inner_textView.setText(exam_name);
//                                                question_textView.setText(" [ "+(j+1)+" ] "+question);
//                                                answer_1_textView.setText("① "+answer[0]);
//                                                answer_2_textView.setText("② "+answer[1]);
//                                                answer_3_textView.setText("③ "+answer[2]);
//                                                answer_4_textView.setText("④ "+answer[3]);
//

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

                                    Log.e("total pass fail", String.valueOf(pass_fail)+"/"+total_correct+"/"+total_questions);


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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_right_bit,R.anim.slide_out);// first entering // second exiting
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

    private void toolbar(){
        Toolbar tb = (Toolbar) findViewById(R.id.exam_result_toolbar);
        tb.setElevation(5);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리

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


}
