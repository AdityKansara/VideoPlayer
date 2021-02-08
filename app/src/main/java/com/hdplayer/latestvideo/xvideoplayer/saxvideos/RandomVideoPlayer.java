package com.hdplayer.latestvideo.xvideoplayer.saxvideos;

import android.os.Bundle;

import com.hdplayer.latestvideo.xvideoplayer.saxvideos.Adapter.GirlRoomMethods;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.Adapter.GirlRoomAdapter;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.Adapter.GirlRoomModel;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pub.devrel.easypermissions.EasyPermissions;

public class RandomVideoPlayer extends AppCompatActivity {

    RecyclerView mRecycler;
    GirlRoomAdapter adapter;

    ArrayList<GirlRoomModel> girlRoomModelArrayList;
    String[] PERMISSIONS = {"android.permission.CAMERA",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_video_player);
        mRecycler = findViewById(R.id.mainRecycler);

        EasyPermissions.requestPermissions(this,
                "For The Best Random Video Call Experience, Please Allow Permission",
                123, PERMISSIONS);

        girlRoomModelArrayList = new GirlRoomMethods().GetRoomDetails(this);
        adapter = new GirlRoomAdapter(this, girlRoomModelArrayList);
        mRecycler.setLayoutManager(new GridLayoutManager(this, 1));
        mRecycler.setAdapter(adapter);
    }
}
