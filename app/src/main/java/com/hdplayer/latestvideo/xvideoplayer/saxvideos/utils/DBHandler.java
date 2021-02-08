package com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SampleDatasManager";
    private static final String TABLE_SampleDataS = "FavouriteVideoData";
    private static final String ID = "id";
    private static final String KEY_SAMPLEVIDEO = "samplevideo";
    private static final String KEY_VIDEOTIME = "videotime";
    private static final String KEY_VIDEOSIZE = "videosize";
    private static final String KEY_SONGNAME = "songname";
    private static final String KEY_ID = "videoId";
    private static final String KEY_RESO = "videoReso";
    private static final String KEY_DATEMODI = "videoDateModi";

    private static final String TABLE_RecentDatas = "RecentVideoData";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_SampleDataS_TABLE = "CREATE TABLE " + TABLE_SampleDataS + "(" + ID + " INTEGER PRIMARY KEY," + KEY_SAMPLEVIDEO + " TEXT," + KEY_SONGNAME + " TEXT," +
                KEY_VIDEOTIME + " TEXT," + KEY_VIDEOSIZE + " TEXT," + KEY_RESO + " TEXT," + KEY_DATEMODI + " TEXT," + KEY_ID + " INTEGER" + ")";

        String CREATE_RecentDataS_TABLE = "CREATE TABLE " + TABLE_RecentDatas + "(" + ID + " INTEGER PRIMARY KEY," + KEY_SAMPLEVIDEO + " TEXT," + KEY_SONGNAME + " TEXT," +
                KEY_VIDEOTIME + " TEXT," + KEY_VIDEOSIZE + " TEXT," + KEY_RESO + " TEXT," + KEY_DATEMODI + " TEXT," + KEY_ID + " INTEGER" + ")";

        db.execSQL(CREATE_SampleDataS_TABLE);
        db.execSQL(CREATE_RecentDataS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SampleDataS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RecentDatas);

        // Create tables again
        onCreate(db);
    }

    // code to add the new SampleData
    public void addSampleData(SampleData SampleData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, SampleData.getKeyID());
        values.put(KEY_SAMPLEVIDEO, SampleData.getSampleVideo());
        values.put(KEY_SONGNAME, SampleData.getSongName());
        values.put(KEY_VIDEOTIME, SampleData.getVideoTime());
        values.put(KEY_VIDEOSIZE, SampleData.getVideoSize());
        values.put(KEY_RESO, SampleData.getVideoResolution());
        values.put(KEY_DATEMODI, SampleData.getVideoDateModi());


        // Inserting Row
        db.insert(TABLE_SampleDataS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public void addRecentviewdata(SampleData SampleData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, SampleData.getKeyID());
        values.put(KEY_SAMPLEVIDEO, SampleData.getSampleVideo());
        values.put(KEY_SONGNAME, SampleData.getSongName());
        values.put(KEY_VIDEOTIME, SampleData.getVideoTime());
        values.put(KEY_VIDEOSIZE, SampleData.getVideoSize());
        values.put(KEY_RESO, SampleData.getVideoResolution());
        values.put(KEY_DATEMODI, SampleData.getVideoDateModi());


        // Inserting Row
        db.insert(TABLE_RecentDatas, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single SampleData
    SampleData getSampleData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_SampleDataS, new String[]{ID, KEY_SAMPLEVIDEO, KEY_ID, KEY_SONGNAME, KEY_VIDEOTIME, KEY_VIDEOSIZE}, ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        SampleData SampleData = null;
        if (cursor != null) {
            cursor.moveToFirst();
            SampleData = new SampleData(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2));
        }
        // return SampleData
        return SampleData;
    }

    // code to get all SampleDatas in a list view
    public List<SampleData> getAllSampleDatas() {
        List<SampleData> SampleDataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SampleDataS;


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        try {
            if (cursor.moveToFirst()) {
                do {
                    SampleData sampleData = new SampleData();
                    sampleData.setID(Integer.parseInt(cursor.getString(0)));
                    sampleData.setSampleVideo(cursor.getString(1));
                    sampleData.setSongName(cursor.getString(2));
                    sampleData.setVideoTime(cursor.getString(3));
                    sampleData.setVideoSize(cursor.getString(4));
                    sampleData.setVideoResolution(cursor.getString(5));
                    sampleData.setVideoDateModi(cursor.getString(6));
                    sampleData.setKeyID(cursor.getString(7));
                    // Adding SampleData to list
                    SampleDataList.add(sampleData);

                    Logger.Print(">>> Sampledata >>> " + SampleDataList);
                } while (cursor.moveToNext());
            }
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }


        // return SampleData list
        return SampleDataList;
    }

    public List<SampleData> getAllRecentDatas() {
        List<SampleData> SampleDataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RecentDatas;


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        try {
            if (cursor.moveToFirst()) {
                do {
                    SampleData sampleData = new SampleData();
                    sampleData.setID(Integer.parseInt(cursor.getString(0)));
                    sampleData.setSampleVideo(cursor.getString(1));
                    sampleData.setSongName(cursor.getString(2));
                    sampleData.setVideoTime(cursor.getString(3));
                    sampleData.setVideoSize(cursor.getString(4));
                    sampleData.setVideoResolution(cursor.getString(5));
                    sampleData.setVideoDateModi(cursor.getString(6));
                    sampleData.setKeyID(cursor.getString(7));
                    // Adding SampleData to list
                    SampleDataList.add(sampleData);
                } while (cursor.moveToNext());
            }
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        // return SampleData list
        return SampleDataList;
    }

    // code to update the single SampleData
    public int updateSampleData(SampleData SampleData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, SampleData.getKeyID());
        values.put(KEY_SAMPLEVIDEO, SampleData.getSampleVideo());
        values.put(KEY_SONGNAME, SampleData.getSongName());
        values.put(KEY_VIDEOTIME, SampleData.getVideoTime());
        values.put(KEY_VIDEOSIZE, SampleData.getVideoSize());

        // updating row
        return db.update(TABLE_SampleDataS, values, KEY_ID + " = ?",
                new String[]{SampleData.getKeyID()});
    }

    // Deleting single SampleData
    public void deleteSampleData(String key) {

        Logger.Print(">> Keuy >> " + key);
        Logger.Print(">> Keuy 11 >> " + getAllSampleDatas());
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SampleDataS, KEY_ID + " = ?", new String[]{key});
        db.close();
    }

    public void deleteRecentData(String key) {

        Logger.Print(">> KeuyR >> " + key);
        Logger.Print(">> KeuyR 11 >> " + getAllSampleDatas());

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RecentDatas, KEY_ID + " = ?", new String[]{key});
        db.close();
    }


    // Getting SampleDatas Count
    public int getSampleDatasCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SampleDataS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
