package com.pepdeal.in.model.productdetailsmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShopDetailsModel {
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
    @SerializedName("shop_description")
    @Expose
    private String shopDescription;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("bg_color_id")
    @Expose
    private String bgColorId;
    @SerializedName("bg_color_name")
    @Expose
    private String bgColorName;
    @SerializedName("bgcolor_name2")
    @Expose
    private String bgcolorName2;
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
    @SerializedName("fontcolor_name2")
    @Expose
    private String fontcolorName2;
    @SerializedName("font_style_id")
    @Expose
    private String fontStyleId;
    @SerializedName("font_style_name")
    @Expose
    private String fontStyleName;
    @SerializedName("super_shop_id")
    @Expose
    private String superShopId;
    @SerializedName("super_shop_tatus")
    @Expose
    private String superShopTatus;

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getBgcolorName2() {
        return bgcolorName2;
    }

    public void setBgcolorName2(String bgcolorName2) {
        this.bgcolorName2 = bgcolorName2;
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

    public String getFontcolorName2() {
        return fontcolorName2;
    }

    public void setFontcolorName2(String fontcolorName2) {
        this.fontcolorName2 = fontcolorName2;
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

    public String getSuperShopId() {
        return superShopId;
    }

    public void setSuperShopId(String superShopId) {
        this.superShopId = superShopId;
    }

    public String getSuperShopTatus() {
        return superShopTatus;
    }

    public void setSuperShopTatus(String superShopTatus) {
        this.superShopTatus = superShopTatus;
    }

    public String getShopAddress2() {
        return shopAddress2;
    }

    public void setShopAddress2(String shopAddress2) {
        this.shopAddress2 = shopAddress2;
    }
}
