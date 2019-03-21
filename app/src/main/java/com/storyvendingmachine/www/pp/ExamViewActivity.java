package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.inthecheesefactory.thecheeselibrary.fragment.bus.ActivityResultBus;
import com.inthecheesefactory.thecheeselibrary.fragment.bus.ActivityResultEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.storyvendingmachine.www.pp.Allurl.base_url;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;

public class ExamViewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    final int NOTE_REQUEST_CODE = 40002;
    DrawerLayout drawer;
    NavigationView navigationView;
    LinearLayout answer_sheet_element_layout;

    static ProgressBar ExamView_progressBar;
    LinearLayout progressBarBackground;

    String exam_name, exam_code, published_year, published_round;
    static String navi_selection;
    static List<Integer> answer;
    static ExamViewActivityViewPagerAdaper eViewPagerAdapter;
    static ViewPager eviewPager;


    LawExamViewTypeAViewPagerAdapter examViewTypeAViewPagerAdapter;



    JSONArray resultJSONarray;
    JSONArray examNoteJSONArray;

    JSONArray userPersonalNoteArray;


    static String[] note_array;
    static ArrayList<String> note_array_list;


    String refresh_upload_prevent;
    Button submit_button;


    String exam_major_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent getIntent = getIntent();
        exam_major_type = getIntent.getStringExtra("exam_major_type");
        if(exam_major_type.equals("lawyer")){
            setTheme(R.style.PassPopLawTheme);
        }else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_view);

        if(exam_major_type.equals("lawyer")){
            navi_selection = getIntent.getStringExtra("navi_selection");
            String exam_placed_year = getIntent.getStringExtra("exam_placed_year");
            String major_type = getIntent.getStringExtra("major_type");
            String minor_type = getIntent.getStringExtra("minor_type");

            String title_message = exam_placed_year+"년도 "+LAW_major_minor_to_kor(major_type)+" "+LAW_major_minor_to_kor(minor_type);
            toolbar( exam_major_type, title_message);
            drawer = (DrawerLayout) findViewById(R.id.drawer);
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            answer_sheet_element_layout = (LinearLayout) navigationView.findViewById(R.id.answer_element_layout);
            ExamView_progressBar = (ProgressBar) findViewById(R.id.read_progress_bar);
            if(navi_selection.equals("1")){
                progressbar_visible();

                submit_button = (Button) findViewById(R.id.submit_textView);
                submit_button.setBackgroundColor(getResources().getColor(R.color.colorCrimsonRed));
                submit_button.setTextColor(getResources().getColor(R.color.colorWhite));

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                Date date = new Date();
                refresh_upload_prevent = dateFormat.format(date);

                answer = new ArrayList<Integer>();
                LAW_getSelectedExam(exam_placed_year, major_type, minor_type);
                drawer_listener();
            }else{
                // navi_selection.equals("2")
                submit_button = (Button) findViewById(R.id.submit_textView);
                submit_button.setVisibility(View.GONE);

                LAW_getSelectedExam(exam_placed_year, major_type, minor_type);
                drawer_listener();
            }
        }else{
            navi_selection = getIntent.getStringExtra("navi_selection");
            exam_name = getIntent.getStringExtra("exam_name");
            exam_code = getIntent.getStringExtra("exam_code");
            published_year = getIntent.getStringExtra("published_year");
            published_round = getIntent.getStringExtra("published_round");


            submit_button = (Button) findViewById(R.id.submit_textView);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date date = new Date();
            refresh_upload_prevent = dateFormat.format(date);

            ExamView_progressBar = (ProgressBar) findViewById(R.id.read_progress_bar);
            progressBarBackground = (LinearLayout) findViewById(R.id.progress_bar_background);
//            progressbar_visible();
            new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                @Override
                public void run() {
                    progressbar_invisible();
                }
            }, 2500);
    //         2초후에 progressbar 없애기

            if(navi_selection.equals("1")){// 1 일때는 기출시험 문제 풀이
                //기출시험 문제 풀이
                drawer = (DrawerLayout) findViewById(R.id.drawer);
                //handling navigation view item event
                navigationView = (NavigationView) findViewById(R.id.nav_view);

                navigationView.setNavigationItemSelectedListener(this);
                answer_sheet_element_layout= (LinearLayout) navigationView.findViewById(R.id.answer_element_layout);

                answer = new ArrayList<Integer>();
                getSelectedExam();
                String title_message = published_year+" 년도 "+published_round+" 회 "+exam_name;
                toolbar(exam_major_type, title_message);

                drawer_listener();

                submit_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean is = checkifAllAnswersAreSelected();
                        if(is){
                            String message = "채점 하시겠습니까?";
                            String pos_message = "네";
                            String neg_message = "아니요";
                            notifier_examSubmitButtonProcess(message, pos_message, neg_message);
                        }else{
                            String message = "답을 선택하지 않은 문제가 존재합니다.\n그래도 채점 하시겠습니까?";
                            String pos_message = "네";
                            String neg_message = "아니요";
                            notifier_examSubmitButtonProcess(message, pos_message, neg_message);
                        }
                    }
                });

            }else { // else 는 2 밖에 없기때문에 우선 else 로 둔다.
                //기출 시험 공부
                submit_button.setVisibility(View.GONE);
                drawer = (DrawerLayout) findViewById(R.id.drawer);

                navigationView = (NavigationView) findViewById(R.id.nav_view);
    //navigation drawer size
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
    //            params.width = metrics.widthPixels;  // full size of drawer
                params.width = (int) (metrics.widthPixels*0.9);
                navigationView.setLayoutParams(params);
    //navigation drawer size

                navigationView.setNavigationItemSelectedListener(this);
                answer_sheet_element_layout = (LinearLayout) navigationView.findViewById(R.id.answer_element_layout);

                String title_message = published_year+" 년도 "+published_round+" 회 "+exam_name+" 시험 공부";
                toolbar(exam_major_type, title_message);
                answer = new ArrayList<Integer>();

                getExamNote(exam_code, published_year, published_round);

                Toast.makeText(this, "Exam selection study exam", Toast.LENGTH_SHORT).show();
                drawer_listener();
            }
        }
    }

    public void drawer_listener(){
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                Log.e("drawer state ", "on slide");
            }
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if(exam_major_type.equals("lawyer")){
                    if(navi_selection.equals("1")){
                        LAW_makeAnswerSheet();
                    }else{

                    }
                }else{
                    if(navi_selection.equals("1")){
                        // 기출 풀기
                        makeAnswerSheet();
                    }else{
                        //기출 공부
                        if(LoginType.equals("null")){
                            //로그인을 하지 않은 상태
                        }else{
                            //로그인 한 상태
                            progressbar_visible();
                            answer_sheet_element_layout.removeAllViews();
                            try {
                                makeUserPersonalNoteSheet_renew();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                Log.e("drawer state ", "on change");
//                Toast.makeText(ExamViewActivity.this, "on change", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void toolbar(String major_exam_type, String title_message){
        if(major_exam_type.equals("lawyer")){
            Toolbar tb = (Toolbar) findViewById(R.id.loggedin_toolBar);
            tb.setElevation(0);
            tb.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
            getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리

            TextView exam_title_TextView = (TextView) tb.findViewById(R.id.exam_titleTextView);
            exam_title_TextView.setText(title_message);
        }else{
            Toolbar tb = (Toolbar) findViewById(R.id.loggedin_toolBar);
            tb.setElevation(5);
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
            getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리

            TextView exam_title_TextView = (TextView) tb.findViewById(R.id.exam_titleTextView);
            exam_title_TextView.setText(title_message);
        }
    }
    public void getSelectedExam(){
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/getSelectedExam.php";
        RequestQueue queue = Volley.newRequestQueue(ExamViewActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("exam list exam" , response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            // Toast.makeText(SelectExamActivity.this,access_token, Toast.LENGTH_SHORT).show();
                            if(access_token.equals("valid")){

                                int count = Integer.parseInt(jsonObject.getString("count"));
                                note_array = new String[count];

                                JSONArray jsonArray = jsonObject.getJSONArray("response");
                                resultJSONarray  = jsonArray;

                                eViewPagerAdapter = new ExamViewActivityViewPagerAdaper(getSupportFragmentManager());
                                eViewPagerAdapter.count =count;
                                eViewPagerAdapter.jsonArray = jsonArray;
                                eViewPagerAdapter.naviSelection = navi_selection;
                                eViewPagerAdapter.examNoteJSONArray = examNoteJSONArray;


                                eviewPager = (ViewPager) findViewById(R.id.container);
                                eviewPager.setAdapter(eViewPagerAdapter);
                                eviewPager.setOffscreenPageLimit(count);
                                eviewPager.setPageMargin(8);


                                if(navi_selection.equals("1")){
                                    // 1 은 기출시험
                                    TextView title_textView = new TextView(ExamViewActivity.this);
                                    title_textView.setBackground(getResources().getDrawable(R.drawable.outline_round_orange));
                                    title_textView.setGravity(Gravity.CENTER);
                                    title_textView.setText("답안지");
                                    answer_sheet_element_layout.addView(title_textView);

                                    for(int i =0 ; i<count; i++){
                                        final int ii = i;
                                        View answer_sheet_element = getLayoutInflater().inflate(R.layout.answer_sheet_element, null);
                                        answer_sheet_element.setId(i);

                                        TextView questionNumber = (TextView) answer_sheet_element.findViewById(R.id.questionNumber_textView);
                                        questionNumber.setText(String.valueOf(i+1)+".");

                                        ConstraintLayout layout = (ConstraintLayout) answer_sheet_element.findViewById(R.id.answer_sheet_element_container);
                                        layout.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Log.e("page", "page:::"+String.valueOf(ii));
                                                eviewPager.setCurrentItem(ii);
                                                drawer.closeDrawer(Gravity.RIGHT);
                                            }
                                        });

                                        answer_sheet_element_layout.addView(answer_sheet_element);

                                    }
                                    Button submit_button = new Button(ExamViewActivity.this);
                                    examSubmitButtonProcess(submit_button);// 시험 종료 및 체점 버튼 프로세스
                                    answer_sheet_element_layout.addView(submit_button);



                                }else{
                                    //2 or else 기출 공부!!
                                    if(LoginType.equals("kakao") || LoginType.equals("normal")){
                                        makeUserPersonalNoteSheet();
                                    }else{
                                        TextView title_textView = new TextView(ExamViewActivity.this);
                                        title_textView.setBackground(getResources().getDrawable(R.drawable.outline_round_orange));
                                        title_textView.setGravity(Gravity.CENTER);
                                        title_textView.setText("시험 노트");
                                        answer_sheet_element_layout.addView(title_textView);

                                        View personal_note_view = getLayoutInflater().inflate(R.layout.examview_exam_note_personal_container, null);
                                        TextView personal_note = (TextView) personal_note_view.findViewById(R.id.user_personal_note_textView);
                                        personal_note.setText("로그인 하셔서 더 많은 혜택을 받아보세요 \n시험 노트는 로그인 하셔야 작성 및 수정 이 가능합니다.");

                                        answer_sheet_element_layout.addView(personal_note_view);
                                    }

                                }


//                                progressbar_invisible();
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
                    params.put("exam_code", exam_code);
                    params.put("exam_placed_year", published_year);
                    params.put("exam_placed_round", published_round);
                    return params;
            }
        };
                    queue.add(stringRequest);
    }
    public void getExamNote(final String exam_code, final String exam_placed_year, final String exam_placed_round){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/getExamNote.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("exam note ::" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
//                                examNoteJSONArray.put(jsonObject.getJSONObject("user_personal_note"));
                                examNoteJSONArray = jsonObject.getJSONArray("response");
                                if(LoginType.equals("kakao") || LoginType.equals("normal")){
                                    userPersonalNoteArray = jsonObject.getJSONObject("user_personal_note").getJSONArray("note");
                                }else{

                                }
                                getSelectedExam();
                            }else if(access_token.equals("invalid")){

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
                        //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
//                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
//                        toast(message);
//                        getExamNameAndCode(); // 인터넷 에러가 났을시 다시 한번 시도한다.
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("exam_code", exam_code);
                params.put("exam_placed_year", exam_placed_year);
                params.put("exam_placed_round", exam_placed_round);

                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void slider_note_select_notifier(int count_button, String message, String positive_message, String negative_message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(count_button == 2){
            builder.setMessage(message)
                    .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton(negative_message, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create()
                    .show();
        }else if(count_button ==1){
            builder.setMessage(message)
                    .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create()
                    .show();
        }


    }
    public void notifier_slider_move_write_revise_note(String message, String positive_message, String negative_message, final int note_number){
        AlertDialog.Builder builder = new AlertDialog.Builder(ExamViewActivity.this);
        builder.setMessage(message)
//                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        //positive button
//                        drawer.closeDrawer(Gravity.RIGHT);
//                        int page = note_number - 10001;// 10000을 빼주면 page 가 나온다
//                        Intent intent = new Intent(ExamViewActivity.this, ExamNoteWriteActivity.class);
//                        intent.putExtra("type", "note_write");
//                        intent.putExtra("exam_code", exam_code);
//                        intent.putExtra("exam_name", exam_name);
//                        intent.putExtra("exam_placed_round", published_round);
//                        intent.putExtra("exam_placed_year", published_year);
//                        intent.putExtra("note_number", String.valueOf(page));
////                        startActivityForResult(intent, NOTE_REQUEST_CODE);
//                        startActivity(intent);
//                        slide_left_and_slide_in();
//
//                    }
//                })
                .setNegativeButton(negative_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //negative button
                        int page = note_number - 10001;// 10000을 빼주면 page 가 나온다
                        eviewPager.setCurrentItem(page);
                        drawer.closeDrawer(Gravity.RIGHT);
                    }
                })
                .create()
                .show();
    }
    public void makeUserPersonalNoteSheet_renew() throws JSONException{ // 일단 이 매소드만 생각하면 된다  renew 말고는 일단 필요없음

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,16);
        TextView title_textView = new TextView(ExamViewActivity.this);
        title_textView.setBackground(getResources().getDrawable(R.drawable.outline_round_orange));
        title_textView.setGravity(Gravity.CENTER);
        title_textView.setLayoutParams(params);
        title_textView.setText("시험 노트");
        answer_sheet_element_layout.addView(title_textView);

        for(int i = 0 ; i< note_array.length; i++){
//            String question = resultJSONarray.getJSONObject(i).getString("question_question");
//            String answers = resultJSONarray.getJSONObject(i).getString("question_answer");
//            String correct_answer = resultJSONarray.getJSONObject(i).getString("correct_answer");
//            String question_Q_image_exist = resultJSONarray.getJSONObject(i).getString("question_Q_image");
//            String question_A_image_exist = resultJSONarray.getJSONObject(i).getString("question_A_image");
//            String example_exist = resultJSONarray.getJSONObject(i).getString("example_exist");

            final View personal_note_view = getLayoutInflater().inflate(R.layout.examview_exam_note_personal_container, null);
            personal_note_view.setId(10000+(i+1));// slider 로 나오는 노트의 아이디는 100000 으로시작한다
            TextView personal_note_number = (TextView) personal_note_view.findViewById(R.id.question_number_textView);
            TextView personal_note = (TextView) personal_note_view.findViewById(R.id.user_personal_note_textView);

            String note = note_array[i];
            if(note.equals("null") || note.length() <=0){
                personal_note_number.setText("노트 ["+(i+1) +"]");
                personal_note.setText("작성되 노트가 없습니다.");
            }else{
                personal_note_number.setText("노트 ["+(i+1) +"]");
                personal_note.setText(note_array[i]);
            }

            personal_note_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = view.getId();
//                    String message = "해당 번호로 이동하시겠습니까? \n 아니면, 노트작성 및 수정 하시겠습니까?";
                    String message = "해당 번호로 이동하시겠습니까?";
                    String positive_message = "노트 작성 및 수정";
                    String negative_message = "이동";
                    notifier_slider_move_write_revise_note(message, positive_message, negative_message, id);

                }
            });

            answer_sheet_element_layout.addView(personal_note_view);
        }
        progressbar_invisible();
    }
    public void makeUserPersonalNoteSheet() throws JSONException {
        TextView title_textView = new TextView(ExamViewActivity.this);
        title_textView.setBackground(getResources().getDrawable(R.drawable.outline_round_orange));
        title_textView.setGravity(Gravity.CENTER);
        title_textView.setText("시험 노트");
        answer_sheet_element_layout.addView(title_textView);

//    int size = answer.size();
//    note_array = new String[size];

    for(int i = 0 ; i<note_array.length; i++){
        if(i<userPersonalNoteArray.length()){
            final View personal_note_view = getLayoutInflater().inflate(R.layout.examview_exam_note_personal_container, null);
            personal_note_view.setId(10000+(i+1));// slider 로 나오는 노트의 아이디는 100000 으로시작한다
            TextView personal_note = (TextView) personal_note_view.findViewById(R.id.user_personal_note_textView);
            String note = userPersonalNoteArray.getString(i).toString();
            if(note.equals("null") || note.length() <=0){
                personal_note.setText("["+(i+1) +"] \n 작성되 노트가 없습니다.");

                note_array[i] = "null";

            }else{
                personal_note.setText("["+(i+1) +"] \n" + userPersonalNoteArray.getString(i).toString());

                note_array[i] = userPersonalNoteArray.getString(i);

            }

            personal_note_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = view.getId();
                    String message = "해당 번호로 이동하시겠습니까? \n 아니면, 노트작성 및 수정 하시겠습니까?";
                    String positive_message = "노트 작성 및 수정";
                    String negative_message = "이동";
                    notifier_slider_move_write_revise_note(message, positive_message, negative_message, id);

                }
            });

            answer_sheet_element_layout.addView(personal_note_view);

        }else{
            final View personal_note_view = getLayoutInflater().inflate(R.layout.examview_exam_note_personal_container, null);
            personal_note_view.setId(10000+(i+1));// slider 로 나오는 노트의 아이디는 100000 으로시작한다
            TextView personal_note = (TextView) personal_note_view.findViewById(R.id.user_personal_note_textView);
            personal_note.setText("["+(i+1) +"] \n 작성되 노트가 없습니다.");

            note_array[i] = "null";

            personal_note_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = view.getId();
                    String message = "해당 번호로 이동하시겠습니까? \n 아니면, 노트작성 및 수정 하시겠습니까?";
                    String positive_message = "노트 작성 및 수정";
                    String negative_message = "이동";
                    notifier_slider_move_write_revise_note(message, positive_message, negative_message, id);


                }
            });

            answer_sheet_element_layout.addView(personal_note_view);
        }
    }


}
    public void makeAnswerSheet(){// 기출 시험에만 적용되는것
        int size = answer.size();
        for(int i = 0; i<size; i++){
            View v = findViewById(i);
            TextView one = (TextView) v.findViewById(R.id.answerChoice_1_textView);
            TextView two = (TextView) v.findViewById(R.id.answerChoice_2_textView);
            TextView three = (TextView) v.findViewById(R.id.answerChoice_3_textView);
            TextView four = (TextView) v.findViewById(R.id.answerChoice_4_textView);

            int answer1 = answer.get(i);

            if(answer1 ==1){
                one.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                two.setBackground(null);
                three.setBackground(null);
                four.setBackground(null);
            }else if(answer1==2){
                one.setBackground(null);
                two.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                three.setBackground(null);
                four.setBackground(null);
            }else if(answer1==3){
                one.setBackground(null);
                two.setBackground(null);
                three.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                four.setBackground(null);
            }else if(answer1==4){
                one.setBackground(null);
                two.setBackground(null);
                three.setBackground(null);
                four.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
            }
        }
    }
    public void notifier_examSubmitButtonProcess(String message, String positive_message, String negative_message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ExamViewActivity.this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //positive button
                        temp();
                        drawer.closeDrawer(Gravity.RIGHT);
                    }
                })
                .setNegativeButton(negative_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //negative button

                        drawer.closeDrawer(Gravity.RIGHT);
                    }
                })
                .create()
                .show();
    }
    public void examSubmitButtonProcess(Button submitButton){// 기출 시험에만 적용되는것
        submitButton.setText("시험 종료 및 체점");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,20,0,20);
        submitButton.setLayoutParams(params);
        submitButton.setBackground(getResources().getDrawable(R.drawable.solid_round_orange));
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean is = checkifAllAnswersAreSelected();
                if(is){
                    String message = "채점 하시겠습니까?";
                    String pos_message = "네";
                    String neg_message = "아니요";
                    notifier_examSubmitButtonProcess(message, pos_message, neg_message);
                }else{
                    String message = "답을 선택하지 않은 문제가 존재합니다.\n그래도 채점 하시겠습니까?";
                    String pos_message = "네";
                    String neg_message = "아니요";
                    notifier_examSubmitButtonProcess(message, pos_message, neg_message);
                }
            }
        });
    }
    public boolean checkifAllAnswersAreSelected(){
        boolean isEverythingSelected = true;
        for(int l = 0 ; l <answer.size(); l++){
            int user_selected_answer_choice = answer.get(l);
            if(user_selected_answer_choice==-1){
//                Toast.makeText(ExamViewActivity.this, String.valueOf(l)+"번 미싱", Toast.LENGTH_SHORT).show();
                isEverythingSelected = false;
            }
        }
        return isEverythingSelected;
    }
    public void temp(){
        JSONArray savejsonArray = new JSONArray();
        for(int i = 0 ; i < resultJSONarray.length(); i++){
            try {
                JSONObject jsonObject = resultJSONarray.getJSONObject(i);
                String exam_code = jsonObject.getString("exam_code");
                String exam_name = jsonObject.getString("exam_name");
                String published_year = jsonObject.getString("published_year");
                String published_round = jsonObject.getString("published_round");
                String subject_code = jsonObject.getString("subject_code");
                String subject_name = jsonObject.getString("subject_name");
                String question_number = jsonObject.getString("question_number");
                String question_question = jsonObject.getString("question_question").replace("\"","").replace("\n","<br>");
                String question_answer = jsonObject.getString("question_answer").replace("\"","").replace("\n","<br>");
                String correct_answer = jsonObject.getString("correct_answer");
                String question_Q_image = jsonObject.getString("question_Q_image");
                String question_A_image = jsonObject.getString("question_A_image");
                String example_exist = jsonObject.getString("example_exist");
                // from try to this line, this is objects from ----

                JSONObject savejsonObject = new JSONObject();
                savejsonObject.put("subject_code", subject_code);
                savejsonObject.put("subject_name", subject_name);
                savejsonObject.put("correct_answer", correct_answer);
                savejsonObject.put("user_answer", answer.get(i));
                if(correct_answer.equals(String.valueOf(answer.get(i)))){
                    savejsonObject.put("compared", "correct");
                }else{
                    savejsonObject.put("compared", "incorrect");
                }
                savejsonObject.put("question_number", question_number);
                savejsonObject.put("question", question_question);
                savejsonObject.put("answer", question_answer);
                savejsonObject.put("question_image", question_Q_image);
                savejsonObject.put("answer_image", question_A_image);
                savejsonObject.put("example_exist", example_exist);



                savejsonArray.put(savejsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //upload process and result below
        if(LoginType.equals("kakao") || LoginType.equals("normal")){
            progressbar_visible();

            upLoadSubmittedExamData(savejsonArray, refresh_upload_prevent);
//            upLoadSubmittedExamData(resultJSONarray, refresh_upload_prevent);

            Intent intent = new Intent(ExamViewActivity.this, ExamResultActivity.class);
            intent.putExtra("from", "ExamViewActivity");
            intent.putExtra("from_exam_type", "sugs_1001/gs_2001");
            intent.putExtra("ExamResult", savejsonArray.toString());
            intent.putExtra("exam_code", exam_code);
            intent.putExtra("exam_name", exam_name);
            intent.putExtra("published_year", published_year);
            intent.putExtra("published_round", published_round);
            intent.putExtra("refresh_upload_prevent", refresh_upload_prevent);
            startActivity(intent);
//            overridePendingTransition(R.anim.slide_in,R.anim.slide_left_bit);// first entering // second exiting
            setResult(RESULT_OK);
            finish();
            slide_left_and_slide_in();
//            onBackPressed();
        }else{
            Intent intent = new Intent(ExamViewActivity.this, ExamResultActivity.class);
            intent.putExtra("ExamResult", savejsonArray.toString());
            intent.putExtra("from_exam_type", "sugs_1001/gs_2001");
            intent.putExtra("exam_code", exam_code);
            intent.putExtra("exam_name", exam_name);
            intent.putExtra("published_year", published_year);
            intent.putExtra("published_round", published_round);
            intent.putExtra("refresh_upload_prevent", refresh_upload_prevent);
            startActivity(intent);
//            overridePendingTransition(R.anim.slide_in,R.anim.slide_left_bit);// first entering // second exiting
            setResult(RESULT_OK);
            finish();
            slide_left_and_slide_in();
//            onBackPressed();
        }
    }
    private void upLoadSubmittedExamData(final JSONArray jsonArray, final String today_date){
        RequestQueue queue = Volley.newRequestQueue(ExamViewActivity.this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/uploadUserExamData.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response :::", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid_normal_success") || access_token.equals("valid_kakao_success")){
//                                progressbar_invisible();

                            }else if(access_token.equals("invalid")){
//                                progressbar_invisible();
                                Toast.makeText(ExamViewActivity.this,"잘못된 접근입니다", Toast.LENGTH_SHORT).show();
                            }else{
//                                progressbar_invisible();
                                Toast.makeText(ExamViewActivity.this,"error", Toast.LENGTH_SHORT).show();
                            }
                                progressbar_invisible();
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
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                params.put("exam_code", exam_code);
                params.put("exam_placed_year", published_year);
                params.put("exam_placed_round", published_round);

                params.put("exam_duration", "0");
                params.put("current_time", today_date);

                params.put("user_json_data", TextUtils.htmlEncode(jsonArray.toString()));
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void progressbar_visible(){
        ExamView_progressBar.setVisibility(View.VISIBLE);
//        progressBarBackground.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void progressbar_invisible(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        ExamView_progressBar.setVisibility(View.GONE);
//        progressBarBackground.setVisibility(View.GONE);
    }
    public void slide_left_and_slide_in(){//opening new activity
        overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

//UNDER THIS LINE LAW FUNCTIONS ------------------------------
//UNDER THIS LINE LAW FUNCTIONS ------------------------------
//UNDER THIS LINE LAW FUNCTIONS ------------------------------
    public String LAW_major_minor_to_kor(String input_str){
        switch (input_str){
            case "major_1001":
                return "사례형";
            case "major_1002":
                return "기록형";
            case "major_1003":
                return "선택형";
            case "minor_2001":
                return "공법";
            case "minor_2002":
                return "민사법";
            case "minor_2003":
                return "형사법";
            default:
//                "minor_2004":
                return "선택형";
        }
    }
    public void LAW_getSelectedExam(final String exam_placed_year, final String major_type, final String minor_type){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = base_url+"getExamList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("exam selected response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")){
                                JSONObject response1 = jsonObject.getJSONObject("response1");
                                String question_count_str = response1.getString("question_count");
                                int question_count_int = Integer.parseInt(question_count_str);
                                JSONArray exam_data = response1.getJSONArray("exam_data");

                                examViewTypeAViewPagerAdapter = new LawExamViewTypeAViewPagerAdapter(getSupportFragmentManager());
                                examViewTypeAViewPagerAdapter.count = question_count_int;
                                examViewTypeAViewPagerAdapter.jsonArray = exam_data;

                                eviewPager = (ViewPager) findViewById(R.id.container);
                                eviewPager.setAdapter(examViewTypeAViewPagerAdapter);
                                eviewPager.setOffscreenPageLimit(question_count_int);
                                eviewPager.setPageMargin(16);

                                LAW_create_answer_sheet(exam_data.length(), exam_data, exam_placed_year, major_type, minor_type);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//
//                        examlistviewAdapter.notifyDataSetChanged();
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("are we here 6", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("type", "select");
                params.put("exam_placed_year", exam_placed_year);
                params.put("major_type", major_type);
                params.put("minor_type", minor_type);
                params.put("navi_selection", navi_selection);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void LAW_create_answer_sheet(int count, JSONArray exam_data, String exam_placed_year, String major_type, String minor_type){
        TextView title_textView = new TextView(ExamViewActivity.this);
//        title_textView.setBackground(getResources().getDrawable(R.drawable.outline_round_orange));
        title_textView.setGravity(Gravity.CENTER);
        title_textView.setText("답안지");
        answer_sheet_element_layout.addView(title_textView);
        for(int i =0 ; i<count; i++){
            final int ii = i;
            View answer_sheet_element = getLayoutInflater().inflate(R.layout.law_container_exam_view_type_a_answer_sheet_element, null);
            answer_sheet_element.setId(i);

            TextView questionNumber = (TextView) answer_sheet_element.findViewById(R.id.questionNumber_textView);
            questionNumber.setText(String.valueOf(i+1)+".");

            ConstraintLayout layout = (ConstraintLayout) answer_sheet_element.findViewById(R.id.answer_sheet_element_container);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("page", "page:::"+String.valueOf(ii));
                    eviewPager.setCurrentItem(ii);
                    drawer.closeDrawer(Gravity.RIGHT);
                }
            });

            answer_sheet_element_layout.addView(answer_sheet_element);

        }
        Button submit_button = new Button(ExamViewActivity.this);
        LAW_examSubmitButtonProcess(submit_button, exam_data,  exam_placed_year,  major_type,  minor_type);// 시험 종료 및 체점 버튼 프로세스
        answer_sheet_element_layout.addView(submit_button);
    }
    public void LAW_makeAnswerSheet(){// 기출 시험에만 적용되는것
        int size = answer.size();
        for(int i = 0; i<size; i++){
            View v = findViewById(i);
            TextView one = (TextView) v.findViewById(R.id.answerChoice_1_textView);
            TextView two = (TextView) v.findViewById(R.id.answerChoice_2_textView);
            TextView three = (TextView) v.findViewById(R.id.answerChoice_3_textView);
            TextView four = (TextView) v.findViewById(R.id.answerChoice_4_textView);
            TextView five = (TextView) v.findViewById(R.id.answerChoice_5_textView);

            int answer1 = answer.get(i);

            if(answer1 ==1){
                one.setTextColor(getResources().getColor(R.color.colorWhite));
                one.setBackground(getResources().getDrawable(R.drawable.answer_selected_container_law));
                two.setBackground(null);
                three.setBackground(null);
                four.setBackground(null);
                five.setBackground(null);
            }else if(answer1==2){
                one.setBackground(null);
                two.setTextColor(getResources().getColor(R.color.colorWhite));
                two.setBackground(getResources().getDrawable(R.drawable.answer_selected_container_law));
                three.setBackground(null);
                four.setBackground(null);
                five.setBackground(null);
            }else if(answer1==3){
                one.setBackground(null);
                two.setBackground(null);
                three.setTextColor(getResources().getColor(R.color.colorWhite));
                three.setBackground(getResources().getDrawable(R.drawable.answer_selected_container_law));
                four.setBackground(null);
                five.setBackground(null);
            }else if(answer1==4){
                one.setBackground(null);
                two.setBackground(null);
                three.setBackground(null);
                four.setTextColor(getResources().getColor(R.color.colorWhite));
                four.setBackground(getResources().getDrawable(R.drawable.answer_selected_container_law));
                five.setBackground(null);
            }else if(answer1==5){
                one.setBackground(null);
                two.setBackground(null);
                three.setBackground(null);
                four.setBackground(null);
                five.setTextColor(getResources().getColor(R.color.colorWhite));
                five.setBackground(getResources().getDrawable(R.drawable.answer_selected_container_law));
            }
        }
    }
    public void LAW_examSubmitButtonProcess(Button submitButton, final JSONArray exam_data, final String exam_placed_year, final String major_type, final String minor_type){// 기출 시험에만 적용되는것
        submitButton.setText("시험 종료 및 체점");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,20,0,20);
        submitButton.setLayoutParams(params);
//        submitButton.setBackground(getResources().getDrawable(R.drawable.solid_round_orange));
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean is = checkifAllAnswersAreSelected();
                if(is){
                    String message = "채점 하시겠습니까?";
                    String pos_message = "네";
                    String neg_message = "아니요";
                    LAW_notifier_examSubmitButtonProcess(message, pos_message, neg_message, exam_data, exam_placed_year,  major_type,  minor_type);
                }else{
                    String message = "답을 선택하지 않은 문제가 존재합니다.\n그래도 채점 하시겠습니까?";
                    String pos_message = "네";
                    String neg_message = "아니요";
                    LAW_notifier_examSubmitButtonProcess(message, pos_message, neg_message, exam_data,exam_placed_year,  major_type,  minor_type);
                }
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean is = checkifAllAnswersAreSelected();
                if(is){
                    String message = "채점 하시겠습니까?";
                    String pos_message = "네";
                    String neg_message = "아니요";
                    LAW_notifier_examSubmitButtonProcess(message, pos_message, neg_message, exam_data,exam_placed_year,  major_type,  minor_type);
                }else{
                    String message = "답을 선택하지 않은 문제가 존재합니다.\n그래도 채점 하시겠습니까?";
                    String pos_message = "네";
                    String neg_message = "아니요";
                    LAW_notifier_examSubmitButtonProcess(message, pos_message, neg_message, exam_data,exam_placed_year,  major_type,  minor_type);
                }
            }
        });
    }
    public void LAW_notifier_examSubmitButtonProcess(String message, String positive_message, String negative_message,
                                                     final JSONArray exam_data, final String exam_placed_year, final String major_type, final String minor_type){
        AlertDialog.Builder builder = new AlertDialog.Builder(ExamViewActivity.this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //positive button
//                        sendExamResult(exam_data, exam_placed_year,  major_type,  minor_type);
                        if(LoginType.equals("normal") || LoginType.equals("kakao")){
                            JSONArray answer_json = LAW_user_answers_to_json();
                            LAW_upload_process(exam_placed_year, major_type, minor_type, exam_data, answer_json);
                        }else{
                            JSONArray answer_json = LAW_user_answers_to_json();
                            sendExamResult( exam_data,  exam_placed_year,  major_type,  minor_type, answer_json);
                        }
                        drawer.closeDrawer(Gravity.RIGHT);
                    }
                })
                .setNegativeButton(negative_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //negative button
                        drawer.closeDrawer(Gravity.RIGHT);
                    }
                })
                .create()
                .show();
    }
    public void LAW_upload_process(final String exam_placed_year, final String major_type, final String minor_type, final JSONArray exam_data, final JSONArray answer_json){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = base_url+"LAW_upload_process.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("exam upload res ::" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                String response1 = jsonObject.getString("response1");
                                if(response1.equals("success")){
                                    sendExamResult( exam_data,  exam_placed_year,  major_type,  minor_type, answer_json);
                                }
                            }else if(access_token.equals("invalid")){

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
                        //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
//                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
//                        toast(message);
//                        getExamNameAndCode(); // 인터넷 에러가 났을시 다시 한번 시도한다.
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("exam_data", answer_json.toString());
                params.put("exam_placed_year", exam_placed_year);
                params.put("major_type", major_type);
                params.put("minor_type", minor_type);
                params.put("refresh_prevent", refresh_upload_prevent);

                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public JSONArray LAW_user_answers_to_json(){
        JSONArray jsonArray = new JSONArray();
        try {
            for(int i = 0 ; i < answer.size(); i++){
                int a = answer.get(i);
                jsonArray.put(i, a);
            }
            return jsonArray;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
    public void sendExamResult(JSONArray exam_data, String exam_placed_year, String major_type, String minor_type, JSONArray answer_json){
        Intent intent = new Intent(ExamViewActivity.this, ExamResultActivity.class);
        intent.putExtra("from", "ExamViewActivity");
        intent.putExtra("major_exam_type", "lawyer");
        intent.putExtra("exam_placed_year", exam_placed_year);
        intent.putExtra("major_type", major_type);
        intent.putExtra("minor_type", minor_type);
        intent.putExtra("exam_data", exam_data.toString());
        intent.putExtra("answer_json", answer_json.toString());
//        intent.putExtra("UserSelectedAnswer", answer);
        startActivity(intent);
        finish();
    }



    @Override
    public void onBackPressed(){
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out);// first entering // second exiting
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if(navi_selection.equals("1")){// 1 일때는 기출시험 문제 풀이
            getMenuInflater().inflate(R.menu.exam_answer_sheet, menu);
        }else { // else 는 2 밖에 없기때문에 우선 else 로 둔다.
            getMenuInflater().inflate(R.menu.exam_note_sheet, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.answer_sheet_menu){
            //기출 시험 응시
            // drawer
            if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                //drawer is open
//                drawer.closeDrawer(Gravity.LEFT);
                drawer.closeDrawer(Gravity.RIGHT);

            } else {
                //drawer is closed
                drawer.openDrawer(Gravity.RIGHT);

            }
        }else if(id==R.id.exam_note_sheet){// backpress id
            //기출 시험 공부
            // drawer
            if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                //drawer is open
                drawer.closeDrawer(Gravity.RIGHT);

            } else {
                //drawer is closed
                drawer.openDrawer(Gravity.RIGHT);
                if(LoginType.equals("null")){
                    //로그인을 하지 않은 상태
                }else{
                    //로그인 한 상태
                    progressbar_visible();
                    answer_sheet_element_layout.removeAllViews();
                    try {
                        makeUserPersonalNoteSheet_renew();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("activity request", "activity request");
        ActivityResultBus.getInstance().postQueue(
                new ActivityResultEvent(requestCode, resultCode, data));
//        if(requestCode == NOTE_REQUEST_CODE){
//            if(resultCode==RESULT_OK){
//                eViewPagerAdapter.getItem(0).onActivityResult(40001, resultCode, data);
//            }
//        }
    }
}
