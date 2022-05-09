package com.example.wayout_ver_01.Retrofit;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.ArrayList;

public class DTO_theme {

    // 테마명, 난이도, 제한시간
    // 평점, 장르, 카페명, 게시판 번호
    // 테마소개, 이미지들
    @Expose
    @SerializedName("index")private String index;
    @Expose
    @SerializedName("name") private String name;
    @Expose
    @SerializedName("difficult") private String difficult;
    @Expose
    @SerializedName("limit") private String limit;
    @Expose
    @SerializedName("grade") private int grade;
    @Expose
    @SerializedName("genre") private String genre;
    @Expose
    @SerializedName("cafe") private String cafe;
    @Expose
    @SerializedName("board_number") private String board_number;
    @Expose
    @SerializedName("info") private String info;
    @Expose
    @SerializedName("images") private ArrayList<String> images;
    @Expose
    @SerializedName("image") private String image;

    private Bitmap bitmap;
    private File file;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public DTO_theme(String name, String difficult, String limit, String genre, String cafe, String info, ArrayList<String> images) {
        this.name = name;
        this.difficult = difficult;
        this.limit = limit;
        this.genre = genre;
        this.cafe = cafe;
        this.info = info;
        this.images = images;
    }

    public DTO_theme(String index, String name, String difficult, String limit, String genre, String cafe, String image) {
        this.index = index;
        this.name = name;
        this.difficult = difficult;
        this.limit = limit;
        this.genre = genre;
        this.cafe = cafe;
        this.image = image;
    }

    public String getBoard_number() {
        return board_number;
    }

    public void setBoard_number(String board_number) {
        this.board_number = board_number;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDifficult() {
        return difficult;
    }

    public void setDifficult(String difficult) {
        this.difficult = difficult;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCafe() {
        return cafe;
    }

    public void setCafe(String cafe) {
        this.cafe = cafe;
    }
}
