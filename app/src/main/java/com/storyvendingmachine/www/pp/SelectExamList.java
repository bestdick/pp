package com.storyvendingmachine.www.pp;

/**
 * Created by Administrator on 2018-06-24.
 */

public class SelectExamList {


    String exam_type_code;
    String exam_type_name;
    String exam_code;
    String exam_name;
    String from;

    public String getExam_type_code() {
        return exam_type_code;
    }

    public void setExam_type_code(String exam_type_code) {
        this.exam_type_code = exam_type_code;
    }

    public String getExam_type_name() {
        return exam_type_name;
    }

    public void setExam_type_name(String exam_type_name) {
        this.exam_type_name = exam_type_name;
    }
    public String getExam_code() {
        return exam_code;
    }
    public void setExam_code(String exam_code) {
        this.exam_code = exam_code;
    }

    public String getExam_name() {
        return exam_name;
    }

    public void setExam_name(String exam_name) {
        this.exam_name = exam_name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public SelectExamList(String exam_type_code, String exam_type_name, String exam_code, String exam_name, String from){
        this.exam_type_code = exam_type_code;
        this.exam_type_name = exam_type_name;
        this.exam_code = exam_code;
        this.exam_name = exam_name;
        this.from = from;
    }


}
