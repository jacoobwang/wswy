package com.example.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.entity.ChatMsgEntity;
import com.example.imageCache.ImageStore;
import com.example.wswy.R;


public class ChatMsgViewAdapter extends BaseAdapter {
	
	private Context context;
	private LayoutInflater layoutInflater;
	private List<ChatMsgEntity> coll ; //�洢��Ϣ��bean����
	
	public static interface IMsgViewType{
		int IMVT_COM_MSG = 0; //��������Ϣ
		int IMVT_TO_MSG = 1;  //��������Ϣ
	}
	
	public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll){
		this.context = context;
		this.coll   = coll;
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return coll.size();
	}

	@Override
	public Object getItem(int position) {
		return coll.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public int getItemViewType(int position){
		ChatMsgEntity msgEntity = coll.get(position);
		if(msgEntity.getisComMeg()){
			return IMsgViewType.IMVT_COM_MSG;		
		}
		return IMsgViewType.IMVT_TO_MSG;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ChatMsgEntity entity = coll.get(position);
		boolean isComMsg = entity.getisComMeg();
		Log.i("adapter","---->"+position);
		Log.i("adapter","---->"+isComMsg);
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			if (isComMsg) {
				convertView = layoutInflater.inflate(
						R.layout.chatting_item_msg_text_left, null);
			} else {
				convertView = layoutInflater.inflate(
						R.layout.chatting_item_msg_text_right, null);
			}

			viewHolder = new ViewHolder();
			viewHolder.tvSendTime = (TextView) convertView
					.findViewById(R.id.tv_sendtime);
			viewHolder.tvUserName = (TextView) convertView
					.findViewById(R.id.tv_username);
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);
			viewHolder.tvTime = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.tvUserHead = (ImageView) convertView.findViewById(R.id.iv_userhead);
			viewHolder.isComMsg = isComMsg;

			convertView.setTag(viewHolder);
		} else {
			if (isComMsg) {
				convertView = layoutInflater.inflate(
						R.layout.chatting_item_msg_text_left, null);
			} else {
				convertView = layoutInflater.inflate(
						R.layout.chatting_item_msg_text_right, null);
			}

			viewHolder = new ViewHolder();
			viewHolder.tvSendTime = (TextView) convertView
					.findViewById(R.id.tv_sendtime);
			viewHolder.tvUserName = (TextView) convertView
					.findViewById(R.id.tv_username);
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);
			viewHolder.tvTime = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.tvUserHead = (ImageView) convertView.findViewById(R.id.iv_userhead);
			viewHolder.isComMsg = isComMsg;
		}

		viewHolder.tvSendTime.setText(entity.getDate());
	
		viewHolder.tvContent.setText(entity.getText());			
		viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		viewHolder.tvTime.setText("");
		viewHolder.tvUserHead.setTag(R.id.tag_first, entity.getImage());
		ImageStore.Instance().SetImageViewBitmap(viewHolder.tvUserHead,false);
		
		viewHolder.tvUserName.setText(entity.getName());
		return convertView;
	}
	
	static class ViewHolder {
		public TextView tvSendTime;
		public TextView tvUserName;
		public TextView tvContent;
		public TextView tvTime;
		public boolean isComMsg = true;
		public ImageView tvUserHead;
	}

}
