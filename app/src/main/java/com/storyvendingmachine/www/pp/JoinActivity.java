package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinActivity extends AppCompatActivity {



    EditText user_email;
    TextView email_check;

    EditText user_password;
    EditText user_password_confirm;
//    TextView password_check;

    EditText user_name;

    EditText user_nickname;
    TextView nickname_check;

    TextView cell_number;
    Button register_button;

    LinearLayout term_text_container;
    TextView term_textView;
    CheckBox term_checkBox;

    ScrollView scrollView, scrollView2;


    boolean isEmailOK, isNicknameOK, isPasswordOK;

    int VIEW_ONE=1;// 회원 가입 뷰
    int VIEW_TWO=2;// 인증 코드 재발송 뷰
    int VIEW_THREE=3; // 비밀번호 찾기 뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        toolbar();


        Intent intent = getIntent();
        String intent_type = intent.getStringExtra("type");
        if(intent_type.equals("join")){
            viewSelector(VIEW_ONE);
            initializer();
        }else if(intent_type.equals("verification_resend")){
            viewSelector(VIEW_TWO);
            initialize_resend_verificationCode(VIEW_TWO);
        }else{
            // forgotten_password
            viewSelector(VIEW_THREE);
            initialize_resend_verificationCode(VIEW_THREE);
        }
    }

    public void viewSelector(int input){

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView2 = (ScrollView) findViewById(R.id.scrollView2);

        if (input == VIEW_ONE){
            scrollView.setVisibility(View.VISIBLE);
        }else if(input == VIEW_TWO){
            scrollView2.setVisibility(View.VISIBLE);
        }else{
            scrollView2.setVisibility(View.VISIBLE);
        }
    }
    private void toolbar(){
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
    }
    public void initialize_resend_verificationCode(int view_code){
        if(view_code == 2){
            TextView title_textView = (TextView) findViewById(R.id.title_textView);
            final EditText resend_email_editText = (EditText)  findViewById(R.id.email_editText);
            Button resend_button = (Button) findViewById(R.id.resend_button);
            resend_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String user_email = resend_email_editText.getText().toString();
                    if(user_email.trim().isEmpty()){
                        String message = "이메일을 입력해주세요";
                        String positive_message = "확인";
                        simple_notifier(message, positive_message);
                    }else{
                        resendVerificationCode(user_email);
                    }

                }
            });
            TextView info_container = (TextView) findViewById(R.id.info_container);
            info_container.setText(getResources().getString(R.string.verification_code_send));
        }else{
            //view code 3
            TextView title_textView = (TextView) findViewById(R.id.title_textView);
            title_textView.setText("비밀번호 찾기");
            final EditText resend_email_editText = (EditText)  findViewById(R.id.email_editText);
            Button resend_button = (Button) findViewById(R.id.resend_button);
            resend_button.setText("비밀번호 발송");
            resend_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String user_email = resend_email_editText.getText().toString();
                    if(user_email.trim().isEmpty()){
                        String message = "이메일을 입력해주세요";
                        String positive_message = "확인";
                        simple_notifier(message, positive_message);
                    }else{
                        send_password(user_email);
                    }
                }
            });
            TextView info_container = (TextView) findViewById(R.id.info_container);
            info_container.setText(getResources().getString(R.string.password_find));
        }
    }
    public void send_password(final String user_email){
        String url= "http://www.joonandhoon.com/pp/PassPop/android/server/resendVerificationCodeORpassword.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("send password logcat", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")) {
                                String response_str = jsonObject.getString("response");
                                // *****************************************
                                // response_str 에서 나오는 return 값
                                // 1. email_not_verified
                                // 2. email_not_exist
                                // 3. password_change_fail
                                // 4. send_email_fail
                                // 5. send_email_success
                                // ******************************************
                                if(response_str.equals("send_email_success")){
                                    String message = "비밀번호를 성공적으로 변경하였습니다.\n이메일을 확인하시고 로그인해주세요.";
                                    String positive_message = "확인";
                                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                    builder.setMessage(message)
                                            .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    onBackPressed();
                                                }
                                            })
                                            .create()
                                            .show();
                                }else if(response_str.equals("email_not_exist")){
                                    String message = "존재하지 않는 이메일 입니다. 먼저 가입해주세요";
                                    String positive_message = "확인";
                                    simple_notifier(message, positive_message);
                                }else if(response_str.equals("email_not_verified")) {
                                    String message = "인증되지 않은 이메일 입니다";
                                    String positive_message = "확인";
                                    simple_notifier(message, positive_message);
                                }else{
                                    String message = "비밀번호 변경에 실패하였습니다. 다시 시도해주세요";
                                    String positive_message = "확인";
                                    simple_notifier(message, positive_message);
                                }

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
                params.put("type", "send_password");
                params.put("user_email",user_email);
                return params;
            }
        };
        queue.add(stringRequest);
    }


    public void resendVerificationCode(final String user_email){
        String url= "http://www.joonandhoon.com/pp/PassPop/android/server/resendVerificationCodeORpassword.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resend logcat", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")) {
                                String response_str = jsonObject.getString("response");
                                // *****************************************
                                // response_str 에서 나오는 return 값
                                // 1. email_already_verified
                                // 2. email_not_exist
                                // 3. update_fail
                                // 4. send_email_fai
                                // 5. send_email_success
                                if (response_str.equals("send_email_success")){
                                    String message = "인증코드 발송 성공\n이메일을 확인하시고 로그인해주세요.";
                                    String positive_message = "확인";
                                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                    builder.setMessage(message)
                                            .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    onBackPressed();
                                                }
                                            })
                                            .create()
                                            .show();
                                }else if(response_str.equals("email_not_exist")){
                                    String message = "존재하지 않는 이메일 입니다. 먼저 가입해주세요";
                                    String positive_message = "확인";
                                    simple_notifier(message, positive_message);
                                }else if(response_str.equals("email_already_verified")) {
                                    String message = "이미 인증된 이메일입니다. 로그인 해주세요";
                                    String positive_message = "확인";
                                    simple_notifier(message, positive_message);
                                }else{
                                    String message = "인증코드 발송 실패\n다시 시도해 주세요.";
                                    String positive_message = "확인";
                                    simple_notifier(message, positive_message);
                                }

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
                params.put("type", "resend_verification");
                params.put("user_email",user_email);
                return params;
            }
        };
        queue.add(stringRequest);
    }


    public void initializer(){
        isEmailOK=false;
        isNicknameOK=false;
        isPasswordOK=false;

        user_email = (EditText) findViewById(R.id.r_userEmailTextbox);
        email_check = (TextView) findViewById(R.id.r_emailcheck);
        user_password = (EditText) findViewById(R.id.r_userPasswordTextbox);
        user_password_confirm = (EditText) findViewById(R.id.r_userPasswordConfirm);
//        password_check = (TextView) findViewById(R.id.r_passwordCheck);
        user_name = (EditText) findViewById(R.id.r_userName);
        user_nickname = (EditText) findViewById(R.id.r_userNickname);
        nickname_check = (TextView) findViewById(R.id.r_nicknamecheck);
        register_button = (Button) findViewById(R.id.r_register);
        cell_number = (TextView) findViewById(R.id.r_userCellNumber);

        term_text_container = (LinearLayout) findViewById(R.id.term_text_container);
        term_textView = (TextView) findViewById(R.id.terms_of_agree_textView);
        term_checkBox = (CheckBox) findViewById(R.id.terms_of_agree_checkBox);

        editTextListener();
        RegisterButtonClicked();

        term_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //가입 약관 보기
                if(term_text_container.getVisibility() == View.VISIBLE){
                    term_text_container.setVisibility(View.GONE);
                }else{
                    term_text_container.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    public void editTextListener(){
        user_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("charsequence", charSequence.toString());
                String check_input = charSequence.toString();
                if(check_input.contains("@") && check_input.contains(".")){
                    String check_type = "email_check";
                    email_nickname_check_Function(check_type, charSequence.toString());
                }else{
                    email_check.setText("이메일 형식에 맞지 않습니다.");
                    email_check.setTextColor(getResources().getColor(R.color.colorRed));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        user_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("charsequence", charSequence.toString());
                String check_input = charSequence.toString();
                if(check_input.length()>=2){
                    String check_type = "nickname_check";
                    email_nickname_check_Function(check_type, charSequence.toString());
                }else{
                    nickname_check.setText("닉네임은 두 글자 이상으로 설정해 주세요");
                    nickname_check.setTextColor(getResources().getColor(R.color.colorRed));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    public void email_nickname_check_Function(final String check_type, final String check_input){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/check_email_nickname_password.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                      Log.e("email check response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(check_type.equals("email_check")){
                                String email_check_result = jsonObject.getJSONObject("response").getString("email_check");
                                String return_email_address = jsonObject.getJSONObject("response").getString("email_address");
                                if(email_check_result.equals("usable")){
                                    isEmailOK=true;
                                    email_check.setText(return_email_address+" (은)는 사용 할 수 있는 이메일 입니다");
                                    email_check.setTextColor(getResources().getColor(R.color.colorGreen));
                                }else{
                                    //unusable 일떄
                                    isEmailOK=false;
                                    email_check.setText(return_email_address+" (은)는 이미 사용중인 이메일 입니다.");
                                    email_check.setTextColor(getResources().getColor(R.color.colorRed));
                                }
                            }else{
                                //check_type == "nickname_check
                                String nickname_check_result = jsonObject.getJSONObject("response").getString("nickname_check");
                                String return_nickname = jsonObject.getJSONObject("response").getString("nickname");
                                if(nickname_check_result.equals("usable")){
                                    isNicknameOK=true;
                                    nickname_check.setText(return_nickname+" (은)는 사용 할 수 있는 별명 입니다");
                                    nickname_check.setTextColor(getResources().getColor(R.color.colorGreen));
                                }else if(nickname_check_result.equals("bad_word")){
                                    //욕을 포함할때
                                    isNicknameOK=false;
                                    nickname_check.setText(return_nickname+" (은)는 부적합한 단어를 포함하고 있습니다");
                                    nickname_check.setTextColor(getResources().getColor(R.color.colorRed));
                                }else{
                                    // 이미 사용중
                                    isNicknameOK=false;
                                    nickname_check.setText(return_nickname+" (은)는 이미 사용중인 별명 입니다");
                                    nickname_check.setTextColor(getResources().getColor(R.color.colorRed));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("check_type", check_type);
                params.put("check_input", check_input);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void RegisterButtonClicked(){
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //버튼을 클릭하면 일단 모든 edittext 가 빈칸이 없는지 확인을 한다.
                if(user_email.getText().toString().trim().length()<=0||user_password.getText().toString().trim().length()<=0||user_password_confirm.getText().toString().trim().length()<=0||
                        user_name.getText().toString().trim().length()<=0||user_nickname.getText().toString().trim().length()<=0||cell_number.getText().toString().trim().length()<=0){
                    String message = "작성되지 않은 회원 정보가 존재합니다.\n작성 후 가입 부탁드립니다.";
                    String positive_message = "확인";
                    simple_notifier(message, positive_message);
                }else{
                    if(!isEmailOK){
                        String message = "잘못된 이메일\n가입하실 이메일을 확인해주세요.";
                        String positive_message = "확인";
                        simple_notifier(message, positive_message);
                    }else{
                        if(!isNicknameOK){
                            String message = "잘못된 별명 \n가입하실 별명을 확인해주세요.";
                            String positive_message = "확인";
                            simple_notifier(message, positive_message);
                        }else{
                            String password_str = user_password.getText().toString();
                            String password_confirm_str = user_password_confirm.getText().toString();
                            if(password_str.trim().equals(password_confirm_str.trim())){
                                if(term_checkBox.isChecked()){
                                    //체크박스가 체크 되있으면....
                                    //가입 완료
                                    String user_email_Reg = user_email.getText().toString();
                                    String user_password_Reg = user_password.getText().toString();
                                    String user_nickname_Reg = user_nickname.getText().toString();
                                    String user_name_Reg = user_name.getText().toString();
                                    String user_cell_Reg = cell_number.getText().toString();
                                    uploadRegisterInfo(user_email_Reg, user_password_Reg, user_name_Reg, user_nickname_Reg, user_cell_Reg);
                                }else{
                                    String message = "가입약관을 동의하셔야 가입이 가능합니다.";
                                    String positive_message = "확인";
                                    simple_notifier(message, positive_message);
                                }
                            }else{
                                String message = "비밀번호 불일치\n비밀번호가 일치하지 않습니다. 다시 확인해주세요.";
                                String positive_message = "확인";
                                simple_notifier(message, positive_message);
                            }
                        }
                    }
                }

            }
        });
    }
    public void uploadRegisterInfo(final String email, final String password, final String name, final String nickname, final String cell){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/uploadRegisterInfo.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("register", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")){
                                String isDB = jsonObject.getJSONObject("response").getString("db");
                                String isEmail = jsonObject.getJSONObject("response").getString("email");
                                if(isDB.trim().equals("success") && isEmail.trim().equals("success")){
                                    String message = "가입을 축하합니다!\n("+email+")으로 인증코드가 발송되었습니다.\n이메일은 최대 10분정도 소요될 수 있습니다.";
                                    String positive_message = "로그인";
                                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                    builder.setMessage(message)
                                            .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    onBackPressed();
                                                }
                                            })
                                            .create()
                                            .show();
                                }else{
                                    //가입 안됨!!!
                                    //어디에서 에러가 났는지 확인....
                                }
                            }else{
                                //잘못된 접근입니다.
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("email", email);
                params.put("password", password);
                params.put("name", name);
                params.put("nickname", nickname);
                params.put("cell", cell);
                return params;
            }
        };
        queue.add(stringRequest);
    }


    private void simple_notifier(String message, String positive_message){
        AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }
}
