package com.example.wswy;

import java.io.ByteArrayOutputStream;
import java.io.File;

import com.example.imageCache.ImageStore;
import com.example.protocol.WsyMeta;
import com.example.protocol.WsyProtocol;
import com.example.protocol.WsyMeta.UserInfo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MeInfoDetailActivity extends WsyBaseActivity{
	private WsyMeta.UserInfo userInfo;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	private ImageView faceImage;
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = "faceImage.jpg";
	@Override
	public void onCreate(Bundle savedInstanceState){ 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_info);
		WsyProtocol.getInstance().getUserDetail(MeInfoDetailActivity.this,uid);
		faceImage = (ImageView) findViewById(R.id.me_tx);
		
		//显示头像
		if(utx != null && utx.length()>0){
			faceImage.setTag(R.id.tag_first, utx);
			ImageStore.Instance().SetImageViewBitmap(faceImage, false);
		}	
		setLeftButton();
	}		
	
	/**
	 * @desc 绑定事件到对应的按钮
	 */
	private void bindItemClickListener() {
		View detail_txImage = findViewById(R.id.tx_rea);
		View detail_nickname = findViewById(R.id.detail_nickname);
		View detail_cellphone = findViewById(R.id.detail_cellphone);
		View detail_phone = findViewById(R.id.detail_phone);
		View detail_qq = findViewById(R.id.detail_qq);
		View detail_address = findViewById(R.id.detail_address);
		View detail_about_us = findViewById(R.id.detail_about_us);
		View detail_contact = findViewById(R.id.detail_contact);
		
		TextView me_tx = (TextView) findViewById(R.id.me_txnickname);
		TextView me_txphone = (TextView) findViewById(R.id.me_txphone);
		TextView me_txtel = (TextView) findViewById(R.id.me_txtel);
		TextView me_txqq = (TextView) findViewById(R.id.me_txqq);
		TextView me_txaddress = (TextView) findViewById(R.id.me_txaddress);
		Button login_out = (Button) findViewById(R.id.login_out);
		
		me_tx.setText(userInfo.uname);
		me_txphone.setText(userInfo.cellphone);
		me_txtel.setText(userInfo.phone);
		me_txqq.setText(userInfo.qq);
		me_txaddress.setText(userInfo.address);
		
		detail_txImage.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				showImageDialog();	
			}
		});
		
		detail_nickname.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("msg", uname);
				intent.putExtra("msgType","nickname");
				intent.setClass(MeInfoDetailActivity.this, EditInfoActivity.class);
				startActivity(intent);
			}
		});
		detail_cellphone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("msg", userInfo.cellphone);
				intent.putExtra("msgType","cellphone");
				intent.setClass(MeInfoDetailActivity.this, EditInfoActivity.class);
				startActivity(intent);
			}
		});
		detail_phone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("msg", userInfo.phone);
				intent.putExtra("msgType","phone");
				intent.setClass(MeInfoDetailActivity.this, EditInfoActivity.class);
				startActivity(intent);
			}
		});
		detail_qq.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("msg", userInfo.qq);
				intent.putExtra("msgType","qq");
				intent.setClass(MeInfoDetailActivity.this, EditInfoActivity.class);
				startActivity(intent);
			}
		});
		detail_address.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("msg", userInfo.address);
				intent.putExtra("msgType","address");
				intent.setClass(MeInfoDetailActivity.this, EditInfoActivity.class);
				startActivity(intent);
			}
		});
		detail_about_us.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(MeInfoDetailActivity.this, AboutusActivity.class);
				startActivity(intent);
			}
		});
		detail_contact.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String phoneNum = "tel:18938886180";
				Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse(phoneNum));
				startActivity(intent);
			}
		});
		login_out.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				Editor userinfo = getSharedPreferences("user_info",0).edit();
				userinfo.clear();
				Intent intent = new Intent();
				intent.setClass(MeInfoDetailActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		//结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory()
									+ IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(MeInfoDetailActivity.this, "未找到存储卡，无法存储照片！",
							Toast.LENGTH_LONG).show();
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
		
	}
	
	/**
	 * @desc 显示选择按钮
	 */
	public void showImageDialog() {
		new AlertDialog.Builder(this)
		.setTitle("设置头像")
		.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					Intent intentFromGallery = new Intent();
					intentFromGallery.setType("image/*"); // 设置文件类型
					intentFromGallery
							.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intentFromGallery,
							IMAGE_REQUEST_CODE);
					break;
				case 1:

					Intent intentFromCapture = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					// 判断存储卡是否可以用，可用进行存储
					if (hasSdcard()) {
						intentFromCapture.putExtra(
								MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(new File(Environment
										.getExternalStorageDirectory(),
										IMAGE_FILE_NAME)));
					}

					startActivityForResult(intentFromCapture,
							CAMERA_REQUEST_CODE);
					break;
				}
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}
	
	/**
	 * @desc 裁剪图片方法实现
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}
	
	/**
	 * @desc 保存裁剪之后的图片数据
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			faceImage.setImageDrawable(drawable);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			photo.compress(CompressFormat.JPEG, 100, bos);
			byte[] abyte = bos.toByteArray();
			WsyProtocol.getInstance().UpdateAvatar(MeInfoDetailActivity.this, uid, abyte);
		}
	}

	
	/**
	 * @desc 检查是否支持存储
	 * @return
	 */
	public static boolean hasSdcard(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * @desc 设置返回按钮
	 */
	public void setLeftButton(){
		ImageView leftBtn = (ImageView) findViewById(R.id.left_arrow);
		leftBtn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				MeInfoDetailActivity.this.finish();
			}
		});
	}
	
	@Override
	public boolean OnNewDataArrived(int aRequestType, int aErrCode, Object aData){
		Log.i("MeInfo", "---->"+aRequestType);
		if(!super.OnNewDataArrived(aRequestType, aErrCode, aData)){
			if (aRequestType == WsyProtocol.KRequestIdGetMeInfo){
				userInfo = (UserInfo) aData;
				bindItemClickListener();
			}
			if (aRequestType == WsyProtocol.KRequestIdUpdateAvatar){
				Toast.makeText(MeInfoDetailActivity.this, "保存成功", 
						Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(MeInfoDetailActivity.this, "网络错误", 
					Toast.LENGTH_SHORT).show();
		} 
		return true;
	}
}
