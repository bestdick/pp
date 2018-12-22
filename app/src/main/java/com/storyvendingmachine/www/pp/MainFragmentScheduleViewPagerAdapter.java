package com.storyvendingmachine.www.pp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Administrator on 2018-11-23.
 */

public class MainFragmentScheduleViewPagerAdapter extends FragmentStatePagerAdapter {

    String getType;
    int count;
    JSONArray jsonArray;
    JSONArray ddayjsonArray;



    public MainFragmentScheduleViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
Log.e("type ::", getType);
            Bundle bundle = jsonArrayToEachBundle(position);
            Bundle dday_bundle = ddayjsonArrayToEachBundle(position);
            return MainFragmentScheduleFragment.newInstance(String.valueOf(position), bundle, dday_bundle);

    }
    @Override
    public int getCount() {
        // Show 3 total pages.
        return count;
    }

    public Bundle ddayjsonArrayToEachBundle(int i){
        try {
            String title = ddayjsonArray.getJSONObject(i).getString("title");
            String doc_remain = ddayjsonArray.getJSONObject(i).getString("docRemain");
            String prac_remain = ddayjsonArray.getJSONObject(i).getString("pracRemain");

            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            bundle.putString("doc_remain", doc_remain);
            bundle.putString("prac_remain", prac_remain);
            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
    public Bundle jsonArrayToEachBundle(int i){

            try{
                if(getType.equals("all_exam")) {
                    String exam_schedule_year = jsonArray.getJSONObject(i).getString("exam_schedule_year");// 시험 종목 이름
                    String exam_schedule_round = jsonArray.getJSONObject(i).getString("exam_schedule_round");
                    String exam_doc_reg_start = jsonArray.getJSONObject(i).getString("exam_doc_reg_start");
                    String exam_doc_reg_end = jsonArray.getJSONObject(i).getString("exam_doc_reg_end");
                    String exam_doc_exam = jsonArray.getJSONObject(i).getString("exam_doc_exam");
                    String exam_doc_pass = jsonArray.getJSONObject(i).getString("exam_doc_reg_end");
                    String exam_doc_submit_start = jsonArray.getJSONObject(i).getString("exam_doc_submit_start");
                    String exam_doc_submit_end = jsonArray.getJSONObject(i).getString("exam_doc_submit_end");

                    String exam_prac_reg_start = jsonArray.getJSONObject(i).getString("exam_prac_reg_start");
                    String exam_prac_reg_end = jsonArray.getJSONObject(i).getString("exam_prac_reg_end");
                    String exam_prac_exam_start = jsonArray.getJSONObject(i).getString("exam_prac_exam_start");
                    String exam_prac_exam_end = jsonArray.getJSONObject(i).getString("exam_prac_exam_end");
                    String exam_prac_pass = jsonArray.getJSONObject(i).getString("exam_prac_pass");


                    Bundle bundle = new Bundle();
                    bundle.putString("exam_name", "기사/산업기사");
                    bundle.putString("exam_placed_year", exam_schedule_year);
                    bundle.putString("exam_placed_round", exam_schedule_round);
                    bundle.putString("docRegStartDt", exam_doc_reg_start);
                    bundle.putString("docRegEndDt", exam_doc_reg_end);
                    bundle.putString("docExamStartDt", exam_doc_exam);
                    bundle.putString("docExamEndDt", exam_doc_exam);
                    bundle.putString("docPassDt", exam_doc_pass);
                    bundle.putString("docSubmitStartDt", exam_doc_submit_start);
                    bundle.putString("docSubmitEndDt", exam_doc_submit_end);
                    bundle.putString("pracRegStartDt", exam_prac_reg_start);
                    bundle.putString("pracRegEndDt", exam_prac_reg_end);
                    bundle.putString("pracExamStartDt", exam_prac_exam_start);
                    bundle.putString("pracExamEndDt", exam_prac_exam_end);
                    bundle.putString("pracPassStartDt", exam_prac_pass);
                    bundle.putString("pracPassEndDt", exam_prac_pass);
                    return bundle;
                }else{
                    String jmfldnm = jsonArray.getJSONObject(i).getString("jmfldnm");// 시험 종목 이름
                    String[] implplannm = jsonArray.getJSONObject(i).getString("implplannm").split("-");// 시험 횟차  0 => 년도 , 1 => 회차
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

                    Bundle bundle = new Bundle();
                    bundle.putString("exam_name", jmfldnm);
                    bundle.putString("exam_placed_year", implplannm[0]);
                    bundle.putString("exam_placed_round", implplannm[1]);
                    bundle.putString("docRegStartDt", docRegStartDt);
                    bundle.putString("docRegEndDt", docRegEndDt);
                    bundle.putString("docExamStartDt", docExamStartDt);
                    bundle.putString("docExamEndDt", docExamEndDt);
                    bundle.putString("docPassDt", docPassDt);
                    bundle.putString("docSubmitStartDt", docSubmitStartDt);
                    bundle.putString("docSubmitEndDt", docSubmitEndDt);
                    bundle.putString("pracRegStartDt", pracRegStartDt);
                    bundle.putString("pracRegEndDt", pracRegEndDt);
                    bundle.putString("pracExamStartDt", pracExamStartDt);
                    bundle.putString("pracExamEndDt", pracExamEndDt);
                    bundle.putString("pracPassStartDt", pracPassStartDt);
                    bundle.putString("pracPassEndDt", pracPassEndDt);
                    return bundle;
                }
            } catch (JSONException e){
                return null;
            }
    }
}
