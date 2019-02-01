package com.storyvendingmachine.www.pp;

/**
 * Created by Administrator on 2018-06-24.
 */

public class TestYearOrderList {
    String exam_code;
    String exam_name;
    String published_year;
    String max_questions;
    String this_count;

    String published_num;
    String navi_selection;

    String total_taker;
    String pass_count;
    String fail_count;
    String percent;

    public String getThis_count() {
        return this_count;
    }

    public void setThis_count(String this_count) {
        this.this_count = this_count;
    }

    public String getMax_questions() {
        return max_questions;
    }

    public void setMax_questions(String max_questions) {
        this.max_questions = max_questions;
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

    public String getPublished_year() {
        return published_year;
    }

    public void setPublished_year(String published_year) {
        this.published_year = published_year;
    }

    public String getPublished_num() {
        return published_num;
    }

    public void setPublished_num(String published_num) {
        this.published_num = published_num;
    }

    public String getNavi_selection() {
        return navi_selection;
    }

    public void setNavi_selection(String navi_selection) {
        this.navi_selection = navi_selection;
    }

    public String getTotal_taker() {
        return total_taker;
    }

    public void setTotal_taker(String total_taker) {
        this.total_taker = total_taker;
    }

    public String getPass_count() {
        return pass_count;
    }

    public void setPass_count(String pass_count) {
        this.pass_count = pass_count;
    }

    public String getFail_count() {
        return fail_count;
    }

    public void setFail_count(String fail_count) {
        this.fail_count = fail_count;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public TestYearOrderList(String exam_code, String exam_name, String published_year, String max_questions, String this_count, String published_num, String navi_selection, String total_taker, String pass_count, String fail_count, String percent) {
        this.exam_code = exam_code;
        this.exam_name = exam_name;
        this.published_year = published_year;
        this.max_questions = max_questions;
        this.this_count = this_count;
        this.published_num = published_num;
        this.navi_selection = navi_selection;
        this.total_taker = total_taker;
        this.pass_count = pass_count;
        this.fail_count = fail_count;
        this.percent = percent;
    }

}
