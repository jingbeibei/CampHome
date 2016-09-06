package com.nuc.camphome.beans;

/**
 * Created by 景贝贝 on 2016/8/30.
 */
public class Conversation {
    private String LetterID;
    private String Title;
    private String Contents;
    private String Answer;
    private String EntryTime;
    private String AnswerTime;

    public String getLetterID() {
        return LetterID;
    }

    public void setLetterID(String letterID) {
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
        if (Answer == null) {
            Answer = "";
        }
        return Answer;
    }

    public void setAnswer(String answer) {
        if (answer.equals("null")) {
            Answer = "";
        } else {
            Answer = answer;
        }

    }

    public String getEntryTime() {
        return EntryTime.substring(0, 10);
    }

    public void setEntryTime(String entryTime) {
        EntryTime = entryTime;
    }

    public String getAnswerTime() {
        if (AnswerTime != null) {
            AnswerTime = AnswerTime.substring(0, 10);
        }
        return AnswerTime;
    }

    public void setAnswerTime(String answerTime) {
        if (answerTime.equals("null")) {
            AnswerTime = "";
        } else {
            AnswerTime = answerTime.substring(0, 10);
        }
    }
}
