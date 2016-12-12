package com.sunway.lakala.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sunway.lakala.R;
import com.sunway.lakala.model.Store;
import com.sunway.lakala.view.MyList;

import java.util.List;


public class SelStoreDialog {

	private Activity context;
	Display display;
	RelativeLayout container;
	Dialog dialog;
	List<Store> stores;
	MyList storeList;
	TextView storesText;
	String account;
	ImageButton cancelBtn;
	Button loginBtn;
	public interface LoginInferce{
		void loginStore(Store store);
	}

	LoginInferce loginInferce;


	public void setLoginInferce(LoginInferce loginInferce) {
		this.loginInferce = loginInferce;
	}

	public SelStoreDialog(Activity context, List<Store> stores, String account) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
		this.stores = stores;
		this.account = account;
	}


	public SelStoreDialog builder() {

		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_sel_store, null);
		dialog = new Dialog(context, R.style.CustomDialog);
		container = (RelativeLayout) view
				.findViewById(R.id.ll_sel_store);
		storeList = (MyList) view.findViewById(R.id.list_store);
		storesText = (TextView) view.findViewById(R.id.text_stores);
		loginBtn = (Button) view.findViewById(R.id.btn_login);
		loginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(storeAdapter == null){
					return;
				}
				if(storeAdapter.getSelectStore() == null){
					Toast.makeText(context,"请先选择登录的门店",Toast.LENGTH_SHORT).show();
					return;
				}

				if(loginInferce != null){

					loginInferce.loginStore(storeAdapter.getSelectStore());
					cancel();
				}
//				if(TextUtils.isEmpty(storeAdapter.getSelectStoreId()){
//					Toast.makeText(context,"请现在登录的门店",Toast.LENGTH_SHORT).show();
//					return;
//				}
			}
		});
		cancelBtn = (ImageButton) view.findViewById(R.id.image_cancel);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		storesText.setText("账户" + account + "有" + stores.size() + "个门店\n请选择登录的门店");
		dialog.setCancelable(false);
		dialog.setContentView(view);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (display
				.getWidth() * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		container.setLayoutParams(params);
		dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
		return this;
	}

	public void show() {
		if (dialog != null && !dialog.isShowing()) {
			dialog.show();
		}
	}

	class StoreAdapter extends BaseAdapter{
		List<Store> stores;
		public StoreAdapter(List<Store> stores){
			this.stores = stores;
		}
		@Override
		public int getCount() {
			return stores.size();
		}

		@Override
		public Object getItem(int position) {
			return stores.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public Store getSelectStore(){
			for(Store store : stores){
				if(store.isSelected()){
					return store;
				}
			}
			return null;
		}

		private void clearSel(){
			for(Store store: stores){
				store.setSelected(false);
			}
		}

		public void setSelStore(int position){
			clearSel();
			this.stores.get(position).setSelected(true);
			notifyDataSetChanged();

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Store store = (Store) getItem(position);
			Holder holder = null;
			if(convertView == null){
				holder = new Holder();
				convertView = LayoutInflater.from(context).inflate(R.layout.store_item,null);
				holder.storeNameText = (TextView) convertView.findViewById(R.id.text_store_name);
				holder.selStoreBox = (CheckBox) convertView.findViewById(R.id.box_store);
				convertView.setTag(holder);
			}else {
				holder = (Holder) convertView.getTag();
			}
			holder.storeNameText.setText(store.getStoreName());
			if(store.isSelected()){
				holder.selStoreBox.setChecked(true);
			}else {
				holder.selStoreBox.setChecked(false);
			}
			return convertView;
		}

		class Holder{
			TextView storeNameText;
			CheckBox selStoreBox;
		}
	}

	StoreAdapter storeAdapter;
	public void setStores(){
		storeAdapter = new StoreAdapter(stores);
		storeList.setAdapter(storeAdapter);
		storeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				storeAdapter.setSelStore(position);
			}
		});
	}

	public void cancel() {
		if (dialog != null && dialog.isShowing()) {
			dialog.cancel();
		}
	}

}
