package com.pepdeal.in.model.shopdetailsmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShopDetailsDataModel {
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
    @SerializedName("bgcolor_name")
    @Expose
    private String bgcolorName;
    @SerializedName("bgcolor_name2")
    @Expose
    private String bgcolorName2;
    @SerializedName("fontsize_name")
    @Expose
    private String fontsizeName;
    @SerializedName("fontstyle_name")
    @Expose
    private String fontstyleName;
    @SerializedName("font_style_id")
    @Expose
    private String fontStyleId;
    @SerializedName("super_shop_id")
    @Expose
    private String superShopId;
    @SerializedName("super_shop_tatus")
    @Expose
    private String superShopTatus;
    @SerializedName("shop_description")
    @Expose
    private String shopDescription;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("fontcolor_name")
    @Expose
    private String fontColorName;
    @SerializedName("fontcolor_name2")
    @Expose
    private String fontColorName2;

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

    public String getBgcolorName() {
        return bgcolorName;
    }

    public void setBgcolorName(String bgcolorName) {
        this.bgcolorName = bgcolorName;
    }

    public String getFontsizeName() {
        return fontsizeName;
    }

    public void setFontsizeName(String fontsizeName) {
        this.fontsizeName = fontsizeName;
    }

    public String getFontstyleName() {
        return fontstyleName;
    }

    public void setFontstyleName(String fontstyleName) {
        this.fontstyleName = fontstyleName;
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

    public String getShopDescription() {
        return shopDescription;
    }

    public void setShopDescription(String shopDescription) {
        this.shopDescription = shopDescription;
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

    public String getFontColorName() {
        return fontColorName;
    }

    public void setFontColorName(String fontColorName) {
        this.fontColorName = fontColorName;
    }

    public String getFontStyleId() {
        return fontStyleId;
    }

    public void setFontStyleId(String fontStyleId) {
        this.fontStyleId = fontStyleId;
    }

    public String getBgcolorName2() {
        return bgcolorName2;
    }

    public void setBgcolorName2(String bgcolorName2) {
        this.bgcolorName2 = bgcolorName2;
    }

    public String getFontColorName2() {
        return fontColorName2;
    }

    public void setFontColorName2(String fontColorName2) {
        this.fontColorName2 = fontColorName2;
    }
}
