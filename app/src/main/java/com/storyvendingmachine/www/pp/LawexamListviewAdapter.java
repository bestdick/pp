package com.storyvendingmachine.www.pp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2019-02-21.
 */

public class LawexamListviewAdapter extends BaseAdapter {
    private Context context;
    private List<LawExamListTypeA> list;


    public LawexamListviewAdapter(Context context, List<LawExamListTypeA> list) {
        this.context = context;
        this.list= list;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        String identifier = list.get(i).getIdentifier();
        if(identifier.equals("empty")){
            View v =View.inflate(context, R.layout.law_container_listview_element_type_a, null);
            TextView exam_placed_year_textView = (TextView) v.findViewById(R.id.exam_placed_year_textView);
            exam_placed_year_textView.setText("기출 준비중");
            return v;
        }else if(identifier.equals("year")){
            View v =View.inflate(context, R.layout.law_container_listview_element_type_a, null);
            TextView exam_placed_year_textView = (TextView) v.findViewById(R.id.exam_placed_year_textView);

            String exam_placed_year = list.get(i).getExam_placed_year();

            exam_placed_year_textView.setText(exam_placed_year + "년도 변호사시험 선택형 기출");
            return v;
        }else{
//            identifier.equals("all")
            View v2 =View.inflate(context, R.layout.law_container_listview_element_type_b, null);
            TextView minor_type_textView = (TextView) v2.findViewById(R.id.minor_type_textView);

            final String exam_placed_year = list.get(i).getExam_placed_year();
            final String major_type = list.get(i).getMajor_type();
            String major_type_kor = list.get(i).getMajor_type_kor();
            final String minor_type = list.get(i).getMinor_type();
            String minor_type_kor = list.get(i).getMinor_type_kor();
            int count_questions = list.get(i).getCount_questions();

            minor_type_textView.setText(minor_type_kor + " ( "+count_questions +" ) ");
            v2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectAlertDialog(exam_placed_year, major_type, minor_type);
                }
            });
            return v2;
        }
    }

    public void selectAlertDialog(final String exam_placed_year, final String major_type, final String minor_type){
        final CharSequence list[] = new CharSequence[]{"#시험 보기", "#시험 답 함께 보기"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("시험응시 방법");
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("value is", "" + which);
                Intent intent = new Intent(context, ExamViewActivity.class);
                switch (which) {
                    case 0:
                        intent.putExtra("exam_major_type", "lawyer");
                        intent.putExtra("navi_selection", "1");
                        intent.putExtra("exam_placed_year", exam_placed_year);
                        intent.putExtra("major_type", major_type);
                        intent.putExtra("minor_type", minor_type);
                        context.startActivity(intent);
                        break;
                    case 1:
                        intent.putExtra("exam_major_type", "lawyer");
                        intent.putExtra("navi_selection", "2");
                        intent.putExtra("exam_placed_year", exam_placed_year);
                        intent.putExtra("major_type", major_type);
                        intent.putExtra("minor_type", minor_type);
                        context.startActivity(intent);
                        break;
                }
            }
        });
        builder.show();
    }
}
