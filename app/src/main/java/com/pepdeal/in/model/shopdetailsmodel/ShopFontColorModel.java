package com.pepdeal.in.model.shopdetailsmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShopFontColorModel {
    @SerializedName("font_color_id")
    @Expose
    private String fontColorId;
    @SerializedName("font_color_name")
    @Expose
    private String fontColorName;
    @SerializedName("font_color_name2")
    @Expose
    private String fontColorName2;

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
}
