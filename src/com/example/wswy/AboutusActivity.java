package com.example.wswy;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AboutusActivity extends WsyBaseActivity{

	@Override
	public void onCreate(Bundle savedInstanceState){ 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutus);
		setLeftButton();
	}	
	
	/**
	 * @desc …Ë÷√∑µªÿ∞¥≈•
	 */
	public void setLeftButton(){
		ImageView leftBtn = (ImageView) findViewById(R.id.left_arrow);
		leftBtn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				AboutusActivity.this.finish();
			}
		});
	}
}
