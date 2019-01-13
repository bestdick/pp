package com.storyvendingmachine.www.pp;

public class NewsActivityItemList {
    String key;
    String type;
    String title;
    String content;
    String upload_date;
    String upload_time;
    String isNew;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public NewsActivityItemList(String key, String type, String title, String content, String upload_date, String upload_time, String isNew) {
        this.key = key;
        this.type = type;
        this.title = title;
        this.content = content;
        this.upload_date = upload_date;
        this.upload_time = upload_time;
        this.isNew = isNew;
    }
}
