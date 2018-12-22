package com.storyvendingmachine.www.pp;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2018-10-23.
 */

public class FlashcardAdapter extends BaseAdapter {
    private Context context;
    private List<FlashCardList> list;

    public FlashcardAdapter(Context context, List<FlashCardList> list){
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
        View v =View.inflate(context, R.layout.flashcard_list_container, null);
        final String flashcard_db_id = list.get(i).getFlashcard_db_id();
        final String exam_code = list.get(i).getExam_code();
        final String exam_name = list.get(i).getExam_name();
        final String subject = list.get(i).getSubject();
        final String flashcard_title = list.get(i).getTitle();
        String author = list.get(i).getUser_nickname();
        String hit = list.get(i).getHit_count();
        String scrap = list.get(i).getScrap_count();
        String like = list.get(i).getLike_count();
        String upload_date = list.get(i).getUpload_date();
        final String flashcard_count = list.get(i).getFlashcard_count();
        String first_term = list.get(i).getFlashcard_first_term();


        TextView title_textView = (TextView) v.findViewById(R.id.flashcard_title_textView);
        TextView exam_name_subject_textView = (TextView) v.findViewById(R.id.flashcard_subject_textView);
        TextView author_textView = (TextView) v.findViewById(R.id.flashcard_author_textView);
        TextView hit_textView = (TextView) v.findViewById(R.id.flashcard_hit_textView);
        TextView like_textView = (TextView) v.findViewById(R.id.flashcard_like_textView);
        TextView scrap_textView = (TextView) v.findViewById(R.id.flashcard_scrap_textView);
        TextView upload_textView = (TextView) v.findViewById(R.id.flashcard_upload_date_textView);
        ImageView new_imageView = (ImageView) v.findViewById(R.id.new_imageView);

        // 아래 코드가 하는 역활은 해당 플래쉬 카드가 1주일 안에 쓰여진 카드면 new 를 보여주고
        // 1주일보다 오래된 플래쉬 카드이면 new 를 없애는 코드이다.
        boolean isNew = identifyThisFlashCardIsNew(upload_date);
        if(!isNew){
            new_imageView.setVisibility(View.GONE);
        }

        title_textView.setText(flashcard_title+ " ( "+flashcard_count+" ) ");
        exam_name_subject_textView.setText(exam_name + " "+ subject);
        author_textView.setText("작성자  " +author);
        hit_textView.setText("조회수 "+hit);
        like_textView.setText("좋아요 "+like);
        scrap_textView.setText("펌 "+scrap);
        upload_textView.setText("작성일  " + upload_date);



        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FlashCardViewActivity.class);
                intent.putExtra("type", "regular");
                intent.putExtra("exam_code", exam_code);
                intent.putExtra("exam_name", exam_name);
                intent.putExtra("subject_name", subject);
                intent.putExtra("flashcard_title", flashcard_title);
                intent.putExtra("flashcard_db_id", flashcard_db_id);
                context.startActivity(intent);
            }
        });

        return v;
    }



    public boolean identifyThisFlashCardIsNew(String upload_date){
        String[] upload_date_array = upload_date.split("-");
        String upload_date_transform = upload_date_array[0]+upload_date_array[1]+upload_date_array[2];

        String today_date = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        SimpleDateFormat sdf_for_today = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            Date uploadDate = sdf.parse(upload_date_transform);
            long uploadDateMilli = uploadDate.getTime();

            Date todayDate = sdf_for_today.parse(today_date);
            long todayDateMilli = todayDate.getTime();
            long week = (long) (7*24*60*60*1000);
            long diff = todayDateMilli-uploadDateMilli;
            if(diff<=week){
                return true;
            }else{
                return false;
            }
//            Log.e("Milli Time", upload_date+"::"+uploadDateMilli+"////// current date--"+today_date+"::"+todayDateMilli);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    }
}
