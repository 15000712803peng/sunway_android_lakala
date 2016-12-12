package com.sunway.lakala.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sunway.lakala.model.Results;
import com.sunway.lakala.model.SuccResult;

import java.util.ArrayList;

public class DataManager {

    private static DataManager instance;

    private SharedPreferences userInfos;

    private DataManager(Context context) {
        userInfos = context.getSharedPreferences("order_data", 0);
    }

    public void saveAccount(String account){
        userInfos.edit().putString("account",account).commit();

    }

    public String getAccount(){
     return    userInfos.getString("account", "");
    }

    public void savePwd(String pwd){
        userInfos.edit().putString("pwd", pwd).commit();
    }

    public String getPwd(){
        return userInfos.getString("pwd","");
    }

    public static synchronized DataManager getInstance(Context context) {

        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }

    public Results getAllResults() {
        String json = userInfos.getString("results", "");
        if (TextUtils.isEmpty(json)){
            Results results = new Results();
            results.setResults(new ArrayList<SuccResult>());
            return results;
        }
        return (Results) JsonParser.jsonToObject(json, Results.class);
    }

    public void saveResults(Results results){
        userInfos.edit().putString("results",JsonParser.ObjectToJsonStr(results)).commit();
    }

    public void saveResult(SuccResult result){
        Results results = getAllResults();
        if(results.getResults().contains(result)){
            return;
        }else {
            results.getResults().add(result);
        }
        saveResults(results);
    }

    public void delResult(SuccResult result){
        Results results = getAllResults();
        results.getResults().remove(result);
        saveResults(results);
    }

    public void saveToken(String token){
        userInfos.edit().putString("token",token).commit();
    }

    public String getToken(){
        return userInfos.getString("token","");
    }

}
