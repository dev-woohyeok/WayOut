package com.example.wayout_ver_01.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DTO_shop {
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("images")
    private ArrayList<DTO_image> images;
    @Expose
    @SerializedName("address")
    private String address;
    @Expose
    @SerializedName("index")
    private String index;
    @Expose
    @SerializedName("writer")
    private String writer;
    @Expose
    @SerializedName("more")
    private String more_address;
    @Expose
    @SerializedName("info")
    private String info;
    @Expose
    @SerializedName("open")
    private String open;
    @Expose
    @SerializedName("close")
    private String close;
    @Expose
    @SerializedName("holiday")
    private String holiday;
    @Expose
    @SerializedName("themes")
    private ArrayList<DTO_theme> themes;
    @Expose
    @SerializedName("cafe_index")
    private String cafe_index;
    @Expose
    @SerializedName("image")
    private String image;
    @Expose
    @SerializedName("total")
    private float total;
    @Expose
    @SerializedName("reviews")
    private ArrayList<DTO_review> reviews;
    @Expose
    @SerializedName("isChecked")
    private int isChecked;
    public DTO_shop(String name, String index, String image , String address, float rate) {
        this.name = name;
        this.index = index;
        this.image = image;
        this.address = address;
        this.total = rate;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public ArrayList<DTO_review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<DTO_review> reviews) {
        this.reviews = reviews;
    }

    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<DTO_image> getImages() {
        return images;
    }

    public void setImages(ArrayList<DTO_image> images) {
        this.images = images;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMore_address() {
        return more_address;
    }

    public void setMore_address(String more_address) {
        this.more_address = more_address;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }

    public ArrayList<DTO_theme> getThemes() {
        return themes;
    }

    public void setThemes(ArrayList<DTO_theme> themes) {
        this.themes = themes;
    }

    public String getCafe_index() {
        return cafe_index;
    }

    public void setCafe_index(String cafe_index) {
        this.cafe_index = cafe_index;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
