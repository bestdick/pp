package com.storyvendingmachine.www.pp;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static com.storyvendingmachine.www.pp.FlashCardWriteActivity.flashCardWriteListAdapter;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;

/**
 * Created by Administrator on 2018-12-18.
 */

public class FlashCardWriteListAdapter extends BaseAdapter{
    private Context context;
    private List<FlashCardWriteList> list;

    public FlashCardWriteListAdapter(Context context, List<FlashCardWriteList> list){
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
        View v =View.inflate(context, R.layout.container_flashcard_write, null);

        TextView no = (TextView) v.findViewById(R.id.flashcard_number_textView);
        final EditText flashcard_term_editText = (EditText) v.findViewById(R.id.flashcard_term_editText);
        final TextView flashcard_term_count_textView = (TextView) v.findViewById(R.id.flashcard_term_count_textView);
        final EditText flashcard_def_editText = (EditText) v.findViewById(R.id.flashcard_def_editText);
        final TextView flashcard_def_count_textView = (TextView) v.findViewById(R.id.flashcard_def_count_textView);
        final ImageView flashcard_delete = (ImageView) v.findViewById(R.id.flashcard_delete);

        flashcard_term_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length = flashcard_term_editText.length();
                String convert = String.valueOf(length);
                flashcard_term_count_textView.setText(convert+"/1000");


            }

            @Override
            public void afterTextChanged(Editable editable) {
                list.get(i).setTerm(flashcard_term_editText.getText().toString());
            }
        });

        flashcard_def_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length = flashcard_def_editText.length();
                String convert = String.valueOf(length);
                flashcard_def_count_textView.setText(convert+"/1000");


            }

            @Override
            public void afterTextChanged(Editable editable) {
                list.get(i).setDef(flashcard_def_editText.getText().toString());
            }
        });
        flashcard_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(i);
                flashCardWriteListAdapter.notifyDataSetChanged();
            }
        });

        no.setText("No."+(i+1));
        flashcard_term_editText.setText(list.get(i).getTerm());
        flashcard_def_editText.setText(list.get(i).getDef());
        return v;
    }
}
