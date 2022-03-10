package com.example.wayout_ver_01.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import okhttp3.MultipartBody;

public class DTO_board {

    @Expose
    @SerializedName("writer") private String writer;
    @Expose
    @SerializedName("title") private String title;
    @Expose
    @SerializedName("content") private String content;
    @Expose
    @SerializedName("boardNum") private String boardNum;
    @Expose
    @SerializedName("date") private String date;
    @Expose
    @SerializedName("file") private List<MultipartBody.Part> files;
    @Expose
    @SerializedName("success") private boolean success;
    @Expose
    @SerializedName("message") private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBoardNum() {
        return boardNum;
    }

    public void setBoardNum(String boardNum) {
        this.boardNum = boardNum;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<MultipartBody.Part> getFiles() {
        return files;
    }

    public void setFiles(List<MultipartBody.Part> files) {
        this.files = files;
    }
}
