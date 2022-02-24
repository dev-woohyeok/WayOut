package com.example.wayout_ver_01;

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
    @SerializedName("files")
    private MultipartBody.Part files;

    @Expose
    @SerializedName("status")
    private Boolean status;

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("userProfile")
    private String userProfile;

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public MultipartBody.Part getFiles() {
        return files;
    }

    public void setFiles(MultipartBody.Part files) {
        this.files = files;
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
