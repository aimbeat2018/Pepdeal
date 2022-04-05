package com.pepdeal.in.model.messagemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShopLeadModel {
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
    @SerializedName("shop_name")
    @Expose
    private String shopName;
    @SerializedName("shop_mobile_no")
    @Expose
    private String shopMobileNo;
    @SerializedName("shop_address")
    @Expose
    private String shopAddress;
    @SerializedName("shop_description")
    @Expose
    private String shopDescription;
    @SerializedName("shop_flag")
    @Expose
    private String shopFlag;
    @SerializedName("bg_color_id")
    @Expose
    private String bgColorId;
    @SerializedName("bg_color_name")
    @Expose
    private String bgColorName;
    @SerializedName("bg_color_name2")
    @Expose
    private String bgColorName2;
    @SerializedName("isActive")
    @Expose
    private String isActive;
    @SerializedName("font_size_id")
    @Expose
    private String fontSizeId;
    @SerializedName("font_size_name")
    @Expose
    private String fontSizeName;
    @SerializedName("font_color_id")
    @Expose
    private String fontColorId;
    @SerializedName("font_color_name")
    @Expose
    private String fontColorName;
    @SerializedName("font_color_name2")
    @Expose
    private String fontColorName2;
    @SerializedName("font_style_id")
    @Expose
    private String fontStyleId;
    @SerializedName("font_style_name")
    @Expose
    private String fontStyleName;
    @SerializedName("city_name")
    @Expose
    private String cityName;
    @SerializedName("state_name")
    @Expose
    private String stateName;

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

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopMobileNo() {
        return shopMobileNo;
    }

    public void setShopMobileNo(String shopMobileNo) {
        this.shopMobileNo = shopMobileNo;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopDescription() {
        return shopDescription;
    }

    public void setShopDescription(String shopDescription) {
        this.shopDescription = shopDescription;
    }

    public String getShopFlag() {
        return shopFlag;
    }

    public void setShopFlag(String shopFlag) {
        this.shopFlag = shopFlag;
    }

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

    public String getBgColorName2() {
        return bgColorName2;
    }

    public void setBgColorName2(String bgColorName2) {
        this.bgColorName2 = bgColorName2;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getFontSizeId() {
        return fontSizeId;
    }

    public void setFontSizeId(String fontSizeId) {
        this.fontSizeId = fontSizeId;
    }

    public String getFontSizeName() {
        return fontSizeName;
    }

    public void setFontSizeName(String fontSizeName) {
        this.fontSizeName = fontSizeName;
    }

    public String getFontColorId() {
        return fontColorId;
    }

    public void setFontColorId(String fontColorId) {
        this.fontColorId = fontColorId;
    }

    public String getFontColorName() {
        return fontColorName;
    }

    public void setFontColorName(String fontColorName) {
        this.fontColorName = fontColorName;
    }

    public String getFontColorName2() {
        return fontColorName2;
    }

    public void setFontColorName2(String fontColorName2) {
        this.fontColorName2 = fontColorName2;
    }

    public String getFontStyleId() {
        return fontStyleId;
    }

    public void setFontStyleId(String fontStyleId) {
        this.fontStyleId = fontStyleId;
    }

    public String getFontStyleName() {
        return fontStyleName;
    }

    public void setFontStyleName(String fontStyleName) {
        this.fontStyleName = fontStyleName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
