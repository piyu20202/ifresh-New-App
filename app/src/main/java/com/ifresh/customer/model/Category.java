package com.ifresh.customer.model;

import java.util.ArrayList;

public class Category {
    private String id, name, subtitle, image, category_id, payType, status, date, amountReq, month, year, email;
    private String style;
    private ArrayList<ModelProduct> productList;
    private Boolean allow_upload,is_comingsoon;

    public Category() {
    }

    public Boolean getIs_comingsoon() {
        return is_comingsoon;
    }

    public Boolean getAllow_upload() {
        return allow_upload;
    }

    public void setAllow_upload(Boolean allow_upload) {
        this.allow_upload = allow_upload;
    }

    public Category(String id, String name, String subtitle, String image, Boolean allow_upload) {
        this.id = id;
        this.name = name;
        this.subtitle = subtitle;
        this.image = image;
        this.allow_upload = allow_upload;
    }

    public Category(String id, String name, String subtitle, String image, Boolean allow_upload,Boolean is_comingsoon) {
        this.id = id;
        this.name = name;
        this.subtitle = subtitle;
        this.image = image;
        this.allow_upload = allow_upload;
        this.is_comingsoon = is_comingsoon;


    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAmountReq(String amountReq) {
        this.amountReq = amountReq;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getImage() {
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public ArrayList<ModelProduct> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<ModelProduct> productList) {
        this.productList = productList;
    }

    public String getPayType() {
        return payType;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public String getAmountReq() {
        return amountReq;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
