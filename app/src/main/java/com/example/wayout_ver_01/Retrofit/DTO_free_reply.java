package com.example.wayout_ver_01.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DTO_free_reply {


    @Expose
    @SerializedName("reply_writer") private String reply_writer;
    @Expose
    @SerializedName("reply_content") private String reply_content;
    @Expose
    @SerializedName("reply_date") private String reply_date;
    @Expose
    @SerializedName("board_number") private String board_number;
    @Expose
    @SerializedName("reply_answer") private int reply_answer;
    @Expose
    @SerializedName("reply_index") private String reply_index;
    @Expose
    @SerializedName("success") private Boolean success;
    @Expose
    @SerializedName("message") private String message;
    @Expose
    @SerializedName("total") private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getReply_index() {
        return reply_index;
    }

    public void setReply_index(String reply_index) {
        this.reply_index = reply_index;
    }

    public int getReply_answer() {
        return reply_answer;
    }

    public void setReply_answer(int reply_answer) {
        this.reply_answer = reply_answer;
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

    public String getReply_writer() {
        return reply_writer;
    }

    public void setReply_writer(String reply_writer) {
        this.reply_writer = reply_writer;
    }

    public String getReply_content() {
        return reply_content;
    }

    public void setReply_content(String reply_content) {
        this.reply_content = reply_content;
    }

    public String getReply_date() {
        return reply_date;
    }

    public void setReply_date(String reply_date) {
        this.reply_date = reply_date;
    }

    public String getBoard_number() {
        return board_number;
    }

    public void setBoard_number(String board_number) {
        this.board_number = board_number;
    }
}
