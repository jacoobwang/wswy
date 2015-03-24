package com.example.wswy;

import java.util.ArrayList;
import java.util.List;

import com.example.adapter.CommonAdapter;
import com.example.adapter.ViewHolderModel;
import com.example.imageCache.ImageStore;
import com.example.protocol.WsyMeta;
import com.example.protocol.WsyMeta.MStoreInfo;
import com.example.protocol.WsyProtocol;
import com.example.view.PullToRefreshListView;
import com.example.view.PullToRefreshListView.OnRefreshListener;
import com.example.view.PullToRefreshView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class StoreActivity extends WsyBaseActivity implements OnItemClickListener,OnRefreshListener{
	private PullToRefreshListView mListView;
	private List<WsyMeta.MStoreInfo> mShowStoreData = new ArrayList<WsyMeta.MStoreInfo>();
	private List<WsyMeta.MStoreInfo> mStoreListData = new ArrayList<WsyMeta.MStoreInfo>();
	private int PushupDropdownFlag = 0;
	private int catId = 1;
	private PullToRefreshView mPullToRefreshView;
	private boolean isRefreshData = false;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.buy_activity);
		this.initView();
		showWaitDialog();
		WsyProtocol.getInstance().getStore(true, this, catId);
	}

	private void initView() {
		mListView = (PullToRefreshListView) findViewById(R.id.content_lv);
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
			vAdapter = new CommonAdapter(vLocalHolderView,
					R.layout.buy_listview);
			mListView.setAdapter(vAdapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		WsyMeta.MStoreInfo info = mShowStoreData.get(position);
		Intent intent = new Intent();
		intent.putExtra("id", info.mProId);		
		intent.putExtra("img", info.mProImg);		
		intent.setClass(StoreActivity.this, StoreDetailActivity.class);
		startActivity(intent);
	}

	@Override
	public void onRefresh(int refreshType) {
		Log.i("alex", "onRefresh");
		if (refreshType == 1)
		{
			PushupDropdownFlag = 1;
			WsyProtocol.getInstance().getStore(false, this, catId);
		}else{
			PushupDropdownFlag = 0;
			WsyProtocol.getInstance().getStore(true, this, catId);
		}	
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
			vHolder.tvProductPrice = (TextView) convertView.findViewById(R.id.product_price);
			vHolder.tvProductOldPrice = (TextView) convertView.findViewById(R.id.product_price_old);
			vHolder.tvProductBuyNum = (TextView) convertView.findViewById(R.id.buy_people);
			return vHolder;
		}

		@Override
		public void SetViewHolerValues(Object aItemObject) { 
			WsyMeta.MStoreInfo info = (MStoreInfo) aItemObject;
			
			imProductPhoto.setBackgroundResource(R.drawable.thumb_bg);
			imProductPhoto.setTag(R.id.tag_first,info.mProImg);
			ImageStore.Instance().SetImageViewBitmap(imProductPhoto, false);
			tvProductTitle.setText(info.mProTitle);
			tvProductPrice.setText("¥" + info.mProPrice);
			tvProductOldPrice.setText("原价¥" + info.mProOldPrice);
			tvProductOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			tvProductBuyNum.setText("有" +info.mProBuyNum+ "人已购买");
		}
	}
	
	@Override
	public boolean OnNewDataArrived(int aRequestType, int aErrCode, Object aData){
		mListView.onRefreshComplete(this);
		if(!super.OnNewDataArrived(aRequestType, aErrCode, aData))
		{
			if(aRequestType == WsyProtocol.KRequestIdGetStore &&
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
