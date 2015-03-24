package com.example.wswy;

import com.example.protocol.WsyProtocol;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EditInfoActivity extends WsyBaseActivity{
	String msg = "";
	String msgType = "";
	private EditText editItem;
	@Override
	public void onCreate(Bundle savedInstanceState){ 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editinfo);
		setLeftButton();
		
		Intent intent = getIntent();
		msg = intent.getStringExtra("msg");
		msgType= intent.getStringExtra("msgType");
		
		editItem = (EditText) findViewById(R.id.edit_item);
		editItem.setHint(msg);
		
		Button edit_cancel = (Button) findViewById(R.id.edit_cancel); 
		Button edit_save = (Button) findViewById(R.id.edit_save);
		
		edit_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// ȡ��
				EditInfoActivity.this.finish();
			}
		});
		
		edit_save.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				String msg = editItem.getText().toString();
				WsyProtocol.getInstance().SendEditInfo(EditInfoActivity.this,msgType,msg,uid);
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
				EditInfoActivity.this.finish();
			}
		});
	}
	
	@Override
	public boolean OnNewDataArrived(int aRequestType, int aErrCode, Object aData){
		if(!super.OnNewDataArrived(aRequestType, aErrCode, aData))
		{
			if(aRequestType == WsyProtocol.KRequestIdSendEdit){
				Toast.makeText(EditInfoActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
			}		
		}else{
			Toast.makeText(EditInfoActivity.this, "网络错误，请稍候再来", 
					Toast.LENGTH_SHORT).show();
		}
		return true;
	}
}
