package com.pepdeal.in.model.homemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomeShopDataModel {
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
    @SerializedName("shop_description")
    @Expose
    private String shopDescription;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("bg_color_id")
    @Expose
    private String bgColorId;
    @SerializedName("bgcolor_name")
    @Expose
    private String bgcolorName;
    @SerializedName("font_size_id")
    @Expose
    private String fontSizeId;
    @SerializedName("fontsize_name")
    @Expose
    private String fontsizeName;
    @SerializedName("font_style_id")
    @Expose
    private String fontStyleId;
    @SerializedName("fontstyle_name")
    @Expose
    private String fontstyleName;
    @SerializedName("font_color_id")
    @Expose
    private String fontColorId;
    @SerializedName("fontcolor_name")
    @Expose
    private String fontcolorName;
    @SerializedName("products_list")
    @Expose
    private List<HomeProductDataModel> productsList = null;
    @SerializedName("super_shop_status")
    @Expose
    private Integer superShopStatus;
    @SerializedName("super_shop_id")
    @Expose
    private String superShopId;

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

    public String getBgColorId() {
        return bgColorId;
    }

    public void setBgColorId(String bgColorId) {
        this.bgColorId = bgColorId;
    }

    public String getBgcolorName() {
        return bgcolorName;
    }

    public void setBgcolorName(String bgcolorName) {
        this.bgcolorName = bgcolorName;
    }

    public String getFontSizeId() {
        return fontSizeId;
    }

    public void setFontSizeId(String fontSizeId) {
        this.fontSizeId = fontSizeId;
    }

    public String getFontsizeName() {
        return fontsizeName;
    }

    public void setFontsizeName(String fontsizeName) {
        this.fontsizeName = fontsizeName;
    }

    public String getFontStyleId() {
        return fontStyleId;
    }

    public void setFontStyleId(String fontStyleId) {
        this.fontStyleId = fontStyleId;
    }

    public String getFontstyleName() {
        return fontstyleName;
    }

    public void setFontstyleName(String fontstyleName) {
        this.fontstyleName = fontstyleName;
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

    public List<HomeProductDataModel> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<HomeProductDataModel> productsList) {
        this.productsList = productsList;
    }

    public Integer getSuperShopStatus() {
        return superShopStatus;
    }

    public void setSuperShopStatus(Integer superShopStatus) {
        this.superShopStatus = superShopStatus;
    }

    public String getSuperShopId() {
        return superShopId;
    }

    public void setSuperShopId(String superShopId) {
        this.superShopId = superShopId;
    }
}
