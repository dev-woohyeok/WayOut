package com.example.wayout_ver_01.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DTO_address {
    @Expose
    @SerializedName("status") private String status;

    @Expose
    @SerializedName("addresses") private ArrayList<DTO_addresses> addresses;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<DTO_addresses> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<DTO_addresses> addresses) {
        this.addresses = addresses;
    }
//    public DTO_addresses getAddresses() {
//        return addresses;
//    }
//
//    public void setAddresses(DTO_addresses addresses) {
//        this.addresses = addresses;
//    }
}
