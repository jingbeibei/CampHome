package com.nuc.camphome.beans;

import java.io.Serializable;

/**
 * Created by 景贝贝 on 2016/9/3.
 */
public class Suggest implements Serializable{
    private int ID;
    private String Contents;
    private String SubmitTime;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getContents() {
        return Contents;
    }

    public void setContents(String contents) {
        Contents = contents;
    }

    public String getSubmitTime() {
        return SubmitTime;
    }

    public void setSubmitTime(String submitTime) {
        SubmitTime = submitTime;
    }
}
