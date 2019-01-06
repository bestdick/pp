package com.storyvendingmachine.www.pp;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.storyvendingmachine.www.pp.FlashCardViewActivity.global_flashcard_jsonObject;
import static com.storyvendingmachine.www.pp.FlashCardViewActivity.global_flashcard_my_jsonArray;

public class FlashcardSoloViewActivity extends AppCompatActivity {
    ViewPager fviewPager;
    FlashCardViewActivityViewPagerAdapter fViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_solo_view);

        ImageView close_button = (ImageView) findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        boolean solo_page = intent.getBooleanExtra("solo_page", true);
        String flashcard_or_folder = intent.getStringExtra("flashcard_or_folder");

        if(flashcard_or_folder.equals("flashcard")){
                makeFlashCard();
        }else if(flashcard_or_folder.equals("folder")){
            makeFolderFlashCard();

        }

    }

    public void makeFolderFlashCard() {
        try {
            int count = (global_flashcard_my_jsonArray.length() * 2);
            ArrayList<String> flashcards = new ArrayList<>();
            for (int i = 0; i < global_flashcard_my_jsonArray.length(); i++) {
                flashcards.add(global_flashcard_my_jsonArray.getJSONObject(i).getString("term"));
                flashcards.add(global_flashcard_my_jsonArray.getJSONObject(i).getString("definition"));
            }

            fViewPagerAdapter = new FlashCardViewActivityViewPagerAdapter(getSupportFragmentManager());
            fViewPagerAdapter.count = count;
            fViewPagerAdapter.flashcard_array = flashcards;
            fViewPagerAdapter.exam_name = "";
            fViewPagerAdapter.subject_name = "";
            fViewPagerAdapter.solo_page = false;
            fViewPagerAdapter.flashcard_or_folder = "folder";


            fviewPager = (ViewPager) findViewById(R.id.flashcard_solo_container);
            fviewPager.setAdapter(fViewPagerAdapter);
            fviewPager.setOffscreenPageLimit(count);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void makeFlashCard(){
        try {
            JSONArray flashcard_json = global_flashcard_jsonObject.getJSONArray("flashcards");
            String f_exam_name = global_flashcard_jsonObject.getString("exam_name");
            String f_subject_name = global_flashcard_jsonObject.getString("subject_name");

            int count = (flashcard_json.length()*2);//앞뒤가 있기때문에 2장을 만들어야한다.
            ArrayList<String> flashcards = new ArrayList<>();
            for(int i = 0 ; i<flashcard_json.length(); i++){
                flashcards.add(flashcard_json.getJSONObject(i).getString("term"));
                flashcards.add(flashcard_json.getJSONObject(i).getString("definition"));
            }

            fViewPagerAdapter = new FlashCardViewActivityViewPagerAdapter(getSupportFragmentManager());
            fViewPagerAdapter.count = count;
            fViewPagerAdapter.flashcard_array = flashcards;
            fViewPagerAdapter.exam_name = f_exam_name;
            fViewPagerAdapter.subject_name = f_subject_name;
            fViewPagerAdapter.solo_page = false;
            fViewPagerAdapter.flashcard_or_folder = "flashcard";


            fviewPager = (ViewPager) findViewById(R.id.flashcard_solo_container);
            fviewPager.setAdapter(fViewPagerAdapter);
            fviewPager.setOffscreenPageLimit(count);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out);// first entering // second exiting
    }

}
