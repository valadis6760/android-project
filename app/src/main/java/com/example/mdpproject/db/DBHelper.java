package com.example.mdpproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "activities_db";
    DateFormat dt = new SimpleDateFormat("yyyy/MM/dd");

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DailyInfo.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DailyInfo.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public List<DailyInfo> getAllRecords() throws ParseException {
        ArrayList<DailyInfo> dailyInfoList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DailyInfo.TABLE_NAME + " ORDER BY " + DailyInfo.COLUMN_DATE + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DailyInfo dailyInfo = new DailyInfo();
                dailyInfo.setId(cursor.getString(cursor.getColumnIndexOrThrow(DailyInfo.COLUMN_ID)));
                dailyInfo.setDate(dt.parse(cursor.getString(cursor.getColumnIndexOrThrow(DailyInfo.COLUMN_DATE))));
                dailyInfo.setSteps(cursor.getInt(cursor.getColumnIndexOrThrow(DailyInfo.COLUMN_STEPS)));
                dailyInfo.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow(DailyInfo.COLUMN_LATITUDE)));
                dailyInfo.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow(DailyInfo.COLUMN_LONGITUDE)));
                dailyInfoList.add(dailyInfo);
            } while (cursor.moveToNext());
            db.close();
        }
        return dailyInfoList;
    }

    public void addDailyInfo(DailyInfo dailyInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DailyInfo.COLUMN_ID, dailyInfo.getId());
        values.put(DailyInfo.COLUMN_DATE, dt.format(dailyInfo.getDate()));
        values.put(DailyInfo.COLUMN_STEPS, dailyInfo.getSteps());
        values.put(DailyInfo.COLUMN_LATITUDE, dailyInfo.getLatitude());
        values.put(DailyInfo.COLUMN_LONGITUDE, dailyInfo.getLongitude());
        values.put(DailyInfo.COLUMN_GOAL_REACHED, dailyInfo.isGoalReached() ? 1 : 0);

        db.insert(DailyInfo.TABLE_NAME, null, values);
        db.close();
    }

    public DailyInfo getDailyInfoByDate(Date date) throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DailyInfo.TABLE_NAME, new String[]{
                DailyInfo.COLUMN_ID,
                DailyInfo.COLUMN_DATE,
                DailyInfo.COLUMN_STEPS,
                DailyInfo.COLUMN_LATITUDE,
                DailyInfo.COLUMN_LONGITUDE,
                DailyInfo.COLUMN_GOAL_REACHED}, "date=?",
                new String[]{dt.format(date)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        DailyInfo dailyInfo = new DailyInfo(
                cursor.getString(0),
                dt.parse(cursor.getString(1)),
                Integer.parseInt(cursor.getString(2)),
                cursor.getString(3),
                cursor.getString(4),
                1 >= cursor.getInt(5)
        );
        return dailyInfo;
    }

    public List<DailyInfo> getDailyInfoByDateRange(Date fromDate, Date toDate) throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DailyInfo.TABLE_NAME, new String[]{
                        DailyInfo.COLUMN_ID,
                        DailyInfo.COLUMN_DATE,
                        DailyInfo.COLUMN_STEPS,
                        DailyInfo.COLUMN_LATITUDE,
                        DailyInfo.COLUMN_LONGITUDE,
                        DailyInfo.COLUMN_GOAL_REACHED}, "date between ? and ?",
                new String[]{dt.format(fromDate), dt.format(toDate)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        List<DailyInfo> dailyInfoList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                DailyInfo dailyInfo = new DailyInfo();
                dailyInfo.setId(cursor.getString(0));
                dailyInfo.setDate(dt.parse(cursor.getString(1)));
                dailyInfo.setSteps(Integer.parseInt(cursor.getString(2)));
                dailyInfo.setLatitude(cursor.getString(3));
                dailyInfo.setLongitude(cursor.getString(4));
                dailyInfo.setGoalReached(1 >= cursor.getInt(5));
                dailyInfoList.add(dailyInfo);
            } while (cursor.moveToNext());
        }

        return dailyInfoList;
    }

    public void updateDailyInfo(DailyInfo dailyInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DailyInfo.COLUMN_DATE, dt.format(dailyInfo.getDate()));
        values.put(DailyInfo.COLUMN_STEPS, dailyInfo.getSteps());
        values.put(DailyInfo.COLUMN_LATITUDE, dailyInfo.getLatitude());
        values.put(DailyInfo.COLUMN_LONGITUDE, dailyInfo.getLongitude());
        values.put(DailyInfo.COLUMN_GOAL_REACHED, dailyInfo.isGoalReached() ? 1 : 0);

        db.update(DailyInfo.TABLE_NAME, values, "id = ?" , new String[] { dailyInfo.getId() });
        db.close();
    }
}
