package com.pepdeal.in.model.searchmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchDataBoth {
    @SerializedName("productData")
    @Expose
    private List<SearchProductModel> productData;
    @SerializedName("shopData")
    @Expose
    private List<SearchShopModel> shopData;

    public List<SearchProductModel> getProductData() {
        return productData;
    }

    public void setProductData(List<SearchProductModel> productData) {
        this.productData = productData;
    }

    public List<SearchShopModel> getShopData() {
        return shopData;
    }

    public void setShopData(List<SearchShopModel> shopData) {
        this.shopData = shopData;
    }
}
