package com.storyvendingmachine.www.pp;

public class NewsActivityGroupList {
    String news_title;
    String type;
    String upload_date;
    String upload_time;
    String isNew;

    public String getNews_title() {
        return news_title;
    }

    public void setNews_title(String news_title) {
        this.news_title = news_title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public NewsActivityGroupList(String news_title, String type, String upload_date, String upload_time, String isNew) {
        this.news_title = news_title;
        this.type = type;
        this.upload_date = upload_date;
        this.upload_time = upload_time;
        this.isNew = isNew;
    }
}
