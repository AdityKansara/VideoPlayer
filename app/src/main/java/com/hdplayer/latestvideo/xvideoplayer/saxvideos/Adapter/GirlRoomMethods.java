package com.hdplayer.latestvideo.xvideoplayer.saxvideos.Adapter;

import android.content.Context;

import com.hdplayer.latestvideo.xvideoplayer.saxvideos.R;

import java.util.ArrayList;

public class GirlRoomMethods {
    public ArrayList<GirlRoomModel> GetRoomDetails(Context context) {
        ArrayList<GirlRoomModel> arrayList = new ArrayList<>();


        for (int x = 0; x < 120; x++) {
            GirlRoomModel room = new GirlRoomModel();
            room.setRoomImg(R.drawable.room1);
            arrayList.add(room);
        }

        for (int x = 0; x < 120; x++) {

            arrayList.get(x).setRoomNo("Room no.");
            arrayList.get(x).setRoomType("Girls are waiting...");
            arrayList.get(x).setRoomCount("1234");

        }

        return arrayList;
    }
}
