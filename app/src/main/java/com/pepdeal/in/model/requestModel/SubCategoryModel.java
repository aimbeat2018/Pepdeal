package com.pepdeal.in.model.requestModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubCategoryModel {
    @SerializedName("sub_category_id")
    @Expose
    private String subCategoryId;
    @SerializedName("sub_category_name")
    @Expose
    private String subCategoryName;
    @SerializedName("sub_category_images")
    @Expose
    private String subCategoryImages;

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getSubCategoryImages() {
        return subCategoryImages;
    }

    public void setSubCategoryImages(String subCategoryImages) {
        this.subCategoryImages = subCategoryImages;
    }
}
