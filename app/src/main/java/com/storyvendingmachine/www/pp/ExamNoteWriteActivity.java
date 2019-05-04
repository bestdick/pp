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
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.pp.ExamViewActivity.note_array;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.major_exam_type_code;

public class ExamNoteWriteActivity extends AppCompatActivity {

    EditText note_editText;
    TextView note_notice_textView;
    String type, exam_major_type, exam_code, exam_name, exam_placed_year, exam_placed_round, major_type, minor_type;
    int note_number;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(major_exam_type_code.equals("lawyer")){
            setTheme(R.style.PassPopLawTheme);
        }else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_note_write);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        if(type.equals("note_write")){
            exam_major_type = intent.getStringExtra("exam_major_type");
            if(exam_major_type.equals("lawyer")){
                note_notice_textView = (TextView) findViewById(R.id.note_notice_textView);
                note_notice_textView.setVisibility(View.VISIBLE);
                note_editText = (EditText) findViewById(R.id.note_editText);

                exam_placed_year = intent.getStringExtra("exam_placed_year");
                major_type = intent.getStringExtra("major_type");
                minor_type = intent.getStringExtra("minor_type");
                note_number = Integer.parseInt(intent.getStringExtra("note_number"));

                boolean isNoteExist = intent.getBooleanExtra("isNoteExist", false);
                if(isNoteExist){
                    String note = intent.getStringExtra("note");
                    note_editText.setText(note);
                    note_editText.setSelection(note.length());
                }else{
                    note_editText.setHint("노트를 작성해주세요...");
                }

                note_uploadButtonClick();

                String title = exam_placed_year+"년도"+major_type+"/"+minor_type+"/"+note_number;
                toolbar(title);
            }else{
                note_notice_textView = (TextView) findViewById(R.id.note_notice_textView);
                note_notice_textView.setVisibility(View.VISIBLE);
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
            }
        }else if(type.equals("flashcard_comment")){
            exam_major_type = intent.getStringExtra("exam_major_type");
            if(exam_major_type.equals("lawyer")){
                // 변호사 시험의 플래시카드 댓글을 남기는 페이지
                String primary_key = intent.getStringExtra("primary_key");
                pb = (ProgressBar) findViewById(R.id.exam_note_progressbar);


                Button comment_uploadButton = (Button) findViewById(R.id.note_upload_button);
                EditText comment_editText = (EditText) findViewById(R.id.note_editText);
                comment_uploadButton.setText("댓글 달기");
                comment_editText.setHint("댓글을 작성해주세요");

                uploadCommentClick(primary_key, comment_uploadButton, comment_editText, "law_flashcard_comment_upload");
                toolbar(null);
            }else{
                pb = (ProgressBar) findViewById(R.id.exam_note_progressbar);
                String flashcard_exam_name = intent.getStringExtra("flashcard_exam_name");
                String flashcard_subject_name = intent.getStringExtra("flashcard_subject_name");
                String flashcard_title = intent.getStringExtra("flashcard_title");
                String flashcard_db_id = intent.getStringExtra("flashcard_db_id");

                Button comment_uploadButton = (Button) findViewById(R.id.note_upload_button);
                EditText comment_editText = (EditText) findViewById(R.id.note_editText);
                comment_uploadButton.setText("댓글 달기");
                comment_editText.setHint("댓글을 작성해주세요");

                uploadCommentClick(flashcard_db_id, comment_uploadButton, comment_editText, null);
                toolbar(flashcard_title);
            }
        }else{
            //type.equals("flashcard_revise_comment");
            exam_major_type = intent.getStringExtra("exam_major_type");
            if(exam_major_type.equals("lawyer")){
                //여기서 가져오는 primary_key는 flashcard comment 에 해당하는 primary_key 이다.
                String primary_key  = intent.getStringExtra("primary_key");
                String comment = intent.getStringExtra("comment");

                pb = (ProgressBar) findViewById(R.id.exam_note_progressbar);

                Button comment_uploadButton = (Button) findViewById(R.id.note_upload_button);
                EditText comment_editText = (EditText) findViewById(R.id.note_editText);
                comment_uploadButton.setText("댓글 달기");
                comment_editText.setText(comment);

                uploadCommentClick(primary_key, comment_uploadButton, comment_editText, "law_flashcard_comment_revise_update");
                toolbar(null);
            }else{
                // exam_major_type.equals("sugs_1001/gs_2001");
            }
        }
    }
    //아래 매소드들은 commment 에 해당하는 매소드 들이다.
    public void uploadCommentClick(final String flashcard_db_id, Button comment_uploadButton, final EditText comment_editText, final String upload_type){
        comment_uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    // exam_major_type.equals("sugs_1001/gs_2001");
                    String message = "댓글을 등록하시겠습니까?";
                    String positive_message = "네";
                    String negative_message = " 아니요";
                    notifier_positive_negative_uploadCommentProcess(message, positive_message, negative_message, flashcard_db_id, comment_editText, upload_type);

            }
        });
    }

    public void notifier_positive_negative_uploadCommentProcess(String message, String positive_message, String negative_message, final String flashcard_db_id, final EditText comment_editText, final String upload_type){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressbar_visible();
                        if(exam_major_type.equals("lawyer")){
                            LAW_uploadcommentProcess(flashcard_db_id, comment_editText, upload_type);
                        }else{
                            uploadCommentProcess(flashcard_db_id, comment_editText);
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

    public void notifier_confirm_uploadCommentProcess(String message, String positive_message, final String primary_key){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("upload_status", "success");
                        resultIntent.putExtra("primary_key", primary_key);
                        setResult(RESULT_OK, resultIntent);
                        onBackPressed();
                    }
                })
                .create()
                .show();
    }


    public void LAW_uploadcommentProcess(final String flashcard_db_id, final EditText comment_editText, final String upload_type){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.joonandhoon.com/pp/passpop_law/android/server/upload_update.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("upload_comment ::" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                if(upload_type.equals("law_flashcard_comment_upload")){
                                    progressbar_invisible();
                                    String result = jsonObject.getString("response");
                                    if(result.equals("upload_success")){
                                        //성공시
                                        String message = "댓글을 성공적으로 업로드 하였습니다.";
                                        String positive_message = "확인";
                                        notifier_confirm_uploadCommentProcess(message, positive_message, flashcard_db_id);
                                    }
                                }else{
                                    //upload_type.equals("law_flashcard_comment_revise_update")
                                    progressbar_invisible();
                                    String result = jsonObject.getString("response");
                                    if(result.equals("upload_success")){
                                        //성공시
                                        String flashcard_table_id = jsonObject.getString("flashcard_table_id");
                                        String message = "댓글을 성공적으로 업데이트 하였습니다.";
                                        String positive_message = "확인";
                                        notifier_confirm_uploadCommentProcess(message, positive_message, flashcard_table_id);
                                    }
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
                params.put("upload_type", upload_type);
                params.put("flashcard_db_id", flashcard_db_id);
                params.put("comment", comment_editText.getText().toString());
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                return params;
            }
        };
        queue.add(stringRequest);
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
                                    notifier_confirm_uploadCommentProcess(message, positive_message, flashcard_db_id);
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
    //여기까지 매소드들은 comment 에 해당하는 매소드 들이다.




    //아래 매소드들은 note에 해당하는 매소드 들이다.....
    public void note_uploadButtonClick(){
        Button note_uploadButton = (Button) findViewById(R.id.note_upload_button);
        note_uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(exam_major_type.equals("lawyer")){
                    String message = "\"공개\" 노트로 업로드 및 수정 하시겠습니까?\n\"비공개\" 노트로 업로드 및 수정 하시겠습니까?";
                    String pos = "공개";
                    String neg = "비공개";
                    LAW_note_simple_notifier(message, pos, neg);
                }else{
                    note_array[note_number] = note_editText.getText().toString();
                    Log.e("note array look like", String.valueOf(note_array.length));
                    JSONArray note_json_array = note_to_json_array(note_array);

                    String message = "\"공개\" 노트로 업로드 및 수정 하시겠습니까?\n\"비공개\" 노트로 업로드 및 수정 하시겠습니까?";
                    String pos = "공개";
                    String neg = "비공개";
                    note_simple_notifier(message, pos, neg, note_json_array);
                }
            }
        });
    }

    public void note_simple_notifier(String message, String positive_message, String negative_message, final JSONArray note_json_array){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("노트 업로드 및 수정")
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //public
                        String public_or_private = "public";
                        note_upload_process(note_json_array, public_or_private);
                    }
                })
                .setNegativeButton(negative_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //private
                        String public_or_private = "private";
                        note_upload_process(note_json_array, public_or_private);
                    }
                })
                .setCancelable(true)
                .create()
                .show();
    }

    public void LAW_note_simple_notifier(String message, String positive_message, String negative_message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("노트 업로드 및 수정")
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //public
                        String public_or_private = "public";
                        String final_input_note = note_editText.getText().toString();
                        LAW_note_upload_process(final_input_note, public_or_private);
                    }
                })
                .setNegativeButton(negative_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //private
                        String public_or_private = "private";
                        String final_input_note = note_editText.getText().toString();
                        LAW_note_upload_process(final_input_note, public_or_private);
                    }
                })
                .setCancelable(true)
                .create()
                .show();
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


    public void LAW_note_upload_process(final String final_input_note, final String public_or_private){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.joonandhoon.com/pp/passpop_law/android/server/upload_updateExamNote.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("exam note ::" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                String resp =jsonObject.getString("response");
                                if(resp.equals("success")){
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("upload_status", "success");
                                    resultIntent.putExtra("exam_placed_year", exam_placed_year);
                                    resultIntent.putExtra("major_type", major_type);
                                    resultIntent.putExtra("minor_type", minor_type);
                                    resultIntent.putExtra("note_number", String.valueOf(note_number));
                                    setResult(RESULT_OK, resultIntent);
                                    onBackPressed();
                                }else{

                                }
////                                examNoteJSONArray = jsonObject.getJSONArray("response");
//                                if(LoginType.equals("kakao") || LoginType.equals("normal")){
////                                    userPersonalNoteArray = jsonObject.getJSONArray("user_personal_note");
//                                }else{
//
//                                }
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
                params.put("exam_placed_year", exam_placed_year);
                params.put("major_type", major_type);
                params.put("minor_type", minor_type);
                params.put("question_number", String.valueOf(note_number));
                params.put("note", final_input_note);

                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);

                params.put("public_private", public_or_private);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void note_upload_process(final JSONArray note_array, final String public_or_private){
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
                                String resp =jsonObject.getString("response");
                                if(resp.equals("success")){
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("upload_status", "success");
                                    resultIntent.putExtra("note_number", String.valueOf(note_number));
                                    setResult(RESULT_OK, resultIntent);
                                    onBackPressed();
                                }else{

                                }
////                                examNoteJSONArray = jsonObject.getJSONArray("response");
//                                if(LoginType.equals("kakao") || LoginType.equals("normal")){
////                                    userPersonalNoteArray = jsonObject.getJSONArray("user_personal_note");
//                                }else{
//
//                                }
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
                params.put("public_private", public_or_private);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    //여기까지는 note에 해당하는 매소드들이다.



    //아래ㄴ부터는 공통통
    private void toolbar(String title){
        if(major_exam_type_code.equals("lawyer")){
            Toolbar tb = (Toolbar) findViewById(R.id.loggedin_toolBar);
            tb.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
            getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
//        getSupportActionBar().setSubtitle(title);
            TextView title_textView = (TextView) findViewById(R.id.exam_titleTextView);
            title_textView.setText(title);
        }else{
            Toolbar tb = (Toolbar) findViewById(R.id.loggedin_toolBar);
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
            getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
//        getSupportActionBar().setSubtitle(title);
            TextView title_textView = (TextView) findViewById(R.id.exam_titleTextView);
            title_textView.setText(title);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out);// first entering // second exiting
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
