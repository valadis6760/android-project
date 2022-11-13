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

    private String id;
    private Date date;
    private int steps;
    private String latitude;
    private String longitude;

    public DailyInfo() {
        this.id = UUID.randomUUID().toString();
    }

    public DailyInfo(Date date, int steps, String latitude, String longitude) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.steps = steps;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public DailyInfo(Date date, int steps) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.steps = steps;
    }

    public DailyInfo(String id, Date date, int steps, String latitude, String longitude) {
        this.id = id;
        this.date = date;
        this.steps = steps;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
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

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " TEXT PRIMARY KEY,"
            + COLUMN_DATE + " DATETIME2,"
            + COLUMN_STEPS + " INTEGER,"
            + COLUMN_LATITUDE + " TEXT,"
            + COLUMN_LONGITUDE + " TEXT"
            + ")";
}
