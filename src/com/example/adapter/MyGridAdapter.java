package com.example.adapter;

import com.example.wswy.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class MyGridAdapter extends BaseAdapter{   
	private Context mContext;
	private int sWidth;
	
	private int [] images = new int[] { R.drawable.nine01, R.drawable.nine02,
			R.drawable.nice03, R.drawable.nine04, R.drawable.nine05,
			R.drawable.nine06, R.drawable.nine07, R.drawable.nine08 };
	

	public MyGridAdapter(Context context, int screenWidth){
		this.mContext=context;
		this.sWidth=screenWidth;
	}
	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parentView){
		int width = this.sWidth/4;
		ImageView imageView=new ImageView(mContext);
		imageView.setLayoutParams(new GridView.LayoutParams(width,width));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(images[position]);
        imageView.setTag(position);
        
        imageView.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				int tag = (Integer) v.getTag();
				switch(tag){
					case 0:
						MyGridAdapter.this.openNetPage("http://www.baidu.com/");	
						break;
					case 1:
						MyGridAdapter.this.openNetPage("http://m.hao123.com/");	
						break;
					case 2:
						MyGridAdapter.this.openNetPage("http://www.youku.com/");
						break;
					case 3:
						MyGridAdapter.this.openNetPage("http://map.baidu.com/");
						break;
					case 4:
						MyGridAdapter.this.openNetPage("http://sina.cn/");	
						break;
					case 5:
						MyGridAdapter.this.openNetPage("http://3gqq.qq.com/");	
						break;
					case 6:
						MyGridAdapter.this.openNetPage("http://v.m.hao123.com/");	
						break;
					case 7:
						MyGridAdapter.this.openNetPage("http://m.hao123.com/n/v/tianqi?z=107077&level=1&page=index_pt&ver=1&pos=tjfl/");
						break;
				}
			}
		});
        return imageView;
	}
	
	/**
	 * @desc open a website from system browser
	 * @param website string
	 */
	private void openNetPage(String website){
		Uri uri = Uri.parse(website);
		Intent intent = new Intent(Intent.ACTION_VIEW,uri);
		mContext.startActivity(intent);
	}

}
