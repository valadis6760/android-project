package com.example.mdpproject.db;

import java.util.Date;
import java.util.UUID;

public class DailyInfo {

    public static final String TABLE_NAME = "activity_info";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_STEPS = "steps";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_GOAL_REACHED = "goal_reached";

    private String id;
    private Date date;
    private int steps;
    private String latitude;
    private String longitude;
    private boolean goalReached;

    public DailyInfo() {
        this.id = UUID.randomUUID().toString();
    }

    public DailyInfo(Date date, int steps, String latitude, String longitude, boolean goalReached) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.steps = steps;
        this.latitude = latitude;
        this.longitude = longitude;
        this.goalReached = goalReached;
    }

    public DailyInfo(Date date, int steps) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.steps = steps;
    }

    public DailyInfo(String id, Date date, int steps, String latitude, String longitude, boolean goal_reached) {
        this.id = id;
        this.date = date;
        this.steps = steps;
        this.latitude = latitude;
        this.longitude = longitude;
        this.goalReached = goal_reached;
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

    public boolean isGoalReached() {
        return goalReached;
    }

    public void setGoalReached(boolean goalReached) {
        this.goalReached = goalReached;
    }

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " TEXT PRIMARY KEY,"
            + COLUMN_DATE + " DATETIME2,"
            + COLUMN_STEPS + " INTEGER,"
            + COLUMN_LATITUDE + " TEXT,"
            + COLUMN_LONGITUDE + " TEXT,"
            + COLUMN_GOAL_REACHED + " INTEGER"
            + ")";
}
