package com.hdplayer.latestvideo.xvideoplayer.saxvideos;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.io.IOException;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VideoCallingActivity extends AppCompatActivity implements Player.EventListener {

    PlayerView simpleExoPlayerView;
    SimpleExoPlayer exoPlayer;
    String videourl = "https://videostatus.sgp1.cdn.digitaloceanspaces.com/randomvideocall/";
    ProgressBar loading;
    int randomVideo = 0;
    TemplateView template;
    private Context myContext;
    private AudioManager audioManager;
    private InterstitialAd interstitialAd;
    private AdManager adManager;
    private NativeTemplateStyle styles;

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_calling);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;

        getSupportActionBar().hide();

        interstitialAd = new InterstitialAd(this);
        adManager = new AdManager(this, interstitialAd);
        adManager.fullScreenAd();

        Intent intent = getIntent();
        randomVideo = intent.getIntExtra("VdoPosition", 1);
        styles = new NativeTemplateStyle.Builder().build();
        template = findViewById(R.id.my_template);
        MobileAds.initialize(this, getString(R.string.nativead));
        AdLoader adLoader = new AdLoader.Builder(this, getString(R.string.nativead))
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                        template.setStyles(styles);
                        template.setNativeAd(unifiedNativeAd);

                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());

        simpleExoPlayerView = findViewById(R.id.player_view);
        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            Uri videoUri = Uri.parse(videourl + randomVideo + ".mp4");
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("player");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null,
                    null);
            simpleExoPlayerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.addListener(this);

        } catch (Exception e) {
            Log.e("error:", "video can not be loaded");
        }

        loading = findViewById(R.id.loading);
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

    }

    @Override
    public void onResume() {
        resumePlayer();
        super.onResume();
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void pausePlayer() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.getPlaybackState();
        }
    }

    private void resumePlayer() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.getPlaybackState();
        }
    }

    @Override
    protected void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }

    private boolean isPlaying() {
        return exoPlayer != null
                && exoPlayer.getPlaybackState() != Player.STATE_ENDED
                && exoPlayer.getPlaybackState() != Player.STATE_IDLE
                && exoPlayer.getPlayWhenReady();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {

            if (!isNetworkConnected(this)) {
                Toast.makeText(getApplicationContext(), "Please Check Your Internet Connection..", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.VISIBLE);
            }
            loading.setVisibility(View.VISIBLE);

        } else if (playbackState == Player.STATE_ENDED) {

            interstitialAd = new InterstitialAd(this);
            adManager = new AdManager(this, interstitialAd);
            adManager.fullScreenAd();
            finish();

        } else if (playbackState == Player.STATE_IDLE || playbackState == Player.STATE_READY) {

            if (!isNetworkConnected(this)) {
                Toast.makeText(getApplicationContext(), "Please Check Your Internet Connection..", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.VISIBLE);
            }
            loading.setVisibility(View.GONE);

        } else {

            if (!isNetworkConnected(this)) {
                loading.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Please Check Your Internet Connection..", Toast.LENGTH_SHORT).show();
            }
            loading.setVisibility(View.GONE);

        }

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

        checkingInternet();

        if (error.type == ExoPlaybackException.TYPE_SOURCE) {
            IOException cause = error.getSourceException();
            if (cause instanceof HttpDataSource.HttpDataSourceException) {
                // An HTTP error occurred.
                HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                // This is the request for which the error occurred.
                DataSpec requestDataSpec = httpError.dataSpec;
                // It's possible to find out more about the error both by casting and by
                // querying the cause.
                if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                    // Cast to InvalidResponseCodeException and retrieve the response code,
                    // message and headers.
                } else {
                    // Try calling httpError.getCause() to retrieve the underlying cause,
                    // although note that it may be null.
                }
            }
        }
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {
    }

    public void checkingInternet() {
        if (!isNetworkConnected(this)) {
            Toast.makeText(getApplicationContext(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}