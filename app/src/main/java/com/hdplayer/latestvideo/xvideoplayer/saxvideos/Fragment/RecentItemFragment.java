package com.hdplayer.latestvideo.xvideoplayer.saxvideos.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdplayer.latestvideo.xvideoplayer.saxvideos.Adapter.RecentItemListAdapter;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.R;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.AppData;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.DBHandler;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.Logger;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.SampleData;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecentItemFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecentItemListAdapter videoListAdapter;
    private DBHandler DBHandler;
    private ArrayList<SampleData> VideoLIst;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = root.findViewById(R.id.recyclerview);

        AppData.contextRecent = getActivity();

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        SetData();
    }

    private void SetData() {

        VideoLIst = new ArrayList<>();
        DBHandler = new DBHandler(AppData.contextRecent);

        if (DBHandler.getAllRecentDatas() != null && DBHandler.getAllRecentDatas().size() > 0) {

            for (int i = 0; i < DBHandler.getAllRecentDatas().size(); i++) {

                SampleData sampleData = new SampleData();
                sampleData.setKeyID(DBHandler.getAllRecentDatas().get(i).getKeyID());
                sampleData.setSongName(DBHandler.getAllRecentDatas().get(i).getSongName());
                sampleData.setSampleVideo(DBHandler.getAllRecentDatas().get(i).getSampleVideo());
                sampleData.setVideoTime(DBHandler.getAllRecentDatas().get(i).getVideoTime());
                sampleData.setVideoSize(DBHandler.getAllRecentDatas().get(i).getVideoSize());
                sampleData.setVideoDateModi(DBHandler.getAllRecentDatas().get(i).getVideoDateModi());
                sampleData.setVideoResolution(DBHandler.getAllRecentDatas().get(i).getVideoResolution());

                VideoLIst.add(sampleData);

                Logger.Print(">> Recent Fragment 11 >> " + sampleData);
            }
        }

        if (VideoLIst.size() > 0) {

            videoListAdapter = new RecentItemListAdapter(getContext(), VideoLIst, RecentItemFragment.this);
            recyclerView.setAdapter(videoListAdapter);
            videoListAdapter.notifyDataSetChanged();
        }

    }
}
