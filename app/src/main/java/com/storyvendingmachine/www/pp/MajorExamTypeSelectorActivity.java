package com.storyvendingmachine.www.pp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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

public class MajorExamTypeSelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major_exam_type_selector);
        whenLoginEmpty();
    }

    public void whenLoginEmpty(){
        final LinearLayout exam_selector_linLayout = (LinearLayout) findViewById(R.id.exam_selector_linLayout);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/getMajorExamSelector.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("get major exam ", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")){
                                JSONArray jsonArray = jsonObject.getJSONArray("response1");
                                for( int i = 0 ; i < jsonArray.length(); i++){
                                    View exam_type_view = getLayoutInflater().inflate(R.layout.container_major_exam_selector, null);
                                    TextView exam_type_name_textView = (TextView) exam_type_view.findViewById(R.id.exam_name_textView);
                                    final String exam_type_code = jsonArray.getJSONObject(i).getString("exam_type_code");
                                    String exam_type_name = jsonArray.getJSONObject(i).getString("exam_type_name");
                                    exam_type_name_textView.setText(exam_type_name);

                                    exam_type_view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if(exam_type_code.equals("sugs_1001") || exam_type_code.equals("gs_2001")){
                                                Intent intent = new Intent(MajorExamTypeSelectorActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out); // 처번째가 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
                                                finish();
                                            }else{
                                                Intent intent = new Intent(MajorExamTypeSelectorActivity.this, LawMainActivity.class);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out); // 처번째가 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
                                                finish();
                                            }

                                        }
                                    });
                                    exam_selector_linLayout.addView(exam_type_view);
                                }
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

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
