package com.sharekit.smartkit.tools;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ShareItemEntity {
    private int id;
    private String name;
    private Drawable drawable;
    private ImageView imageView;

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}