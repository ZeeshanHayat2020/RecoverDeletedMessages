package com.messages.recovery.deleted.messages.recovery.models;

public class Users {

    private long id;
    private String userTitle;
    private String largeIconUri;


    public Users() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getLargeIconUri() {
        return largeIconUri;
    }

    public void setLargeIconUri(String largeIconUri) {
        this.largeIconUri = largeIconUri;
    }

}
