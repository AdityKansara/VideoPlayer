package com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;

public class AppData {

    @SuppressLint("StaticFieldLeak")
    public static Context contextRecent;
    public static String VideoPath = "videopath";
    public static String VideoReso = "videoreso";
    public static String VideoDate = "videodate";
    public static String VideoName = "videoname";
    public static String VideoTime = "videotime";
    public static String VideoSize = "videosize";
    public static String VideoId = "videoid";
    @SuppressLint("StaticFieldLeak")
    public static Context contextFav;
    @SuppressLint("StaticFieldLeak")
    public static Context contextFolder;
    public static ArrayList<HashMap<String, String>> VideoListForContinuePlay = new ArrayList<>();
    private static int width;

    public static int getWidth(int i) {
        return (width * i) / 1280;
    }

    static void getSize(Context context) {
        int hieght = context.getResources().getDisplayMetrics().heightPixels;
        width = context.getResources().getDisplayMetrics().widthPixels;
        if (hieght > width) {
            width = context.getResources().getDisplayMetrics().heightPixels;
            hieght = context.getResources().getDisplayMetrics().widthPixels;
        }
        hieght = (int) (((double) width) * 0.5625d);
    }

    public static void setViewMargins(@NonNull View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            mlp.setMargins(left, top, right, bottom);
        }
    }
}
