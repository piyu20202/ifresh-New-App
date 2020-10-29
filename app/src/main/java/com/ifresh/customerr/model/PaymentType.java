package com.ifresh.customerr.model;

public class PaymentType {
    String id, title, abv;

    public PaymentType(String id, String title, String abv) {
        this.id = id;
        this.title = title;
        this.abv = abv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbv() {
        return abv;
    }

    public void setAbv(String abv) {
        this.abv = abv;
    }
}
