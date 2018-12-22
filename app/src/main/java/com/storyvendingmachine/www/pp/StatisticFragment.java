package com.storyvendingmachine.www.pp;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import java.util.Map;

import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_name;

public class StatisticFragment extends Fragment {
    PieChart pieChart;
    TextView title_textView, second_title_textView, never_took_exam_textView;
    LinearLayout statistic_exam_resul_container;

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
        }else{
            Initializer(rootview);
            getExamResultData(rootview);

        }
        return rootview;
    }

    public void Initializer(View rootview){
        pieChart = (PieChart) rootview.findViewById(R.id.piechart);
        title_textView = (TextView) rootview.findViewById(R.id.statistic_title_textView);
        second_title_textView = (TextView) rootview.findViewById(R.id.statistic_second_title_textView);
        never_took_exam_textView = (TextView) rootview.findViewById(R.id.never_took_exam_textView);
        statistic_exam_resul_container = (LinearLayout) rootview.findViewById(R.id.statistic_exam_resul_container);

        String title_str = "현재 공부중인 시험 \n\""+exam_selection_name+"\"\n기출 시험 통계";
        title_textView.setText(title_str);
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
                                JSONObject jsonObject_for_pie = jsonObject.getJSONObject("response_for_piechart");
                                for(int i = 0 ; i <jsonArray.length(); i++){
                                    String exam_code = jsonArray.getJSONObject(i).getString("exam_code");
                                    String exam_name = jsonArray.getJSONObject(i).getString("exam_name");
                                    String exam_placed_year = jsonArray.getJSONObject(i).getString("exam_placed_year");
                                    String exam_placed_round = jsonArray.getJSONObject(i).getString("exam_placed_round");
                                    String date_user_took_exam = jsonArray.getJSONObject(i).getString("date_user_took_exam");
                                    String time_user_took_exam = jsonArray.getJSONObject(i).getString("time_user_took_exam");
                                    String time_duration = jsonArray.getJSONObject(i).getString("time_duration");
                                    String pass = jsonArray.getJSONObject(i).getString("pass");

                                    String correct_count = jsonArray.getJSONObject(i).getJSONArray("total_score").getString(0);
                                    String total_question = jsonArray.getJSONObject(i).getJSONArray("total_score").getString(1);

                                    JSONArray subjectjsonArray = jsonArray.getJSONObject(i).getJSONArray("subject_score_json");
                                        for(int j = 0 ; j < subjectjsonArray.length(); j++){
                                            String subject_name = subjectjsonArray.getJSONObject(j).getString("subject");
                                            String subject_correct = subjectjsonArray.getJSONObject(j).getString("correct_count");
                                            String subject_total = subjectjsonArray.getJSONObject(j).getString("total_count");
                                        }
                                }

                                String user_taken_exam_count = jsonObject_for_pie.getString("user_taken_exam_count");
                                String second_title_str = exam_selection_name+" 기출 "+ user_taken_exam_count+ " 회 응시";
                                second_title_textView.setText(second_title_str);
                                String pass = jsonObject_for_pie.getString("pass");
                                String fail = jsonObject_for_pie.getString("fail");
                                String percent_pass = jsonObject_for_pie.getString("percent_pass");
                                String percent_fail = jsonObject_for_pie.getString("percent_fail");

                                if(Integer.parseInt(user_taken_exam_count) == 0){
                                    //시험을 응시한적이 없을떄
                                    never_took_exam_textView.setVisibility(View.VISIBLE);
                                    pieChart.setVisibility(View.GONE);
                                    statistic_exam_resul_container.setVisibility(View.GONE);
                                }else{
                                    //한번이라도 시험을 응시했을때
                                    pieChart(rootview, Integer.parseInt(percent_pass), Integer.parseInt(percent_fail));
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


    public void pieChart(View rootview, int pass, int fail){
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

}
