package com.example.adapter;

import java.util.List;

import com.example.imageCache.ImageStore;
import com.example.protocol.WsyMeta;
import com.example.wswy.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListMsgAdapter extends BaseAdapter{

	private Context mContext;
	private List<WsyMeta.UserInfo> aData ;
	
	public ListMsgAdapter(Context context,List<WsyMeta.UserInfo> list){
		this.mContext = context;	
		this.aData = list;
	}
	@Override
	public int getCount() {
		return this.aData.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup root) {
		if(view == null){
			  view = LayoutInflater.from(mContext).inflate(R.layout.msg_listrow, null);
			  //ImageView msgImageView = new ImageView(mContext);
			  //msgImageView.setImageResource(R.drawable.redbg);
			 // msgImageView.setScaleType(ImageView.ScaleType.CENTER);
			  //msgImageView.setPadding(160,30,30,20);
			 // ((ViewGroup) view).addView(msgImageView);
			  
			  TextView uname = (TextView) view.findViewById(R.id.list_uname);
			  ImageView mListTx = (ImageView) view.findViewById(R.id.msg_listrowtx);
			  mListTx.setTag(R.id.tag_first, this.aData.get(position).uTx);
			  ImageStore.Instance().SetImageViewBitmap(mListTx, false);
			  
			  ImageView mImage = (ImageView) view.findViewById(R.id.msg_listFlag);
			  String mFlag = this.aData.get(position).mFlag;
			  if(mFlag.equalsIgnoreCase("1")) mImage.setVisibility(view.VISIBLE); 
			  uname.setText(this.aData.get(position).uname);
		}
		return view;
	}

}
