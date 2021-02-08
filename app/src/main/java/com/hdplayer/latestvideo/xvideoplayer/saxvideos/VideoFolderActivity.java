package com.hdplayer.latestvideo.xvideoplayer.saxvideos;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.AppData;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.DBHandler;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.ImageData;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.Logger;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.PreferenceManager;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.SampleData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VideoFolderActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    TextView title;
    ArrayList<ImageData> list = new ArrayList<>();
    VideoListAdapter videoListAdapter;
    AdView adviewBanner;
    AdManager adManager;
    InterstitialAd interstitialAd;
    private DBHandler DBHandler;
    private Dialog deleteDialog;
    private int Count = 0;
    private Dialog detaliDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_folder);

        list = (ArrayList<ImageData>) getIntent().getSerializableExtra("data");
        DBHandler = new DBHandler(this);

        Logger.Print(">> list >> " + list);
        recyclerview = findViewById(R.id.recyclerview);
        title = findViewById(R.id.txt_title);
        adviewBanner = findViewById(R.id.adviewBanner);

        interstitialAd = new InterstitialAd(this);
        adManager = new AdManager(this, adviewBanner, interstitialAd);

        adManager.requestBannerAds();
        adManager.fullScreenAd();

        title.setText(list.get(0).folderName);
        PreferenceManager.ShowAds();
        GridLayoutManager layoutManager = new GridLayoutManager(VideoFolderActivity.this, 2);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setLayoutManager(layoutManager);

        if (list != null && list.size() > 0) {
            videoListAdapter = new VideoListAdapter();
            recyclerview.setAdapter(videoListAdapter);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (list != null && list.size() > 0) {
            list.clear();
        }
        if (AppData.VideoListForContinuePlay != null && AppData.VideoListForContinuePlay.size() > 0) {
            AppData.VideoListForContinuePlay.clear();
        }
    }

    public void OnbackClick(View view) {
        if (list != null && list.size() > 0) {
            list.clear();
        }
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void ShowDetailDailogue(int position) {

        if (detaliDialog != null && detaliDialog.isShowing()) {
            detaliDialog.dismiss();
            detaliDialog = null;
        }

        detaliDialog = new Dialog(VideoFolderActivity.this, R.style.Transparent);
        detaliDialog.setContentView(R.layout.details_dialog);
        detaliDialog.setCancelable(true);

        TextView name = detaliDialog.findViewById(R.id.videoname);
        name.setText("Name: " + mSampleData(position).getSongName());

        TextView path = detaliDialog.findViewById(R.id.videopath);
        path.setText("Path: " + mSampleData(position).getSampleVideo());

        TextView time = detaliDialog.findViewById(R.id.videotime);
        time.setText("Time: " + mSampleData(position).getVideoTime());

        TextView size = detaliDialog.findViewById(R.id.videosize);
        size.setText("Size: " + mSampleData(position).getVideoSize() + "MB");

        TextView date = detaliDialog.findViewById(R.id.videodatemodified);
        date.setText("Date Modified : " + mSampleData(position).getVideoDateModi());

        TextView reso = detaliDialog.findViewById(R.id.videoresolution);
        reso.setText("Resolution: " + mSampleData(position).getVideoResolution());

        detaliDialog.findViewById(R.id.img_ok).setOnClickListener(v -> detaliDialog.dismiss());

        if (!isFinishing()) {

            detaliDialog.show();
        }
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

        for (int i = 0; i < list.size(); i++) {

            HashMap<String, String> map = new HashMap<>();

            map.put(AppData.VideoPath, list.get(i).imagePath);
            map.put(AppData.VideoReso, list.get(i).videoResolution);
            map.put(AppData.VideoDate, list.get(i).videodateModified);
            map.put(AppData.VideoName, list.get(i).videoTitle);
            map.put(AppData.VideoTime, list.get(i).videoTime);
            map.put(AppData.VideoSize, list.get(i).videoSize);
            map.put(AppData.VideoId, list.get(i).videokeyid);

            AppData.VideoListForContinuePlay.add(map);
        }

        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        }

        Intent intent = new Intent(VideoFolderActivity.this, VideoPlayActivity.class);
        intent.putExtra("songLink", list.get(position).imagePath);
        intent.putExtra("VideoPosition", position);
        startActivity(intent);
    }

    private void ShowPopUpmenu(View v, int position) {

        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.home, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {

                case R.id.addtofavourite:

                    if (PreferenceManager.isNetworkConnected(this)) {
                        showAdsWithCount();
                    }

                    boolean isPresent = false;
                    for (int i = 0; i < DBHandler.getAllSampleDatas().size(); i++) {
                        if (DBHandler.getAllSampleDatas().get(i).getKeyID().equalsIgnoreCase(mSampleData(position).getKeyID())) {

                            isPresent = true;
                            break;
                        }
                    }

                    if (isPresent) {

                        Toast.makeText(VideoFolderActivity.this, "Already in favourite..!!", Toast.LENGTH_LONG).show();

                    } else {

                        DBHandler.addSampleData(mSampleData(position));
                        Toast.makeText(VideoFolderActivity.this, "Added in favourite..!!", Toast.LENGTH_LONG).show();
                    }
                    break;

                case R.id.play:
                    PlayClick(position);
                    break;

                case R.id.details:

                    if (PreferenceManager.isNetworkConnected(this)) {
                        showAdsWithCount();
                    }
                    ShowDetailDailogue(position);
                    break;

                case R.id.delete:
                    ShowDeteteDailogue(position);
                    break;

                case R.id.share:

                    if (PreferenceManager.isNetworkConnected(this)) {
                        showAdsWithCount();
                    }

                    Uri photoURI;
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {

                        photoURI = Uri.fromFile(new File(Objects.requireNonNull(list.get(position).imagePath)));
                    } else {

                        photoURI = FileProvider.getUriForFile(VideoFolderActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(Objects.requireNonNull(list.get(position).imagePath)));
                    }

                    Intent videoshare = new Intent(Intent.ACTION_SEND);
                    videoshare.setType("*/*");
                    videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    videoshare.putExtra(Intent.EXTRA_STREAM, photoURI);

                    try {
                        startActivity(videoshare);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
            return false;
        });
    }


    private void ShowDeteteDailogue(int position) {

        if (deleteDialog != null && deleteDialog.isShowing()) {
            deleteDialog.dismiss();
            deleteDialog = null;
        }

        deleteDialog = new Dialog(VideoFolderActivity.this, R.style.Transparent);
        deleteDialog.setContentView(R.layout.delete_dialog);
        deleteDialog.setCancelable(true);

        TextView name = deleteDialog.findViewById(R.id.txt_videoname);
        name.setText(mSampleData(position).getSongName());

        deleteDialog.findViewById(R.id.okbtn).setOnClickListener(v -> {

            File fdelete = new File(Objects.requireNonNull(list.get(position).imagePath));

            if (fdelete.exists()) {
                if (fdelete.delete()) {

                    boolean isPresent = false;
                    for (int i = 0; i < DBHandler.getAllRecentDatas().size(); i++) {
                        if (DBHandler.getAllRecentDatas().get(i).getKeyID().equalsIgnoreCase(list.get(position).videokeyid)) {

                            isPresent = true;
                            break;
                        }
                    }

                    if (!isPresent) {

                        DBHandler.deleteRecentData(list.get(position).videokeyid);
                    }

                    boolean isPresent1 = false;
                    for (int i = 0; i < DBHandler.getAllSampleDatas().size(); i++) {
                        if (DBHandler.getAllSampleDatas().get(i).getKeyID().equalsIgnoreCase(list.get(position).videokeyid)) {

                            isPresent1 = true;
                            break;
                        }
                    }

                    if (!isPresent1) {

                        DBHandler.deleteSampleData(list.get(position).videokeyid);
                    }

                    list.remove(position);
                    videoListAdapter.notifyDataSetChanged();

                    Logger.Print(">>> Data 33 >> " + list);
                }
            }
            deleteDialog.dismiss();
        });

        deleteDialog.findViewById(R.id.cancel).setOnClickListener(v -> deleteDialog.dismiss());

        if (!isFinishing()) {
            deleteDialog.show();
        }
    }

    private SampleData mSampleData(int position) {

        SampleData sampleData = new SampleData();
        sampleData.setKeyID(list.get(position).videokeyid);
        sampleData.setSampleVideo(list.get(position).imagePath);
        sampleData.setSongName(list.get(position).videoTitle);
        sampleData.setVideoTime(list.get(position).videoTime);
        sampleData.setVideoSize(list.get(position).videoSize);
        sampleData.setVideoResolution(list.get(position).videoResolution);
        sampleData.setVideoDateModi(list.get(position).videodateModified);

        return sampleData;
    }

    private void showAdsWithCount() {

        if (Count == 2) {
            PreferenceManager.ShowAds();
            Count = 0;
        } else {
            Count = Count + 1;
        }
    }

    private class VideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder holder;
            LayoutInflater inflater = LayoutInflater.from(VideoFolderActivity.this);

            View view = inflater.inflate(R.layout.videolist_grid_item, parent, false);
            holder = new MyViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            MyViewHolder viewHolder = (MyViewHolder) holder;

            viewHolder.txt_videoname.setText(list.get(position).videoTitle);
            viewHolder.txt_videotime.setText(list.get(position).videoTime);
            viewHolder.txt_videosize.setText(list.get(position).videoSize);

            Glide.with(VideoFolderActivity.this).load(list.get(position).imagePath).centerCrop().into(viewHolder.imgpick);

            viewHolder.img_more.setOnClickListener(v -> ShowPopUpmenu(v, position));
            viewHolder.constraintLayout.setOnClickListener(v -> PlayClick(position));
        }

        @Override
        public int getItemCount() {

            if (list != null && list.size() > 0) {
                return list.size();
            }
            return 0;
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView imgpick, img_more;
            TextView txt_videoname, txt_videotime, txt_videosize;
            ConstraintLayout constraintLayout;

            MyViewHolder(@NonNull View itemView) {
                super(itemView);

                imgpick = itemView.findViewById(R.id.imgpick);
                img_more = itemView.findViewById(R.id.img_more);
                txt_videoname = itemView.findViewById(R.id.txt_videoname);
                txt_videotime = itemView.findViewById(R.id.txt_videotime);
                txt_videosize = itemView.findViewById(R.id.txt_videosize);
                constraintLayout = itemView.findViewById(R.id.constraintLayout);

            }
        }
    }
}
