package com.storyvendingmachine.www.pp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.zip.Inflater;

import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;



/**
     * Created by symoo on 2018-02-21.
     */

    public class FlashcardFragment extends Fragment {
    static ListView flashcardListView;
    List<FlashCardList> flashcard_list;
    FlashcardAdapter flashcard_adapter;

    List<FlashCardMyList> flashcard_my_list;
    FlashcardMyListAdapter flashcard_my_list_adapter;

    ProgressBar see_more_pb;

    int flashcard_menu;

    int page;
    int total_query_count;

    View rootView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    Button  menu_one, menu_two, menu_three, menu_four;
    TextView see_more_textView, header_title_textView, notice_textView;
    View listview_footer, listview_header;

    ConstraintLayout my_folder_not_login_empty_conLayout;
    public static FlashcardFragment newInstance() {
        FlashcardFragment fragment = new FlashcardFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_flashcard, container, false);
        page = 0;
        flashcard_menu = 0;// flashcard menu 0 은 기분  1 은 인기순, 2는 과목
        my_folder_not_login_empty_conLayout = (ConstraintLayout) rootView.findViewById(R.id.my_folder_not_login_empty_conLayout);

        flashcardListView = (ListView) rootView.findViewById(R.id.flashcard_listView);
        flashcard_list =new ArrayList<FlashCardList>();
        flashcard_adapter = new FlashcardAdapter(getActivity(), flashcard_list);
        flashcardListView.setAdapter(flashcard_adapter);

        menu_one = (Button) rootView.findViewById(R.id.testFragment_bottom_btn1);
        menu_two = (Button) rootView.findViewById(R.id.testFragment_bottom_btn2);
        menu_three = (Button) rootView.findViewById(R.id.testFragment_bottom_btn3);
        menu_four = (Button) rootView.findViewById(R.id.testFragment_bottom_btn4);

        getFlashcardList(exam_selection_code);// 플레쉬카드

        swiper(rootView);
        AddHeader();
        ListViewFooterControl(rootView);
        flashcardMenuButtonClicked(rootView);

        return rootView;
    }

    public void flashcardMenuButtonClicked(final View rootView){
        menu_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //최신 순서
                if(my_folder_not_login_empty_conLayout.getVisibility() == View.VISIBLE){
                    my_folder_not_login_empty_conLayout.setVisibility(View.INVISIBLE);
                    flashcardListView.setVisibility(View.VISIBLE);
                }

                menu_one.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button));
                menu_two.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                menu_three.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                menu_four.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));

                flashcard_menu=0; // 최신매뉴
                page = 0;
                flashcard_list.clear();


                flashcard_list =new ArrayList<FlashCardList>();
                flashcard_adapter = new FlashcardAdapter(getActivity(), flashcard_list);
                flashcardListView.setAdapter(flashcard_adapter);
                flashcardListView.removeFooterView(listview_footer);
                see_more_textView.setText("더 보기");
                ListViewFooterControl(rootView);
                AddHeader();
                getFlashcardList(exam_selection_code);// 플레쉬카드

                Log.e("flashcard menu", String.valueOf(flashcard_menu));
            }
        });

        menu_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //인기순서
                if(my_folder_not_login_empty_conLayout.getVisibility() == View.VISIBLE){
                    my_folder_not_login_empty_conLayout.setVisibility(View.INVISIBLE);
                    flashcardListView.setVisibility(View.VISIBLE);
                }


                menu_one.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                menu_two.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button));
                menu_three.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                menu_four.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                flashcard_menu=1; //인기순위
                page = 0;
                flashcard_list.clear();

                flashcard_list =new ArrayList<FlashCardList>();
                flashcard_adapter = new FlashcardAdapter(getActivity(), flashcard_list);
                flashcardListView.setAdapter(flashcard_adapter);
                flashcardListView.removeFooterView(listview_footer);
                see_more_textView.setText("더 보기");
                ListViewFooterControl(rootView);
                AddHeader();
                getFlashcardList(exam_selection_code);// 플레쉬카드

                Log.e("flashcard menu", String.valueOf(flashcard_menu));
            }
        });

        menu_three.setOnClickListener(new View.OnClickListener() {// my list
            @Override
            public void onClick(View view) {
                if(LoginType.equals("null")){
                    menu_one.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                    menu_two.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                    menu_three.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button));
                    menu_four.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                    flashcard_menu=2;//나의 폴더
                    total_query_count=0;
                    page = 0;
                    flashcard_list.clear();

                    if(my_folder_not_login_empty_conLayout.getVisibility() == View.INVISIBLE ||
                            my_folder_not_login_empty_conLayout.getVisibility() == View.GONE){
                        my_folder_not_login_empty_conLayout.setVisibility(View.VISIBLE);
                        flashcardListView.setVisibility(View.INVISIBLE);

                    }
                    TextView flashcard_empty_title_textView = (TextView) rootView.findViewById(R.id.flashcard_empty_title_textView);
                    TextView flashcard_empty_notice_textView = (TextView) rootView.findViewById(R.id.flashcard_empty_notice_textView);

                    flashcard_empty_title_textView.setText("나의 플래시카드 폴더");
                    flashcard_empty_notice_textView.setText("로그인 후 플래시 카드 폴더에 공부하시고 싶은 플래시카드를 넣어 나만의 플래시카드를 만들어 보세요.");

                }else{
                    // 폴더 로그인 했을떄
                    if(my_folder_not_login_empty_conLayout.getVisibility() == View.VISIBLE){
                        my_folder_not_login_empty_conLayout.setVisibility(View.INVISIBLE);
                        flashcardListView.setVisibility(View.VISIBLE);
                    }
                    menu_one.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                    menu_two.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                    menu_three.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button));
                    menu_four.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                    flashcard_menu=2;//나의 폴더
                    total_query_count=0;
                    page = 0;
                    flashcard_list.clear();


                    flashcard_my_list =new ArrayList<FlashCardMyList>();
                    flashcard_my_list_adapter = new FlashcardMyListAdapter(getActivity(), flashcard_my_list);
                    flashcardListView.setAdapter(flashcard_my_list_adapter);
                    flashcardListView.removeFooterView(listview_footer);
                    ListViewFooterControl(rootView);
                    see_more_textView.setText("플래시카드 폴더 추가");
                    AddHeader();
                    getFlashcardMyFolderList();
                    flashcard_folder_select_process();

                    Log.e("flashcard menu", String.valueOf(flashcard_menu));

                }
            }
        });

        menu_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginType.equals("null")){
                    menu_one.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                    menu_two.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                    menu_three.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                    menu_four.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button));
                    flashcard_menu=3;//나의 플래시 카드
                    total_query_count=0;
                    page = 0;
                    flashcard_list.clear();

                    if(my_folder_not_login_empty_conLayout.getVisibility() == View.INVISIBLE ||
                            my_folder_not_login_empty_conLayout.getVisibility() == View.GONE){
                        my_folder_not_login_empty_conLayout.setVisibility(View.VISIBLE);
                        flashcardListView.setVisibility(View.INVISIBLE);
                    }

                    TextView flashcard_empty_title_textView = (TextView) rootView.findViewById(R.id.flashcard_empty_title_textView);
                    TextView flashcard_empty_notice_textView = (TextView) rootView.findViewById(R.id.flashcard_empty_notice_textView);

                    flashcard_empty_title_textView.setText("내가 작성한 플래시카드");
                    flashcard_empty_notice_textView.setText("로그인 후 플래시 카드를 만들어 보세요.");
                }else{
                    // 폴더 로그인 했을떄
                    if(my_folder_not_login_empty_conLayout.getVisibility() == View.VISIBLE){
                        my_folder_not_login_empty_conLayout.setVisibility(View.INVISIBLE);
                        flashcardListView.setVisibility(View.VISIBLE);
                    }


                    menu_one.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                    menu_two.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                    menu_three.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button_off));
                    menu_four.setBackground(getResources().getDrawable(R.drawable.test_fragment_navi_button));
                    flashcard_menu=3;//나의 플래시 카드
                    total_query_count=0;
                    page = 0;
                    flashcard_list.clear();

                    flashcard_list =new ArrayList<FlashCardList>();
                    flashcard_adapter = new FlashcardAdapter(getActivity(), flashcard_list);
                    flashcardListView.setAdapter(flashcard_adapter);
                    flashcardListView.removeFooterView(listview_footer);
                    see_more_textView.setText("더 보기");
                    ListViewFooterControl(rootView);
                    AddHeader();
                    getFlashcardList(exam_selection_code);// 플레쉬카드

                }
            }
        });
    }
    public void flashcard_folder_select_process(){
        flashcardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0){
                    Log.e("short click", String.valueOf(i-1));
                    String folder_code = flashcard_my_list.get(i-1).getFolder_code();
                    String folder_name = flashcard_my_list.get(i-1).getFolder_name();
                    String flashcard_length = flashcard_my_list.get(i-1).getFlashcard_length();

                    Intent intent = new Intent(getActivity(), FlashCardViewActivity.class);
                    intent.putExtra("type", "my_folder");
                    intent.putExtra("folder_name", folder_name);
                    intent.putExtra("folder_code", folder_code);
                    intent.putExtra("flashcard_length", flashcard_length);
                    startActivity(intent);
                    slide_left_and_slide_in();
                }
            }
        });

        flashcardListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View v, int i, long l) {
                if(i != 0){
                    Log.e("long click", String.valueOf(i-1));
                    final String folder_code = flashcard_my_list.get(i-1).getFolder_code();
                    String folder_name = flashcard_my_list.get(i-1).getFolder_name();
                    String flashcard_length = flashcard_my_list.get(i-1).getFlashcard_length();

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
    public void updateDeleteSelectedFolder(final String folder_code, final String del_type){
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/updateDeleteSelectedFolder.php";
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
                                    flashcard_my_list.clear();
                                    getFlashcardMyFolderList();
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
    public void slide_left_and_slide_in(){
        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
    }
    private void del_notifier(String message, String positive_message, String negative_message,
                              final String folder_code, final String del_type, final LinearLayout delete_linLayout){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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



    public void AddHeader(){
        flashcardListView.removeHeaderView(listview_header);
        listview_header = getLayoutInflater().inflate(R.layout.container_flashcard_header, null);
        header_title_textView = (TextView) listview_header.findViewById(R.id.header_title_textView);
        notice_textView = (TextView) listview_header.findViewById(R.id.notice_textView);

        flashcardListView.addHeaderView(listview_header);
        //header
        if(flashcard_menu == 0){
            header_title_textView.setText("플래시 카드 최신순");
            String message = "플래시 카드 최신순";
            notice_textView.setText(message);
        }else if(flashcard_menu == 1){
            header_title_textView.setText("플래시 카드 인기순");
            String message = "플래시 카드 인기순";
            notice_textView.setText(message);
        }else if(flashcard_menu ==2) {
            header_title_textView.setText("내 플래시 카드 폴더");
            String message = "폴더 삭제 또는 폴더 내용 삭제를 하시려면 폴더를 2초 동안 꾹 눌러주세요";
            notice_textView.setText(message);
        }else{
            //3 일때
            header_title_textView.setText("내 플래시 카드");
            String message = "내 플래시 카드";
            notice_textView.setText(message);
        }

    }
    public void ListViewFooterControl(View rootView){
        listview_footer = getLayoutInflater().inflate(R.layout.listview_footer_see_more, null);
        see_more_textView = listview_footer.findViewById(R.id.see_more_textView);
        see_more_pb = listview_footer.findViewById(R.id.see_more_progressbar);



        flashcardListView.addFooterView(listview_footer);
        listview_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                flashcard_menu = 0;// flashcard menu 0 은 기분  1 은 , 2는 , 3은 /..
                if(flashcard_menu== 0 || flashcard_menu==1 || flashcard_menu ==3){

                    // footer
                    if((page+15) >= total_query_count){
                        see_more_textView.setText("마지막 페이지");
                    }else{
                        see_more_textView.setText("더 보기");
                        page += 15;
                        see_more_pb.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                            @Override
                            public void run() {
                                getFlashcardList(exam_selection_code);
                            }
                        }, 800);
                    }
                }else{

                    folder_create_editable_alertDialog();
                }
            }
        });

    }
    public void swiper(View RootView){
        mSwipeRefreshLayout = (SwipeRefreshLayout) RootView.findViewById(R.id.FragmentFlashcardSwiperLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0; // refresh 했을때 page 를 0으로 만들어 디비 limit 을 초기화시킨다.

                if(exam_selection_code.equals("null")){
                    if(flashcard_menu == 0 || flashcard_menu == 1 || flashcard_menu == 3){
                        Log.e("flashcard exam ", exam_selection_code);
                        flashcard_list.clear();
                        getFlashcardList(exam_selection_code);// 플레쉬카드
                    }else{
                        if(LoginType.equals("null")){

                        }else{
                            flashcard_my_list.clear();
                            getFlashcardMyFolderList();
                        }
                    }
                }else{
                    if(flashcard_menu == 0 || flashcard_menu == 1|| flashcard_menu == 3) {
                        Log.e("flashcard exam ", exam_selection_code);
                        flashcard_list.clear();
                        getFlashcardList(exam_selection_code);// 플레쉬카드
                    }else{
                        if(LoginType.equals("null")){

                        }else{
                            flashcard_my_list.clear();
                            getFlashcardMyFolderList();
                        }
                    }

                }
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
    }
    public void toast(String message){
        Toast toast = new Toast(getActivity());
        toast.setGravity(Gravity.CENTER, 0,0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(32,32,32,32); // left top right bottom
        TextView tv = new TextView(getActivity());
//        tv.setBackgroundColor(getResources().getColor(R.color.colorExamViewMain));
        tv.setBackground(getResources().getDrawable(R.drawable.toast_background));
        tv.setPadding(16,16,16,16);
        tv.setLayoutParams(params);
        tv.setTextSize(16);
        tv.setText(message);
        toast.setView(tv);
        toast.show();
    }
    private void folder_create_editable_alertDialog(){
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
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
        String url_getSelectedExam = "http://www.joonandhoon.com/pp/PassPop/android/server/uploadNewlyCreatedFolder.php";
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_getSelectedExam,
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
//                                    toast(message);
                                    flashcard_my_list.clear();
                                    getFlashcardMyFolderList();
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
                params.put("folder_name", TextUtils.htmlEncode(folder_name));
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void getFlashcardMyFolderList(){
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/getFlashCardMyFolderList.php";
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("my flashcard ::", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");

                            if(access.equals("valid")){
                                // access valid

                                    JSONArray jsonArray = jsonObject.getJSONArray("json_response");
                                    for(int i = 0 ; i< jsonArray.length(); i++){
                                        String folder_name = Html.fromHtml((String) jsonArray.getJSONObject(i).getString("folder_name")).toString();
                                        String folder_code = jsonArray.getJSONObject(i).getString("folder_code");
                                        String folder_length = jsonArray.getJSONObject(i).getString("length");

                                        FlashCardMyList elements = new FlashCardMyList(folder_name, folder_code, folder_length);
                                        flashcard_my_list.add(elements);
                                    }

                                flashcard_my_list_adapter.notifyDataSetChanged();
                            }else{
                                //access invalid
                            }

                            see_more_pb.setVisibility(View.INVISIBLE);
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
                params.put("page", String.valueOf(page));

                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void getFlashcardList(final String exam_code){
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/getFlashCardList.php";
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("flashcard ::", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            total_query_count = Integer.parseInt(jsonObject.getString("total_query_count"));

                            if(access.equals("valid")){
                                // access valid
                                JSONArray jsonArray = jsonObject.getJSONArray("response");
                                if(total_query_count == 0){
                                    //flashcard 가 존재하지 않을때.
                                    my_folder_not_login_empty_conLayout.setVisibility(View.VISIBLE);
                                    TextView flashcard_empty_title_textView = (TextView) rootView.findViewById(R.id.flashcard_empty_title_textView);
                                    TextView flashcard_empty_notice_textView = (TextView) rootView.findViewById(R.id.flashcard_empty_notice_textView);
                                    if(flashcard_menu == 0){
                                        flashcard_empty_title_textView.setText("플래시 카드 최신순");
                                        flashcard_empty_notice_textView.setText("해당 시험에 대한 플래시 카드가 없습니다.\n플래시카드를 작성해주세요.");
                                    }else if (flashcard_menu ==1){
                                        flashcard_empty_title_textView.setText("플래시 카드 인기순");
                                        flashcard_empty_notice_textView.setText("해당 시험에 대한 플래시 카드가 없습니다.\n플래시카드를 작성해주세요.");
                                    }else{
                                        // 3일때
                                        flashcard_empty_title_textView.setText("내 플래시 카드");
                                        flashcard_empty_notice_textView.setText("작성하신 플래시카드가 없습니다");
                                    }
                                }else{
                                    my_folder_not_login_empty_conLayout.setVisibility(View.INVISIBLE);
                                    for(int i = 0 ; i< jsonArray.length(); i++){
                                        String fc_db_id = jsonArray.getJSONObject(i).getString("flashcard_db_id");
                                        String fc_exam_code = jsonArray.getJSONObject(i).getString("exam_code");
                                        String fc_exam_name = jsonArray.getJSONObject(i).getString("exam_name");
                                        String fc_subject_code = jsonArray.getJSONObject(i).getString("subject_code");
                                        String fc_subject_name = jsonArray.getJSONObject(i).getString("subject_name");
                                        String fc_author_login_type = jsonArray.getJSONObject(i).getString("author_login_type");
                                        String fc_author_id = jsonArray.getJSONObject(i).getString("author_id");
                                        String fc_author_nickname = jsonArray.getJSONObject(i).getString("author_nickname");
                                        String fc_upload_date = jsonArray.getJSONObject(i).getString("upload_date");
                                        String fc_upload_time = jsonArray.getJSONObject(i).getString("upload_time");
                                        String fc_comment_count = jsonArray.getJSONObject(i).getString("comment_count");
                                        String fc_flashcard_hit = jsonArray.getJSONObject(i).getString("flashcard_hit");
                                        String fc_flashcard_scrap = jsonArray.getJSONObject(i).getString("flashcard_scrap");
                                        String fc_flashcard_like = jsonArray.getJSONObject(i).getString("flashcard_like");
                                        String fc_flashcard_title = jsonArray.getJSONObject(i).getString("flashcard_title");
                                        String fc_flashcard_count = jsonArray.getJSONObject(i).getString("flashcard_count");
                                        String fc_flashcard_first_term = jsonArray.getJSONObject(i).getString("flashcard_first_term");

                                        FlashCardList elements = new FlashCardList(fc_db_id, fc_exam_code, fc_exam_name, fc_subject_name, fc_author_login_type, fc_author_id,
                                                fc_author_nickname, fc_upload_date, fc_upload_time, fc_flashcard_scrap, fc_comment_count, fc_flashcard_hit, fc_flashcard_like, fc_flashcard_title, fc_flashcard_count, fc_flashcard_first_term);
                                        flashcard_list.add(elements);
                                    }
                                    flashcard_adapter.notifyDataSetChanged();
                                }
                            }else{
                                //access invalid
                            }
                            see_more_pb.setVisibility(View.INVISIBLE);
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
                params.put("menu_selection", String.valueOf(flashcard_menu));
                params.put("exam_code", exam_code);
                params.put("page", String.valueOf(page));

                params.put("login_type", LoginType);
                params.put("user_id", G_user_id);

                return params;
            }
        };
        queue.add(stringRequest);
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
}
