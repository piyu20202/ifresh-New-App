package com.ifresh.customer.model;

public class Slot {
    public String id, title, fromTime, toTime, lastOrderTime;
    boolean isSlotAvailable, is_timeslotAvailable;

    public boolean isIs_timeslotAvailable() {
        return is_timeslotAvailable;
    }

    /*public Slot(String id, String title, String lastOrderTime) {
        this.id = id;
        this.title = title;
        this.lastOrderTime = lastOrderTime;
    }*/

    public Slot(String id, String title, boolean is_timeslotAvailable) {
        this.id = id;
        this.title = title;
        this.is_timeslotAvailable = is_timeslotAvailable;
    }




    public boolean isSlotAvailable() {
        return isSlotAvailable;
    }

    public void setSlotAvailable(boolean slotAvailable) {
        isSlotAvailable = slotAvailable;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFromTime() {
        return fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public String getLastOrderTime() {
        return lastOrderTime;
    }
}
