package com.hdplayer.latestvideo.xvideoplayer.saxvideos.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdplayer.latestvideo.xvideoplayer.saxvideos.Adapter.FavouriteListAdapter;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.R;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.AppData;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.DBHandler;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.Logger;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.SampleData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavouriteListFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);
        AppData.contextFav = getActivity();

        recyclerView = root.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
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

        ArrayList<SampleData> videoLIst = new ArrayList<>();
        DBHandler DBHandler = new DBHandler(AppData.contextFav);

        Logger.Print(">> Favourite list >> " + DBHandler.getAllSampleDatas().size());
        if (DBHandler.getAllSampleDatas() != null && DBHandler.getAllSampleDatas().size() > 0) {

            for (int i = 0; i < DBHandler.getAllSampleDatas().size(); i++) {

                SampleData sampleData = new SampleData();
                sampleData.setKeyID(DBHandler.getAllSampleDatas().get(i).getKeyID());
                sampleData.setSongName(DBHandler.getAllSampleDatas().get(i).getSongName());
                sampleData.setSampleVideo(DBHandler.getAllSampleDatas().get(i).getSampleVideo());
                sampleData.setVideoTime(DBHandler.getAllSampleDatas().get(i).getVideoTime());
                sampleData.setVideoSize(DBHandler.getAllSampleDatas().get(i).getVideoSize());

                videoLIst.add(sampleData);

                Logger.Print(">> Favourite Fragment 11 >> " + sampleData.getSongName());
            }
        }

        if (videoLIst.size() > 0) {

            FavouriteListAdapter videoListAdapter = new FavouriteListAdapter(getContext(), videoLIst);
            recyclerView.setAdapter(videoListAdapter);
            videoListAdapter.notifyDataSetChanged();
        }

    }
}
