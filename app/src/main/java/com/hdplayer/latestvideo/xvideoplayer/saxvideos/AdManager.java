package com.hdplayer.latestvideo.xvideoplayer.saxvideos;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;

import org.jetbrains.annotations.NotNull;

public class AdManager {
    AdView mAdView;
    InterstitialAd mInterstitialAd;
    Context context;

    public AdManager(Context context, AdView mAdView, InterstitialAd mInterstitialAd) {
        this.mAdView = mAdView;
        this.mInterstitialAd = mInterstitialAd;
        this.context = context;

    }

    public AdManager(Context context, AdView mAdView) {
        this.mAdView = mAdView;
        this.context = context;

    }

    public AdManager(Context context, InterstitialAd interstitialAd) {
        this.mInterstitialAd = interstitialAd;
        this.context = context;

    }


    public void requestBannerAds() {

        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
                Toast.makeText(context, "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(context, "Ad left application!", Toast.LENGTH_SHORT).show();
            }

        });

        mAdView.loadAd(adRequest);
    }

    @NotNull
    private String getAds_fail() {
        return "ADS_FAIL";
    }

    public void requestForFullScreenAd() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Load ads into Interstitial Ads
        this.mInterstitialAd.loadAd(adRequest);

    }

    public void fullScreenAd() {
        mInterstitialAd.setAdUnitId(context.getString(R.string.i));

        requestForFullScreenAd();

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestForFullScreenAd();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e(getAds_fail(), loadAdError.getMessage());
            }


        });

    }
}
