package com.sunway.lakala.model;

import java.util.List;

/**
 * Created by LL on 2016/11/30.
 */
public class LoginData {
    String token;
    Store loginStore;
    List<Store> stores;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Store getLoginStore() {
        return loginStore;
    }

    public void setLoginStore(Store loginStore) {
        this.loginStore = loginStore;
    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }
}
