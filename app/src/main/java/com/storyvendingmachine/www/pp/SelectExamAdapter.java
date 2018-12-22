package com.storyvendingmachine.www.pp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.storyvendingmachine.www.pp.Allurl.url_getExamNameAndCode;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;

/**
 * Created by Administrator on 2018-06-24.
 */

public class SelectExamAdapter extends BaseAdapter {
    private Context context;
    private List<SelectExamList> list;

    public SelectExamAdapter(Context context, List<SelectExamList> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v =View.inflate(context, R.layout.select_exam_elements_container, null);

        View v2 =View.inflate(context, R.layout.select_exam_type_container, null);



        String exam_type_code = list.get(i).getExam_type_code();
        String exam_type_name = list.get(i).getExam_type_name();
        final String exam_code = list.get(i).getExam_code();
        final String exam_name = list.get(i).getExam_name();
        final String from = list.get(i).getFrom();

//        ExamEachNameTextView.setText(exam_type_name + exam_name);

        if(exam_code == null){
            TextView ExamEachNameTextView =(TextView) v2.findViewById(R.id.ExamEachNameTextView);
            ExamEachNameTextView.setText(exam_type_name);
            return v2;
        }else{
            TextView ExamEachNameTextView =(TextView) v.findViewById(R.id.ExamEachNameTextView);
            TextView ExamType_textView = (TextView) v.findViewById(R.id.ExamType_textView);
            ExamEachNameTextView.setText(exam_name);
            ExamType_textView.setText(exam_type_name);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(from.equals("main_activity")){
                        upDateLastExamSelection(exam_code);// update exam selection

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("exam_name", exam_name);
                        resultIntent.putExtra("exam_code", exam_code);
                        ((SelectExamActivity)context).setResult(RESULT_OK, resultIntent);
                        ((SelectExamActivity)context).finish();
                    }else{
                        // flashcard_write_activity
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("exam_name", exam_name);
                        resultIntent.putExtra("exam_code", exam_code);
                        ((SelectExamActivity)context).setResult(RESULT_OK, resultIntent);
                        ((SelectExamActivity)context).finish();
                    }


                }
            });

            return v;
        }

    }

    private void upDateLastExamSelection(final String exam_code){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/updateExamSelection.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response :::", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                String result_response = jsonObject.getString("response");

                                Log.e("result response :::", result_response);
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

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                params.put("picked_exam_code", exam_code);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
