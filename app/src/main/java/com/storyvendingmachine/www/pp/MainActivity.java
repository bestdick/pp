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
import android.support.v4.app.FragmentManager;
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

    final static int REQUEST_CODE_FOR_TESTFRAGMENT = 22001;
    final static int REQUEST_CODE_FOR_FLASHCARDFRAGMENT = 22002;
    final static int REQUEST_CODE_FOR_FLASHCARD_WRITE = 22011; // write

    final static int REQUEST_CODE_FOR_SUGGESTION = 22022;



    int requestCode_flashcardwrite = 10004;

    SharedPreferences login_remember;
    SharedPreferences.Editor editor;
    String LT;

    static FloatingActionButton fab;

    static String major_exam_type_code;
    static String exam_selection_code;
    static String exam_selection_name;

    static String LoginType;
    static String G_user_id;
    static String G_user_level;
    static String G_user_nickname;
    static String G_user_thumbnail;

    FragmentManager MainfragmentManager;
    private MainActivityViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;

    TextView logintype_textView;
    TextView exam_selection_textView;
    ImageView user_thumbnail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent getIntent = getIntent();
        major_exam_type_code = getIntent.getStringExtra("user_selected_last_major_exam");
        if(major_exam_type_code.equals("lawyer")){
            setTheme(R.style.PassPopLawTheme);
        }else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission_global_instance();

        logintype_textView = (TextView) findViewById(R.id.login_type_textview);// logintype textview
        exam_selection_textView = (TextView) findViewById(R.id.exam_selection_textView); // exam selection textView
        user_thumbnail = (ImageView) findViewById(R.id.user_thumbnail_imageView);


        login_remember = getSharedPreferences("setting", 0);
        editor = login_remember.edit();
        LT = login_remember.getString("login_type", "");
        if(LT.equals("kakao")){
            if(major_exam_type_code.equals("lawyer")){
                LoginType = getIntent.getStringExtra("login_type");
                G_user_id = getIntent.getStringExtra("user_id");
                G_user_level = "one";
                G_user_nickname =getIntent.getStringExtra("user_nickname");
                G_user_thumbnail = getIntent.getStringExtra("user_thumbnail");

                exam_selection_code = getIntent.getStringExtra("user_selected_last_exam_code");
                exam_selection_name = getIntent.getStringExtra("user_selected_last_exam_name");

                lawyer_toolbar();
                app_start_fragment_initializer();
            }else{
                toolbar();
                sugs_gs_viewPagerControll();
                LoginType = getIntent.getStringExtra("login_type");
                G_user_id = getIntent.getStringExtra("user_id");
                G_user_level = "one";
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
            }
        }else if(LT.equals("normal")){
//            Intent getIntent = getIntent();
//            major_exam_type_code = getIntent.getStringExtra("user_selected_last_major_exam");
            if(major_exam_type_code.equals("lawyer")){
                LoginType = getIntent.getStringExtra("login_type");
                G_user_id = getIntent.getStringExtra("user_id");
                G_user_level = getIntent.getStringExtra("member_level");
                G_user_nickname =getIntent.getStringExtra("user_nickname");
                G_user_thumbnail = getIntent.getStringExtra("user_thumbnail");

                exam_selection_code = getIntent.getStringExtra("user_selected_last_exam_code");
                exam_selection_name = getIntent.getStringExtra("user_selected_last_exam_name");

                lawyer_toolbar();
                app_start_fragment_initializer();

            }else{
                toolbar();
                sugs_gs_viewPagerControll();
                LoginType = getIntent.getStringExtra("login_type");
                G_user_id = getIntent.getStringExtra("user_id");
                G_user_level = getIntent.getStringExtra("member_level");
                G_user_nickname =getIntent.getStringExtra("user_nickname");
                G_user_thumbnail = getIntent.getStringExtra("user_thumbnail");

                exam_selection_code = getIntent.getStringExtra("user_selected_last_exam_code");
                exam_selection_name = getIntent.getStringExtra("user_selected_last_exam_name");

                logintype_textView.setText("passpop");

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
            }
        }else{
//            Intent getIntent = getIntent();
//            major_exam_type_code = getIntent.getStringExtra("user_selected_last_major_exam");
            if(major_exam_type_code.equals("lawyer")){
                LoginType = "null";
                G_user_id = "null";
                G_user_level = "null";
                G_user_nickname ="null";
                G_user_thumbnail = "null";

                exam_selection_name ="null";
                exam_selection_code ="null";


                lawyer_toolbar();
                app_start_fragment_initializer();
            }else{
                toolbar();
                sugs_gs_viewPagerControll();
                LoginType = "null";
                G_user_id = "null";
                G_user_level = "null";
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
        }







    }


    public void permission_global_instance(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 0);
    }
    public void lawyer_toolbar(){
        logintype_textView.setTextColor(getResources().getColor(R.color.colorCrimsonRed));
        if(LoginType.equals("kakao")){
            logintype_textView.setText("kakao");
            user_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(MainActivity.this, LoggedInActivity.class);
                    startActivityForResult(intent, 10003);
                    slide_left_and_slide_in();
                }
            });
        }else if(LoginType.equals("normal")){
            logintype_textView.setText("passpop");
            getThumbnailImageForAuthor(user_thumbnail, G_user_thumbnail);
            user_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(MainActivity.this, LoggedInActivity.class);
                    startActivityForResult(intent, 10003);
                    slide_left_and_slide_in();
                }
            });
        }else{
            logintype_textView.setText("guest");
            user_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 10002);// 10002 카카오 로그인 RESULT 값
                    slide_left_and_slide_in();
                }
            });
        }
        if(G_user_thumbnail.equals("null") || G_user_thumbnail == null){
            user_thumbnail.setImageDrawable(getResources().getDrawable(R.drawable.icon_law_thumbnail));
        }else{
            getThumbnailImageForAuthor(user_thumbnail, G_user_thumbnail);
        }
        exam_selection_textView.setText("#변호사시험");
        exam_selection_textView.setTextColor(getResources().getColor(R.color.colorCrimsonRed));

        law_view_pager_control();
    }
    public void app_start_fragment_initializer(){
        LawHomeFragment homeFragment = new LawHomeFragment();
        homeFragment.setArguments(null);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, homeFragment)
                .commit();

    }
    public void law_view_pager_control(){
        TabLayout tabLayout = (TabLayout) findViewById(R.id.MainActivityTabs);
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("Exam"));
        tabLayout.addTab(tabLayout.newTab().setText("Study"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tab_position = tab.getPosition();

                String title = "";
                Fragment fragment = null;
                switch (tab_position){
                    case 0:
//                        title = "#홈";
//                        toolbarTitle(title);
                        fragment = new LawHomeFragment();
//                        detach_attach_fragment(homeFragment);
//                        last_fragment= 0;
                        break;
                    case 1:
//                        title = "#기출문제";
//                        toolbarTitle(title);
//                        detach_attach_fragment(examFragment);
                        fragment = new LawExamFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("param1", "one");
                        bundle.putString("param2", "two");
                        fragment.setArguments(bundle);
//                        last_fragment= 1;
                        break;
                    case 2:
//                        title = "#스터디";
//                        toolbarTitle(title);
//                        detach_attach_fragment(studyFragment);
//                        fragment = new StudyFragment();
//                        last_fragment= 2;
                        break;
                }
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                Log.e("tab selected", String.valueOf(tab_position));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }



























    private void toolbar(){
        Toolbar tb = (Toolbar) findViewById(R.id.MainToolBar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_exam_select);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
        getSupportActionBar().setSubtitle("시험 선택");
    }
    public void UIChange(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mViewPagerAdapter.notifyDataSetChanged();
            }
        });
    }


    public void sugs_gs_viewPagerControll(){

        MainfragmentManager = getSupportFragmentManager();
        mViewPagerAdapter = new MainActivityViewPagerAdapter(MainfragmentManager);
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
                                intent.putExtra("type", "new");
                                startActivityForResult(intent, REQUEST_CODE_FOR_FLASHCARD_WRITE);
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


        TabLayout tabLayout = (TabLayout) findViewById(R.id.MainActivityTabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        tabLayout.getTabAt(0).setIcon(R.drawable.icon_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.icon_exam);
        tabLayout.getTabAt(2).setIcon(R.drawable.icon_flashcard);
        tabLayout.getTabAt(3).setIcon(R.drawable.icon_mypage);

        UIChange();
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
            //시험을 고른 후의 setting
                if (resultCode == RESULT_OK) {
                    Log.e("result 10001", "here???10001"+"//"+LT);

                    String  exam_name = data.getStringExtra("exam_name");
                    String exam_code = data.getStringExtra("exam_code");

                    exam_selection_code=exam_code;
                    exam_selection_name=exam_name;
                    exam_selection_textView.setText(exam_selection_name);
    //                getSupportActionBar().setSubtitle(exam_selection_name);

                    Toast.makeText(MainActivity.this, "mainactivity : "+ exam_name+","+exam_code, Toast.LENGTH_SHORT).show();
                    removeFragmentInSupportManager();
                    mViewPagerAdapter = new MainActivityViewPagerAdapter(MainfragmentManager);
                    mViewPager = (ViewPager) findViewById(R.id.container);
                    mViewPager.setAdapter(mViewPagerAdapter);
                    mViewPager.setOffscreenPageLimit(3);


                }else if(resultCode == RESULT_CANCELED){
                    Log.e("result 10001", "cancel");
                }
        }else if(requestCode == 10002){
            // 로그인 후 돌아왔을때 request
                if (resultCode == RESULT_OK) {
                    Log.e("result 10002", "here???10002");

                    login_remember = getSharedPreferences("setting", 0);
                    editor = login_remember.edit();
                    LT = login_remember.getString("login_type", "");

                    LoginType=data.getStringExtra("login_type");
//                    G_user_id = data.getStringExtra("user_id");
//
//                    G_user_nickname = data.getStringExtra("user_nickname");
//                    G_user_thumbnail = data.getStringExtra("user_thumbnail");

                    String  exam_code = data.getStringExtra("user_selected_last_exam_code");
                    String exam_name = data.getStringExtra("user_selected_last_exam_name");

                    exam_selection_code=exam_code;
                    exam_selection_name=exam_name;
                    if(exam_selection_code.equals("null") || exam_selection_code ==null){
                        exam_selection_textView.setText("전체 선택");
                    }else{
                        exam_selection_textView.setText(exam_selection_name);
                    }

                    if(LoginType.equals("normal")){
                        //normal login 일때
                        G_user_id = data.getStringExtra("user_id");
                        G_user_nickname = data.getStringExtra("user_nickname");
                        G_user_thumbnail = data.getStringExtra("user_thumbnail");
                        G_user_level = data.getStringExtra("user_level");
                        logintype_textView.setText("passpop");
                        if(G_user_thumbnail.equals("null")){
                            // if there is no thumbnail
                        }else{
                            getThumbnailImageForAuthor(user_thumbnail, G_user_thumbnail);
                        }
                    }else{
                        //kakao login 일때
                        G_user_id = data.getStringExtra("user_id");
                        G_user_nickname = data.getStringExtra("user_nickname");
                        G_user_thumbnail = data.getStringExtra("user_thumbnail");
                        G_user_level = "one";
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
                    removeFragmentInSupportManager();
                    mViewPagerAdapter = new MainActivityViewPagerAdapter(MainfragmentManager);
                    mViewPager = (ViewPager) findViewById(R.id.container);
                    mViewPager.setAdapter(mViewPagerAdapter);
                    mViewPager.setOffscreenPageLimit(3);

            }else if(resultCode == RESULT_CANCELED){
                    Log.e("result 10002", "cancel");
                }
        }else if(requestCode == 10003){
            //this requestCode is from LoggedInActivity
            // 로그아웃 후 리퀘스트코드
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

                removeFragmentInSupportManager();
                mViewPagerAdapter = new MainActivityViewPagerAdapter(MainfragmentManager);
                mViewPager = (ViewPager) findViewById(R.id.container);
                mViewPager.setAdapter(mViewPagerAdapter);
                mViewPager.setOffscreenPageLimit(3);

            }else if(resultCode == RESULT_CANCELED){
                Log.e("result 10003", "cancel");
                if(G_user_thumbnail.equals("null") || G_user_thumbnail.length() <=0 ){
                    user_thumbnail.setImageResource(R.drawable.user_thumbnail_icon);
                }else{
                    getThumbnailImageForAuthor(user_thumbnail, G_user_thumbnail);
                }
            }
        }else if(requestCode == REQUEST_CODE_FOR_TESTFRAGMENT){
            // 기출시험을 보고나왔을때 이창이 켜진다
                //시험을 채점 까지 마친상태
            if(resultCode==RESULT_OK){
                TestFragment testFragment = (TestFragment) MainfragmentManager.getFragments().get(1);
                int menu_selection = testFragment.navi_selection;
                testFragment.testYearOrderList.clear();
                testFragment.getExamList(menu_selection);

                StatisticFragment statisticFragment = (StatisticFragment) MainfragmentManager.getFragments().get(3);
                View rootview = statisticFragment.rootview;
                if(LoginType.equals("null") || G_user_id.equals("null")){
                    //로그인 하지 않았을때.....
                    statisticFragment.guest_initializer(rootview);
                }else{
                    //로그인 한 상태
                    if(exam_selection_code.equals("null")){
                        //처음 가입했을때 시험을 선택하지 않았을떄...
                        statisticFragment.no_exam_select_initializer(rootview);
                    }else{
                        //기본 적인 메뉴 생성
                        statisticFragment.statistic_exam_result_container.removeAllViews();
                        statisticFragment.subject_result_container.removeAllViews();
                        statisticFragment.piechart_container.removeAllViews();
                        statisticFragment.Initializer(rootview);
                        statisticFragment.getExamResultData(rootview);
                    }
                }
                Log.e("test fragment", "result ok");
            }else if (resultCode == RESULT_CANCELED){
                Log.e("test fragment", "result cancel");
            }else{
                Log.e("test fragment", "result else");
            }
                Log.e("size of supportfragment", String.valueOf(MainfragmentManager.getFragments().size()));

        }else if(requestCode == REQUEST_CODE_FOR_FLASHCARD_WRITE ||
                requestCode == REQUEST_CODE_FOR_FLASHCARDFRAGMENT){
            //플래시카드 공부 또는   requestCode == REQUEST_CODE_FOR_FLASHCARDFRAGMENT  이때는 리프레쉬 시키지 않는다.
            //플래시카드 작성 후.... 작성후에만 리프레시ㅣ 시킨다.
            //단 플래시 카드 폴더는 여기서 업데이트 하지 않는다.
            if(resultCode==RESULT_OK){
                FlashcardFragment flashcardFragment = (FlashcardFragment) MainfragmentManager.getFragments().get(2);
                int menu_selection = flashcardFragment.flashcard_menu;
                if(menu_selection==0){
                    flashcardFragment.flashcard_list.clear();
                    flashcardFragment.AddHeader();
                    flashcardFragment.getFlashcardList(exam_selection_code);

                }else if (menu_selection == 1){
                    flashcardFragment.flashcard_list.clear();
                    flashcardFragment.AddHeader();
                    flashcardFragment.getFlashcardList(exam_selection_code);
                }else if(menu_selection == 2){

                }else {
                    flashcardFragment.flashcard_list.clear();
                    flashcardFragment.AddHeader();
                    flashcardFragment.getFlashcardList(exam_selection_code);
                }
            }else if(resultCode==RESULT_CANCELED){
                Log.e("flashcard fragment", "result cancel");
            }else{
                Log.e("flashcard fragment", "result other");
            }

//            mViewPagerAdapter = new MainActivityViewPagerAdapter(MainfragmentManager);
//            mViewPager = (ViewPager) findViewById(R.id.container);
//            mViewPager.setAdapter(mViewPagerAdapter);
//            mViewPager.setOffscreenPageLimit(3);
//            mViewPager.setCurrentItem(2);
//              Log.e("from fragment", "fragment or write");
        }else if(requestCode == REQUEST_CODE_FOR_SUGGESTION){
            MainFragment mainFragment = (MainFragment) MainfragmentManager.getFragments().get(0);
            mainFragment.today_quiz_wrapper.removeAllViews();
            mainFragment.suggestion_wrapper.removeAllViews();
            mainFragment.news_wrapper.removeAllViews();
            mainFragment.getShortTodayQuiz();
//            mainFragment.getNewsAndAnnouncement_Suggestion();
            Log.e("suggestion ", "result other_!");
        }else{
            Log.e("suggestion ", "result other_2");
        }
    }
    public void removeFragmentInSupportManager(){
        for(int i = 0 ; i< MainfragmentManager.getFragments().size(); i++){
            MainfragmentManager.beginTransaction().remove(MainfragmentManager.getFragments().get(i)).commit();
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
                                String user_level = temp.getString("member_level");
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
                                G_user_level = user_level;
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
