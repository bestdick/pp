package com.storyvendingmachine.www.pp;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.storyvendingmachine.www.pp.Allurl.url_getAllExamSchedule;
import static com.storyvendingmachine.www.pp.Allurl.url_getExamInfo;
import static com.storyvendingmachine.www.pp.Allurl.url_getExamList;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_name;


public class MainFragment extends Fragment {

    int infoView_visible =-1;
    LinearLayout wrapper;



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

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        wrapper = (LinearLayout) rootView.findViewById(R.id.fragment_main_container);


        if(exam_selection_code == "null"){
            wrapper.removeAllViews();
            getWithOutChoosingExamDate();
            Toast.makeText(getActivity(), "exam empty 2", Toast.LENGTH_LONG).show();
        }else{
            wrapper.removeAllViews();
            getExamInfo();
            Toast.makeText(getActivity(), "not null", Toast.LENGTH_LONG).show();

        }


        return rootView;
    }


    public void getWithOutChoosingExamDate(){
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getAllExamSchedule,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("exam not selected" , response);

                        try {
//                            View dday_view_layout = getLayoutInflater().inflate(R.layout.h_scrollview_d_day_container, null);
//                            LinearLayout dday_inner_wrapper = (LinearLayout) dday_view_layout.findViewById(R.id.horizontalScrollView_dday_cointainer);
//
//
//                            View exam_info_container_view = getLayoutInflater().inflate(R.layout.exam_info_container, null);
//                            LinearLayout inner_wrapper = (LinearLayout) exam_info_container_view.findViewById(R.id.ExamInfoContainer);
//
//                            TextView examName = (TextView) exam_info_container_view.findViewById(R.id.ExamNameTextView);
//                            ImageView infoDropImageView = (ImageView) exam_info_container_view.findViewById(R.id.examInfoDropImageView);
//                            examName.setText("시험 일정");
//
//                            examInfoDropDown(examName, infoDropImageView, inner_wrapper);




                            JSONArray ddayjsonArray = new JSONArray();

                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")){
                                final JSONArray jsonArray = jsonObject.getJSONArray("response");

                                for(int i = 0; i<jsonArray.length(); i++) {
                                    String exam_schedule_year= jsonArray.getJSONObject(i).getString("exam_schedule_year");// 시험 종목 이름
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
                                    ddayjsonObject.put("title", exam_schedule_year+" 년 "+ (Integer.parseInt(exam_schedule_round)+1) + " 회 기사/산업기사");//jsonobject
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

                                    if(doc_remain_days.equals("end_exam")){
                                        //만약 이미 종료된 시험일때
                                        remain_doc.setText("종료"); //  필기 남은 날
                                        remain_doc.setTextSize(20);
                                        remain_doc.setTextColor(getResources().getColor(R.color.colorRed));
                                    }else{
                                        //d아직 종료되지 않은 시험
                                        remain_doc.setText(doc_remain_days); //  필기 남은 날
                                    }
                                    String prac_remain_days= remaindays(exam_prac_exam__start);
                                    ddayjsonObject.put("pracRemain", doc_remain_days);//jsonobject
                                    if(prac_remain_days.equals("end_exam")){
                                        //만약 이미 종료된 시험일때
                                        remain_prac.setText("종료");// 실기 남은 날
                                        remain_prac.setTextSize(20);
                                        remain_prac.setTextColor(getResources().getColor(R.color.colorRed));
                                    }else{
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

                                MainFragmentScheduleViewPagerAdapter adapter = new MainFragmentScheduleViewPagerAdapter(getActivity().getSupportFragmentManager());
                                adapter.getType = "all_exam";
                                adapter.count = jsonArray.length();
                                adapter.jsonArray = jsonArray;
                                adapter.ddayjsonArray = ddayjsonArray;


                                for(int i = 0 ; i < jsonArray.length(); i++){
                                    ImageView indicator_image = new ImageView(getActivity());
                                    indicator_image.setId(33000+i);// image tag
                                    if(i==0){
                                        indicator_image.setImageResource(R.drawable.active_indicator);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, 30);
                                        indicator_image.setLayoutParams(params);
                                        indicatorLayout.addView(indicator_image);
                                    }else{
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
                                        for(int i = 0 ; i< jsonArray.length(); i++){
                                            if(position == i){
                                                ImageView geIV = getActivity().findViewById(33000+position);
                                                geIV.setImageResource(R.drawable.active_indicator);
                                            }else{
                                                ImageView geIV = getActivity().findViewById(33000+i);
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
                return params;
            }
        };
        queue.add(stringRequest);

    }

    public void getExamInfo(){
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getExamInfo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("exam info" , response);

                        try {
//                            View dday_view_layout = getLayoutInflater().inflate(R.layout.h_scrollview_d_day_container, null);
//                            LinearLayout dday_inner_wrapper = (LinearLayout) dday_view_layout.findViewById(R.id.horizontalScrollView_dday_cointainer);
//
//                            View exam_info_container_view = getLayoutInflater().inflate(R.layout.exam_info_container, null);
//                            LinearLayout inner_wrapper = (LinearLayout) exam_info_container_view.findViewById(R.id.ExamInfoContainer);
//
//                            TextView examName = (TextView) exam_info_container_view.findViewById(R.id.ExamNameTextView);
//                            ImageView infoDropImageView = (ImageView) exam_info_container_view.findViewById(R.id.examInfoDropImageView);
//                            examName.setText(exam_selection_name + "시험 일정");

//                            examInfoDropDown(examName, infoDropImageView, inner_wrapper);

                            JSONArray ddayjsonArray = new JSONArray();

                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")){
                                final JSONArray jsonArray = jsonObject.getJSONArray("response");
                                if(jsonArray.length()==3){

                                }else if(jsonArray.length() == 1){

                                }

                                String[][] d_day_string = new String[jsonArray.length()][2];
                                String[][] d_day_string2 = new String[jsonArray.length()][2];
                                for(int i = 0; i<jsonArray.length(); i++) {
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



                                    String title = implplannm[0] + " "+exam_selection_name+" "+ implplannm[1] + "회";

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

                                    if(doc_remain_days.equals("end_exam")){
                                        //만약 이미 종료된 시험일때
                                        remain_doc.setText("종료"); //  필기 남은 날
                                        remain_doc.setTextSize(20);
                                        remain_doc.setTextColor(getResources().getColor(R.color.colorRed));
                                    }else{
                                        //d아직 종료되지 않은 시험
                                        remain_doc.setText(doc_remain_days); //  필기 남은 날
                                    }
                                    String prac_remain_days= remaindays(pracExamStartDt);

                                    ddayjsonObject.put("pracRemain", doc_remain_days);//jsonobject

                                    if(prac_remain_days.equals("end_exam")){
                                        //만약 이미 종료된 시험일때
                                        remain_prac.setText("종료");// 실기 남은 날
                                        remain_prac.setTextSize(20);
                                        remain_prac.setTextColor(getResources().getColor(R.color.colorRed));
                                    }else{
                                        // 아직 종료되지 않은 시험
                                        remain_prac.setText(prac_remain_days);// 실기 남은 날
                                    }

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

                                MainFragmentScheduleViewPagerAdapter adapter = new MainFragmentScheduleViewPagerAdapter(getActivity().getSupportFragmentManager());
                                adapter.getType = "selected_exam";
                                adapter.count = jsonArray.length();
                                adapter.jsonArray = jsonArray;
                                adapter.ddayjsonArray = ddayjsonArray;


                                for(int i = 0 ; i < jsonArray.length(); i++){
                                    ImageView indicator_image = new ImageView(getActivity());
                                    indicator_image.setId(33000+i);// image tag
                                    if(i==0){
                                        indicator_image.setImageResource(R.drawable.active_indicator);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, 30);
                                        indicator_image.setLayoutParams(params);
                                        indicatorLayout.addView(indicator_image);
                                    }else{
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
                                        for(int i = 0 ; i< jsonArray.length(); i++){
                                            if(position == i){
                                                ImageView geIV = getActivity().findViewById(33000+position);
                                                geIV.setImageResource(R.drawable.active_indicator);
                                            }else{
                                                ImageView geIV = getActivity().findViewById(33000+i);
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
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
//        wrapper.removeAllViews();
//        getExamInfo();

//        if(exam_selection_code == "null"){
//            wrapper.removeAllViews();
//            getWithOutChoosingExamDate();
//            Toast.makeText(getActivity(), "exam empty 2", Toast.LENGTH_LONG).show();
//        }else{
//            wrapper.removeAllViews();
//            getExamInfo();
//            Toast.makeText(getActivity(), "not null", Toast.LENGTH_LONG).show();
//
//        }

    }

    public void refresh_frg(){
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }


    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}