package com.pepdeal.in.model.requestModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileRequestModel {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("product_id")
    @Expose
    private String product_id;
    @SerializedName("fav_id")
    @Expose
    private String fav_id;
    @SerializedName("shop_id")
    @Expose
    private String shop_id;
    @SerializedName("msg_flag")
    @Expose
    private String msg_flag;
    @SerializedName("search_key")
    @Expose
    private String search_key;
    @SerializedName("super_id")
    @Expose
    private String super_id;

    public String getSearch_key() {
        return search_key;
    }

    public void setSearch_key(String search_key) {
        this.search_key = search_key;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getFav_id() {
        return fav_id;
    }

    public void setFav_id(String fav_id) {
        this.fav_id = fav_id;
    }

    public String getMsg_flag() {
        return msg_flag;
    }

    public void setMsg_flag(String msg_flag) {
        this.msg_flag = msg_flag;
    }

    public String getSuper_id() {
        return super_id;
    }

    public void setSuper_id(String super_id) {
        this.super_id = super_id;
    }
}
