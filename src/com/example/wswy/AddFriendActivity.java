package com.example.wswy;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AddFriendActivity extends WsyBaseActivity implements OnItemClickListener{
	private Context mContext = null;
	/** ��ȡphone���ֶ� */
	private static final String[] PHONES_PROJECTION = new String[]{
		Phone.DISPLAY_NAME,Phone.NUMBER,Phone.PHOTO_ID,Phone.CONTACT_ID
	};
	private static final int PHONES_DISPLAY_NAME_INDEX=0;
	private static final int PHONES_NUMBER_INDEX =1;
	private static final int PHONES_PHOTO_ID_INDEX=2;
	private static final int PHONES_CONTACT_ID_INDEX=3;
	
	private ArrayList<String> mContactsName = new ArrayList<String>();
	private ArrayList<String> mContactsNumber = new ArrayList<String>();
	
	MyListAdapter myAdapter =null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		mContext = this;
		getPhoneContacts();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friend);
		setLeftButton();
		
		ListView lsView = (ListView) findViewById(R.id.contact_list);
		myAdapter = new MyListAdapter(this);
		lsView.setAdapter(myAdapter);
		lsView.setOnItemClickListener(this);
	}
	
	private void getPhoneContacts() {  
	    ContentResolver resolver = mContext.getContentResolver();  
	  
	    // ��ȡ�ֻ���ϵ��  
	    Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  
	    if (phoneCursor != null) {  
	        while (phoneCursor.moveToNext()) {  
	  
		        String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);  
		        if (TextUtils.isEmpty(phoneNumber))  
		            continue;  
		        String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);  
		          
		        Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);  
		  
		        Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);  	     
		  
		        //photoid 
		        /**
		        if(photoid > 0 ) {  
		            Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);  
		            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);  
		            contactPhoto = BitmapFactory.decodeStream(input);  
		        }else {  
		            contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.contact_photo);  
		        }**/  
		        //mContactsPhonto.add(contactPhoto);  
		        mContactsName.add(contactName);  
		        mContactsNumber.add(phoneNumber);    
	        }  
	  
	        phoneCursor.close();  
	    }  
	}  

	/**
	 * @desc ���÷��ذ�ť
	 */
	public void setLeftButton(){
		ImageView leftBtn = (ImageView) findViewById(R.id.left_arrow);
		leftBtn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				AddFriendActivity.this.finish();
			}
		});
	}
	
	class MyListAdapter extends BaseAdapter{
		
		public MyListAdapter(Context context){
			mContext = context;
		}
		@Override
		public int getCount() {
			return mContactsName.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tx = null;
			if(convertView == null || position <mContactsName.size()){
				convertView = LayoutInflater.from(mContext).
						inflate(R.layout.contact, null);
				tx = (TextView) convertView.findViewById(R.id.contact_name);
			}
			tx.setText(mContactsName.get(position));
			return convertView;
		}		
	}

	@Override
	public void onItemClick(AdapterView<?> root, View covertView, int position, long arg3) {
		//跳走页面
		Intent intent = new Intent();
		intent.putExtra("phone",mContactsNumber.get(position));
		intent.setClass(AddFriendActivity.this,SendInviteActivity.class);
		startActivity(intent);
	}
}
