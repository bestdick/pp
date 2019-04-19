package com.storyvendingmachine.www.pp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.storyvendingmachine.www.pp.Allurl.base_url;
import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.REQUEST_CODE_FOR_FLASHCARD_WRITE;

//import static com.storyvendingmachine.www.pp_law.MainActivity.REQUEST_CODE_FOR_FLASHCARD_WRITE;



///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link StudySubClassFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link StudySubClassFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class LawStudySubClassFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private OnFragmentInteractionListener mListener;
//
//    public StudySubClassFragment() {
//        // Required empty public constructor
//    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudySubClassFragment.
     */
    // TODO: Rename and change types and number of parameters

    ListView listView;
    LawStudyFlashcardListviewAdapter studyFlashcardListviewAdapter;
    List<LawStudyFlashcardList> studyFlashcardLists;

    int flashcard_menu;
    int total_list_count;
    int page;

    View footer_view;
    int footer_view_flag;
    public static LawStudySubClassFragment newInstance(String param1, String param2) {
        LawStudySubClassFragment fragment = new LawStudySubClassFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        flashcard_menu = 0;
        total_list_count=0;
        page=0;

        footer_view_flag = -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =inflater.inflate(R.layout.fragment_study_sub_class_law, container, false);
        footer_view =getLayoutInflater().inflate(R.layout.law_container_folder_add_footer_view, null);
        initializer(rootview);
        swiper(rootview);
        return rootview;
    }
    public void swiper(View rootview){
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swiper);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page=0;
                studyFlashcardLists.clear();
                getFlashcardList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void initializer(View rootview){
        listView = rootview.findViewById(R.id.listView);
        studyFlashcardLists = new ArrayList<LawStudyFlashcardList>();
        studyFlashcardListviewAdapter= new LawStudyFlashcardListviewAdapter(getActivity(), studyFlashcardLists);
        listView.setAdapter(studyFlashcardListviewAdapter);

        getFlashcardList();
        flashcard_list_header();
    }

    public void flashcard_list_header(){
        View header_view = getLayoutInflater().inflate(R.layout.law_container_flashcard_listview_header, null);
        final TextView view_category_textView = (TextView) header_view.findViewById(R.id.view_category_textView);
        TextView create_flashcard_textView = (TextView) header_view.findViewById(R.id.create_flashcard_textView);
        view_category_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginType.equals("null") || G_user_id.equals("null")){
                    selectAlertDialog_Not_loggedIn(view_category_textView);
                }else{
                    selectAlertDialog(view_category_textView);
                }

            }
        });
        create_flashcard_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginType.equals("null")|| G_user_id.equals("null")){
                    String message = "플래시카드 작성을 하시려면 로그인 후 작성해주시기 바람니다.";
                    String positive_message = "확인";
                    notifier(message, positive_message);
                }else{
                    Intent intent = new Intent(getActivity(), FlashCardWriteActivity.class);
                    intent.putExtra("type", "new");
                    startActivityForResult(intent, REQUEST_CODE_FOR_FLASHCARD_WRITE);
                    slide_left_and_slide_in();
                }
            }
        });
        listView.addHeaderView(header_view);
    }
    public void notifier(String message, String positive_message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }
    private void getFlashcardList(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = base_url+"getFlashcardList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("exam list response", response);
                        try {
                                JSONObject jsonObject = new JSONObject(response);
                                String return_menu = jsonObject.getString("menu");
                                if(return_menu.equals("folder_list")){
                                    String basic_folder_count = jsonObject.getString("basic_folder_flashcard_count");//기본 폴더
                                    LawStudyFlashcardList basic_item = new LawStudyFlashcardList("folder_list", null,null,null,null,null,null,null,
                                            null,null,null,null,null,null,null,null,null,null,null,
                                            "0", "기본 폴더", basic_folder_count);
                                    studyFlashcardLists.add(basic_item);
                                    JSONArray jsonArray = jsonObject.getJSONArray("response1");
                                    for(int i = 0 ; i < jsonArray.length(); i++){
                                        String folder_name = jsonArray.getJSONObject(i).getString("scrap_folder_name");
                                        String folder_code = jsonArray.getJSONObject(i).getString("scrap_folder_code");
                                        String total_flashcads_in_folder = jsonArray.getJSONObject(i).getString("total_flashcads_in_folder");
                                        LawStudyFlashcardList item = new LawStudyFlashcardList("folder_list", null,null,null,null,null,null,null,
                                                null,null,null,null,null,null,null,null,null,null,null,
                                                folder_code, folder_name, total_flashcads_in_folder);
                                        studyFlashcardLists.add(item);
                                    }
                                }else {
                                    total_list_count = Integer.parseInt(jsonObject.getString("total_count"));
                                    JSONArray jsonArray = jsonObject.getJSONArray("response1");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        String primary_key = jsonArray.getJSONObject(i).getString("primary_key");
                                        String major_type = jsonArray.getJSONObject(i).getString("major_type");
                                        String minor_type = jsonArray.getJSONObject(i).getString("minor_type");
                                        String login_type = jsonArray.getJSONObject(i).getString("login_type");
                                        String user_id = jsonArray.getJSONObject(i).getString("user_id");
                                        String user_nickname = jsonArray.getJSONObject(i).getString("user_nickname");
                                        String user_thumbnail = jsonArray.getJSONObject(i).getString("user_thumbnail");
                                        String upload_date = jsonArray.getJSONObject(i).getString("upload_date");
                                        String upload_time = jsonArray.getJSONObject(i).getString("upload_time");
                                        String revised_date = jsonArray.getJSONObject(i).getString("revised_date");
                                        String revised_time = jsonArray.getJSONObject(i).getString("revised_time");
                                        String flashcard_hit = jsonArray.getJSONObject(i).getString("flashcard_hit");
                                        String title = jsonArray.getJSONObject(i).getString("title");
                                        String count = jsonArray.getJSONObject(i).getString("count");
                                        String first_term = jsonArray.getJSONObject(i).getString("first_term");

                                        LawStudyFlashcardList item = new LawStudyFlashcardList("list", primary_key, major_type, null, minor_type, null,
                                                login_type, user_id, user_nickname, user_thumbnail, upload_date, upload_time, null, null, flashcard_hit, null,
                                                title, count, first_term, null, null, null);
                                        studyFlashcardLists.add(item);
                                    }
                                }

                                studyFlashcardListviewAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//
//                        examlistviewAdapter.notifyDataSetChanged();
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("are we here 6", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("type", "list");// list or selected
                params.put("page", String.valueOf(page));
                params.put("flashcard_menu", String.valueOf(flashcard_menu));
                // flashcaard_db_id 는 여기서ㅏ 사용안함
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);

                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void selectAlertDialog_Not_loggedIn(final TextView view_category_textView){
        final CharSequence list[] = new CharSequence[]{"#최신순", "#인기순"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("목록 정렬");
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("value is", "" + which);
                switch (which) {
                    case 0:
                        view_category_textView.setText(list[0].toString());
                        studyFlashcardLists.clear();
                        flashcard_menu = 0;
                        page=0;
                        getFlashcardList();
                        break;
                    case 1:
                        studyFlashcardLists.clear();
                        flashcard_menu = 1;
                        page=0;
                        getFlashcardList();
                        view_category_textView.setText(list[1].toString());
                        break;
                }
            }
        });
        builder.show();
    }
    public void selectAlertDialog(final TextView view_category_textView){
        final CharSequence list[] = new CharSequence[]{"#최신순", "#인기순", "#나의카드", "#나의폴더"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("목록 정렬");
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("value is", "" + which);
                switch (which) {
                    case 0:
                        remove_footer_view();
                        studyFlashcardLists.clear();
                        flashcard_menu = 0;
                        page=0;
                        getFlashcardList();
                        view_category_textView.setText(list[0].toString());
                        break;
                    case 1:
                        remove_footer_view();
                        studyFlashcardLists.clear();
                        flashcard_menu = 1;
                        page=0;
                        getFlashcardList();
                        view_category_textView.setText(list[1].toString());
                        break;
                    case 2:
                        remove_footer_view();
                        studyFlashcardLists.clear();
                        flashcard_menu = 2;
                        page=0;
                        getFlashcardList();
                        view_category_textView.setText(list[2].toString());
                        break;
                    case 3:
                        remove_footer_view();
                        studyFlashcardLists.clear();
                        flashcard_menu = 3;
                        page=0;
                        getFlashcardList();
                        myFolderListClickControl();
                        view_category_textView.setText(list[3].toString());
                        break;
                }
            }
        });
        builder.show();
    }
    public void remove_footer_view(){
        if(footer_view_flag == 1){
            listView.removeFooterView(footer_view);
        }
        footer_view_flag = -1;
    }
    public void myFolderListClickControl(){
        footer_view_flag = 1;
        listView.addFooterView(footer_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){

                }else if(i == (studyFlashcardLists.size()+1) ){
                    //footer click
                    Log.e("footer clicked position", String.valueOf(i));
                    create_new_folder_dialog();
                }else{

                    String folder_code = studyFlashcardLists.get(i-1).getFolder_code();
                    String folder_name = studyFlashcardLists.get(i-1).getFolder_name();
                    String flashcard_length = studyFlashcardLists.get(i-1).getFlashcard_length();

                    Log.e("clicked position", String.valueOf(i)+ "// "+ folder_name);
//                    Intent intent = new Intent(getActivity(), StudyFlashcardViewActivity.class);
//                    intent.putExtra("type", "my_folder");
//                    intent.putExtra("folder_name", folder_name);
//                    intent.putExtra("folder_code", folder_code);
//                    intent.putExtra("flashcard_length", flashcard_length);
//                    startActivity(intent);
//                    slide_left_and_slide_in();
                }

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View v, int i, long l) {
                if(i==0){

                }else if(i == (studyFlashcardLists.size()+1)){

                }else{
                    Log.e("long clicked position", String.valueOf(i));
                    final String folder_code = studyFlashcardLists.get(i-1).getFolder_code();
                    String folder_name = studyFlashcardLists.get(i-1).getFolder_name();
                    String flashcard_length = studyFlashcardLists.get(i-1).getFlashcard_length();

                    final LinearLayout delete_linLayout = (LinearLayout) v.findViewById(R.id.delete_linLayout);
                    final TextView flashcard_folder_delete_textView = (TextView) v.findViewById(R.id.flashcard_folder_delete_textView);
                    final TextView flashcard_folder_content_delete_textView = (TextView) v.findViewById(R.id.flashcard_folder_content_delete_textView);
                    final ImageView delete_close_imageView = (ImageView) v.findViewById(R.id.delete_close_imageView);

                    slideLeft(delete_linLayout);
                    if (i == 1) {
                        flashcard_folder_delete_textView.setVisibility(View.GONE);
                    }
                    flashcard_folder_delete_textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String message = "폴더 삭제\n폴더를 삭제하시겠습니까? 삭제하시면 폴더 안의 모든 내용이 삭제되며 복구가 불가능 합니다.";
                            String pos_message = "삭제";
                            String neg_message = "취소";
                            del_notifier(message, pos_message, neg_message, folder_code, "del_folder", delete_linLayout);
                            Toast.makeText(getActivity(), "폴더 삭제", Toast.LENGTH_SHORT).show();
                        }
                    });
                    flashcard_folder_content_delete_textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String message = "폴더 내용 삭제\n폴더 내용을 삭제하시겠습니까? 삭제된 데이터는 복구가 불가능 합니다.";
                            String pos_message = "삭제";
                            String neg_message = "취소";
                            del_notifier(message, pos_message, neg_message, folder_code, "del_contents", delete_linLayout);
                            Toast.makeText(getActivity(), "폴더 삭제", Toast.LENGTH_SHORT).show();
                        }
                    });
                    delete_close_imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            slideRight(delete_linLayout);
                        }
                    });


                }

                return true;
            }
        });
    }
    private void create_new_folder_dialog(){
        android.support.v7.app.AlertDialog.Builder ad = new android.support.v7.app.AlertDialog.Builder(getActivity());
        ad.setTitle("폴더 추가");
        ad.setMessage("폴더 이름을 적어주세요");
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(32,0,32,0);

        final EditText editText = new EditText(getActivity());
        final TextView count_textView = new TextView(getActivity());
        count_textView.setGravity(Gravity.RIGHT);
        count_textView.setText("0/80");

        linearLayout.addView(editText);
        linearLayout.addView(count_textView);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                count_textView.setText(charSequence.length()+"/80");
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ad.setView(linearLayout);
        ad.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String input_folder_name = editText.getText().toString();
                if(input_folder_name.isEmpty()){
                    String mess = "폴더이름을 입력해주세요";
                    String pos_mess = "확인";
                    notifier(mess, pos_mess);
                }else{
                    uploadNewlyCreatedFolder(input_folder_name);
                }
            }
        });
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        ad.show();
    }
    private void uploadNewlyCreatedFolder(final String folder_name){
//        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/uploadNewlyCreatedFolder.php";
        String url = base_url+"uploadNewlyCreatedFolder.php";
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Newly Created Folder", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access");
                            if(access_token.equals("valid")){
                                String result = jsonObject.getString("response");
                                // ***********************************************
                                // 1. success
                                // 2. fail
                                // 3. folder_already_exist
                                // 4. max_folder_reached
                                // ***********************************************
                                if(result.equals("success")){
                                    String message = "폴더를 성공적으로 만들었습니다.";
                                    String pos_message = "확인";
                                    notifier(message, pos_message);

//                                    scrap_folder_layout.removeAllViews();
//                                    getScrapFolderName();
                                    studyFlashcardLists.clear();
                                    getFlashcardList();
                                }else if(result.equals("folder_already_exist")){
                                    String message = "'"+folder_name +"'폴더는 이미 존재합니다. 다른 이름으로 폴더를 만들어 주세요.";
                                    String pos_message = "확인";
                                    notifier(message, pos_message);
                                }else if(result.equals("max_folder_reached")){
                                    String message = "폴더 한도 초과 하였습니다. 나의 페이지에서 한도를 늘려주세요";
                                    String pos_message = "확인";
                                    notifier(message, pos_message);
                                }else{
                                    //업로드 실패
                                }
                            }else {
                                //token invalid

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
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                params.put("folder_name", folder_name);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    private void del_notifier(String message, String positive_message, String negative_message,
                              final String folder_code, final String del_type, final LinearLayout delete_linLayout){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateDeleteSelectedFolder(folder_code,  del_type);
                    }
                })
                .setNegativeButton(negative_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        slideRight(delete_linLayout);
                    }
                })
                .create()
                .show();
    }
    public void updateDeleteSelectedFolder(final String folder_code, final String del_type){
//        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/updateDeleteSelectedFolder.php";
        String url = base_url+"updateDeleteSelectedFolder.php";
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("del response ::", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")){
                                // access valid
                                String delType = jsonObject.getString("del_type");
                                String result_response = jsonObject.getString("response");
                                if(result_response.equals("success")){
                                    studyFlashcardLists.clear();
                                    getFlashcardList();
                                }else if(result_response.equals("fail")){

                                }else{

                                }
                            }else{
                                //access invalid
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
//                        getExamNameAndCode(); // 인터넷 에러가 났을시 다시 한번 시도한다.
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);
                params.put("folder_code", folder_code);
                params.put("del_type", del_type);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void slideLeft(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                view.getWidth(),                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
//        animate.setFillAfter(true);
        view.startAnimation(animate);
    }
    public void slideRight(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                view.getWidth(),                 // toXDelta
                0,                 // fromYDelta
                0); // toYDelta
        animate.setDuration(500);
//        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.INVISIBLE);
    }


//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

    public void slide_left_and_slide_in(){
        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }
}
