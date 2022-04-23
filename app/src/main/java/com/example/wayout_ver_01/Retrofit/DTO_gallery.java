package com.example.wayout_ver_01.Retrofit;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import java.io.Serializable;

public class DTO_gallery implements Serializable {
    @Expose
    @SerializedName("writer") private String writer;
    @Expose
    @SerializedName("cafe") private String cafe;
    @Expose
    @SerializedName("theme") private String theme;
    @Expose
    @SerializedName("date") private String date;
    @Expose
    @SerializedName("total_like") private int total_like;
    @Expose
    @SerializedName("click") private boolean click;
    @Expose
    @SerializedName("content") private String content;
    @Expose
    @SerializedName("board_number") private String board_number;
    @Expose
    @SerializedName("page") private int page;
    @Expose
    @SerializedName("limit") private int limit;
    @Expose
    @SerializedName("files") private ArrayList<MultipartBody> files;
    @Expose
    @SerializedName("files_number") private int files_number;
    @Expose
    @SerializedName("total_comment") private int total_comment;
    @Expose
    @SerializedName("images_uri") private ArrayList<DTO_image> images_uri;
    @Expose
    @SerializedName("gallery_comment") private ArrayList<DTO_comment> gallery_comment;
    @Expose
    @SerializedName("message") private String message;
    @Expose
    @SerializedName("success") private boolean success;
    @Expose
    @SerializedName("image") private String image;
    @Expose
    @SerializedName("like_id") private String like_id;
    @Expose
    @SerializedName("user_id") private String user_id;

    private String imageUri;
    private Bitmap bitmap;

    // 갤러리 게시판 생성자
    public DTO_gallery(String writer, String cafe, String theme, String date, int total_like, boolean click, String content, String board_number, String image) {
        this.writer = writer;
        this.cafe = cafe;
        this.theme = theme;
        this.date = date;
        this.total_like = total_like;
        this.click = click;
        this.content = content;
        this.board_number = board_number;
        this.image = image;
    }

    // 게시판 다중이미지 처리시 비트맵 담을때 생성자
    public DTO_gallery(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    // 게시판 이미지 uri  처리 생성자
    public DTO_gallery(String imageUri){
        this.imageUri = imageUri;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getCafe() {
        return cafe;
    }

    public void setCafe(String cafe) {
        this.cafe = cafe;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTotal_like() {
        return total_like;
    }

    public void setTotal_like(int total_like) {
        this.total_like = total_like;
    }

    public boolean isClick() {
        return click;
    }

    public void setClick(boolean click) {
        this.click = click;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBoard_number() {
        return board_number;
    }

    public void setBoard_number(String board_number) {
        this.board_number = board_number;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public ArrayList<MultipartBody> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<MultipartBody> files) {
        this.files = files;
    }

    public int getFiles_number() {
        return files_number;
    }

    public void setFiles_number(int files_number) {
        this.files_number = files_number;
    }

    public int getTotal_comment() {
        return total_comment;
    }

    public void setTotal_comment(int total_comment) {
        this.total_comment = total_comment;
    }

    public ArrayList<DTO_image> getImages_uri() {
        return images_uri;
    }

    public void setImages_uri(ArrayList<DTO_image> images_uri) {
        this.images_uri = images_uri;
    }

    public ArrayList<DTO_comment> getGallery_comment() {
        return gallery_comment;
    }

    public void setGallery_comment(ArrayList<DTO_comment> gallery_comment) {
        this.gallery_comment = gallery_comment;
    }
}

