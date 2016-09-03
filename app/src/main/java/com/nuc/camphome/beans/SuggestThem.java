package com.nuc.camphome.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 景贝贝 on 2016/9/3.
 */
public class SuggestThem implements Serializable{
    private int ID;
    private String Title;
    private String Contents;
    private String PublishTime;
    private List<Suggest> Suggests;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContents() {
        return Contents;
    }

    public void setContents(String contents) {
        Contents = contents;
    }

    public String getPublishTime() {
        return PublishTime;
    }

    public void setPublishTime(String publishTime) {
        PublishTime = publishTime;
    }

    public List<Suggest> getSuggests() {
        return Suggests;
    }

    public void setSuggests(List<Suggest> suggests) {
        this.Suggests = suggests;
    }
}
