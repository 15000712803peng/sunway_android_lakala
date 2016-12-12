package com.sunway.lakala;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sunway.lakala.data.Const;
import com.sunway.lakala.dialog.SelStoreDialog;
import com.sunway.lakala.model.Store;
import com.sunway.lakala.resp.LoginResp;
import com.sunway.lakala.util.DataManager;
import com.sunway.lakala.util.JsonParser;
import com.sunway.lakala.util.JsonVolley;
import com.sunway.lakala.util.NetParams;
import com.sunway.lakala.view.LoadingDialog;

import java.util.List;
import com.sunway.lakala.R;

/**
 * Created by LL on 2016/11/29.
 */
public class LoginActivity extends Activity implements View.OnClickListener,SelStoreDialog.LoginInferce{

    Button loginBtn;
    EditText accountEdit,pwdEdit;
    JsonVolley loginVolley;
    JsonVolley storeLoginVolley;
    final int LOGIN_SUCC = 1;
    final int LOGIN_FAIL = 2;
    final int STORE_LOGIN_SUCC = 3;
    final int STORE_LOGIN_FAIL = 4;
    Handler handler;
    LinearLayout savePwdContainer;
    CheckBox savePwdBox;
    DataManager dataMgr;
    SelStoreDialog dialog;
    Store store;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        loginBtn = (Button) findViewById(R.id.btn_login);
        accountEdit = (EditText) findViewById(R.id.edit_account);
        pwdEdit = (EditText) findViewById(R.id.edit_password);
        savePwdContainer = (LinearLayout) findViewById(R.id.ll_save_pwd);
        savePwdBox = (CheckBox) findViewById(R.id.box_save_pwd);
        savePwdContainer.setOnClickListener(this);
        dataMgr = DataManager.getInstance(this);
        accountEdit.setText(dataMgr.getAccount());
        pwdEdit.setText(dataMgr.getPwd());
        if(!TextUtils.isEmpty(dataMgr.getPwd())){
            savePwdBox.setChecked(true);
        }else {
            savePwdBox.setChecked(false);
        }
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case LOGIN_SUCC:
                        if(msg.arg1 == NetParams.RESPONCE_NORMAL){
                            LoginResp loginResp = (LoginResp) JsonParser.jsonToObject(msg.obj + "", LoginResp.class);
                            Store loginStore = loginResp.getData().getLoginStore();
                            dataMgr.saveToken(loginResp.getData().getToken());
                            if(loginStore == null){
                                String account = accountEdit.getText().toString().trim();
                                List<Store> stores = loginResp.getData().getStores();
                                dialog = new SelStoreDialog(LoginActivity.this,stores,account).builder();
                                dialog.setStores();
                                dialog.setLoginInferce(LoginActivity.this);
                                dialog.show();
//                                Log.e("size","size:" + stores.size());
                            }else {
                                Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_LONG).show();
                                String account = accountEdit.getText().toString().trim();
                                dataMgr.saveAccount(account);
                                if(savePwdBox.isChecked()){
                                    String pwd = pwdEdit.getText().toString().trim();
                                    dataMgr.savePwd(pwd);
                                }else {
                                    dataMgr.savePwd("");
                                }
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("store", JsonParser.ObjectToJsonStr(loginStore));
                                startActivity(intent);
                                if(dialog != null)
                                dialog.cancel();
//                                finish();
                            }

                        }else {
                            Toast.makeText(LoginActivity.this,msg.obj + "",Toast.LENGTH_LONG).show();
                        }

                        break;
                    case LOGIN_FAIL:
                        Toast.makeText(LoginActivity.this,"网络异常，登录失败",Toast.LENGTH_LONG).show();
                        break;

                    case STORE_LOGIN_SUCC:
                        if(msg.arg1 == NetParams.RESPONCE_NORMAL){
                            Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_LONG).show();
                            String account = accountEdit.getText().toString().trim();
                            dataMgr.saveAccount(account);
                            if(savePwdBox.isChecked()){
                                String pwd = pwdEdit.getText().toString().trim();
                                dataMgr.savePwd(pwd);
                            }else {
                                dataMgr.savePwd("");
                            }
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("store", JsonParser.ObjectToJsonStr(store));
                            startActivity(intent);
                            dialog.cancel();
//                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this,msg.obj + "",Toast.LENGTH_LONG).show();
                        }
                        break;

                    case STORE_LOGIN_FAIL:
                        Toast.makeText(LoginActivity.this,"网络异常，登录失败",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        loginVolley = new JsonVolley(this,LOGIN_SUCC,LOGIN_FAIL);
        storeLoginVolley = new JsonVolley(this,STORE_LOGIN_SUCC,STORE_LOGIN_FAIL);

    }

    private void login(){
        String account = accountEdit.getText().toString().trim();
        String pwd = pwdEdit.getText().toString().trim();
        if(TextUtils.isEmpty(account)){
            Toast.makeText(this,"请输入账号",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(account)){
            Toast.makeText(this,"请输入密码",Toast.LENGTH_LONG).show();
            return;
        }

        loginVolley.addParams("operMobile",account);
        loginVolley.addParams("operPassword",pwd);
        loginVolley.requestPost(Const.Request.login,new LoadingDialog(this),handler);
    }

    @Override
    public void onClick(View v) {
        if(v == savePwdContainer){
                savePwdBox.setChecked(!savePwdBox.isChecked());
        }
    }

    @Override
    public void loginStore(Store store) {
//        Log.e("storeId","store id:" + storeId);
        this.store = store;
        storeLoginVolley.addParams("id",store.getId());
        storeLoginVolley.requestPost(Const.Request.storeLogin,new LoadingDialog(this),handler,dataMgr.getToken());
    }
}
