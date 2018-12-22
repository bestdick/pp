package com.storyvendingmachine.www.pp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
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

        if(published_num.equals("empty")){

            String text = published_year +"  년도  "+exam_name;
            TextView ExamEachNameTextView =(TextView) v2.findViewById(R.id.exam_year_textView);
            ExamEachNameTextView.setText(text);

            return v2;
        }else{


            TextView ExamEachNameTextView =(TextView) v.findViewById(R.id.ExamEachListTextView);



            if(navi_selection.equals("1")){
                //1 눌러졌을때   == > 기출시험 시험보기
                String text = exam_name + "   "+ published_year +"  년도  "+published_num+" 회차 기출 문제 풀이";
                ExamEachNameTextView.setText(text);

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
                        Toast.makeText(context, Integer.toString(i)+"adapter", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                // 1외의 다른 번호가 눌러졌을떄 ( 현재는 1외는 2밖에없다 )
                // 기출시험공부 하는 페이지가 불려온다
                String text = exam_name + "   "+ published_year +"  년도  "+published_num+" 회차 기출 공부";
                ExamEachNameTextView.setText(text);

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
                        Toast.makeText(context, "exam study", Toast.LENGTH_SHORT).show();
                    }
                });

            }


            return v;
        }




//        return v;
    }
}
