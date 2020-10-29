package com.ifresh.customerr.model;

import java.io.Serializable;

public class ModelProductVariation implements Serializable {
    String measurment, unit, price, disc_price,  description,catId, frproductId, productId,franchiseId,is_active, id, stock, discountpercent, serve_for,type;
    Integer qty;
    private double totalprice;

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServe_for() {
        return serve_for;
    }

    public void setServe_for(String serve_for) {
        this.serve_for = serve_for;
    }

    public String getDiscountpercent() {
        return discountpercent;
    }

    public void setDiscountpercent(String discountpercent) {
        this.discountpercent = discountpercent;
    }

    public String getId() {
        return id;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeasurement() {
        return measurment;
    }

    public void setMeasurement(String measurment) {
        this.measurment = measurment;
    }

    public String getMeasurement_unit_name() {
        return unit;
    }

    public void setMeasurement_unit_name(String unit) {
        this.unit = unit;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscounted_price() {
        return disc_price;
    }

    public void setDiscounted_price(String disc_price) {
        this.disc_price = disc_price;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getFrproductId() {
        return frproductId;
    }

    public void setFrproductId(String frproductId) {
        this.frproductId = frproductId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getFranchiseId() {
        return franchiseId;
    }

    public void setFranchiseId(String franchiseId) {
        this.franchiseId = franchiseId;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }
}
