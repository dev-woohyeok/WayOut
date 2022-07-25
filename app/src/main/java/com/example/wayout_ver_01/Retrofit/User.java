package com.example.wayout_ver_01.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import okhttp3.MultipartBody;

public class User {

    @Expose
    @SerializedName("userId")
    private String userId;

    @Expose
    @SerializedName("userPw")
    private String userPw;

    @Expose
    @SerializedName("follow_num") private String follow_num;

    @Expose
    @SerializedName("following_num") private String following_num;

    @Expose
    @SerializedName("userNick")
    private String userNick;

    @Expose
    @SerializedName("userIndex")
    private Integer userIndex;

    @Expose
    @SerializedName("userNewPw")
    private String userNewPw;

    @Expose
    @SerializedName("userPhone")
    private String userPhone;

    @Expose
    @SerializedName("code")
    private String code;

    @Expose
    @SerializedName("codeIndex")
    private String codeIndex;

    @Expose
    @SerializedName("file")
    private MultipartBody.Part file;

    @Expose
    @SerializedName("status")
    private Boolean status;

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("userProfile")
    private String userProfile;

    public String getFollow_num() {
        return follow_num;
    }

    public void setFollow_num(String follow_num) {
        this.follow_num = follow_num;
    }

    public String getFollowing_num() {
        return following_num;
    }

    public void setFollowing_num(String following_num) {
        this.following_num = following_num;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public MultipartBody.Part getFile() {
        return file;
    }

    public void setFile(MultipartBody.Part file) {
        this.file = file;
    }

    public String getCodeIndex() {
        return codeIndex;
    }

    public void setCodeIndex(String codeIndex) {
        this.codeIndex = codeIndex;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

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

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public Integer getUserIndex() {
        return userIndex;
    }

    public void setUserIndex(Integer userIndex) {
        this.userIndex = userIndex;
    }

    public String getUserNewPw() {
        return userNewPw;
    }

    public void setUserNewPw(String userNewPw) {
        this.userNewPw = userNewPw;
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
