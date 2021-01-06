package com.ifresh.customer.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderTracker_2 implements Serializable {

    public String show_id,username, id, user_id, order_id, product_variant_id, quantity, price, discount, order_type,dPercent, dAmount, sub_total, tax_amt, tax_percent, deliver_by, date_added, name, image, measurement, unit, status, statusdate, mobile, delivery_charge, payment_method, address, final_total, total, walletBalance, promoCode, promoDiscount, activeStatus, activeStatusDate;
    public ArrayList<OrderTracker_2> orderStatusArrayList;
    public ArrayList<OrderTracker_2> itemsList;


    public OrderTracker_2(String show_id,String user_id, String order_id, String date_added, String status, String statusdate, ArrayList<OrderTracker_2> orderStatusArrayList, String mobile, String delivery_charge, String payment_method, String address, String total, String final_total, String tax_amt, String tax_percent, String walletBalance, String promoCode, String promoDiscount, String dPercent, String order_type,String dAmount, String username, ArrayList<OrderTracker_2> itemsList) {
        this.show_id=show_id;
        this.user_id = user_id;
        this.order_id = order_id;
        this.date_added = date_added;
        this.status = status;
        this.statusdate = statusdate;
        this.orderStatusArrayList = orderStatusArrayList;
        this.mobile = mobile;
        this.delivery_charge = delivery_charge;
        this.payment_method = payment_method;
        this.address = address;
        this.total = total;
        this.final_total = final_total;
        this.tax_amt = tax_amt;
        this.tax_percent = tax_percent;
        this.walletBalance = walletBalance;
        this.promoCode = promoCode;
        this.promoDiscount = promoDiscount;
        this.dAmount = dAmount;
        this.order_type = order_type;
        this.dPercent = dPercent;
        this.username = username;
        this.itemsList = itemsList;

        Log.d("perent==>",""+ dPercent);


    }

    public String getShow_id() {
        return show_id;
    }

    public void setShow_id(String show_id) {
        this.show_id = show_id;
    }

    public OrderTracker_2(String id, String order_id, String product_variant_id, String quantity, String price, String discount,  String order_type ,String sub_total, String deliver_by, String name, String image, String measurement, String unit, String payment_method, String activeStatus, String activeStatusDate, ArrayList<OrderTracker_2> orderStatusArrayList) {
        this.id = id;
        this.order_id = order_id;
        this.product_variant_id = product_variant_id;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.order_type = order_type;
        this.sub_total = sub_total;

        this.deliver_by = deliver_by;
        this.name = name;
        this.image = image;
        this.measurement = measurement;
        this.unit = unit;
        this.payment_method = payment_method;
        this.activeStatus = activeStatus;
        this.activeStatusDate = activeStatusDate;
        this.orderStatusArrayList = orderStatusArrayList;
    }

    public String getdPercent() {
        return dPercent;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getdAmount() {
        return dAmount;
    }

    public String getTotal() {
        return total;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public String getPromoDiscount() {
        return promoDiscount;
    }

    public String getTax_amt() {
        return tax_amt;
    }

    public String getTax_percent() {
        return tax_percent;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public String getActiveStatusDate() {
        return activeStatusDate;
    }

    public ArrayList<OrderTracker_2> getItemsList() {
        return itemsList;
    }

    public String getUsername() {
        return username;
    }

    public String getMobile() {
        return mobile;
    }

    public String getDelivery_charge() {
        return delivery_charge;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public String getAddress() {
        return address;
    }

    public String getFinal_total() {
        return final_total;
    }

    public ArrayList<OrderTracker_2> getOrderStatusArrayList() {
        return orderStatusArrayList;
    }

    public OrderTracker_2(String status, String statusdate) {
        this.status = status;
        this.statusdate = statusdate;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getOrder_id() {
        return order_id;
    }


    public String getProduct_variant_id() {
        return product_variant_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getDiscount() {
        return discount;
    }

    public String getSub_total() {
        return sub_total;
    }

    public String getDeliver_by() {
        return deliver_by;
    }

    public String getDate_added() {
        return date_added;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getMeasurement() {
        return measurement;
    }

    public String getUnit() {
        return unit;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusdate() {
        return statusdate;
    }
}
