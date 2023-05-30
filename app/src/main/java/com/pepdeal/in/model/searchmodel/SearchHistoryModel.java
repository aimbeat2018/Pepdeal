package com.pepdeal.in.model.searchmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchHistoryModel {
    @SerializedName("search_id")
    @Expose
    private String searchId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("search_key")
    @Expose
    private String searchKey;

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

}
