package com.storyvendingmachine.www.pp;


import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.storyvendingmachine.www.pp.Allurl.url_getExamList;
import static com.storyvendingmachine.www.pp.Allurl.url_getExamNameAndCode;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_name;

/**
 * Created by symoo on 2018-02-13.
 */

public class TestFragment extends Fragment {
    int navi_selection;  //현재 선택한 네비게이션의 값    1 == 1번   2 == 2번    3== 3번   4== 4번  아래 버튼이 눌렸을때
    Button firstNaviButton, secondNaviButton, thirdNaviButton,fourthNaviButton;

    ListView FragmentTestListView;
    List<TestYearOrderList> testYearOrderList;
    TestYearOrderAdapter testYearOrderAdapter;


    ProgressBar progressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout menu_layout;
    ConstraintLayout no_exam_conLayout;
    public static TestFragment newInstance() {
        TestFragment fragment = new TestFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.testfragment_progress_bar);
        menu_layout = (LinearLayout) rootView.findViewById(R.id.linearLayout);
        no_exam_conLayout = (ConstraintLayout) rootView.findViewById(R.id.no_exam_conLayout);

        navi_selection = 1;
        firstNaviButton = (Button) rootView.findViewById(R.id.testFragment_bottom_btn1);
        secondNaviButton = (Button) rootView.findViewById(R.id.testFragment_bottom_btn2);
//        thirdNaviButton = (Button) rootView.findViewById(R.id.testFragment_bottom_btn3);
//        fourthNaviButton = (Button) rootView.findViewById(R.id.testFragment_bottom_btn4);

        FragmentTestListView = (ListView) rootView.findViewById(R.id.FragmentTestListView);
        adaptListView();
        swiper(rootView);

        testYearOrderList.clear();
        getExamList(navi_selection);

        navigationButtonClickFunction();
        return rootView;
    }

    public void navigationButtonClickFunction(){
        firstNaviButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbar_visible();
                firstNaviButton.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button));
                secondNaviButton.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                navi_selection = 1;

                testYearOrderList.clear();
                getExamList(navi_selection);
            }
        });

        secondNaviButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbar_visible();
                firstNaviButton.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                secondNaviButton.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button));
                navi_selection = 2;

                testYearOrderList.clear();
                getExamList(navi_selection);
            }
        });
    }



    public void adaptListView(){
        testYearOrderList =new ArrayList<TestYearOrderList>();
        testYearOrderAdapter = new TestYearOrderAdapter(getActivity(), testYearOrderList);
        FragmentTestListView.setAdapter(testYearOrderAdapter);
    }

    public void swiper(View RootView){
        mSwipeRefreshLayout = (SwipeRefreshLayout) RootView.findViewById(R.id.FragmentTestSwiperLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressbar_visible();
                testYearOrderList.clear();
                getExamList(navi_selection);
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    public void getExamList(final int test_selection){
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getExamList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("exam list exam" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                JSONArray jsonArray = jsonObject.getJSONArray("response");
                                if(jsonArray.length() <= 0 || jsonArray == null){
                                    menu_layout.setVisibility(View.INVISIBLE);
                                    mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                                    no_exam_conLayout.setVisibility(View.VISIBLE);
                                }else{
                                    for(int i=0; i<jsonArray.length(); i++){
                                        String exam_name = jsonArray.getJSONObject(i).getString("exam_name");
                                        String exam_code = jsonArray.getJSONObject(i).getString("exam_code");
                                        String published_year = jsonArray.getJSONObject(i).getString("published_year");
                                        String empty = "empty";
                                        TestYearOrderList element_outer = new TestYearOrderList(exam_code, exam_name, published_year, empty, String.valueOf(navi_selection), empty,empty,empty,empty);
                                        testYearOrderList.add(element_outer);

                                        JSONArray statistic_jsonArray = jsonArray.getJSONObject(i).getJSONArray("published_data_by_round");
                                        for(int k = 0 ; k <statistic_jsonArray.length(); k++){
                                            String published_round = statistic_jsonArray.getJSONObject(k).getString("published_round");
                                            String total = statistic_jsonArray.getJSONObject(k).getJSONObject("statistic").getString("total");
                                            String pass = statistic_jsonArray.getJSONObject(k).getJSONObject("statistic").getString("pass");
                                            String fail = statistic_jsonArray.getJSONObject(k).getJSONObject("statistic").getString("fail");
                                            String percent = statistic_jsonArray.getJSONObject(k).getJSONObject("statistic").getString("percent");

                                            TestYearOrderList element = new TestYearOrderList(exam_code, exam_name, published_year, published_round, String.valueOf(navi_selection), total, pass, fail, percent);
                                            testYearOrderList.add(element);
                                        }

                                    }
                                    testYearOrderAdapter.notifyDataSetChanged();
                                }
                            }else if(access_token.equals("invalid")){

                            }else{

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                        progressbar_invisible();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("exam list error ", error.toString()+"// 에러에러");
                        //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
//                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
//                        toast(message);
//                        getExamNameAndCode(); // 인터넷 에러가 났을시 다시 한번 시도한다.
                        progressbar_invisible();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("exam_code", exam_selection_code);
                params.put("test_selection", String.valueOf(test_selection));
                return params;
            }
        };
        queue.add(stringRequest);
    }



    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
//        testYearOrderList.clear();
//        getExamList(navi_selection);

    }



    public void progressbar_visible(){
        progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void progressbar_invisible(){
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
