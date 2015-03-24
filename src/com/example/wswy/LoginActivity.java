package com.example.wswy;

import com.example.protocol.WsyMeta;
import com.example.protocol.WsyMeta.UserInfo;
import com.example.protocol.WsyProtocol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends WsyBaseActivity{
	
	private EditText login1 = null;
	private EditText login2 = null;
	private Button login_forget = null;
	private Button login_submit = null;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		login1  = (EditText) findViewById(R.id.login1);
		login2  = (EditText) findViewById(R.id.login2);
		login2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		
		login_forget = (Button) findViewById(R.id.login_forget);
		login_submit = (Button) findViewById(R.id.login_submit);
		
		login_forget.setOnClickListener(this);
		login_submit.setOnClickListener(this);
	}
	
	@Override
	public boolean onViewClick(View v)
	{
		int id = v.getId();
		switch(id){
			case R.id.login_forget:
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegActivity.class);
				startActivity(intent);
				break;
			case R.id.login_submit:
				sendContext2Server();
				break;
			default:break;	
		}
		return false;
	}

	private void sendContext2Server() {
		String username = login1.getText().toString();
		String userpwd  = login2.getText().toString();
		
		if(username.length() == 0){
			Toast.makeText(LoginActivity.this, "请先填写用户名", Toast.LENGTH_SHORT).show();
			return;
		}
		if(userpwd.length() == 0){
			Toast.makeText(LoginActivity.this, "请填写密码", Toast.LENGTH_SHORT).show();
			return;
		}
		WsyProtocol.getInstance().SendLoginInfo(LoginActivity.this, username, userpwd); 
	}
	
	@Override
	public boolean OnNewDataArrived(int aRequestType, int aErrCode, Object aData){
		if(aErrCode == 4){
			Toast toast = Toast.makeText(LoginActivity.this, "账户名和密码错误，请检查您的用户名和密码是否正确", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 10);
			toast.show();
			return true;
		}
		if(!super.OnNewDataArrived(aRequestType, aErrCode, aData)){
			if (aRequestType == WsyProtocol.kRequestIdSendLoginInfo){
				saveSharedInfo(aData);
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MainActivity.class);
				startActivity(intent);
			}
		}  
		return true;
	}

	private void saveSharedInfo(Object aData) {
		WsyMeta.UserInfo userInfo = (UserInfo) aData;
		Editor userinfo = getSharedPreferences("user_info",0).edit();
		userinfo.putString("uid", userInfo.uid);
		userinfo.putString("uname", userInfo.uname);
		userinfo.putString("utx", userInfo.uTx);
		userinfo.commit();
	}
}
