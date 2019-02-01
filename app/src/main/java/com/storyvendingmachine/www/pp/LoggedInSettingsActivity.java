package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;

public class LoggedInSettingsActivity extends AppCompatActivity implements RewardedVideoAdListener {
    ProgressBar progressBar;
    private RewardedVideoAd mRewardedVideoAd;// for rewarded advertisment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_settings);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        toolbar();
        ad();
        rewardAdInitialize();
        Intent intent = getIntent();
        String inType = intent.getStringExtra("type");
        if(inType.equals("change_password")){
            initialize_change_password();
        }else if(inType.equals("flashcard_folder_manager")){
            progressbar_visible();
            initialize_flashcard_folder_manager();
        }else{

        }
    }

    public void updateFolderSize(){
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/updateFolderSize.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("pw_change_result" , response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                String result = jsonObject.getString("response");
                                if(result.equals("success")){
                                    String mes = "최대 폴더 갯수를 증가하였습니다.";
                                    String pos_mes = "확인";
                                    notifier(mes, pos_mes);
                                    TextView folder_count_fraction_textview = (TextView) findViewById(R.id.folder_count_fraction_textview);
                                    updateSettings("flashcard_folder_manager", "null",  "null", folder_count_fraction_textview);
                                }else{
                                    String mes = "최대 폴더 갯수를 증가하지 못했습니다.";
                                    String pos_mes = "확인";
                                    notifier(mes, pos_mes);
                                }
                            }else if(access_token.equals("invalid")){

                            }else{

                            }
                            progressbar_invisible();
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
                progressbar_invisible();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    private void updateSettings(final String setting_type, final String pw, final String pw_confirm,
                                final TextView folder_count_fraction_textview){
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/updateSettings.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("pw_change_result" , response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                // ********************************************
                                // 1. change_password
                                // 2. flashcard_folder_manager
                                //
                                // ********************************************
                                String settingType = jsonObject.getString("setting_type");
                                if(settingType.equals("change_password")){
                                    String result = jsonObject.getString("response");
                                    pw_change_process(result, pw);
                                }else if (settingType.equals("flashcard_folder_manager")){
                                    JSONObject result = jsonObject.getJSONObject("response");
                                    String max_folder_count = result.getString("max_folder");
                                    String user_created_folder_count = result.getString("user_created_folder");
                                    user_flashcard_folders_count_process(max_folder_count, user_created_folder_count, folder_count_fraction_textview);
                                }else{

                                }


                            }else if(access_token.equals("invalid")){

                            }else{

                            }
                            progressbar_invisible();
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
                progressbar_invisible();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("setting_type", setting_type);
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                params.put("pw", pw);
                params.put("pw_confirm", pw_confirm);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    private void initialize_flashcard_folder_manager(){
        ScrollView foler_manager_scrollView = (ScrollView) findViewById(R.id.manager_scrollView);
        foler_manager_scrollView.setVisibility(View.VISIBLE);

        TextView folder_count_fraction_textview = (TextView) findViewById(R.id.folder_count_fraction_textview);
        Button increase_flashcard_folder_button = (Button) findViewById(R.id.increase_flashcard_folder_button);
        updateSettings("flashcard_folder_manager", "null",  "null", folder_count_fraction_textview);
        increase_flashcard_folder_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbar_visible();
                loadRewardedVideoAd();
            }
        });
    }
    public void user_flashcard_folders_count_process(String max_folder_count, String user_created_folder_count, TextView folder_count_fraction_textview){
        folder_count_fraction_textview.setText(user_created_folder_count + " / "+ max_folder_count);
    }


    private void initialize_change_password(){
        ScrollView change_pw_scrollView = (ScrollView) findViewById(R.id.change_pw_scrollView);
        change_pw_scrollView.setVisibility(View.VISIBLE);

        final EditText new_pw_editText = (EditText) findViewById(R.id.new_pw_editText);
        final EditText new_pw_confirm_editText = (EditText) findViewById(R.id.new_pw_confirm_editText);
        Button pw_change_submit_button = (Button) findViewById(R.id.pw_change_submit_button);
        pw_change_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String new_pw_str = new_pw_editText.getText().toString();
                final String new_pw_confirm_str = new_pw_confirm_editText.getText().toString();
                if(new_pw_str.trim().isEmpty() || new_pw_confirm_str.trim().isEmpty()){
                    //하나라도 공백이면 ....
                    String message = "새 비밀번호를 입력해주세요";
                    String pos_message = "확인";
                    notifier(message, pos_message);
                }else{
                    if(new_pw_str.equals(new_pw_confirm_str)){
                        // 새 비밀번호와 새 비밀번호 확인과 같을때....
                        progressbar_visible();
                        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                            @Override
                            public void run() {
                                updateSettings("change_password",new_pw_str, new_pw_confirm_str, null);
                            }
                        }, 800);

                    }else{
                        //새 비밀번호와 새 비밀번호 확인과 같지 않을떄...
                        String message = "비밀번호가 일치 하지 않습니다. 비밀번호를 확인해 주세요";
                        String pos_message = "확인";
                        notifier(message, pos_message);
                    }
                }

            }
        });
    }
    public void pw_change_process(String result, String pw){
        // ********************************************
        // 1.pw_update_success
        // 2. pw_update_fail
        // 3. email_not_found
        // ********************************************
        if(result.equals("pw_update_success")){
            SharedPreferences login_remember = getSharedPreferences("setting", 0);
            SharedPreferences.Editor editor = login_remember.edit();
            editor.remove("user_password");
            editor.putString("user_password", pw);
            editor.commit();

            String message = "비밀번호를 성공적으로 변경하였습니다.";
            String pos_message = "확인";
            pw_change_success_notifier(message, pos_message);
        }else if(result.equals("pw_update_fail")){
            String message = "비밀번호 변경을 실패하였습니다. 다시 한번 시도해 주세요.";
            String pos_message = "확인";
            notifier(message, pos_message);
        }else{
            String message = "존재하지 않은 사용자입니다.";
            String pos_message = "확인";
            notifier(message, pos_message);
        }
    }

    private void toolbar(){
        Toolbar tb = (Toolbar) findViewById(R.id.loggedinsetting_toolBar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
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
    public void pw_change_success_notifier(String message, String positive_message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                })
                .create()
                .show();
    }
    public void progressbar_visible(){
        progressBar.setVisibility(View.VISIBLE);
//        progressBarBackground.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void progressbar_invisible(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.GONE);
//        progressBarBackground.setVisibility(View.GONE);
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = getIntent();
        String inType = intent.getStringExtra("type");
        if(inType.equals("change_password")){
            onBackPressed();
        }else if(inType.equals("flashcard_folder_manager")){
            Intent intentResult = new Intent();
            setResult(RESULT_OK, intentResult);
            onBackPressed();
        }else{

        }

        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();  // optional depending on your needs
        finish();
        overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }

    //**************************************
    //banner ad
    public void ad(){
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("C646CB521B367AC2D90D66C8B4A3EECE")
//                .build();
        // 내 애뮬의 test device id 이다
        //12-27 05:38:09.561 14026-14026/com.storyvendingmachine.www.pp I/Ads: Use AdRequest.Builder.addTestDevice("C646CB521B367AC2D90D66C8B4A3EECE") to get test ads on this device.


        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.e("ad load fail", String.valueOf(errorCode));
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }
    //***************************************
    //rewarded ad
    private void rewardAdInitialize(){
//          MobileAds.initialize(this, "ca-app-pub-9203333069147351~3494839374");
        //tester
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        //tester
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
//        loadRewardedVideoAd();
    }
    private void loadRewardedVideoAd() {
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("C646CB521B367AC2D90D66C8B4A3EECE")
//                .build();
//        mRewardedVideoAd.loadAd("ca-app-pub-9203333069147351/2841207026", adRequest); // device tester

//        mRewardedVideoAd.loadAd("ca-app-pub-9203333069147351/2841207026",
//                new AdRequest.Builder().build());
//        google tester
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    //안드로이드 앱 자체의 수기주기에 대하여 이 리워드 비디오를 시작하고 없앤다.
    @Override
    public void onResume() {
//        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
//        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
//        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }
    //안드로이드 앱 자체의 수기주기에 대하여 이 리워드 비디오를 시작하고 없앤다.

    /////// 보상 광고 에 필요한 override
    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();
        // Reward the user.
        updateFolderSize();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad-"+String.valueOf(errorCode), Toast.LENGTH_SHORT).show();
        progressbar_invisible();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
        if (mRewardedVideoAd.isLoaded()) {
            progressbar_invisible();
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
        progressbar_invisible();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {
        Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
    }
    /////// 보상 광고 에 필요한 override
}
