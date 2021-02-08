package com.hdplayer.latestvideo.xvideoplayer.saxvideos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.AdManager;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.VideoCallingActivity;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.R;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


public class GirlRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<GirlRoomModel> mRoom;
    private Context mContext;
    private LayoutInflater mInflater;
    private NativeTemplateStyle styles;
    private InterstitialAd interstitialAd;
    private AdManager adManager;

    public GirlRoomAdapter(Context context, List<GirlRoomModel> girlRoomModels) {
        this.mInflater = LayoutInflater.from(context);
        this.mRoom = girlRoomModels;
        this.mContext = context;
        interstitialAd = new InterstitialAd(context);
        adManager = new AdManager(context, interstitialAd);
        adManager.fullScreenAd();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
            case 3:
                RecyclerView.ViewHolder viewHolder1;
                LayoutInflater inflater1 = LayoutInflater.from(parent.getContext());
                View v1 = inflater1.inflate(R.layout.ad_layout, parent, false);
                viewHolder1 = new AdViewHolder(v1);
                return viewHolder1;
            case 1:
                RecyclerView.ViewHolder viewHolder2;
                LayoutInflater inflater2 = LayoutInflater.from(parent.getContext());
                View v2 = inflater2.inflate(R.layout.main_single_item_view, parent, false);
                viewHolder1 = new LayoutHolder(v2);
                return viewHolder1;
            case 2:
                LayoutInflater inflater3 = LayoutInflater.from(parent.getContext());
                View v3 = inflater3.inflate(R.layout.main_single_item_view2, parent, false);
                viewHolder1 = new LayoutHolder(v3);
                return viewHolder1;
            case 4:
                LayoutInflater inflater5 = LayoutInflater.from(parent.getContext());
                View v5 = inflater5.inflate(R.layout.main_single_item_view3, parent, false);
                viewHolder2 = new LayoutHolder(v5);
                return viewHolder2;
            default:
                RecyclerView.ViewHolder viewHolder;
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View v6 = inflater.inflate(R.layout.main_single_item_view4, parent, false);
                viewHolder = new LayoutHolder(v6);
                return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int ttlAdCount = (position / 3) + 1;
        switch (holder.getItemViewType()) {
            case 0:
            case 3:
                // google native ad code
                final AdViewHolder holder2 = (AdViewHolder) holder;
                styles = new NativeTemplateStyle.Builder().build();
                MobileAds.initialize(mContext, mContext.getString(R.string.nativead));
                AdLoader adLoader = new AdLoader.Builder(mContext, mContext.getString(R.string.nativead))
                        .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                            @Override
                            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                                holder2.template.setStyles(styles);
                                holder2.template.setNativeAd(unifiedNativeAd);
                            }
                        })
                        .build();
                adLoader.loadAd(new AdRequest.Builder().build());
                break;

            default:
                int test = ThreadLocalRandom.current().nextInt(1000, 7000 + 1);
                GirlRoomModel cardItem = mRoom.get(position);
                LayoutHolder holder1 = (LayoutHolder) holder;
                holder1.roomNo.setText(cardItem.getRoomNo() + (position + 1 - ttlAdCount));
                holder1.roomType.setText(cardItem.getRoomType());
                holder1.roomCount.setText(test + " Online");
                holder1.roomImg.setImageResource(cardItem.getRoomImg());


                holder1.rl_feature_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myActivity = new Intent(mContext.getApplicationContext(), VideoCallingActivity.class);
                        myActivity.putExtra("VdoPosition", position + 1);
                        myActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.getApplicationContext().startActivity(myActivity);
                        if (interstitialAd.isLoaded()) {
                            interstitialAd.show();
                        }
                    }
                });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position % 6;
    }

    @Override
    public int getItemCount() {
        return mRoom.size();
    }

    private class LayoutHolder extends RecyclerView.ViewHolder {
        LinearLayout rl_feature_item;
        ImageView roomImg;
        TextView roomNo, roomType, roomCount;

        public LayoutHolder(View itemView) {
            super(itemView);
            rl_feature_item = itemView.findViewById(R.id.rl_feature_item);
            roomImg = itemView.findViewById(R.id.roomImg);
            roomNo = itemView.findViewById(R.id.roomNo);
            roomType = itemView.findViewById(R.id.roomType);
            roomCount = itemView.findViewById(R.id.roomCount);
        }
    }

    private class AdViewHolder extends RecyclerView.ViewHolder {
        TemplateView template;
        ConstraintLayout constraintLayout;

        TextView videoname, videotime, videosize;
        ImageView videoimage, menuitem;
        LinearLayout rl_feature_item;
        ImageView roomImg;
        TextView roomNo, roomType, roomCount;

        public AdViewHolder(View root) {
            super(root);
            template = root.findViewById(R.id.my_template);
            menuitem = root.findViewById(R.id.menuitem);
            videoname = root.findViewById(R.id.txt_video_name);
            videotime = root.findViewById(R.id.txt_video_time);
            videosize = root.findViewById(R.id.txt_video_size);
            videoimage = root.findViewById(R.id.image);
            constraintLayout = root.findViewById(R.id.constraintLayout);

            //nativeAdLayout = root.findViewById(R.id.native_ad_container);
            //adView = root.findViewById(R.id.adContent);
            // Create native UI using the ad metadata.
            //nativeAdTitle = root.findViewById(R.id.tvTitle);
            //nativeAdMedia = root.findViewById(R.id.ivCoverImg);
            //nativeAdSocialContext = root.findViewById(R.id.tvSocialContext);
            //nativeAdBody = root.findViewById(R.id.tvDescription);
            //sponsoredLabel = root.findViewById(R.id.lblSponsored);
            //nativeAdCallToAction = root.findViewById(R.id.btnCTA);

            rl_feature_item = itemView.findViewById(R.id.rl_feature_item);
            roomImg = itemView.findViewById(R.id.roomImg);
            roomNo = itemView.findViewById(R.id.roomNo);
            roomType = itemView.findViewById(R.id.roomType);
            roomCount = itemView.findViewById(R.id.roomCount);

        }
    }
}
