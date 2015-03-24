package com.example.adapter;

import com.example.wswy.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridAdapter extends BaseAdapter{
	private Context mContext;
	private final int[] buttonImages;
	private GridView mGv;
	private static int ROW_NUMBER = 2;
	
	public GridAdapter(GridView gv, Context c, int[] v){
		this.mContext = c;
		this.buttonImages = v;
		this.mGv = gv;
	}
	@Override
	public int getCount() {
		return this.buttonImages.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getViewBak(int position, View convertView, ViewGroup parentView) {
		convertView = LayoutInflater.from(mContext).inflate(R.layout.nine_item, null);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.ItemOneImage);
		imageView.setImageResource(buttonImages[position]);
		LayoutParams param = new AbsListView.LayoutParams(android.view.ViewGroup.
				LayoutParams.FILL_PARENT,mGv.getHeight()/ROW_NUMBER);
		convertView.setLayoutParams(param);
        return convertView;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parentView){
		ImageView imageView;
        if (convertView == null) { 
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(110, 110));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(buttonImages[position]);
        return imageView;
	}
}
