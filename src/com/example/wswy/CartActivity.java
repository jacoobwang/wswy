package com.example.wswy;

import java.util.ArrayList;
import java.util.List;

import com.example.adapter.CommonAdapter;
import com.example.adapter.ViewHolderModel;
import com.example.imageCache.ImageStore;
import com.example.protocol.WsyMeta;
import com.example.protocol.WsyProtocol;
import com.example.protocol.WsyMeta.MStoreInfo;
import com.example.view.PullToRefreshListView;
import com.example.view.PullToRefreshListView.OnRefreshListener;
import com.example.wswy.StoreActivity.LocalListHolderView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CartActivity extends WsyBaseActivity implements
OnItemClickListener,OnRefreshListener{
	private PullToRefreshListView mListView;
	private List<WsyMeta.MStoreInfo> mShowStoreData = new ArrayList<WsyMeta.MStoreInfo>();
	private List<WsyMeta.MStoreInfo> mStoreListData = new ArrayList<WsyMeta.MStoreInfo>();
	private int PushupDropdownFlag = 0;
	private String type ;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cart_activity);
		
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);
		String uid = userInfo.getString("uid", null);
		
		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		
		this.initView();
		showWaitDialog();
		WsyProtocol.getInstance().getCart(this, uid, type);
		setLeftButton();
	}
	
	private void initView() {
		mListView = (PullToRefreshListView) findViewById(R.id.content_lv2);
		mListView.setOnItemClickListener(this);
		mListView.setonRefreshListener(this);
	}
	
	private void showData2View(){
		CommonAdapter vAdapter = (CommonAdapter) mListView.getAdapter();
		if(vAdapter != null){
			vAdapter.SetListData(mShowStoreData);
			vAdapter.notifyDataSetChanged(); // data changed update view
		}else{
			LocalListHolderView vLocalHolderView = new LocalListHolderView(
					this,mShowStoreData);
			if(type.equalsIgnoreCase("0")){
				vAdapter = new CommonAdapter(vLocalHolderView,
						R.layout.cart_listview2);	
			}else{
				vAdapter = new CommonAdapter(vLocalHolderView,
					R.layout.cart_listview);
			}
			mListView.setAdapter(vAdapter);
		}
	}
	
	/**
	 * @desc 设置返回按钮
	 */
	public void setLeftButton(){
		ImageView leftBtn = (ImageView) findViewById(R.id.left_arrow);
		leftBtn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				CartActivity.this.finish();
			}
		});
	}
	
	/**
	 * @desc 设置button事件
	 */
	public void excuteButton(View v){	
		Toast toast = Toast.makeText(CartActivity.this, "暂不支持评论", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 10);
		toast.show();
	}
	
	@Override
	public void onRefresh(int refreshType) {
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}	
	
	class LocalListHolderView extends ViewHolderModel{
		private ImageView imProductPhoto;
		private TextView tvProductTitle;
		private TextView tvProductPrice;
		private TextView tvProductOldPrice;
		private TextView tvProductBuyNum;
		
		public LocalListHolderView(Context context, List<?> list){
			super(context, list);
		}
		
		public LocalListHolderView(){
			super(null,null);
		}
		
		@Override
		public ViewHolderModel InitViewHoler(View convertView, int aPos) {
			LocalListHolderView vHolder = new LocalListHolderView();
			vHolder.imProductPhoto = (ImageView) convertView.findViewById(R.id.product_photo);
			vHolder.tvProductTitle = (TextView) convertView.findViewById(R.id.product_title);
			vHolder.tvProductPrice = (TextView) convertView.findViewById(R.id.cart_total);
			return vHolder;
		}

		@Override
		public void SetViewHolerValues(Object aItemObject) { 
			WsyMeta.MStoreInfo info = (MStoreInfo) aItemObject;
			imProductPhoto.setBackgroundResource(R.drawable.thumb_bg);
			imProductPhoto.setTag(R.id.tag_first,info.mProImg);
			ImageStore.Instance().SetImageViewBitmap(imProductPhoto, false);
			tvProductTitle.setText(info.mProTitle);
			tvProductPrice.setText("总价:"+info.mProPrice+"元  数量:"+info.mProBuyNum);
		}
	}
	
	@Override
	public boolean OnNewDataArrived(int aRequestType, int aErrCode, Object aData){
		if(aErrCode == 4){
			showErrorToast(4);
		}
		if(!super.OnNewDataArrived(aRequestType, aErrCode, aData))
		{
			if(aRequestType == WsyProtocol.kRequestIdGetCart &&
					aData != null){
				addListData(mStoreListData, aData);
				mShowStoreData = mStoreListData;
				showData2View();
			}
		}else{
			//error
			PushupDropdownFlag = 0;
			showErrorToast(aErrCode);
		}
		return true;
	}
	
	private void addListData(List<WsyMeta.MStoreInfo> typeList,Object aData){
		if(PushupDropdownFlag == 0){
			typeList.clear();
			typeList.addAll((List<WsyMeta.MStoreInfo>)aData);
		}else{
			typeList.addAll((List<WsyMeta.MStoreInfo>)aData);
			PushupDropdownFlag = 0;
		}
	}
}
