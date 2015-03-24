package com.example.wswy;

import com.example.protocol.WsyMeta;
import com.example.protocol.WsyProtocol;
import com.example.protocol.WsyMeta.UserInfo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SendInviteActivity extends WsyBaseActivity{
	private String phone ;
	@Override
	public void onCreate(Bundle savedInstanceState){		
		super.onCreate(savedInstanceState);
		Intent intent  = getIntent();
		phone = intent.getStringExtra("phone").replace("-", "");
		showWaitDialog();
		WsyProtocol.getInstance().SendPhoneNum(SendInviteActivity.this,phone);
	}
	
	/**
	 * @desc 初始化view
	 */
	private void initView(UserInfo userInfo) {
		setContentView(R.layout.person_detail);
		TextView name = (TextView) findViewById(R.id.person_name);
		TextView _phone = (TextView) findViewById(R.id.person_phone);
		Button add = (Button) findViewById(R.id.person_add);
		name.setText(userInfo.uname);
		_phone.setText(phone);
		add.setOnClickListener(this);
		setLeftButton();
	}

	/**
	 * @desc 发送短信给某某用户  
	 * @param smsBody
	 */
	private void sendSMS(String phone, String smsBody){    
		Uri smsToUri = Uri.parse("smsto:"+phone);  		  
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);    
		intent.putExtra("sms_body", smsBody);  
		startActivity(intent);  
	}  
	
	/**
	 * @desc 返回
	 */
	public void setLeftButton(){
		ImageView leftBtn = (ImageView) findViewById(R.id.left_arrow);
		leftBtn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				SendInviteActivity.this.finish();
			}
		});
	}
	
	@Override
	public boolean onViewClick(View v){
		WsyProtocol.getInstance().SendAddFriend(SendInviteActivity.this,phone,uid);
		return false;	
	}
	
	@Override
	public boolean OnNewDataArrived(int aRequestType, int aErrCode, Object aData){
		Log.i("test","===="+aErrCode);	
		if(aErrCode == -1001){
			Toast toast = Toast.makeText(SendInviteActivity.this, "网络错误，请设置您的网络", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 10);
			toast.show();
			return true;
		}
		if(aErrCode == 0 && aRequestType == WsyProtocol.kRequestIdSendPhone){
			//用户不存在，则发送短信邀请
			sendSMS(phone,"万事无忧可以发送语音短信，挺简单的，推荐你使用一下");
			return true;
		}
		if(aRequestType == WsyProtocol.kRequestIdSendInvite && aErrCode ==2){
			Toast toast = Toast.makeText(SendInviteActivity.this, "已经发送邀请，无需重复发送", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 10);
			toast.show();
		}
		if(aRequestType == WsyProtocol.kRequestIdSendInvite && aErrCode ==3){
			Toast toast = Toast.makeText(SendInviteActivity.this, "他已经是您的好友，无需添加", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 10);
			toast.show();
		}
		if(!super.OnNewDataArrived(aRequestType, aErrCode, aData)){
			if (aRequestType == WsyProtocol.kRequestIdSendPhone){
				//用户存在，显示用户信息	
				WsyMeta.UserInfo userInfo = (UserInfo) aData;
				initView(userInfo);
			}
			if(aRequestType == WsyProtocol.kRequestIdSendInvite && aErrCode ==1){
				Toast toast = Toast.makeText(SendInviteActivity.this, "好友添加申请发送成功", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 10);
				toast.show();
			}
		}  
		return true;
	}
}
