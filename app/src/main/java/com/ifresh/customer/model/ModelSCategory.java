package com.ifresh.customer.model;



public class ModelSCategory {

    private String title;
    private String is_active;
    private String id;
    private String catagory_img;

    public String getCatagory_img() {
        return catagory_img;
    }

    public void setCatagory_img(String catagory_img) {
        this.catagory_img = catagory_img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
