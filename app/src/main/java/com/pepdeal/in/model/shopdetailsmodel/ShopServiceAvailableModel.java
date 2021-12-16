package com.pepdeal.in.model.shopdetailsmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShopServiceAvailableModel {
    @SerializedName("shop_status_id")
    @Expose
    private String shopStatusId;
    @SerializedName("shop_id")
    @Expose
    private String shopId;
    @SerializedName("cash_on_delivery")
    @Expose
    private String cashOnDelivery;
    @SerializedName("door_step")
    @Expose
    private String doorStep;
    @SerializedName("home_delivery")
    @Expose
    private String homeDelivery;
    @SerializedName("live_demo")
    @Expose
    private String liveDemo;
    @SerializedName("offers")
    @Expose
    private String offers;
    @SerializedName("bargain")
    @Expose
    private String bargain;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public String getShopStatusId() {
        return shopStatusId;
    }

    public void setShopStatusId(String shopStatusId) {
        this.shopStatusId = shopStatusId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getCashOnDelivery() {
        return cashOnDelivery;
    }

    public void setCashOnDelivery(String cashOnDelivery) {
        this.cashOnDelivery = cashOnDelivery;
    }

    public String getDoorStep() {
        return doorStep;
    }

    public void setDoorStep(String doorStep) {
        this.doorStep = doorStep;
    }

    public String getHomeDelivery() {
        return homeDelivery;
    }

    public void setHomeDelivery(String homeDelivery) {
        this.homeDelivery = homeDelivery;
    }

    public String getLiveDemo() {
        return liveDemo;
    }

    public void setLiveDemo(String liveDemo) {
        this.liveDemo = liveDemo;
    }

    public String getOffers() {
        return offers;
    }

    public void setOffers(String offers) {
        this.offers = offers;
    }

    public String getBargain() {
        return bargain;
    }

    public void setBargain(String bargain) {
        this.bargain = bargain;
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
}
