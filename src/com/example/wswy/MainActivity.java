package com.example.wswy;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.adapter.MyGridAdapter;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private int[] images = null;
	private String[] texts = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_body);		

		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		
		GridView gridView01 = (GridView) findViewById(R.id.gridOne);
		gridView01.setAdapter(new MyGridAdapter(this, screenWidth));
		
		BindWin8Listener();
	}
	
	/**
	 * @desc ��win8����¼�
	 * @author v_wyongwang
	 */
	public void BindWin8Listener(){	
		TextView home_win8_01 = (TextView) findViewById(R.id.home_win8_01);
		TextView home_win8_02 = (TextView) findViewById(R.id.home_win8_02);
		TextView home_win8_03 = (TextView) findViewById(R.id.home_win8_03);
		TextView home_win8_04 = (TextView) findViewById(R.id.home_win8_04);
		TextView home_win8_05 = (TextView) findViewById(R.id.home_win8_05);
		TextView home_win8_06 = (TextView) findViewById(R.id.home_win8_06);
		TextView home_win8_07 = (TextView) findViewById(R.id.home_win8_07);
		RadioButton callButton = (RadioButton) findViewById(R.id.call_button);
		RadioButton talkButton = (RadioButton) findViewById(R.id.talk_button);
		
		home_win8_01.setOnClickListener(new Win8OnClickListener(1));
		home_win8_02.setOnClickListener(new Win8OnClickListener(2));
		home_win8_03.setOnClickListener(new Win8OnClickListener(3));
		home_win8_04.setOnClickListener(new Win8OnClickListener(4));
		home_win8_05.setOnClickListener(new Win8OnClickListener(5));
		home_win8_06.setOnClickListener(new Win8OnClickListener(6));
		home_win8_07.setOnClickListener(new Win8OnClickListener(7));
		callButton.setOnClickListener(new Win8OnClickListener(8));
		talkButton.setOnClickListener(new Win8OnClickListener(9));
	}
	
	/**
	 * @desc ����ҳ�ײ���������ť
	 * @author v_wyongwang
	 */
	public void BindeHomeBottomListener(){
		RadioGroup homeRadio = (RadioGroup) findViewById(R.id.home_radio);	
	}
	
	/**
	 * @desc �ڲ��࣬ʵ��һ������¼�������
	 * @author v_wyongwang
	 */
	public class Win8OnClickListener implements View.OnClickListener{
		private int index = 1;
		public Win8OnClickListener(int position){
			this.index = position;		
		}
		@Override
		public void onClick(View view) {
			switch(index){
				case 1:
					Intent intent1 = new Intent();
					intent1.setClass(MainActivity.this, StoreActivity.class);
					startActivity(intent1);
					break;
				case 2:
					Intent intent2 = new Intent();
					intent2.putExtra("type", "0");
					intent2.setClass(MainActivity.this, CartActivity.class);
					startActivity(intent2);
					break;
				case 3:
					Intent intent3 = new Intent();
					intent3.setClass(MainActivity.this, MsgHandleActivity.class);
					startActivity(intent3);
					break;
				case 4:
					openNetWeb("http://touch.qunar.com/h5/train/?from=touchindex");
					break;
				case 5:
					openNetWeb("http://m.hao123.com/n/v/meishi?z=107077&level=1&page=index_pt&ver=1&pos=tjfl");
					break;
				case 6:
					openNetWeb("http://m.tuniu.com/");
					break;
				case 7:
					openNetWeb("http://m.hao123.com/n/v/remenyouxi?z=1&set=1");
					break;	
				case 8:
					// call  phone
					String phoneNum = "tel:18938886180";
					Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse(phoneNum));
					startActivity(intent);
					break;	
				case 9:
					Intent intent9 = new Intent();
					intent9.setClass(MainActivity.this,MsgActivity.class);
					startActivity(intent9);
					break;	
			}
			
		}
		
		public void openNetWeb(String website){
			Uri uri = Uri.parse(website);
			Intent intent = new Intent(Intent.ACTION_VIEW,uri);
			startActivity(intent);	
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
