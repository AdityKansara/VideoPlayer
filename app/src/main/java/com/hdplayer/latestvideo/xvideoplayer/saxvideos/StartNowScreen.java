package com.hdplayer.latestvideo.xvideoplayer.saxvideos;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.CustomBottomSheetDialog;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class StartNowScreen extends AppCompatActivity {

    public static ArrayList<String> appopenlist1;
    public static ArrayList<String> mainlink;
    public static boolean isFlagCheck = false;
    ProgressDialog pDialog;
    Button Start;
    RelativeLayout MainPage;
    RelativeLayout relativeMainLink;
    ImageView NoInternet;
    ImageView BG;
    ColorDrawable bgcolor;
    InterstitialAd interstitialAd;
    AdManager adManager;
    NativeTemplateStyle styles;
    TextView gtxt;
    TextView ctxt;
    ImageView gallery;
    ImageView cal;
    CustomBottomSheetDialog customBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_now_screen);

        PreferenceManager.SetupInterstitialAds();
        BG = findViewById(R.id.image);

        gallery = findViewById(R.id.gallery);
        cal = findViewById(R.id.cal);

        gtxt = findViewById(R.id.gtxt);
        ctxt = findViewById(R.id.ctxt);
        MainPage = findViewById(R.id.mainpage);
        relativeMainLink = findViewById(R.id.link);
        NoInternet = findViewById(R.id.nointernet);

        interstitialAd = new InterstitialAd(StartNowScreen.this);
        adManager = new AdManager(this, interstitialAd);
        adManager.fullScreenAd();
        final Handler handler = new Handler();
        customBottomSheetDialog = new CustomBottomSheetDialog(this);
        bgcolor = new ColorDrawable(ContextCompat.getColor(StartNowScreen.this, R.color.white));
        loadNativeAd();
        loadNativeAd2();
        pDialog = new ProgressDialog(this);
        gtxt.setSelected(true);
        ctxt.setSelected(true);
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.skylineapps.hidepicturesvideos");
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.skylineapps.gallery.pro");
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                }
            }
        });

        relativeMainLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connection.checkConnection(StartNowScreen.this)) {

                    try {
                        if (appopenlist1.size() >= 1) {

                            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(appopenlist1.get(0)));
                            try {
                                startActivity(myAppLinkToMarket);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(StartNowScreen.this, "unable to find market app", Toast.LENGTH_LONG).show();
                            }

                        }
                    } catch (NullPointerException n) {
                        n.printStackTrace();
                    }
                }
            }
        });

        Start = findViewById(R.id.btnStarts);
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd != null && interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
                if (!isFlagCheck) {
                    Intent intent = new Intent(StartNowScreen.this, AppSelection.class);
                    startActivity(intent);
                } else {
                    Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(mainlink.get(0))));
                    try {
                        startActivity(myAppLinkToMarket);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(StartNowScreen.this, " unable to find market app", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    @NotNull
    private String getInstant_loan() {
        return "Instant loan";
    }

    private void loadNativeAd() {
        styles = new NativeTemplateStyle.Builder().
                withMainBackgroundColor(bgcolor).build();
        MobileAds.initialize(StartNowScreen.this, getString(R.string.app_id));
        AdLoader adLoader = new AdLoader.Builder(StartNowScreen.this, getString(R.string.nativead2))
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {


                        TemplateView template = findViewById(R.id.my_template);
                        template.setStyles(styles);
                        template.setNativeAd(unifiedNativeAd);

                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void loadNativeAd2() {
        styles = new NativeTemplateStyle.Builder().build();
        MobileAds.initialize(StartNowScreen.this, getString(R.string.app_id));
        AdLoader adLoader = new AdLoader.Builder(StartNowScreen.this, getString(R.string.nativead2))
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        TemplateView template = findViewById(R.id.my_template2);
                        template.setStyles(styles);
                        template.setNativeAd(unifiedNativeAd);

                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onBackPressed() {
        customBottomSheetDialog.show();
    }

}
