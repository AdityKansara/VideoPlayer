package com.hdplayer.latestvideo.xvideoplayer.saxvideos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.simplemobiletools.gallery.pro.activities.SplashActivity;

import androidx.appcompat.app.AppCompatActivity;

public class AppSelection extends AppCompatActivity {

    private TemplateView template;
    private NativeTemplateStyle styles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);

        this.template = findViewById(R.id.my_template);
        this.styles = new NativeTemplateStyle.Builder().build();
        Button vdoplayerApp = findViewById(R.id.vdoplayerApp);
        Button randomvdoApp = findViewById(R.id.randomvdoApp);
        Button galleryApp = findViewById(R.id.myGallery);

        new AdLoader.Builder(this, getString(R.string.nativead2)).forUnifiedNativeAd(unifiedNativeAd -> {
            template.setStyles(styles);
            template.setNativeAd(unifiedNativeAd);
        }).build().loadAd(new AdRequest.Builder().addTestDevice("C68053D076017E2F8591897B2342DF90").build());


        vdoplayerApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppSelection.this, VideoPlayerDashboard.class);
                startActivity(intent);
            }
        });

        randomvdoApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppSelection.this, RandomVideoPlayer.class);
                startActivity(intent);
            }
        });

        galleryApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppSelection.this, SplashActivity.class);
                startActivity(intent);
            }
        });
    }
}
