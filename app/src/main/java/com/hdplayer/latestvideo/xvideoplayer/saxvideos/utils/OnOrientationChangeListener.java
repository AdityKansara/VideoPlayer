package com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils;

import android.content.Context;
import android.view.OrientationEventListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;

public abstract class OnOrientationChangeListener {
    private final OrientationEventListener mListener;
    @ScreenOrientation
    private int mOrientation;

    public OnOrientationChangeListener(@NonNull Context context) {
        this(context, SCREEN_ORIENTATION_PORTRAIT);
    }

    public OnOrientationChangeListener(
            @NonNull Context context, @ScreenOrientation int initialOrientation) {
        mOrientation = initialOrientation;
        mListener = new OrientationEventListener(context.getApplicationContext()) {
            @Override
            public void onOrientationChanged(int rotation) {

                if (rotation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return;
                }


                if (rotation > 345 || rotation < 15) {
                    if (mOrientation != SCREEN_ORIENTATION_PORTRAIT) {
                        mOrientation = SCREEN_ORIENTATION_PORTRAIT;
                        onOrientationChange(mOrientation);
                    }
                } else if (rotation > 75 && rotation < 105) {
                    if (mOrientation != SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                        mOrientation = SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                        onOrientationChange(mOrientation);
                    }
                } else if (rotation > 165 && rotation < 195) {
                    if (mOrientation != SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                        mOrientation = SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                        onOrientationChange(mOrientation);
                    }
                } else if (rotation > 255 && rotation < 285) {
                    if (mOrientation != SCREEN_ORIENTATION_LANDSCAPE) {
                        mOrientation = SCREEN_ORIENTATION_LANDSCAPE;
                        onOrientationChange(mOrientation);
                    }
                }
            }
        };
    }

    public OnOrientationChangeListener setEnabled(boolean enabled) {
        if (enabled) {
            mListener.enable();
        } else {
            mListener.disable();
        }
        return this;
    }

    @ScreenOrientation
    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(@ScreenOrientation int orientation) {
        mOrientation = orientation;
    }

    public abstract void onOrientationChange(@ScreenOrientation int orientation);

    @IntDef({
            SCREEN_ORIENTATION_PORTRAIT,
            SCREEN_ORIENTATION_LANDSCAPE,
            SCREEN_ORIENTATION_REVERSE_PORTRAIT,
            SCREEN_ORIENTATION_REVERSE_LANDSCAPE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScreenOrientation {
    }
}
