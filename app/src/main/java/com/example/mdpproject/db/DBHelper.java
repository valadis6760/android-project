package com.example.mdpproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "activities_db";

   public  DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DailyInfo.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DailyInfo.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }


    public long insertContact(String date, String steps, String latitude, String longitude){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DailyInfo.COLUMN_DATE, date);
        values.put(DailyInfo.COLUMN_STEPS,steps);
        values.put(DailyInfo.COLUMN_LATITUDE,latitude);
        values.put(DailyInfo.COLUMN_LONGITUDE,longitude);
        long id = db.insert(DailyInfo.TABLE_NAME,null,values);
        db.close();
        return id;
    }

//    public DailyInfo getContact(long id){
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(DailyInfo.TABLE_NAME,
//                new String[]{
//                        DailyInfo.COLUMN_ID,
//                        DailyInfo.COLUMN_NAME,
//                        DailyInfo.COLUMN_EMAIL},
//                DailyInfo.CREATE_TABLE+"=?",
//                new String[]{
//                        String.valueOf(id)
//                },null,null,null,null);
//
//        if(cursor!= null)cursor.moveToFirst();
//
//        DailyInfo dailyInfo = new dailyInfo(
//                cursor.getString(cursor.getColumnIndexOrThrow(dailyInfo.COLUMN_NAME)),
//                cursor.getString(cursor.getColumnIndexOrThrow(dailyInfo.COLUMN_EMAIL)),
//                cursor.getInt(cursor.getColumnIndexOrThrow(dailyInfo.COLUMN_ID)));
//
//
//        cursor.close();
//        return dailyInfo;
//
//
//    }


    public ArrayList<DailyInfo> getAllRecords(){
        ArrayList<DailyInfo> dailyInfos = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DailyInfo.TABLE_NAME +" ORDER BY "+DailyInfo.COLUMN_ID+" DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()) {
            do {
                DailyInfo dailyInfo = new DailyInfo();
                dailyInfo.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DailyInfo.COLUMN_ID)));
                dailyInfo.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DailyInfo.COLUMN_DATE)));
                dailyInfo.setSteps(cursor.getString(cursor.getColumnIndexOrThrow(DailyInfo.COLUMN_STEPS)));
                dailyInfo.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow(DailyInfo.COLUMN_LATITUDE)));
                dailyInfo.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow(DailyInfo.COLUMN_LONGITUDE)));
                dailyInfos.add(dailyInfo);
            } while (cursor.moveToNext());
            db.close();
        }
        return dailyInfos;
    }

//    SELECT * FROM PERSONAL WHERE BIRTH_DATE_TIME BETWEEN
//'2000-01-01 00:00:00' AND '2002-09-18 12:00:00';

    public ArrayList<DailyInfo> getWeekRecords(){

        SimpleDateFormat yymmdd = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();

        System.out.println("Date = "+ cal.getTime());
        String CURRENT_DATE = yymmdd.format(cal.getTime());

        Log.d("DATE TIME", "CURRENT_DATE : "+CURRENT_DATE);
        cal.add(Calendar.DATE, -7);
        String PREV_DATE = yymmdd.format(cal.getTime());
        Log.d("DATE TIME", "PREV_DATE : "+PREV_DATE);


        ArrayList<DailyInfo> dailyInfos = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DailyInfo.TABLE_NAME +" WHERE "+DailyInfo.COLUMN_DATE+" BETWEEN '"+PREV_DATE+"' AND '"+ CURRENT_DATE+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()) {
            do {
                DailyInfo dailyInfo = new DailyInfo();
                dailyInfo.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DailyInfo.COLUMN_ID)));
                dailyInfo.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DailyInfo.COLUMN_DATE)));
                dailyInfo.setSteps(cursor.getString(cursor.getColumnIndexOrThrow(DailyInfo.COLUMN_STEPS)));
                dailyInfo.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow(DailyInfo.COLUMN_LATITUDE)));
                dailyInfo.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow(DailyInfo.COLUMN_LONGITUDE)));
                dailyInfos.add(dailyInfo);
            } while (cursor.moveToNext());
            db.close();
        }
        return dailyInfos;
    }
}
