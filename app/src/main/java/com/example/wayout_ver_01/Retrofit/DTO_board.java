package com.example.wayout_ver_01.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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
    @SerializedName("board_number") private String board_number;
    @Expose
    @SerializedName("date") private String date;
    @Expose
    @SerializedName("imgNum") private int imgNum;
    @Expose
    @SerializedName("file") private List<MultipartBody.Part> files;
    @Expose
    @SerializedName("success") private boolean success;
    @Expose
    @SerializedName("reply_number") private int reply_number;
    @Expose
    @SerializedName("message") private String message;
    @Expose
    @SerializedName("images_uri") private List<DTO_image> images_uri;
    @Expose
    @SerializedName("free_reply") private List<DTO_free_reply> free_reply;
    @Expose
    @SerializedName("page") private int page;
    @Expose
    @SerializedName("total_reply") private String total_reply;

    public String getTotal_reply() {
        return total_reply;
    }

    public void setTotal_reply(String total_reply) {
        this.total_reply = total_reply;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getBoard_number() {
        return board_number;
    }

    public void setBoard_number(String board_number) {
        this.board_number = board_number;
    }

    public int getReply_number() {
        return reply_number;
    }

    public void setReply_number(int reply_number) {
        this.reply_number = reply_number;
    }

    public List<DTO_image> getImages_uri() {
        return images_uri;
    }

    public void setImages_uri(List<DTO_image> images_uri) {
        this.images_uri = images_uri;
    }

    public List<DTO_free_reply> getFree_reply() {
        return free_reply;
    }

    public int get_reply_size() {
        return free_reply.size();
    }

    public void setFree_reply(List<DTO_free_reply> free_reply) {
        this.free_reply = free_reply;
    }

    public int getImgNum() {
        return imgNum;
    }

    public void setImgNum(int imgNum) {
        this.imgNum = imgNum;
    }

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
