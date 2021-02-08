package com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils;

import android.text.TextUtils;

import java.io.Serializable;

public class ImageData implements Serializable {
    public String folderName;
    public long id;
    public String imagePath;
    public String videoTitle;
    public String videoSize;
    public String videoTime;
    public String videoResolution;
    public String videodateModified;
    public String videokeyid;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String toString() {
        if (TextUtils.isEmpty(imagePath)) {
            return super.toString();
        }
        return "ImageData { imagePath=" + imagePath + ",folderName=" + folderName + ",videoTime=" + videoTime + ",videoSize=" + videoSize + ",videoTitle=" + videoTitle + " }";
    }
}
