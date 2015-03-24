package com.example.wswy;

import java.util.Arrays;

import com.example.adapter.GridBottomAdapter;
import com.example.imageCache.ImageStore;
import com.example.protocol.WsyProtocol;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class StoreDetailActivity extends WsyBaseActivity{
	private String proId = null;
	private String proImg = null;
	private ImageView imageView = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buy_detail);
		setBottomView(R.id.gridBottom, StoreDetailActivity.this,1);
		setLeftButton();
		setAddCart();
		Intent intent = getIntent();
		proId = intent.getStringExtra("id");
		proImg = intent.getStringExtra("img");
		imageView = (ImageView) findViewById(R.id.buyImg);
		imageView.setTag(R.id.tag_first, proImg);
		ImageStore.Instance().SetImageViewBitmap(imageView, false);
	}
	
	/**
	 * @desc ��ӹ��ﳵ
	 */
	public void setAddCart(){
		TextView buy_button = (TextView) findViewById(R.id.buy_button);
		buy_button.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {
				if(proId != null){
					WsyProtocol.getInstance().SendAddCart(StoreDetailActivity.this,uid,proId);	
					Toast.makeText(StoreDetailActivity.this, "添加购物车成功", Toast.LENGTH_SHORT).show();
				}	
			}
		});	
	}
	
	/**
	 * @desc ���÷��ذ�ť
	 */
	public void setLeftButton(){
		ImageView leftBtn = (ImageView) findViewById(R.id.left_arrow);
		leftBtn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				StoreDetailActivity.this.finish();
			}
		});
	}
	
	/**
	 * @desc ���õײ���ť
	 * @param iLayoutId ����һ����Gridview ID
	 */
	public void setBottomViews(int iLayoutId){
		String meunDatas[] = {"��ҳ","�Ź�","��Ϣ","�ҵ�"};	
		GridView gdView01 = (GridView) findViewById(iLayoutId);
		gdView01.setNumColumns(meunDatas.length);
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		GridBottomAdapter gdAdapter = new GridBottomAdapter(this,
				Arrays.asList(meunDatas),1,screenWidth);
		gdView01.setAdapter(gdAdapter);
		//bindClickListener(gdView01);
	}
	
	@Override
	public boolean OnNewDataArrived(int aRequestType, int aErrCode, Object aData){
		Log.i("test","errcode--->"+aErrCode);
		if(aErrCode == -1001){
			Toast toast = Toast.makeText(StoreDetailActivity.this, "网络错误，请稍后再来", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 10);
			toast.show();
			return true;
		}
		if(!super.OnNewDataArrived(aRequestType, aErrCode, aData)){
			if (aRequestType == WsyProtocol.kRequestIdSendCartInfo && aErrCode ==1){
				Toast.makeText(StoreDetailActivity.this, "添加购物车成功", Toast.LENGTH_SHORT).show();
			}	
		}	
		return true;
	}
	
}
