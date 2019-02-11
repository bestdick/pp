package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kakao.auth.Session;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.pp.EnteranceActivity.LATEST_VERSION;
import static com.storyvendingmachine.www.pp.LoginActivity.callback;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_level;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_nickname;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_thumbnail;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_name;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;
import static com.storyvendingmachine.www.pp.VERSION.THIS_APP_VERSION;


public class LoggedInActivity extends AppCompatActivity {
    final int FOLDER_MANAGER_RESULT = 2211;
    SharedPreferences login_remember;
    SharedPreferences.Editor editor;

    final int REQUEST_CODE_IMAGE = 8081;

    private Uri mDestinationUri;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage.jpeg";

    ImageView user_thumbnail_imageView;



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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        versionControll();

        mDestinationUri = Uri.fromFile(new File(getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME));

        ad();
        user_thumbnail_imageView = (ImageView) findViewById(R.id.user_thumbnail_imageView);
        TextView login_type_textView = (TextView) findViewById(R.id.login_type_textView);
        TextView user_nickname_textView = (TextView) findViewById(R.id.user_nickname_textView);
        TextView join_date_textView = (TextView) findViewById(R.id.join_date_textView);
        TextView study_exam_name_textView = (TextView) findViewById(R.id.study_exam_name_textView);
        TextView flashcard_folder_count_textView = (TextView) findViewById(R.id.flashcard_folder_count_textView);

        user_kakao_normal_info(login_type_textView, user_nickname_textView, join_date_textView, study_exam_name_textView, flashcard_folder_count_textView, user_thumbnail_imageView);
        toolbar();

    }
    public void versionControll(){
        TextView this_app_version_textView = (TextView) findViewById(R.id.this_app_version_textView);
        TextView latest_version_textView = (TextView) findViewById(R.id.latest_app_version_textView);
        this_app_version_textView.setText("(beta) 현재 앱 버전 " + THIS_APP_VERSION);
        latest_version_textView.setText("(beta) 업데이트 가능 앱 버전 "+ LATEST_VERSION);

    }
    public void user_kakao_normal_info(TextView login_type_textView, TextView user_nickname_textView, TextView join_date_textView,
                                       TextView study_exam_name_textView, TextView flashcard_folder_count_textView, ImageView user_thumbnail_imageView){
        if(LoginType.equals("kakao")){
            login_type_textView.setText("카카오 계정");
            user_nickname_textView.setText(G_user_nickname);
            getThumbnailImageForAuthor(user_thumbnail_imageView, G_user_thumbnail);
            getUserInfo(join_date_textView, study_exam_name_textView,flashcard_folder_count_textView);
            logoutProcess();
            changePasswordProcess("kakao");
            folderManageProcess();
        }else if(LoginType.equals("normal")){
            login_type_textView.setText("패스팝 계정");
            user_nickname_textView.setText(G_user_nickname+" ( "+ G_user_id +" ) ");
            getThumbnailImageForAuthor(user_thumbnail_imageView, G_user_thumbnail);
            getUserInfo(join_date_textView, study_exam_name_textView, flashcard_folder_count_textView);

            user_thumbnail_imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, REQUEST_CODE_IMAGE);
                }
            });
            logoutProcess();
            changePasswordProcess("normal");
            folderManageProcess();
        }else{
            //로그인 타입 // 정해지지 않음
        }
    }
    public void getUserInfo(final TextView join_date_textView, final TextView study_exam_name_textView, final TextView flashcard_folder_count_textView){
        RequestQueue queue = Volley.newRequestQueue(LoggedInActivity.this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/getUserInfo.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response:::", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")){
                                String exam_name = jsonObject.getJSONArray("response").getJSONObject(0).getString("last_exam");
                                String[] join_date = jsonObject.getJSONArray("response").getJSONObject(0).getString("join_date").split(" ");
                                String user_max_folder = jsonObject.getJSONObject("response_folder").getString("max_folder");
                                String user_created_folder = jsonObject.getJSONObject("response_folder").getString("user_created_folder");

                                join_date_textView.setText("가입 일자 : "+join_date[0]);
                                study_exam_name_textView.setText("공부 중 인 시험 : "+exam_name);

                                flashcard_folder_count_textView.setText("플래시 카드 폴더 :"+user_created_folder+"/"+user_max_folder);
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
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void getThumbnailImageForAuthor(ImageView imageView, String url){
        imageView.setImageDrawable(null);
        if(url.equals("null") || url.length() <=0){
            imageView.setImageResource(R.drawable.icon_empty_thumbnail);
        }else{
            Picasso.with(this)
                    .load(url)
                    .transform(new CircleTransform())
                    .fit()
                    .centerInside()
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
    }
    private void toolbar(){
        Toolbar tb = (Toolbar) findViewById(R.id.loggedin_toolBar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
    }
    public void logoutProcess(){
        Button logout_button = (Button) findViewById(R.id.logout_button);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "로그아웃 하시겠습니까?";
                String postive_message = "네";
                String negative_message = "아니요";
                logout_notifier(message, postive_message, negative_message);
            }
        });

    }
    public void changePasswordProcess(String login_type){
        Button change_password_button = (Button) findViewById(R.id.change_password_button);
        if(login_type.equals("kakao")){
            change_password_button.setVisibility(View.GONE);
        }else{
            //normal login
            change_password_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoggedInActivity.this, LoggedInSettingsActivity.class);
                    intent.putExtra("type", "change_password");
                    startActivity(intent);
                    slide_left_and_slide_in();
                }
            });
        }
    }
    public void folderManageProcess(){
        Button folder_manage_button = (Button) findViewById(R.id.folder_manage_button);
        folder_manage_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoggedInActivity.this, LoggedInSettingsActivity.class);
                intent.putExtra("type", "flashcard_folder_manager");
                startActivityForResult(intent, FOLDER_MANAGER_RESULT);
                slide_left_and_slide_in();
            }
        });
    }
    private void logout_notifier(String message, String positive_message, String negative_message){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoggedInActivity.this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoginType = "null";

                        G_user_id = "null";
                        G_user_level = "null";
                        G_user_nickname = "null";
                        G_user_thumbnail = "null";

                        exam_selection_name ="null";
                        exam_selection_code ="null";

                        login_remember = getSharedPreferences("setting", 0);
                        editor = login_remember.edit();
                        editor.putString("login_type", "null");
                        editor.putBoolean("id_pw_match", false);
                        editor.putString("user_email", "");
                        editor.putString("user_password", "");
                        editor.commit();

                        Session.getCurrentSession().removeCallback(callback);
                        Session.getCurrentSession().clearCallbacks();
                        Session.getCurrentSession().close();

                        Intent intent = new Intent(LoggedInActivity.this, MainActivity.class);
                        setResult(RESULT_OK, intent);

                        onBackPressed();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == REQUEST_CODE_IMAGE){
//            Uri selectedImage = data.getData();
//            UCrop.of(selectedImage, selectedImage)
////                    .withAspectRatio(16, 9)
////                    .withMaxResultSize(124, 124)
//                    .start(this);
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
//            } catch (IOException e) {
//                Log.i("TAG", "Some exception " + e);
//            }
//        }else{
//            if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
//                final Uri resultUri = UCrop.getOutput(data);
//                Log.e("image result uri :", resultUri.toString());
//            } else if (resultCode == UCrop.RESULT_ERROR) {
//                final Throwable cropError = UCrop.getError(data);
//            }
//        }

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_IMAGE) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCropActivity(data.getData());
                    Log.e("e", "selected uri is not empty ");
                } else {
                    Toast.makeText(LoggedInActivity.this,
                            "cannot retrive select image",
                            Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                // 여기서 이미지를 자르고 회전하고 한 이미지를 받아오는 곳이다.
                //아래 handleCropResult 가 그 처리를 한다.
                handleCropResult(data);
                Log.e("e", "selected handling crop result ");
            } else if(requestCode == FOLDER_MANAGER_RESULT){
                //************************************
                // this result for activity
                //      플래시 카드 폴더 매니저 세팅에 들어갔다 나왔을때.....
                TextView join_date_textView = (TextView) findViewById(R.id.join_date_textView);
                TextView study_exam_name_textView = (TextView) findViewById(R.id.study_exam_name_textView);
                TextView flashcard_folder_count_textView = (TextView) findViewById(R.id.flashcard_folder_count_textView);
                getUserInfo( join_date_textView,  study_exam_name_textView,  flashcard_folder_count_textView);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
//            handleCropError(data);
            Toast.makeText(this, "crop error", Toast.LENGTH_SHORT).show();
        }
    }
    private void startCropActivity(@NonNull Uri uri) {
        UCrop uCrop = UCrop.of(uri, mDestinationUri);
//        uCrop = _setRatio(uCrop, RATIO_ORIGIN, 0, 0);
//        uCrop = _setSize(uCrop, 0, 0);
//        uCrop = _advancedConfig(uCrop, FORMAT_JPEG, 90);
        uCrop.start(this);
    }

//    private void handleCropResult(@NonNull final Intent result) {
    private void handleCropResult(final Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            // ResultActivity.startWithUri(MainActivity.this, resultUri);
            Toast.makeText(this, resultUri.toString(), Toast.LENGTH_SHORT).show();
            user_thumbnail_imageView.setImageDrawable(null);
            Picasso.with(this)
                    .load(resultUri)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .transform(new CircleTransform())
                    .fit()
                    .centerInside()
                    .into(user_thumbnail_imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            //온라인에 저장한다.
                            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                            Date date = new Date();
                            String today_date = dateFormat.format(date);
                            try {
                                Bitmap bitmap_image = MediaStore.Images.Media.getBitmap(LoggedInActivity.this.getContentResolver(), resultUri);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                bitmap_image.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
                                String encoded_image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                                JSONObject jsonObject = new JSONObject();
                                String image_name = "thumbnail_"+G_user_id+"_"+today_date+".JPEG";
                                G_user_thumbnail = "http://www.joonandhoon.com/pp/PassPop/android/server/thumbnail/"+image_name;
                                try {
                                    Log.e("name", image_name);
                                    Log.e("image", encoded_image);

                                    jsonObject.put("name", image_name);
                                    jsonObject.put("image", encoded_image);
//                                    jsonObject.put("login_type", LoginType);
                                    jsonObject.put("user_id", G_user_id);
                                    upload_user_thumbnail(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError() {
                            Log.e("load image", "fail to load images ");
                        }
                    });
        } else {
            Toast.makeText(this, "crop cannot retrieve select image", Toast.LENGTH_SHORT).show();
//            user_thumbnail_imageView.setImageURI(resultUri);
        }
    }
    public void upload_user_thumbnail(JSONObject jsonObject){
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/UploadUserThumbnail.php";
        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                            Log.e("thumbnail message succ", jsonObject.toString());

                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e("thumbnail message fail", volleyError.getMessage());
                }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy( 5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(LoggedInActivity.this).add(jsonObjectRequest);
    }



    public void slide_left_and_slide_in(){//opening new activity
        overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
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
//        Session.getCurrentSession().removeCallback(callback);
        super.onBackPressed();  // optional depending on your needs
        finish();
        overridePendingTransition(R.anim.slide_right_bit, R.anim.slide_out); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }
}
