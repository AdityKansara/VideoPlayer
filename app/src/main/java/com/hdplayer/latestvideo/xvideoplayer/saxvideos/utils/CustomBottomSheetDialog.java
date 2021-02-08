package com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.R;

import androidx.annotation.NonNull;

public class CustomBottomSheetDialog extends BottomSheetDialog implements View.OnClickListener {

    private static CustomBottomSheetDialog instance;
    Context context;
    NativeTemplateStyle styles;
    TextView txtClose;
    private TemplateView template;

    public CustomBottomSheetDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        create();
    }

    public static CustomBottomSheetDialog getInstance(@NonNull Context context) {
        return instance == null ? new CustomBottomSheetDialog(context) : instance;
    }

    public void create() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        setContentView(bottomSheetView);
        txtClose = bottomSheetView.findViewById(R.id.close);
        txtClose.setOnClickListener(this);
        template = bottomSheetView.findViewById(R.id.my_template);
        styles = new NativeTemplateStyle.Builder().build();
        AdLoader adLoader = new AdLoader.Builder(context, context.getString(R.string.nativead))
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                        template.setStyles(styles);
                        template.setNativeAd(unifiedNativeAd);

                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        };


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.close) {
            ((Activity) context).finish();
        }

    }
}