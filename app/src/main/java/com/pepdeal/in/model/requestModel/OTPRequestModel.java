package com.pepdeal.in.model.requestModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OTPRequestModel {

    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    private List<OTPRequestModel> data = null;


    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public List<OTPRequestModel> getData() {

        return data;
    }



}
