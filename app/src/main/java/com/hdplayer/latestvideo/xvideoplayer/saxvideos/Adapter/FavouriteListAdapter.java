package com.hdplayer.latestvideo.xvideoplayer.saxvideos.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.R;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.VideoPlayActivity;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.AppData;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.DBHandler;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.SampleData;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class FavouriteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SampleData> videoLIst;
    private DBHandler DBHandler;
    private Context context;

    public FavouriteListAdapter(Context context, ArrayList<SampleData> videoLIst) {

        this.context = context;
        this.videoLIst = videoLIst;
        DBHandler = new DBHandler(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.video_list_item, parent, false);
        viewHolder = new MyViewHolder(v);

        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;

        viewHolder.videoname.setText(videoLIst.get(position).getSongName());
        viewHolder.videotime.setText(videoLIst.get(position).getVideoTime());
        viewHolder.videosize.setText(videoLIst.get(position).getVideoSize() + " MB");

        Glide.with(AppData.contextFav).load(videoLIst.get(position).getSampleVideo()).centerCrop().into(viewHolder.videoimage);

        viewHolder.fav_icon.setOnClickListener(v -> {

            DBHandler.deleteSampleData(videoLIst.get(position).getKeyID());
            videoLIst.remove(position);
            notifyDataSetChanged();
        });

        viewHolder.constraintLayout.setOnClickListener(v -> PlayClick(position));
    }

    private void PlayClick(int position) {

        boolean isPresent = false;
        for (int i = 0; i < DBHandler.getAllRecentDatas().size(); i++) {
            if (DBHandler.getAllRecentDatas().get(i).getKeyID().equalsIgnoreCase(mSampleData(position).getKeyID())) {

                isPresent = true;
                break;
            }
        }

        if (!isPresent) {

            DBHandler.addRecentviewdata(mSampleData(position));
        }

        for (int i = 0; i < videoLIst.size(); i++) {

            HashMap<String, String> map = new HashMap<>();

            map.put(AppData.VideoPath, videoLIst.get(i).getSampleVideo());
            map.put(AppData.VideoReso, videoLIst.get(i).getVideoResolution());
            map.put(AppData.VideoDate, videoLIst.get(i).getVideoDateModi());
            map.put(AppData.VideoName, videoLIst.get(i).getSongName());
            map.put(AppData.VideoTime, videoLIst.get(i).getVideoTime());
            map.put(AppData.VideoSize, videoLIst.get(i).getVideoSize());
            map.put(AppData.VideoId, videoLIst.get(i).getKeyID());

            AppData.VideoListForContinuePlay.add(map);
        }

        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra("songLink", videoLIst.get(position).getSampleVideo());
        intent.putExtra("VideoPosition", position);
        context.startActivity(intent);
    }

    private SampleData mSampleData(int position) {

        SampleData sampleData = new SampleData();
        sampleData.setKeyID(videoLIst.get(position).getKeyID());
        sampleData.setSampleVideo(videoLIst.get(position).getSampleVideo());
        sampleData.setSongName(videoLIst.get(position).getSongName());
        sampleData.setVideoTime(videoLIst.get(position).getVideoTime());
        sampleData.setVideoSize(videoLIst.get(position).getVideoSize());

        return sampleData;
    }

    @Override
    public int getItemCount() {

        if (videoLIst.size() > 0) {
            return videoLIst.size();
        }
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView videoname, videotime, videosize;
        ImageView videoimage, menuitem, fav_icon;
        ConstraintLayout constraintLayout;

        MyViewHolder(@NonNull View root) {
            super(root);

            menuitem = root.findViewById(R.id.menuitem);
            videoname = root.findViewById(R.id.txt_video_name);
            videotime = root.findViewById(R.id.txt_video_time);
            videosize = root.findViewById(R.id.txt_video_size);
            videoimage = root.findViewById(R.id.image);
            fav_icon = root.findViewById(R.id.fav_icon);
            constraintLayout = root.findViewById(R.id.constraintLayout);

            fav_icon.setVisibility(View.VISIBLE);
            menuitem.setVisibility(View.GONE);
        }
    }
}
