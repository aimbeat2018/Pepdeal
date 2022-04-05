package com.pepdeal.in.model.ticketmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TicketDataModel {

    @SerializedName("ticket_id")
    @Expose
    private String ticketId;
    @SerializedName("ticket_status")
    @Expose
    private String ticketStatus;
    @SerializedName("ticket_created_at")
    @Expose
    private String ticketCreatedAt;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("product_image")
    @Expose
    private String productImage;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("shop_name")
    @Expose
    private String shopName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("discount_mrp")
    @Expose
    private String discountMrp;
    @SerializedName("selling_price")
    @Expose
    private String sellingPrice;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("created")
    @Expose
    private String created;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getTicketCreatedAt() {
        return ticketCreatedAt;
    }

    public void setTicketCreatedAt(String ticketCreatedAt) {
        this.ticketCreatedAt = ticketCreatedAt;
    }

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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
