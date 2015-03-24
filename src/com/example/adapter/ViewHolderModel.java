package com.example.adapter;

import java.util.List;


import android.content.Context;
import android.view.View;

interface IViewHolderController 
{
	ViewHolderModel InitViewHoler(View convertView, int aPos);
    void SetViewHolerValues(Object aItemObject);
}

public abstract class ViewHolderModel implements IViewHolderController{	
	 public List<?> list;
	 public Context context;

	public ViewHolderModel(Context context, List<?> list) {
		this.context = context;
		this.list = list;
	}
}
