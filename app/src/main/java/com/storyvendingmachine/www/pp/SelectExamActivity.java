package com.storyvendingmachine.www.pp;

import android.Manifest;
import android.content.Intent;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.storyvendingmachine.www.pp.Allurl.url_getExamNameAndCode;

public class SelectExamActivity extends AppCompatActivity {
    SearchView searchView;

    ListView SelectExamActivityListView;
    List<SelectExamList> SelectExamList;
    SelectExamAdapter SelectExamAdapter;

    List<SelectedExamSubjectList> selectedExamSubjectList;
    SelectedExamSubjectAdapter selectedExamSubjectAdapter;

    ProgressBar progressBar;

    List<SelecExam_ExamTypeList> exam_type_code_name;
    Menu passingMenu;

    Intent select_exam_intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exam);
        select_exam_intent = getIntent();
        String identifier = select_exam_intent.getStringExtra("from");
        if(identifier.equals("flashcard_write_activity_select_subject")){
            //플래쉬카드를 작성할때 시험 과목을 선택할때 실행
            String selected_exam_code = select_exam_intent.getStringExtra("selected_exam_code");
            String selected_exam_name = select_exam_intent.getStringExtra("selected_exam_name");
            progressBar = (ProgressBar) findViewById(R.id.SelectExamActivityProgressBar);
            progressbar_visible(); // when the screen first intialized the progressbar activates
            SelectExamActivityListView = (ListView) findViewById(R.id.SelectExamActivityListView);
            toolbar();
            subject_adaptListView();
            SelectedExamSubjectList element = new SelectedExamSubjectList(selected_exam_code, selected_exam_name,
                    "all_subjects", "전체", "0");
            selectedExamSubjectList.add(element);
            getSubjectName(selected_exam_code);

        }else{
            //시험을 고를때 실행된다.
            progressBar = (ProgressBar) findViewById(R.id.SelectExamActivityProgressBar);
            progressbar_visible(); // when the screen first intialized the progressbar activates
            SelectExamActivityListView = (ListView) findViewById(R.id.SelectExamActivityListView);
            toolbar();
            adaptListView();
            exam_type_code_name = new ArrayList<SelecExam_ExamTypeList>();
            getExamNameAndCode("");
        }
    }

    private void toolbar(){
        Toolbar tb = (Toolbar) findViewById(R.id.SelectExamActivityToolBar);
        tb.setElevation(5);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
    }

    public void subject_adaptListView(){
        selectedExamSubjectList =new ArrayList<SelectedExamSubjectList>();
        selectedExamSubjectAdapter = new SelectedExamSubjectAdapter(SelectExamActivity.this, selectedExamSubjectList);
        SelectExamActivityListView.setAdapter(selectedExamSubjectAdapter);
    }
    public void adaptListView(){
        SelectExamList =new ArrayList<SelectExamList>();
//        SelectExamAdapter = new SelectExamAdapter(getApplicationContext(), SelectExamList);
        SelectExamAdapter = new SelectExamAdapter(SelectExamActivity.this, SelectExamList);
        SelectExamActivityListView.setAdapter(SelectExamAdapter);
    }
    public void listViewOnclickListener(){
        SelectExamActivityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Toast.makeText(SelectExamActivity.this, "selectexamactivity : "+position, Toast.LENGTH_SHORT).show();

                String exam_name = SelectExamList.get(position).getExam_name();
                String exam_code = SelectExamList.get(position).getExam_code();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("exam_name", exam_name);
                resultIntent.putExtra("exam_code", exam_code);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

    }
    public void getSubjectName(final String exam_code){
        RequestQueue queue = Volley.newRequestQueue(SelectExamActivity.this);
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/getSelectedExamSubject.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response :::", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            // Toast.makeText(SelectExamActivity.this,access_token, Toast.LENGTH_SHORT).show();

                            if(access_token.equals("valid")){
                                JSONArray jsonArray = jsonObject.getJSONArray("response");
                                for(int i = 0 ; i<jsonArray.length(); i++){
                                    String selected_subject_exam_code = jsonArray.getJSONObject(i).getString("exam_code");
                                    String selected_subject_exam_name= jsonArray.getJSONObject(i).getString("exam_name");
                                    String selected_subject_number = jsonArray.getJSONObject(i).getString("subject_number");
                                    String selected_subject_code = jsonArray.getJSONObject(i).getString("subject_code");
                                    String selected_subject_name = jsonArray.getJSONObject(i).getString("subject_name");

                                    SelectedExamSubjectList element = new SelectedExamSubjectList(selected_subject_exam_code, selected_subject_exam_name,
                                            selected_subject_number,selected_subject_name, selected_subject_code);
                                    selectedExamSubjectList.add(element);
                                }
                                selectedExamSubjectAdapter.notifyDataSetChanged();
                                progressbar_invisible();


                            }else if(access_token.equals("invalid")){
                                Toast.makeText(SelectExamActivity.this,"잘못된 접근입니다", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(SelectExamActivity.this,"error", Toast.LENGTH_SHORT).show();
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
                params.put("exam_code", exam_code);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void getExamNameAndCode(final String input_exam_name){
        RequestQueue queue = Volley.newRequestQueue(SelectExamActivity.this);
//        String url = "http://www.storyvendingmachine.com/PassPop/android/server/getExamNameAndCode.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getExamNameAndCode,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response :::", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            // Toast.makeText(SelectExamActivity.this,access_token, Toast.LENGTH_SHORT).show();
                            if(access_token.equals("valid")){
                                JSONArray jsonArray = jsonObject.getJSONArray("response");


                                for (int i=0; i < jsonArray.length(); i++) {
                                    JSONObject temp = jsonArray.getJSONObject(i);
                                    String exam_type_code = temp.getString("exam_type_code");
                                    String exam_type_name = temp.getString("exam_type_name");


                                    SelectExamList element_title = new SelectExamList(exam_type_code, exam_type_name, null,null, select_exam_intent.getStringExtra("from"));
                                    SelectExamList.add(element_title);

                                    SelecExam_ExamTypeList examTypeList = new SelecExam_ExamTypeList(exam_type_code, exam_type_name);
                                    exam_type_code_name.add(examTypeList);

                                    JSONArray jsonInnerArray  =temp.getJSONArray("exams");
                                    for(int j = 0 ; j<jsonInnerArray.length(); j++){
                                        JSONObject temp2 = jsonInnerArray.getJSONObject(j);
                                        String exam_code = temp2.getString("exam_code");
                                        String exam_name = temp2.getString("exam_name");

                                        SelectExamList element = new SelectExamList(exam_type_code,exam_type_name, exam_code, exam_name, select_exam_intent.getStringExtra("from"));
                                        SelectExamList.add(element);
                                    }
                                }
                                SelectExamAdapter.notifyDataSetChanged();
                                progressbar_invisible();


                            }else if(access_token.equals("invalid")){
                                Toast.makeText(SelectExamActivity.this,"잘못된 접근입니다", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(SelectExamActivity.this,"error", Toast.LENGTH_SHORT).show();
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
                        getExamNameAndCode(input_exam_name); // 인터넷 에러가 났을시 다시 한번 시도한다.
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("input_exam_name", input_exam_name);
                return params;
            }
        };
        queue.add(stringRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String identifier = select_exam_intent.getStringExtra("from");
        if(identifier.equals("flashcard_write_activity_select_subject")){
            //플래쉬카드를 작성할때 시험 과목을 선택할때 실행
            //메뉴를 만들지 않는다.
        }else{
            passingMenu = menu;
            getMenuInflater().inflate(R.menu.search_menu, menu);
            MenuItem searchItem = menu.findItem(R.id.searchBar);
            final SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setIconified(true);
            searchView.setQueryHint("시험 검색");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    SelectExamList.clear();
                    getExamNameAndCode(s);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    SelectExamList.clear();
                    getExamNameAndCode(s);
//                Toast.makeText(SelectExamActivity.this, s, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            SubMenu themeMenu = menu.findItem(R.id.list_option_menu).getSubMenu();
            Log.e("on create length:::::", String.valueOf(exam_type_code_name.size()));
            for(int i =0; i<exam_type_code_name.size(); i++){
                String exam_type_name = exam_type_code_name.get(i).getExam_type_name();
                int exam_type_code = Integer.parseInt(exam_type_code_name.get(i).exam_type_code.split("_")[1]);
                themeMenu.add(Menu.NONE, exam_type_code, Menu.NONE, exam_type_name);// groupId, itemId, order, title
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.list_option_menu){
            Log.e("length:::::::", String.valueOf(exam_type_code_name.size()));
            SubMenu themeMenu = passingMenu.findItem(R.id.list_option_menu).getSubMenu();
            for(int i =0; i<exam_type_code_name.size(); i++){
                String exam_type_name = exam_type_code_name.get(i).getExam_type_name();
                int exam_type_code = Integer.parseInt(exam_type_code_name.get(i).exam_type_code.split("_")[1]);
                themeMenu.add(Menu.NONE, exam_type_code, Menu.NONE, exam_type_name);// groupId, itemId, order, title
            }
        }else{
            onBackPressed();
        }

        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
//        overridePendingTransition(0,R.anim.slide_down);// first entering // second exiting
    }
    public void progressbar_visible(){
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void progressbar_invisible(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.INVISIBLE);
    }




}
