package com.pepdeal.in.model.supershopmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SuperShopDataModel {

    @SerializedName("super_id")
    @Expose
    private String superId;
    @SerializedName("shop_id")
    @Expose
    private String shopId;
    @SerializedName("super_created_at")
    @Expose
    private String superCreatedAt;
    @SerializedName("shop_name")
    @Expose
    private String shopName;
    @SerializedName("shop_address")
    @Expose
    private String shopAddress;
    @SerializedName("shop_mobile_no")
    @Expose
    private String shopMobileNo;
    @SerializedName("bg_color_id")
    @Expose
    private String bgColorId;
    @SerializedName("bg_color_name")
    @Expose
    private String bgColorName;
    @SerializedName("font_size_id")
    @Expose
    private String fontSizeId;
    @SerializedName("font_size_name")
    @Expose
    private String fontSizeName;
    @SerializedName("font_style_id")
    @Expose
    private String fontStyleId;
    @SerializedName("font_style_name")
    @Expose
    private String fontStyleName;

    public String getSuperId() {
        return superId;
    }

    public void setSuperId(String superId) {
        this.superId = superId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getSuperCreatedAt() {
        return superCreatedAt;
    }

    public void setSuperCreatedAt(String superCreatedAt) {
        this.superCreatedAt = superCreatedAt;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopMobileNo() {
        return shopMobileNo;
    }

    public void setShopMobileNo(String shopMobileNo) {
        this.shopMobileNo = shopMobileNo;
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
}
