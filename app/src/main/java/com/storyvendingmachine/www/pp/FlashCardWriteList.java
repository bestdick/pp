package com.storyvendingmachine.www.pp;

/**
 * Created by Administrator on 2018-12-18.
 */

public class FlashCardWriteList {
    String term;
    String def;



    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public FlashCardWriteList(String term, String def) {
        this.term = term;
        this.def = def;
    }
}
