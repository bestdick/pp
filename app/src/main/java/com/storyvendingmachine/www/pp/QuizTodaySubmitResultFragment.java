package com.storyvendingmachine.www.pp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.pp.MainActivity.G_user_id;
import static com.storyvendingmachine.www.pp.MainActivity.LoginType;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_code;
import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_name;
import static com.storyvendingmachine.www.pp.MainFragment.quizUserSelectedAnswers;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link QuizTodaySubmitResultFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link QuizTodaySubmitResultFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class QuizTodaySubmitResultFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";//jsonarray
    private static final String ARG_PARAM2 = "param2";//today(quiz date)

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    ProgressBar progressBar;
    ProgressBar percent_bar;
//    private OnFragmentInteractionListener mListener;

    public QuizTodaySubmitResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizTodaySubmitResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizTodaySubmitResultFragment newInstance(String param1, String param2) {
        QuizTodaySubmitResultFragment fragment = new QuizTodaySubmitResultFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);// 이놈이 quiz 에 대한 모든 문제를 가지고있다.
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View rootview = inflater.inflate(R.layout.fragment_quiz_today_submit_result, container, false);
         progressBar =  (ProgressBar) rootview.findViewById(R.id.quiz_progressBar);
         percent_bar = (ProgressBar) rootview.findViewById(R.id.percent_bar);


        whenQuizSubmitButtonClick(rootview);
        return rootview;
    }
    public void whenQuizSubmitButtonClick(final View rootview){
        final ConstraintLayout quiz_submit_conLayout = (ConstraintLayout) rootview.findViewById(R.id.quiz_submit_conLayout);
        TextView quiz_submit_textView = (TextView) rootview.findViewById(R.id.quiz_submit_textView);

        final ConstraintLayout quiz_result_conLayout = (ConstraintLayout) rootview.findViewById(R.id.quiz_result_conLayout);
        quiz_submit_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setClickable(false);
                progressbar_visible();
                new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                    @Override
                    public void run() {
                        progressbar_invisible();
                        quiz_submit_conLayout.setVisibility(View.INVISIBLE);
                        quiz_result_conLayout.setVisibility(View.VISIBLE);
                        JSONArray jsonArray = combineProblems_And_UserAnswer_And_Score(rootview);//여기서 점수까지 한번에 계산
                        uploadQuizTodayData_returnResult(jsonArray);
                    }
                }, 500); // 2.5초 후에 실행됨

            }
        });
    }

    public JSONArray combineProblems_And_UserAnswer_And_Score(View rootview){
//also this function compare correct and incorrect to return score
        final ConstraintLayout circle_conLayout = (ConstraintLayout) rootview.findViewById(R.id.circle_conLayout);
        final TextView score_percent_textView = (TextView) rootview.findViewById(R.id.score_percent_textView);
        final TextView score_fraction_textView = (TextView) rootview.findViewById(R.id.score_fraction_textView);
        final JSONArray jsonArray_return = new JSONArray();
        try{
            JSONArray jsonArray = new JSONArray(mParam1);
            int count = jsonArray.length();
            int correct = 0;
            for(int i = 0 ; i<count; i++){
                int user_answer = quizUserSelectedAnswers.get(i);
                JSONObject object = jsonArray.getJSONObject(i);
                object.put("user_answer", String.valueOf(user_answer));
                String primary_key = object.getString("primary_key");
                String exam_code = object.getString("exam_code");
                String exam_name = object.getString("exam_name");
                String exam_placed_year = object.getString("exam_placed_year");
                String exam_placed_round = object.getString("exam_placed_round");
                String subject_code = object.getString("subject_code");
                String subject_name = object.getString("subject_name");
                String question_number = object.getString("question_number");
                String question_question = object.getString("question_question");
                String question_answer = object.getString("question_answer");
                String correct_answer = object.getString("correct_answer");
                String question_image_exist = object.getString("question_image_exist");
                String answer_image_exist = object.getString("answer_image_exist");
                String example_exist = object.getString("example_exist");
                jsonArray_return.put(object);

                if(user_answer == Integer.parseInt(correct_answer)){
                    correct++;
                }
            }

            float float_correct  = correct;
            float float_count = count;
            float percent  =  (float_correct/float_count)*100;
            String percent_str =  String.format("%.2f", percent);
            String fraction = correct+" / "+count;
            percent_bar.setProgress((int) percent);

            score_percent_textView.setText(percent_str+" % ");
            score_fraction_textView.setText(fraction);


            final String current_time = currecnt_time();
            circle_conLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), QuizResultActivity.class);
                    intent.putExtra("isNew", "new");
                    intent.putExtra("quiz_date", mParam2);
                    intent.putExtra("current_time", current_time);
                    intent.putExtra("json_array", jsonArray_return.toString());
                    startActivity(intent);
                    slide_left_and_slide_in();
                }
            });

        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonArray_return;
    }

    public String currecnt_time(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
    public void uploadQuizTodayData_returnResult(final JSONArray jsonArray){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://www.joonandhoon.com/pp/PassPop/android/server/uploadResultOfQuizToday.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("quiz upload result" , response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access = jsonObject.getString("access");
                            if(access.equals("valid")){
                                progressbar_invisible();
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
                params.put("quiz_date", mParam2);
                params.put("exam_code", exam_selection_code);
                params.put("json_data", TextUtils.htmlEncode(jsonArray.toString()));
                return params;
            }
        };
        queue.add(stringRequest);
    }



    public void progressbar_visible(){
        progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void progressbar_invisible(){
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
    public void slide_left_and_slide_in(){//opening new activity
        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); // 처음이 앞으로 들어올 activity 두번째가 현재 activity 가 할 애니매이션
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

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
