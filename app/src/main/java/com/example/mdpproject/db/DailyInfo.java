package com.example.mdpproject.db;

import java.util.Date;
import java.util.UUID;

public class DailyInfo {

    public static final String TABLE_NAME = "activity_info";
    public  static final String COLUMN_ID = "id";
    public  static final String COLUMN_DATE = "date";
    public static final String COLUMN_STEPS = "steps";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    private int id;
    private String date;
    private String steps;
    private String latitude;
    private String longitude;

    public DailyInfo() {

    }



    public DailyInfo( int id,String date, String steps, String latitude, String longitude) {
        this.id = id;
        this.date = date;
        this.steps = steps;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public  void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("
            +COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            +COLUMN_DATE+" DATETIME2,"
            +COLUMN_STEPS+" TEXT,"
            +COLUMN_LATITUDE+" TEXT,"
            +COLUMN_LONGITUDE+" TEXT"
            +")";
}
