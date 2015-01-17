package com.sharekit.smartkit.params;

import android.graphics.Bitmap;

/**
 * MofunSky.
 * Created by ChadSong on 15/1/16.
 * ^_^
 */
public class SinaWeiboParams {
    String title;
    String actionUrl;
    String dataUrl;
    String dataHdUrl;
    String content;
    String description;
    Bitmap thumb;
    //网络图片地址
    String thumbUrl;

    int duration;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getDataHdUrl() {
        return dataHdUrl;
    }

    public void setDataHdUrl(String dataHdUrl) {
        this.dataHdUrl = dataHdUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }
}
