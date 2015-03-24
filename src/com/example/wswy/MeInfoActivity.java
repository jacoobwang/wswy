package com.example.wswy;

import java.util.Arrays;

import com.example.adapter.GridBottomAdapter;
import com.example.imageCache.ImageStore;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MeInfoActivity extends WsyBaseActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState){ 
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.me_activity);
		
		TextView meName = (TextView) findViewById(R.id.me_name);
		TextView meId = (TextView) findViewById(R.id.me_id);
		meName.setText(uname);
		meId.setText(uid);
		
		//显示头像
		if(utx != null && utx.length()>0){
			ImageView txView = (ImageView) findViewById(R.id.me_tx);
			txView.setTag(R.id.tag_first, utx);
			ImageStore.Instance().SetImageViewBitmap(txView, false);
		}
		
		setBottomView(R.id.gridBottom,MeInfoActivity.this,3);
		
		ImageView mePay = (ImageView) findViewById(R.id.me_pay);
		ImageView meNoPay = (ImageView) findViewById(R.id.me_nopay);
		ImageView meRecommend = (ImageView) findViewById(R.id.me_recommend);
		View mHeadBtn = findViewById(R.id.head_tx_btn);
	
		mHeadBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(MeInfoActivity.this, MeInfoDetailActivity.class);
				startActivity(intent);
			}
		});
		
		mePay.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("type", "1");
				intent.setClass(MeInfoActivity.this, CartActivity.class);
				startActivity(intent);
			}
		});
		meNoPay.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("type", "0");
				intent.setClass(MeInfoActivity.this, CartActivity.class);
				startActivity(intent);
			}
		});
		meRecommend.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(MeInfoActivity.this, StoreActivity.class);
				startActivity(intent);	
			}
		});
	}
	
	/**
	 * @desc ���÷���
	 * @param iLayoutId
	 */
	public void setBottomViews(int iLayoutId){
		String meunDatas[] = {"��ҳ","�Ź�","��Ϣ","�ҵ�"};
		GridView gdView01 = (GridView) findViewById(iLayoutId);
		gdView01.setNumColumns(meunDatas.length);
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		GridBottomAdapter gdAdapter = new GridBottomAdapter(this,
				Arrays.asList(meunDatas),3,screenWidth);
		gdView01.setAdapter(gdAdapter);
		//bindClickListener(gdView01);
	}
}
