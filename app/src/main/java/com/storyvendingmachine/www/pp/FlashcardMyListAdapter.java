package com.storyvendingmachine.www.pp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;

/**
 * Created by Administrator on 2018-10-23.
 */

public class FlashcardMyListAdapter extends BaseAdapter {
    private Context context;
    private List<FlashCardMyList> list;

    public FlashcardMyListAdapter(Context context, List<FlashCardMyList> list){
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
        View v =View.inflate(context, R.layout.container_flashcard_my_list, null);

        final String folder_name = list.get(i).getFolder_name();
        final String folder_code = list.get(i).getFolder_code();
        final String flashcard_length = list.get(i).getFlashcard_length();

        if(LoginType.equals("null") && G_user_id.equals("null")){
            TextView folder_name_textView = (TextView) v.findViewById(R.id.folder_name_textView);
            TextView flashcard_length_textView = (TextView) v.findViewById(R.id.folder_flashcard_length_textView);

            folder_name_textView.setText("가입을 하시면 폴더 메뉴를 사용할수 있습니다.");
            flashcard_length_textView.setText("0");
            return v;
        }else {
            TextView folder_name_textView = (TextView) v.findViewById(R.id.folder_name_textView);
            TextView flashcard_length_textView = (TextView) v.findViewById(R.id.folder_flashcard_length_textView);

            folder_name_textView.setText(folder_name);
            flashcard_length_textView.setText(flashcard_length);


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, FlashCardViewActivity.class);
                    intent.putExtra("type", "my_folder");
                    intent.putExtra("folder_name", folder_name);
                    intent.putExtra("folder_code", folder_code);
                    intent.putExtra("flashcard_length", flashcard_length);
                    context.startActivity(intent);
                }
            });
            return v;
        }
    }
}
