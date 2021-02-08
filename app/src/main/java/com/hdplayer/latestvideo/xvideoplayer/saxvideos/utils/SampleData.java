package com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils;

public class SampleData {

    private int _id;
    private String _videoid;
    private String _sampleVideo;
    private String _size;
    private String _reso;
    private String _songName;
    private String _datemodi;
    private String _time;

    public SampleData() {
    }

    SampleData(int id, String sampleVideo, String banner) {
        this._id = id;
        this._sampleVideo = sampleVideo;

    }

    public void setID(int id) {
        this._id = id;
    }

    public String getKeyID() {
        return this._videoid;
    }

    public void setKeyID(String id) {
        this._videoid = id;
    }

    public String getSampleVideo() {
        return this._sampleVideo;
    }

    public void setSampleVideo(String sampleVideo) {
        this._sampleVideo = sampleVideo;
    }


    public String getVideoSize() {
        return this._size;
    }

    public void setVideoSize(String size) {
        this._size = size;
    }

    public String getVideoResolution() {
        return this._reso;
    }

    public void setVideoResolution(String reso) {
        this._reso = reso;
    }

    public String getVideoDateModi() {
        return this._datemodi;
    }

    public void setVideoDateModi(String date) {
        this._datemodi = date;
    }

    public String getSongName() {
        return this._songName;
    }

    public void setSongName(String songName) {
        this._songName = songName;
    }

    public String getVideoTime() {
        return this._time;
    }

    public void setVideoTime(String time) {
        this._time = time;
    }
}
