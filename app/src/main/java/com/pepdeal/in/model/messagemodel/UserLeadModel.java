package com.pepdeal.in.model.messagemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserLeadModel {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("shop_id")
    @Expose
    private String shopId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("flag")
    @Expose
    private String flag;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("device_token")
    @Expose
    private String deviceToken;
    @SerializedName("email_id")
    @Expose
    private String emailId;
    @SerializedName("isActive")
    @Expose
    private String isActive;
    @SerializedName("user_status")
    @Expose
    private String userStatus;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
