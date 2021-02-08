package com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.liuzhenlin.floatingmenu.DensityUtils;
import com.liuzhenlin.texturevideoview.utils.SystemBarUtils;
import com.onesignal.OneSignal;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;


public class PreferenceManager extends MultiDexApplication {

    public static InterstitialAd interstitialAd;
    public static AdView adView;
    private static SharedPreferences sharedPreferences;
    private static PreferenceManager sApp;
    private static String sAppDirectory;
    AppOpenManager appOpenManager;
    private int mStatusHeight;
    private int mRealScreenWidth = -1;

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    public static void SetupInterstitialAds() {

        interstitialAd = new InterstitialAd(sApp, "");

        InterstitialAdListener adlistner = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                interstitialAd.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e("ADS_DATA", "aderror " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.e("ADS_DATA", "adloaded");
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig()
                .withAdListener(adlistner)
                .build());
    }

    public static void ShowAds() {
        if (interstitialAd != null && interstitialAd.isAdLoaded()) {
            interstitialAd.show();
        }
    }

    @NonNull
    public static PreferenceManager getInstance(@NonNull Context context) {
        return sApp == null ? (PreferenceManager) context.getApplicationContext() : sApp;
    }

    @Nullable
    public static PreferenceManager getInstanceUnsafe() {
        return sApp;
    }

    public static int getSorting() {
        return sharedPreferences.getInt("sorting", 3);
    }

    public static void setSorting(int name) {
        sharedPreferences.edit().putInt("sorting", name).apply();
    }

    @NonNull
    public static String getAppDirectory() {
        if (sAppDirectory == null) {
            sAppDirectory = Environment.getExternalStorageDirectory() + File.separator + "videos_lzl";
        }
        return sAppDirectory;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AudienceNetworkAds.initialize(this);

        sApp = this;
        sharedPreferences = getApplicationContext().getSharedPreferences("video", MODE_PRIVATE);
        AppData.getSize(this);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        mStatusHeight = SystemBarUtils.getStatusHeight(this);
        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });
        appOpenManager = new AppOpenManager(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public int getStatusHeightInPortrait() {
        return mStatusHeight;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public int getRealScreenWidthIgnoreOrientation() {
        if (mRealScreenWidth == -1) {
            int screenWidth = DensityUtils.getRealScreenWidth(this);
            if (getResources().getConfiguration().orientation
                    != Configuration.ORIENTATION_PORTRAIT) {
                //@formatter:off
                int screenHeight = DensityUtils.getRealScreenHeight(this);
                if (screenWidth > screenHeight) {
                    screenWidth ^= screenHeight;
                    screenHeight ^= screenWidth;
                    screenWidth ^= screenHeight;
                }
                //@formatter:on
            }
            mRealScreenWidth = screenWidth;
        }
        return mRealScreenWidth;
    }
}
