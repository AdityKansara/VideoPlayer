package com.hdplayer.latestvideo.xvideoplayer.saxvideos.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.BuildConfig;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.Fragment.RecentItemFragment;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.R;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.VideoPlayActivity;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.AppData;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.DBHandler;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.Logger;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.PreferenceManager;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.SampleData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

public class RecentItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    NativeTemplateStyle styles;
    private Context context;
    private ArrayList<SampleData> videoList;
    private DBHandler DBHandler;
    private RecentItemFragment fragment;
    private Dialog detailDialog;
    private Dialog deleteDialog;
    private int Count = 0;

    public RecentItemListAdapter(Context context, ArrayList<SampleData> videoLIst, RecentItemFragment fragment) {

        this.context = context;
        this.videoList = videoLIst;
        this.fragment = fragment;
        DBHandler = new DBHandler(this.context);
    }

    @Override
    public int getItemViewType(int position) {

        return position % 3;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case 0:
                RecyclerView.ViewHolder viewHolder;
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View v = inflater.inflate(R.layout.video_list_ad_layout, parent, false);
                viewHolder = new RecentItemListAdapter.AdViewHolder(v);
                return viewHolder;

            default:
                RecyclerView.ViewHolder viewHolder2;
                LayoutInflater inflater2 = LayoutInflater.from(parent.getContext());
                View v2 = inflater2.inflate(R.layout.video_list_item, parent, false);
                viewHolder2 = new RecentItemListAdapter.MyViewHolder(v2);
                return viewHolder2;
        }
    }

    private void showAdsWithCount() {

        if (Count == 4) {
            PreferenceManager.ShowAds();
            Count = 0;
        } else {
            Count = Count + 1;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case 0:
                AdViewHolder holder2 = (AdViewHolder) holder;
                styles = new NativeTemplateStyle.Builder().build();
                MobileAds.initialize(context, context.getString(R.string.app_id));
                AdLoader adLoader = new AdLoader.Builder(context, context.getString(R.string.nativead))
                        .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                            @Override
                            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                                holder2.template.setStyles(styles);
                                holder2.template.setNativeAd(unifiedNativeAd);

                            }
                        })
                        .build();

                adLoader.loadAd(new AdRequest.Builder().build());
                holder2.videoname.setText(videoList.get(position).getSongName());
                holder2.videotime.setText(videoList.get(position).getVideoTime());
                holder2.videosize.setText(videoList.get(position).getVideoSize() + " MB");

                Glide.with(AppData.contextRecent).load(videoList.get(position).getSampleVideo()).centerCrop().into(holder2.videoimage);

                holder2.menuitem.setOnClickListener(v -> ShowPopUpmenu(v, position));
                holder2.constraintLayout.setOnClickListener(v -> PlayClick(position));
                break;
            default:
                RecentItemListAdapter.MyViewHolder holder1 = (RecentItemListAdapter.MyViewHolder) holder;

                holder1.videoname.setText(videoList.get(position).getSongName());
                holder1.videotime.setText(videoList.get(position).getVideoTime());
                holder1.videosize.setText(videoList.get(position).getVideoSize() + " MB");

                Glide.with(AppData.contextRecent).load(videoList.get(position).getSampleVideo()).centerCrop().into(holder1.videoimage);

                holder1.menuitem.setOnClickListener(v -> ShowPopUpmenu(v, position));
                holder1.constraintLayout.setOnClickListener(v -> PlayClick(position));

                break;

        }

    }

    private void PlayClick(int position) {

        for (int i = 0; i < videoList.size(); i++) {

            HashMap<String, String> map = new HashMap<>();

            map.put(AppData.VideoPath, videoList.get(i).getSampleVideo());
            map.put(AppData.VideoReso, videoList.get(i).getVideoResolution());
            map.put(AppData.VideoDate, videoList.get(i).getVideoDateModi());
            map.put(AppData.VideoName, videoList.get(i).getSongName());
            map.put(AppData.VideoTime, videoList.get(i).getVideoTime());
            map.put(AppData.VideoSize, videoList.get(i).getVideoSize());
            map.put(AppData.VideoId, videoList.get(i).getKeyID());

            AppData.VideoListForContinuePlay.add(map);
        }

        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra("songLink", videoList.get(position).getSampleVideo());
        intent.putExtra("VideoPosition", position);
        context.startActivity(intent);
    }

    private void ShowPopUpmenu(View v, int position) {

        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.recentmenu, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.addtofavourite) {

                if (PreferenceManager.isNetworkConnected(context)) {
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
                    Toast.makeText(context, "Already in favourite..!!", Toast.LENGTH_LONG).show();
                } else {

                    DBHandler.addSampleData(mSampleData(position));
                    Toast.makeText(context, "Added in favourite..!!", Toast.LENGTH_LONG).show();
                }

            } else if (item.getItemId() == R.id.play) {

                PlayClick(position);


            } else if (item.getItemId() == R.id.details) {

                if (PreferenceManager.isNetworkConnected(context)) {
                    showAdsWithCount();
                }
                ShowDetailDailogue(position);

            } else if (item.getItemId() == R.id.remove) {

                if (PreferenceManager.isNetworkConnected(context)) {
                    showAdsWithCount();
                }

                DBHandler.deleteRecentData(videoList.get(position).getKeyID());
                videoList.remove(position);
                notifyDataSetChanged();

            } else if (item.getItemId() == R.id.delete) {

                if (PreferenceManager.isNetworkConnected(context)) {
                    showAdsWithCount();
                }
                ShowDeteteDailogue(position);

            } else if (item.getItemId() == R.id.share) {

                Uri photoURI;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {

                    photoURI = Uri.fromFile(new File(Objects.requireNonNull(videoList.get(position).getSampleVideo())));
                } else {

                    photoURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(Objects.requireNonNull(videoList.get(position).getSampleVideo())));
                }
                Intent videoshare = new Intent(Intent.ACTION_SEND);
                videoshare.setType("*/*");
                videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                videoshare.putExtra(Intent.EXTRA_STREAM, photoURI);

                try {
                    context.startActivity(videoshare);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        });
    }

    private void ShowDeteteDailogue(int position) {

        if (deleteDialog != null && deleteDialog.isShowing()) {
            deleteDialog.dismiss();
            deleteDialog = null;
        }

        deleteDialog = new Dialog(context, R.style.Transparent);
        deleteDialog.setContentView(R.layout.delete_dialog);
        deleteDialog.setCancelable(true);

        TextView name = deleteDialog.findViewById(R.id.txt_videoname);
        name.setText(mSampleData(position).getSongName());


        deleteDialog.findViewById(R.id.okbtn).setOnClickListener(v -> {

            File fdelete = new File(Objects.requireNonNull(videoList.get(position).getSampleVideo()));

            if (fdelete.exists()) {

                if (fdelete.delete()) {

                    boolean isPresent1 = false;
                    for (int i = 0; i < DBHandler.getAllSampleDatas().size(); i++) {
                        if (DBHandler.getAllSampleDatas().get(i).getKeyID().equalsIgnoreCase(videoList.get(position).getKeyID())) {

                            isPresent1 = true;
                            break;
                        }
                    }

                    if (!isPresent1) {

                        DBHandler.deleteSampleData(videoList.get(position).getKeyID());
                    }
                    DBHandler.deleteRecentData(videoList.get(position).getKeyID());
                    videoList.remove(position);
                    notifyDataSetChanged();

                    Logger.Print(">>> Dataa 33 >> " + videoList);
                }
            }
            deleteDialog.dismiss();
        });
        deleteDialog.findViewById(R.id.cancel).setOnClickListener(v -> deleteDialog.dismiss());

        if (!Objects.requireNonNull(fragment.getActivity()).isFinishing()) {

            deleteDialog.show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void ShowDetailDailogue(int position) {

        if (detailDialog != null && detailDialog.isShowing()) {
            detailDialog.dismiss();
            detailDialog = null;
        }

        detailDialog = new Dialog(context, R.style.Transparent);
        detailDialog.setContentView(R.layout.details_dialog);
        detailDialog.setCancelable(true);

        TextView name = detailDialog.findViewById(R.id.videoname);
        name.setText("Name: " + mSampleData(position).getSongName());

        TextView path = detailDialog.findViewById(R.id.videopath);
        path.setText("Path: " + mSampleData(position).getSampleVideo());

        TextView time = detailDialog.findViewById(R.id.videotime);
        time.setText("Time: " + mSampleData(position).getVideoTime());

        TextView size = detailDialog.findViewById(R.id.videosize);
        size.setText("Size: " + mSampleData(position).getVideoSize() + "MB");

        TextView date = detailDialog.findViewById(R.id.videodatemodified);
        date.setText("Date Modified : " + mSampleData(position).getVideoDateModi());

        TextView reso = detailDialog.findViewById(R.id.videoresolution);
        reso.setText("Resolution: " + mSampleData(position).getVideoResolution());

        detailDialog.findViewById(R.id.img_ok).setOnClickListener(v -> detailDialog.dismiss());

        if (!Objects.requireNonNull(fragment.getActivity()).isFinishing()) {

            detailDialog.show();
        }
    }

    private SampleData mSampleData(int position) {

        SampleData sampleData = new SampleData();
        sampleData.setKeyID(videoList.get(position).getKeyID());
        sampleData.setSampleVideo(videoList.get(position).getSampleVideo());
        sampleData.setSongName(videoList.get(position).getSongName());
        sampleData.setVideoTime(videoList.get(position).getVideoTime());
        sampleData.setVideoSize(videoList.get(position).getVideoSize());

        return sampleData;
    }

    @Override
    public int getItemCount() {

        if (videoList.size() > 0) {
            return videoList.size();
        }
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView videoname, videotime, videosize;
        ImageView videoimage, menuitem;
        ConstraintLayout constraintLayout;

        MyViewHolder(@NonNull View root) {
            super(root);

            menuitem = root.findViewById(R.id.menuitem);
            videoname = root.findViewById(R.id.txt_video_name);
            videotime = root.findViewById(R.id.txt_video_time);
            videosize = root.findViewById(R.id.txt_video_size);
            videoimage = root.findViewById(R.id.image);
            constraintLayout = root.findViewById(R.id.constraintLayout);
        }
    }

    private class AdViewHolder extends RecyclerView.ViewHolder {
        TemplateView template;
        TextView videoname, videotime, videosize;
        ImageView videoimage, menuitem;
        ConstraintLayout constraintLayout;

        public AdViewHolder(View root) {
            super(root);
            template = root.findViewById(R.id.my_template);
            menuitem = root.findViewById(R.id.menuitem);
            videoname = root.findViewById(R.id.txt_video_name);
            videotime = root.findViewById(R.id.txt_video_time);
            videosize = root.findViewById(R.id.txt_video_size);
            videoimage = root.findViewById(R.id.image);
            constraintLayout = root.findViewById(R.id.constraintLayout);

        }
    }
}
