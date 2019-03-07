package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.ads.MobileAds;
import com.kakao.auth.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.pp.LoginActivity.callback;
import static com.storyvendingmachine.www.pp.VERSION.THIS_APP_VERSION;

public class EnteranceActivity extends AppCompatActivity {

    SharedPreferences login_remember;
    SharedPreferences.Editor editor;
    static String LATEST_VERSION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterance);
        initializer_element();
        initializer_ad();
        getLatestVersion();//getVersion


        //full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //full screen

        login_remember = getSharedPreferences("setting", 0);
        editor = login_remember.edit();
        String LT = login_remember.getString("login_type", "");
        Log.e("login_type:::", LT);

        if(LT.equals("kakao")){
            new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                @Override
                public void run() {
                //****************kakao login **************************
                    callback = new SessionCallback(EnteranceActivity.this, "enterance_activity", editor);
                    callback.pb = (ProgressBar) findViewById(R.id.progress_bar);

                    Session.getCurrentSession().addCallback(callback);
                    if (Session.getCurrentSession().checkAndImplicitOpen()) {
                        // 액세스토큰 유효하거나 리프레시 토큰으로 액세스 토큰 갱신을 시도할 수 있는 경우
                    } else {
                        // 무조건 재로그인을 시켜야 하는 경우
                        //            Session.getCurrentSession().clearCallbacks();
                    }
                //****************kakao login **************************

                }
            }, 1500);
        }else if(LT.equals("normal")){
            new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                @Override
                public void run() {
                    LoginRemember();
                }
            }, 1500);
        }else{
            new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                @Override
                public void run() {
                    String str_null = "null";
                    Intent intent = new Intent(EnteranceActivity.this, MajorExamTypeSelectorActivity.class);
                    intent.putExtra("login_type", str_null);
                    intent.putExtra("user_id", str_null);
                    intent.putExtra("member_level", str_null);
                    intent.putExtra("user_nickname", str_null);
                    intent.putExtra("user_thumbnail", str_null);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out); // 처번째가 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
                    finish();


                }
            }, 1500);
        }


    }
    public void initializer_ad(){
        //        makeQuiz(); // upload quiz~~ wait to see what happen
//아래가 나의 !!것
        MobileAds.initialize(this, "ca-app-pub-9203333069147351~3494839374");
//        MobileAds.initialize(this, "ca-app-pub-9203333069147351~3494839374");

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        //under sample
//        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
    }
    public void initializer_element(){
        TextView version_textView = (TextView) findViewById(R.id.version_textView);
        version_textView.setText("beta "+ THIS_APP_VERSION);
    }







    public void LoginRemember(){
//        login_remember = getSharedPreferences("setting", 0);
//        editor = login_remember.edit();
        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
            @Override
            public void run() {
                // 실행할 동작 코딩
                //front_cover.setVisibility(View.GONE);

                boolean login_success = login_remember.getBoolean("id_pw_match", true);
                if(login_success){
                    String user_email = login_remember.getString("user_email", "");
                    String user_password = login_remember.getString("user_password", "");
                    if(user_email.trim().length() == 0 || user_password.trim().length()==0){
                        Toast.makeText(EnteranceActivity.this, "preference empty 1", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(FrontActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }else{
                        Toast.makeText(EnteranceActivity.this, "login check", Toast.LENGTH_SHORT).show();
                        loginCheck(user_email, user_password);
                    }

                }else{

                    Toast.makeText(EnteranceActivity.this, "preference empty 2", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EnteranceActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);



                }
            }
        }, 800);


    }
    private void loginCheck(final String input_user_email, final String input_user_password){
        RequestQueue queue = Volley.newRequestQueue(EnteranceActivity.this);
//        String url = "http://www.storyvendingmachine.com/android/front_page_normal_login_check.php";
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/front_page_normal_login_check.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("response");
                            JSONObject temp = jsonArray.getJSONObject(0);

                            String login_success_fail = temp.getString("call");
                            if(login_success_fail.equals("login_success")){
                                String user_email = temp.getString("user_email");
                                String member_level = temp.getString("member_level");
                                String user_nickname = temp.getString("user_nickname");
                                String user_thumbnail = temp.getString("user_thumbnail");
                                String user_selected_last_major_exam = temp.getString("user_selected_last_major_exam");
                                String user_selected_last_exam_code = temp.getString("user_selected_last_exam_code");
                                String user_selected_last_exam_name = temp.getString("user_selected_last_exam_name");

                                editor.putBoolean("id_pw_match", true);
                                editor.putString("user_email", input_user_email);
                                editor.putString("user_password", input_user_password);
                                editor.commit();

                                if(user_selected_last_major_exam.equals("null") || user_selected_last_exam_code.equals("null")){
                                    Intent intent = new Intent(EnteranceActivity.this, MajorExamTypeSelectorActivity.class);
                                    intent.putExtra("login_type", "normal");
                                    intent.putExtra("user_id", user_email);
                                    intent.putExtra("member_level", member_level);
                                    intent.putExtra("user_nickname", user_nickname);
                                    intent.putExtra("user_thumbnail", user_thumbnail);
//                                    intent.putExtra("user_selected_last_major_exam", user_selected_last_major_exam);
//                                    intent.putExtra("user_selected_last_exam_code", user_selected_last_exam_code);
//                                    intent.putExtra("user_selected_last_exam_name", user_selected_last_exam_name);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                }else{
                                    Intent intent = new Intent(EnteranceActivity.this, MainActivity.class);
                                    intent.putExtra("login_type", "normal");
                                    intent.putExtra("user_id", user_email);
                                    intent.putExtra("member_level", member_level);
                                    intent.putExtra("user_nickname", user_nickname);
                                    intent.putExtra("user_thumbnail", user_thumbnail);
                                    intent.putExtra("user_selected_last_major_exam", user_selected_last_major_exam);
                                    intent.putExtra("user_selected_last_exam_code", user_selected_last_exam_code);
                                    intent.putExtra("user_selected_last_exam_name", user_selected_last_exam_name);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                }
                            }else if(login_success_fail.equals("login_fail")){
                                editor.putString("login_type", "null");
                                editor.putBoolean("id_pw_match", false);
                                editor.putString("user_email", "");
                                editor.putString("user_password", "");
                                editor.commit();

                                String str_null="null";
                                Intent intent = new Intent(EnteranceActivity.this, MainActivity.class);
                                intent.putExtra("login_type", str_null);
                                intent.putExtra("user_id", str_null);
                                intent.putExtra("member_level", str_null);
                                intent.putExtra("user_nickname", str_null);
                                intent.putExtra("user_thumbnail", str_null);
                                intent.putExtra("user_selected_last_major_exam", str_null);
                                intent.putExtra("user_selected_last_exam_code", str_null);
                                intent.putExtra("user_selected_last_exam_name", str_null);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();

                            }else{
                                Log.e("알수 없는 에러 발생", "php 를 켜서 확인 하세요");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", input_user_email);
                params.put("password", input_user_password);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void getLatestVersion(){
        RequestQueue queue = Volley.newRequestQueue(EnteranceActivity.this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/getLatestVersion.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("version", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")){
                                String version = jsonObject.getJSONObject("response").getString("version");
                                String upload_date = jsonObject.getJSONObject("response").getString("upload_date");
                                String upload_time = jsonObject.getJSONObject("response").getString("upload_time");
                                LATEST_VERSION = version;
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

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void select_exam_type(){

    }
}
