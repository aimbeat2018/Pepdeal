package com.pepdeal.in.model.requestModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SellerTicketStatusModel {
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("ticket_id")
    @Expose
    private String ticketId;
    @SerializedName("ticket_status")
    @Expose
    private String ticketStatus;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("flag")
    @Expose
    private String flag;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
