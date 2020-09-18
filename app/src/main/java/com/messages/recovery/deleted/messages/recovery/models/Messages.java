package com.messages.recovery.deleted.messages.recovery.models;

public class Messages {


    private String title;
    private String message;
    private long timeStamp;
    private String readStatus;

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


    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }
}
