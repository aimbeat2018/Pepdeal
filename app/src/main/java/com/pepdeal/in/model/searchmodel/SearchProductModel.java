package com.pepdeal.in.model.searchmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchProductModel {
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("product_image")
    @Expose
    private String productImage;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("description2")
    @Expose
    private String description2;
    @SerializedName("specification")
    @Expose
    private String specification;
    @SerializedName("warranty")
    @Expose
    private String warranty;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("search_tags")
    @Expose
    private String searchTags;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("selling_price")
    @Expose
    private String sellingPrice;
    @SerializedName("discount_mrp")
    @Expose
    private String discountMrp;
    @SerializedName("fav_id")
    @Expose
    private String favId;
    @SerializedName("fav_status")
    @Expose
    private Integer favStatus;
    @SerializedName("on_call")
    @Expose
    private String onCall;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSearchTags() {
        return searchTags;
    }

    public void setSearchTags(String searchTags) {
        this.searchTags = searchTags;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getDiscountMrp() {
        return discountMrp;
    }

    public void setDiscountMrp(String discountMrp) {
        this.discountMrp = discountMrp;
    }

    public String getFavId() {
        return favId;
    }

    public void setFavId(String favId) {
        this.favId = favId;
    }

    public Integer getFavStatus() {
        return favStatus;
    }

    public void setFavStatus(Integer favStatus) {
        this.favStatus = favStatus;
    }

    public String getOnCall() {
        return onCall;
    }

    public void setOnCall(String onCall) {
        this.onCall = onCall;
    }
}
