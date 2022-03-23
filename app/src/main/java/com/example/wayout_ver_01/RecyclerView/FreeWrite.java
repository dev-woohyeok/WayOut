package com.example.wayout_ver_01.RecyclerView;

import android.net.Uri;

public class FreeWrite {
    Uri imageUri;

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public FreeWrite(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
