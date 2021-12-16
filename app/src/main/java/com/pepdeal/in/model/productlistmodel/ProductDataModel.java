package com.pepdeal.in.model.productlistmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductDataModel {

    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("IMges")
    @Expose
    private List<ProductImageData> iMges = null;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("brand_id")
    @Expose
    private String brandId;
    @SerializedName("brand_name")
    @Expose
    private String brandName;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("warranty")
    @Expose
    private String warranty;
    @SerializedName("size_id")
    @Expose
    private String sizeId;
    @SerializedName("size_name")
    @Expose
    private String sizeName;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("search_tags")
    @Expose
    private String searchTags;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("discount_mrp")
    @Expose
    private String discountMrp;
    @SerializedName("selling_price")
    @Expose
    private String sellingPrice;
    @SerializedName("isActive")
    @Expose
    private String isActive;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public List<ProductImageData> getIMges() {
        return iMges;
    }

    public void setIMges(List<ProductImageData> iMges) {
        this.iMges = iMges;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
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

    public String getDiscountMrp() {
        return discountMrp;
    }

    public void setDiscountMrp(String discountMrp) {
        this.discountMrp = discountMrp;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
