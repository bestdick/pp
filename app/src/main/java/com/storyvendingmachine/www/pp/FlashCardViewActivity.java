package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;

public class FlashCardViewActivity extends AppCompatActivity {
    DrawerLayout drawer;
    NavigationView navigationView;

    ProgressBar pb;
    LinearLayout progressBarBackground;

    LinearLayout scrap_folder_layout;
    LinearLayout comment_layout;

    TextView hit_count_textView, like_count_textView, scrap_count_textView, flashcard_author_textView, flashcard_written_date_textView, comment_write_textView;

    private FlashCardViewActivityViewPagerAdapter fViewPagerAdapter;
    private ViewPager fviewPager;

    static JSONObject global_flashcard_jsonObject;
    static JSONArray global_flashcard_my_jsonArray;

    String flashcard_view_type;//공용 변수
    String flashcard_title, flashcard_db_id, flashcard_exam_name, flashcard_subject_name;//regular 일때 사용하는 변수들
    String folder_name, folder_code, folder_flashcard_length; // my_folder일때 사용하는 변수


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card_view);

        Intent intent = getIntent();
        flashcard_view_type = intent.getStringExtra("type");
        if(flashcard_view_type.equals("regular")){
            flashcard_exam_name =  intent.getStringExtra("exam_name");
            flashcard_subject_name = intent.getStringExtra("subject_name");
            flashcard_title = intent.getStringExtra("flashcard_title");
            flashcard_db_id = intent.getStringExtra("flashcard_db_id");

            pb = findViewById(R.id.flashcard_activity_progress_bar);
            progressBarBackground = (LinearLayout) findViewById(R.id.progress_bar_background);
            progressbar_visible();
            new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                @Override
                public void run() {
                    progressbar_invisible();
                }
            }, 2500); // 2.5초 후에 실행됨

            comment_layout = findViewById(R.id.flashcard_comment_layout);
            scrap_folder_layout = findViewById(R.id.scrap_folder_layout);

            hit_count_textView = (TextView) findViewById(R.id.hit_count_textView);
            like_count_textView = (TextView) findViewById(R.id.like_count_textView);
            scrap_count_textView = (TextView) findViewById(R.id.scrap_count_textView);
            flashcard_author_textView = (TextView) findViewById(R.id.flashcard_author_textView);
            flashcard_written_date_textView = (TextView) findViewById(R.id.flashcard_written_date_textView);

            drawer = (DrawerLayout) findViewById(R.id.drawer);

            BackgroundTask backgroundTask  = new BackgroundTask();
            backgroundTask.execute();

            toolbar(flashcard_title);
            comment_write_textView = (TextView) findViewById(R.id.comment_write_textView);
            if(LoginType.equals("kakao") || LoginType.equals("normal")){
                likeButtonClicked();

                comment_write_textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FlashCardViewActivity.this, ExamNoteWriteActivity.class);
                        intent.putExtra("type", "flashcard_comment");
                        intent.putExtra("flashcard_exam_name", flashcard_exam_name);
                        intent.putExtra("flashcard_subject_name", flashcard_subject_name);
                        intent.putExtra("flashcard_title", flashcard_title);
                        intent.putExtra("flashcard_db_id", flashcard_db_id);
//                        startActivity(intent);
                        startActivityForResult(intent, 20001);//20001 mean flashcard comment change result.
                        slide_left_and_slide_in();
                    }
                });
            }else{
                likeButtonClicked();
                comment_write_textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String message = "로그인 후에 사용 가능한 메뉴 입니다.";
                        String positivie_message = "확인";
                        notifier(message, positivie_message);
                    }
                });
                Toast.makeText(this, "로그인 후에 사용 가능", Toast.LENGTH_SHORT).show();
            }
        }else{
            //----------------my_folder--my_folder--my_folder---my_folder--my_folder--my_folder------------
            Toast.makeText(this, "folder 접속", Toast.LENGTH_SHORT).show();
            folder_name = intent.getStringExtra("folder_name");
            folder_code = intent.getStringExtra("folder_code");
            folder_flashcard_length = intent.getStringExtra("flashcard_length");

            ConstraintLayout like_scrap_hit_layout = (ConstraintLayout) findViewById(R.id.like_scrap_hit_layout);
            LinearLayout flashcard_comment_layout = (LinearLayout) findViewById(R.id.flashcard_comment_layout);
            like_scrap_hit_layout.setVisibility(View.GONE);
            flashcard_comment_layout.setVisibility(View.GONE);
//
//            ViewPager flashcard_container = (ViewPager) findViewById(R.id.flashcard_container);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
//            flashcard_container.setLayoutParams(params);
            ConstraintLayout outter_most_layout = (ConstraintLayout) findViewById(R.id.outter_most_layout);
            outter_most_layout.setBackgroundColor(getResources().getColor(R.color.colorWhiteSmoke));
            toolbar(folder_name);
            getSelectedMyFlashCard();
        }


    }

    public void getSelectedMyFlashCard(){
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/getSelectedMyFlashCard.php";
        RequestQueue queue = Volley.newRequestQueue(FlashCardViewActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("flashcard_response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            global_flashcard_my_jsonArray = jsonObject.getJSONArray("response");
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                JSONArray array = jsonObject.getJSONArray("response");
                                int count = (array.length()*2);
                                ArrayList<String> flashcards = new ArrayList<>();
                                for(int i = 0 ; i<array.length(); i++){
                                    flashcards.add(array.getJSONObject(i).getString("term"));
                                    flashcards.add(array.getJSONObject(i).getString("definition"));
                                }

                                fViewPagerAdapter = new FlashCardViewActivityViewPagerAdapter(getSupportFragmentManager());
                                fViewPagerAdapter.count = count;
                                fViewPagerAdapter.flashcard_array = flashcards;
                                fViewPagerAdapter.exam_name = "";
                                fViewPagerAdapter.subject_name = "";
                                fViewPagerAdapter.solo_page = true;
                                fViewPagerAdapter.flashcard_or_folder = "folder";



                                fviewPager = (ViewPager) findViewById(R.id.flashcard_container);
                                fviewPager.setAdapter(fViewPagerAdapter);
                                fviewPager.setOffscreenPageLimit(count);

//                                String author_nickname = jsonObject.getJSONObject("response").getString("author_nickname");
//                                String upload_date = jsonObject.getJSONObject("response").getString("upload_date");

//                                flashcard_author_textView.setText("작성자 "+author_nickname);
//                                flashcard_written_date_textView.setText("작성일 "+upload_date);
                            }else if(access_token.equals("invalid")){

                            }else{

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
                //                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
                //                        toast(message);
                //                        getExamNameAndCode(); // 인터넷 에러가 났을시 다시 한번 시도한다.

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("flashcard_folder_name", folder_name);
                params.put("flashcard_folder_code", folder_code);
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    private void toolbar(String title_message){
        Toolbar tb = (Toolbar) findViewById(R.id.loggedin_toolBar);
        tb.setElevation(5);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리


        TextView exam_title_TextView = (TextView) tb.findViewById(R.id.flashcard_titleTextView);
        exam_title_TextView.setText(title_message);
    }
    public void likeButtonClicked(){
//        TextView like_button = (TextView) findViewById(R.id.like_count_textView);
        like_count_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginType.equals("null") || G_user_id.equals("null")){
                    String message = "로그인 하셔야 사용할수 있는 메뉴 입니다.";
                    String positive_message = "확인";
                    notifier(message, positive_message);
                }else{
                    updateLike();
                }

            }
        });
    }
    public void notifier(String message, String positive_message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }
    public void notifierAddToFlashCardFolder(String message, String positive_message, String negative_message, final String folder_code, final TextView textView){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateFlashCardInToFolder(folder_code, "add", textView);
                    }
                })
                .setNegativeButton(negative_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }
    public void updateFlashCardInToFolder(final String folder_code, final String action, final TextView textView){  /// action == "ADD" or "DELETE"
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/updateFlashCardInToFolder.php";
        RequestQueue queue = Volley.newRequestQueue(FlashCardViewActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("add to folder response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                String result = jsonObject.getString("response");
                                if(result.equals("success")){
                                    //성공적으로 업로드
                                    String count_flashcard_in_folder = jsonObject.getString("count_flashcard_in_folder");
                                    String count_flashcard_scrapped = jsonObject.getString("count_flashcard_scrapped");
                                    textView.setText("( "+ count_flashcard_in_folder+ " ) ");
                                    scrap_count_textView.setText("스크랩 +"+count_flashcard_scrapped);

                                    String toast_message = "해당 폴더에 플래쉬 카드를 추가하였습니다.";
                                    toast(toast_message);
                                }else if(result.equals("exist")){
                                    // 이미 존재한다
                                    String toast_message = "해당 폴더에 현재 플래쉬 카드가 이미 존재합니다.";
                                    toast(toast_message);
                                }else{
                                    //업로드 실패
                                }
                            }else if(access_token.equals("invalid")){

                            }else{

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
                //                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
                //                        toast(message);
                //                        getExamNameAndCode(); // 인터넷 에러가 났을시 다시 한번 시도한다.

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                params.put("flashcard_db_id", flashcard_db_id);
                params.put("folder_code", folder_code);
                params.put("action", action);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    private void folder_create_editable_alertDialog(){
        AlertDialog.Builder ad = new AlertDialog.Builder(FlashCardViewActivity.this);
        ad.setTitle("폴더 추가");
        ad.setMessage("폴더 이름을 적어주세요");
        LinearLayout linearLayout = new LinearLayout(FlashCardViewActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(16,0,16,0);

        final EditText editText = new EditText(FlashCardViewActivity.this);
        final TextView count_textView = new TextView(FlashCardViewActivity.this);
        count_textView.setGravity(Gravity.RIGHT);
        count_textView.setText("0/80");

        linearLayout.addView(editText);
        linearLayout.addView(count_textView);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                count_textView.setText(charSequence.length()+"/80");
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ad.setView(linearLayout);
        ad.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String input_folder_name = editText.getText().toString();
                uploadNewlyCreatedFolder(input_folder_name);
            }
        });
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        ad.show();
    }
    private void uploadNewlyCreatedFolder(final String folder_name){
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/uploadNewlyCreatedFolder.php";
        RequestQueue queue = Volley.newRequestQueue(FlashCardViewActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Newly Created Folder", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                String result = jsonObject.getString("response");
                                if(result.equals("success")){
                                    //성공적으로 업로드

                                }else{
                                    //업로드 실패
                                }
                            }else {
                                //token invalid

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
                //                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
                //                        toast(message);
                //                        getExamNameAndCode(); // 인터넷 에러가 났을시 다시 한번 시도한다.

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                params.put("folder_name", folder_name);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void getScrapFolderName(){
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/getScrapFolderName.php";
        RequestQueue queue = Volley.newRequestQueue(FlashCardViewActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("scrap response", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");

                            if(access_token.equals("valid")){
                                JSONArray jsonArray = jsonObject.getJSONArray("response");
//                              기본 폴더 세팅
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(0,16,0,16); // left top right bottom
                                TextView name_of_folder_textView = new TextView(FlashCardViewActivity.this);
                                name_of_folder_textView.setText("폴더 리스트");
                                name_of_folder_textView.setPadding(25, 25,25,25);
                                name_of_folder_textView.setLayoutParams(params);
                                name_of_folder_textView.setGravity(Gravity.CENTER);
                                name_of_folder_textView.setBackground(getResources().getDrawable(R.drawable.outline_solid_round_orange));
                                scrap_folder_layout.addView(name_of_folder_textView);


                                if(jsonArray.length() != 0) {
//                              기본 폴더 세팅
                                    String basic_folder_count = jsonObject.getString("basic_folder_flashcard_count");
                                    View folder_container_basic = getLayoutInflater().inflate(R.layout.container_flashcard_scrap_folder, null);
                                    final TextView folder_name_textView_basic = folder_container_basic.findViewById(R.id.scrap_folder_textView);
                                    final TextView scrap_count_textView_basic = folder_container_basic.findViewById(R.id.scrap_count_textView);

                                    folder_name_textView_basic.setText("기본 폴더");
                                    scrap_count_textView_basic.setText("( " + basic_folder_count + " ) ");
                                    scrap_folder_layout.addView(folder_container_basic);

                                    folder_container_basic.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
//                                        Toast.makeText(FlashCardViewActivity.this, "기본 폴더", Toast.LENGTH_SHORT).show();
                                            String message = "기본 폴더에 해당 플래쉬 카드를 넣으시겠습니까?";
                                            String positive_message = "네";
                                            String negative_message = "아니요";
                                            notifierAddToFlashCardFolder(message, positive_message, negative_message, "0", scrap_count_textView_basic);
                                        }
                                    });


                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        View folder_container = getLayoutInflater().inflate(R.layout.container_flashcard_scrap_folder, null);
                                        final TextView folder_name_textView = folder_container.findViewById(R.id.scrap_folder_textView);
                                        final TextView scrap_count_textView = folder_container.findViewById(R.id.scrap_count_textView);

                                        final String folder_name = jsonArray.getJSONObject(i).getString("scrap_folder_name");
                                        final String folder_code = jsonArray.getJSONObject(i).getString("scrap_folder_code");
                                        String total_flashcards_infolder = jsonArray.getJSONObject(i).getString("total_flashcads_in_folder");

                                        folder_name_textView.setText(folder_name);
                                        scrap_count_textView.setText("( " + total_flashcards_infolder + " ) ");

                                        scrap_folder_layout.addView(folder_container);

                                        folder_container.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
//                                            Toast.makeText(FlashCardViewActivity.this, folder_code, Toast.LENGTH_SHORT).show();
                                                String message = folder_name + "에 해당 플래쉬 카드를 넣으시겠습니까?";
                                                String positive_message = "네";
                                                String negative_message = "아니요";
                                                notifierAddToFlashCardFolder(message, positive_message, negative_message, folder_code, scrap_count_textView);
                                            }
                                        });


                                    }
                                }else{
                                    String basic_folder_count = jsonObject.getString("basic_folder_flashcard_count");
                                    View folder_container_basic = getLayoutInflater().inflate(R.layout.container_flashcard_scrap_folder, null);
                                    final TextView folder_name_textView_basic = folder_container_basic.findViewById(R.id.scrap_folder_textView);
                                    final TextView scrap_count_textView_basic = folder_container_basic.findViewById(R.id.scrap_count_textView);

                                    folder_name_textView_basic.setText("기본 폴더");
                                    scrap_count_textView_basic.setText("( " + basic_folder_count + " ) ");
                                    scrap_folder_layout.addView(folder_container_basic);

                                    folder_container_basic.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
//                                        Toast.makeText(FlashCardViewActivity.this, "기본 폴더", Toast.LENGTH_SHORT).show();
                                            String message = "기본 폴더에 해당 플래쉬 카드를 넣으시겠습니까?";
                                            String positive_message = "네";
                                            String negative_message = "아니요";
                                            notifierAddToFlashCardFolder(message, positive_message, negative_message, "0", scrap_count_textView_basic);
                                        }
                                    });
                                }

                                Button create_folder = new Button(FlashCardViewActivity.this);
                                create_folder.setText("폴더 추가 + ");
                                create_folder.setPadding(25, 15,25,15);
                                create_folder.setLayoutParams(params);
                                create_folder.setGravity(Gravity.CENTER);
                                create_folder.setBackground(getResources().getDrawable(R.drawable.outline_round_gray));
                                create_folder.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        folder_create_editable_alertDialog();
                                    }
                                });
                                scrap_folder_layout.addView(create_folder);

                            }else {
                                //access invalid
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
                //                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
                //                        toast(message);
                //                        getExamNameAndCode(); // 인터넷 에러가 났을시 다시 한번 시도한다.

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                params.put("flashcard_db_id", flashcard_db_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void updateLike(){
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/updateLike.php";
        RequestQueue queue = Volley.newRequestQueue(FlashCardViewActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("like or not", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                String like = jsonObject.getString("response");
                                like_count_textView.setText("좋아요 +"+like);
                            }else if(access_token.equals("invalid")){

                            }else{

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
                //                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
                //                        toast(message);
                //                        getExamNameAndCode(); // 인터넷 에러가 났을시 다시 한번 시도한다.

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                params.put("flashcard_db_id", flashcard_db_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void getThumbnailImageForAuthor(ImageView imageView, String url){
        Picasso.with(this)
                .load(url)
                .transform(new CircleTransform())
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {
                        Log.e("load image", "fail to load images ");
                    }
                });
    }
    public void getSelectedFlashCardComments_And_Others(){
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/getSelectedFlashCardComments_And_Others.php";
        RequestQueue queue = Volley.newRequestQueue(FlashCardViewActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                JSONArray jsonArray = jsonObject.getJSONArray("comment_response");
                                if(jsonArray.length() == 0){
                                    View comment_container = getLayoutInflater().inflate(R.layout.container_flashcard_comment_empty, null);
                                    TextView empty_textView = (TextView)comment_container.findViewById(R.id.empty_textView);
                                    empty_textView.setText("댓글이 존재하지 않습니다");
                                    comment_layout.addView(comment_container);
                                }else{
                                    for(int i = 0 ; i < jsonArray.length(); i++){
                                        String commenter_login_type = jsonArray.getJSONObject(i).getString("login_type");
                                        String commenter_id = jsonArray.getJSONObject(i).getString("login_type");
                                        String author_nickname = jsonArray.getJSONObject(i).getString("user_nickname");
                                        String author_thumbnail_url = jsonArray.getJSONObject(i).getString("user_thumbnail");
                                        String comment = jsonArray.getJSONObject(i).getString("comment");
                                        String upload_date = jsonArray.getJSONObject(i).getString("upload_date");

                                        View comment_container = getLayoutInflater().inflate(R.layout.container_flashcard_comment, null);

                                        TextView author_textView = (TextView) comment_container.findViewById(R.id.comment_author_textView);
                                        TextView comment_textView = (TextView) comment_container.findViewById(R.id.comment_textView);
                                        TextView upload_date_textView = (TextView) comment_container.findViewById(R.id.upload_textView);
                                        ImageView author_thumbnail_imageView = (ImageView) comment_container.findViewById(R.id.author_imageView);

                                        if(author_thumbnail_url.equals("null") || author_thumbnail_url.length()<=0){
                                            //thumnail 이 없을때
                                            author_thumbnail_imageView.setBackground(getResources().getDrawable(R.drawable.thumbnail_outline));
                                            author_thumbnail_imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_empty_thumbnail));
                                        }else{
                                            getThumbnailImageForAuthor(author_thumbnail_imageView, author_thumbnail_url);
                                        }



                                        author_textView.setText(author_nickname);
                                        comment_textView.setText(comment);
                                        upload_date_textView.setText(upload_date);

                                        comment_layout.addView(comment_container);
                                    }
                                }

                                String hit_count = jsonObject.getString("hit_count");
                                String scrap_count = jsonObject.getString("scrap_count");
                                String like_count = jsonObject.getString("like_count");



                                 hit_count_textView.setText("조회수 +"+hit_count);
                                 scrap_count_textView.setText("스크랩 +"+scrap_count);
                                 like_count_textView.setText("좋아요 +"+like_count);







                            }else if(access_token.equals("invalid")){}else{}
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
                //                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
                //                        toast(message);
                //                        getExamNameAndCode(); // 인터넷 에러가 났을시 다시 한번 시도한다.

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("flashcard_db_id", flashcard_db_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void getSelectedFlashCard(){
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/getSelectedFlashCard.php";
        RequestQueue queue = Volley.newRequestQueue(FlashCardViewActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("flashcard_response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                global_flashcard_jsonObject = jsonObject.getJSONObject("response");
                                JSONArray flashcard_json = jsonObject.getJSONObject("response").getJSONArray("flashcards");
                                int count = (jsonObject.getJSONObject("response").getJSONArray("flashcards").length()*2);//앞뒤가 있기때문에 2장을 만들어야한다.

                                ArrayList<String> flashcards = new ArrayList<>();
                                for(int i = 0 ; i<flashcard_json.length(); i++){
                                    flashcards.add(flashcard_json.getJSONObject(i).getString("term"));
                                    flashcards.add(flashcard_json.getJSONObject(i).getString("definition"));
                                }

                                fViewPagerAdapter = new FlashCardViewActivityViewPagerAdapter(getSupportFragmentManager());
                                fViewPagerAdapter.count = count;
                                fViewPagerAdapter.flashcard_array = flashcards;
                                fViewPagerAdapter.exam_name = flashcard_exam_name;
                                fViewPagerAdapter.subject_name = flashcard_subject_name;
                                fViewPagerAdapter.solo_page = true;
                                fViewPagerAdapter.flashcard_or_folder = "flashcard";



                                fviewPager = (ViewPager) findViewById(R.id.flashcard_container);
                                fviewPager.setAdapter(fViewPagerAdapter);
                                fviewPager.setOffscreenPageLimit(count);

                                String author_nickname = jsonObject.getJSONObject("response").getString("author_nickname");
                                String upload_date = jsonObject.getJSONObject("response").getString("upload_date");

                                flashcard_author_textView.setText("작성자 "+author_nickname);
                                flashcard_written_date_textView.setText("작성일 "+upload_date);
                            }else if(access_token.equals("invalid")){

                            }else{

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
                //                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
                //                        toast(message);
                //                        getExamNameAndCode(); // 인터넷 에러가 났을시 다시 한번 시도한다.

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("flashcard_db_id", flashcard_db_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20001){
            if (resultCode == RESULT_OK) {
                comment_layout.removeAllViews();
                getSelectedFlashCardComments_And_Others();
            }else if(resultCode == RESULT_CANCELED){

            }
        }else{
        }
    }



    public class BackgroundTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... integers) {
            getSelectedFlashCard();
            getSelectedFlashCardComments_And_Others();
            if(LoginType.equals("null")){
                View folder_container_basic = getLayoutInflater().inflate(R.layout.examview_exam_note_personal_container, null);
                TextView folder_name_textView_basic = folder_container_basic.findViewById(R.id.user_personal_note_textView);

                folder_name_textView_basic.setText("로그인 하시면 더 많은 기능을 사용 할 수 있습니다");
                scrap_folder_layout.addView(folder_container_basic);
            }else{
                getScrapFolderName();
            }

            return -1;
        }

        @Override
        protected void onProgressUpdate(Void... params) {
            super.onProgressUpdate(params);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }
    }
    public class BackgroundTaskMyFolderFlashCard extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... integers) {

            return -1;
        }

        @Override
        protected void onProgressUpdate(Void... params) {
            super.onProgressUpdate(params);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.flashcard_scrap_add_menu){
            if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                //drawer is open
//                drawer.closeDrawer(Gravity.LEFT);
                drawer.closeDrawer(Gravity.RIGHT);

            } else {
                //drawer is closed
                drawer.openDrawer(Gravity.RIGHT);
            }
        }else if(id == R.id.solo_view_menu){
            if(flashcard_view_type.equals("regular")){
                Intent intent = new Intent(this, FlashcardSoloViewActivity.class);
                intent.putExtra("solo_page", true);
                intent.putExtra("flashcard_or_folder", "flashcard");
                startActivity(intent);
                slide_left_and_slide_in();
            }else{
                Intent intent = new Intent(this, FlashcardSoloViewActivity.class);
                intent.putExtra("solo_page", true);
                intent.putExtra("flashcard_or_folder", "folder");
                startActivity(intent);
                slide_left_and_slide_in();
            }

        }else{
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out);// first entering // second exiting
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if(flashcard_view_type.equals("regular")){
            getMenuInflater().inflate(R.menu.flashcard_scrap_add_menu, menu);
        }else{
            getMenuInflater().inflate(R.menu.flashcard_solo_view, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }
    public void toast(String message){
        Toast toast = new Toast(FlashCardViewActivity.this);
        toast.setGravity(Gravity.CENTER, 0,0);
        TextView tv = new TextView(FlashCardViewActivity.this);
//        tv.setBackgroundColor(getResources().getColor(R.color.colorExamViewMain));
        tv.setBackground(getResources().getDrawable(R.drawable.toast_background));
        tv.setTextSize(16);
        tv.setText(message);
        toast.setView(tv);
        toast.show();
    }
    public void progressbar_visible(){
        pb.setVisibility(View.VISIBLE);
        progressBarBackground.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void progressbar_invisible(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pb.setVisibility(View.INVISIBLE);
        progressBarBackground.setVisibility(View.GONE);
    }
    public void slide_left_and_slide_in(){//opening new activity
        overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }

//    static com.melnykov.fab.FloatingActionButton fab;
//    public void floatingbutton(){
//
//        fab = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.floating_action_button);
////        fab.setImageResource(R.drawable.floating_button);
//        fab.setColorNormal(Color.parseColor("#FEDD47"));
//        fab.setColorPressed(Color.parseColor("#E3342F"));
//        fab.attachToScrollView();
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(LoginType.trim().equals("guest")){
////                    String message = "회원 가입이 필요한 메뉴 입니다";
////                    String positive_message = "확인";
////                    guest_notifier(message, positive_message);
//                }else {
////                    Intent intent = new Intent(MainActivity.this, WritingActivity.class);
//////                        startActivity(intent);
////                    startActivityForResult(intent, 2);
////                    overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit);
//                }
//
//            }
//        });
//    }
}
