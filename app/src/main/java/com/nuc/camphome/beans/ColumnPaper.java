package com.nuc.camphome.beans;

/**
 * Created by 景贝贝 on 2016/9/5.
 */
public class ColumnPaper {
    private int ColumnPaperID;
    private String Title;
    private int ColumnID;
    private String PublishTime;
    private int Hits;

    public int getColumnPaperID() {
        return ColumnPaperID;
    }

    public void setColumnPaperID(int columnPaperID) {
        ColumnPaperID = columnPaperID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getColumnID() {
        return ColumnID;
    }

    public void setColumnID(int columnID) {
        ColumnID = columnID;
    }

    public String getPublishTime() {
        return PublishTime;
    }

    public void setPublishTime(String publishTime) {
        PublishTime = publishTime;
    }


    public int getHits() {
        return Hits;
    }

    public void setHits(int hits) {
        Hits = hits;
    }
}
