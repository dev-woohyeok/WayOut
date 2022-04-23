package com.example.wayout_ver_01.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DTO_comment {
    @Expose
    @SerializedName("writer") private String writer;
    @Expose
    @SerializedName("content") private String content;
    @Expose
    @SerializedName("date") private String date;
    @Expose
    @SerializedName("board_number") private String board_number;
    @Expose
    @SerializedName("total_comment") private int total_comment;
    @Expose
    @SerializedName("comment_index") private String comment_index;
    @Expose
    @SerializedName("total_reply") private int total_reply;
    @Expose
    @SerializedName("success") private Boolean success;
    @Expose
    @SerializedName("message") private String message;



    public DTO_comment(String writer, String content, String date, String number,String index,int total_comment,int total_reply){
        this.writer = writer;
        this.content = content;
        this.date = date;
        this.board_number = number;
        this.comment_index = index;
        this.total_comment = total_comment;
        this.total_reply = total_reply;
    }

    public DTO_comment(String writer, String content, String date, String board_number, String comment_index) {
        this.writer = writer;
        this.content = content;
        this.date = date;
        this.board_number = board_number;
        this.comment_index = comment_index;
    }

    public int getTotal_reply() {
        return total_reply;
    }

    public void setTotal_reply(int total_reply) {
        this.total_reply = total_reply;
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

    public int getTotal_comment() {
        return total_comment;
    }

    public void setTotal_comment(int total_comment) {
        this.total_comment = total_comment;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBoard_number() {
        return board_number;
    }

    public void setBoard_number(String board_number) {
        this.board_number = board_number;
    }

    public String getComment_index() {
        return comment_index;
    }

    public void setComment_index(String comment_index) {
        this.comment_index = comment_index;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
