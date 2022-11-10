package com.example.mdpproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TABLE_DAILY_INFO = "daily_info";

    DateFormat dt = new SimpleDateFormat("yyyy/mm/dd");

    public DBHelper(Context context) {
        super(context, "mdp", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_STEPS_TABLE = "create table " + TABLE_DAILY_INFO + "(id text primary key, date date, steps integer, latitude text, longitude text)";
        sqLiteDatabase.execSQL(CREATE_STEPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_DAILY_INFO);
        onCreate(sqLiteDatabase);
    }

    public void addDailyInfo(DailyInfo dailyInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", dailyInfo.getId());
        values.put("date", dt.format(dailyInfo.getDate()));
        values.put("steps", dailyInfo.getSteps());
        values.put("latitude", dailyInfo.getLatitude());
        values.put("longitude", dailyInfo.getLongitude());

        db.insert(TABLE_DAILY_INFO, null, values);
        db.close();
    }

    public DailyInfo getDailyInfoByDate(Date date) throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DAILY_INFO, new String[] { "id", "date", "steps", "latitude", "longitude" }, "date=?",
                new String[] { dt.format(date) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        DailyInfo dailyInfo = new DailyInfo(
                cursor.getString(0),
                dt.parse(cursor.getString(1)),
                Integer.parseInt(cursor.getString(2)),
                cursor.getString(4),
                cursor.getString(5)
        );
        return dailyInfo;
    }

    public List<DailyInfo> getDailyInfoByDateRange(Date fromDate, Date toDate) throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DAILY_INFO, new String[] { "id", "date", "steps", "latitude", "longitude" }, "date between ? and ?",
                new String[] { dt.format(fromDate), dt.format(toDate) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        List<DailyInfo> dailyInfoList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                DailyInfo dailyInfo = new DailyInfo();
                dailyInfo.setId(cursor.getString(0));
                dailyInfo.setDate(dt.parse(cursor.getString(1)));
                dailyInfo.setSteps(Integer.parseInt(cursor.getString(2)));
                dailyInfo.setId(cursor.getString(3));
                dailyInfo.setId(cursor.getString(4));
                dailyInfoList.add(dailyInfo);
            } while (cursor.moveToNext());
        }

        return dailyInfoList;
    }
}
