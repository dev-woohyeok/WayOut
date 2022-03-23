package com.example.wayout_ver_01.Retrofit;

import android.graphics.Bitmap;
import android.net.Uri;

import java.net.URI;

public class DTO_img {

    String imageUri;
    Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public DTO_img(String imageUri) {
        this.imageUri = imageUri;
    }
}
