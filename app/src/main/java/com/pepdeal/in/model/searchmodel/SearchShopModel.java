package com.pepdeal.in.model.searchmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchShopModel {
    @SerializedName("shop_id")
    @Expose
    private String shopId;
    @SerializedName("shop_name")
    @Expose
    private String shopName;
    @SerializedName("shop_mobile_no")
    @Expose
    private String shopMobileNo;
    @SerializedName("shop_address")
    @Expose
    private String shopAddress;
    @SerializedName("shop_address2")
    @Expose
    private String shopAddress2;
    @SerializedName("shop_statusId")
    @Expose
    private String shopStatusId;
    @SerializedName("shop_status")
    @Expose
    private Integer shopStatus;
    @SerializedName("bg_color")
    @Expose
    private String bgColor;
    @SerializedName("font_size_name")
    @Expose
    private String fontSizeName;
    @SerializedName("fontStyle_name")
    @Expose
    private String fontStyleName;
    @SerializedName("font_style_id")
    @Expose
    private String fontStyleId;
    @SerializedName("font_color_id")
    @Expose
    private String fontColorId;
    @SerializedName("fontcolor_name")
    @Expose
    private String fontcolorName;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
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

    public String getShopStatusId() {
        return shopStatusId;
    }

    public void setShopStatusId(String shopStatusId) {
        this.shopStatusId = shopStatusId;
    }

    public Integer getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(Integer shopStatus) {
        this.shopStatus = shopStatus;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getFontSizeName() {
        return fontSizeName;
    }

    public void setFontSizeName(String fontSizeName) {
        this.fontSizeName = fontSizeName;
    }

    public String getFontStyleName() {
        return fontStyleName;
    }

    public void setFontStyleName(String fontStyleName) {
        this.fontStyleName = fontStyleName;
    }

    public String getFontStyleId() {
        return fontStyleId;
    }

    public void setFontStyleId(String fontStyleId) {
        this.fontStyleId = fontStyleId;
    }

    public String getFontColorId() {
        return fontColorId;
    }

    public void setFontColorId(String fontColorId) {
        this.fontColorId = fontColorId;
    }

    public String getFontcolorName() {
        return fontcolorName;
    }

    public void setFontcolorName(String fontcolorName) {
        this.fontcolorName = fontcolorName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getShopAddress2() {
        return shopAddress2;
    }

    public void setShopAddress2(String shopAddress2) {
        this.shopAddress2 = shopAddress2;
    }
}
