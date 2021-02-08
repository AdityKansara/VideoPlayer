package com.hdplayer.latestvideo.xvideoplayer.saxvideos.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.VideoFolderActivity;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.Fragment.FolderListFragment;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.R;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.AppData;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.ImageData;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FolderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private HashMap<String, ArrayList<ImageData>> allAlbum;
    private ArrayList<String> allFolder;
    private FolderListFragment folderListFragment;

    public FolderListAdapter(HashMap<String, ArrayList<ImageData>> allAlbum, ArrayList<String> allFolder, FolderListFragment folderListFragment) {
        this.allAlbum = allAlbum;
        this.allFolder = allFolder;
        this.folderListFragment = folderListFragment;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.folder_list_item, parent, false);
        viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder viewHolder = (MyViewHolder) holder;

        ImageData imageDatas = getImageByAlbum(allFolder.get(position)).get(0);

        String[] split = imageDatas.imagePath.split(imageDatas.folderName);

        Logger.Print("Split >> " + Arrays.toString(split));

        viewHolder.videoname.setText(imageDatas.folderName);
        viewHolder.videosize.setText(split[0] + imageDatas.folderName);
        viewHolder.videotime.setText(getImageByAlbum(allFolder.get(position)).size() + " Video");

        Glide.with(folderListFragment).load(imageDatas.imagePath).centerCrop().into(viewHolder.videoimage);

        viewHolder.back.setOnClickListener(v -> {

            Intent intent = new Intent(AppData.contextFolder, VideoFolderActivity.class);
            intent.putExtra("data", getImageByAlbum(allFolder.get(position)));
            AppData.contextFolder.startActivity(intent);
        });
    }

    private ArrayList<ImageData> getImageByAlbum(String folderId) {
        ArrayList<ImageData> imageDatas = allAlbum.get(folderId);
        if (imageDatas == null) {
            return new ArrayList<>();
        }
        return imageDatas;
    }

    @Override
    public int getItemCount() {

        if (allFolder.size() > 0) {
            return allFolder.size();
        }
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView videoname, videotime, videosize;
        ImageView videoimage, menuitem, back;

        MyViewHolder(@NonNull View root) {
            super(root);

            menuitem = root.findViewById(R.id.menuitem);
            videoname = root.findViewById(R.id.txt_video_name);
            videotime = root.findViewById(R.id.txt_video_time);
            videosize = root.findViewById(R.id.txt_video_size);
            videoimage = root.findViewById(R.id.image);
            back = root.findViewById(R.id.back);

            menuitem.setVisibility(View.GONE);
        }
    }

}
