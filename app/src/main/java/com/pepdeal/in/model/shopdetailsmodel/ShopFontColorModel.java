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
}
