package com.example.wayout_ver_01.Activity.Chat.Chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DTO_message {
    @Expose
    @SerializedName("code") private String code;
    @Expose
    @SerializedName("room") private String room;
    @Expose
    @SerializedName("name") private String name;
    @Expose
    @SerializedName("message") private String message;
    @Expose
    @SerializedName("image") private String image;
    @Expose
    @SerializedName("date") private String date;
    @Expose
    @SerializedName("type") private String type;
    @Expose
    @SerializedName("IO") private int IO;
    @Expose
    @SerializedName("member") private int member;
    @Expose
    @SerializedName("last_id") private String last_id;
    @Expose
    @SerializedName("room_name") private String room_name;

    public DTO_message(String code, String room, String name, String message, String image, String date, String type, String room_name){
    }

    public DTO_message(String code, String room, String name, String message, String image, String date, String type) {
        this.code = code;
        this.room = room;
        this.name = name;
        this.message = message;
        this.image = image;
        this.date = date;
        this.type = type;
    }

    public DTO_message(String code, String room, String name, String message, String image, String date, String type, int member,String room_name) {
        this.code = code;
        this.room = room;
        this.name = name;
        this.message = message;
        this.image = image;
        this.date = date;
        this.type = type;
        this.member = member;
        this.room_name = room_name;
    }

    public DTO_message(String code, String room, String name, String message, String image, String date, String type, int IO, int member,String room_name) {
        this.code = code;
        this.room = room;
        this.name = name;
        this.message = message;
        this.image = image;
        this.date = date;
        this.type = type;
        this.IO = IO;
        this.member = member;
        this.room_name = room_name;
    }

    public DTO_message() {

    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getLast_id() {
        return last_id;
    }

    public void setLast_id(String last_id) {
        this.last_id = last_id;
    }

    public int getMember() {
        return member;
    }

    public void setMember(int member) {
        this.member = member;
    }

    public int getIO() {
        return IO;
    }

    public void setIO(int IO) {
        this.IO = IO;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
