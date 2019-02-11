package com.storyvendingmachine.www.pp;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.github.mikephil.charting.charts.PieChart;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.storyvendingmachine.www.pp.Allurl.url_getAllExamSchedule;
import static com.storyvendingmachine.www.pp.Allurl.url_getExamInfo;
import static com.storyvendingmachine.www.pp.Allurl.url_getExamList;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_name;



public class MainFragment extends Fragment {
    static ViewPager quiz_viewPager;
    ProgressBar progressBar;

    int infoView_visible =-1;
    LinearLayout wrapper;

    View rootView;
    boolean isQuizOpened;
    static ArrayList<Integer> quizUserSelectedAnswers;
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        return fragment;
    }
    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.main_fragment_progress_bar);
        wrapper = (LinearLayout) rootView.findViewById(R.id.fragment_main_container);
        isQuizOpened =false;
        if(exam_selection_code.equals("null") || exam_selection_code == null){
            //시험을 고르지 않은 상태
            //시험을 고르지 않은 상태에서는 1. 전체 시험 스케쥴과
            // 2 . 공지를 나태내준다.
            wrapper.removeAllViews();
            getWithOutChoosingExamDate();//스캐쥴을 가져오는 function
        }else{
            //시험을 고른 상태이다.
            //시험을 고른 상태에서는 1. 전체 시험 스케쥴과
            // 2 . 공지와
            // 3. today quiz 를 만들어준다.
            wrapper.removeAllViews();
            getExamInfo();
        }
        return rootView;
    }

    // Below ---- this data fetch when user did not select exam
    public void getWithOutChoosingExamDate(){
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getAllExamSchedule,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("exam not selected" , response);

                        try {

                            JSONArray ddayjsonArray = new JSONArray();

                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")) {
                                final JSONArray jsonArray = jsonObject.getJSONArray("response");
                                if (jsonArray.length() > 0){
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        String exam_schedule_year = jsonArray.getJSONObject(i).getString("exam_schedule_year");// 시험 종목 이름
                                        String exam_schedule_round = jsonArray.getJSONObject(i).getString("exam_schedule_round");
                                        String exam_description = jsonArray.getJSONObject(i).getString("exam_description");

                                        String exam_doc_reg_start = jsonArray.getJSONObject(i).getString("exam_doc_reg_start");
                                        String exam_doc_reg_end = jsonArray.getJSONObject(i).getString("exam_doc_reg_end");

                                        String exam_doc_exam = jsonArray.getJSONObject(i).getString("exam_doc_exam");

                                        String exam_doc_pass = jsonArray.getJSONObject(i).getString("exam_doc_pass");

                                        String exam_doc_submit_start = jsonArray.getJSONObject(i).getString("exam_doc_submit_start");
                                        String exam_doc_submit_end = jsonArray.getJSONObject(i).getString("exam_doc_submit_end");

                                        String exam_prac_reg_start = jsonArray.getJSONObject(i).getString("exam_prac_reg_start");
                                        String exam_prac_reg_end = jsonArray.getJSONObject(i).getString("exam_prac_reg_end");

                                        String exam_prac_exam__start = jsonArray.getJSONObject(i).getString("exam_prac_exam_start");
                                        String exam_prac_exam_end = jsonArray.getJSONObject(i).getString("exam_prac_exam_end");

                                        String exam_prac_pass = jsonArray.getJSONObject(i).getString("exam_prac_pass");


                                        View exam_info_element_view = getLayoutInflater().inflate(R.layout.exam_info_element, null);

                                        TextView implplannmtextview = (TextView) exam_info_element_view.findViewById(R.id.implplanTextView);
                                        TextView docexamregisterdateTextView = (TextView) exam_info_element_view.findViewById(R.id.docexamregisterdateTextView);
                                        TextView docexamdateTextView = (TextView) exam_info_element_view.findViewById(R.id.docexamdateTextView);
                                        TextView docpassdateTextView = (TextView) exam_info_element_view.findViewById(R.id.docpassdateTextView);
                                        TextView submitqualifierdateTextView = (TextView) exam_info_element_view.findViewById(R.id.submitqualifierdateTextView);
                                        TextView pracregisterdateTextView = (TextView) exam_info_element_view.findViewById(R.id.pracregisterdateTextView);
                                        TextView pracreexamdateTextView = (TextView) exam_info_element_view.findViewById(R.id.pracreexamdateTextView);
                                        TextView pracrepassdateTextView = (TextView) exam_info_element_view.findViewById(R.id.pracrepassdateTextView);

                                        implplannmtextview.setText(exam_description);
                                        docexamregisterdateTextView.setText(exam_doc_reg_start + "  ~  " + exam_doc_reg_end);
                                        docexamdateTextView.setText(exam_doc_exam);
                                        docpassdateTextView.setText(exam_doc_pass);
                                        submitqualifierdateTextView.setText(exam_doc_submit_start + "  ~  " + exam_doc_submit_end);
                                        pracregisterdateTextView.setText(exam_prac_reg_start + "  ~  " + exam_prac_reg_end);
                                        pracreexamdateTextView.setText(exam_prac_exam__start + "  ~  " + exam_prac_exam_end);
                                        pracrepassdateTextView.setText(exam_prac_pass);

                                        JSONObject ddayjsonObject = new JSONObject();//jsonobject
                                        ddayjsonObject.put("title", exam_schedule_year + " 년 " + (Integer.parseInt(exam_schedule_round) + 1) + " 회 기사/산업기사");//jsonobject
                                        // ************** 각각의 회차 시험 남은 일 dday counter 를 inflate 해온 view
                                        View dday_element_view = getLayoutInflater().inflate(R.layout.d_day_element, null);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getScreenWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
                                        dday_element_view.setLayoutParams(params);

                                        TextView exam_title = (TextView) dday_element_view.findViewById(R.id.testtype_TextView);
                                        TextView remain_doc = (TextView) dday_element_view.findViewById(R.id.ddaycount_TextView);
                                        TextView remain_prac = (TextView) dday_element_view.findViewById(R.id.prac_ddaycount_TextView);
                                        // ************** 각각의 회차 시험 남은 일 dday counter 를 inflate 해온 view

                                        exam_title.setText(exam_description);//시험 회차 이름

                                        String doc_remain_days = remaindays(exam_doc_exam);
                                        ddayjsonObject.put("docRemain", doc_remain_days);//jsonobject

                                        if (doc_remain_days.equals("end_exam")) {
                                            //만약 이미 종료된 시험일때
                                            remain_doc.setText("종료"); //  필기 남은 날
                                            remain_doc.setTextSize(20);
                                            remain_doc.setTextColor(getResources().getColor(R.color.colorRed));
                                        } else {
                                            //d아직 종료되지 않은 시험
                                            remain_doc.setText(doc_remain_days); //  필기 남은 날
                                        }
                                        String prac_remain_days = remaindays(exam_prac_exam__start);
                                        ddayjsonObject.put("pracRemain", prac_remain_days);//jsonobject
                                        if (prac_remain_days.equals("end_exam")) {
                                            //만약 이미 종료된 시험일때
                                            remain_prac.setText("종료");// 실기 남은 날
                                            remain_prac.setTextSize(20);
                                            remain_prac.setTextColor(getResources().getColor(R.color.colorRed));
                                        } else {
                                            // 아직 종료되지 않은 시험
                                            remain_prac.setText(prac_remain_days);// 실기 남은 날
                                        }


                                        ddayjsonArray.put(ddayjsonObject);


//                                    dday_inner_wrapper.addView(dday_element_view);
//                                    inner_wrapper.addView(exam_info_element_view);

//********************* 여기까지는 단순히 일정을 만드는 과정이다 *******************************************
                                    }

                                //********************* fragment 사용 하여 시험일정 말들기 **********************************
                                View schedule_view_pager = getLayoutInflater().inflate(R.layout.fragment_main_schedule_view_pager, null);
                                ViewPager viewPager = (ViewPager) schedule_view_pager.findViewById(R.id.schedule_viewPager);
                                LinearLayout indicatorLayout = schedule_view_pager.findViewById(R.id.pager_indicator_layout);
                                TextView no_exam_schedule_textView =(TextView) schedule_view_pager.findViewById(R.id.no_exam_schedule_textView);
                                Spanned text = Html.fromHtml("* 위 스케쥴이 정확하지 않을 수 있습니다. <a style='color:red' href='http://www.q-net.or.kr/crf021.do?id=crf02101&gSite=Q&gId='>Q-net</a>에서 다시 한번 확인 해보세요.");

                                    no_exam_schedule_textView.setText(text);
                                    no_exam_schedule_textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String url = "http://www.q-net.or.kr/crf021.do?id=crf02101&gSite=Q&gId=";
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(url));
                                            startActivity(intent);
                                        }
                                    });



                                MainFragmentScheduleViewPagerAdapter adapter = new MainFragmentScheduleViewPagerAdapter(getActivity().getSupportFragmentManager());
                                adapter.getType = "all_exam";
                                adapter.count = jsonArray.length();
                                adapter.jsonArray = jsonArray;
                                adapter.ddayjsonArray = ddayjsonArray;


                                for (int i = 0; i < jsonArray.length(); i++) {
                                    ImageView indicator_image = new ImageView(getActivity());
                                    indicator_image.setId(33000 + i);// image tag
                                    if (i == 0) {
                                        indicator_image.setImageResource(R.drawable.active_indicator);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, 30);
                                        indicator_image.setLayoutParams(params);
                                        indicatorLayout.addView(indicator_image);
                                    } else {
                                        indicator_image.setImageResource(R.drawable.inactive_indicator);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, 30);
                                        indicator_image.setLayoutParams(params);
                                        indicatorLayout.addView(indicator_image);
                                    }

                                }
                                viewPager.setAdapter(adapter);
                                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                    @Override
                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                    }

                                    @Override
                                    public void onPageSelected(int position) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            if (position == i) {
                                                ImageView geIV = getActivity().findViewById(33000 + position);
                                                geIV.setImageResource(R.drawable.active_indicator);
                                            } else {
                                                ImageView geIV = getActivity().findViewById(33000 + i);
                                                geIV.setImageResource(R.drawable.inactive_indicator);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int state) {

                                    }
                                });

                                wrapper.addView(schedule_view_pager);
                                //********************* fragment 사용 하여 시험일정 말들기 **********************************
//                                wrapper.addView(dday_view_layout);
//                                wrapper.addView(exam_info_container_view);


//                            View first_test_dDay_counter_view = dDayCountView("<필기시험>", d_day_string);
//                            View second_test_dDay_counter_view = dDayCountView("<실기시험>",d_day_string2);


//                            wrapper.addView(first_test_dDay_counter_view);
//                            wrapper.addView(second_test_dDay_counter_view);

                                }else{
                                    View empty_schedule_view = getLayoutInflater().inflate(R.layout.container_schedule_empty, null);
                                    TextView no_exam_schedule_textView = (TextView) empty_schedule_view.findViewById(R.id.no_exam_schedule_textView);
                                    TextView title_no_exam_schedule_textView = (TextView) empty_schedule_view.findViewById(R.id.title_no_exam_schedule_textView);

                                    String year = jsonObject.getString("this_year");
                                    Spanned text = Html.fromHtml(year+"년도 시험 스케쥴이 아직 업데이트 되지 않았습니다.<br><a style='color:red' href='http://www.q-net.or.kr/crf021.do?id=crf02101&gSite=Q&gId='>Q-net</a>에서 확인 해보세요.");
//                                    no_exam_schedule_textView.setMovementMethod(LinkMovementMethod.getInstance());
//                                    String text = "0000년도 시험 스케쥴이 아직 없습니다. Q-net(큐넷)에서 확인 해보세요.";
                                    no_exam_schedule_textView.setText(text);
                                    no_exam_schedule_textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String url = "http://www.q-net.or.kr/crf021.do?id=crf02101&gSite=Q&gId=";
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(url));
                                            startActivity(intent);
                                        }
                                    });

                                    title_no_exam_schedule_textView.setText(year+" 년도 시험 스케줄");
                                    wrapper.addView(empty_schedule_view);


                                    Log.e("해당년도 시험이 없음;", "해당년도 시험이 스캐쥴 없음");
                                }
                            }

                            BackgroundTask backgroundTask = new BackgroundTask();
                            backgroundTask.execute();
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
                return params;
            }
        };
        queue.add(stringRequest);

    } // THIS MUST BE CHANGE WHEN DIFFERENT KINDS OF EXAM UPLOADS
    // Below ---- this data fetch when user select exam
    public void getExamInfo(){
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getExamInfo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("getExaminfo" , response);

                        try {
                            JSONArray ddayjsonArray = new JSONArray();

                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")){
                                final JSONArray jsonArray = jsonObject.getJSONArray("response");
                                if(jsonArray.length()>0) {
                                    String[][] d_day_string = new String[jsonArray.length()][2];
                                    String[][] d_day_string2 = new String[jsonArray.length()][2];
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        String jmfldnm = jsonArray.getJSONObject(i).getString("jmfldnm");// 시험 종목 이름
                                        String[] implplannm = jsonArray.getJSONObject(i).getString("implplannm").split("-");// 시험 횟차


                                        String docRegStartDt = jsonArray.getJSONObject(i).getJSONObject("docRegStartDt").getString("0");// 필기 시험 접수 시작 날짜
                                        String docRegEndDt = jsonArray.getJSONObject(i).getJSONObject("docRegEndDt").getString("0");// 필기 시험 접수 종료 날짜
                                        String docExamStartDt = jsonArray.getJSONObject(i).getJSONObject("docExamStartDt").getString("0");// 필기 시험 시작 날짜
                                        String docExamEndDt = jsonArray.getJSONObject(i).getJSONObject("docExamEndDt").getString("0");// 필기 시험 시작 날짜
                                        String docPassDt = jsonArray.getJSONObject(i).getJSONObject("docPassDt").getString("0");// 필기 합격 발표 날짜
                                        String docSubmitStartDt = jsonArray.getJSONObject(i).getJSONObject("docSubmitStartDt").getString("0");// 응시서료 제출 서류 시작 날짜
                                        String docSubmitEndDt = jsonArray.getJSONObject(i).getJSONObject("docSubmitEndDt").getString("0");// 응시서료 제출 서류 종료 날짜
                                        String pracRegStartDt = jsonArray.getJSONObject(i).getJSONObject("pracRegStartDt").getString("0");// 실기시험 접수 시작 날짜
                                        String pracRegEndDt = jsonArray.getJSONObject(i).getJSONObject("pracRegEndDt").getString("0");// 실기시험 접수 종료 날짜
                                        String pracExamStartDt = jsonArray.getJSONObject(i).getJSONObject("pracExamStartDt").getString("0");// 실기시험 시작 날짜
                                        String pracExamEndDt = jsonArray.getJSONObject(i).getJSONObject("pracExamEndDt").getString("0");// 실기시험 종료 날짜
                                        String pracPassStartDt = jsonArray.getJSONObject(i).getJSONObject("pracPassStartDt").getString("0");// 실기시험 합격 시작 날짜
                                        String pracPassEndDt = jsonArray.getJSONObject(i).getJSONObject("pracPassEndDt").getString("0");// 실기시험 합격 종료 날짜

                                        View exam_info_element_view = getLayoutInflater().inflate(R.layout.exam_info_element, null);
                                        TextView implplannmtextview = (TextView) exam_info_element_view.findViewById(R.id.implplanTextView);
                                        TextView docexamregisterdateTextView = (TextView) exam_info_element_view.findViewById(R.id.docexamregisterdateTextView);
                                        TextView docexamdateTextView = (TextView) exam_info_element_view.findViewById(R.id.docexamdateTextView);
                                        TextView docpassdateTextView = (TextView) exam_info_element_view.findViewById(R.id.docpassdateTextView);
                                        TextView submitqualifierdateTextView = (TextView) exam_info_element_view.findViewById(R.id.submitqualifierdateTextView);
                                        TextView pracregisterdateTextView = (TextView) exam_info_element_view.findViewById(R.id.pracregisterdateTextView);
                                        TextView pracreexamdateTextView = (TextView) exam_info_element_view.findViewById(R.id.pracreexamdateTextView);
                                        TextView pracrepassdateTextView = (TextView) exam_info_element_view.findViewById(R.id.pracrepassdateTextView);

                                        implplannmtextview.setText(implplannm[0] + "년 기사/산업기사 " + implplannm[1] + "회");
                                        docexamregisterdateTextView.setText(docRegStartDt + "  ~  " + docRegEndDt);
                                        docexamdateTextView.setText(docExamStartDt + "  ~  " + docExamEndDt);
                                        docpassdateTextView.setText(docPassDt);
                                        submitqualifierdateTextView.setText(docSubmitStartDt + "  ~  " + docSubmitEndDt);
                                        pracregisterdateTextView.setText(pracRegStartDt + "  ~  " + pracRegEndDt);
                                        pracreexamdateTextView.setText(pracExamStartDt + "  ~  " + pracExamEndDt);
                                        pracrepassdateTextView.setText(pracPassStartDt + "  ~  " + pracPassEndDt);


                                        String title = implplannm[0] + " " + exam_selection_name + " " + implplannm[1] + "회";

                                        JSONObject ddayjsonObject = new JSONObject();//jsonobject
                                        ddayjsonObject.put("title", title);//jsonobject
                                        // ************** 각각의 회차 시험 남은 일 dday counter 를 inflate 해온 view
                                        View dday_element_view = getLayoutInflater().inflate(R.layout.d_day_element, null);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getScreenWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
                                        dday_element_view.setLayoutParams(params);

                                        TextView exam_title = (TextView) dday_element_view.findViewById(R.id.testtype_TextView);
                                        TextView remain_doc = (TextView) dday_element_view.findViewById(R.id.ddaycount_TextView);
                                        TextView remain_prac = (TextView) dday_element_view.findViewById(R.id.prac_ddaycount_TextView);
                                        // ************** 각각의 회차 시험 남은 일 dday counter 를 inflate 해온 view

                                        exam_title.setText(title);//시험 회차 이름

                                        String doc_remain_days = remaindays(docExamStartDt);
                                        ddayjsonObject.put("docRemain", doc_remain_days);//jsonobject

                                        if (doc_remain_days.equals("end_exam")) {
                                            //만약 이미 종료된 시험일때
                                            remain_doc.setText("종료"); //  필기 남은 날
                                            remain_doc.setTextSize(20);
                                            remain_doc.setTextColor(getResources().getColor(R.color.colorRed));
                                        } else {
                                            //d아직 종료되지 않은 시험
                                            remain_doc.setText(doc_remain_days); //  필기 남은 날
                                        }

                                        String prac_remain_days = remaindays(pracExamStartDt);
                                        ddayjsonObject.put("pracRemain", prac_remain_days);//jsonobject

                                        if (prac_remain_days.equals("end_exam")) {
                                            //만약 이미 종료된 시험일때
                                            remain_prac.setText("종료");// 실기 남은 날
                                            remain_prac.setTextSize(20);
                                            remain_prac.setTextColor(getResources().getColor(R.color.colorRed));
                                        } else {
                                            // 아직 종료되지 않은 시험
                                            remain_prac.setText(prac_remain_days);// 실기 남은 날
                                        }
                                        Log.e("필기 / 실기::", doc_remain_days+"////"+prac_remain_days);
                                        ddayjsonArray.put(ddayjsonObject);

//                                    dday_inner_wrapper.addView(dday_element_view);
//                                    inner_wrapper.addView(exam_info_element_view);
//********************* 여기까지는 단순히 일정을 만드는 과정이다 ******************************************
                                    }
//                                wrapper.addView(dday_view_layout);


                                    //********************* fragment 사용 하여 시험일정 말들기 **********************************
                                    View schedule_view_pager = getLayoutInflater().inflate(R.layout.fragment_main_schedule_view_pager, null);
                                    ViewPager viewPager = (ViewPager) schedule_view_pager.findViewById(R.id.schedule_viewPager);
                                    LinearLayout indicatorLayout = schedule_view_pager.findViewById(R.id.pager_indicator_layout);
                                    TextView no_exam_schedule_textView =(TextView) schedule_view_pager.findViewById(R.id.no_exam_schedule_textView);
                                    Spanned text = Html.fromHtml("* 위 스케쥴이 정확하지 않을 수 있습니다. <a style='color:red' href='http://www.q-net.or.kr/crf021.do?id=crf02101&gSite=Q&gId='>Q-net</a>에서 다시 한번 확인 해보세요.");
                                    no_exam_schedule_textView.setText(text);
                                    no_exam_schedule_textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String url = "http://www.q-net.or.kr/crf021.do?id=crf02101&gSite=Q&gId=";
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(url));
                                            startActivity(intent);
                                        }
                                    });

                                    MainFragmentScheduleViewPagerAdapter adapter = new MainFragmentScheduleViewPagerAdapter(getActivity().getSupportFragmentManager());
                                    adapter.getType = "selected_exam";
                                    adapter.count = jsonArray.length();
                                    adapter.jsonArray = jsonArray;
                                    adapter.ddayjsonArray = ddayjsonArray;


                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        ImageView indicator_image = new ImageView(getActivity());
                                        indicator_image.setId(33000 + i);// image tag
                                        if (i == 0) {
                                            indicator_image.setImageResource(R.drawable.active_indicator);
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, 30);
                                            indicator_image.setLayoutParams(params);
                                            indicatorLayout.addView(indicator_image);
                                        } else {
                                            indicator_image.setImageResource(R.drawable.inactive_indicator);
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, 30);
                                            indicator_image.setLayoutParams(params);
                                            indicatorLayout.addView(indicator_image);
                                        }

                                    }
                                    viewPager.setAdapter(adapter);
                                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                        @Override
                                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                        }

                                        @Override
                                        public void onPageSelected(int position) {
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                if (position == i) {
                                                    ImageView geIV = getActivity().findViewById(33000 + position);
                                                    geIV.setImageResource(R.drawable.active_indicator);
                                                } else {
                                                    ImageView geIV = getActivity().findViewById(33000 + i);
                                                    geIV.setImageResource(R.drawable.inactive_indicator);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onPageScrollStateChanged(int state) {

                                        }
                                    });

                                    wrapper.addView(schedule_view_pager);
                                    //********************* fragment 사용 하여 시험일정 말들기 **********************************

//                                wrapper.addView(exam_info_container_view);
                                }else{
                                    View empty_schedule_view = getLayoutInflater().inflate(R.layout.container_schedule_empty, null);
                                    TextView no_exam_schedule_textView = (TextView) empty_schedule_view.findViewById(R.id.no_exam_schedule_textView);
                                    TextView title_no_exam_schedule_textView = (TextView) empty_schedule_view.findViewById(R.id.title_no_exam_schedule_textView);

                                    String year = jsonObject.getString("this_year");
                                    Spanned text = Html.fromHtml(year+"년도 시험 스케쥴이 아직 업데이트 되지 않았습니다.<br><a style='color:red' href='http://www.q-net.or.kr/crf021.do?id=crf02101&gSite=Q&gId='>Q-net</a>에서 확인 해보세요.");
//                                    no_exam_schedule_textView.setMovementMethod(LinkMovementMethod.getInstance());
//                                    String text = "0000년도 시험 스케쥴이 아직 없습니다. Q-net(큐넷)에서 확인 해보세요.";
                                    no_exam_schedule_textView.setText(text);
                                    no_exam_schedule_textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String url = "http://www.q-net.or.kr/crf021.do?id=crf02101&gSite=Q&gId=";
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(url));
                                            startActivity(intent);
                                        }
                                    });

                                    title_no_exam_schedule_textView.setText(year+" 년도 시험 스케줄");
                                    wrapper.addView(empty_schedule_view);


                                    Log.e("해당년도 시험이 없음;", "해당년도 시험이 스캐쥴 없음");

                                }
                                getShortTodayQuiz();
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
                params.put("exam_code", exam_selection_code);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public String remaindays(String test_date){

        String hour = "01";
        String d_day = test_date+hour;
    //    Log.e("test date", d_day);
        String today_date = new SimpleDateFormat("yyyyMMddHH", Locale.getDefault()).format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        try {
            Date testDate = sdf.parse(d_day);
            long testtimeInMilliseconds = testDate.getTime();

            Date mDate = sdf.parse(today_date);
            long timeInMilliseconds = mDate.getTime();

            long oneDay = (long) (24*60*60*1000);
            long one = (long) 1;
    //        Log.e("date :::", String.valueOf(testtimeInMilliseconds) +"///"+ String.valueOf(timeInMilliseconds));
    //        Log.e("Date in milli :: ", String.valueOf((testtimeInMilliseconds - timeInMilliseconds)/oneDay));

            if((((testtimeInMilliseconds - timeInMilliseconds)/oneDay)+one) < 0 ){
                return "end_exam";
            }else{
                return String.valueOf(((testtimeInMilliseconds - timeInMilliseconds)/oneDay)+one);
            }


        } catch (ParseException e) {
            e.printStackTrace();
            return "null";
        }

    }

    public void getShortTodayQuiz(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
//        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/getShortTodayQuiz.php";
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/updateQuiz.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Quiz response :::" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")){
                                String existence_of_test =jsonObject.getString("existence_of_test");
                                if(existence_of_test.equals("false")){
                                    Log.e("test exist? :::" , existence_of_test);
                                }else{
                                    final String today = jsonObject.getString("today");
                                    final JSONArray jsonArray = jsonObject.getJSONArray("response");
                                    final int count = (jsonArray.length()+1);
                                    JSONObject display_info_jsonObject = jsonObject.getJSONObject("display_info");
                                    String average_percent = display_info_jsonObject.getString("average_percent");
                                    String total_takers = display_info_jsonObject.getString("total_takers");

                                    Log.e("percent taker", String.valueOf(average_percent)+"//"+String.valueOf(total_takers));
                                    View container_today_quiz = getLayoutInflater().inflate(R.layout.container_today_quiz, null);
                                    wrapper.addView(container_today_quiz);
                                    TextView today_quiz_date_textView = (TextView) container_today_quiz.findViewById(R.id.today_quiz_date_textView);
                                    TextView see_more_textView = (TextView) container_today_quiz.findViewById(R.id.see_more_textView);
                                    TextView today_quiz_exam_title_textview = (TextView) container_today_quiz.findViewById(R.id.today_quiz_exam_title_textview);
                                    TextView take_quiz_textView =(TextView)  container_today_quiz.findViewById(R.id.take_quiz_textView);
                                    TextView quiz_total_takers_textView =(TextView)  container_today_quiz.findViewById(R.id.quiz_total_takers_textView);
                                    TextView quiz_average_percent_textView =(TextView)  container_today_quiz.findViewById(R.id.quiz_average_percent_textView);
                                    today_quiz_date_textView.setText(period_between_date(today));
                                    today_quiz_exam_title_textview.setText(exam_selection_name);
                                    String quiz_total_takers_str = "퀴즈 응시자 "+total_takers+" 명";
                                    quiz_total_takers_textView.setText(quiz_total_takers_str);
                                    String quiz_average_percent_str = "평균 점수 "+average_percent+" %";
                                    quiz_average_percent_textView.setText(quiz_average_percent_str);

                                    see_more_textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(getActivity(), NewsActivity.class);
                                            intent.putExtra("enter_method", "QUIZ");
                                            intent.putExtra("quiz_menu","1");
                                            startActivity(intent);
                                            slide_left_and_slide_in();
                                        }
                                    });

                                    take_quiz_textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            quizUserSelectedAnswers = new ArrayList<Integer>();
                                            progressbar_visible();
                                            new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                                                @Override
                                                public void run() {
                                                    progressbar_invisible();
                                                }
                                            }, 1500); // 2.5초 후에 실행됨
                                            final ConstraintLayout quiz_display_container = (ConstraintLayout) rootView.findViewById(R.id.quiz_display_container);

                                            slideUp(quiz_display_container);
                                            ImageView close_imageView = (ImageView) rootView.findViewById(R.id.close_imageView);
//                                        TextView submit_textView = (TextView) rootView.findViewById(R.id.submit_textView);
                                            TextView close_textView = (TextView) rootView.findViewById(R.id.close_textView);

                                            quiz_viewPager = (ViewPager) rootView.findViewById(R.id.quiz_container);
                                            QuizViewPagerAdapter adapter = new QuizViewPagerAdapter(getActivity().getSupportFragmentManager());
                                            adapter.from = "main_fragment";
                                            adapter.count= count;
                                            adapter.jsonArray = jsonArray;
                                            adapter.today = today;
                                            quiz_viewPager.setAdapter(adapter);
                                            quiz_viewPager.setOffscreenPageLimit(count);
                                            quiz_viewPager.setPageMargin(8);



                                            close_imageView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    slideDown(quiz_display_container);
//                                                quiz_display_container.setVisibility(View.INVISIBLE);

                                                }
                                            });
                                            close_textView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    slideDown(quiz_display_container);
//                                                quiz_display_container.setVisibility(View.INVISIBLE);
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                            BackgroundTask backgroundTask = new BackgroundTask();
                            backgroundTask.execute();

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
                params.put("exam_code", exam_selection_code);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void getNewsAndAnnouncement_Suggestion(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/getNewsAndAnnouncement.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("announcement :::" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")){
                                View container_news = getLayoutInflater().inflate(R.layout.container_news, null);
                                LinearLayout ll = container_news.findViewById(R.id.news_announcement_linearLayout);
                                TextView see_more_textView = (TextView) container_news.findViewById(R.id.see_more_textView);

                                see_more_textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), NewsActivity.class);
                                        intent.putExtra("enter_method", "NEWS");
                                        startActivity(intent);
                                        slide_left_and_slide_in();
                                    }
                                });
                                wrapper.addView(container_news);

                                JSONArray jsonArray = jsonObject.getJSONArray("response");
                                for(int i = 0 ; i<jsonArray.length(); i++){
                                    View container_news_inner_element = getLayoutInflater().inflate(R.layout.container_news_inner_element, null);
                                    ImageView new_imageView = (ImageView) container_news_inner_element.findViewById(R.id.new_imageView);
                                    TextView type_textView = (TextView) container_news_inner_element.findViewById(R.id.new_title_type_textView);
                                    TextView new_title_textView = (TextView) container_news_inner_element.findViewById(R.id.new_title_textView);
                                    TextView upload_date_textView = (TextView) container_news_inner_element.findViewById(R.id.upload_date_textView);


                                    final String key = jsonArray.getJSONObject(i).getString("key");
                                    String type = jsonArray.getJSONObject(i).getString("type");
                                    String title = jsonArray.getJSONObject(i).getString("title");
                                    String content = jsonArray.getJSONObject(i).getString("content");
                                    String upload_date = jsonArray.getJSONObject(i).getString("upload_date");
                                    String upload_time = jsonArray.getJSONObject(i).getString("upload_time");
                                    String news_announcement_new = jsonArray.getJSONObject(i).getString("new");

                                    if(news_announcement_new.equals("old")){
                                        //마약 해당 공지 또는 뉴스가 1주일 안에 발표된 공지가 아닌 오래된 공지일때
                                        new_imageView.setVisibility(View.GONE);
                                    }
                                    if(type.equals("news")){
                                        //소식 및 뉴스
                                        type_textView.setText("[소식]");
                                    }else if(type.equals("update")){
                                        //업데이트
                                        type_textView.setText("[업데이트]");
                                    }else{
                                        //공지
                                        type_textView.setText("[공지]");
                                    }
                                    new_title_textView.setText(title);
                                    upload_date_textView.setText(upload_date);
                                    final String temp_i = String.valueOf(i);
                                    container_news_inner_element.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(getActivity(), NewsActivity.class);
                                            intent.putExtra("enter_method", "ONE");
                                            intent.putExtra("key", temp_i);
                                            startActivity(intent);
                                            slide_left_and_slide_in();
                                        }
                                    });

                                    ll.addView(container_news_inner_element);
                                }

                                View container_suggestion = getLayoutInflater().inflate(R.layout.container_news, null);
                                LinearLayout ll_suggestion = container_suggestion.findViewById(R.id.news_announcement_linearLayout);
                                TextView suggestion_title_textView = (TextView) container_suggestion.findViewById(R.id.title_textView);
                                TextView suggestion_see_more_textView = (TextView) container_suggestion.findViewById(R.id.see_more_textView);
                                suggestion_title_textView.setText("피드백/건의및개선/오류신고");
                                suggestion_see_more_textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), NewsActivity.class);
                                        intent.putExtra("enter_method", "SUGGESTION_ALL");
                                        startActivity(intent);
                                        slide_left_and_slide_in();
                                    }
                                });
                                wrapper.addView(container_suggestion);

                                JSONArray suggestion_jsonArray = jsonObject.getJSONArray("response_2");
                                for(int j = 0 ; j < suggestion_jsonArray.length(); j++){
                                    View container_suggestion_inner_element = getLayoutInflater().inflate(R.layout.container_news_inner_element, null);
                                    ImageView new_imageView = (ImageView) container_suggestion_inner_element.findViewById(R.id.new_imageView);
                                    TextView type_textView = (TextView) container_suggestion_inner_element.findViewById(R.id.new_title_type_textView);
                                    TextView new_title_textView = (TextView) container_suggestion_inner_element.findViewById(R.id.new_title_textView);
                                    TextView upload_date_textView = (TextView) container_suggestion_inner_element.findViewById(R.id.upload_date_textView);

                                    final String key = suggestion_jsonArray.getJSONObject(j).getString("key");
                                    String login_type = suggestion_jsonArray.getJSONObject(j).getString("login_type");
                                    String user_id = suggestion_jsonArray.getJSONObject(j).getString("user_id");
                                    String user_nickname = suggestion_jsonArray.getJSONObject(j).getString("user_nickname");
                                    String type = suggestion_jsonArray.getJSONObject(j).getString("type");
                                    String title = suggestion_jsonArray.getJSONObject(j).getString("title");
                                    String content = suggestion_jsonArray.getJSONObject(j).getString("content");
                                    String upload_date = suggestion_jsonArray.getJSONObject(j).getString("upload_date");
                                    String upload_time = suggestion_jsonArray.getJSONObject(j).getString("upload_time");
                                    String suggestioon_new = suggestion_jsonArray.getJSONObject(j).getString("new");

                                    if(suggestioon_new.equals("old")){
                                        //마약 해당 공지 또는 뉴스가 1주일 안에 발표된 공지가 아닌 오래된 공지일때
                                        new_imageView.setVisibility(View.GONE);
                                    }
                                    if(type.equals("feedback")){
                                        type_textView.setText("[피드백]");
                                    }else if(type.equals("suggestion")){
                                        type_textView.setText("[건의및개선]");
                                    }else{
                                        type_textView.setText("[오류]");
                                    }
                                    new_title_textView.setText(title);
                                    upload_date_textView.setText(upload_date);

                                    final String temp_j = String.valueOf(j);
                                    container_suggestion_inner_element.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(getActivity(), NewsActivity.class);
                                            intent.putExtra("enter_method", "SUGGESTION_SELECT  ");
                                            intent.putExtra("key", temp_j);
                                            startActivity(intent);
                                            slide_left_and_slide_in();
                                        }
                                    });

                                    ll_suggestion.addView(container_suggestion_inner_element);
                                }



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
                params.put("limit", "true");
                return params;
            }
        };
        queue.add(stringRequest);
    }

//    background task
    public class BackgroundTask extends AsyncTask<Void, Void, Integer> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... integers) {
        getNewsAndAnnouncement_Suggestion();
        return -1;
    }

    @Override
    protected void onProgressUpdate(Void... params) {
        super.onProgressUpdate(params);
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}


// ******************** OVERRIDE FUNCTIONS *********************************
    @Override
    public void onResume(){
        super.onResume();


    }

    public void refresh_frg(){
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
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
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
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
    public void slide_left_and_slide_in(){//opening new activity
        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }
    public void progressbar_visible(){
        progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void progressbar_invisible(){
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    // deprecated function
    public View dDayCountView(String testtype, String[][] data_string){
        View exam_d_day_container = getLayoutInflater().inflate(R.layout.exam_d_day_container, null);
        TextView testtype_textView = (TextView) exam_d_day_container.findViewById(R.id.testtype_TextView);
        TextView testround_TextView = (TextView) exam_d_day_container.findViewById(R.id.testround_TextView);
        TextView ddaycount_TextView = (TextView) exam_d_day_container.findViewById(R.id.ddaycount_TextView);
        TextView sub_one_remaindaystitleTextView = (TextView) exam_d_day_container.findViewById(R.id.sub_one_remaindaystitleTextView);//현재 upcoming 시험이아닌 지난 시험이나 다음 시험의 횟차 1
        TextView sub_one_remaindaysTextView = (TextView) exam_d_day_container.findViewById(R.id.sub_one_remaindaysTextView);//현재 upcoming 시험이아닌 지난 시험이나 다음 시험의 일수 1
        TextView sub_two_remaindaystitleTextView = (TextView) exam_d_day_container.findViewById(R.id.sub_two_remaindaystitleTextView);//현재 upcoming 시험이아닌 지난 시험이나 다음 시험의 횟차 1
        TextView sub_two_remaindaysTextView = (TextView) exam_d_day_container.findViewById(R.id.sub_two_remaindaysTextView);//현재 upcoming 시험이아닌 지난 시험이나 다음 시험의 일수 1

        for(int i =0; i<data_string.length; i++){
            if(i==0 && Integer.parseInt(data_string[i][1])>=0){
                testtype_textView.setText(testtype);
                testround_TextView.setText(data_string[i][0]);
                ddaycount_TextView.setText(data_string[i][1]);

                sub_one_remaindaystitleTextView.setText(data_string[1][0]);
                sub_one_remaindaysTextView.setText(data_string[1][1]);
                sub_two_remaindaystitleTextView.setText(data_string[2][0]);
                sub_two_remaindaysTextView.setText(data_string[2][1]);
                break;
            }else if(i==1 && Integer.parseInt(data_string[i][1])>=0){
                testtype_textView.setText(testtype);
                testround_TextView.setText(data_string[i][0]);
                ddaycount_TextView.setText(data_string[i][1]);

                sub_one_remaindaystitleTextView.setText(data_string[0][0]);
                sub_one_remaindaysTextView.setText(data_string[0][1]);

                sub_two_remaindaystitleTextView.setText(data_string[2][0]);
                sub_two_remaindaysTextView.setText(data_string[2][1]);
                break;
            }else if(i==2 && Integer.parseInt(data_string[i][1])>=0){
                testtype_textView.setText(testtype);
                testround_TextView.setText(data_string[i][0]);
                ddaycount_TextView.setText(data_string[i][1]);

                sub_one_remaindaystitleTextView.setText(data_string[0][0]);
                sub_one_remaindaysTextView.setText(data_string[0][1]);
                sub_two_remaindaystitleTextView.setText(data_string[1][0]);
                sub_two_remaindaysTextView.setText(data_string[1][1]);

                break;
            }
        }
        return exam_d_day_container;
    }
    public void examInfoDropDown(TextView examName, final ImageView examInfoDrop, final LinearLayout innerWrapper){

        examName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(infoView_visible == -1){
                    innerWrapper.setVisibility(View.VISIBLE);
                    examInfoDrop.setImageResource(R.drawable.icon_fold);
                    infoView_visible = 1;
                }else if(infoView_visible ==1){
                    innerWrapper.setVisibility(View.GONE);
                    examInfoDrop.setImageResource(R.drawable.icon_drop_down);
                    infoView_visible = -1;
                }

            }
        });
        examInfoDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(infoView_visible == -1){
                    innerWrapper.setVisibility(View.VISIBLE);
                    examInfoDrop.setImageResource(R.drawable.icon_fold);
                    infoView_visible = 1;
                }else if(infoView_visible ==1){
                    innerWrapper.setVisibility(View.GONE);
                    examInfoDrop.setImageResource(R.drawable.icon_drop_down);
                    infoView_visible = -1;
                }

            }
        });
    }
}