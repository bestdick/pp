package com.storyvendingmachine.www.pp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

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
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_name;
import static com.storyvendingmachine.www.pp.MainActivity.getScreenHeight;

public class StatisticFragment extends Fragment {

    //
    //      ConstraintLayout id = "guest_notifier" can be visible when login as guest or also there are no exam that user taken
    //              if user have not take exam than text view must be set.
    //




    TextView title_textView, second_title_textView, never_took_exam_textView;
    LinearLayout statistic_exam_result_container, subject_result_container;
    final int EXAM_RESULT_LINEARLAOUT_TAG_BASE =90000;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public static StatisticFragment newInstance(String param1, String param2) {
        StatisticFragment fragment = new StatisticFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview =inflater.inflate(R.layout.fragment_statistic, container, false);
        if(LoginType.equals("null") || G_user_id.equals("null")){
            //로그인 하지 않았을때.....
            guest_initializer(rootview);
        }else{
            //로그인 한 상태

            if(exam_selection_code.equals("null")){
                //처음 가입했을때 시험을 선택하지 않았을떄...
                no_exam_select_initializer(rootview);
            }else{
                //기본 적인 메뉴 생성
                Initializer(rootview);
                BackgroundTask backgroundTask = new BackgroundTask();
                backgroundTask.rootview = rootview;
                backgroundTask.execute();
                swiper(rootview);
            }

        }
        return rootview;
    }

    public void guest_initializer(View rootview){
        SwipeRefreshLayout swiper_layout = (SwipeRefreshLayout) rootview.findViewById(R.id.swiper_layout);
        swiper_layout.setVisibility(View.GONE);
        ConstraintLayout guest_notifier = (ConstraintLayout) rootview.findViewById(R.id.guest_notifier);
        guest_notifier.setVisibility(View.VISIBLE);

    }
    public void no_exam_select_initializer(View rootview){
        SwipeRefreshLayout swiper_layout = (SwipeRefreshLayout) rootview.findViewById(R.id.swiper_layout);
        swiper_layout.setVisibility(View.GONE);
        ConstraintLayout guest_notifier = (ConstraintLayout) rootview.findViewById(R.id.guest_notifier);
        guest_notifier.setVisibility(View.VISIBLE);
        TextView guest_notifer_textView = (TextView) rootview.findViewById(R.id.guest_notifer_textView);
        String str = "시험을 선택하시고 해당 시험에 대한 통계를 받아보세요";
        guest_notifer_textView.setText(str);
    }
    public void Initializer(View rootview){

        title_textView = (TextView) rootview.findViewById(R.id.statistic_title_textView);
        second_title_textView = (TextView) rootview.findViewById(R.id.statistic_second_title_textView);
        never_took_exam_textView = (TextView) rootview.findViewById(R.id.never_took_exam_textView);
        statistic_exam_result_container = (LinearLayout) rootview.findViewById(R.id.statistic_exam_result_container);
        subject_result_container = (LinearLayout) rootview.findViewById(R.id.subject_result_container);


        String title_str = "현재 공부중인 시험 《 "+exam_selection_name+" 》 기출 시험 통계";
        title_textView.setText(title_str);
    }
    public void swiper(final View rootview){
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swiper_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getExamResultData(rootview);
                statistic_exam_result_container.removeAllViews();
                subject_result_container.removeAllViews();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
    }


    public void behaveLike_expandableListview(View rootview, JSONArray jsonArray){
        HashMap<StatisticExpandable_List_Group,  List<StatisticExpandable_List_Item>> hashMap = new HashMap<StatisticExpandable_List_Group,  List<StatisticExpandable_List_Item>>();
        List<StatisticExpandable_List_Group> group_list = new ArrayList<StatisticExpandable_List_Group>();
            try {
                for (int i = 0 ; i<jsonArray.length(); i++){
                    final String exam_code = jsonArray.getJSONObject(i).getString("exam_code");
                    final String exam_name = jsonArray.getJSONObject(i).getString("exam_name");
                    final String exam_placed_year = jsonArray.getJSONObject(i).getString("exam_placed_year");
                    final String exam_placed_round = jsonArray.getJSONObject(i).getString("exam_placed_round");

                    JSONArray inner_array = jsonArray.getJSONObject(i).getJSONArray("exam_array");

                    View transcript_container = getLayoutInflater().inflate(R.layout.container_statistic_exam_result_title, null);
                    ImageView drop_imageView = (ImageView) transcript_container.findViewById(R.id.drop_down_imageView);
                    TextView exam_titleTextView = (TextView) transcript_container.findViewById(R.id.exam_titleTextView);
                    TextView count_textView = (TextView) transcript_container.findViewById(R.id.count_textView);

                    exam_titleTextView.setText(exam_name +" "+exam_placed_year+"년 "+exam_placed_round+"회");
                    count_textView.setText(String.valueOf(inner_array.length()));

                    statistic_exam_result_container.addView(transcript_container);
                    final LinearLayout linearLayout = new LinearLayout(getActivity());
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setTag(EXAM_RESULT_LINEARLAOUT_TAG_BASE+i);
                    linearLayout.setVisibility(View.GONE);


                    transcript_container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(linearLayout.getVisibility() == View.GONE){
                                linearLayout.setVisibility(View.VISIBLE);
                            }else{
                                linearLayout.setVisibility(View.GONE);

                            }
                        }
                    });

                    statistic_exam_result_container.addView(linearLayout);



                StatisticExpandable_List_Group group_element = new StatisticExpandable_List_Group(exam_code, exam_name, exam_placed_year, exam_placed_round, String.valueOf(jsonArray.length()));
                group_list.add(group_element);


                List<StatisticExpandable_List_Item> item_list = new ArrayList<StatisticExpandable_List_Item>();

                    for(int j = 0 ; j < inner_array.length(); j++){
                        View container = getLayoutInflater().inflate(R.layout.container_statistic_exam_result, null);
                        TextView title_textView = (TextView) container.findViewById(R.id.title_textView);
                        TextView date_time_textView =(TextView) container.findViewById(R.id.exam_took_date_textView);
                        TextView see_more_textView = (TextView) container.findViewById(R.id.see_correct_incorrect_textView);
                        LinearLayout subject_container_linearLayout=(LinearLayout) container.findViewById(R.id.subject_container);
                        TextView pass_textView =(TextView) container.findViewById(R.id.pass_textView);


                        final String date_user_took_exam = inner_array.getJSONObject(j).getString("date_user_took_exam");
                        final String time_user_took_exam = inner_array.getJSONObject(j).getString("time_user_took_exam");
                        String time_duration = inner_array.getJSONObject(j).getString("time_duration");
                        JSONArray subject_score_json = inner_array.getJSONObject(j).getJSONArray("subject_score_json");
                        String total_score = inner_array.getJSONObject(j).getString("total_score");
                        String pass = inner_array.getJSONObject(j).getString("pass");

                        title_textView.setText(exam_name+" "+exam_placed_year+"년 "+" "+exam_placed_round+"회 성적");
                        date_time_textView.setText(date_user_took_exam+" "+time_user_took_exam);
                        if(pass.equals("pass")){
                            //pass일때
                            pass_textView.setText("합격");
                        }else{
                            // fail 일떄
                            pass_textView.setText("불합격");
                            pass_textView.setBackground(getResources().getDrawable(R.drawable.outline_cornered_red));
                        }


                        see_more_textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //틀린 문제 보러간다.
                               Intent intent = new Intent(getActivity(), ExamResultActivity.class);
                               intent.putExtra("from","StatisticFragment");
                               intent.putExtra("exam_code", exam_code);
                               intent.putExtra("exam_name", exam_name);
                               intent.putExtra("exam_placed_year", exam_placed_year);
                               intent.putExtra("exam_placed_round", exam_placed_round);
                               intent.putExtra("date_user_took_exam", date_user_took_exam);
                               intent.putExtra("time_user_took_exam", time_user_took_exam);
                               startActivity(intent);

                            }
                        });

                        LinearLayout l = statistic_exam_result_container.findViewWithTag(EXAM_RESULT_LINEARLAOUT_TAG_BASE+i);
                        l.addView(container);

                            for(int k = 0 ; k < subject_score_json.length(); k++){
                                String subject_name = subject_score_json.getJSONObject(k).getString("subject");
                                String subject_correct = subject_score_json.getJSONObject(k).getString("correct_count");
                                String subject_total = subject_score_json.getJSONObject(k).getString("total_count");

                                View subject_container = getLayoutInflater().inflate(R.layout.inner_container_statistic_exam_result_element, null);
                                TextView subjectname_textView  = (TextView) subject_container.findViewById(R.id.subject_name_textView);
                                TextView subjectscore_textView  = (TextView) subject_container.findViewById(R.id.subject_score_textView);

                                subjectname_textView.setText(subject_name);
                                subjectscore_textView.setText(subject_correct+"/"+subject_total);
                                subject_container_linearLayout.addView(subject_container);
                            }



                        StatisticExpandable_List_Item item_element = new StatisticExpandable_List_Item(date_user_took_exam, time_user_took_exam, time_duration, subject_score_json, total_score, pass);
                        item_list.add(item_element);
                    }
                    hashMap.put(group_element, item_list);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



    }
    public void getExamResultData(final View rootview){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/getStatisticExamResult.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response :::", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                JSONArray jsonArray = jsonObject.getJSONArray("response");
                                if(jsonArray.length() == 0){
                                    SwipeRefreshLayout swiper_layout = (SwipeRefreshLayout) rootview.findViewById(R.id.swiper_layout);
                                    swiper_layout.setVisibility(View.GONE);
                                    ConstraintLayout guest_notifier = (ConstraintLayout) rootview.findViewById(R.id.guest_notifier);
                                    guest_notifier.setVisibility(View.VISIBLE);
                                    TextView guest_notifer_textView = (TextView) rootview.findViewById(R.id.guest_notifer_textView);
                                    guest_notifer_textView.setText(getResources().getString(R.string.statistic_notice_never_took_exam_string));
                                }else{
                                    JSONArray jsonArray_array_by_each_exam = jsonObject.getJSONArray("array_by_each_exam");
                                    behaveLike_expandableListview(rootview, jsonArray_array_by_each_exam);

                                    JSONObject jsonObject_for_pie = jsonObject.getJSONObject("response_for_piechart");
                                    JSONArray jsonArray_for_subject_pie = jsonObject.getJSONArray("response_for_subjectpiechart");

    //subject pie chart
                                    for(int i = 0 ; i<jsonArray_for_subject_pie.length(); i++){
                                        String pie_subject_name = jsonArray_for_subject_pie.getJSONObject(i).getString("subject_name");
                                        String pie_pass_count = jsonArray_for_subject_pie.getJSONObject(i).getString("pass_count");
                                        String pass_percent = jsonArray_for_subject_pie.getJSONObject(i).getString("pass_percent");
                                        String fail_percent = jsonArray_for_subject_pie.getJSONObject(i).getString("fail_percent");

                                        Log.e("percent", "pass ::" +pass_percent+ "/// fail ::"+fail_percent);

                                        View subject_pieView = getLayoutInflater().inflate(R.layout.container_subject_pie_chart, null);
                                        TextView subject_title_textView = (TextView) subject_pieView.findViewById(R.id.subject_title_textView);
                                        PieChart subject_pieChart = (PieChart) subject_pieView.findViewById(R.id.subject_piechart);
                                        pieChart_subject(subject_pieChart, Integer.parseInt(pass_percent),Integer.parseInt(fail_percent));//casting pie chart for subject
                                        subject_title_textView.setText(pie_subject_name);

                                        subject_result_container.addView(subject_pieView);

                                    }
    // total pie chart
                                    String user_taken_exam_count = jsonObject_for_pie.getString("user_taken_exam_count");
                                    String second_title_str = exam_selection_name+" 기출 "+ user_taken_exam_count+ " 회 응시";
                                    second_title_textView.setText(second_title_str);
                                    String pass = jsonObject_for_pie.getString("pass");
                                    String fail = jsonObject_for_pie.getString("fail");
                                    String percent_pass = jsonObject_for_pie.getString("percent_pass");
                                    String percent_fail = jsonObject_for_pie.getString("percent_fail");

                                    if(Integer.parseInt(user_taken_exam_count) == 0){
                                        //시험을 응시한적이 없을떄
                                        PieChart pieChart = (PieChart) rootview.findViewById(R.id.piechart);
                                        pieChart.setVisibility(View.GONE);
                                        never_took_exam_textView.setVisibility(View.VISIBLE);
                                        statistic_exam_result_container.setVisibility(View.GONE);
                                        subject_result_container.setVisibility(View.GONE);
                                    }else{
                                        //한번이라도 시험을 응시했을때
                                        PieChart pieChart = (PieChart) rootview.findViewById(R.id.piechart);
                                        pieChart(rootview, pieChart, Integer.parseInt(percent_pass), Integer.parseInt(percent_fail));
                                    }
                                }
                            }else if(access_token.equals("invalid")){
                                Toast.makeText(getContext(),"잘못된 접근입니다", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(),"error", Toast.LENGTH_SHORT).show();
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
//                        getExamNameAndCode(input_exam_name); // 인터넷 에러가 났을시 다시 한번 시도한다.
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("exam_code", exam_selection_code);
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void pieChart_subject(PieChart subject_pieChart, int pass, int fail ){
        subject_pieChart.setTouchEnabled(false);

        subject_pieChart.setUsePercentValues(true);
        subject_pieChart.getDescription().setEnabled(false);
        subject_pieChart.setExtraOffsets(5,5,5,5);

//        pieChart.setDragDecelerationFrictionCoef(0.95f);

        subject_pieChart.setDrawHoleEnabled(false);
        subject_pieChart.setHoleColor(Color.WHITE);
        subject_pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
        yValues.add(new PieEntry(pass,"통과"));
        yValues.add(new PieEntry(fail,"과락"));

//        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(COLOR_EUGENE);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        subject_pieChart.setData(data);
    }
    public void pieChart(View rootview, PieChart pieChart, int pass, int fail){
        pieChart.setTouchEnabled(false);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,5,5,5);

//        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
        yValues.add(new PieEntry(pass,"합격"));
        yValues.add(new PieEntry(fail,"불합격"));

//        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(COLOR_EUGENE);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);

    }

    public static final int[] COLOR_EUGENE = {
            Color.rgb(255, 193, 7), Color.rgb(199, 199, 199)
    };


    public class BackgroundTask extends AsyncTask<Void, Void, Integer> {

        View rootview;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... integers) {

            RequestQueue queue = Volley.newRequestQueue(getContext());
            String url = "http://www.joonandhoon.com/pp/PassPop/android/server/getStatisticExamResult.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response :::", response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String access_token = jsonObject.getString("access");
                                if(access_token.equals("valid")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                                    if (jsonArray.length() == 0) {
                                        SwipeRefreshLayout swiper_layout = (SwipeRefreshLayout) rootview.findViewById(R.id.swiper_layout);
                                        swiper_layout.setVisibility(View.GONE);
                                        ConstraintLayout guest_notifier = (ConstraintLayout) rootview.findViewById(R.id.guest_notifier);
                                        guest_notifier.setVisibility(View.VISIBLE);
                                        TextView guest_notifer_textView = (TextView) rootview.findViewById(R.id.guest_notifer_textView);
                                        guest_notifer_textView.setText(getResources().getString(R.string.statistic_notice_never_took_exam_string));
                                    } else {
                                        JSONArray jsonArray_array_by_each_exam = jsonObject.getJSONArray("array_by_each_exam");
                                        behaveLike_expandableListview(rootview, jsonArray_array_by_each_exam);
                                        JSONObject jsonObject_for_pie = jsonObject.getJSONObject("response_for_piechart");
                                        JSONArray jsonArray_for_subject_pie = jsonObject.getJSONArray("response_for_subjectpiechart");
//                                for(int i = 0 ; i <jsonArray.length(); i++){
//                                    String exam_code = jsonArray.getJSONObject(i).getString("exam_code");
//                                    String exam_name = jsonArray.getJSONObject(i).getString("exam_name");
//                                    String exam_placed_year = jsonArray.getJSONObject(i).getString("exam_placed_year");
//                                    String exam_placed_round = jsonArray.getJSONObject(i).getString("exam_placed_round");
//                                    String date_user_took_exam = jsonArray.getJSONObject(i).getString("date_user_took_exam");
//                                    String time_user_took_exam = jsonArray.getJSONObject(i).getString("time_user_took_exam");
//                                    String time_duration = jsonArray.getJSONObject(i).getString("time_duration");
//                                    String pass = jsonArray.getJSONObject(i).getString("pass");
//
//                                    String correct_count = jsonArray.getJSONObject(i).getJSONArray("total_score").getString(0);
//                                    String total_question = jsonArray.getJSONObject(i).getJSONArray("total_score").getString(1);
//
//                                    View transcript_container = getLayoutInflater().inflate(R.layout.container_statistic_exam_result, null);
//                                    TextView transcript_title = (TextView)transcript_container.findViewById(R.id.title_textView);
//                                    TextView exam_took_date_textView = (TextView) transcript_container.findViewById(R.id.exam_took_date_textView);
//                                    LinearLayout transcript_subject_container = (LinearLayout) transcript_container.findViewById(R.id.subject_container);
//                                    TextView pass_textView = (TextView)transcript_container.findViewById(R.id.pass_textView);
//
//
//                                    transcript_title.setText(exam_name+" "+ exam_placed_year+" 년도 "+exam_placed_round + "회");
//                                    exam_took_date_textView.setText(date_user_took_exam+" "+time_user_took_exam);
//                                    if(pass.equals("fail")){
//                                        pass_textView.setBackground(getResources().getDrawable(R.drawable.outline_cornered_red));
//                                        pass_textView.setText("불합격");
//                                    }else{
//                                        pass_textView.setBackground(getResources().getDrawable(R.drawable.outline_cornered_green));
//                                        pass_textView.setText("합격");
//                                    }
//
//
//                                    statistic_exam_result_container.addView(transcript_container);
//
//                                    JSONArray subjectjsonArray = jsonArray.getJSONObject(i).getJSONArray("subject_score_json");
//                                        for(int j = 0 ; j < subjectjsonArray.length(); j++){
//                                            String subject_name = subjectjsonArray.getJSONObject(j).getString("subject");
//                                            String subject_correct = subjectjsonArray.getJSONObject(j).getString("correct_count");
//                                            String subject_total = subjectjsonArray.getJSONObject(j).getString("total_count");
//
//                                            View subject_container = getLayoutInflater().inflate(R.layout.inner_container_statistic_exam_result_element, null);
//                                            TextView subjectname_textView  = (TextView) subject_container.findViewById(R.id.subject_name_textView);
//                                            TextView subjectscore_textView  = (TextView) subject_container.findViewById(R.id.subject_score_textView);
//
//                                            subjectname_textView.setText(subject_name);
//                                            subjectscore_textView.setText(subject_correct+"/"+subject_total);
//                                            transcript_subject_container.addView(subject_container);
//
//
//                                        }
//                                }
//subject pie chart
                                        for (int i = 0; i < jsonArray_for_subject_pie.length(); i++) {
                                            String pie_subject_name = jsonArray_for_subject_pie.getJSONObject(i).getString("subject_name");
                                            String pie_pass_count = jsonArray_for_subject_pie.getJSONObject(i).getString("pass_count");
                                            String pass_percent = jsonArray_for_subject_pie.getJSONObject(i).getString("pass_percent");
                                            String fail_percent = jsonArray_for_subject_pie.getJSONObject(i).getString("fail_percent");

                                            Log.e("percent", "pass ::" + pass_percent + "/// fail ::" + fail_percent);

                                            View subject_pieView = getLayoutInflater().inflate(R.layout.container_subject_pie_chart, null);
                                            TextView subject_title_textView = (TextView) subject_pieView.findViewById(R.id.subject_title_textView);
                                            PieChart subject_pieChart = (PieChart) subject_pieView.findViewById(R.id.subject_piechart);
                                            pieChart_subject(subject_pieChart, Integer.parseInt(pass_percent), Integer.parseInt(fail_percent));//casting pie chart for subject
                                            subject_title_textView.setText(pie_subject_name);

                                            subject_result_container.addView(subject_pieView);
                                        }
// total pie chart
                                        String user_taken_exam_count = jsonObject_for_pie.getString("user_taken_exam_count");
                                        String second_title_str = exam_selection_name+" 기출 "+ user_taken_exam_count+ " 회 응시";
                                        second_title_textView.setText(second_title_str);
                                        String pass = jsonObject_for_pie.getString("pass");
                                        String fail = jsonObject_for_pie.getString("fail");
                                        String percent_pass = jsonObject_for_pie.getString("percent_pass");
                                        String percent_fail = jsonObject_for_pie.getString("percent_fail");

                                        if (Integer.parseInt(user_taken_exam_count) == 0) {
                                            //시험을 응시한적이 없을떄
                                            PieChart pieChart = (PieChart) rootview.findViewById(R.id.piechart);
                                            pieChart.setVisibility(View.GONE);
                                            never_took_exam_textView.setVisibility(View.VISIBLE);
                                            statistic_exam_result_container.setVisibility(View.GONE);
                                            subject_result_container.setVisibility(View.GONE);
                                        } else {
                                            //한번이라도 시험을 응시했을때
                                            PieChart pieChart = (PieChart) rootview.findViewById(R.id.piechart);
                                            pieChart(rootview, pieChart, Integer.parseInt(percent_pass), Integer.parseInt(percent_fail));
                                        }

                                        }
                                    }else if (access_token.equals("invalid")) {
                                        Toast.makeText(getContext(), "잘못된 접근입니다", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
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
//                        getExamNameAndCode(input_exam_name); // 인터넷 에러가 났을시 다시 한번 시도한다.
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", "passpop");
                    params.put("exam_code", exam_selection_code);
                    params.put("login_type", LoginType);
                    params.put("user_id", G_user_id);
                    return params;
                }
            };
            queue.add(stringRequest);
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
}
