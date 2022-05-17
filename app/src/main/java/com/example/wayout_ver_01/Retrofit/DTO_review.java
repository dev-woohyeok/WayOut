package com.example.wayout_ver_01.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DTO_review {
    @Expose
    @SerializedName("writer") private String writer;
    @Expose
    @SerializedName("content") private String content;
    @Expose
    @SerializedName("rate") private float rate;
    @Expose
    @SerializedName("index") private String index;
    @Expose
    @SerializedName("date") private String date;
    @Expose
    @SerializedName("diff") private String diff;
    @Expose
    @SerializedName("success") private String success;
    @Expose
    @SerializedName("user_index") private String user_index;
    @Expose
    @SerializedName("total") private float total;

    public DTO_review(String writer, String content, float rate, String index, String date) {
        this.writer = writer;
        this.content = content;
        this.rate = rate;
        this.index = index;
        this.date = date;
    }

    public DTO_review(String writer, String content, float rate, String index, String date, String diff, String success, String user_index, float total) {
        this.writer = writer;
        this.content = content;
        this.rate = rate;
        this.index = index;
        this.date = date;
        this.diff = diff;
        this.success = success;
        this.user_index = user_index;
        this.total = total;
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getUser_index() {
        return user_index;
    }

    public void setUser_index(String user_index) {
        this.user_index = user_index;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
