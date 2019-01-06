package com.storyvendingmachine.www.pp;

public class StatisticExpandable_List_Group {
    String exam_code;
    String exam_name;
    String exam_placed_year;
    String exam_placed_round;
    String count;

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

    public String getExam_placed_year() {
        return exam_placed_year;
    }

    public void setExam_placed_year(String exam_placed_year) {
        this.exam_placed_year = exam_placed_year;
    }

    public String getExam_placed_round() {
        return exam_placed_round;
    }

    public void setExam_placed_round(String exam_placed_round) {
        this.exam_placed_round = exam_placed_round;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public StatisticExpandable_List_Group(String exam_code, String exam_name, String exam_placed_year, String exam_placed_round, String count) {
        this.exam_code = exam_code;
        this.exam_name = exam_name;
        this.exam_placed_year = exam_placed_year;
        this.exam_placed_round = exam_placed_round;
        this.count = count;
    }
}
