package com.storyvendingmachine.www.pp;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;

public class NewsActivity extends AppCompatActivity {
    final String ENTER_METHOD_TYPE_ALL = "ALL";
    final String ENTER_METHOD_TYPE_ONE = "ONE";
    final String ENTER_METHOD_TYPE_QUIZ = "QUIZ";

    ProgressBar progressBar;
    ExpandableListView expandableListView;
    NewsActivityAdapter expandableListAdapter;


    ConstraintLayout quiz_conLayout;


    int quiz_page;
    ListView quiz_listView;
    QuizListAdapter quizListAdapter;
    List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // there are two things you must be ware!!!
        // one user will enter this activity with "더보기" menu
        //          if so it should not open any content below main list
        //          else it should open specific content below main list.

        initializer();
    }

    public void initializer(){
        TextView appbar_title_textView = (TextView) findViewById(R.id.appbar_title_textView);
        expandableListView = (ExpandableListView) findViewById(R.id.expandable_listView);
        progressBar = (ProgressBar) findViewById(R.id.NewsActivityProgressBar);
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
            quiz_listView = (ListView) findViewById(R.id.quiz_listView);
            list = new ArrayList<String>();
            quizListAdapter = new QuizListAdapter(this, list);
            quiz_listView.setAdapter(quizListAdapter);

            quiz_page =0;
            quiz_conLayout = (ConstraintLayout) findViewById(R.id.quiz_conLayout);
            quiz_conLayout.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.INVISIBLE);
            appbar_title_textView.setText("오늘의 퀴즈");
            quiz_menu_button_controll();
            getAllSelectedQuiz();
        }

    }



    // ALL THE MOTHODS UNDER THIS LINE REPRESENTS METHODS THOSE ARE RELEVANT TO QUIZ
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

                                JSONArray jsonArray  = jsonObject.getJSONArray("response");
                                for(int i = 0 ; i< jsonArray.length(); i++){
                                    String date = jsonArray.getString(i);
                                    list.add(period_between_date(date));
                                }
                                quizListAdapter.notifyDataSetChanged();
                                progressbar_invisible();
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
                params.put("exam_code",exam_selection_code);
                params.put("quiz_page", String.valueOf(quiz_page));
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void quiz_menu_button_controll(){
        final TextView quiz_menu_one = (TextView) findViewById(R.id.quiz_menu_one);
        final TextView quiz_menu_two = (TextView) findViewById(R.id.quiz_menu_two);

        quiz_menu_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quiz_page =0;
                quiz_menu_one.setBackground(getResources().getDrawable(R.drawable.underline_quiz_menu));
                quiz_menu_two.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
            }
        });
        quiz_menu_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quiz_page =0;
                quiz_menu_one.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                quiz_menu_two.setBackground(getResources().getDrawable(R.drawable.underline_quiz_menu));
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
        Toolbar tb = (Toolbar) findViewById(R.id.NewsActivityToolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
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

    // ALL THE OVERRIDE METHOD GOES UNDER THIS LINE -------------------------
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

}
