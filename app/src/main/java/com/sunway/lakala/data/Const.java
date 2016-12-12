package com.sunway.lakala.data;

/**
 * Created by LL on 2016/11/29.
 */
public class Const {

    private static final String BaseServer = "http://121.196.237.206:9095/ldj-store";

    public static class Request{
        public static final String login = BaseServer + "/operators/login";
        public static final String storeLogin = BaseServer  + "/operators/login/store";
        public static final String payOrders = BaseServer + "/payment/pos/list";
        public static final String confirmPay = BaseServer + "/payment/pos/confirm";



    }
}
