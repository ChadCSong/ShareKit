package com.sharekit.smartkit.params;

import android.graphics.Bitmap;

public class WeChatParams {
    String shareUrl;
    String shareLowBandUrl;
    String title;
    String description;
    Bitmap thumb;
    String thumbUrl;

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getShareLowBandUrl() {
        return shareLowBandUrl;
    }

    public void setShareLowBandUrl(String shareLowBandUrl) {
        this.shareLowBandUrl = shareLowBandUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }
}