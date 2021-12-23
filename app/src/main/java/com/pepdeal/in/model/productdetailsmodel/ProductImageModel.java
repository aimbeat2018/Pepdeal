package com.pepdeal.in.model.productdetailsmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductImageModel {
    @SerializedName("product_image")
    @Expose
    private String productImage;
    @SerializedName("product_imageID")
    @Expose
    private String productImageID;

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductImageID() {
        return productImageID;
    }

    public void setProductImageID(String productImageID) {
        this.productImageID = productImageID;
    }
}
