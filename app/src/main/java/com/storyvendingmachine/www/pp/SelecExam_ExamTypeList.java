package com.storyvendingmachine.www.pp;

/**
 * Created by Administrator on 2018-09-28.
 */

public class SelecExam_ExamTypeList {
    String exam_type_code;
    String exam_type_name;

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
    public SelecExam_ExamTypeList(String exam_type_code, String exam_type_name){
        this.exam_type_code = exam_type_code;
        this.exam_type_name = exam_type_name;
    }
}
