package com.example.wswy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.protocol.WsyProtocol;
import com.example.util.StringHelper;

public class RegActivity extends WsyBaseActivity{
	private EditText mPhone1 = null;
	private EditText mPhone2 = null;
	private EditText mPhone3 = null;
	private EditText mPhone4 = null;
	private EditText mPhone5 = null;
	private EditText mPhone6 = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_activity);
		
		mPhone1 = (EditText) findViewById(R.id.reg_cellp);
		mPhone2 = (EditText) findViewById(R.id.reg_phone);
		mPhone3 = (EditText) findViewById(R.id.reg_qq);
		mPhone4 = (EditText) findViewById(R.id.reg_address);
		mPhone5 = (EditText) findViewById(R.id.reg_name);
		mPhone6 = (EditText) findViewById(R.id.reg_pwd);
		
		Button regBtn = (Button) findViewById(R.id.reg_sub);
		regBtn.setOnClickListener(this);
		
		OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				EditText _v=(EditText) v;
		        if (!hasFocus) {// ʧȥ����
		            _v.setHint(_v.getTag().toString());
		        } else {
		            String hint=_v.getHint().toString();
		            _v.setTag(hint);
		            _v.setHint("");
		        }
			}
		}; 
		
		mPhone1.setOnFocusChangeListener(focusChangeListener);
		mPhone2.setOnFocusChangeListener(focusChangeListener);
		mPhone3.setOnFocusChangeListener(focusChangeListener);
		mPhone4.setOnFocusChangeListener(focusChangeListener);
		mPhone5.setOnFocusChangeListener(focusChangeListener);
		mPhone6.setOnFocusChangeListener(focusChangeListener);
	}
	
	@Override
	public boolean onViewClick(View v)
	{
		int id = v.getId();
		switch(id){
			case R.id.reg_sub:
				sendContext2Server();
				break;
			default:
				break;
		}
		return false;
	}

	private void sendContext2Server() { 
		String phone1 = mPhone1.getText().toString();
		String phone2 = mPhone2.getText().toString();
		String phone3 = mPhone3.getText().toString();
		String phone4 = mPhone4.getText().toString();
		String phone5 = mPhone5.getText().toString();
		String phone6 = mPhone6.getText().toString();
		if(!StringHelper.isCellPhone(phone1)){
			Toast.makeText(RegActivity.this, "手机号码填写不正确", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!StringHelper.isPhone(phone2)){
			Toast.makeText(RegActivity.this, "电话号码填写不正确", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!StringHelper.isNumberic(phone3)){
			Toast.makeText(RegActivity.this, "QQ号码填写不正确", Toast.LENGTH_SHORT).show();
			return;
		}
		if(phone4.length() == 0 || phone4.trim().equals("")){
			Toast.makeText(RegActivity.this, "地址填写不正确", Toast.LENGTH_SHORT).show();
			return;
		}
		if(phone5.length() == 0 || phone5.trim().equals("")){
			Toast.makeText(RegActivity.this, "用户名填写不正确", Toast.LENGTH_SHORT).show();
			return;
		}
		if(phone6.length() == 0 || phone6.trim().equals("")){
			Toast.makeText(RegActivity.this, "密码填写不正确", Toast.LENGTH_SHORT).show();
			return;
		}
		if(phone4.length()>=100){
			Toast.makeText(RegActivity.this, "地址长度超过最大长度", Toast.LENGTH_SHORT).show();
			return;
		}
		if(phone5.length()>=20){
			Toast.makeText(RegActivity.this, "用户名长度设置过长", Toast.LENGTH_SHORT).show();
			return;
		}
		if(phone6.length()>=20){
			Toast.makeText(RegActivity.this, "密码长度设置过长", Toast.LENGTH_SHORT).show();
			return;
		}
		WsyProtocol.getInstance().SendRegInfo(RegActivity.this, phone1, phone2, 
				phone3, phone4, phone5, phone6);
	}
	
	@Override
	public boolean OnNewDataArrived(int aRequestType, int aErrCode, Object aData){
		if(!super.OnNewDataArrived(aRequestType, aErrCode, aData)){
			if (aRequestType == WsyProtocol.KRequestIdSendRegInfo){
				Toast.makeText(RegActivity.this, "注册成功，欢迎使用万事无忧", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(RegActivity.this, MainActivity.class);
				startActivity(intent);
			}	
		}	
		return true;
	}
}
