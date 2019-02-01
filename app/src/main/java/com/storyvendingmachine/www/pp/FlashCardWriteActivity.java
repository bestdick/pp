package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
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


    JSONObject header_jsonObject;
    JSONObject jsonObjectTotal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card_write);
        toolbar();
        initializer();
    }

    public void initializer(){
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
        uploadButtonProcessDeco();

        addFlashCardContainer();
    }
    public void toolbar(){
        Toolbar tb = (Toolbar) findViewById(R.id.FlashCardWrite_Toolbar);
        tb.setElevation(5);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
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
    public void uploadButtonProcessDeco(){
        final Button upload_button = new Button(this);
        upload_button.setText("작성");
        upload_button.setBackgroundColor(getResources().getColor(R.color.colorExamViewMainDark));
        upload_button.setTextColor(getResources().getColor(R.color.colorBlack));

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
                    confirm_notifier(message, confirm_button);
                }else{
                    if(checkifmissingcontainer()){
                        //모든 플래시카드 컨테이너들에 내용이 들어가있다.
                        JSONArray jsonArray = new JSONArray();
                        for(int i = 0; i < flashcardwriteList.size(); i++){
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
                            jsonObjectTotal.put("exam_code", selected_exam_code);
                            jsonObjectTotal.put("subject_number", subject_number);
                            jsonObjectTotal.put("flashcards", jsonArray);
                            String message = "플래시카드를 업로드 하시겠습니까?";
                            String positive_message = "확인";
                            String negative_message = "취소";
                            notifier_private_public( message,  positive_message,
                                     negative_message,   jsonObjectTotal);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        //플래시카드 컨테이너에 "빈" 곳이있다.
                        String message = "내용이 없는 플래시카드가 존재합니다. 내용을 입력해주세요.";
                        String confirm_button = "확인";
                        confirm_notifier( message,  confirm_button);
                    }
                }
            }
        });
    }

    public void notifier_private_public(String message, String positive_message,
                                        String negative_message, final JSONObject jsonObjectTotal){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        uploadWrittenFlashCard(jsonObjectTotal);
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
    public void confirm_notifier(String message, String confirm_button){
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
    public void uploadWrittenFlashCard(final JSONObject jsonObject){
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
                                    onBackPressed();
                                }else{

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
//                params.put("public_private", public_private);
                return params;
            }
        };
        queue.add(stringRequest);
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



    public String changeLineTransform(String input_str){
        String output_str = input_str.replace("\n", "<br>");
        return output_str;
    }
    public void slide_left_and_slide_in(){//opening new activity
        overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }

    @Override
    public void onBackPressed() {
//        Session.getCurrentSession().removeCallback(callback);
        super.onBackPressed();  // optional depending on your needs
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }
}
