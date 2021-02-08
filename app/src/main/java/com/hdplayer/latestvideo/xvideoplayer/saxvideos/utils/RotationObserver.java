package com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;

import com.liuzhenlin.texturevideoview.utils.ScreenUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class RotationObserver extends ContentObserver {

    private final Context mContext;

    public RotationObserver(@Nullable Handler handler, @NonNull Context context) {
        super(handler);
        mContext = context.getApplicationContext();
    }

    @Override
    public void onChange(boolean selfChange) {
        onRotationChange(selfChange, ScreenUtils.isRotationEnabled(mContext));
    }

    public void startObserver() {
        onChange(true);

        mContext.getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, this);
    }

    public void stopObserver() {
        mContext.getContentResolver().unregisterContentObserver(this);
    }

    public abstract void onRotationChange(boolean selfChange, boolean enabled);
}