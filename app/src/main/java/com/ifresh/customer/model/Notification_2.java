package com.ifresh.customer.model;

public class Notification_2 {
    String mtitle,mbody,user_id,date,time;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    Boolean is_read,is_general;

    public String getMtitle() {
        return mtitle;
    }

    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }

    public String getMbody() {
        return mbody;
    }

    public void setMbody(String mbody) {
        this.mbody = mbody;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Boolean getIs_read() {
        return is_read;
    }

    public void setIs_read(Boolean is_read) {
        this.is_read = is_read;
    }

    public Boolean getIs_general() {
        return is_general;
    }

    public void setIs_general(Boolean is_general) {
        this.is_general = is_general;
    }
}
