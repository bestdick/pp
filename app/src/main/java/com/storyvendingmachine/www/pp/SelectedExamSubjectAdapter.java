package com.storyvendingmachine.www.pp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2018-12-20.
 */

public class SelectedExamSubjectAdapter extends BaseAdapter{
    private Context context;
    private List<SelectedExamSubjectList> list;

    public SelectedExamSubjectAdapter(Context context, List<SelectedExamSubjectList> list){
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
        View v =View.inflate(context, R.layout.select_exam_elements_container, null);
        TextView subjectname_textView = (TextView) v.findViewById(R.id.ExamEachNameTextView);
        TextView exam_name_textView = (TextView) v.findViewById(R.id.ExamType_textView);

        final String exam_name = list.get(i).getExam_name();
        final String subject_name = list.get(i).getSubject_name();
        final String subject_code = list.get(i).getSubject_code();
        final String subject_number = list.get(i).getSubject_number();

        subjectname_textView.setText(subject_name);
        exam_name_textView.setText(exam_name);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("subject_name", subject_name);
                resultIntent.putExtra("subject_code", subject_code);
                resultIntent.putExtra("subject_number", subject_number);
                ((SelectExamActivity)context).setResult(RESULT_OK, resultIntent);
                ((SelectExamActivity)context).finish();
            }
        });

        return v;
    }

}


