package com.storyvendingmachine.www.pp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.storyvendingmachine.www.pp.MainActivity.exam_selection_name;

public class MainFragmentScheduleFragment extends Fragment {
    String param1;
    Bundle param2;
    Bundle param3;
    public static MainFragmentScheduleFragment newInstance(String param1, Bundle param2, Bundle param3) {
        MainFragmentScheduleFragment fragment = new MainFragmentScheduleFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putBundle("param2", param2);
        args.putBundle("param3", param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        param1 = getArguments().getString("param1");
        param2 = getArguments().getBundle("param2");//스케쥴 정보
        param3 = getArguments().getBundle("param3");//dday 정보
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_fragment_schedule, container, false);
        ddayMaker(rootView);
        scheduleMaker(rootView);


        return rootView;
    }

    public void ddayMaker(View rootView){
        TextView exam_title = (TextView) rootView.findViewById(R.id.testtype_TextView);
        TextView remain_doc = (TextView) rootView.findViewById(R.id.ddaycount_TextView);
        TextView remain_prac = (TextView) rootView.findViewById(R.id.prac_ddaycount_TextView);

        String title = param3.getString("title");
        String doc_remain = param3.getString("doc_remain");
        String prac_remain = param3.getString("prac_remain");


        exam_title.setText(title);
        if(doc_remain.equals("end_exam")){
            remain_doc.setText("종료");// 실기 남은 날
            remain_doc.setTextSize(20);
            remain_doc.setTextColor(getResources().getColor(R.color.colorRed));
        }else{
            remain_doc.setText(doc_remain);
        }
        if(prac_remain.equals("end_exam")){
            remain_prac.setText("종료");// 실기 남은 날
            remain_prac.setTextSize(20);
            remain_prac.setTextColor(getResources().getColor(R.color.colorRed));
        }else{
            remain_prac.setText(prac_remain);
        }

    }

    public void scheduleMaker(View rootView){
//        TextView exam_placed_year_textView = (TextView) rootView.findViewById(R.id.implplanTextView);
        TextView docexamregisterdateTextView = (TextView) rootView.findViewById(R.id.docexamregisterdateTextView);
        TextView docexamdateTextView = (TextView) rootView.findViewById(R.id.docexamdateTextView);
        TextView docpassdateTextView = (TextView) rootView.findViewById(R.id.docpassdateTextView);
        TextView submitqualifierdateTextView = (TextView) rootView.findViewById(R.id.submitqualifierdateTextView);
        TextView pracregisterdateTextView = (TextView) rootView.findViewById(R.id.pracregisterdateTextView);
        TextView pracreexamdateTextView = (TextView) rootView.findViewById(R.id.pracreexamdateTextView);
        TextView pracrepassdateTextView = (TextView) rootView.findViewById(R.id.pracrepassdateTextView);

        String exam_name = param2.getString("exam_name");
        String exam_placed_year = param2.getString("exam_placed_year");
        String exam_placed_round = param2.getString("exam_placed_round");
        String docRegStartDt = param2.getString("docRegStartDt");
        String docRegEndDt = param2.getString("docRegEndDt");
        String docExamStartDt = param2.getString("docExamStartDt");
        String docPassDt = param2.getString("docPassDt");
        String docSubmitStartDt = param2.getString("docSubmitStartDt");
        String docSubmitEndDt = param2.getString("docSubmitEndDt");
        String pracRegStartDt = param2.getString("pracRegStartDt");
        String pracRegEndDt = param2.getString("pracRegEndDt");
        String pracExamStartDt = param2.getString("pracExamStartDt");
        String pracExamEndDt = param2.getString("pracExamEndDt");
        String pracPassStartDt = param2.getString("pracPassStartDt");
        String pracPassEndDt = param2.getString("pracPassEndDt");

//        exam_placed_year_textView.setText(exam_selection_name+" "+exam_placed_year +" 년 " +exam_placed_round+ "회");

        String docRegStartDt_str = dateSpliter(docRegStartDt)[0] +" 년 "+dateSpliter(docRegStartDt)[1]+" 월 "+ dateSpliter(docRegStartDt)[2] + " 일";
        String docRegEndDt_str =  dateSpliter(docRegEndDt)[0] +" 년 "+dateSpliter(docRegEndDt)[1]+" 월 "+ dateSpliter(docRegEndDt)[2] + " 일";
        docexamregisterdateTextView.setText(docRegStartDt_str + " ~ \n" + docRegEndDt_str);

        String docExamStartDt_str = dateSpliter(docExamStartDt)[0] +" 년 "+dateSpliter(docExamStartDt)[1]+" 월 "+ dateSpliter(docExamStartDt)[2] + " 일";
        docexamdateTextView.setText(docExamStartDt_str);

        String docPassDt_str = dateSpliter(docPassDt)[0] +" 년 "+dateSpliter(docPassDt)[1]+" 월 "+ dateSpliter(docPassDt)[2] + " 일";
        docpassdateTextView.setText(docPassDt_str);

        String docSubmitStartDt_str = dateSpliter(docSubmitStartDt)[0] +" 년 "+dateSpliter(docSubmitStartDt)[1]+" 월 "+ dateSpliter(docSubmitStartDt)[2] + " 일";
        String docSubmitEndDt_str = dateSpliter(docSubmitEndDt)[0] +" 년 "+dateSpliter(docSubmitEndDt)[1]+" 월 "+ dateSpliter(docSubmitEndDt)[2] + " 일";
        submitqualifierdateTextView.setText(docSubmitStartDt_str+" ~ \n"+docSubmitEndDt_str);

        String pracRegStartDt_str =  dateSpliter(pracRegStartDt)[0] +" 년 "+dateSpliter(pracRegStartDt)[1]+" 월 "+ dateSpliter(pracRegStartDt)[2] + " 일";
        String pracRegEndDt_str =   dateSpliter(pracRegEndDt)[0] +" 년 "+dateSpliter(pracRegEndDt)[1]+" 월 "+ dateSpliter(pracRegEndDt)[2] + " 일";
        pracregisterdateTextView.setText(pracRegStartDt_str +" ~ \n"+ pracRegEndDt_str);

        String pracExamStartDt_str = dateSpliter(pracExamStartDt)[0] +" 년 "+dateSpliter(pracExamStartDt)[1]+" 월 "+ dateSpliter(pracExamStartDt)[2] + " 일";
        String pracExamEndDt_str = dateSpliter(pracExamEndDt)[0] +" 년 "+dateSpliter(pracExamEndDt)[1]+" 월 "+ dateSpliter(pracExamEndDt)[2] + " 일";
        pracreexamdateTextView.setText(pracExamStartDt_str + " ~ \n"+ pracExamEndDt_str);

        String pracPassStartDt_str = dateSpliter(pracPassStartDt)[0] +" 년 "+dateSpliter(pracPassStartDt)[1]+" 월 "+ dateSpliter(pracPassStartDt)[2] + " 일";
        String pracPassEndDt_str = dateSpliter(pracPassEndDt)[0] +" 년 "+dateSpliter(pracPassEndDt)[1]+" 월 "+ dateSpliter(pracPassEndDt)[2] + " 일";
        pracrepassdateTextView.setText(pracPassStartDt_str+" ~ \n"+ pracPassEndDt_str);
    }

    public String[] dateSpliter(String date){
        String year = String.valueOf(date.charAt(0))+String.valueOf(date.charAt(1))+String.valueOf(date.charAt(2))+String.valueOf(date.charAt(3));
        String month = String.valueOf(date.charAt(4))+String.valueOf(date.charAt(5));
        String day = String.valueOf(date.charAt(6))+String.valueOf(date.charAt(7));
        String[] year_month_day  = new String[3];
        year_month_day[0] = year;
        year_month_day[1] = month;
        year_month_day[2] = day;
        return year_month_day;
    }


}
