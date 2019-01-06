package com.storyvendingmachine.www.pp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Administrator on 2018-06-24.
 */

public class TestYearOrderAdapter extends BaseAdapter {
    private Context context;
    private List<TestYearOrderList> list;

    public TestYearOrderAdapter(Context context, List<TestYearOrderList> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v =View.inflate(context, R.layout.select_examlist_elements_container, null);
        View v2 =View.inflate(context, R.layout.exam_year_division, null);

        final String exam_code = list.get(i).getExam_code();
        final String exam_name = list.get(i).getExam_name();
        final String published_year = list.get(i).getPublished_year();
        final String published_num = list.get(i).getPublished_num();
        final String navi_selection = list.get(i).getNavi_selection();

        String total = list.get(i).getTotal_taker();
        String pass = list.get(i).getPass_count();
        String fail = list.get(i).getFail_count();
        String percent = list.get(i).getPercent();

        if(published_num.equals("empty") || total.equals("empty")|| pass.equals("empty")|| fail.equals("empty")||percent.equals("empty")){

            String text = published_year +"  년도  "+exam_name;
            TextView ExamEachNameTextView =(TextView) v2.findViewById(R.id.exam_year_textView);
            ExamEachNameTextView.setText(text);

            return v2;
        }else{


            TextView ExamEachNameTextView =(TextView) v.findViewById(R.id.ExamEachListTextView);

            TextView total_textView = (TextView) v.findViewById(R.id.total_taker_textView);
            TextView percent_textView = (TextView) v.findViewById(R.id.percent_textView);


            if(navi_selection.equals("1")){
                //1 눌러졌을때   == > 기출시험 시험보기
                String text = "기출 시험 문제 풀이\n"+exam_name + "   "+ published_year +"  년도  "+published_num+" 회차";
                ExamEachNameTextView.setText(text);
                total_textView.setText("응시횟수 : "+total+" 회");
                percent_textView.setText("합격률 : "+percent+" %");

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ExamViewActivity.class);
                        intent.putExtra("navi_selection", navi_selection);// navi_selection = 1  일때는 시험 2일때는 기출 공부
                        intent.putExtra("exam_code", exam_code);
                        intent.putExtra("exam_name", exam_name);
                        intent.putExtra("published_year",published_year);
                        intent.putExtra("published_round", published_num);
                        context.startActivity(intent);
                        slide_left_and_slide_in();
                    }
                });
            }else {
                // 1외의 다른 번호가 눌러졌을떄 ( 현재는 1외는 2밖에없다 )
                // 기출시험공부 하는 페이지가 불려온다
                String text = "기출 시험 시험지 공부\n"+ exam_name + "   "+ published_year +"  년도  "+published_num+" 회차";
                ExamEachNameTextView.setText(text);
                total_textView.setText("응시횟수 : "+total+" 회");
                percent_textView.setText("합격률 : "+percent+" %");

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ExamViewActivity.class);
                        intent.putExtra("navi_selection", navi_selection);// navi_selection = 1  일때는 시험 2일때는 기출 공부
                        intent.putExtra("exam_code", exam_code);
                        intent.putExtra("exam_name", exam_name);
                        intent.putExtra("published_year",published_year);
                        intent.putExtra("published_round", published_num);
                        context.startActivity(intent);
                        slide_left_and_slide_in();

                    }
                });

            }


            return v;
        }




//        return v;
    }


    public void slide_left_and_slide_in(){
        ((MainActivity) context).overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }
}
