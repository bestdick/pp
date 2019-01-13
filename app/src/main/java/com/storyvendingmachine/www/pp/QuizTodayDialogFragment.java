package com.storyvendingmachine.www.pp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.storyvendingmachine.www.pp.ExamViewActivity.answer;
import static com.storyvendingmachine.www.pp.ExamViewActivity.eviewPager;
import static com.storyvendingmachine.www.pp.MainFragment.quizUserSelectedAnswers;
import static com.storyvendingmachine.www.pp.MainFragment.quiz_viewPager;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
//// * {@link QuizTodayDialogFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link QuizTodayDialogFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class QuizTodayDialogFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Bundle mParam1;
    private String mParam2; // position

    TextView subject_textView, exam_name_textView, question_textView, question_example_textView;
    ImageView question_imageView;



//    private OnFragmentInteractionListener mListener;
    public QuizTodayDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizTodayDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizTodayDialogFragment newInstance(Bundle param1, String param2) {
        QuizTodayDialogFragment fragment = new QuizTodayDialogFragment();
        Bundle args = new Bundle();
        args.putBundle(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getBundle(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =inflater.inflate(R.layout.fragment_quiz_today_dialog, container, false);
        initializer(rootview);
        return rootview;
    }

    public void initializer(View rootview){
        Log.e("length of quiz answer", String.valueOf(quizUserSelectedAnswers.size()));
        String exam_code = mParam1.getString("exam_code");
        String exam_name = mParam1.getString("exam_name");
        String exam_placed_year = mParam1.getString("exam_placed_year");
        String exam_placed_round = mParam1.getString("exam_placed_round");
        String subject_code = mParam1.getString("subject_code");
        String subject_name = mParam1.getString("subject_name");
        String question_number = mParam1.getString("question_number");
        String question_question = mParam1.getString("question_question");
        String question_answer = mParam1.getString("question_answer");
        String correct_answer = mParam1.getString("correct_answer");
        String question_image_exist = mParam1.getString("question_image_exist");
        String answer_image_exist = mParam1.getString("answer_image_exist");
        String example_exist = mParam1.getString("example_exist");

        subject_textView = rootview.findViewById(R.id.subject_textView);
        exam_name_textView = rootview.findViewById(R.id.exam_name_textView);


        subject_textView.setText(subject_name);
        exam_name_textView.setText(exam_name);


        if_question_image_exist( rootview,  question_question,  question_image_exist,  exam_code,  exam_placed_year,  exam_placed_round,  subject_code,  question_number);
        if_answer_image_exist( rootview,  question_answer,  answer_image_exist,  exam_code,  exam_placed_year,  exam_placed_round,  subject_code,  question_number);
        if_example_exist(rootview, question_question, example_exist);
    }
    public void if_example_exist(View rootview, String question_question, String example_exist){
        if(example_exist.equals("true")){
            String[] question = Html.fromHtml((String) question_question).toString().split("#_SPLIT_#");
            question_textView = (TextView)rootview.findViewById(R.id.question_textView);
            question_example_textView = (TextView)rootview.findViewById(R.id.question_example_textView);
            question_example_textView.setVisibility(View.VISIBLE);
            question_textView.setText("["+(Integer.parseInt(mParam2)+1)+"] " +question[0]);
            question_example_textView.setText(question[1]);
        }else{
            String question = Html.fromHtml((String) question_question).toString();
            question_textView = (TextView)rootview.findViewById(R.id.question_textView);
            question_example_textView = (TextView)rootview.findViewById(R.id.question_example_textView);
            question_example_textView.setVisibility(View.GONE);
            question_textView.setText((Integer.parseInt(mParam2)+1)+" " +question);
        }
    }
    public void if_question_image_exist(View rootview, String question_question, String question_image_exist, String exam_code, String published_year, String published_round, String subject_code, String question_number){
        if(question_image_exist.equals("true")){
            String question = Html.fromHtml((String) question_question).toString();
            question_textView = (TextView) rootview.findViewById(R.id.question_textView);
            question_textView.setText((Integer.parseInt(mParam2)+1)+" " +question);
            question_imageView = (ImageView) rootview.findViewById(R.id.question_imageView);
            question_imageView.setVisibility(View.VISIBLE);
            String url = "http://www.joonandhoon.com/pp/PassPop/exam_images/"+exam_code+"/"+exam_code+"_"+published_year+"_"+published_round+"_"+subject_code+"_q_"+question_number+".PNG";
            getQuestionImage(question_imageView, url);
        }else{
            String question = Html.fromHtml((String) question_question).toString();
            question_textView = (TextView) rootview.findViewById(R.id.question_textView);
            question_textView.setText((Integer.parseInt(mParam2)+1)+" " +question);
            question_imageView = (ImageView) rootview.findViewById(R.id.question_imageView);
            question_imageView.setVisibility(View.GONE);
        }
    }
    public void if_answer_image_exist(View rootview, String question_answer, String answer_image_exist, String exam_code, String published_year, String published_round, String subject_code, String question_number){
        if(answer_image_exist.equals("true")){
            ConstraintLayout answer_1_conLayout, answer_2_conLayout, answer_3_conLayout, answer_4_conLayout;
            answer_1_conLayout = (ConstraintLayout) rootview.findViewById(R.id.answer_1_conLayout);
            answer_2_conLayout = (ConstraintLayout) rootview.findViewById(R.id.answer_2_conLayout);
            answer_3_conLayout = (ConstraintLayout) rootview.findViewById(R.id.answer_3_conLayout);
            answer_4_conLayout = (ConstraintLayout) rootview.findViewById(R.id.answer_4_conLayout);

            ImageView answer_1_imageView, answer_2_imageView, answer_3_imageView, answer_4_imageView;
            answer_1_imageView = (ImageView) rootview.findViewById(R.id.answer_1_imageView);
            answer_1_imageView.setTag(1);
            answer_2_imageView = (ImageView) rootview.findViewById(R.id.answer_2_imageView);
            answer_2_imageView.setTag(2);
            answer_3_imageView = (ImageView) rootview.findViewById(R.id.answer_3_imageView);
            answer_3_imageView.setTag(3);
            answer_4_imageView = (ImageView) rootview.findViewById(R.id.answer_4_imageView);
            answer_4_imageView.setTag(4);

            answer_1_conLayout.setVisibility(View.VISIBLE);
            answer_2_conLayout.setVisibility(View.VISIBLE);
            answer_3_conLayout.setVisibility(View.VISIBLE);
            answer_4_conLayout.setVisibility(View.VISIBLE);
            for(int i = 1; i<=4; i++){
                String url = "http://www.joonandhoon.com/pp/PassPop/exam_images/"+exam_code+"/"+exam_code+"_"+published_year+"_"+published_round+"_"+subject_code+"_q_"+question_number+"_a_"+i+".PNG";
                ImageView imageView = (ImageView) rootview.findViewWithTag(i);
                getAnswerImage(imageView, url);
            }
            imageAnswerChoice(answer_1_conLayout, answer_2_conLayout, answer_3_conLayout, answer_4_conLayout);
        }else{
            String[] answers = Html.fromHtml((String) question_answer).toString().split("##");
            TextView answer_1_textView,answer_2_textView,answer_3_textView,answer_4_textView;
            answer_1_textView = (TextView) rootview.findViewById(R.id.answer_1_textView);
            answer_2_textView = (TextView) rootview.findViewById(R.id.answer_2_textView);
            answer_3_textView = (TextView) rootview.findViewById(R.id.answer_3_textView);
            answer_4_textView = (TextView) rootview.findViewById(R.id.answer_4_textView);

            answer_1_textView.setVisibility(View.VISIBLE);
            answer_2_textView.setVisibility(View.VISIBLE);
            answer_3_textView.setVisibility(View.VISIBLE);
            answer_4_textView.setVisibility(View.VISIBLE);

            answer_1_textView.setText("①."+answers[0]);
            answer_2_textView.setText("②."+answers[1]);
            answer_3_textView.setText("③."+answers[2]);
            answer_4_textView.setText("④."+answers[3]);

            answerChoice(answer_1_textView, answer_2_textView, answer_3_textView, answer_4_textView);
        }
    }



    public void imageAnswerChoice(final ConstraintLayout one, final ConstraintLayout two, final ConstraintLayout three, final ConstraintLayout four){
        final int page = Integer.parseInt(mParam2);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                quizUserSelectedAnswers.set(page, 1);
                quiz_viewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(quizUserSelectedAnswers.get(page)));
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                quizUserSelectedAnswers.set(page, 2);
                quiz_viewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(quizUserSelectedAnswers.get(page)));

            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                quizUserSelectedAnswers.set(page, 3);
                quiz_viewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(quizUserSelectedAnswers.get(page)));
            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                quizUserSelectedAnswers.set(page, 4);
                quiz_viewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(quizUserSelectedAnswers.get(page)));
            }
        });
    }
    public void answerChoice(final TextView one, final TextView two, final TextView three, final TextView four){
        final int page = Integer.parseInt(mParam2);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                quizUserSelectedAnswers.set(page, 1);
                quiz_viewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(quizUserSelectedAnswers.get(page)));
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                quizUserSelectedAnswers.set(page, 2);
                quiz_viewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(quizUserSelectedAnswers.get(page)));

            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                quizUserSelectedAnswers.set(page, 3);
                quiz_viewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(quizUserSelectedAnswers.get(page)));
            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                two.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                three.setBackground(getResources().getDrawable(R.drawable.answer_unselected_container));
                four.setBackground(getResources().getDrawable(R.drawable.answer_selected_container));
                quizUserSelectedAnswers.set(page, 4);
                quiz_viewPager.setCurrentItem(page+1);
                Log.e("answer", String.valueOf(quizUserSelectedAnswers.get(page)));
            }
        });
    }
    public void getQuestionImage(ImageView imageView, String url){
        Picasso.with(getContext())
                .load(url)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {
                        Log.e("load image", "fail to load question images ");
                    }
                });
    }
    public void getAnswerImage(ImageView imageView, String url){
        Picasso.with(getContext())
                .load(url)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {
                        Log.e("load image", "fail to load answer images ");
                    }
                });
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
