package com.example.adapter;

import java.util.List;

import com.example.wswy.R;
import com.example.wswy.R.color;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;

public class GridBottomAdapter extends BaseAdapter{
	    Context context;
	    List list;
	    int length;
	    int index;
	    int sWidth;
	    
	    private int[] images = new int[]{
	    	R.drawable.bottom_friend_bg,
	    	R.drawable.bottom_index_bg,
	    	R.drawable.bottom_buy_bg,
	    	R.drawable.bottom_my_bg
	    };
	    public GridBottomAdapter(Context c,List datalist,int index,int screenWidth){
	    	this.context = c;
	    	this.sWidth=screenWidth;
	    	list = datalist;
	    	length = list != null ? list.size() : 0;
	    	this.index = index;
	    }
	
		@Override
		public int getCount() {
			return images.length;
			//return length;
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
		public View getView(int position,View convertView, ViewGroup root){
			int width = this.sWidth/4;
			
			ImageView imageView;
			
			if(convertView == null){
				imageView = new ImageView(context);
			}else{
				imageView = (ImageView)convertView;
			}
			
			imageView.setLayoutParams( new GridView.LayoutParams(width,width));
			imageView.setImageResource(images[position]);
			return imageView;
		}
		
		
		
		/**
		 * @desc 备份 ，改动原因，需要用图片代替文字menu
		 * @param position
		 * @param convertView
		 * @param arg2
		 * @return
		 */
		public View getView2(int position, View convertView, ViewGroup arg2) {
			TextView tv;
			if(convertView == null){
				tv = new TextView(context);
			}else{
				tv = (TextView)convertView;
			}
			if(length==5){
				tv.setLayoutParams( new GridView.LayoutParams(88,48));
			}
			else{
				tv.setLayoutParams( new GridView.LayoutParams(LayoutParams.FILL_PARENT,48));
			}
			if(position == index){
				tv.setTextColor(color.bottom_high);
			}else{
				tv.setTextColor(Color.WHITE);
			}
			tv.setGravity(Gravity.CENTER);
			tv.setTextSize(14);
			tv.setText(list.get(position).toString());
	 
			return tv;
		}
}
