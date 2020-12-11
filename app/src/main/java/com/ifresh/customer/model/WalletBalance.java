package com.ifresh.customer.model;

public class WalletBalance {

    private String message, amount, date,actype, expdate;
    private  int wallet_status;

    public String getActype() {
        return actype;
    }

    public String getExpdate() {
        return expdate;
    }

    public int getWallet_status() {
        return wallet_status;
    }

    public void setWallet_status(int wallet_status) {
        this.wallet_status = wallet_status;
    }

    public void setExpdate(String expdate) {
        this.expdate = expdate;
    }

    public void setActype(String actype) {
        this.actype = actype;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
