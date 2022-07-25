package com.example.wayout_ver_01.Activity.Chat.Chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DTO_follow {
    @Expose
    @SerializedName("follow_count")
    private int follow_count;
    @Expose
    @SerializedName("user_id")
    private String user_id;
    @Expose
    @SerializedName("user_image")
    private String user_image;
    @Expose
    @SerializedName("follow_state")
    private int follow_state;
    @Expose
    @SerializedName("user_index")
    private String user_index;
    @Expose
    @SerializedName("follows")
    private ArrayList<DTO_follow> follows;
    @Expose
    @SerializedName("join_user")
    private ArrayList<String> join_names;
    @Expose
    @SerializedName("follow_num")
    private String follow_num;
    @Expose
    @SerializedName("following_num")
    private String Following_num;

    public String getFollow_num() {
        return follow_num;
    }

    public void setFollow_num(String follow_num) {
        this.follow_num = follow_num;
    }

    public String getFollowing_num() {
        return Following_num;
    }

    public void setFollowing_num(String following_num) {
        Following_num = following_num;
    }

    public String getUser_index() {
        return user_index;
    }

    public void setUser_index(String user_index) {
        this.user_index = user_index;
    }

    public ArrayList<String> getJoin_names() {
        return join_names;
    }

    public void setJoin_names(ArrayList<String> join_names) {
        this.join_names = join_names;
    }

    public int getFollow_count() {
        return follow_count;
    }

    public void setFollow_count(int follow_count) {
        this.follow_count = follow_count;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public int getFollow_state() {
        return follow_state;
    }

    public void setFollow_state(int follow_state) {
        this.follow_state = follow_state;
    }

    public ArrayList<DTO_follow> getFollows() {
        return follows;
    }

    public void setFollows(ArrayList<DTO_follow> follows) {
        this.follows = follows;
    }
}
