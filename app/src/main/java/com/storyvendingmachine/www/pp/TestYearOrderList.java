package com.storyvendingmachine.www.pp;

/**
 * Created by Administrator on 2018-06-24.
 */

public class TestYearOrderList {
    String exam_code;
    String exam_name;
    String published_year;
    String published_num;
    String navi_selection;

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

    public TestYearOrderList(String exam_code, String exam_name, String published_year, String published_num, String navi_selection){
        this.exam_code = exam_code;
        this.exam_name = exam_name;
        this.published_year = published_year;
        this.published_num = published_num;
        this.navi_selection = navi_selection;
    }


}
