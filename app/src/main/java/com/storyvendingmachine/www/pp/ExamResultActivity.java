package com.storyvendingmachine.www.pp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
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
//    LinearLayout linearLayout;//ExamResultActivity total container ::: must add in here at the end
    LinearLayout linearLayout_inner; // ExamResultActivity inner container :::
    LinearLayout linearLayout_inner_second;

    String exam_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_result);

        progressBar = (ProgressBar) findViewById(R.id.exam_result_progress_bar);

//        linearLayout = (LinearLayout) findViewById(R.id.exam_result_container);
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
        sendExamResultToServerForCalculator(ExamResult);
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
    public void sendExamResultToServerForCalculator(final String ExamResult){

        RequestQueue queue = Volley.newRequestQueue(ExamResultActivity.this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/CalculateResult.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                                        JSONArray question_array = jsonArray.getJSONObject(i).getJSONArray("question_array");
                                        JSONArray answer_array = jsonArray.getJSONObject(i).getJSONArray("answer_array");
                                        JSONArray correct_answer = jsonArray.getJSONObject(i).getJSONArray("correct_answer");
                                        JSONArray user_answer = jsonArray.getJSONObject(i).getJSONArray("user_answer");
                                        for(int j = 0; j<compared_array.length(); j++){
                                            String compared = compared_array.getString(j);// identify correct or incorrect
                                            if(compared.equals("incorrect")){
                                                View view_incorrect =View.inflate(ExamResultActivity.this, R.layout.exam_result_wrong_question_element, null);

                                                TextView subject_name_inner_textView = view_incorrect.findViewById(R.id.subject_name_textView);
                                                TextView exam_name_inner_textView = view_incorrect.findViewById(R.id.exam_name_textView);
                                                TextView question_textView = view_incorrect.findViewById(R.id.question_textView);
                                                TextView answer_1_textView = view_incorrect.findViewById(R.id.answer_1_textView);
                                                TextView answer_2_textView = view_incorrect.findViewById(R.id.answer_2_textView);
                                                TextView answer_3_textView = view_incorrect.findViewById(R.id.answer_3_textView);
                                                TextView answer_4_textView = view_incorrect.findViewById(R.id.answer_4_textView);

                                                String question = question_array.getString(j);
                                                String[] answer = answer_array.getString(j).split("##");
                                                String c_answer = correct_answer.getString(j);
                                                String u_answer = user_answer.getString(j);

                                                int c_answer_int = Integer.parseInt(c_answer);
                                                int u_answer_int = Integer.parseInt(u_answer);

                                                subject_name_inner_textView.setText(subject_name);
                                                exam_name_inner_textView.setText(exam_name);
                                                question_textView.setText(" [ "+(j+1)+" ] "+question);
                                                answer_1_textView.setText("① "+answer[0]);
                                                answer_2_textView.setText("② "+answer[1]);
                                                answer_3_textView.setText("③ "+answer[2]);
                                                answer_4_textView.setText("④ "+answer[3]);

                                                highlight_user_and_correct_answer(answer_1_textView, answer_2_textView,
                                                        answer_3_textView, answer_4_textView, c_answer_int, u_answer_int);
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



    public void progressbar_visible(){
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void progressbar_invisible(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.INVISIBLE);
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
}
