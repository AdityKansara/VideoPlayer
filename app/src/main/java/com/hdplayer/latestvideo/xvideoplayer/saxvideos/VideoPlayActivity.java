package com.hdplayer.latestvideo.xvideoplayer.saxvideos;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Rational;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.InterstitialAd;
import com.liuzhenlin.texturevideoview.AbsTextureVideoView;
import com.liuzhenlin.texturevideoview.BuildConfig;
import com.liuzhenlin.texturevideoview.VideoPlayerControl;
import com.liuzhenlin.texturevideoview.utils.FileUtils;
import com.liuzhenlin.texturevideoview.utils.SystemBarUtils;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.AppData;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.OnOrientationChangeListener;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.PreferenceManager;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.RotationObserver;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.IconCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;


public class VideoPlayActivity extends AppCompatActivity {

    private static final int PIP_ACTION_FAST_REWIND = 1 << 3;
    private static final int PIP_ACTION_PLAY = 1;
    private static final int PIP_ACTION_PAUSE = 1 << 1;
    private static final int PIP_ACTION_FAST_FORWARD = 1 << 2;
    private static final int REQUEST_FAST_REWIND = 4;
    private static final int REQUEST_FAST_FORWARD = 3;
    private static final int REQUEST_PAUSE = 2;
    private static final int REQUEST_PLAY = 1;
    private static final String ACTION_MEDIA_CONTROL = "media_control";
    private static final String EXTRA_PIP_ACTION = "PiP_action";
    private static final int PFLAG_SCREEN_NOTCH_SUPPORT = 1;
    private static final int PFLAG_SCREEN_NOTCH_SUPPORT_ON_EMUI = 1 << 1;
    private static final int PFLAG_SCREEN_NOTCH_SUPPORT_ON_MIUI = 1 << 2;
    private static final int PFLAG_SCREEN_NOTCH_HIDDEN = 1 << 3;
    private static final int PFLAG_DEVICE_SCREEN_ROTATION_ENABLED = 1 << 4;
    private static final int PFLAG_SCREEN_ORIENTATION_LOCKED = 1 << 5;
    private static final int PFLAG_SCREEN_ORIENTATION_PORTRAIT_IMMUTABLE = 1 << 6;
    private static final int PFLAG_LAST_VIDEO_LAYOUT_IS_FULLSCREEN = 1 << 7;
    private static final int DELAY_TIME_HIDE_LOCK_UNLOCK_ORIENTATION_BUTTON = 2500;
    private static final Object sRefreshVideoProgressPayload = new Object();
    private static final Object sHighlightSelectedItemIfExistsPayload = new Object();
    private static final float RATIO_TOLERANCE_PIP_LAYOUT_SIZE = 5.0f / 3.0f;
    private static final String KEY_IS_SCREEN_ORIENTATION_LOCKED = "kisol";
    private static final String KEY_DEVICE_ORIENTATION = "kdo";
    private static final String KEY_SCREEN_ORIENTATION = "kso";
    private static final String KEY_STATUS_HEIGHT_IN_LANDSCAPE_OF_NOTCH_SUPPORT_DEVICES = "kshilonsd";
    private static final String KEY_VIDEO_INDEX = "kvi";
    private static WeakReference<VideoPlayActivity> sActivityInPiP;
    private static float sPipRatioOfProgressHeightToVideoSize;
    private static int sPipProgressMinHeight;
    private static int sPipProgressMaxHeight;
    private final int mStatusHeight = Objects.requireNonNull(PreferenceManager.getInstanceUnsafe()).getStatusHeightInPortrait();
    String link = "";
    AdManager adManager;
    InterstitialAd interstitialAd;
    private AbsTextureVideoView mVideoView;
    private int mVideoWidth;
    private int mVideoHeight;
    private String mPlay;
    private String mPause;
    private String mFastForward;
    private String mFastRewind;
    private String mLockOrientation;
    private String mUnlockOrientation;
    private String mWatching;
    private PictureInPictureParams.Builder mPipParamsBuilder;
    private Handler mHandler;
    private int mPrivateFlags;
    private int mDeviceOrientation = SCREEN_ORIENTATION_PORTRAIT;
    private int mScreenOrientation = SCREEN_ORIENTATION_PORTRAIT;
    private int mStatusHeightInLandscapeOfNotchSupportDevices;
    private int mNotchHeight;
    private ImageView mLockUnlockOrientationButton;
    private final Runnable mHideLockUnlockOrientationButtonRunnable = () -> showLockUnlockOrientationButton(false);
    private RotationObserver mRotationObserver;
    private OnOrientationChangeListener mOnOrientationChangeListener;
    private int mVideoIndex = -1;
    private RecyclerView mPlayList;
    private BroadcastReceiver mReceiver;
    private View.OnLayoutChangeListener mOnPipLayoutChangeListener;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private RefreshVideoProgressInPiPTask mRefreshVideoProgressInPiPTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

//        mpw_video_player = findViewById(R.id.mpw_video_player);

        link = getIntent().getStringExtra("songLink");
        mVideoIndex = getIntent().getIntExtra("VideoPosition", 0);
//        mpw_video_player.autoStartPlay(link, MxVideoPlayer.SCREEN_LAYOUT_NORMAL, "");


//        mVideoView.setVideoPath(link);

        VideoViewListner(savedInstanceState);
        interstitialAd = new InterstitialAd(this);
        adManager = new AdManager(this, interstitialAd);
        adManager.fullScreenAd();

    }

    private void setScreenOrientationLocked(boolean locked) {
        if (locked) {
            mPrivateFlags |= PFLAG_SCREEN_ORIENTATION_LOCKED;
            mLockUnlockOrientationButton.setImageResource(R.drawable.ic_unlock);
            mLockUnlockOrientationButton.setContentDescription(mUnlockOrientation);
        } else {
            mPrivateFlags &= ~PFLAG_SCREEN_ORIENTATION_LOCKED;
            mLockUnlockOrientationButton.setImageResource(R.drawable.ic_lock);
            mLockUnlockOrientationButton.setContentDescription(mLockOrientation);
            changeScreenOrientationIfNeeded(mDeviceOrientation);
        }
    }

    private void showLockUnlockOrientationButton(boolean show) {
        mLockUnlockOrientationButton.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void setAutoRotationEnabled(boolean enabled) {
        if (enabled) {
            mRotationObserver.startObserver();
            mOnOrientationChangeListener.setEnabled(true);
        } else {
            mOnOrientationChangeListener.setEnabled(false);
            mRotationObserver.stopObserver();
        }
    }

    private void changeScreenOrientationIfNeeded(int orientation) {
        mScreenOrientation = orientation;
        setRequestedOrientation(orientation);
        if ((mPrivateFlags & PFLAG_SCREEN_ORIENTATION_LOCKED) == 0)
            setRequestedOrientation(orientation);

        final boolean fullscreen = orientation != SCREEN_ORIENTATION_PORTRAIT;
        if (fullscreen == mVideoView.isInFullscreenMode()) {
            //@formatter:off
            if ((mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT) == 0
                    || (mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT_ON_EMUI) != 0
                    && (mPrivateFlags & PFLAG_SCREEN_NOTCH_HIDDEN) != 0) {
                //@formatter:on
                if (mVideoView.isControlsShowing()) {
                    mVideoView.showControls(true, false);
                }
                return;
            }
            setFullscreenMode(fullscreen);
            return;
        }
        setFullscreenMode(fullscreen);

        showLockUnlockOrientationButton(true);
        mHandler.removeCallbacks(mHideLockUnlockOrientationButtonRunnable);
        mHandler.postDelayed(mHideLockUnlockOrientationButtonRunnable,
                DELAY_TIME_HIDE_LOCK_UNLOCK_ORIENTATION_BUTTON);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        Window window = getWindow();
        View decorView = window.getDecorView();

        setFullscreenMode(mVideoView.isInFullscreenMode());

        mHandler = decorView.getHandler();

        mRotationObserver = new RotationObserver(mHandler, this) {
            @Override
            public void onRotationChange(boolean selfChange, boolean enabled) {
                mPrivateFlags = (mPrivateFlags & ~PFLAG_DEVICE_SCREEN_ROTATION_ENABLED) |
                        (enabled ? PFLAG_DEVICE_SCREEN_ROTATION_ENABLED : 0);
            }
        };
        mOnOrientationChangeListener = new OnOrientationChangeListener(this, mDeviceOrientation) {
            @Override
            public void onOrientationChange(int orientation) {
                if (orientation != SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    if (mVideoWidth == 0 && mVideoHeight == 0) {
                        mOnOrientationChangeListener.setOrientation(mDeviceOrientation);
                        return;
                    }
                    mDeviceOrientation = orientation;
                    changeScreenOrientationIfNeeded(orientation);
                }
            }
        };
        setAutoRotationEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (sActivityInPiP != null && sActivityInPiP.get() == this) {
            sActivityInPiP.clear();
            sActivityInPiP = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(mHideLockUnlockOrientationButtonRunnable, null);
        }

        if (AppData.VideoListForContinuePlay != null) {
            AppData.VideoListForContinuePlay.clear();
        }
    }

    private void setVideoToPlay(int videoIndex) {

        if (AppData.VideoListForContinuePlay != null && AppData.VideoListForContinuePlay.size() > videoIndex) {
            mVideoView.setCanSkipToPrevious(videoIndex > 0);
            mVideoView.setCanSkipToNext(videoIndex < AppData.VideoListForContinuePlay.size() - 1);
            mVideoView.setVideoPath(AppData.VideoListForContinuePlay.get(videoIndex).get(AppData.VideoPath));
        }
    }

    private void VideoViewListner(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mRefreshVideoProgressInPiPTask != null) {
                mRefreshVideoProgressInPiPTask.execute();
            }
        }

        mLockUnlockOrientationButton = findViewById(R.id.bt_lockUnlockOrientation);
        mLockUnlockOrientationButton.setOnClickListener(v -> setScreenOrientationLocked((mPrivateFlags & PFLAG_SCREEN_ORIENTATION_LOCKED) == 0));

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(KEY_IS_SCREEN_ORIENTATION_LOCKED, false)) {
                setScreenOrientationLocked(true);
            }
            mDeviceOrientation = savedInstanceState.getInt(
                    KEY_DEVICE_ORIENTATION, SCREEN_ORIENTATION_PORTRAIT);
            mScreenOrientation = savedInstanceState.getInt(
                    KEY_SCREEN_ORIENTATION, SCREEN_ORIENTATION_PORTRAIT);
            mStatusHeightInLandscapeOfNotchSupportDevices = savedInstanceState.getInt(
                    KEY_STATUS_HEIGHT_IN_LANDSCAPE_OF_NOTCH_SUPPORT_DEVICES, 0);
        }


        mVideoView = findViewById(R.id.video_view);
        if (AppData.VideoListForContinuePlay.size() > 1) {
            mVideoView.setPlayListAdapter(new VideoEpisodesAdapter());
        }

        if (savedInstanceState == null && mVideoIndex != 0) {
            notifyItemSelectionChanged(0, mVideoIndex, true);
        }
        setVideoToPlay(mVideoIndex);
        setFullscreenMode(true);

        setListner();
        setEventListner();
        setOpCallBack();
    }

    private void setOpCallBack() {
        mVideoView.setOpCallback(new AbsTextureVideoView.OpCallback() {
            @NonNull
            @Override
            public Window getWindow() {
                return VideoPlayActivity.this.getWindow();
            }

            @NonNull
            @Override
            public String getFileOutputDirectory() {
                return PreferenceManager.getAppDirectory();
            }
        });
    }

    private void setEventListner() {
        mVideoView.setEventListener(new AbsTextureVideoView.EventListener() {

            @Override
            public void onSkipToPrevious() {

                setVideoToPlay(--mVideoIndex);
                notifyItemSelectionChanged(mVideoIndex + 1, mVideoIndex, true);
            }

            @Override
            public void onSkipToNext() {
//                mVideoView.setVideoPath(link);

                setVideoToPlay(++mVideoIndex);
                notifyItemSelectionChanged(mVideoIndex - 1, mVideoIndex, true);
            }

            @Override
            public void onReturnClicked() {
                finish();
            }

            public void onViewModeChange(int oldMode, int newMode, boolean layoutMatches) {
                switch (newMode) {
                    case AbsTextureVideoView.VIEW_MODE_MINIMUM:
                        if (!layoutMatches
                                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                                && mVideoWidth != 0 && mVideoHeight != 0) {
                            if (mPipParamsBuilder == null) {
                                mPipParamsBuilder = new PictureInPictureParams.Builder();
                            }
                            enterPictureInPictureMode(mPipParamsBuilder
                                    .setAspectRatio(new Rational(mVideoWidth, mVideoHeight))
                                    .build());
                        }
                        break;
                    case AbsTextureVideoView.VIEW_MODE_DEFAULT:
                        setFullscreenModeManually(true);
                        break;
                    case AbsTextureVideoView.VIEW_MODE_LOCKED_FULLSCREEN:
                    case AbsTextureVideoView.VIEW_MODE_VIDEO_STRETCHED_LOCKED_FULLSCREEN:
                        showLockUnlockOrientationButton(false);
                        break;
                    case AbsTextureVideoView.VIEW_MODE_VIDEO_STRETCHED_FULLSCREEN:
                    case AbsTextureVideoView.VIEW_MODE_FULLSCREEN:
                        setFullscreenModeManually(true);
                        break;
                }
            }

            @Override
            public void onShareVideo() {

                FileUtils.shareFile(VideoPlayActivity.this, "com.mxhdplayer.video.player.videoplayer" + ".provider", new File(Objects.requireNonNull(link)), "*/*");

            }

            @Override
            public void onShareCapturedVideoPhoto(@NonNull File photo) {
                Context context = VideoPlayActivity.this;
                FileUtils.shareFile(context, "com.mxhdplayer.video.player.videoplayer" + ".provider", photo, "image/*");
            }
        });
    }

    private void setListner() {
        mVideoView.setVideoListener(new AbsTextureVideoView.VideoListener() {
            @Override
            public void onVideoStarted() {

                if (mVideoWidth == 0 && mVideoHeight == 0) {
                    mVideoWidth = mVideoView.getVideoWidth();
                    mVideoHeight = mVideoView.getVideoHeight();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    if (isInPictureInPictureMode()) {
                        // This Activity is recreated after killed by the System
                        if (mPipParamsBuilder == null) {
                            mPipParamsBuilder = new PictureInPictureParams.Builder();
                            onPictureInPictureModeChanged(true);
                        } else {
                            // We are playing the video now. In PiP mode, we want to show several
                            // action items to fast rewind, pause and fast forward the video.
                            updatePictureInPictureActions(PIP_ACTION_FAST_REWIND
                                    | PIP_ACTION_PAUSE | PIP_ACTION_FAST_FORWARD);

                            if (mRefreshVideoProgressInPiPTask != null) {
                                mRefreshVideoProgressInPiPTask.execute();
                            }
                        }
                    }
                }
            }

            @Override
            public void onVideoStopped() {
                // The video stopped or reached the playlist end. In PiP mode, we want to show some
                // action items to fast rewind, play and fast forward the video.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isInPictureInPictureMode()) {
                    int actions = PIP_ACTION_FAST_REWIND | PIP_ACTION_PLAY;
                    if (!(mVideoView.getPlaybackState() == VideoPlayerControl.PLAYBACK_STATE_COMPLETED
                            && !mVideoView.canSkipToNext())) {
                        actions |= PIP_ACTION_FAST_FORWARD;
                    }
                    updatePictureInPictureActions(actions);
                }
            }

            @Override
            public void onVideoSizeChanged(int oldWidth, int oldHeight, int width, int height) {
                mVideoWidth = width;
                mVideoHeight = height;

                if (width == 0 && height == 0) return;
                if (width > height) {
                    mPrivateFlags &= ~PFLAG_SCREEN_ORIENTATION_PORTRAIT_IMMUTABLE;
                    if (mScreenOrientation == SCREEN_ORIENTATION_PORTRAIT
                            && mVideoView.isInFullscreenMode()) {
                        mScreenOrientation = mDeviceOrientation == SCREEN_ORIENTATION_PORTRAIT
                                ? SCREEN_ORIENTATION_LANDSCAPE : mDeviceOrientation;
                        setRequestedOrientation(mScreenOrientation);
                        setFullscreenMode(true);
                    }
                } else {
                    mPrivateFlags |= PFLAG_SCREEN_ORIENTATION_PORTRAIT_IMMUTABLE;
                    if (mScreenOrientation != SCREEN_ORIENTATION_PORTRAIT) {
                        mScreenOrientation = SCREEN_ORIENTATION_PORTRAIT;
                        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
                        setFullscreenMode(true);
                    }
                }
            }
        });
    }

    private void notifyItemSelectionChanged(int oldPosition, int position, boolean checkNewItemVisibility) {
        if (mPlayList != null) {
            setAdapterMethod(oldPosition, position);
            if (checkNewItemVisibility) {
                RecyclerView.LayoutManager lm = mPlayList.getLayoutManager();
                if (lm instanceof LinearLayoutManager) {
                    LinearLayoutManager llm = (LinearLayoutManager) lm;
                    if (llm.findLastCompletelyVisibleItemPosition() < position) {
                        llm.scrollToPosition(position);
                    }
                } else if (lm instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager sglm = (StaggeredGridLayoutManager) lm;
                    int maxCompletelyVisiblePosition = 0;
                    for (int i : sglm.findLastCompletelyVisibleItemPositions(null)) {
                        maxCompletelyVisiblePosition = Math.max(maxCompletelyVisiblePosition, i);
                    }
                    if (maxCompletelyVisiblePosition < position) {
                        sglm.scrollToPosition(position);
                    }
                }
            }
        }
    }

    private void setAdapterMethod(int oldPosition, int position) {
        RecyclerView.Adapter adapter = mPlayList.getAdapter();
        assert adapter != null;
        adapter.notifyItemChanged(oldPosition, sRefreshVideoProgressPayload);
        adapter.notifyItemChanged(oldPosition, sHighlightSelectedItemIfExistsPayload);
        adapter.notifyItemChanged(position, sRefreshVideoProgressPayload);
        adapter.notifyItemChanged(position, sHighlightSelectedItemIfExistsPayload);
    }

    private void setFullscreenModeManually(boolean fullscreen) {
        if (mVideoView.isInFullscreenMode() == fullscreen) {
            return;
        }
        if ((mPrivateFlags & PFLAG_SCREEN_ORIENTATION_PORTRAIT_IMMUTABLE) == 0) {
            mScreenOrientation = fullscreen ?
                    mDeviceOrientation == SCREEN_ORIENTATION_PORTRAIT
                            ? SCREEN_ORIENTATION_LANDSCAPE : mDeviceOrientation
                    : SCREEN_ORIENTATION_PORTRAIT;
            setRequestedOrientation(mScreenOrientation);
        }
        setFullscreenMode(fullscreen);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void resizeVideoView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isInPictureInPictureMode()) {
            setVideoViewSize(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return;
        }

        switch (mScreenOrientation) {

            case SCREEN_ORIENTATION_PORTRAIT:
                potraitCase();
                break;
            case SCREEN_ORIENTATION_LANDSCAPE:
                setVideoViewSize(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                orientationLandscape(mNotchHeight, 0);
                break;
            case SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                setVideoViewSize(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                orientationLandscape(0, mNotchHeight);
                break;
        }
    }

    private void orientationLandscape(int mNotchHeight, int i2) {
        if ((mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT_ON_EMUI) != 0
                && (mPrivateFlags & PFLAG_SCREEN_NOTCH_HIDDEN) != 0) {
            for (int i = 0, childCount = mVideoView.getChildCount(); i < childCount; i++) {
                AppData.setViewMargins(mVideoView.getChildAt(i), 0, 0, 0, 0);
            }
        } else if ((mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT) != 0) {
            for (int i = 0, childCount = mVideoView.getChildCount(); i < childCount; i++) {
                AppData.setViewMargins(mVideoView.getChildAt(i), mNotchHeight, 0, i2, 0);
            }
        }
    }

    private void potraitCase() {
        final boolean layoutIsFullscreen = mVideoView.isInFullscreenMode();

        final boolean lastLayoutIsFullscreen = (mPrivateFlags & PFLAG_LAST_VIDEO_LAYOUT_IS_FULLSCREEN) != 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && (mPrivateFlags & PFLAG_SCREEN_ORIENTATION_PORTRAIT_IMMUTABLE) != 0
                && layoutIsFullscreen != lastLayoutIsFullscreen) {
            TransitionManager.beginDelayedTransition(mVideoView, new ChangeBounds());
        }

        if (layoutIsFullscreen) {

            setVideoViewSize(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if ((mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT) != 0) {

                for (int i = 0, childCount = mVideoView.getChildCount(); i < childCount; i++) {
                    AppData.setViewMargins(mVideoView.getChildAt(i), 0, mNotchHeight, 0, 0);
                }
            }
        } else {
            final int screenWidth = PreferenceManager.getInstance(this).getRealScreenWidthIgnoreOrientation();
            // portrait w : h = 16 : 9
            final int minLayoutHeight = (int) ((float) screenWidth / 16f * 9f + 0.5f);

            setVideoViewSize(screenWidth, minLayoutHeight);
            if ((mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT) != 0) {

                for (int i = 0, childCount = mVideoView.getChildCount(); i < childCount; i++) {
                    AppData.setViewMargins(mVideoView.getChildAt(i), 0, 0, 0, 0);
                }
            }
        }
    }

    private void setVideoViewSize(int layoutWidth, int layoutHeight) {
        ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
        lp.width = layoutWidth;
        lp.height = layoutHeight;
        mVideoView.setLayoutParams(lp);
    }

    private void showSystemBars(boolean show) {
        Window window = getWindow();
        if (show) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    SystemBarUtils.setStatusBackgroundColor(window, Color.TRANSPARENT);
                } else {
                    SystemBarUtils.setTranslucentStatus(window, true);
                }
                View decorView = window.getDecorView();
                int visibility = decorView.getVisibility();

                visibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

                visibility &= ~(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                decorView.setSystemUiVisibility(visibility);
            } else {
                SystemBarUtils.showSystemBars(window, true);
            }
        } else {
            SystemBarUtils.showSystemBars(window, false);
        }
    }

    private void setFullscreenMode(boolean fullscreen) {

        showSystemBars(fullscreen);
        mVideoView.setFullscreenMode(fullscreen,
                fullscreen && (
                        (mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT) == 0
                                || (mPrivateFlags & PFLAG_SCREEN_ORIENTATION_PORTRAIT_IMMUTABLE) == 0
                ) ? ((mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT) == 0) ?
                        mStatusHeight : mStatusHeightInLandscapeOfNotchSupportDevices
                        : 0);
        //@formatter:on
        if (mVideoView.isControlsShowing()) {
            mVideoView.showControls(true, false);
        }
        resizeVideoView();

        mPrivateFlags = mPrivateFlags & ~PFLAG_LAST_VIDEO_LAYOUT_IS_FULLSCREEN
                | (fullscreen ? PFLAG_LAST_VIDEO_LAYOUT_IS_FULLSCREEN : 0);
    }

    /**
     * Create an pip action item in Picture-in-Picture mode.
     *
     * @param iconId      The icon to be used.
     * @param title       The title text.
     * @param pipAction   The type for the pip action. May be {@link #PIP_ACTION_PLAY},
     *                    {@link #PIP_ACTION_PAUSE},
     *                    {@link #PIP_ACTION_FAST_FORWARD},
     *                    or {@link #PIP_ACTION_FAST_REWIND}.
     * @param requestCode The request code for the {@link PendingIntent}.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private RemoteAction createPipAction(
            @DrawableRes int iconId, String title, int pipAction, int requestCode) {
        // This is the PendingIntent that is invoked when a user clicks on the action item.
        // You need to use distinct request codes for play, pause, fast forward, and fast rewind,
        // or the PendingIntent won't be properly updated.
        PendingIntent intent = PendingIntent.getBroadcast(this,
                requestCode,
                new Intent(ACTION_MEDIA_CONTROL).putExtra(EXTRA_PIP_ACTION, pipAction),
                0);
        Icon icon = IconCompat.createWithResource(this, iconId).toIcon();
        return new RemoteAction(icon, title, title, intent);
    }

    /**
     * Update the action items in Picture-in-Picture mode.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updatePictureInPictureActions(int pipActions) {
        final List<RemoteAction> actions = new LinkedList<>();

        if ((pipActions & PIP_ACTION_FAST_REWIND) != 0) {
            actions.add(createPipAction(R.drawable.ic_fast_rewind_white_24dp,
                    mFastRewind, PIP_ACTION_FAST_REWIND, REQUEST_FAST_REWIND));
        }
        if ((pipActions & PIP_ACTION_PLAY) != 0) {
            actions.add(createPipAction(R.drawable.ic_play_white_24dp,
                    mPlay, PIP_ACTION_PLAY, REQUEST_PLAY));

        } else if ((pipActions & PIP_ACTION_PAUSE) != 0) {
            actions.add(createPipAction(R.drawable.ic_pause_white_24dp,
                    mPause, PIP_ACTION_PAUSE, REQUEST_PAUSE));
        }
        if ((pipActions & PIP_ACTION_FAST_FORWARD) != 0) {
            actions.add(createPipAction(R.drawable.ic_fast_forward_white_24dp,
                    mFastForward, PIP_ACTION_FAST_FORWARD, REQUEST_FAST_FORWARD));
        } else {
            RemoteAction action = createPipAction(R.drawable.ic_fast_forward_lightgray_24dp,
                    mFastForward, PIP_ACTION_FAST_FORWARD, REQUEST_FAST_FORWARD);
            action.setEnabled(false);
            actions.add(action);
        }

        mPipParamsBuilder.setActions(actions);

        // This is how you can update action items (or aspect ratio) for Picture-in-Picture mode.
        setPictureInPictureParams(mPipParamsBuilder.build());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        mLockOrientation = getString(R.string.lockScreenOrientation);
        mUnlockOrientation = getString(R.string.unlockScreenOrientation);
        mWatching = getString(R.string.watching);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPlay = getString(R.string.play);
            mPause = getString(R.string.pause);
            mFastForward = getString(R.string.fastForward);
            mFastRewind = getString(R.string.fastRewind);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sActivityInPiP != null) {
            final VideoPlayActivity activity = sActivityInPiP.get();
            if (activity != null && activity != this) {
                activity.unregisterReceiver(activity.mReceiver);
                activity.finish();
                sActivityInPiP.clear();
                sActivityInPiP = null;
            }
        }
    }

    @SuppressLint("SwitchIntDef")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        mVideoView.onMinimizationModeChange(isInPictureInPictureMode);
        if (isInPictureInPictureMode) {
            int actions = PIP_ACTION_FAST_REWIND;
            final int playbackState = mVideoView.getPlaybackState();
            switch (playbackState) {
                case VideoPlayerControl.PLAYBACK_STATE_PLAYING:
                    actions |= PIP_ACTION_PAUSE | PIP_ACTION_FAST_FORWARD;
                    break;
                case VideoPlayerControl.PLAYBACK_STATE_COMPLETED:
                    actions |= PIP_ACTION_PLAY
                            | (mVideoView.canSkipToNext() ? PIP_ACTION_FAST_FORWARD : 0);
                    break;
                default:
                    actions |= PIP_ACTION_PLAY | PIP_ACTION_FAST_FORWARD;
                    break;
            }
            updatePictureInPictureActions(actions);

            // Starts receiving events from action items in PiP mode.
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent == null || !ACTION_MEDIA_CONTROL.equals(intent.getAction())) {
                        return;
                    }

                    // This is where we are called back from Picture-in-Picture action items.
                    final int action = intent.getIntExtra(EXTRA_PIP_ACTION, 0);
                    switch (action) {
                        case PIP_ACTION_PLAY:
                            mVideoView.play(true);
                            break;
                        case PIP_ACTION_PAUSE:
                            mVideoView.pause(true);
                            break;
                        case PIP_ACTION_FAST_REWIND: {
                            mVideoView.fastRewind(true);
                        }
                        break;
                        case PIP_ACTION_FAST_FORWARD: {
                            mVideoView.fastForward(true);
                        }
                        break;
                    }
                }
            };
            registerReceiver(mReceiver, new IntentFilter(ACTION_MEDIA_CONTROL));

            sActivityInPiP = new WeakReference<>(this);

            setAutoRotationEnabled(false);
            showLockUnlockOrientationButton(false);
            mRefreshVideoProgressInPiPTask = new RefreshVideoProgressInPiPTask();
            mRefreshVideoProgressInPiPTask.execute();

            mVideoView.showControls(false, false);
            mVideoView.setClipViewBounds(true);
            resizeVideoView();

            mOnPipLayoutChangeListener = new View.OnLayoutChangeListener() {
                static final String TAG = "VideoActivityInPIP";
                float cachedVideoAspectRatio;
                int cachedSize = -1;

                {
                    if (sPipRatioOfProgressHeightToVideoSize == 0) {
                        // 1dp -> 2.75px (5.5inch  w * h = 1080 * 1920)
                        final float dp = getResources().getDisplayMetrics().density;
                        sPipRatioOfProgressHeightToVideoSize = 1.0f / (12121.2f * dp); // 1 : 33333.3 (px)
                        sPipProgressMinHeight = (int) (dp * 1.8f + 0.5f); // 5.45px -> 5px
                        sPipProgressMaxHeight = (int) (dp * 2.5f + 0.5f); // 7.375px -> 7px
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "sPipRatioOfProgressHeightToVideoSize = " + sPipRatioOfProgressHeightToVideoSize
                                    + "    " + "sPipProgressMinHeight = " + sPipProgressMinHeight
                                    + "    " + "sPipProgressMaxHeight = " + sPipProgressMaxHeight);
                        }
                    }
                }

                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (mVideoWidth == 0 || mVideoHeight == 0) return;

                    final float videoAspectRatio = (float) mVideoWidth / mVideoHeight;
                    final int width = right - left;
                    final int height = (int) (width / videoAspectRatio + 0.5f);
                    final int size = width * height;
                    final float sizeRatio = (float) size / cachedSize;

                    if (videoAspectRatio != cachedVideoAspectRatio
                            || sizeRatio > RATIO_TOLERANCE_PIP_LAYOUT_SIZE
                            || sizeRatio < 1.0f / RATIO_TOLERANCE_PIP_LAYOUT_SIZE) {
                        final int progressHeight = Math.max(sPipProgressMinHeight,
                                Math.min(sPipProgressMaxHeight,
                                        (int) (size * sPipRatioOfProgressHeightToVideoSize + 0.5f)));
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "sizeRatio = " + sizeRatio
                                    + "    " + "progressHeight = " + progressHeight
                                    + "    " + "size / 1.8dp = " + size / sPipProgressMinHeight
                                    + "    " + "size / 2.5dp = " + size / sPipProgressMaxHeight);
                        }

                        mPipParamsBuilder.setAspectRatio(
                                new Rational(width, height + progressHeight));
                        setPictureInPictureParams(mPipParamsBuilder.build());

                        cachedVideoAspectRatio = videoAspectRatio;
                        cachedSize = size;
                    }
                }
            };
            mVideoView.addOnLayoutChangeListener(mOnPipLayoutChangeListener);
        } else {
            // We are out of PiP mode. We can stop receiving events from it.
            unregisterReceiver(mReceiver);
            mReceiver = null;

            sActivityInPiP.clear();
            sActivityInPiP = null;

            mVideoView.removeOnLayoutChangeListener(mOnPipLayoutChangeListener);
            mOnPipLayoutChangeListener = null;

            mVideoView.showControls(true);
            mVideoView.setClipViewBounds(false);
            resizeVideoView();

            setAutoRotationEnabled(true);

            mRefreshVideoProgressInPiPTask.cancel();
            mRefreshVideoProgressInPiPTask = null;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || !isInPictureInPictureMode()) {
          /*  if (mNotchSwitchObserver != null) {
                mNotchSwitchObserver.startObserver();
            }*/
            setAutoRotationEnabled(true);
        }
        mVideoView.openVideo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isFinishing()) {
//            recordCurrVideoProgress();
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || !isInPictureInPictureMode()) {
          /*  if (mNotchSwitchObserver != null) {
                mNotchSwitchObserver.stopObserver();
            }*/
            setAutoRotationEnabled(false);
        }
        mVideoView.closeVideo();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
                && mStatusHeightInLandscapeOfNotchSupportDevices == 0) {
            mStatusHeightInLandscapeOfNotchSupportDevices = SystemBarUtils.getStatusHeight(this);
            if (mVideoView.isInFullscreenMode()) {
                mVideoView.setFullscreenMode(true, mStatusHeightInLandscapeOfNotchSupportDevices);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && mVideoView.isInFullscreenMode()) {
            showSystemBars(false);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_SCREEN_ORIENTATION_LOCKED,
                (mPrivateFlags & PFLAG_SCREEN_ORIENTATION_LOCKED) != 0);
        outState.putInt(KEY_DEVICE_ORIENTATION, mDeviceOrientation);
        outState.putInt(KEY_SCREEN_ORIENTATION, mScreenOrientation);
        outState.putInt(KEY_STATUS_HEIGHT_IN_LANDSCAPE_OF_NOTCH_SUPPORT_DEVICES,
                mStatusHeightInLandscapeOfNotchSupportDevices);
        outState.putInt(KEY_VIDEO_INDEX, 0);
    }

    private final class VideoEpisodesAdapter
            extends AbsTextureVideoView.PlayListAdapter<VideoEpisodesAdapter.ViewHolder> {

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            mPlayList = recyclerView;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(VideoPlayActivity.this)
                    .inflate(R.layout.video_play_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
            if (payloads.isEmpty()) {
                super.onBindViewHolder(holder, position, payloads);
            } else {
                for (Object payload : payloads) {
                    if (payload == sRefreshVideoProgressPayload) {

                        if (position == mVideoIndex) {
                            holder.videoProgressDurationText.setText(mWatching);
                        } else {
                            holder.videoProgressDurationText.setText(AppData.VideoListForContinuePlay.get(position).get(AppData.VideoTime));
                        }
                    } else if (payload == sHighlightSelectedItemIfExistsPayload) {
                        highlightSelectedItemIfExists(holder, position);
                    }
                }
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            highlightSelectedItemIfExists(holder, position);


            Glide.with(VideoPlayActivity.this).load(AppData.VideoListForContinuePlay.get(position).get(AppData.VideoPath)).centerCrop().into(holder.videoImage);

            holder.videoNameText.setText(AppData.VideoListForContinuePlay.get(position).get(AppData.VideoName));
            if (position == mVideoIndex) {
                holder.videoProgressDurationText.setText(mWatching);
            } else {
                holder.videoProgressDurationText.setText(AppData.VideoListForContinuePlay.get(position).get(AppData.VideoTime));
            }
        }

        @Override
        public int getItemCount() {
            return AppData.VideoListForContinuePlay.size();
        }

        void highlightSelectedItemIfExists(ViewHolder holder, int position) {
            final boolean selected = position == mVideoIndex;
            holder.itemView.setSelected(selected);
            holder.videoNameText.setTextColor(selected ? getResources().getColor(R.color.highlighted) : Color.WHITE);
            holder.videoProgressDurationText.setTextColor(selected ? getResources().getColor(R.color.highlighted) : 0x80FFFFFF);
        }

        @Override
        public void onItemClick(@NonNull View view, int position) {
            if (mVideoIndex == position) {
                Toast.makeText(VideoPlayActivity.this, R.string.theVideoIsPlaying, Toast.LENGTH_SHORT).show();
            } else {

                setVideoToPlay(position);

                final int oldIndex = mVideoIndex;
                mVideoIndex = position;
                notifyItemSelectionChanged(oldIndex, position, false);
            }
        }

        final class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView videoImage;
            final TextView videoNameText;
            final TextView videoProgressDurationText;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                videoImage = itemView.findViewById(R.id.image_video);
                videoNameText = itemView.findViewById(R.id.text_videoName);
                videoProgressDurationText = itemView.findViewById(R.id.text_videoProgressAndDuration);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private final class RefreshVideoProgressInPiPTask {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final int progress = mVideoView.getVideoProgress();
                if (mVideoView.isPlaying()) {
                    mHandler.postDelayed(this, 1000 - progress % 1000);
                }

            }
        };

        void execute() {
            cancel();
            runnable.run();
        }

        void cancel() {
            mHandler.removeCallbacks(runnable);
        }
    }
}
