package com.sunway.lakala.model;

/**
 * Created by LL on 2016/11/27.
 */
public class Order {
    String description;
    String mobile;
    String orderNo;
    String toPOSPayAmount;
    String type;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getToPOSPayAmount() {
        return toPOSPayAmount;
    }

    public void setToPOSPayAmount(String toPOSPayAmount) {
        this.toPOSPayAmount = toPOSPayAmount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
