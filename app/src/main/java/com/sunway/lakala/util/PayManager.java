package com.sunway.lakala.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.sunway.lakala.R;
import com.sunway.lakala.model.MemberInfo;
import com.sunway.lakala.model.SuccResult;
import com.ums.AppHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by LL on 2016/11/25.
 */
public class PayManager {

    String TAG = "PayManager";
    private   static PayManager instance;
    Activity activity;
    String orderNo;
    String type;
    public interface PayResultLinster{
        void paySucc(SuccResult result);
        void payFail(String reason);
    }

    PayResultLinster payResultLinster;

    public PayResultLinster getPayResultLinster() {
        return payResultLinster;
    }

    public void setPayResultLinster(PayResultLinster payResultLinster) {
        this.payResultLinster = payResultLinster;
    }

    public static synchronized PayManager getInstance(Activity activity){
        if(instance == null){
            instance = new PayManager(activity);
        }
        return instance;
    }

    private PayManager(Activity activity){
            this.activity = activity;
    }

    public void cardPay(String orderNo,String amt,String type ,PayResultLinster listener){
        this.type = type;
        this.orderNo  = orderNo;
        this.payResultLinster = listener;
        String amount = "{amt: "+ amt + "}";
        try {
            AppHelper.callTrans(activity, activity.getString(R.string.card), activity.getString(R.string.consume), new JSONObject(amount));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void scanPay(String orderNo,String amt,String type,PayResultLinster listener){
        this.type = type;
        this.orderNo = orderNo;
        this.payResultLinster = listener;
        String amount = "{amt: "+ amt + "}";
        try {
            AppHelper.callTrans(activity, activity.getString(R.string.pos), activity.getString(R.string.scan), new JSONObject(amount));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void payResult(int requestCode, int resultCode, Intent data){
        if(AppHelper.TRANS_REQUEST_CODE == requestCode){
            Log.d(TAG, "resultCode" + resultCode);
            if (Activity.RESULT_OK == resultCode) {
                if (null != data) {
                    StringBuilder result = new StringBuilder();
                    Map<String,String> map = AppHelper.filterTransResult(data);
//                    result.append(AppHelper.TRANS_APP_NAME + ":" +map.get(AppHelper.TRANS_APP_NAME) + "\r\n");
//                    result.append(AppHelper.TRANS_BIZ_ID + ":" +map.get(AppHelper.TRANS_BIZ_ID) + "\r\n");
//                    result.append(AppHelper.RESULT_CODE + ":" +map.get(AppHelper.RESULT_CODE) + "\r\n");
//                    result.append(AppHelper.RESULT_MSG + ":" +map.get(AppHelper.RESULT_MSG) + "\r\n");
                    try {
                        JSONObject resultObject = new JSONObject(map.get(AppHelper.TRANS_DATA));
                        if(resultObject.getString("resCode").equals("FF")){
                            if(payResultLinster != null){
                                payResultLinster.payFail("支付失败，" + resultObject.getString("resDesc"));
                            }
                        }else if(resultObject.getString("resCode").equals("00")){
                            if(payResultLinster != null){
                                payResultLinster.paySucc(getResult(resultObject,orderNo,type));

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    result.append(AppHelper.TRANS_DATA + ":" + + "\r\n");

                    Log.d(TAG, "result" + result);
                    if (null != result) {

                    }
                }else{

                }
            }else{
            }
        }
//        else if(AppHelper.PRINT_REQUEST_CODE == requestCode){
//            Log.d(TAG, "resultCode" + resultCode);
//            if (Activity.RESULT_OK == resultCode) {
//                if (null != data) {
//                    StringBuilder result = new StringBuilder();
//                    String printCode = data.getStringExtra("resultCode");
//                    result.append("resultCode:" + printCode);
//                    Log.d(TAG, "result" + result);
//                    if (null != result) {
//                    }
//                }else{
//                }
//            }else{
//
//            }
//        }
    }

    private SuccResult getResult(JSONObject result,String orderNo,String type){
        SuccResult succResult = new SuccResult();
        try {
            succResult.setResCode(result.getString("resCode"));
            succResult.setResDesc(result.getString("resDesc"));
            succResult.setOrderNo(orderNo);
            succResult.setType(type);
            succResult.setMerchantName(result.getString("merchantName"));
            succResult.setMerchantNo(result.getString("merchantNo"));
            succResult.setTerminalNo(result.getString("terminalNo"));
            succResult.setAmt(result.getString("amt"));
            succResult.setBatchNO(result.getString("batchNo"));
            succResult.setTraceNo(result.getString("traceNo"));
            succResult.setRefNo(result.getString("refNo"));
            succResult.setAuthNo(result.getString("authNo"));
            succResult.setExpDate(result.getString("expDate"));
            succResult.setCardNo(result.getString("cardNo"));
            succResult.setCardIssuer(result.getString("cardIssuer"));
            succResult.setTransChnName(result.getString("transChnName"));
            succResult.setTransEngName(result.getString("transEngName"));
            succResult.setDate(result.getString("date"));
            succResult.setTime(result.getString("time"));
            succResult.setIsReprint(result.getString("isReprint"));
            succResult.setVendor(result.getString("vendor"));
            succResult.setModel(result.getString("model"));
            succResult.setQrcode(result.getString("qrcode"));
            MemberInfo memberInfo = (MemberInfo) JsonParser.jsonToObject(result.get("memInfo") + "",MemberInfo.class);
            succResult.setMemberInfo(memberInfo);
//            succResult.setMemInfo(result.getString("memInfo"));
//            succResult.setTransNo(result.getString("transNo"));
//            succResult.setDiscountInfo(result.getString("discountInfo"));
//            succResult.setChannelName(result.getString("channelName"));
//            succResult.setPayNo(result.getString("payNo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return  succResult;
    }
}
