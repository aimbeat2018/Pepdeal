package com.pepdeal.in.model.requestModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HelpRequestModel {
    @SerializedName("enquiry_name")
    @Expose
    private String enquiryName;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("email_id")
    @Expose
    private String emailId;
    @SerializedName("query")
    @Expose
    private String query;

    public String getEnquiryName() {
        return enquiryName;
    }

    public void setEnquiryName(String enquiryName) {
        this.enquiryName = enquiryName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
