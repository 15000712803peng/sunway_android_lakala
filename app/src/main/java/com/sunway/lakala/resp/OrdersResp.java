package com.sunway.lakala.resp;

import com.sunway.lakala.model.Order;

import java.util.List;

/**
 * Created by LL on 2016/12/1.
 */
public class OrdersResp {
    
    List<Order> data;

    public List<Order> getData() {
        return data;
    }

    public void setData(List<Order> data) {
        this.data = data;
    }
}
