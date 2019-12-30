package com.example.artbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class userPosts {
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    String url;
    String caption;

}
