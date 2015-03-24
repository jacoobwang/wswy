package com.example.wswy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.adapter.GridBottomAdapter;
import com.example.protocol.WsyProtocol;

public class WsyBaseActivity extends Activity implements
	WsyProtocol.MDataUpdateNotify,OnClickListener{
	protected static ProgressDialog mProgressDialog = null;
	protected int[] images = new int[]{
	    	R.drawable.bottom_index_bg,
	    	R.drawable.bottom_buy_bg,
	    	R.drawable.bottom_friend_bg,
	    	R.drawable.bottom_my_bg
	};
	protected int[] imagesSelect = new int[]{
			R.drawable.bottom_buy_select,
			R.drawable.bottom_friend_select,
			R.drawable.bottom_my_select
	};
	private final int INDEX = 0;
	private final int BUY =1;
	private final int MSG =2;
	private final int ME =3;
	public static String uid;   //用户id
	public static String uname; //用户名
	public static String utx;   //头像
	
	
	@Override
	public void onCreate(Bundle savedInstanceStated){
		super.onCreate(savedInstanceStated);
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);
		uid = userInfo.getString("uid", null);
		uname = userInfo.getString("uname", null);
		utx   = userInfo.getString("utx", null);
	}
	
	@Override
	public boolean OnNewDataArrived(int aRequestType, int aErrCode, Object aData)
	{
		boolean ret = false;
		dismissWaitDialog();
		if (aErrCode == -1)
		{
			ret = true;
		}
		else if (aErrCode == -5)
		{
			ret = true;
		}
		else if (aErrCode == 0)
		{
			ret = true;
		}
		else if (aErrCode != 1)
		{
			ret = true;
		}
		return ret;
	}
	
	protected void showWaitDialog(){
		if(mProgressDialog != null)
		{
			mProgressDialog.dismiss();
		}
		mProgressDialog = ProgressDialog.show(this,null,
				getResources().getString(R.string.loading),true,true);
	}
	
	protected void dismissWaitDialog() {
		if(mProgressDialog != null)
		{
			mProgressDialog.dismiss();
		}		
	}
	
	public void showErrorToast(int aErrCode){
		if (aErrCode == -1)
		{
			Toast.makeText(this, "网络错误，请稍后再来!", Toast.LENGTH_SHORT).show();
		}
		else if(aErrCode == 4)
		{
			Toast.makeText(this, "未找到相关数据", Toast.LENGTH_SHORT).show();
		}
		else if(aErrCode == 0)
		{
			Toast.makeText(this, "未知错误，请稍后再来!", Toast.LENGTH_SHORT).show();
		}
		else if(aErrCode != 1)
		{
			Toast.makeText(this, "网络错误，请稍后再来!", Toast.LENGTH_SHORT).show();
		}
	}
	
	public boolean onViewClick(View v)
	{
		return false;
	}
	
	@Override
	public void onClick(View v) {
		onViewClick(v);
	} 
	
	/**
	 * @desc ���õײ������˵�
	 * @param iLayoutId
	 */
	public void setBottomView(int iLayoutId,Context context,int index){
		GridView gdView01 = (GridView) findViewById(iLayoutId);
		gdView01.setBackgroundResource(R.drawable.bottom_bg);//���ñ���
		gdView01.setNumColumns(4); //����ÿ������
		gdView01.setGravity(Gravity.CENTER);
		if(index != 0){
			images[index] = imagesSelect[index-1];
		}	 
		gdView01.setAdapter(getMenuAdapter(images));
		bindClickListener(gdView01,context,index);
	}
	
	/**
	 * @desc �󶨲˵��¼�
	 * @param gdView01
	 */
	private void bindClickListener(GridView gdView01,final Context context, final int idx) {
		gdView01.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int id,
					long arg3) {
				Intent intent = new Intent();
				switch(id){
					case INDEX:
						if(idx != INDEX){
							intent.setClass(context, MainActivity.class);
							startActivity(intent);
						}
						break;
					case BUY:
						if(idx != BUY){	
							intent.setClass(context, StoreActivity.class);
							startActivity(intent);
						}
						break;
					case MSG:
						if(idx != MSG){
							intent.setClass(context, MsgActivity.class);
							startActivity(intent);
						}
						break;
					case ME:
						if(idx != ME){
							intent.setClass(context, MeInfoActivity.class);
							startActivity(intent);
						}
						break;
				}	
			}	
		});
	} 
	
	protected SimpleAdapter getMenuAdapter(int[] imageResourceArray){
		ArrayList<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();
		for(int i=0; i < imageResourceArray.length; i++){
			HashMap<String,Object> map = new HashMap<String, Object>();	
			map.put("itemImage",imageResourceArray[i]);
			data.add(map);
		}
		SimpleAdapter simpleAdapter= new SimpleAdapter(this, data, 
				R.layout.item_menu, new String[] {"itemImage"},
				new int[]{R.id.item_image});
		return simpleAdapter;
	}
	
}
