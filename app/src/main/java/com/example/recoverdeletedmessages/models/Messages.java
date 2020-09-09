package com.example.recoverdeletedmessages.models;

public class Messages {


    private String title;
    private String message;
    private long timeStamp;

    public Messages() {
    }

    public Messages(String title, String message, long timeStamp) {
        this.title = title;
        this.message = message;
        this.timeStamp = timeStamp;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
