package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;
import static com.storyvendingmachine.www.pp.MainFragment.quiz_viewPager;
import static com.storyvendingmachine.www.pp.MainFragment.quizUserSelectedAnswers;

public class NewsActivity extends AppCompatActivity {
    final String ENTER_METHOD_TYPE_ALL = "ALL";
    final String ENTER_METHOD_TYPE_ONE = "ONE";
    final String ENTER_METHOD_TYPE_QUIZ = "QUIZ";

    Toolbar tb;


    ProgressBar progressBar;
    ExpandableListView expandableListView;
    NewsActivityAdapter expandableListAdapter;

    TextView appbar_title_textView;

    ConstraintLayout quiz_conLayout;
    int quiz_page;
    int TotalQuizListSize;
    ListView quiz_listView;
    QuizListAdapter quizListAdapter;
    List<QuizList> list;
    JSONArray jsonArray;
    int QUIZ_MENU_NAVI;
    SwipeRefreshLayout quiz_view_swiper;
    ConstraintLayout empty_quiz_conLayout;
    ConstraintLayout quiz_display_container;
    boolean stop;
    boolean isQuizOpened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        stop = false;
        isQuizOpened =false;
        // there are two things you must be ware!!!
        // one user will enter this activity with "더보기" menu
        //          if so it should not open any content below main list
        //          else it should open specific content below main list.

        initializer();
    }

    public void initializer(){
        appbar_title_textView = (TextView) findViewById(R.id.appbar_title_textView);
        expandableListView = (ExpandableListView) findViewById(R.id.expandable_listView);
        progressBar = (ProgressBar) findViewById(R.id.NewsActivityProgressBar);
        empty_quiz_conLayout = (ConstraintLayout) findViewById(R.id.empty_quiz_conLayout);
        toolbar();
        progressbar_visible();
        Intent intent = getIntent();
        String enter_method =  intent.getStringExtra("enter_method");
        if(enter_method.equals(ENTER_METHOD_TYPE_ALL)){
            // "더보기" 메뉴를 타고 들어왔을떄...
            ad();
            getNewsAnnouncement(-1);
        }else if(enter_method.equals(ENTER_METHOD_TYPE_ONE)){
            // 하나의 "제목" aka 뉴스, 업데이트, 소식 을 타고 들어왔을떄....
            ad();
            String key = intent.getStringExtra("key");
            getNewsAnnouncement(Integer.parseInt(key));
        }else{
            // today quiz start under here
            QUIZ_MENU_NAVI = Integer.parseInt(intent.getStringExtra("quiz_menu"));
            quiz_view_swiper = (SwipeRefreshLayout) findViewById(R.id.quiz_view_swiper);
            quiz_listView = (ListView) findViewById(R.id.quiz_listView);
            list = new ArrayList<QuizList>();
            quizListAdapter = new QuizListAdapter(this, list);
            quiz_listView.setAdapter(quizListAdapter);

            if(QUIZ_MENU_NAVI == 1){
                navi_one();
            }else{
                //2
                navi_two();
            }
        }
    }

    // ALL THE MOTHODS UNDER THIS LINE REPRESENTS METHODS THOSE ARE RELEVANT TO QUIZ
    public void navi_one(){
        swiper();
        quiz_page =0;
        quiz_conLayout = (ConstraintLayout) findViewById(R.id.quiz_conLayout);
        quiz_conLayout.setVisibility(View.VISIBLE);
        expandableListView.setVisibility(View.INVISIBLE);
        appbar_title_textView.setText("오늘의 퀴즈");
        quiz_menu_button_controll();
        getAllSelectedQuiz();

//        quiz_listView = (ListView) findViewById(R.id.quiz_listView);
//        list = new ArrayList<QuizList>();
//        quizListAdapter = new QuizListAdapter(this, list);
//        quiz_listView.setAdapter(quizListAdapter);
        quiz_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(i > 0){
                    tb.setElevation(9);
                }else{
                    tb.setElevation(0);
                }
//                if (quiz_listView.getLastVisiblePosition() == quiz_listView.getAdapter().getCount() -1 &&
//                        quiz_listView.getChildAt(quiz_listView.getChildCount() - 2).getBottom() <= quiz_listView.getHeight() &&
//                        quiz_listView.getAdapter().getCount() >0) {
//                    //It is scrolled all the way down here
//                    Log.e("scroll bottom? ", "true");
//                    progressbar_visible();
//                    new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
//                        @Override
//                        public void run() {
//                            seeMoreFooter();
//                        }
//                    }, 500);
//                }
            }
        });

//        if(quiz_listView.getFooterViewsCount() == 0){
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 130);
//            TextView footer_textView = new TextView(this);
//            footer_textView.setLayoutParams(params);
//            footer_textView.setGravity(Gravity.CENTER);
//            footer_textView.setText("더보기");
//            quiz_listView.addFooterView(footer_textView);
//        }



        quiz_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                isQuizOpened = true;
                if(i == list.size()){
                    Log.e("log listview click", String.valueOf(i));
                    progressbar_visible();
                    seeMoreFooter();
                }else{
                    try {
                        String date = jsonArray.getJSONObject(i).getString("quiz_date");
                        String count = jsonArray.getJSONObject(i).getJSONObject("data").getString("count");
                        String percent = jsonArray.getJSONObject(i).getJSONObject("data").getString("percent");
                        String fraction = jsonArray.getJSONObject(i).getJSONObject("data").getString("fraction");
                        final JSONArray jsonArrayQuizContent = jsonArray.getJSONObject(i).getJSONArray("json_data");
                        quiz_display_controll((jsonArrayQuizContent.length()+1), jsonArrayQuizContent, date);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    public void navi_two(){
        swiper();
        quiz_page =0;
        quiz_conLayout = (ConstraintLayout) findViewById(R.id.quiz_conLayout);
        quiz_conLayout.setVisibility(View.VISIBLE);
        expandableListView.setVisibility(View.INVISIBLE);
        appbar_title_textView.setText("퀴즈 성적");
        quiz_menu_button_controll();
        getQuizUserTook();

//        quiz_listView = (ListView) findViewById(R.id.quiz_listView);
//        list = new ArrayList<QuizList>();
//        quizListAdapter = new QuizListAdapter(this, list);
//        quiz_listView.setAdapter(quizListAdapter);
        quiz_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                Log.e("scroll state", String.valueOf(i));
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(i > 0){
                    tb.setElevation(9);
                }else{
                    tb.setElevation(0);
                }

//                if (quiz_listView.getLastVisiblePosition() == quiz_listView.getAdapter().getCount() -1 &&
//                        quiz_listView.getChildAt(quiz_listView.getChildCount() -1).getBottom() <= quiz_listView.getHeight() &&
//                            quiz_listView.getAdapter().getCount() >0) {
//                    //It is scrolled all the way down here
//                    Log.e("scroll bottom? ", "true");
//                    progressbar_visible();
//                    new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
//                        @Override
//                        public void run() {
//                            seeMoreFooter();
//                        }
//                    }, 500);
//                }
            }
        });

//        if(quiz_listView.getFooterViewsCount() == 0){
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 130);
//            TextView footer_textView = new TextView(this);
//            footer_textView.setLayoutParams(params);
//            footer_textView.setGravity(Gravity.CENTER);
//            footer_textView.setText("더보기");
//            quiz_listView.addFooterView(footer_textView);
//        }

        quiz_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == list.size()){
                    Log.e("log listview click", String.valueOf(i));
                    seeMoreFooter();
                }else{
                        String primary_key = list.get(i).getPrimary_key();
                        String quiz_date = list.get(i).getQuiz_date();
                        String percent = list.get(i).getPercent();
                        String fraction = list.get(i).getFraction();
                        String quiz_took_date = list.get(i).getQuiz_took_date();
                        String quiz_took_time = list.get(i).getQuiz_took_time();

                        Intent intent = new Intent(NewsActivity.this, QuizResultActivity.class);
                        intent.putExtra("isNew", "old");
                        intent.putExtra("key", primary_key);
                        startActivity(intent);
                        slide_left_and_slide_in();
                }
            }
        });
    }
    public void scroll_Listener(){
        quiz_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(i > 0){
                    tb.setElevation(9);
                }else{
                    tb.setElevation(0);
                }
                if (quiz_listView.getLastVisiblePosition() == quiz_listView.getAdapter().getCount() -1 &&
                        quiz_listView.getChildAt(quiz_listView.getChildCount() - 1).getBottom() <= quiz_listView.getHeight() &&
                        quiz_listView.getAdapter().getCount() >0) {
                    //It is scrolled all the way down here
                    Log.e("scroll bottom? ", "true");
                    if(!stop){
                        progressbar_visible();
                        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                            @Override
                            public void run() {
                                seeMoreFooter();
                            }
                        }, 500);
                    }

                }
            }
        });
    }
    public void seeMoreFooter(){
        if(QUIZ_MENU_NAVI==1){
            quiz_page+=10;
            if(TotalQuizListSize >quiz_page) {
                getAllSelectedQuiz();
            }else{
                Toast.makeText(this, "마지막 페이지 입니다.", Toast.LENGTH_SHORT).show();
                stop=true;
                progressbar_invisible();
            }
        }else{
            quiz_page+=20;
            if(TotalQuizListSize >quiz_page){
                getQuizUserTook();
            }else{
                Toast.makeText(this, "마지막 페이지 입니다.", Toast.LENGTH_SHORT).show();
                stop=true;
                progressbar_invisible();
            }
        }

    }
    public void swiper(){
        quiz_view_swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                quiz_page =0;
                progressbar_visible();
                list.clear();
                quizListAdapter.notifyDataSetChanged();
                if(QUIZ_MENU_NAVI == 1){
                    // 1
                    navi_one();
//                    getAllSelectedQuiz();
                }else{
                    // 2
                    navi_two();
//                    getQuizUserTook();
                }
                quiz_view_swiper.setRefreshing(false);

            }
        });
    }
    public void getQuizUserTook(){
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/getQuizUserTook.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("QUIZ ::" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                TotalQuizListSize= Integer.parseInt(jsonObject.getString("list_size"));
                                jsonArray  = jsonObject.getJSONArray("response");
                                if(jsonArray.length() <= 0){
                                    //퀴즈를 본적이 없다는 것.
//                                    empty_quiz_conLayout = (ConstraintLayout) findViewById(R.id.empty_quiz_conLayout);
                                    empty_quiz_conLayout.setVisibility(View.VISIBLE);
                                    quiz_view_swiper.setVisibility(View.INVISIBLE);

                                }else{
                                    for(int i = 0 ; i< jsonArray.length(); i++){
                                        String primary_key = jsonArray.getJSONObject(i).getString("primary_key");
                                        String date = jsonArray.getJSONObject(i).getString("quiz_date");
                                        String percent = jsonArray.getJSONObject(i).getString("percent");
                                        String fraction = jsonArray.getJSONObject(i).getString("fraction");
                                        String quiz_took_date = jsonArray.getJSONObject(i).getString("quiz_took_date");
                                        String quiz_took_time = jsonArray.getJSONObject(i).getString("quiz_took_time");

                                        QuizList quizList = new QuizList("my_quiz_list", primary_key, date, null, percent, fraction, quiz_took_date, quiz_took_time);
                                        list.add(quizList);
                                    }
                                    quizListAdapter.notifyDataSetChanged();
                                    scroll_Listener();
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
                params.put("exam_code",exam_selection_code);
                params.put("quiz_page", String.valueOf(quiz_page));
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void getAllSelectedQuiz(){
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/getAllSelectedQuiz.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("QUIZ ::" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                TotalQuizListSize= Integer.parseInt(jsonObject.getString("list_size"));
                                jsonArray  = jsonObject.getJSONArray("response");
                                for(int i = 0 ; i< jsonArray.length(); i++){
                                    String primary_key = jsonArray.getJSONObject(i).getString("primary_key");
                                    String date = jsonArray.getJSONObject(i).getString("quiz_date");
                                    String count = jsonArray.getJSONObject(i).getJSONObject("data").getString("count");
                                    String percent = jsonArray.getJSONObject(i).getJSONObject("data").getString("percent");
                                    String fraction = jsonArray.getJSONObject(i).getJSONObject("data").getString("fraction");
                                    JSONArray jsonArrayQuizContent = jsonArray.getJSONObject(i).getJSONArray("json_data");
                                    QuizList quizList = new QuizList("all_quiz_list", primary_key,  date, count, percent, fraction, null, null);
                                    list.add(quizList);
                                }
                                quizListAdapter.notifyDataSetChanged();
                                scroll_Listener();
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
                params.put("exam_code",exam_selection_code);
                params.put("quiz_page", String.valueOf(quiz_page));
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void quiz_display_controll(int count, JSONArray jsonArray, String today){
        quizUserSelectedAnswers = new ArrayList<Integer>();
        progressbar_visible();
        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
            @Override
            public void run() {
                progressbar_invisible();
            }
        }, 1500); // 2.5초 후에 실행됨
        quiz_display_container = (ConstraintLayout) findViewById(R.id.quiz_display_container);

        slideUp(quiz_display_container);
        ImageView close_imageView = (ImageView) findViewById(R.id.close_imageView);
        TextView close_textView = (TextView) findViewById(R.id.close_textView);

        quiz_viewPager = (ViewPager) findViewById(R.id.quiz_container);
        QuizViewPagerAdapter adapter = new QuizViewPagerAdapter(getSupportFragmentManager());
        adapter.from = "news_activity";
        adapter.count= count;
        adapter.jsonArray = jsonArray;
        adapter.today = today;
        quiz_viewPager.setAdapter(adapter);
        quiz_viewPager.setOffscreenPageLimit(count);
        quiz_viewPager.setPageMargin(8);



        close_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isQuizOpened = false;
                slideDown(quiz_display_container);
//                                                quiz_display_container.setVisibility(View.INVISIBLE);
            }
        });
        close_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isQuizOpened = false;
                slideDown(quiz_display_container);
//                                                quiz_display_container.setVisibility(View.INVISIBLE);
            }
        });
    }
    public void quiz_menu_button_controll(){
        final TextView quiz_menu_one = (TextView) findViewById(R.id.quiz_menu_one);
        final TextView quiz_menu_two = (TextView) findViewById(R.id.quiz_menu_two);

//        if(QUIZ_MENU_NAVI ==1){
//            quiz_menu_one.setBackground(getResources().getDrawable(R.drawable.underline_quiz_menu));
//            quiz_menu_two.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
//        }else{
//            quiz_menu_one.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
//            quiz_menu_two.setBackground(getResources().getDrawable(R.drawable.underline_quiz_menu));
//        }

        quiz_menu_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(empty_quiz_conLayout.getVisibility() == View.VISIBLE){
                    empty_quiz_conLayout.setVisibility(View.INVISIBLE);
                    quiz_view_swiper.setVisibility(View.VISIBLE);
                }
                QUIZ_MENU_NAVI =1;
                list.clear();
                navi_one();
                quiz_menu_one.setBackground(getResources().getDrawable(R.drawable.underline_quiz_menu));
                quiz_menu_two.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
            }
        });

        quiz_menu_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginType.equals("null") || G_user_id.equals("null")) {
                    String message = "로그인을 하셔야 사용할 수 있는 메뉴 입니다.";
                    String positive_message = "확인";
                    notifier(message, positive_message);
                }else{
                    QUIZ_MENU_NAVI =2;
                    list.clear();
                    navi_two();
                    quiz_menu_one.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                    quiz_menu_two.setBackground(getResources().getDrawable(R.drawable.underline_quiz_menu));
                }
            }
        });
    }
    public String period_between_date(String date){
        String temp = "";
        for(int i = 0 ; i < date.length(); i++){
            if(i == 3 || i ==5){
                temp += date.charAt(i)+".";
            }else {
                temp += date.charAt(i);
            }
        }
        return temp;
    }

    // ALL THE METHODS UNDER THIS LINE REPRESENTS ONLY NEWS AND ANNOUNCEMENT -----------------------------------------
    public void getNewsAnnouncement(final int key){
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/getNewsAndAnnouncement.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("NewsActivity::" , response);
                        HashMap<NewsActivityGroupList, NewsActivityItemList> hashMap = new HashMap<NewsActivityGroupList,NewsActivityItemList>();
                        List<NewsActivityGroupList> group_list = new ArrayList<NewsActivityGroupList>();
                        List<NewsActivityItemList> item_list = new ArrayList<NewsActivityItemList>();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                JSONArray jsonArray  = jsonObject.getJSONArray("response");
                                for(int i = 0 ; i < jsonArray.length(); i++){
                                    String key = jsonArray.getJSONObject(i).getString("key");
                                    String type = jsonArray.getJSONObject(i).getString("type");
                                    String title = jsonArray.getJSONObject(i).getString("title");
                                    String content = jsonArray.getJSONObject(i).getString("content");
                                    String upload_date = jsonArray.getJSONObject(i).getString("upload_date");
                                    String upload_time = jsonArray.getJSONObject(i).getString("upload_time");
                                    String isNew = jsonArray.getJSONObject(i).getString("new");
                                    NewsActivityGroupList group_element = new NewsActivityGroupList(title, type, upload_date, upload_time, isNew);
                                    group_list.add(group_element);
                                    NewsActivityItemList item_element = new NewsActivityItemList(key, type, title, content, upload_date, upload_time, isNew);
                                    item_list.add(item_element);

                                    hashMap.put(group_element, item_element);
                                }
                                expandableListAdapter = new NewsActivityAdapter(NewsActivity.this, group_list, hashMap);
                                expandableListView.setAdapter(expandableListAdapter);
                                if(key != -1){
                                    expandableListView.expandGroup(key);
                                }
                            }else if(access_token.equals("invalid")){

                            }else{

                            }
                            progressbar_invisible();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
                //                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
                //                        toast(message);
                //                        getExamNameAndCode(); // 인터넷 에러가 났을시 다시 한번 시도한다.

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("limit", "false");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    // ALL THE METHOD UNDER THIS LINE REPRESENTS GLOBAL METHOD THAT CAN BE USED COMMONLY ON NEW OR QUIZ
    private void toolbar(){
        tb = (Toolbar) findViewById(R.id.NewsActivityToolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
    }

    public void notifier(String message, String positive_message){
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
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
//        animate.setFillAfter(true);
        view.startAnimation(animate);
    }
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
//        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.INVISIBLE);
    }
    public void slide_left_and_slide_in(){
        overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }

    // ALL THE OVERRIDE METHOD GOES UNDER THIS LINE -------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        if(isQuizOpened){
            slideDown(quiz_display_container);
            isQuizOpened = false;
        }else{
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out);// first entering // second exiting
        }
    }

    //BELOW THIS LINE IS FOR ADVERTISEMENT
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
    public class QuizList{
        String list_type;
        String primary_key;
        String quiz_date;
        String count;
        String percent;
        String fraction;
        String quiz_took_date;
        String quiz_took_time;

        public String getList_type() {
            return list_type;
        }

        public void setList_type(String list_type) {
            this.list_type = list_type;
        }

        public String getPrimary_key() {
            return primary_key;
        }

        public void setPrimary_key(String primary_key) {
            this.primary_key = primary_key;
        }

        public String getQuiz_date() {
            return quiz_date;
        }

        public void setQuiz_date(String quiz_date) {
            this.quiz_date = quiz_date;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getPercent() {
            return percent;
        }

        public void setPercent(String percent) {
            this.percent = percent;
        }

        public String getFraction() {
            return fraction;
        }

        public void setFraction(String fraction) {
            this.fraction = fraction;
        }

        public String getQuiz_took_date() {
            return quiz_took_date;
        }

        public void setQuiz_took_date(String quiz_took_date) {
            this.quiz_took_date = quiz_took_date;
        }

        public String getQuiz_took_time() {
            return quiz_took_time;
        }

        public void setQuiz_took_time(String quiz_took_time) {
            this.quiz_took_time = quiz_took_time;
        }

        public QuizList(String list_type, String primary_key, String quiz_date, String count, String percent, String fraction, String quiz_took_date, String quiz_took_time) {
            this.list_type = list_type;
            this.primary_key = primary_key;
            this.quiz_date = quiz_date;
            this.count = count;
            this.percent = percent;
            this.fraction = fraction;
            this.quiz_took_date = quiz_took_date;
            this.quiz_took_time = quiz_took_time;
        }
    }

}
