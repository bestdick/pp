package com.storyvendingmachine.www.pp;

import org.json.JSONArray;

public class StatisticExpandable_List_Item {
    String date_user_took_exam;
    String time_user_took_exam;
    String time_duration;
    JSONArray subject_score_json;
    String total_score;
    String pass;

    public String getDate_user_took_exam() {
        return date_user_took_exam;
    }

    public void setDate_user_took_exam(String date_user_took_exam) {
        this.date_user_took_exam = date_user_took_exam;
    }

    public String getTime_user_took_exam() {
        return time_user_took_exam;
    }

    public void setTime_user_took_exam(String time_user_took_exam) {
        this.time_user_took_exam = time_user_took_exam;
    }

    public String getTime_duration() {
        return time_duration;
    }

    public void setTime_duration(String time_duration) {
        this.time_duration = time_duration;
    }

    public JSONArray getSubject_score_json() {
        return subject_score_json;
    }

    public void setSubject_score_json(JSONArray subject_score_json) {
        this.subject_score_json = subject_score_json;
    }

    public String getTotal_score() {
        return total_score;
    }

    public void setTotal_score(String total_score) {
        this.total_score = total_score;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public StatisticExpandable_List_Item(String date_user_took_exam, String time_user_took_exam, String time_duration, JSONArray subject_score_json, String total_score, String pass) {
        this.date_user_took_exam = date_user_took_exam;
        this.time_user_took_exam = time_user_took_exam;
        this.time_duration = time_duration;
        this.subject_score_json = subject_score_json;
        this.total_score = total_score;
        this.pass = pass;
    }
}
