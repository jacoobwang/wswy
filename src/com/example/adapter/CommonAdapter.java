package com.example.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CommonAdapter extends BaseAdapter{
	
    private int customLayoutId;
    protected ViewHolderModel iHolder = null;
    private LayoutInflater iInflater;
    
    public CommonAdapter(ViewHolderModel vModel, int aLayoutId){
    	this.customLayoutId = aLayoutId;
    	this.iHolder = vModel;
    	this.iInflater = LayoutInflater.from(this.iHolder.context);
    }
    
    public void SetListData(List<?> aList){
    	this.iHolder.list = aList;
    }
    
	@Override
	public int getCount() {
		return this.iHolder.list.size();
	}

	@Override
	public Object getItem(int position) {
		return this.iHolder.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderModel vHolder = null;
		if(convertView == null){
			convertView = this.iInflater.inflate(this.customLayoutId, parent, false);
			vHolder = iHolder.InitViewHoler(convertView,position);
			convertView.setTag(vHolder);
		}
		else{
			vHolder = (ViewHolderModel) convertView.getTag();
		}
		Object vItemObject = this.getItem(position);
		vHolder.SetViewHolerValues(vItemObject);
		return convertView;
	}

}
