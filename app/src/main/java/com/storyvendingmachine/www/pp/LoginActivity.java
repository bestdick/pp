package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_nickname;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_thumbnail;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_name;


public class LoginActivity extends AppCompatActivity {
    static SessionCallback callback;

    SharedPreferences login_remember;
    SharedPreferences.Editor editor;
    EditText input_email;
    EditText input_password;

    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getKeyHash();

        login_remember = getSharedPreferences("setting", 0);
        editor = login_remember.edit();
        editor.putString("login_type", "null");
        editor.putBoolean("id_pw_match", false);

        input_email = (EditText) findViewById(R.id.user_id);
        input_password = (EditText) findViewById(R.id.user_password);

        //****************kakao login **************************
        callback = new SessionCallback(this, "login_activity", editor);
        callback.pb = (ProgressBar) findViewById(R.id.LoginActivityProgressBar);

        Session.getCurrentSession().addCallback(callback);
        if (Session.getCurrentSession().checkAndImplicitOpen()) {
            // 액세스토큰 유효하거나 리프레시 토큰으로 액세스 토큰 갱신을 시도할 수 있는 경우
        } else {
            // 무조건 재로그인을 시켜야 하는 경우
//            Session.getCurrentSession().clearCallbacks();
        }
        //****************kakao login **************************

        Button loginButton = (Button) findViewById(R.id.login_button);
        Button joinButton = (Button) findViewById(R.id.join_button);

        loginButtonFunction(loginButton);
        joinButtonClicked(joinButton);

        toolbar();
    }

    private void toolbar(){
        Toolbar tb = (Toolbar) findViewById(R.id.LoginActivityToolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        onBackPressed();
        return true;
    }

    public void joinButtonClicked(Button joinButton){
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                intent.putExtra("type", "join");
                startActivity(intent);
                slide_left_and_slide_in();
            }
        });
    }
    public void verificationResendClicked(Button verificationResendButton){
        verificationResendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                intent.putExtra("type", "verification_resend");
                startActivity(intent);
                slide_left_and_slide_in();
            }
        });
    }

    public void loginButtonFunction(Button loginButton){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb = (ProgressBar) findViewById(R.id.LoginActivityProgressBar);
                //////////항상 같이 다녀야합니다
                pb.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                //////////항상 같이 다녀야합니다
                if(input_email.length() == 0 || input_password.length() == 0){
                    String message = "아이디와 비밀번호를 입력해주세요";
                    notifier_retry(message);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    pb.setVisibility(View.INVISIBLE);
                }else{
                    Log.e("normal_login", "clicked");
                    loginButtonClicked();
                }

            }
        });
    }

    private void loginButtonClicked(){

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
//        String url = "http://www.storyvendingmachine.com/android/front_page_normal_login_check.php";
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/front_page_normal_login_check.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response:::", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("response");
                            JSONObject temp = jsonArray.getJSONObject(0);

                            String login_success_fail = temp.getString("call");
                            if(login_success_fail.equals("login_success")){
                                String user_email = temp.getString("user_email");
                                String user_nickname = temp.getString("user_nickname");
                                String user_thumbnail = temp.getString("user_thumbnail");
                                String user_selected_last_exam_code = temp.getString("user_selected_last_exam_code");
                                String user_selected_last_exam_name = temp.getString("user_selected_last_exam_name");

                                editor.putString("login_type", "normal");
                                editor.putBoolean("id_pw_match", true);
                                editor.putString("user_email", input_email.getText().toString());
                                editor.putString("user_password", input_password.getText().toString());
                                editor.commit();


//                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                intent.putExtra("login_type", "normal");
//                                intent.putExtra("user_email", user_email);
//                                intent.putExtra("user_nickname", user_nickname);
//                                intent.putExtra("user_thumbnail", user_thumbnail);
//                                startActivity(intent);
////                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                                finish();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("login_type", "normal");
                                intent.putExtra("user_id", user_email);
                                intent.putExtra("user_nickname", user_nickname);
                                intent.putExtra("user_thumbnail", user_thumbnail);
                                intent.putExtra("user_selected_last_exam_code", user_selected_last_exam_code);
                                intent.putExtra("user_selected_last_exam_name", user_selected_last_exam_name);
                                setResult(RESULT_OK, intent);
//                                finish();
                                onBackPressed();
//                                LoginType = "normal";
//                                G_user_id = user_email;
//                                G_user_nickname=user_nickname;
//                                G_user_thumbnail = user_thumbnail;
//
//                                exam_selection_code = user_selected_last_exam_code;
//                                exam_selection_name = user_selected_last_exam_name;




                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                pb.setVisibility(View.INVISIBLE);


                            }else if(login_success_fail.equals("login_fail")){
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                pb.setVisibility(View.INVISIBLE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("존재하지 않는 사용자 입니다. \n 이메일과 비밀번호를 다시 확인해 주세요")
                                        .setPositiveButton("다시 시도", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                        .create()
                                        .show();

                            }else if(login_success_fail.equals("not_verified")){
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                pb.setVisibility(View.INVISIBLE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("이메일 인증이 완료되지 않았습니다. \n 인증 후 다시 시도해주세요")
                                        .setPositiveButton("다시 시도", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                        .create()
                                        .show();

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
                params.put("email", input_email.getText().toString());
                params.put("password", input_password.getText().toString());
                return params;
            }
        };
        queue.add(stringRequest);

    }

    public void notifier_retry(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(message)
                .setPositiveButton("다시 시도", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }

    private void getKeyHash() {

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.storyvendingmachine.www.pp", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }
    @Override
    public void onBackPressed() {
//        Session.getCurrentSession().removeCallback(callback);
        super.onBackPressed();  // optional depending on your needs
        finish();
        overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }
    public void slide_left_and_slide_in(){//opening new activity
        overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }
}
