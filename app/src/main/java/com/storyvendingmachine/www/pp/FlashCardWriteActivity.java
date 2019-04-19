package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_name;
import static com.storyvendingmachine.www.pp.MainActivity.major_exam_type_code;
import static com.storyvendingmachine.www.pp.REQUESTCODES.REQUEST_CODE_FLASHCARD_REVISE;

public class FlashCardWriteActivity extends AppCompatActivity {
    final static int RESULT_CODE_SELECT_EXAM = 30001;
    final static int RESULT_CODE_SELECT_SUBJECT = 30002;

    ListView listView;
    static List<FlashCardWriteList> flashcardwriteList;
    static FlashCardWriteListAdapter flashCardWriteListAdapter;

    FloatingActionButton fab;
    EditText flashcard_title_editText;
    Button flashcard_select_exam_button;
    Button flashcard_select_subject_button;

    String selected_exam_name;
    String selected_exam_code;

    String subject_name;
    String subject_code;
    String subject_number;
    String flashcard_db_id;


    JSONObject header_jsonObject;
    JSONObject jsonObjectTotal;

    String law_minor_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(major_exam_type_code.equals("lawyer")){
            setTheme(R.style.PassPopLawTheme);
        }else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card_write);

        ExamTypeInitializer();
    }

    public void ExamTypeInitializer(){
        Intent intent = getIntent();
        if(major_exam_type_code.equals("lawyer")){
            String type = intent.getStringExtra("type");
            if(type.equals("revise")){
                //type = revise
                Log.e("enter", "revise");
                toolbar();
            }else{
                //type = new
                Log.e("enter", "new");
                toolbar();
                LAW_initializer();
                LAW_headerContent();
            }
        }else{
            // 산업기사/기사
            String type = intent.getStringExtra("type");
            if(type.equals("revise")){
                //type = revise
                Log.e("enter", "revise");
                toolbar();
                initializer_revise(intent);
            }else{
                //type = new
                Log.e("enter", "new");
                toolbar();
                initializer();
            }
        }
    }

    public void LAW_initializer(){
        law_minor_type = "empty";

        header_jsonObject = new JSONObject();
        jsonObjectTotal = new JSONObject();

        listView = (ListView) findViewById(R.id.flashcardwrite_listView);
        flashcardwriteList = new ArrayList<FlashCardWriteList>();
        flashCardWriteListAdapter = new FlashCardWriteListAdapter(this, flashcardwriteList);
        listView.setAdapter(flashCardWriteListAdapter);

        fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        fab.setColorNormal(getResources().getColor(R.color.colorCrimsonRed));
        fab.setColorPressed(getResources().getColor(R.color.colorCrimsonRed));
        fab.setColorRipple(getResources().getColor(R.color.colorCrimsonRed));
        fab.attachToListView(listView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.smoothScrollToPosition(0);
            }
        });

        addFlashCardContainer(); // 첫번째 플래시 카드 만들기;
        uploadButtonProcessDeco("new", "작성");
    }

    public void LAW_headerContent(){
        View headerView = getLayoutInflater().inflate(R.layout.law_container_flashcard_write_header, null);
        flashcard_title_editText = (EditText) headerView.findViewById(R.id.flashcard_write_title_editText);
        flashcard_select_exam_button = (Button) headerView.findViewById(R.id.flashcard_select_exam_button);

        flashcard_select_exam_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subjectSelectDialog(flashcard_select_exam_button);
            }
        });
        listView.addHeaderView(headerView);
    }
    public void subjectSelectDialog(final Button button){
        final CharSequence list[] = new CharSequence[]{"전체", "#공법", "#형사법", "#민사법"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("과목 선택");
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        button.setText(list[0]);
                        law_minor_type = "all_subject";
                        break;
                    case 1:
                        button.setText(list[1]);
                        law_minor_type = "minor_2001";
                        break;
                    case 2:
                        button.setText(list[2]);
                        law_minor_type = "minor_2002";
                        break;
                    case 3:
                        button.setText(list[3]);
                        law_minor_type = "minor_2003";
                        break;
                }
            }
        });
        builder.show();
    }


    public void initializer(){
        flashcard_db_id="null";
        selected_exam_name = exam_selection_name;
        selected_exam_code = exam_selection_code;
        subject_name="전체";
        subject_code="0";
        subject_number="all_subjects";


        listView = (ListView) findViewById(R.id.flashcardwrite_listView);
        flashcardwriteList = new ArrayList<FlashCardWriteList>();
        flashCardWriteListAdapter = new FlashCardWriteListAdapter(this, flashcardwriteList);
        listView.setAdapter(flashCardWriteListAdapter);
        fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        fab.attachToListView(listView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.smoothScrollToPosition(0);
            }
        });

        header_jsonObject = new JSONObject();
        jsonObjectTotal = new JSONObject();

        headerContent();
        uploadButtonProcessDeco("new", "작성");

        addFlashCardContainer();
    }
    public void initializer_revise(Intent intent){
        subject_code="0";
        listView = (ListView) findViewById(R.id.flashcardwrite_listView);
        flashcardwriteList = new ArrayList<FlashCardWriteList>();
        flashCardWriteListAdapter = new FlashCardWriteListAdapter(this, flashcardwriteList);
        listView.setAdapter(flashCardWriteListAdapter);
        fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        fab.attachToListView(listView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.smoothScrollToPosition(0);
            }
        });
        header_jsonObject = new JSONObject();
        jsonObjectTotal = new JSONObject();
        flashcard_db_id = intent.getStringExtra("flashcard_db_id");
        getSelectedFlashCard(flashcard_db_id);

        uploadButtonProcessDeco("revise", "수정");

    }
    public void toolbar(){
        if(major_exam_type_code.equals("lawyer")){
            Toolbar tb = (Toolbar) findViewById(R.id.FlashCardWrite_Toolbar);
            tb.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
            getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
        }else{
            Toolbar tb = (Toolbar) findViewById(R.id.FlashCardWrite_Toolbar);
            tb.setElevation(5);
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
            getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
        }
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_flashcard_container_add, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.flashcard_Add){
                addFlashCardContainer();
        }else{
            onBackPressed();
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE_SELECT_EXAM) {
            if (resultCode == RESULT_OK) {
                selected_exam_name = data.getStringExtra("exam_name");
                selected_exam_code = data.getStringExtra("exam_code");
                flashcard_select_exam_button.setText("시험 선택 : "+selected_exam_name);
                try {
                    jsonObjectTotal.put("exam_name", selected_exam_name);
                    jsonObjectTotal.put("exam_code", selected_exam_code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }else if(requestCode == RESULT_CODE_SELECT_SUBJECT){
            if (resultCode == RESULT_OK) {
                subject_name = data.getStringExtra("subject_name");
                subject_code = data.getStringExtra("subject_code");
                subject_number = data.getStringExtra("subject_number");
                flashcard_select_subject_button.setText("과목 선택 : "+subject_name);
                try {
                    jsonObjectTotal.put("subject_number", subject_number);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
//        Session.getCurrentSession().removeCallback(callback);
        super.onBackPressed();  // optional depending on your needs
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }


    // ************************      new write flashcard ***************************
    public void addFlashCardContainer(){
        FlashCardWriteList elements = new FlashCardWriteList("", "");
        flashcardwriteList.add(elements);
        flashCardWriteListAdapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(flashCardWriteListAdapter.getCount());
    }

    public void headerContent(){
        View headerView = getLayoutInflater().inflate(R.layout.container_flashcard_write_header, null);
        flashcard_title_editText = (EditText) headerView.findViewById(R.id.flashcard_write_title_editText);
        flashcard_select_exam_button = (Button) headerView.findViewById(R.id.flashcard_select_exam_button);
        flashcard_select_subject_button = (Button) headerView.findViewById(R.id.flashcard_select_subject_button);

        flashcard_select_exam_button.setText("시험 선택 : "+exam_selection_name);
        flashcard_select_exam_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FlashCardWriteActivity.this, SelectExamActivity.class);
                intent.putExtra("from", "flashcard_write_activity");
                startActivityForResult(intent, RESULT_CODE_SELECT_EXAM);
                slide_left_and_slide_in();
            }
        });
        flashcard_select_subject_button.setText("과목 선택 : 전체");
        flashcard_select_subject_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FlashCardWriteActivity.this, SelectExamActivity.class);
                intent.putExtra("from", "flashcard_write_activity_select_subject");
                intent.putExtra("selected_exam_code", selected_exam_code);
                intent.putExtra("selected_exam_name", selected_exam_name);
                startActivityForResult(intent, RESULT_CODE_SELECT_SUBJECT);
                slide_left_and_slide_in();
            }
        });

        listView.addHeaderView(headerView);
    }
    public void uploadButtonProcessDeco(final String flashcard_write_type, String button_name){
        final Button upload_button = new Button(this);
        upload_button.setText(button_name);
        if(major_exam_type_code.equals("lawyer")){
            upload_button.setBackgroundColor(getResources().getColor(R.color.colorCrimsonRed));
            upload_button.setTextColor(getResources().getColor(R.color.colorWhite));
        }else{
            upload_button.setBackgroundColor(getResources().getColor(R.color.colorExamViewMainDark));
            upload_button.setTextColor(getResources().getColor(R.color.colorBlack));
        }
        listView.addFooterView(upload_button);

        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1) TITLE( 제목 ) 이 작성되었는지 확인한다.
                // 2) FLASHCARD ( 플레시카드 ) 모든 플래시카드에 빈 공간이 없는지 확인한다.
                // 1) 2) 중 하나라도 EMPTY 가 존재하면 업로드를 ALERT MESSAGE 를 띠운다.
                if(flashcard_title_editText.getText().toString().length() <= 1 ){
                    // 만약 제목의 길이가 1보다 작거나 같으면 ALERT 메시지를 띄워서 업로드를 막는다.
                    String message = "제목을 입력해주세요. 또는 제목을 두 글자 이상 입력해주세요";
                    String confirm_button = "확인";
                    empty_notifier(message, confirm_button);
                }else {

                        if (checkifmissingcontainer()) {
                            //모든 플래시카드 컨테이너들에 내용이 들어가있다.
                            JSONArray jsonArray = new JSONArray();
                            for (int i = 0; i < flashcardwriteList.size(); i++) {
                                JSONObject jsonObject = new JSONObject();
                                String term = flashcardwriteList.get(i).getTerm();
                                String def = flashcardwriteList.get(i).getDef();
                                try {
                                    jsonObject.put("term", changeLineTransform(term));
                                    jsonObject.put("definition", changeLineTransform(def));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                jsonArray.put(jsonObject);
                            }
                            try {
                                jsonObjectTotal.put("title", changeLineTransform(flashcard_title_editText.getText().toString()));
                                if (major_exam_type_code.equals("lawyer")) {
                                    if (law_minor_type.equals("empty")) {
                                        String message = "시험과목을 선택해주세요";
                                        String confirm_button = "확인";
                                        empty_notifier(message, confirm_button);
                                    }else{
                                        jsonObjectTotal.put("minor_type", law_minor_type);
                                        jsonObjectTotal.put("flashcards", jsonArray);
                                        if (flashcard_write_type.equals("new")) {
                                            String message = "플래시카드를 업로드 하시겠습니까?";
                                            String positive_message = "확인";
                                            String negative_message = "취소";
                                            notifier_private_public(message, positive_message,
                                                    negative_message, jsonObjectTotal, flashcard_write_type);
                                        } else {
                                            String message = "플래시카드를 수정 하시겠습니까?";
                                            String positive_message = "확인";
                                            String negative_message = "취소";
                                            notifier_private_public(message, positive_message,
                                                    negative_message, jsonObjectTotal, flashcard_write_type);

                                            Toast.makeText(FlashCardWriteActivity.this, "수정", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    jsonObjectTotal.put("exam_code", selected_exam_code);
                                    jsonObjectTotal.put("subject_number", subject_number);
                                    jsonObjectTotal.put("flashcards", jsonArray);
                                    if (flashcard_write_type.equals("new")) {
                                        String message = "플래시카드를 업로드 하시겠습니까?";
                                        String positive_message = "확인";
                                        String negative_message = "취소";
                                        notifier_private_public(message, positive_message,
                                                negative_message, jsonObjectTotal, flashcard_write_type);
                                    } else {
                                        String message = "플래시카드를 수정 하시겠습니까?";
                                        String positive_message = "확인";
                                        String negative_message = "취소";
                                        notifier_private_public(message, positive_message,
                                                negative_message, jsonObjectTotal, flashcard_write_type);

                                        Toast.makeText(FlashCardWriteActivity.this, "수정", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //플래시카드 컨테이너에 "빈" 곳이있다.
                            String message = "내용이 없는 플래시카드가 존재합니다. 내용을 입력해주세요.";
                            String confirm_button = "확인";
                            empty_notifier(message, confirm_button);
                        }

                }
            }
        });
    }
    public void notifier_private_public(String message, String positive_message,
                                        String negative_message, final JSONObject jsonObjectTotal, final String flashcard_write_type){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(major_exam_type_code.equals("lawyer")){
                            LAW_uploadWrittenFlashCard(jsonObjectTotal, flashcard_write_type);
                        }else{
                            uploadWrittenFlashCard(jsonObjectTotal, flashcard_write_type);
                        }
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

    public boolean checkifmissingcontainer(){
        boolean isNotEmpty = true;
        for(int i = 0; i < flashcardwriteList.size(); i++){
            String term = flashcardwriteList.get(i).getTerm();
            String def = flashcardwriteList.get(i).getDef();
            if(term.length() <=0 || def.length() <=0){
                isNotEmpty = false;
            }
        }
        return isNotEmpty;
    }
    public void uploadWrittenFlashCard(final JSONObject jsonObject, final String flashcard_write_type){
        final String jsonObject_str = jsonObject.toString();
        RequestQueue queue = Volley.newRequestQueue(FlashCardWriteActivity.this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/uploadWrittenFlashCard.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response :::", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                String upload_result = jsonObject.getString("response");
                                if(upload_result.equals("upload_success")){
                                    String mes = "플래시 카드를 성공적으로 업로드 하였습니다.";
                                    String pos_mes = "확인";
                                    notifier_new_revise_confirm(mes, pos_mes);
                                }else if(upload_result.equals("update_success")){
                                    String mes = "플래시 카드를 성공적으로 업데이트 하였습니다.";
                                    String pos_mes = "확인";
                                    notifier_new_revise_confirm(mes, pos_mes);
                                }else{
                                    // upload_ update_ fail
                                }

                            }else if(access_token.equals("invalid")){
                                Toast.makeText(FlashCardWriteActivity.this,"잘못된 접근입니다", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(FlashCardWriteActivity.this,"error", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
//                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
//                        toast(message);
//                        getExamNameAndCode(input_exam_name); // 인터넷 에러가 났을시 다시 한번 시도한다.
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("exam_code", selected_exam_code);
                params.put("subject", subject_number);
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                params.put("flashcard_data", jsonObject_str);
                params.put("flashcard_write_type", flashcard_write_type);
                params.put("flashcard_db_id", flashcard_db_id);
//                params.put("public_private", public_private);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void LAW_uploadWrittenFlashCard(final JSONObject jsonObject, final String flashcard_write_type){
        final String jsonObject_str = jsonObject.toString();
        RequestQueue queue = Volley.newRequestQueue(FlashCardWriteActivity.this);
        String url = "http://www.joonandhoon.com/pp/passpop_law/android/server/upload_update.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response :::", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
//                                String upload_result = jsonObject.getString("response");
//                                if(upload_result.equals("upload_success")){
//                                    String mes = "플래시 카드를 성공적으로 업로드 하였습니다.";
//                                    String pos_mes = "확인";
//                                    notifier_new_revise_confirm(mes, pos_mes);
//                                }else if(upload_result.equals("update_success")){
//                                    String mes = "플래시 카드를 성공적으로 업데이트 하였습니다.";
//                                    String pos_mes = "확인";
//                                    notifier_new_revise_confirm(mes, pos_mes);
//                                }else{
//                                    // upload_ update_ fail
//                                }
                            }else if(access_token.equals("invalid")){
                                Toast.makeText(FlashCardWriteActivity.this,"잘못된 접근입니다", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(FlashCardWriteActivity.this,"error", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
//                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
//                        toast(message);
//                        getExamNameAndCode(input_exam_name); // 인터넷 에러가 났을시 다시 한번 시도한다.
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("upload_type", "law_flashcard_upload_update");
                params.put("flashcard_write_type", flashcard_write_type); // new or revise
                params.put("minor_type", law_minor_type);
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                params.put("flashcard_data", jsonObject_str);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    // ************************      new write flashcard ***************************


    // ************************      revise write flashcard ***************************
    public void getSelectedFlashCard(final String flashcard_db_id){
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/getSelectedFlashCard.php";
        RequestQueue queue = Volley.newRequestQueue(FlashCardWriteActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("flashcard_response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                JSONObject object = jsonObject.getJSONObject("response");
                                String exam_code = object.getString("exam_code");
                                String exam_name = object.getString("exam_name");
                                String subject_code = object.getString("subject_code");
                                String subject_name = object.getString("subject_name");
                                String author_login_type = object.getString("author_login_type");
                                String author_id = object.getString("author_id");
                                String author_nickname = object.getString("author_nickname");
                                String upload_date = object.getString("upload_date");
                                String upload_time = object.getString("upload_time");
                                String title = object.getString("title");

                                selected_exam_name = exam_name;
                                selected_exam_code = exam_code;
                                subject_name=subject_name;
                                subject_number=subject_code;

                                revise_headerContent( title,  exam_code,  exam_name,  subject_name,  subject_code);

                                JSONArray flashcard_json = jsonObject.getJSONObject("response").getJSONArray("flashcards");
                                int count = flashcard_json.length();

                                ArrayList<String> flashcards = new ArrayList<>();
                                for(int i = 0 ; i<flashcard_json.length(); i++){
                                    String term = flashcard_json.getJSONObject(i).getString("term");
                                    String definition = flashcard_json.getJSONObject(i).getString("definition");

                                    flashcards.add(flashcard_json.getJSONObject(i).getString("term"));
                                    flashcards.add(flashcard_json.getJSONObject(i).getString("definition"));

                                    FlashCardWriteList list = new FlashCardWriteList(term, definition);
                                    flashcardwriteList.add(list);
                                }
                                flashCardWriteListAdapter.notifyDataSetChanged();
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
    public void revise_headerContent(String title, String exam_code, String exam_name, String subject_name, String subject_code){
        View headerView = getLayoutInflater().inflate(R.layout.container_flashcard_write_header, null);
        flashcard_title_editText = (EditText) headerView.findViewById(R.id.flashcard_write_title_editText);
        flashcard_select_exam_button = (Button) headerView.findViewById(R.id.flashcard_select_exam_button);
        flashcard_select_subject_button = (Button) headerView.findViewById(R.id.flashcard_select_subject_button);

        flashcard_title_editText.setText(title);
        flashcard_select_exam_button.setText("시험 선택 : "+exam_name);
        flashcard_select_exam_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(FlashCardWriteActivity.this, SelectExamActivity.class);
//                intent.putExtra("from", "flashcard_write_activity");
//                startActivityForResult(intent, RESULT_CODE_SELECT_EXAM);
//                slide_left_and_slide_in();

                String mes = "시험을 수정할수 없습니다.";
                String pos_mes = "확인";
                empty_notifier(mes, pos_mes);
            }
        });
        flashcard_select_subject_button.setText("과목 선택 : "+subject_name);
        flashcard_select_subject_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FlashCardWriteActivity.this, SelectExamActivity.class);
                intent.putExtra("from", "flashcard_write_activity_select_subject");
                intent.putExtra("selected_exam_code", selected_exam_code);
                intent.putExtra("selected_exam_name", selected_exam_name);
                startActivityForResult(intent, RESULT_CODE_SELECT_SUBJECT);
                slide_left_and_slide_in();
            }
        });

        listView.addHeaderView(headerView);

    }
    // ************************      revise write flashcard ***************************







    public void notifier_new_revise_confirm(String message, String positivie_message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(positivie_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setResult(RESULT_OK);
                        finish();
                        overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
                    }
                })
                .create()
                .show();
    }
    public void empty_notifier(String message, String confirm_button){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(confirm_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }
    public String changeLineTransform(String input_str){
        String output_str = input_str.replace("\n", "<br>");
        return output_str;
    }
    public void slide_left_and_slide_in(){//opening new activity
        overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }


}
