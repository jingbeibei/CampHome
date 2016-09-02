package com.nuc.camphome.beans;

import java.io.Serializable;

/**
 * Created by 景贝贝 on 2016/9/2.
 */
public class Letter implements Serializable{
    private int LetterID;
    private String Title;
    private String Contents;
    private String Answer;
    private String EntryTime;
    private String AnswerTime;

    public int getLetterID() {
        return LetterID;
    }

    public void setLetterID(int letterID) {
        LetterID = letterID;
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

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public String getEntryTime() {
        return EntryTime;
    }

    public void setEntryTime(String entryTime) {
        EntryTime = entryTime;
    }

    public String getAnswerTime() {
        return AnswerTime;
    }

    public void setAnswerTime(String answerTime) {
        AnswerTime = answerTime;
    }
}
