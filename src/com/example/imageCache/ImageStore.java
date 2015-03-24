package com.example.imageCache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.example.wswy.R;
import com.example.http.MD5;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

public class ImageStore 
{
	private LoaderThread iThread;// ����ͼƬ������Ϣ֪ͨ���½�����߳�
//	private HashMap<String, SoftReference<Bitmap>> iCachedBitmapArray;// ͼƬ���󻺴棬key:ͼƬ��url
	private HashMap<String, ImgUri> iCachedImguriArray;
	private Handler iHandler;// ����Activity��Handler����
	private HashMap<ImageView, Runnable> iAsyncUpdateImageviewMap;
	private String	iLocalCachePath;
	private Bitmap 	iDefaultBitmap;
	
	private static ImageStore iInstance;
	
	public static ImageStore Instance()
	{
		if(iInstance == null)
		{
			iInstance = new ImageStore();
		}
		return iInstance;
	}
	
	private ImageStore()
	{
		iHandler = new Handler();
		iAsyncUpdateImageviewMap = new HashMap<ImageView, Runnable>();
//		iCachedBitmapArray = new HashMap<String, SoftReference<Bitmap>>();
		iCachedImguriArray = new HashMap<String, ImgUri>();
		iLocalCachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/jpy/imgcache/";
		//ȷ������Ŀ¼����
		new File(iLocalCachePath).mkdirs();
	}
	
	public void DestroyImageStore()
	{
		if(iThread != null)
		{
			iThread.ExitThread();
			iThread = null;
		}
		
//		if(iDefaultBitmap != null)
		if(!iDefaultBitmap.isRecycled())
		{

			iDefaultBitmap.recycle();
			iDefaultBitmap = null;
		}
		//--------------------------------------------------------------------------
//		if (iCachedBitmapArray != null) 
//		{
//			Bitmap bitmap;
//			for (Entry<String, SoftReference<Bitmap>> entry : iCachedBitmapArray.entrySet()) 
//			{
//				bitmap = entry.getValue().get();
//				if (bitmap != null) 
//				{
//					bitmap.recycle();// �ͷ�bitmap����
//				}
//			}
//			iCachedBitmapArray.clear();
//		}
		if (iCachedImguriArray != null) 
		{
			ImgUri imguri;
			for (Entry<String, ImgUri> entry : iCachedImguriArray.entrySet()) 
			{
				imguri = entry.getValue();
				switch (imguri.type)
				{
					case -1:
					case 0:
						File file = new File(imguri.uri);
						file.deleteOnExit();
						break;
					case -3:
						imguri.bmp.recycle();
						break;

					default:
						break;
				}
			}
			iCachedImguriArray.clear();
		}
		//=============================================================================================
	}
	
	public void SetDefaultBitmap(Bitmap aDefaultBitmap)
	{
		iDefaultBitmap = aDefaultBitmap;
	}
	//--------------------------------------------------------------------------------
	
	public void SetImageViewLocalBitmap(ImageView aImageView)
	{
		// ͼƬ���Ӧ��url,���ֵ�ڼ���ͼƬ����кܿ��ܻᱻ�ı�
		String url = (String) aImageView.getTag(R.id.tag_first);
		if(iCachedImguriArray.containsKey(url)) // �жϻ������Ƿ���
		{
			ImgUri imguri = iCachedImguriArray.get(url);
			
			if(imguri != null) // ���ͼƬ����Ϊ�գ���ɹҽӸ�����ͼ��������
			{
				switch (imguri.type)
				{
					case -1:
						Bitmap bitmap = BitmapFactory.decodeFile(imguri.uri);
						aImageView.setImageBitmap(bitmap);
						break;
					case -2:
						int vResId = Integer.parseInt(imguri.uri.substring(4));
						aImageView.setImageResource(vResId);
						break;
					case 0:
						Uri uri = Uri.parse(imguri.uri);
						aImageView.setImageURI(uri);
						break;
					case -3:
						aImageView.setImageBitmap(imguri.bmp);
						break;
					default:
						break;
				}
				return;
			}
			else // ���Ϊ�գ���Ҫ����ӻ�����ɾ����bitmap�����ѱ������ͷţ���Ҫ���¼��أ�
			{
				iCachedImguriArray.remove(url);
			}
		}
	}
//	public Bitmap GetBitmapFromCache(String aKey)
//	{
//		Bitmap vRetBitmap = null;
//		SoftReference<Bitmap> vRet = iCachedImguriArray.get(aKey);
//		if(vRet != null)
//		{
//			vRetBitmap = vRet.get();
//		}
//		
//		return vRetBitmap;
//	}
//	
//	public void SetImageViewLocalBitmap(ImageView aImageView)
//	{
//		// ͼƬ���Ӧ��url,���ֵ�ڼ���ͼƬ����кܿ��ܻᱻ�ı�
//		String url = (String) aImageView.getTag(R.id.tag_first);
//		if(iCachedBitmapArray.containsKey(url)) // �жϻ������Ƿ���
//		{
//			SoftReference<Bitmap> softReference = iCachedBitmapArray.get(url);
//			Bitmap bitmap = softReference.get();
//			if(bitmap != null) // ���ͼƬ����Ϊ�գ���ɹҽӸ�����ͼ��������
//			{
////				Tracer.Log("LocalͼƬ�����У�ֱ��ʹ��");
//				aImageView.setImageBitmap(bitmap);
//				return;
//			}
//			else // ���Ϊ�գ���Ҫ����ӻ�����ɾ����bitmap�����ѱ������ͷţ���Ҫ���¼��أ�
//			{
////				Tracer.Log("local cached bitmap is null, can remove it");
//				iCachedBitmapArray.remove(url);
//			}
//		}
//		
//		//��鱾�ػ���ͼƬ
//		Bitmap vRetBitmap = null;
//		if(url.startsWith("/")) //only check local files
//		{
//			vRetBitmap = BitmapFactory.decodeFile(url);
//		}
//		else if(url.startsWith("res_")) //only check resource id
//		{
//			int vResId = Integer.parseInt(url.substring(4));
//			vRetBitmap = BitmapFactory.decodeResource(MyApplication.Instance().getResources(), vResId);
//		}
//		
//		if(vRetBitmap != null)
//		{
//			iCachedBitmapArray.put(url, new SoftReference<Bitmap>(vRetBitmap));
//			aImageView.setImageBitmap(vRetBitmap);
//		}
//	}
	//=============================================================================================================
	
	//------------------------------------------------------------------------------------------
	/**
	 * ͼƬ�������
	 * @param aImageView
	 * @param bitmap
	 */
	public void ChangeImageViewBitmap(ImageView aImageView, Bitmap bitmap){
		//����ͷ���ϴ�����ͬ��uri�����Ƕ�Ӧ��ͼƬ��Ҫ��������
		String url = (String) aImageView.getTag(R.id.tag_first);
		if(url != null && url.length() > 0 && iCachedImguriArray.containsKey(url))
		{
			
			String vImgPath = iCachedImguriArray.get(url).uri;
			File file = new File(vImgPath);
			file.deleteOnExit();
			FileOutputStream vOutOs;
			try
			{
				vOutOs = new FileOutputStream(vImgPath);
				bitmap.compress(CompressFormat.JPEG, 100, vOutOs);
				vOutOs.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
	}
//	public void ChangeImageViewBitmap(ImageView aImageView, Bitmap bitmap){
//		//����ͷ���ϴ�����ͬ��uri�����Ƕ�Ӧ�Ļ�����Ҫ��������
//		String url = (String) aImageView.getTag(R.id.tag_first);
//		if(url != null && url.length() > 0 && iCachedBitmapArray.containsKey(url))
//		{
//			
//			SoftReference<Bitmap> softReference = iCachedBitmapArray.remove(url);
//			Bitmap bitmap_old = softReference.get();
//			bitmap_old.recycle();
//			iCachedBitmapArray.put(url, new SoftReference<Bitmap>(bitmap));
//			
//			String vImgPath = iLocalCachePath;
//			Object vExtParam = aImageView.getTag(R.id.tag_second);
//			boolean vNeedBig = vExtParam!=null ? (Boolean)vExtParam : false;
//			if(vNeedBig)
//			{
//				vImgPath += "big_";
//			}
//			vImgPath += MD5.encode(url);
//			vImgPath += url.substring(url.lastIndexOf('.'));
//			
//			File file = new File(vImgPath);
//			file.deleteOnExit();
//			FileOutputStream vOutOs;
//			try
//			{
//				vOutOs = new FileOutputStream(vImgPath);
//				bitmap.compress(CompressFormat.JPEG, 100, vOutOs);
//				vOutOs.close();
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//			
//		}
//	}
	//===============================================================================================
	//------------------------------------------------------------------------------------------------
	public void SetImageViewBitmap(ImageView aImageView, boolean aShowDefault)
	{
		//�ȸ�һ��Ĭ��ͼ
		if(aShowDefault && iDefaultBitmap != null)
		{
			aImageView.setImageBitmap(iDefaultBitmap);
		}
		// ͼƬ���Ӧ��url,���ֵ�ڼ���ͼƬ����кܿ��ܻᱻ�ı�
		String url = (String) aImageView.getTag(R.id.tag_first);

		if(url == null || url.length() < 8)
		{
			return;
		}
		
		if(iCachedImguriArray.containsKey(url)){
			ImgUri imguri = iCachedImguriArray.get(url);
			switch (imguri.type)
			{
				case -2:

					int vResId = Integer.parseInt(imguri.uri.substring(4));
					aImageView.setImageResource(vResId);
					break;
				case -1:
				case 0:
					Uri uri = Uri.parse(imguri.uri);
					aImageView.setImageURI(uri);
					break;
				case -3:
					aImageView.setImageBitmap(imguri.bmp);
					break;
				default:
					break;
			}
			return;
		}
		
		if(iThread == null) // �����̲߳����ڣ��̻߳�δ��������Ҫ�½��̲߳�����
		{
			iThread = new LoaderThread(aImageView, url);
			iThread.start();
		} 
		else // �����ڣ��͵����̶߳���ȥ����
		{
			iThread.ContinueToLoad(aImageView, url);
		}
	}
//	public void SetImageViewBitmap(ImageView aImageView, boolean aShowDefault)
//	{
//		// ͼƬ���Ӧ��url,���ֵ�ڼ���ͼƬ����кܿ��ܻᱻ�ı�
//		String url = (String) aImageView.getTag(R.id.tag_first);
//		Object vExtParam = aImageView.getTag(R.id.tag_second); //�Ƿ��Ǵ�
//		if( (vExtParam==null || !(Boolean)vExtParam) && url != null && url.length() > 0 && iCachedBitmapArray.containsKey(url)) // �жϻ������Ƿ���
//		{
//			SoftReference<Bitmap> softReference = iCachedBitmapArray.get(url);
//			Bitmap bitmap = softReference.get();
//			if (bitmap != null) // ���ͼƬ����Ϊ�գ���ɹҽӸ�����ͼ��������
//			{
////				Tracer.Log("ͼƬ�ڻ����У�ֱ��ʹ��");
//				aImageView.setImageBitmap(bitmap);
//				Runnable runnable = iAsyncUpdateImageviewMap.remove(aImageView);
//				if(runnable != null)
//				{
//					iHandler.removeCallbacks(runnable);
//				}
//				return;
//			}
//			else // ���Ϊ�գ���Ҫ����ӻ�����ɾ����bitmap�����ѱ������ͷţ���Ҫ���¼��أ�
//			{
////				Tracer.Log("cached bitmap is null, can remove it");
//				iCachedBitmapArray.remove(url);
//			}
//		}
//		
//		//��û���ڻ����У��ȸ�һ��Ĭ��ͼ
//		if(aShowDefault && iDefaultBitmap != null)
//		{
//			aImageView.setImageBitmap(iDefaultBitmap);
//		}
//		
//		if(url == null || url.length() < 8)
//		{
//			return;
//		}
//		
//		if(iThread == null) // �����̲߳����ڣ��̻߳�δ��������Ҫ�½��̲߳�����
//		{
//			iThread = new LoaderThread(aImageView, url);
//			iThread.start();
//		} 
//		else // �����ڣ��͵����̶߳���ȥ����
//		{
//			iThread.ContinueToLoad(aImageView, url);
//		}
//	}
	//==================================================================================
	
	class ImgUri{
		int type = 0;
		//-1, -2, 0���sd���ã��������ļ��洢
		//-3 sd�����ñ�����ͼƬ�洢
		String uri = null;
		Bitmap bmp = null;
		//����gallery��ʾ������bitmap�ķ�ʽ��������ImageView������������ɲ����������⣬�������loadbybmp�ж�
		boolean loadbybmp = false;
		public ImgUri(int type , String uri, boolean loadbybmp){
			this.type = type;
			this.uri = uri;
			this.loadbybmp = loadbybmp;
		}
		
		public ImgUri(int type , Bitmap bmp){
			this.type = type;
			this.bmp = bmp;
		}
	}
	
	/**
	 * ����ͼƬ����ʾ���߳�
	 */
	private class LoaderThread extends Thread 
	{
		LinkedHashMap<String, List<ImageView>> mTaskMap;// ��Ҫ����ͼƬ����ʾ��ͼƬ��ͼ����������
		private boolean mIsWait;// ��ʶ���߳��Ƿ��ڵȴ�״̬
		private boolean iCanceled = false;

		public LoaderThread(ImageView imageView, String url) 
		{
			mTaskMap = new LinkedHashMap<String, List<ImageView>>();
			List<ImageView> vItem = new ArrayList<ImageView>();
			vItem.add(imageView);
			mTaskMap.put(url, vItem);
		}
		
		private void Remove(ImageView imageView)
		{
			ImageView vImageView;
			for(Entry<String, List<ImageView>> entry : mTaskMap.entrySet()) 
			{
				List<ImageView> vIter = entry.getValue();
				int vCount = vIter.size();
				for(int i = 0; i < vCount; i++)
				{
					vImageView = vIter.get(i);
					if(vImageView == imageView)
					{
						vIter.remove(i);
						vCount = vIter.size();
						if(vCount <= 0)
						{
							Object vKey = entry.getKey();
							mTaskMap.remove(vKey);
						}
						return;
					}
				}
			}
		}
		
		//---------------------------------------------------------------------------
		
		
		private ImgUri LoadUri(String aUrl, boolean aNeedBig)
		{
			Bitmap vRetBitmap = null;
			ImgUri uri;
			if(iCachedImguriArray.containsKey(aUrl)){
				uri = iCachedImguriArray.get(aUrl);
				return uri;
			}
			//��鱾�ػ���ͼƬ
			if(aUrl!=null && aUrl.startsWith("/")) //only check local files
			{
				uri = new ImgUri(-1, aUrl, aNeedBig); 
				iCachedImguriArray.put(aUrl, uri);
				return uri;
			}
			else if(aUrl!=null && aUrl.startsWith("res_")) //only check resource id
			{
				uri = new ImgUri(-2, aUrl, aNeedBig); 
				iCachedImguriArray.put(aUrl, uri);
				return uri;
			}
			
			String vImgPath = iLocalCachePath;
			vImgPath += MD5.encode(aUrl);
			vImgPath += aUrl.substring(aUrl.lastIndexOf('.'));
			if(new File(vImgPath).exists())
			{
				uri = new ImgUri(0, vImgPath, aNeedBig); 
				iCachedImguriArray.put(aUrl, uri);
				return uri;
			}
			
			try
			{
				HttpURLConnection vConn = (HttpURLConnection) new java.net.URL(aUrl).openConnection();
				vConn.setRequestProperty("User-Agent", "Nokia N70");
				//���ó�ʱʱ��Ϊ6000���룬conn.setConnectionTiem(0);��ʾû��ʱ������
				vConn.setConnectTimeout(6000);
				//�������û�������
				vConn.setDoInput(true);
				//��ʹ�û���
				vConn.setUseCaches(false);
				InputStream vIs = vConn.getInputStream();
				vRetBitmap = BitmapFactory.decodeStream(vIs);
				vIs.close();
	
				//save to lacal
				if(vRetBitmap != null)
				{
					try
					{
						FileOutputStream vOutOs = new FileOutputStream(vImgPath);
						vRetBitmap.compress(CompressFormat.JPEG, 100, vOutOs);
						vOutOs.close();
						uri = new ImgUri(0, vImgPath, aNeedBig); 
						iCachedImguriArray.put(aUrl, uri);
						return uri;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					uri = new ImgUri(-3, vRetBitmap); 
					iCachedImguriArray.put(aUrl, uri);
					return uri;
				}
				    
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			return null;
		}
		
//		private Bitmap LoadBitmap(String aUrl, boolean aNeedBig)
//		{
//			Bitmap vRetBitmap = null;
//			//��鱾�ػ���ͼƬ
//			if(aUrl!=null && aUrl.startsWith("/")) //only check local files
//			{
//				vRetBitmap = BitmapFactory.decodeFile(aUrl);
//				return vRetBitmap;
//			}
//			else if(aUrl!=null && aUrl.startsWith("res_")) //only check resource id
//			{
//				int vResId = Integer.parseInt(aUrl.substring(4));
//				vRetBitmap = BitmapFactory.decodeResource(MyApplication.Instance().getResources(), vResId);
//				return vRetBitmap;
//			}
//			
//			String vImgPath = iLocalCachePath;
//			if(aNeedBig)
//			{
//				vImgPath += "big_";
//			}
//			vImgPath += MD5.encode(aUrl);
//			vImgPath += aUrl.substring(aUrl.lastIndexOf('.'));
//			vRetBitmap = BitmapFactory.decodeFile(vImgPath);
//			if(vRetBitmap != null)
//			{
////				Tracer.Log("bitmap is in local");
//				return vRetBitmap;
//			}
//			
//			try
//			{
//				HttpURLConnection vConn = (HttpURLConnection) new java.net.URL(aUrl).openConnection();
//				vConn.setRequestProperty("User-Agent", "Nokia N70");
//				//���ó�ʱʱ��Ϊ6000���룬conn.setConnectionTiem(0);��ʾû��ʱ������
//				vConn.setConnectTimeout(6000);
//				//�������û�������
//				vConn.setDoInput(true);
//				//��ʹ�û���
//				vConn.setUseCaches(false);
//				InputStream vIs = vConn.getInputStream();
//				vRetBitmap = BitmapFactory.decodeStream(vIs);
//				vIs.close();
//	
//				//save to lacal
//				if(vRetBitmap != null)
//				{
//					FileOutputStream vOutOs = new FileOutputStream(vImgPath);
//					vRetBitmap.compress(CompressFormat.JPEG, 100, vOutOs);
//					vOutOs.close();
//				}
//				    
//				return vRetBitmap;
//			} 
//			catch (Exception e)
//			{
////				Tracer.Log("Down Error: "+e.getMessage());
//				e.printStackTrace();
//			}
//			
//			return null;
//		}
		//=================================================================================

		/**
		 * ����ĳ����ͼ�ĸ�����ʾ
		 * 
		 * @param imageView
		 */
		public void ContinueToLoad(ImageView imageView, String url) 
		{
			Remove(imageView);// �������п����У�����ɾ��
			
			List<ImageView> vItem = mTaskMap.get(url);
			if(vItem != null)
			{
				vItem.add(imageView);
			}
			else
			{
				vItem = new ArrayList<ImageView>();
				vItem.add(imageView);
			}
			mTaskMap.put(url, vItem);// ������ӵ�������
			if(mIsWait) // ����̴߳�ʱ���ڵȴ�û����߳�ȥ������������д��������
			{
				synchronized (this) // ���ö����notify()ʱ����ͬ��
				{
					this.notify();
				}
			}
		}
		
		public void ExitThread()
		{
			iCanceled = true;
			if(mIsWait) // ����̴߳�ʱ���ڵȴ�û����߳�ȥ������������д��������
			{
				synchronized (this) // ���ö����notify()ʱ����ͬ��
				{
					this.notify();
				}
			}
		}

		@Override
		public void run() 
		{
			while (mTaskMap.size() > 0)  // �������������ʱ�߳̾�Ҫһֱ����,һ�������Ҫ��֤�䲻�����ѭ��
			{
				mIsWait = false;
				final String url  = mTaskMap.keySet().iterator().next();
				final List<ImageView> imageViewSet = mTaskMap.remove(url);
				final ImageView imageView = imageViewSet.get(0);
				if (url.equalsIgnoreCase((String)imageView.getTag(R.id.tag_first)))  // �ж���ͼ��û�и��ã�һ��ImageView�����ã���tagֵ�ͻ��޸ı䣩
				{
					final Object vExtParam = imageView.getTag(R.id.tag_second);
					final boolean vNeedBig = vExtParam!=null ? (Boolean)vExtParam : false;
					//------------------------------------------------------------------------------------------
					final ImgUri imguri = LoadUri(url, vNeedBig);
					if(imguri != null)
					{
						int vCount = imageViewSet.size();
						for(int i = 0; i < vCount; i++)
						{
							final ImageView vimageView = imageViewSet.get(i);
							if (url.equalsIgnoreCase((String)vimageView.getTag(R.id.tag_first)))  // �ٴ��ж���ͼ��û�и���
							{
								Runnable runnable = new Runnable() 
								{
									// ͨ����Ϣ���������߳��и���UI
									@Override
									public void run() 
									{
//										if(imguri.loadbybmp)
//										{
//											Bitmap bmp;
//											switch (imguri.type)
//											{
//												case -1:
//												case 0:
//													bmp = BitmapFactory.decodeFile(imguri.uri);		
//													vimageView.setImageBitmap(bmp);
//													break;
//												case -2:
//													int vResId = Integer.parseInt(imguri.uri.substring(4));
//													bmp = BitmapFactory.decodeResource(MyApplication.Instance().getResources(), vResId);
//													vimageView.setImageBitmap(bmp);
//													break;
//												default:
//													break;
//											}
//										}else {
											switch (imguri.type)
											{
												case -2:
													int vResId = Integer.parseInt(imguri.uri.substring(4));
													vimageView.setImageResource(vResId);
													break;
												case -1:
												case 0:
													Uri uri = Uri.parse(imguri.uri);
													vimageView.setImageURI(uri);
													break;
												case -3:
													vimageView.setImageBitmap(imguri.bmp);
													break;
												default:
													break;
											}
//										}
										iAsyncUpdateImageviewMap.remove(vimageView);
									}
								};
								Runnable preRunnable = iAsyncUpdateImageviewMap.put(vimageView, runnable);
								if(preRunnable != null)
								{
									iHandler.removeCallbacks(preRunnable);
								}
								iHandler.post(runnable);
							}
						}
					}
					//===============================================================================================
//					final Bitmap bitmap = LoadBitmap(url, vNeedBig);// �˷���Ӧ���Ǵ������sd���м���
//					if(bitmap != null)
//					{
//						// �����ص�ͼƬ���뻺��map��
//						if(!vNeedBig)
//						{
//							iCachedBitmapArray.put(url, new SoftReference<Bitmap>(bitmap));
//						}
//						
//						int vCount = imageViewSet.size();
//						for(int i = 0; i < vCount; i++)
//						{
//							final ImageView vimageView = imageViewSet.get(i);
//							if (url.equalsIgnoreCase((String)vimageView.getTag(R.id.tag_first)))  // �ٴ��ж���ͼ��û�и���
//							{
//								Runnable runnable = new Runnable() 
//								{
//									// ͨ����Ϣ���������߳��и���UI
//									@Override
//									public void run() 
//									{
//										vimageView.setImageBitmap(bitmap);
//										iAsyncUpdateImageviewMap.remove(vimageView);
//									}
//								};
//								Runnable preRunnable = iAsyncUpdateImageviewMap.put(vimageView, runnable);
//								if(preRunnable != null)
//								{
//									iHandler.removeCallbacks(preRunnable);
//								}
//								iHandler.post(runnable);
//							}
//						}
//					}
				}
				else
				{
				}
				
				if (mTaskMap.isEmpty()) // �����������û�д��������ʱ���߳̽���ȴ�״̬
				{
					try 
					{
						mIsWait = true;// ��ʶ�̵߳�״̬��������wait()����֮ǰ
						synchronized (this) 
						{
							this.wait();// �����߳̽���ȴ�״̬��ֱ�����µ����񱻼���ʱ֪ͨ����
						}
					} catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				
				if(iCanceled)
				{
					break;
				}
			}
			
			mTaskMap.clear();
		}
	}
	
}
