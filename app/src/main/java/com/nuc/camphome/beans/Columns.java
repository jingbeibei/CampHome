package com.nuc.camphome.beans;

/**
 * Created by 景贝贝 on 2016/9/5.
 */
public class Columns {
    private int ColumnID;
    private String Title;
    private String Remarks;
  //  private String LastUpdateTime;//最后更新时间
    private String ImageUrl;
    private int Hits;
    private int PaperCount;

    public int getPaperCount() {
        return PaperCount;
    }

    public void setPaperCount(int paperCount) {
        PaperCount = paperCount;
    }

    public int getColumnID() {
        return ColumnID;
    }

    public void setColumnID(int columnID) {
        ColumnID = columnID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public int getHits() {
        return Hits;
    }

    public void setHits(int hits) {
        Hits = hits;
    }
}
