package com.storyvendingmachine.www.pp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_nickname;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_thumbnail;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;


/**
 * Created by Administrator on 2018-03-18.
 */

public class SessionCallback implements ISessionCallback {

private Context context;
private String fromActivity;

String kakao_id;
String email;

ProgressBar pb;


SharedPreferences.Editor editor;


    public SessionCallback(Context context, String fromActivity, SharedPreferences.Editor editor){
            this.context=context;
            this.fromActivity=fromActivity;
            this.editor = editor;


        }


    @Override
    public void onSessionOpened() {

        pb.setVisibility(View.VISIBLE);
        UserManagement.getInstance().requestMe(new MeResponseCallback() {

            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Log.e("onFailure", "fail");
                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    //에러로 인한 로그인 실패
//                        finish();
                } else {
                    //redirectMainActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("onSessionClosed", "fail");
                Session.getCurrentSession().close();
                pb.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNotSignedUp() {
                Log.e("onNotsignedUp", "fail");
                pb.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSuccess(final UserProfile userProfile) {
                Log.e("Session opened ::", "true");
                //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.

                Log.e("UserProfile", userProfile.toString());

                long number = userProfile.getId();

                kakao_id = Integer.toString((int) userProfile.getId());
                Log.e("카카오 아이디", kakao_id);
                final boolean email_verified = userProfile.getEmailVerified();
                if(email_verified){
                    email=userProfile.getEmail();
                }else{
                    email="";
                }
                Log.e("이메일 주소", email);
                final String thumb_nail;
                if(userProfile.getThumbnailImagePath() == null){
                    thumb_nail="thumb_nail_empty";
                }else{
                    thumb_nail = userProfile.getThumbnailImagePath().toString();
                }
                Log.e("썸내일 주소", thumb_nail);
                final String nickname = userProfile.getNickname().toString();
                Log.e("닉네임", nickname);

                RequestQueue queue = Volley.newRequestQueue(context);
//                String url = "http://www.storyvendingmachine.com/android/kakao_id_to_db.php";
                String url = "http://www.joonandhoon.com/pp/PassPop/android/server/kakao_id_to_db.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("response +  :: ", response);
                                if (response.trim().equals("exist")){
                                    //이미 가입된 회원일때 프로파일 업데이트 하는 과정
                                    Log.e("가입되어있음", "가입됨");
                                    requestprofile();
                                }else{
                                    if(response.trim().equals("add_success")){
                                        //처음 로그인 했을때
                                        if(fromActivity.equals("login_activity")){
                                            Intent intent = new Intent();
                                            intent.putExtra("login_type", "kakao");
                                            intent.putExtra("user_id", Integer.toString((int) userProfile.getId()));
                                            intent.putExtra("user_thumbnail", userProfile.getThumbnailImagePath());
                                            intent.putExtra("user_nickname", userProfile.getNickname());
                                            intent.putExtra("user_selected_last_major_exam", "null");
                                            intent.putExtra("user_selected_last_exam_code", "null");
                                            intent.putExtra("user_selected_last_exam_name", "null");

                                            ((LoginActivity)context).setResult(RESULT_OK, intent);
                                            ((LoginActivity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                            ((LoginActivity)context).finish();

                                            ((LoginActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            pb.setVisibility(View.INVISIBLE);
                                        }else if(fromActivity.equals("enterance_activity")){
                                            //처음 여기서 들어오는 경우는 없다.
                                            Intent intent = new Intent(context, MainActivity.class);
                                            intent.putExtra("login_type", "kakao");
                                            intent.putExtra("user_id", Integer.toString((int) userProfile.getId()));
                                            intent.putExtra("user_thumbnail", userProfile.getThumbnailImagePath());
                                            intent.putExtra("user_nickname", userProfile.getNickname());
                                            //Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                            Log.e("almost", "almost success");
                                            context.startActivity(intent);


                                            ((EnteranceActivity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                            ((EnteranceActivity)context).finish();
                                            ((EnteranceActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            pb.setVisibility(View.INVISIBLE);
                                        }




                                        editor.putString("login_type", "kakao");
                                        editor.commit();

//                                        ((LoginActivity)context).finish();
//                                        ((LoginActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                        pb.setVisibility(View.INVISIBLE);

                                    }else if(response.trim().equals("add_fail")){
                                        Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                                    }

                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                    Log.e("volley Error ", error.toString());
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams(){
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("kakao_id", kakao_id);
                        params.put("email", email);
                        params.put("thumb_nail", thumb_nail);
                        params.put("nickname", nickname);
                        return params;
                    }
                };
                queue.add(stringRequest);
            }
        });

    }

    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        // 세션 연결이 실패했을때
        // 어쩔때 실패되는지는 테스트를 안해보았음 ㅜㅜw

        Log.e("onSessionOpenFailed", "fail" + exception);
        pb.setVisibility(View.INVISIBLE);

    }




    protected void requestprofile(){
        com.kakao.kakaotalk.v2.KakaoTalkService.getInstance().requestProfile(new TalkResponseCallback<KakaoTalkProfile>() {
            @Override
            public void onNotKakaoTalkUser() {
                Log.e("kakao session", "not kakao user");
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("kakao session", "close");
            }

            @Override
            public void onNotSignedUp() {
                Log.e("kakao session", "not kakao not sign up");
            }

            @Override
            public void onSuccess(KakaoTalkProfile result) {
                Log.e("where are we? ","1");
                final String thumb_nail = result.getThumbnailUrl();
                final String nickname = result.getNickName();

                RequestQueue queue = Volley.newRequestQueue(context);
//                String url = "http://www.storyvendingmachine.com/android/kakao_id_to_db.php";
                String url = "http://www.joonandhoon.com/pp/PassPop/android/server/kakao_id_to_db.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            Log.e("where are we::::",response);

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String last_major_exam = jsonObject.getString("last_major_exam");
                                    String last_subject_code = jsonObject.getString("last_subject_code");
                                    String last_subject_name = jsonObject.getString("last_subject_name");
                                    Log.e("where are we::::",last_subject_code+"//"+last_subject_name);

                                    if(last_major_exam.equals("null") || last_subject_code.equals("null")){
                                        Intent intent = new Intent(context, MajorExamTypeSelectorActivity.class);
                                        intent.putExtra("login_type", "kakao");
                                        intent.putExtra("user_id", kakao_id);
                                        if(thumb_nail.length() == 0){
                                            intent.putExtra("user_thumbnail", "null");
                                        }else{
                                            intent.putExtra("user_thumbnail", thumb_nail);
                                        }
                                        intent.putExtra("user_nickname", nickname);
                                        intent.putExtra("user_selected_last_major_exam", "null");
                                        intent.putExtra("user_selected_last_exam_code", "null");
                                        intent.putExtra("user_selected_last_exam_name", "null");

                                        if(fromActivity.equals("login_activity")){
                                            ((LoginActivity)context).setResult(RESULT_OK, intent);
                                            ((LoginActivity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                            ((LoginActivity)context).finish();

                                            ((LoginActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            pb.setVisibility(View.INVISIBLE);
                                        }else if(fromActivity.equals("enterance_activity")){
                                            context.startActivity(intent);
                                            ((EnteranceActivity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                            ((EnteranceActivity)context).finish();
//
                                            ((EnteranceActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            pb.setVisibility(View.INVISIBLE);
                                        }
                                    }else{
                                        Intent intent = new Intent(context, MainActivity.class);
                                        intent.putExtra("login_type", "kakao");
                                        intent.putExtra("user_id", kakao_id);
                                        if(thumb_nail.length() == 0){
                                            intent.putExtra("user_thumbnail", "null");
                                        }else{
                                            intent.putExtra("user_thumbnail", thumb_nail);
                                        }
                                        intent.putExtra("user_nickname", nickname);

                                        intent.putExtra("user_selected_last_major_exam", last_major_exam);
                                        intent.putExtra("user_selected_last_exam_code", last_subject_code);
                                        intent.putExtra("user_selected_last_exam_name", last_subject_name);
                                        if(fromActivity.equals("login_activity")){
                                            ((LoginActivity)context).setResult(RESULT_OK, intent);
                                            ((LoginActivity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                            ((LoginActivity)context).finish();

                                            ((LoginActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            pb.setVisibility(View.INVISIBLE);
                                        }else if(fromActivity.equals("enterance_activity")){
                                            context.startActivity(intent);
                                            ((EnteranceActivity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                            ((EnteranceActivity)context).finish();
//
                                            ((EnteranceActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            pb.setVisibility(View.INVISIBLE);
                                        }
                                    }


                                    editor.putString("login_type", "kakao");
                                    editor.commit();
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
                    protected Map<String, String> getParams(){
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("existing_id", "true");
                        params.put("kakao_id", kakao_id);
                        params.put("thumb_nail", thumb_nail);
                        params.put("nickname", nickname);
                        return params;
                    }
                };
                queue.add(stringRequest);
            }
        });
    }


  // last line of class
}
