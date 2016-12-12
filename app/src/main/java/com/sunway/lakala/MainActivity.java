package com.sunway.lakala;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sunway.lakala.data.Const;
import com.sunway.lakala.model.Order;
import com.sunway.lakala.model.Store;
import com.sunway.lakala.model.SuccResult;
import com.sunway.lakala.resp.OrdersResp;
import com.sunway.lakala.util.DataManager;
import com.sunway.lakala.util.JsonParser;
import com.sunway.lakala.util.JsonVolley;
import com.sunway.lakala.util.NetParams;
import com.sunway.lakala.util.PayManager;
import com.sunway.lakala.util.UploadManager;
import com.sunway.lakala.view.LoadingDialog;
import com.sunway.lakala.view.XListView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements View.OnClickListener,UploadManager.RefreshListenr{

    String TAG = "MainActivity";
    PayManager payMgr;
    XListView orderList;
    OrderAdapter orderAdapter;
    List<Order> testOrders = new ArrayList<>();
    JsonVolley ordersVolley;
    final int ORDERS_SUCC = 1;
    final int ORDERS_FAIL = 2;
    RelativeLayout lodingFame;
    TextView noOrderText;
    TextView netFailText;
    TextView storeNameText;
    TextView promptText;
    Store store;
    UploadManager mgr = new UploadManager(MainActivity.this);
    Handler hander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ORDERS_SUCC:
                    if(msg.arg1 == NetParams.RESPONCE_NORMAL){
                        OrdersResp resp = (OrdersResp) JsonParser.jsonToObject(msg.obj + "",OrdersResp.class);
                        List<Order> orders = resp.getData();
                        lodingFame.setVisibility(View.GONE);
                        if(orders == null || orders.size() == 0){
                            promptText.setText("支持刷卡和扫码支付");
                            noOrderText.setVisibility(View.VISIBLE);
                        }else {
                            netFailText.setVisibility(View.INVISIBLE);
                            noOrderText.setVisibility(View.INVISIBLE);
                            orderList.setVisibility(View.VISIBLE);
                            promptText.setText(orders.size() + "笔需要支付的订单");
                            if(orderAdapter == null){

                                orderAdapter = new OrderAdapter(orders);
                                orderList.setAdapter(orderAdapter);
                            }else {
                                orderAdapter.setOrders(orders);
                                orderAdapter.notifyDataSetChanged();
                            }

                        }

                    }else {
                        promptText.setText("支持刷卡和扫码支付");
                        noOrderText.setVisibility(View.INVISIBLE);
                        orderList.setVisibility(View.INVISIBLE);
                        netFailText.setVisibility(View.VISIBLE);
                    }


                    break;
                case ORDERS_FAIL:
                    promptText.setText("支持刷卡和扫码支付");
                    lodingFame.setVisibility(View.GONE);
                    orderList.setVisibility(View.INVISIBLE);
                    noOrderText.setVisibility(View.GONE);
                    netFailText.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        store = (Store) JsonParser.jsonToObject(getIntent().getStringExtra("store"),Store.class);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.action_bar);
        actionBar.setDefaultDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        promptText = (TextView) findViewById(R.id.text_pmpt);
        storeNameText = (TextView) findViewById(R.id.text_store_name);
        storeNameText.setText(store.getStoreName() + "门店收款");
        ImageButton refreshImage = (ImageButton) actionBar.getCustomView().findViewById(R.id.image_refresh);
        refreshImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadManager mgr = new UploadManager(MainActivity.this);
                mgr.setRefreshListenr(MainActivity.this);
                mgr.upload();
            }
        });

        payMgr = PayManager.getInstance(this);
        orderList = (XListView) findViewById(R.id.list_order);
        testOrders.add(new Order());
        netFailText = (TextView) findViewById(R.id.text_netfail);
        noOrderText = (TextView) findViewById(R.id.text_no_order);
        lodingFame = (RelativeLayout) findViewById(R.id.loding_frame);
        orderList.setAdapter(orderAdapter);
        orderList.setPullRefreshEnable(false);
        orderList.setPullLoadEnable(false);
        ordersVolley = new JsonVolley(this,ORDERS_SUCC,ORDERS_FAIL);
        ordersVolley.requestPost(Const.Request.payOrders,hander, DataManager.getInstance(this).getToken());
        ordersVolley.addParams("storeId",store.getId());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.reload_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void refresh(boolean showDialog) {
        if(showDialog){
            ordersVolley.requestPost(Const.Request.payOrders,new LoadingDialog(MainActivity.this,"刷新中"),hander,DataManager.getInstance(MainActivity.this).getToken());
        }else {
            ordersVolley.requestPost(Const.Request.payOrders,hander,DataManager.getInstance(MainActivity.this).getToken());

        }

    }

    class OrderAdapter extends BaseAdapter{

        List<Order> orders;

        public void setOrders(List<Order> orders) {
            this.orders = orders;
        }

        public OrderAdapter(List<Order> orders){
            this.orders =  orders;
        }

        @Override
        public int getCount() {
            return orders.size();
        }

        @Override
        public Object getItem(int position) {
            return orders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final  Order order = (Order) getItem(position);
            Holder holder = new Holder();
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.pay_item,null);
                holder.cardBtn = (TextView) convertView.findViewById(R.id.btn_card_pay);
                holder.scanBtn = (TextView) convertView.findViewById(R.id.btn_scan_pay);
                holder.orderNoText = (TextView) convertView.findViewById(R.id.text_orderno);
                holder.orderUserText = (TextView) convertView.findViewById(R.id.text_orderuser);
                holder.orderAmtText = (TextView) convertView.findViewById(R.id.text_orderamt);
                holder.orderDescText = (TextView) convertView.findViewById(R.id.text_orderdesc);
                convertView.setTag(holder);
            }else {
                holder = (Holder) convertView.getTag();
            }
            holder.orderNoText.setText("订单编号：" + order.getOrderNo());
            holder.orderAmtText.setText(order.getToPOSPayAmount() + "元");
            holder.orderUserText.setText("用户手机：" + order.getMobile());
            holder.orderDescText.setText("消费目的：" + order.getDescription());
            BigDecimal price = new BigDecimal(Float.parseFloat(order.getToPOSPayAmount()) * 100.0f);
           final   String amt = String.format("%.0f", price);
            holder.cardBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    payMgr.cardPay(order.getOrderNo(), amt,order.getType(), new PayManager.PayResultLinster() {
                        @Override
                        public void paySucc(SuccResult result) {
                            Toast.makeText(getApplicationContext(),"支付成功",Toast.LENGTH_LONG).show();
                            UploadManager mgr = new UploadManager(MainActivity.this);
                            mgr.setRefreshListenr(MainActivity.this);
                            mgr.setResult(result);
                            mgr.upload();
                        }

                        @Override
                        public void payFail(String reason) {
                            Toast.makeText(getApplicationContext(),reason,Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            holder.scanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    payMgr.scanPay(order.getOrderNo(), amt, order.getType(),new PayManager.PayResultLinster() {
                        @Override
                        public void paySucc(SuccResult result) {
                            Toast.makeText(getApplicationContext(),"支付成功",Toast.LENGTH_LONG).show();
                            UploadManager mgr = new UploadManager(MainActivity.this);
                            mgr.setRefreshListenr(MainActivity.this);
                            mgr.setResult(result);
                            mgr.upload();

                        }

                        @Override
                        public void payFail(String reason) {
                            Toast.makeText(getApplicationContext(),reason,Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });


            return convertView;
        }

        class Holder{
            TextView scanBtn;
            TextView cardBtn;
            TextView orderNoText;
            TextView orderUserText;
            TextView orderAmtText;
            TextView orderDescText;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        payMgr.payResult(requestCode, resultCode, data);
    }
}
