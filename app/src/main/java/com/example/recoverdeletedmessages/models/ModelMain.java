package com.example.recoverdeletedmessages.models;

public class ModelMain {
    private int id;
    private String title;
    private String message;
    private String timeStamp;



    public ModelMain(){}
    public ModelMain(int id, String title, String message, String timeStamp) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
