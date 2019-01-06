package com.storyvendingmachine.www.pp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.kakao.auth.Session;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.pp.FlashcardFragment.flashcardListView;

public class MainActivity extends AppCompatActivity{
    //*******************************************************
    //     intent request code    10001   ==  select exam request code
    //

    int requestCode_flashcardwrite = 10004;

    SharedPreferences login_remember;
    SharedPreferences.Editor editor;
    String LT;

    static FloatingActionButton fab;
    static String exam_selection_code;
    static String exam_selection_name;

    static String LoginType;
    static String G_user_id;
    static String G_user_nickname;
    static String G_user_thumbnail;

    private MainActivityViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;

    TextView logintype_textView;
    TextView exam_selection_textView;
    ImageView user_thumbnail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 0);


        logintype_textView = (TextView) findViewById(R.id.login_type_textview);// logintype textview
        exam_selection_textView = (TextView) findViewById(R.id.exam_selection_textView); // exam selection textView
        user_thumbnail = (ImageView) findViewById(R.id.user_thumbnail_imageView);
        toolbar();




        mViewPagerAdapter = new MainActivityViewPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageMargin(16);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
                fab.setImageResource(R.drawable.floating_button);
                if(position==2){
                        fab.setVisibility(View.VISIBLE);
                        fab.attachToListView(flashcardListView);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(LoginType.equals("null") || G_user_id.equals("null")){
                                    String message = "플래시카드 작성을 하시려면 로그인 후 작성해주시기 바람니다.";
                                    notifier(message);
                                }else{
                                    Intent intent = new Intent(MainActivity.this, FlashCardWriteActivity.class);
                                    startActivityForResult(intent, requestCode_flashcardwrite);
                                    slide_left_and_slide_in();
                                }
                            }
                        });
                   }else{
                        fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        login_remember = getSharedPreferences("setting", 0);
        editor = login_remember.edit();

        LT = login_remember.getString("login_type", "");
        if(LT.equals("kakao")){
            Intent getIntent = getIntent();
            LoginType = getIntent.getStringExtra("login_type");
            G_user_id = getIntent.getStringExtra("user_id");
            G_user_nickname =getIntent.getStringExtra("user_nickname");
            G_user_thumbnail = getIntent.getStringExtra("user_thumbnail");

            exam_selection_code = getIntent.getStringExtra("user_selected_last_exam_code");
            exam_selection_name = getIntent.getStringExtra("user_selected_last_exam_name");

            logintype_textView.setText(LoginType);


            getThumbnailImageForAuthor(user_thumbnail, G_user_thumbnail);
            user_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(MainActivity.this, LoggedInActivity.class);
                    startActivityForResult(intent, 10003);
                    slide_left_and_slide_in();
                }
            });
            if(exam_selection_code.equals("null")){
                exam_selection_textView.setText("시험 선택");
            }else{
                exam_selection_textView.setText(exam_selection_name);
            }

        }else if(LT.equals("normal")){
            Intent getIntent = getIntent();
            LoginType = getIntent.getStringExtra("login_type");
            G_user_id = getIntent.getStringExtra("user_id");
            G_user_nickname =getIntent.getStringExtra("user_nickname");
            G_user_thumbnail = getIntent.getStringExtra("user_thumbnail");

            exam_selection_code = getIntent.getStringExtra("user_selected_last_exam_code");
            exam_selection_name = getIntent.getStringExtra("user_selected_last_exam_name");

            logintype_textView.setText("passpop");

            user_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(MainActivity.this, LoggedInActivity.class);
                    startActivityForResult(intent, 10003);
                    slide_left_and_slide_in();
                }
            });
            if(exam_selection_code.equals("null")){
                exam_selection_textView.setText("시험 선택");
            }else{
                exam_selection_textView.setText(exam_selection_name);
            }
        }else{
            LoginType = "null";
            G_user_id = "null";
            G_user_nickname ="null";
            G_user_thumbnail = "null";

            exam_selection_name ="null";
            exam_selection_code ="null";

            logintype_textView.setText("Guest");
            exam_selection_textView.setText("시험 선택");

            user_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 10002);// 10002 카카오 로그인 RESULT 값
                    slide_left_and_slide_in();
                }
            });
        }


        TabLayout tabLayout = (TabLayout) findViewById(R.id.MainActivityTabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        tabLayout.getTabAt(0).setIcon(R.drawable.icon_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.icon_exam);
        tabLayout.getTabAt(2).setIcon(R.drawable.icon_flashcard);
        tabLayout.getTabAt(3).setIcon(R.drawable.icon_mypage);


        UIChange();
    }

    public void UIChange(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mViewPagerAdapter.notifyDataSetChanged();
            }
        });
    }

    public void notifier(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(message)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create()
                    .show();
    }
    public void getThumbnailImageForAuthor(ImageView imageView, String url){
        Picasso.with(this)
                .load(url)
                .transform(new CircleTransform())
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

    public void LoginRemember(){
        login_remember = getSharedPreferences("setting", 0);
        editor = login_remember.edit();
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
                        Toast.makeText(MainActivity.this, "preference empty", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(FrontActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }else{
                        Toast.makeText(MainActivity.this, "login check", Toast.LENGTH_SHORT).show();
                        loginCheck(user_email, user_password);
                    }

                }else{

                    Toast.makeText(MainActivity.this, "preference empty", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(FrontActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        }, 1500);


    }


    private void toolbar(){
        Toolbar tb = (Toolbar) findViewById(R.id.MainToolBar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_exam_select);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
        getSupportActionBar().setSubtitle("시험 선택");

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
//        getMenuInflater().inflate(R.menu.user_login_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.user_login_menu){
//            String p_login_type = login_remember.getString("login_type", "");
//            if(p_login_type.equals("kakao")){
//                Intent intent =new Intent(this, LoggedInActivity.class);
//                startActivity(intent);
//            }else if(p_login_type.equals("normal")){
//                Intent intent =new Intent(this, LoggedInActivity.class);
//                startActivity(intent);
//            }else{
//                Intent intent =new Intent(this, LoginActivity.class);
//                startActivityForResult(intent, 10002);// 10002 카카오 로그인 RESULT 값
//            }
        }else{
            Intent intent = new Intent(MainActivity.this, SelectExamActivity.class);
            intent.putExtra("from", "main_activity");
            startActivityForResult(intent, 10001);
            slide_left_and_slide_in();
        }


        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("request code", String.valueOf(requestCode)+"//"+LT);
        if (requestCode == 10001){
                if (resultCode == RESULT_OK) {
                    Log.e("result 10001", "here???10001"+"//"+LT);

                    String  exam_name = data.getStringExtra("exam_name");
                    String exam_code = data.getStringExtra("exam_code");

                    exam_selection_code=exam_code;
                    exam_selection_name=exam_name;
                    exam_selection_textView.setText(exam_selection_name);
    //                getSupportActionBar().setSubtitle(exam_selection_name);

                    Toast.makeText(MainActivity.this, "mainactivity : "+ exam_name+","+exam_code, Toast.LENGTH_SHORT).show();

                    mViewPagerAdapter = new MainActivityViewPagerAdapter(getSupportFragmentManager());
                    mViewPager = (ViewPager) findViewById(R.id.container);
                    mViewPager.setAdapter(mViewPagerAdapter);
                    mViewPager.setOffscreenPageLimit(3);


                }else if(resultCode == RESULT_CANCELED){
                    Log.e("result 10001", "cancel");
                }
        }else if(requestCode == 10002){
                if (resultCode == RESULT_OK) {
                    Log.e("result 10002", "here???10002");

                    login_remember = getSharedPreferences("setting", 0);
                    editor = login_remember.edit();
                    LT = login_remember.getString("login_type", "");

                    LoginType=data.getStringExtra("login_type");
                    G_user_id = data.getStringExtra("user_id");
                    G_user_nickname = data.getStringExtra("user_nickname");
                    G_user_thumbnail = data.getStringExtra("user_thumbnail");

                    String  exam_code = data.getStringExtra("user_selected_last_exam_code");
                    String exam_name = data.getStringExtra("user_selected_last_exam_name");

                    exam_selection_code=exam_code;
                    exam_selection_name=exam_name;

                    exam_selection_textView.setText(exam_selection_name);
                    if(LoginType.equals("normal")){
                        //normal login 일때
                        logintype_textView.setText("passpop");
                        if(G_user_thumbnail.equals("null")){
                            // if there is no thumbnail
                        }else{
                            getThumbnailImageForAuthor(user_thumbnail, G_user_thumbnail);
                        }
                    }else{
                        //kakao login 일때
                        logintype_textView.setText(LoginType);
                        if(G_user_thumbnail.equals("null")){
                            // if there is no thumbnail
                        }else{
                            getThumbnailImageForAuthor(user_thumbnail, G_user_thumbnail);
                        }
                    }
                    user_thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent =new Intent(MainActivity.this, LoggedInActivity.class);
                            startActivityForResult(intent, 10003);
                            slide_left_and_slide_in();
                        }
                    });

                    mViewPagerAdapter = new MainActivityViewPagerAdapter(getSupportFragmentManager());
                    mViewPager = (ViewPager) findViewById(R.id.container);
                    mViewPager.setAdapter(mViewPagerAdapter);
                    mViewPager.setOffscreenPageLimit(3);

            }else if(resultCode == RESULT_CANCELED){
                    Log.e("result 10002", "cancel");
                }
        }else if(requestCode == 10003){
            Log.e("result 10003", String.valueOf(requestCode));

            if (resultCode == RESULT_OK) {
                Log.e("result 10003", "here???10003");

                login_remember = getSharedPreferences("setting", 0);
                editor = login_remember.edit();
                LT = login_remember.getString("login_type", "");

                user_thumbnail.setImageResource(R.drawable.user_thumbnail_icon);

                logintype_textView.setText("Guest");
                exam_selection_textView.setText("시험 선택");

                user_thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, 10002);// 10002 카카오 로그인 RESULT 값
                        slide_left_and_slide_in();
                    }
                });

                mViewPagerAdapter = new MainActivityViewPagerAdapter(getSupportFragmentManager());
                mViewPager = (ViewPager) findViewById(R.id.container);
                mViewPager.setAdapter(mViewPagerAdapter);
                mViewPager.setOffscreenPageLimit(3);

            }else if(resultCode == RESULT_CANCELED){
                Log.e("result 10003", "cancel");
            }
        }else if(resultCode == RESULT_OK){
            
        }
    }
    private void loginCheck(final String input_user_email, final String input_user_password){

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://www.storyvendingmachine.com/android/front_page_normal_login_check.php";
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
                                String user_nickname = temp.getString("user_nickname");
                                String user_thumbnail = temp.getString("user_thumbnail");

                                editor.putBoolean("id_pw_match", true);
                                editor.putString("user_email", input_user_email);
                                editor.putString("user_password", input_user_password);
                                editor.commit();


//                                Intent intent = new Intent(FrontActivity.this, MainActivity.class);
//                                intent.putExtra("login_type", "normal");
//                                intent.putExtra("user_email", user_email);
//                                intent.putExtra("user_nickname", user_nickname);
//                                intent.putExtra("user_thumbnail", user_thumbnail);
//                                startActivity(intent);
//                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                                finish();

                                LoginType = "normal";
                                G_user_id = user_email;
                                G_user_nickname=user_nickname;
                                G_user_thumbnail = user_thumbnail;


                                Log.e("check success:::", "true");
                                Log.e("login check ::::", LoginType+"//"+G_user_id);


                            }else if(login_success_fail.equals("login_fail")){

                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("존재하지 않는 사용자 입니다.\n로그인 페이지로 이동합니다")
                                        .setPositiveButton("이동", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                editor.putBoolean("id_pw_match", false);
                                                editor.putString("user_email", "");
                                                editor.putString("user_password", "");
                                                editor.commit();
//                                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                                                startActivity(intent);
//                                                finish();

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
                params.put("email", input_user_email);
                params.put("password", input_user_password);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    @Override
    public void onBackPressed() {
        Session.getCurrentSession().clearCallbacks();
        super.onBackPressed();
    }
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void slide_left_and_slide_in(){//opening new activity
        overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }
}
