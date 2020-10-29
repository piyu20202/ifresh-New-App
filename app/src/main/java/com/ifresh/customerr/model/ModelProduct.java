package com.ifresh.customerr.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelProduct implements Serializable {

    String productId, title, catId, frProductId, franchiseId, description, product_img, product_img_id;
    Boolean isPacket;
    private ArrayList<ModelProductVariation> priceVariations;
    public double globalStock;

    public ArrayList<ModelProductVariation> getPriceVariations() {
        return priceVariations;
    }

    public void setPriceVariations(ArrayList<ModelProductVariation> priceVariations) {
        this.priceVariations = priceVariations;
    }

    public double getGlobalStock() {
        return globalStock;
    }

    public void setGlobalStock(double globalStock) {
        this.globalStock = globalStock;
    }

    public String getProduct_img_id() {
        return product_img_id;
    }

    public void setProduct_img_id(String product_img_id) {
        this.product_img_id = product_img_id;
    }

    public Boolean getPacket() {
        return isPacket;
    }

    public void setPacket(Boolean packet) {
        isPacket = packet;
    }

    public String getId() {
        return productId;
    }

    public void setId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return title;
    }

    public void setName(String title) {
        this.title = title;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getFrProductId() {
        return frProductId;
    }

    public void setFrProductId(String frProductId) {
        this.frProductId = frProductId;
    }

    public String getFranchiseId() {
        return franchiseId;
    }

    public void setFranchiseId(String franchiseId) {
        this.franchiseId = franchiseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProduct_img() {
        return product_img;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
    }
}
