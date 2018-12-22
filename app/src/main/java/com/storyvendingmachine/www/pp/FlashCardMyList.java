package com.storyvendingmachine.www.pp;

/**
 * Created by Administrator on 2018-11-05.
 */

public class FlashCardMyList {
    String folder_name;
    String folder_code;
    String flashcard_length;

    public String getFolder_name() {
        return folder_name;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    public String getFolder_code() {
        return folder_code;
    }

    public void setFolder_code(String folder_code) {
        this.folder_code = folder_code;
    }

    public String getFlashcard_length() {
        return flashcard_length;
    }

    public void setFlashcard_length(String flashcard_length) {
        this.flashcard_length = flashcard_length;
    }

    public FlashCardMyList(String folder_name, String folder_code, String flashcard_length){
        this.folder_name = folder_name;
        this.folder_code = folder_code;
        this.flashcard_length = flashcard_length;
    }
}
