package com.example.wswy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends WsyBaseActivity{
	private final int SPLASH_DISPLAY_LENGTH = 3000; // 3sec
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		gonext();
	}

	private void gonext() {
		new Handler().postDelayed(
				new Runnable() {			
					@Override
					public void run() {
						Intent mainIntent = new Intent(SplashActivity.this,LoginActivity.class);
						SplashActivity.this.startActivity(mainIntent);
						SplashActivity.this.finish();
					}
				},SPLASH_DISPLAY_LENGTH);
	}
}
