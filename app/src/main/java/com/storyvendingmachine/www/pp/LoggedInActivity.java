package com.storyvendingmachine.www.pp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kakao.auth.Session;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.pp.LoginActivity.callback;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_nickname;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_thumbnail;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_name;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;


public class LoggedInActivity extends AppCompatActivity {
    SharedPreferences login_remember;
    SharedPreferences.Editor editor;

    public void ad(){
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("C646CB521B367AC2D90D66C8B4A3EECE")
//                .build();
        // 내 애뮬의 test device id 이다
        //12-27 05:38:09.561 14026-14026/com.storyvendingmachine.www.pp I/Ads: Use AdRequest.Builder.addTestDevice("C646CB521B367AC2D90D66C8B4A3EECE") to get test ads on this device.


        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.e("ad load fail", String.valueOf(errorCode));
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        ad();
        ImageView user_thumbnail_imageView = (ImageView) findViewById(R.id.user_thumbnail_imageView);
        TextView login_type_textView = (TextView) findViewById(R.id.login_type_textView);
        TextView user_nickname_textView = (TextView) findViewById(R.id.user_nickname_textView);
        TextView join_date_textView = (TextView) findViewById(R.id.join_date_textView);
        TextView study_exam_name_textView = (TextView) findViewById(R.id.study_exam_name_textView);

        user_kakao_normal_info(login_type_textView, user_nickname_textView, join_date_textView, study_exam_name_textView, user_thumbnail_imageView);
        toolbar();
        logoutProcess();

    }

    public void user_kakao_normal_info(TextView login_type_textView, TextView user_nickname_textView, TextView join_date_textView, TextView study_exam_name_textView, ImageView user_thumbnail_imageView){
        if(LoginType.equals("kakao")){
            login_type_textView.setText("카카오 계정");
            user_nickname_textView.setText(G_user_nickname);
            getThumbnailImageForAuthor(user_thumbnail_imageView, G_user_thumbnail);
            getUserInfo(join_date_textView, study_exam_name_textView);
        }else if(LoginType.equals("normal")){
            login_type_textView.setText("패스팝 계정");
            user_nickname_textView.setText(G_user_nickname+" ( "+ G_user_id +" ) ");
            getThumbnailImageForAuthor(user_thumbnail_imageView, G_user_thumbnail);
            getUserInfo(join_date_textView, study_exam_name_textView);
        }else{
            //로그인 타입 // 정해지지 않음
        }
    }


    public void getUserInfo(final TextView join_date_textView, final TextView study_exam_name_textView){
        RequestQueue queue = Volley.newRequestQueue(LoggedInActivity.this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/getUserInfo.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response:::", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")){
                                String exam_name = jsonObject.getJSONArray("response").getJSONObject(0).getString("last_exam");
                                String[] join_date = jsonObject.getJSONArray("response").getJSONObject(0).getString("join_date").split(" ");


                                join_date_textView.setText("가입 일자 : "+join_date[0]);
                                study_exam_name_textView.setText("공부 중 인 시험 : "+exam_name);
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
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void getThumbnailImageForAuthor(ImageView imageView, String url){
        if(url.equals("null") || url.length() <=0){
            imageView.setImageResource(R.drawable.icon_empty_thumbnail);
        }else{
            Picasso.with(this)
                    .load(url)
                    .transform(new CircleTransform())
                    .fit()
                    .centerInside()
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
    }
    private void toolbar(){
        Toolbar tb = (Toolbar) findViewById(R.id.loggedin_toolBar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
    }
    public void logoutProcess(){
        Button logout_button = (Button) findViewById(R.id.logout_button);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginType = "null";

                G_user_id = "null";
                G_user_nickname = "null";
                G_user_thumbnail = "null";

                exam_selection_name ="null";
                exam_selection_code ="null";

                login_remember = getSharedPreferences("setting", 0);
                editor = login_remember.edit();
                editor.putString("login_type", "null");
                editor.putBoolean("id_pw_match", false);
                editor.putString("user_email", "");
                editor.putString("user_password", "");
                editor.commit();

                Session.getCurrentSession().removeCallback(callback);
                Session.getCurrentSession().clearCallbacks();
                Session.getCurrentSession().close();

                Intent intent = new Intent(LoggedInActivity.this, MainActivity.class);
                setResult(RESULT_OK, intent);
//                finish();
                onBackPressed();
            }
        });

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
    @Override
    public void onBackPressed() {
//        Session.getCurrentSession().removeCallback(callback);
        super.onBackPressed();  // optional depending on your needs
        finish();
        overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }
}
