package com.sunway.lakala.resp;

import com.sunway.lakala.model.UploadModel;

/**
 * Created by LL on 2016/12/1.
 */
public class UploadResp extends BaseResp{

    UploadModel data;

    public UploadModel getData() {
        return data;
    }

    public void setData(UploadModel data) {
        this.data = data;
    }
}
