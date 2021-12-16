package com.pepdeal.in.model.productlistmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductImageData {
    @SerializedName("ImageID")
    @Expose
    private String ImageID;
    @SerializedName("image")
    @Expose
    private String image;

    public String getImageID() {
        return ImageID;
    }

    public void setImageID(String imageID) {
        ImageID = imageID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
