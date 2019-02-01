package com.storyvendingmachine.www.pp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.Session;

import java.util.List;

import static com.storyvendingmachine.www.pp.LoginActivity.callback;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_level;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_nickname;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_thumbnail;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_name;

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
        View v_empty = View.inflate(context, R.layout.container_flashcard_comment_empty, null);

        final String folder_name = list.get(i).getFolder_name();
        final String folder_code = list.get(i).getFolder_code();
        final String flashcard_length = list.get(i).getFlashcard_length();

        if(folder_name == null && folder_code==null && flashcard_length ==null){
            TextView empty_textView = (TextView) v_empty.findViewById(R.id.empty_textView);
            empty_textView.setText("해당 시험에 플래시카드가 없습니다.\n플래시카드를 만들어 주세요");
            return v_empty;
        }else {
            if (LoginType.equals("null") && G_user_id.equals("null")) {
                TextView folder_name_textView = (TextView) v.findViewById(R.id.folder_name_textView);
                TextView flashcard_length_textView = (TextView) v.findViewById(R.id.folder_flashcard_length_textView);

                folder_name_textView.setText("회원 가입 및 로그인을 하시면 폴더 메뉴를 사용할수 있습니다.");
                flashcard_length_textView.setText("");
                return v;
            } else {
                TextView folder_name_textView = (TextView) v.findViewById(R.id.folder_name_textView);
                final TextView flashcard_length_textView = (TextView) v.findViewById(R.id.folder_flashcard_length_textView);

//                final LinearLayout delete_linLayout = (LinearLayout) v.findViewById(R.id.delete_linLayout);
//                final TextView flashcard_folder_delete_textView = (TextView) v.findViewById(R.id.flashcard_folder_delete_textView);
//                final ImageView delete_close_imageView = (ImageView) v.findViewById(R.id.delete_close_imageView);

                folder_name_textView.setText(folder_name);
                flashcard_length_textView.setText(flashcard_length);

//                v.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
////                        if(delete_linLayout.getVisibility() == View.VISIBLE){
////                            slideRight(delete_linLayout);
////                        }
//                        Intent intent = new Intent(context, FlashCardViewActivity.class);
//                        intent.putExtra("type", "my_folder");
//                        intent.putExtra("folder_name", folder_name);
//                        intent.putExtra("folder_code", folder_code);
//                        intent.putExtra("flashcard_length", flashcard_length);
//                        context.startActivity(intent);
//                        slide_left_and_slide_in();
//                    }
//                });

//                v.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View view) {
//                        slideLeft(delete_linLayout);
//
//                        flashcard_folder_delete_textView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                String message = "폴더 삭제\n폴더를 삭제하시겠습니까? 삭제하시면 폴더 안의 모든 내용이 삭제되며 복구 불가능 합니다.";
//                                String pos_message = "삭제";
//                                String neg_message = "취소";
//                                notifier(message, pos_message, neg_message, delete_linLayout);
//                                Toast.makeText(context, "삭제", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        delete_close_imageView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                slideRight(delete_linLayout);
//                                Toast.makeText(context, "삭제 닫기", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        return true;
//                    }
//                });
                return v;
            }
        }
    }

    private void notifier(String message, String positive_message, String negative_message, final LinearLayout delete_linLayout){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(negative_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        slideRight(delete_linLayout);
                    }
                })
                .create()
                .show();
    }
    public void slide_left_and_slide_in(){
        ((MainActivity) context).overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }
    public void slideLeft(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                view.getWidth(),                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(100);
//        animate.setFillAfter(true);
        view.startAnimation(animate);
    }
    public void slideRight(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                view.getWidth(),                 // toXDelta
                0,                 // fromYDelta
                0); // toYDelta
        animate.setDuration(100);
//        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.INVISIBLE);
    }
}
