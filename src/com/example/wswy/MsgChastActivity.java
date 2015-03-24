package com.example.wswy;

import java.util.ArrayList;
import java.util.List;

import com.example.adapter.ChatMsgViewAdapter;
import com.example.entity.ChatMsgEntity;
import com.example.protocol.WsyProtocol;
import com.example.protocol.WsyMeta.UserInfo;
import com.example.util.DateHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class MsgChastActivity extends WsyBaseActivity{
	private Button sendBtn ;
	private ListView listView;
	private EditText mEditText;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mData = new ArrayList<ChatMsgEntity>();
	private static String cid ;
	private static String cname;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_chat);
		setLeftButton();
		listView = (ListView) findViewById(R.id.listview);
		mEditText = (EditText) findViewById(R.id.et_sendmessage);
		
		Intent intent = getIntent();
		cid = intent.getStringExtra("cid");
		cname = intent.getStringExtra("cname");
		
		WsyProtocol.getInstance().getUserMsg(MsgChastActivity.this,uid,cid);
		sendMsg();
	}	
	
	/**
	 * @desc ��adapter
	 */
	private void initView() {
		mAdapter = new ChatMsgViewAdapter(this,mData);
		listView.setAdapter(mAdapter);
	}
	
	/**
	 * @desc ������Ϣ
	 */
	private void sendMsg() {
		sendBtn = (Button) findViewById(R.id.btn_send);
		SendButtonClickListener l = new SendButtonClickListener();
		sendBtn.setOnClickListener(l);
	}

	/**
	 * @desc ���÷��ذ�ť
	 */
	public void setLeftButton(){
		ImageView leftBtn = (ImageView) findViewById(R.id.left_arrow);
		leftBtn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				MsgChastActivity.this.finish();
			}
		});
	}
	
	/**
	 * �ڲ���ʵ��sendbutton����¼�����
	 * @author v_wyongwang
	 *
	 */
	class SendButtonClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			String contString = mEditText.getText().toString();
			if (contString.length() > 0) {				
				try{
					ChatMsgEntity entity = new ChatMsgEntity();
					entity.setDate(DateHelper.getDate());
					entity.setName(cname);
					entity.setisComMeg(false);
					entity.setText(contString);
					entity.setImage(utx);
					
					mData.add(entity);
					if(mAdapter == null){
						mAdapter = new ChatMsgViewAdapter(MsgChastActivity.this,mData);
						listView.setAdapter(mAdapter);
					}else{
						mAdapter.notifyDataSetChanged();
					}
					mEditText.setText("");
					listView.setSelection(listView.getCount()-1); //listview 
					saveMsg2Db(contString,cid);
				}catch(Exception e){
					Log.i("[Dynamic Items]", "Tried to add null value");
				}
				
			}
		}
		
		/**
		 * @desc 保存数据到DB
		 * @param msg
		 */
		public void saveMsg2Db(String msg,String id){
			WsyProtocol.getInstance().SendChatMsg(MsgChastActivity.this,msg,id,uid);
		}
	}
	
	@Override
	public boolean OnNewDataArrived(int aRequestType, int aErrCode, Object aData){
		if(!super.OnNewDataArrived(aRequestType, aErrCode, aData))
		{
			if(aRequestType == WsyProtocol.KRequestIdGetMsgInfo && aData != null){
				mData = (List<ChatMsgEntity>) aData;
				initView();
			}
		}else{
			showErrorToast(aErrCode);
		}
		return true;
	}
}
