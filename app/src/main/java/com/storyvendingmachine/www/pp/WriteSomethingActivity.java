package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;

public class WriteSomethingActivity extends AppCompatActivity {
    ScrollView scrollView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_something);
        global_intialize();
        Intent intent = getIntent();
        String intent_type = intent.getStringExtra("intent_type");

        if(intent_type.equals("write_suggestion")){
            write_suggestion_initialize();
        }
    }

    public void global_intialize(){
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        scrollView = (ScrollView) findViewById(R.id.write_suggestion_scrollView);
        toolbar();
    }
    public void toolbar(){
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setElevation(1);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
    }
    public void write_suggestion_initialize(){
        scrollView.setVisibility(View.VISIBLE);
        EditText title_textView = (EditText) findViewById(R.id.title_editText);
        EditText content_textView = (EditText) findViewById(R.id.content_editText);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        Button submit_button = (Button) findViewById(R.id.submit_button);

        String checked_type = radio_control(radioGroup);
        submit_controll(submit_button, title_textView, content_textView, checked_type);
    }
    public String radio_control(RadioGroup radioGroup){
        int checked_radio_id = radioGroup.getCheckedRadioButtonId();
        if(checked_radio_id == R.id.feedback_radio){
            return "feedback";
        }else if(checked_radio_id == R.id.suggestion_radio){
            return "suggestion";
        }else{
            // error radio checked
            return "error";
        }
    }
    public void submit_controll(Button submit_controll, final TextView title_textView, final TextView content_textView, final String checked_type){
            submit_controll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = title_textView.getText().toString();
                    String content = content_textView.getText().toString();
                    if(title.length() <= 2 || content.length() <= 0){
                        String mes = "피드백/건의및개선/오류 제목 및 내용을 입력해 주세요";
                        String pos = "확인";
                        empty_notifier(mes, pos);
                    }else{
                        String mes = "피드백/건의및개선/오류 업로드 하시겠습니까?";
                        String pos = "확인";
                        String neg = "취소";
                        submit_notifier(mes, pos, neg, title, content, checked_type);
                    }
                }
            });

    }
    public void upload_suggestion(final String title, final String content, final String checked_type){
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/upload_suggestion.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("suggestion ::" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                String isSuccess = jsonObject.getString("response");
                                if(isSuccess.equals("upload_success")){
                                    String mes = "성공적으로 업로드 하였습니다";
                                    String pos = "확인";
                                    close_notifier(mes, pos);
                                }
                                progressbar_invisible();
                            }else if(access_token.equals("invalid")){

                            }else{

                            }
                            progressbar_invisible();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressbar_invisible();
                        }
                    }
                },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressbar_invisible();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("user_id", G_user_id);
                params.put("login_type", LoginType);
                params.put("suggestion_type", checked_type);
                params.put("title", title);
                params.put("content", content);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void submit_notifier(String message, String positive_message, String negative_message, final String title, final String content, final String checked_type){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressbar_visible();
                        String temp_title = make_string_html_format(title);
                        String temp_content = make_string_html_format(content);
                        upload_suggestion(temp_title, temp_content, checked_type);
                        Log.e("upload",temp_title+"/"+temp_content+"/"+checked_type);
                    }
                })
                .setNegativeButton(negative_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setResult(RESULT_OK);
                        finish();
                        overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out);// first entering // second exiting
                    }
                })
                .create()
                .show();
    }
    public void close_notifier(String message, String positive_message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setResult(RESULT_OK);
                        finish();
                        overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out);// first entering // second exiting
                    }
                })
                .create()
                .show();
    }


    public String make_string_html_format(String input){
        String trim_str = input.trim();
//        String replace_str = trim_str.replace("'", "\'");
        String replace_str = trim_str.replace("\n", "<br>");
        String html_encode = TextUtils.htmlEncode(replace_str);
//        return replace_str;
        return html_encode;
    }
    public void empty_notifier(String message, String positive_message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }
    public void progressbar_visible(){
        progressBar.setVisibility(View.VISIBLE);
//        progressBarBackground.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void progressbar_invisible(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.GONE);
//        progressBarBackground.setVisibility(View.GONE);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out);// first entering // second exiting
    }
}
