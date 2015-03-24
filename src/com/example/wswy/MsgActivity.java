package com.example.wswy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.adapter.GridBottomAdapter;
import com.example.adapter.ListMsgAdapter;
import com.example.protocol.WsyMeta;
import com.example.protocol.WsyMeta.UserInfo;
import com.example.protocol.WsyProtocol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MsgActivity extends WsyBaseActivity{
	private List<WsyMeta.UserInfo> data = new ArrayList<WsyMeta.UserInfo>();
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_activity);
		setBottomView(R.id.gridBottom, MsgActivity.this,2);
		setAddFriend(R.id.msg_add);
		WsyProtocol.getInstance().getFriendList(MsgActivity.this,uid);
	}
	
	private void initListView() {
		ListView msg_list = (ListView) findViewById(R.id.msg_list);
		ListMsgAdapter msg_adapter = new ListMsgAdapter(this,data);
		msg_list.setAdapter(msg_adapter);
		msg_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				Intent intent = new Intent();
				intent.putExtra("cname",uname);
				intent.putExtra("cid",data.get(position).uid);
				intent.setClass(MsgActivity.this, MsgChastActivity.class);	
				startActivity(intent);
			}
			
		});
	}

	/**
	 * @desc ��Ӱ�ť�¼�
	 */
	private void setAddFriend(int iLayoutId){
		ImageView msg_add = (ImageView) findViewById(iLayoutId);
		msg_add.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(MsgActivity.this, AddFriendActivity.class);	
				startActivity(intent);
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
				Arrays.asList(meunDatas),2,screenWidth);
		gdView01.setAdapter(gdAdapter);
	}
	
	@Override
	public boolean OnNewDataArrived(int aRequestType, int aErrCode, Object aData){
		if(!super.OnNewDataArrived(aRequestType, aErrCode, aData))
		{
			if(aRequestType == WsyProtocol.kRequestIdGetFriend && aData != null){
				data = (List<UserInfo>) aData;
				initListView();
			}
		}else{
			//showErrorToast(aErrCode);
		}
		return true;
	}
}
