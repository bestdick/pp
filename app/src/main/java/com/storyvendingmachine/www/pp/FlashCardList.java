package com.storyvendingmachine.www.pp;

/**
 * Created by Administrator on 2018-10-23.
 */

public class FlashCardList {
    String flashcard_db_id;
    String exam_code;
    String exam_name;
    String subject;
    String login_type;
    String user_id;
    String user_nickname;
    String upload_date;
    String upload_time;
    String scrap_count;
    String comment_count;
    String hit_count;
    String like_count;
    String title;
    String flashcard_count;
    String flashcard_first_term;

    public String getFlashcard_db_id() {
        return flashcard_db_id;
    }

    public void setFlashcard_db_id(String flashcard_db_id) {
        this.flashcard_db_id = flashcard_db_id;
    }

    public String getFlashcard_count() {
        return flashcard_count;
    }

    public void setFlashcard_count(String flashcard_count) {
        this.flashcard_count = flashcard_count;
    }

    public String getFlashcard_first_term() {
        return flashcard_first_term;
    }

    public void setFlashcard_first_term(String flashcard_first_term) {
        this.flashcard_first_term = flashcard_first_term;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    public String getUser_id() {
        return user_id;
    }


    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    public String getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(String upload_time) {
        this.upload_time = upload_time;
    }

    public String getScrap_count() {
        return scrap_count;
    }

    public void setScrap_count(String scrap_count) {
        this.scrap_count = scrap_count;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public String getHit_count() {
        return hit_count;
    }

    public void setHit_count(String hit_count) {
        this.hit_count = hit_count;
    }

    public String getLike_count() {
        return like_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }


    public FlashCardList(String flashcard_db_id, String exam_code, String exam_name, String subject, String login_type, String user_id, String user_nickname, String upload_date, String upload_time, String scrap_count,
                         String comment_count, String hit_count, String like_count, String title, String flashcard_count, String flashcard_first_term){
        this.flashcard_db_id = flashcard_db_id;
        this.exam_code = exam_code;
        this.exam_name = exam_name;
        this.subject = subject;
        this.login_type = login_type;
        this.user_id = user_id;
        this.user_nickname = user_nickname;
        this.upload_date = upload_date;
        this.upload_time = upload_time;
        this.scrap_count = scrap_count;
        this.comment_count = comment_count;
        this.hit_count = hit_count;
        this.like_count = like_count;
        this.title = title;
        this.flashcard_count = flashcard_count;
        this.flashcard_first_term = flashcard_first_term;
    }


}
