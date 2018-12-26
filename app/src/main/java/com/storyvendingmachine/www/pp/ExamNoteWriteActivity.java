package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.pp.ExamViewActivity.note_array;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;

public class ExamNoteWriteActivity extends AppCompatActivity {

    EditText note_editText;
    String type, exam_code, exam_name, exam_placed_year, exam_placed_round;
    int note_number;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_note_write);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        if(type.equals("note_write")){
            exam_code = intent.getStringExtra("exam_code");
            exam_name= intent.getStringExtra("exam_name");
            exam_placed_year = intent.getStringExtra("exam_placed_year");
            exam_placed_round = intent.getStringExtra("exam_placed_round");
            note_number = Integer.parseInt(intent.getStringExtra("note_number"));

            note_editText = (EditText) findViewById(R.id.note_editText);
            String note = note_array[note_number];
            if(note.equals("null")){
                note_editText.setHint("노트를 작성해주세요...");
            }else{
                note_editText.setText(note_array[note_number]);
            }
            note_uploadButtonClick();
            String title = exam_placed_year+" 년도 "+exam_placed_round + " 회 " + exam_name +" "+ (note_number+1) +" 번";
            toolbar(title);
        }else if(type.equals("flashcard_comment")){
            pb = (ProgressBar) findViewById(R.id.exam_note_progressbar);
            String flashcard_exam_name = intent.getStringExtra("flashcard_exam_name");
            String flashcard_subject_name = intent.getStringExtra("flashcard_subject_name");
            String flashcard_title = intent.getStringExtra("flashcard_title");
            String flashcard_db_id = intent.getStringExtra("flashcard_db_id");

            Button comment_uploadButton = (Button) findViewById(R.id.note_upload_button);
            EditText comment_editText = (EditText) findViewById(R.id.note_editText);
            comment_uploadButton.setText("댓글 달기");
            comment_editText.setHint("댓글을 작성해주세요");

            uploadCommentClick(flashcard_db_id, comment_uploadButton, comment_editText);
            toolbar(flashcard_title);
        }
    }

    public void notifier(){

    }
    public void uploadCommentClick(final String flashcard_db_id, Button comment_uploadButton, final EditText comment_editText){
        comment_uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "댓글을 등록하시겠습니까?";
                String positive_message = "네";
                String negative_message = " 아니요";
                notifier_positive_negative_uploadCommentProcess(message, positive_message, negative_message, flashcard_db_id, comment_editText);

            }
        });
    }

    public void notifier_positive_negative_uploadCommentProcess(String message, String positive_message, String negative_message, final String flashcard_db_id, final EditText comment_editText){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressbar_visible();
                        uploadCommentProcess(flashcard_db_id, comment_editText);
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
    public void notifier_confirm_uploadCommentProcess(String message, String positive_message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("upload_status", "success");
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                })
                .create()
                .show();
    }
    public void uploadCommentProcess(final String flashcard_db_id, final EditText comment_editText){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/uploadFlashCardComment.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("upload_comment ::" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                progressbar_invisible();
                                String result = jsonObject.getString("response");
                                if(result.equals("upload_success")){
                                    //성공시
                                    String message = "댓글을 성공적으로 업로드 하였습니다.";
                                    String positive_message = "확인";
                                    notifier_confirm_uploadCommentProcess(message, positive_message);
                                }
                            }else if(access_token.equals("invalid")){

                            }else{

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("volley error", error.toString());
                        //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
//                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
//                        toast(message);
//                        getExamNameAndCode(); // 인터넷 에러가 났을시 다시 한번 시도한다.
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("flashcard_db_id", flashcard_db_id);
                params.put("comment", comment_editText.getText().toString());
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void note_uploadButtonClick(){
        Button note_uploadButton = (Button) findViewById(R.id.note_upload_button);
        note_uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                note_array[note_number] = note_editText.getText().toString();
                Log.e("note array look like", String.valueOf(note_array.length));
                JSONArray note_json_array = note_to_json_array(note_array);
                note_upload_process(note_json_array);
            }
        });
    }
    public JSONArray note_to_json_array(String[] note_array){
        JSONArray jsonArray = new JSONArray();
        for(int i = 0 ; i<note_array.length; i++){
            Log.e("each note", note_array[i]);
            String note_after_replace = note_array[i].replace("\n", "<br>");
            jsonArray.put(note_after_replace);
        }
        return jsonArray;
    }
    public void note_upload_process(final JSONArray note_array){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/upload_update_ExamNote.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("exam note ::" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
//                                examNoteJSONArray = jsonObject.getJSONArray("response");
                                if(LoginType.equals("kakao") || LoginType.equals("normal")){
//                                    userPersonalNoteArray = jsonObject.getJSONArray("user_personal_note");
                                }else{

                                }

                            }else if(access_token.equals("invalid")){

                            }else{

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("volley error", error.toString());
                        //Toast.makeText(getActivity(), "volley error", Toast.LENGTH_LONG).show();
//                        String message = "인터넷 연결 에러.. 다시 한번 시도해 주세요...ㅠ ㅠ";
//                        toast(message);
//                        getExamNameAndCode(); // 인터넷 에러가 났을시 다시 한번 시도한다.
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("exam_code", exam_code);
                params.put("exam_placed_year", exam_placed_year);
                params.put("exam_placed_round", exam_placed_round);

                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);

                params.put("note", note_array.toString());
                params.put("public_private", "public");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void toolbar(String title){
        Toolbar tb = (Toolbar) findViewById(R.id.loggedin_toolBar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
//        getSupportActionBar().setSubtitle(title);
        TextView title_textView = (TextView) findViewById(R.id.exam_titleTextView);
        title_textView.setText(title);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


    public void progressbar_visible(){
        pb.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void progressbar_invisible(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pb.setVisibility(View.INVISIBLE);
    }
}
