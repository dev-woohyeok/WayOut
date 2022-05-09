package com.example.wayout_ver_01.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JoinRequest {

    @Expose
    @SerializedName("userId")
    private String userId;

    @Expose
    @SerializedName("userPw")
    private String userPw;

    @Expose
    @SerializedName("userIndex")
    private int userIndex;

    @Expose
    @SerializedName("userNick")
    private String userNick;

    @Expose
    @SerializedName("userPhone")
    private String userPhone;

    @Expose
    @SerializedName("status")
    private Boolean status;

    @Expose
    @SerializedName("message")
    private String message;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public int getUserIndex() {
        return userIndex;
    }

    public void setUserIndex(int userIndex) {
        this.userIndex = userIndex;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
