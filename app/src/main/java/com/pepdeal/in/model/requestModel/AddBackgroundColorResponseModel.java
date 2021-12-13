package com.pepdeal.in.model.requestModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddBackgroundColorResponseModel {

    @SerializedName("bg_color_id")
    @Expose
    private String bgColorId;
    @SerializedName("bg_color_name")
    @Expose
    private String bgColorName;
    @SerializedName("isActive")
    @Expose
    private String isActive;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public String getBgColorId() {
        return bgColorId;
    }

    public void setBgColorId(String bgColorId) {
        this.bgColorId = bgColorId;
    }

    public String getBgColorName() {
        return bgColorName;
    }

    public void setBgColorName(String bgColorName) {
        this.bgColorName = bgColorName;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
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