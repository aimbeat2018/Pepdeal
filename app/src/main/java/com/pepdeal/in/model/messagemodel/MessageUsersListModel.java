package com.pepdeal.in.model.messagemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageUsersListModel {
    @SerializedName("msg_count")
    @Expose
    private String msgCount;
    @SerializedName("msgs_id")
    @Expose
    private String msgsId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("shop_id")
    @Expose
    private String shopId;
    @SerializedName("msgs")
    @Expose
    private String msgs;
    @SerializedName("view_status")
    @Expose
    private String viewStatus;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("msg_flag")
    @Expose
    private String msgFlag;
    @SerializedName("shop_name")
    @Expose
    private String shopName;

    public String getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(String msgCount) {
        this.msgCount = msgCount;
    }

    public String getMsgsId() {
        return msgsId;
    }

    public void setMsgsId(String msgsId) {
        this.msgsId = msgsId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getMsgs() {
        return msgs;
    }

    public void setMsgs(String msgs) {
        this.msgs = msgs;
    }

    public String getViewStatus() {
        return viewStatus;
    }

    public void setViewStatus(String viewStatus) {
        this.viewStatus = viewStatus;
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

    public String getMsgFlag() {
        return msgFlag;
    }

    public void setMsgFlag(String msgFlag) {
        this.msgFlag = msgFlag;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
