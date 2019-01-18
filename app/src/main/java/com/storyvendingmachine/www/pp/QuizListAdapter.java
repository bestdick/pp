package com.storyvendingmachine.www.pp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_name;

public class QuizListAdapter extends BaseAdapter {
    private Context context;
    private List<NewsActivity.QuizList> list;

    public QuizListAdapter(Context context, List<NewsActivity.QuizList> list){
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
        View v =View.inflate(context, R.layout.container_today_quiz, null);
        View v2 = View.inflate(context, R.layout.container_quiz_list_user_took, null);

        String list_type = list.get(i).getList_type();
        if(list_type.equals("all_quiz_list")){
            String quiz_date = list.get(i).getQuiz_date();
            String count = list.get(i).getCount();
            String percent = list.get(i).getPercent();
            String fraction = list.get(i).getFraction();

            TextView today_quiz_textView = (TextView) v.findViewById(R.id.today_quiz_textView);
            TextView date_textView = (TextView) v.findViewById(R.id.today_quiz_date_textView);
            TextView today_quiz_exam_title_textview = (TextView) v.findViewById(R.id.today_quiz_exam_title_textview);
            TextView quiz_total_takers_textView = (TextView) v.findViewById(R.id.quiz_total_takers_textView);
            TextView quiz_average_percent_textView = (TextView) v.findViewById(R.id.quiz_average_percent_textView);
            TextView see_more_textView = (TextView) v.findViewById(R.id.see_more_textView);
            TextView take_quiz_textView = (TextView) v.findViewById(R.id.take_quiz_textView);

            date_textView.setVisibility(View.INVISIBLE);
            see_more_textView.setVisibility(View.INVISIBLE);

            today_quiz_textView.setText(period_between_date(quiz_date));
            today_quiz_exam_title_textview.setText(exam_selection_name);
            quiz_total_takers_textView.setText("퀴즈 응시자 "+count);
            quiz_average_percent_textView.setText("평균 "+percent+"%");

            return v;
        }else{
            //my_quiz_list
            String quiz_date = list.get(i).getQuiz_date();
            String percent = list.get(i).getPercent();
            String fraction = list.get(i).getFraction();
            String quiz_took_date = list.get(i).getQuiz_took_date();
            String quiz_took_time = list.get(i).getQuiz_took_time();

            ProgressBar percent_bar =v2.findViewById(R.id.percent_bar);
            TextView quiz_date_name_textView = v2.findViewById(R.id.quiz_date_name_textView);
            TextView quiz_user_took_textView = v2.findViewById(R.id.quiz_user_took_textView);
            TextView quiz_percent_textView = v2.findViewById(R.id.quiz_percent_textView);
            TextView quiz_fraction_textView = v2.findViewById(R.id.quiz_fraction_textView);

            String str_percent = new DecimalFormat("##").format(Double.parseDouble(percent));
            percent_bar.setProgress(Integer.parseInt(str_percent));
            quiz_date_name_textView.setText(period_between_date(quiz_date)+" " +exam_selection_name+" 퀴즈");
            quiz_user_took_textView.setText(quiz_took_date+" "+quiz_took_time);
            quiz_percent_textView.setText(percent+"%");
            quiz_fraction_textView.setText(fraction.replace("#_slash_#", "/"));

            return v2;
        }
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

    public void slide_left_and_slide_in(){
        ((MainActivity) context).overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }
}
