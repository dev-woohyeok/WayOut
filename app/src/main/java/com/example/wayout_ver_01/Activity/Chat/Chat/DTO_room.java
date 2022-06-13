package com.example.wayout_ver_01.Activity.Chat.Chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DTO_room {
    @Expose
    @SerializedName("room_index")
    private String room_id;
    @Expose
    @SerializedName("room_name")
    private String room_name;
    @Expose
    @SerializedName("room_writer")
    private String room_writer;
    @Expose
    @SerializedName("room_date")
    private String room_date;
    @Expose
    @SerializedName("room_max")
    private int room_max;
    @Expose
    @SerializedName("room_info")
    private String room_info;
    @Expose
    @SerializedName("room_image")
    private String room_image;
    @Expose
    @SerializedName("user_image")
    private String user_image;
    @Expose
    @SerializedName("join_number")
    private int join_number;
    @Expose
    @SerializedName("join_user")
    private ArrayList<String> join_names;
    @Expose
    @SerializedName("room_message")
    private String room_message;
    @Expose
    @SerializedName("room_count")
    private int room_count;
    @Expose
    @SerializedName("last_id")
    private int last_id;
    @Expose
    @SerializedName("type")
    private String type;


    public DTO_room(String room_id, String room_name, String room_writer, String room_date, int room_max, String room_info, String room_image, String user_image, int join_number, ArrayList<String> join_names, String room_message) {
        this.room_id = room_id;
        this.room_name = room_name;
        this.room_writer = room_writer;
        this.room_date = room_date;
        this.room_max = room_max;
        this.room_info = room_info;
        this.room_image = room_image;
        this.user_image = user_image;
        this.join_number = join_number;
        this.join_names = join_names;
        this.room_message = room_message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLast_id() {
        return last_id;
    }

    public void setLast_id(int last_id) {
        this.last_id = last_id;
    }

    public int getRoom_count() {
        return room_count;
    }

    public void setRoom_count(int room_count) {
        this.room_count = room_count;
    }

    public int getJoin_number() {
        return join_number;
    }

    public void setJoin_number(int join_number) {
        this.join_number = join_number;
    }

    public String getRoom_message() {
        return room_message;
    }

    public void setRoom_message(String room_message) {
        this.room_message = room_message;
    }

    public ArrayList<String> getJoin_names() {
        return join_names;
    }

    public void setJoin_names(ArrayList<String> join_names) {
        this.join_names = join_names;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getRoom_writer() {
        return room_writer;
    }

    public void setRoom_writer(String room_writer) {
        this.room_writer = room_writer;
    }

    public String getRoom_date() {
        return room_date;
    }

    public void setRoom_date(String room_date) {
        this.room_date = room_date;
    }

    public int getRoom_max() {
        return room_max;
    }

    public void setRoom_max(int room_max) {
        this.room_max = room_max;
    }

    public String getRoom_info() {
        return room_info;
    }

    public void setRoom_info(String room_info) {
        this.room_info = room_info;
    }

    public String getRoom_image() {
        return room_image;
    }

    public void setRoom_image(String room_image) {
        this.room_image = room_image;
    }
}
