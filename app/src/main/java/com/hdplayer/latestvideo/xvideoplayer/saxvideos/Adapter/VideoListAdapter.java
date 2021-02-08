package com.hdplayer.latestvideo.xvideoplayer.saxvideos.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.BuildConfig;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.R;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.VideoPlayActivity;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.AppData;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.DBHandler;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.Logger;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.SampleData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


public class VideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    NativeTemplateStyle styles;
    private ArrayList<HashMap<String, String>> List;
    private Fragment fragment;
    private DBHandler DBHandler;
    private Context context;
    private Dialog deleteDialog;
    private Dialog detailDialog;
    private NativeAd nativeAd;
    private String TAG = "VideoListAdapter";

    public VideoListAdapter(Context context, ArrayList<HashMap<String, String>> List, Fragment fragment) {

        this.context = context;
        this.List = List;
        this.fragment = fragment;
        DBHandler = new DBHandler(this.context);

    }

    public static boolean isViewClicked(View view, MotionEvent e) {
        Rect rect = new Rect();

        view.getGlobalVisibleRect(rect);

        return rect.contains((int) e.getRawX(), (int) e.getRawY());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                RecyclerView.ViewHolder viewHolder;
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View v = inflater.inflate(R.layout.video_list_ad_layout, parent, false);
                viewHolder = new AdViewHolder(v);
                return viewHolder;

            default:
                RecyclerView.ViewHolder viewHolder2;
                LayoutInflater inflater2 = LayoutInflater.from(parent.getContext());
                View v2 = inflater2.inflate(R.layout.video_list_item, parent, false);
                viewHolder = new MyviewHolder(v2);
                return viewHolder;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case 0:
                AdViewHolder holder2 = (AdViewHolder) viewHolder;
                styles = new NativeTemplateStyle.Builder().build();
                MobileAds.initialize(context, context.getString(R.string.app_id));
                AdLoader adLoader = new AdLoader.Builder(context, context.getString(R.string.nativead))
                        .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                            @Override
                            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                                holder2.template.setStyles(styles);
                                holder2.template.setNativeAd(unifiedNativeAd);

                            }
                        }).build();

                adLoader.loadAd(new AdRequest.Builder().build());
                holder2.videoname.setText(List.get(position).get(AppData.VideoName));
                holder2.videotime.setText(List.get(position).get(AppData.VideoTime));
                holder2.videosize.setText(List.get(position).get(AppData.VideoSize) + " MB");

                Glide.with(fragment).load(List.get(position).get(AppData.VideoPath)).centerCrop().into(holder2.videoimage);

                holder2.menuitem.setOnClickListener(v -> ShowPopUpmenu(v, position));

                holder2.constraintLayout.setOnClickListener(v -> PlayClick(position));

                nativeAd = new NativeAd(context, "849082378944882_855663101620143");

                nativeAd.setAdListener(new NativeAdListener() {
                    @Override
                    public void onMediaDownloaded(Ad ad) {
                        // Native ad finished downloading all assets
                        Log.e(TAG, "Native ad finished downloading all assets.");
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        // Native ad failed to load
                        Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                        if (nativeAd == null || nativeAd != ad) {
                            return;
                        }

                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                        // Native ad clicked
                        Log.d(TAG, "Native ad clicked!");
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {
                        // Native ad impression
                        Log.d(TAG, "Native ad impression logged!");
                    }
                });

                nativeAd.loadAd();


                break;
            default:
                MyviewHolder holder = (MyviewHolder) viewHolder;

                holder.videoname.setText(List.get(position).get(AppData.VideoName));
                holder.videotime.setText(List.get(position).get(AppData.VideoTime));
                holder.videosize.setText(List.get(position).get(AppData.VideoSize) + " MB");

                Glide.with(fragment).load(List.get(position).get(AppData.VideoPath)).centerCrop().into(holder.videoimage);

                holder.menuitem.setOnClickListener(v -> ShowPopUpmenu(v, position));

                holder.constraintLayout.setOnClickListener(v -> PlayClick(position));

                break;

        }
    }

//    private void inflateAd(NativeAd nativeAd, AdViewHolder holder) {
//
//        nativeAd.unregisterView();
//
//
//        // Set the Text.
//        holder.nativeAdTitle.setText(nativeAd.getAdvertiserName());
//        holder.nativeAdBody.setText(nativeAd.getAdBodyText());
//        holder.nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
//        holder.nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
//        holder.nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
//        holder.sponsoredLabel.setText(nativeAd.getSponsoredTranslation());
//
//        // Create a list of clickable views
//        java.util.List<View> clickableViews = new ArrayList<>();
//        clickableViews.add(holder.nativeAdTitle);
//        clickableViews.add(holder.nativeAdCallToAction);
//
//
//        // Register the Title and CTA button to listen for clicks.
//        nativeAd.registerViewForInteraction(
//                holder.adView,
//                holder.nativeAdMedia,
//                clickableViews);
//    }


    private SampleData mSampleData(int position) {

        SampleData sampleData = new SampleData();
        sampleData.setKeyID(List.get(position).get(AppData.VideoId));
        sampleData.setSampleVideo(List.get(position).get(AppData.VideoPath));
        sampleData.setSongName(List.get(position).get(AppData.VideoName));
        sampleData.setVideoTime(List.get(position).get(AppData.VideoTime));
        sampleData.setVideoSize(List.get(position).get(AppData.VideoSize));
        sampleData.setVideoResolution(List.get(position).get(AppData.VideoReso));
        sampleData.setVideoDateModi(List.get(position).get(AppData.VideoDate));

        return sampleData;
    }

    @Override
    public int getItemCount() {
        if (List.size() > 0) {
            return List.size();
        }
        return 0;
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

    private void ShowPopUpmenu(View v, int position) {

        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.home, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(item -> onPopupClick(position, item));
    }

    private boolean onPopupClick(int position, MenuItem item) {
        if (item.getItemId() == R.id.addtofavourite) {

            boolean isPresent = false;
            for (int i = 0; i < DBHandler.getAllSampleDatas().size(); i++) {

                Logger.Print(">>Id >> " + DBHandler.getAllSampleDatas().get(i).getKeyID() + " >> key >>> " + List.get(position).get(AppData.VideoId));
                if (DBHandler.getAllSampleDatas().get(i).getKeyID().equalsIgnoreCase(List.get(position).get(AppData.VideoId))) {

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

            ShowDetailDailogue(position);
        } else if (item.getItemId() == R.id.delete) {

            ShowDeteteDailogue(position);
        } else if (item.getItemId() == R.id.share) {

            Uri photoURI;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {

                photoURI = Uri.fromFile(new File(Objects.requireNonNull(List.get(position).get(AppData.VideoPath))));
            } else {

                photoURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(Objects.requireNonNull(List.get(position).get(AppData.VideoPath))));
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

        if (AppData.VideoListForContinuePlay != null) {
            AppData.VideoListForContinuePlay.clear();
            AppData.VideoListForContinuePlay.addAll(List);
        }
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra("songLink", List.get(position).get(AppData.VideoPath));
        intent.putExtra("VideoPosition", position);
        context.startActivity(intent);
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
        name.setText(List.get(position).get(AppData.VideoName));

        deleteDialog.findViewById(R.id.okbtn).setOnClickListener(v -> {
            onDeleteOkButtonClick(position);
        });
        deleteDialog.findViewById(R.id.cancel).setOnClickListener(v -> deleteDialog.dismiss());

        if (!Objects.requireNonNull(fragment.getActivity()).isFinishing()) {
            deleteDialog.show();
        }
    }

    private void onDeleteOkButtonClick(int position) {
        File fdelete = new File(Objects.requireNonNull(List.get(position).get(AppData.VideoPath)));

        if (fdelete.exists()) {

            if (fdelete.delete()) {

                boolean isPresent = false;
                for (int i = 0; i < DBHandler.getAllRecentDatas().size(); i++) {
                    if (DBHandler.getAllRecentDatas().get(i).getKeyID().equalsIgnoreCase(List.get(position).get(AppData.VideoId))) {

                        isPresent = true;
                        break;
                    }
                }

                if (!isPresent) {

                    DBHandler.deleteRecentData(List.get(position).get(AppData.VideoId));
                }

                boolean isPresent1 = false;
                for (int i = 0; i < DBHandler.getAllSampleDatas().size(); i++) {
                    if (DBHandler.getAllSampleDatas().get(i).getKeyID().equalsIgnoreCase(List.get(position).get(AppData.VideoId))) {

                        isPresent1 = true;
                        break;
                    }
                }

                if (!isPresent1) {

                    DBHandler.deleteSampleData(List.get(position).get(AppData.VideoId));
                }
                List.remove(position);
                notifyDataSetChanged();

                RefreshGallery();
                Logger.Print(">>> Dataa 33 >> " + List);
            }
        }
        deleteDialog.dismiss();
    }

    private void RefreshGallery() {

        MediaScannerConnection.scanFile(context, new String[]{Environment.getExternalStorageDirectory().toString()}, null, (path, uri) -> {
            Logger.Print("Scanned " + path + ":");
            Logger.Print("-> uri=" + uri);
        });
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position % 2;
    }

    public interface OnItemSelectedListener {
        void onPopUpShow();

        void onMenuAction(MenuItem item);
    }

    private class MyviewHolder extends RecyclerView.ViewHolder {
        TextView videoname, videotime, videosize;
        ImageView videoimage, menuitem;
        ConstraintLayout constraintLayout;

        MyviewHolder(@NonNull View root) {
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
        ConstraintLayout constraintLayout;
        TextView videoname, videotime, videosize;
        ImageView videoimage, menuitem;

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
