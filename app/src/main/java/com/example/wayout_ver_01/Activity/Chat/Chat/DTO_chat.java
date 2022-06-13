package com.example.wayout_ver_01.Activity.Chat.Chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DTO_chat {
    @Expose
    @SerializedName("room_id") private String room_id;
    @Expose
    @SerializedName("room_name") private String room_name;
    @Expose
    @SerializedName("room_writer") private String room_writer;
    @Expose
    @SerializedName("room_date") private String room_date;
    @Expose
    @SerializedName("room_max") private int room_max;
    @Expose
    @SerializedName("room_info") private String room_info;
    @Expose
    @SerializedName("room_image") private String room_image;
    @Expose
    @SerializedName("user_image") private String user_image;
    @Expose
    @SerializedName("join_number") private int join_number;
    @Expose
    @SerializedName("messages") private ArrayList<DTO_message> messages;
    @Expose
    @SerializedName("last") private String last;
    @Expose
    @SerializedName("members") private ArrayList<DTO_message> members;
    @Expose
    @SerializedName("last_msg") private int last_msg;

    public DTO_chat(String room_id, String room_name, String room_writer, String room_date, int room_max, String room_info, String room_image, String user_image, int join_number, ArrayList<DTO_message> messages, String last) {
        this.room_id = room_id;
        this.room_name = room_name;
        this.room_writer = room_writer;
        this.room_date = room_date;
        this.room_max = room_max;
        this.room_info = room_info;
        this.room_image = room_image;
        this.user_image = user_image;
        this.join_number = join_number;
        this.messages = messages;
        this.last = last;
    }

    public int getLast_msg() {
        return last_msg;
    }

    public void setLast_msg(int last_msg) {
        this.last_msg = last_msg;
    }

    public ArrayList<DTO_message> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<DTO_message> members) {
        this.members = members;
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

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public int getJoin_number() {
        return join_number;
    }

    public void setJoin_number(int join_number) {
        this.join_number = join_number;
    }

    public ArrayList<DTO_message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<DTO_message> messages) {
        this.messages = messages;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
