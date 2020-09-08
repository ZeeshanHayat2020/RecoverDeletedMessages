package com.example.recoverdeletedmessages.models;

public class TableSetter {

    private String TABLE_NAME = "notes";
    private String COLUMN_ID = "id";
    private String COLUMN_TITLE = "title";
    private String COLUMN_MESSAGE = "message";
    private String COLUMN_TIMESTAMP = "timestamp";


    public  TableSetter(){
    }

    public String getTABLE_NAME() {
        return TABLE_NAME;
    }

    public void setTABLE_NAME(String TABLE_NAME) {
        this.TABLE_NAME = TABLE_NAME;
    }

    public String getCOLUMN_ID() {
        return COLUMN_ID;
    }

    public void setCOLUMN_ID(String COLUMN_ID) {
        this.COLUMN_ID = COLUMN_ID;
    }

    public String getCOLUMN_TITLE() {
        return COLUMN_TITLE;
    }

    public void setCOLUMN_TITLE(String COLUMN_TITLE) {
        this.COLUMN_TITLE = COLUMN_TITLE;
    }

    public String getCOLUMN_MESSAGE() {
        return COLUMN_MESSAGE;
    }

    public void setCOLUMN_MESSAGE(String COLUMN_MESSAGE) {
        this.COLUMN_MESSAGE = COLUMN_MESSAGE;
    }

    public String getCOLUMN_TIMESTAMP() {
        return COLUMN_TIMESTAMP;
    }

    public void setCOLUMN_TIMESTAMP(String COLUMN_TIMESTAMP) {
        this.COLUMN_TIMESTAMP = COLUMN_TIMESTAMP;
    }
}
