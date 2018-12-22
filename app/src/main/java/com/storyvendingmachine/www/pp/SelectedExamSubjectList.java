package com.storyvendingmachine.www.pp;

/**
 * Created by Administrator on 2018-12-20.
 */

public class SelectedExamSubjectList {
    String exam_code;
    String exam_name;
    String subject_number;
    String subject_name;
    String subject_code;

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

    public String getSubject_number() {
        return subject_number;
    }

    public void setSubject_number(String subject_number) {
        this.subject_number = subject_number;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getSubject_code() {
        return subject_code;
    }

    public void setSubject_code(String subject_code) {
        this.subject_code = subject_code;
    }

    public SelectedExamSubjectList(String exam_code, String exam_name, String subject_number, String subject_name, String subject_code) {
        this.exam_code = exam_code;
        this.exam_name = exam_name;
        this.subject_number = subject_number;
        this.subject_name = subject_name;
        this.subject_code = subject_code;
    }
}
