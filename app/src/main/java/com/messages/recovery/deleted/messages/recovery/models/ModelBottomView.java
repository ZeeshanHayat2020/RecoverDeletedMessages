package com.messages.recovery.deleted.messages.recovery.models;

public class ModelBottomView {

    private int imageId;
    private String title;

    public ModelBottomView(int imageId, String title) {
        this.imageId = imageId;
        this.title = title;
    }


    public int getImageId() {
        return imageId;
    }

    public String getTitle() {
        return title;
    }
}
