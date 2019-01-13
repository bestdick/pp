package com.storyvendingmachine.www.pp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;

public class QuizListAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;

    public QuizListAdapter(Context context, List<String> list){
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

//        TextView today_quiz_textView = (TextView) v.findViewById(R.id.today_quiz_textView);
//        today_quiz_textView.setVisibility(View.INVISIBLE);
        TextView date_textView = (TextView) v.findViewById(R.id.today_quiz_date_textView);
        String date = list.get(i);
        date_textView.setText(date);


        return v;
    }


//    public void slide_left_and_slide_in(){
//        ((MainActivity) context).overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
//    }
}
