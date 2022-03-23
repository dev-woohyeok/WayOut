package com.example.wayout_ver_01.Retrofit;

import okhttp3.MultipartBody;

public class DTO_freeUpload {
    private MultipartBody file;
    private String uri;

    public DTO_freeUpload(String uri) {
        this.uri = uri;
    }

    public DTO_freeUpload(MultipartBody file) {
        this.file = file;
    }
}
