package com.ifresh.customer.adapter;

public class PromoCode {
    String c_id, c_title, c_url, start_date, end_date,c_is_active;
    Integer c_useno, c_disc_in, c_disc_value;
    Boolean c_has_expiry,  c_reuse;

    public String getC_is_active() {
        return c_is_active;
    }

    public void setC_is_active(String c_is_active) {
        this.c_is_active = c_is_active;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getC_title() {
        return c_title;
    }

    public void setC_title(String c_title) {
        this.c_title = c_title;
    }

    public String getC_url() {
        return c_url;
    }

    public void setC_url(String c_url) {
        this.c_url = c_url;
    }

    public Integer getC_useno() {
        return c_useno;
    }

    public void setC_useno(Integer c_useno) {
        this.c_useno = c_useno;
    }

    public Integer getC_disc_in() {
        return c_disc_in;
    }

    public void setC_disc_in(Integer c_disc_in) {
        this.c_disc_in = c_disc_in;
    }

    public Integer getC_disc_value() {
        return c_disc_value;
    }

    public void setC_disc_value(Integer c_disc_value) {
        this.c_disc_value = c_disc_value;
    }

    public Boolean getC_has_expiry() {
        return c_has_expiry;
    }

    public void setC_has_expiry(Boolean c_has_expiry) {
        this.c_has_expiry = c_has_expiry;
    }


    public Boolean getC_reuse() {
        return c_reuse;
    }

    public void setC_reuse(Boolean c_reuse) {
        this.c_reuse = c_reuse;
    }
}
