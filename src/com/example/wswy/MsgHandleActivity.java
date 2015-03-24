package com.example.wswy;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.protocol.WsyMeta;
import com.example.protocol.WsyProtocol;
import com.example.protocol.WsyMeta.UserInfo;
import com.example.protocol.WsyProtocol.MDataUpdateNotify;
import com.example.view.PullToRefreshListView.OnRefreshListener;
import com.example.wswy.AddFriendActivity.MyListAdapter;

public class MsgHandleActivity  extends WsyBaseActivity implements OnRefreshListener{
	MyListAdapter myAdapter =null;
	private Context mContext = null;
	private List<WsyMeta.UserInfo> list = new ArrayList<UserInfo>();
	private Button success = null;
	private Button refuse = null;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		mContext = this;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.msg_handle);
		
		showWaitDialog();
		WsyProtocol.getInstance().getHandleMsg(MsgHandleActivity.this, uid);
		
		setLeftButton();	
	}
	
	private void initView(){
		ListView lsView = (ListView) findViewById(R.id.msg_handle_list);
		myAdapter = new MyListAdapter(this);
		lsView.setAdapter(myAdapter);
		
	}

	@Override
	public void onRefresh(int refreshType) {
		// TODO 自动生成的方法存根
	}

	
	@Override
	public boolean OnNewDataArrived(int aRequestType, int aErrCode, Object aData){
		if(aErrCode == 4){
			showErrorToast(4);
		}
		if(!super.OnNewDataArrived(aRequestType, aErrCode, aData))
		{
			if(aRequestType == WsyProtocol.KRequestIdGetHandleInfo &&
					aData != null){
				list = (List<UserInfo>) aData;
				initView();
			}
		}else{
			//error
			showErrorToast(aErrCode);
		}
		return true;
	}
	/**
	 * @desc 设置返回按钮
	 */
	public void setLeftButton(){
		ImageView leftBtn = (ImageView) findViewById(R.id.left_arrow);
		leftBtn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				MsgHandleActivity.this.finish();
			}
		});
	}
	
	class MyListAdapter extends BaseAdapter{
		
		public MyListAdapter(Context context){
			mContext = context;
		}
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tx = null;
			final Holder holder;   
			if(convertView == null){
				holder=new Holder();  
				convertView = LayoutInflater.from(mContext).
						inflate(R.layout.handle_list, null);
				TextView txView = (TextView) convertView.findViewById(R.id.handle_text);
				txView.setText(list.get(position).uname+"请求加您为好友");
				holder.success = (Button) convertView.findViewById(R.id.handle_succ);
				holder.refuse = (Button) convertView.findViewById(R.id.handle_fail);
				holder.cellphone = list.get(position).cellphone;
			    convertView.setTag(holder);   
			}else{
				holder=(Holder) convertView.getTag();   
			}
			
			OnClickListener listener=new OnClickListener(){   
	            @Override  
	            public void onClick(View v)   
	            {   
	                if(v==holder.success){   
	                   Log.i("test","cusse");
	                   WsyProtocol.getInstance().sendHandle((MDataUpdateNotify) mContext,uid,"1",holder.cellphone);
	                   Toast.makeText(mContext, "操作成功!", Toast.LENGTH_SHORT).show();
	                }   
	                if(v==holder.refuse){   
	                	 Log.i("test","failes");
	                	 WsyProtocol.getInstance().sendHandle((MDataUpdateNotify) mContext,uid,"0",holder.cellphone);
	                	 Toast.makeText(mContext, "操作成功!", Toast.LENGTH_SHORT).show();
	                }              
	            }   
	        };  
	        holder.success.setOnClickListener(listener);
	        holder.refuse.setOnClickListener(listener);
			return convertView;
		}		
	}
	
	class Holder{   
        public Button success;   
        public Button refuse;   
        public String cellphone;
    }   
	
}
