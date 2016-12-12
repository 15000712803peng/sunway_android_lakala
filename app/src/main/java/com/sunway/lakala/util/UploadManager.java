package com.sunway.lakala.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.sunway.lakala.data.Const;
import com.sunway.lakala.model.Results;
import com.sunway.lakala.model.SuccResult;
import com.sunway.lakala.resp.UploadResp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by LL on 2016/11/27.
 */
public class UploadManager {

    SuccResult result;
    DataManager dataMgr;
    Context context;
    final int UPLOAD_SUCC = 6;
    final  int UPLOAD_FAIL = 7;
    JsonVolley uploadVolley;
    Handler handler;
    public interface  RefreshListenr{
        void refresh(boolean showDialog);
    }
    RefreshListenr refreshListenr;


    public void setRefreshListenr(RefreshListenr refreshListenr) {
        this.refreshListenr = refreshListenr;
    }

    public UploadManager(Context context){
        this.context = context;
        dataMgr = DataManager.getInstance(context);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case UPLOAD_SUCC:
                        if(msg.arg1 == NetParams.RESPONCE_NORMAL){
                            UploadResp resp = (UploadResp) JsonParser.jsonToObject(msg.obj + "",UploadResp.class);
                            dataMgr.delResult(new SuccResult(resp.getData().getOrderNo()));
                            if(refreshListenr != null){
                                refreshListenr.refresh(false);
                            }
                        }
                        break;
                    case UPLOAD_FAIL:
                        break;
                }
            }
        };

    }


    public void setResult(SuccResult result) {
        this.result = result;
    }

    public void upload(){
        new UpdateAsyncTask().execute();
    }

    public class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(result != null){
                dataMgr.saveResult(result);
            }
            Results results = dataMgr.getAllResults();
            if(results != null && results.getResults() != null && results.getResults().size() > 0){
                List<SuccResult> succResults = results.getResults();
                for(SuccResult r: succResults){
                    uploadVolley = new JsonVolley(context,UPLOAD_SUCC,UPLOAD_FAIL);
                    uploadVolley.addParams("orderNo",r.getOrderNo());
                    uploadVolley.addParams("from","pos");
                    uploadVolley.addParams("paymentAmount",r.getAmt());
                    uploadVolley.addParams("type",r.getType());
                    uploadVolley.addParams("payRef",r.getRefNo());
                    JSONObject param = new JSONObject();
                    try {
                        uploadVolley.addParams("posParams",new JSONObject(JsonParser.ObjectToJsonStr(r)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    uploadVolley.requestPost(Const.Request.confirmPay,handler,DataManager.getInstance(context).getToken());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            result = null;
            if(refreshListenr != null){
                refreshListenr.refresh(true);
            }

        }
    }
}
