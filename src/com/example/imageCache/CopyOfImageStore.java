//package com.jpy.imageCache;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.lang.ref.SoftReference;
//import java.net.HttpURLConnection;
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map.Entry;
//
//import com.jpy.application.DeviceInfo;
//import com.jpy.application.MyApplication;
//import com.jpy.debug.Tracer;
//import com.jpy.http.MD5;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Bitmap.CompressFormat;
//import android.net.Uri;
//import android.os.Handler;
//import android.widget.ImageView;
//import com.jpy.R;
//
//public class CopyOfImageStore 
//{
//	private LoaderThread iThread;// 加载图片并发消息通知更新界面的线程
//	private HashMap<String, SoftReference<Bitmap>> iCachedBitmapArray;// 图片对象缓存，key:图片的url
//	private Handler iHandler;// 界面Activity的Handler对象
//	private HashMap<ImageView, Runnable> iAsyncUpdateImageviewMap;
//	private String	iLocalCachePath;
//	private Bitmap 	iDefaultBitmap;
//	
//	private static CopyOfImageStore iInstance;
//	
//	public static CopyOfImageStore Instance()
//	{
//		if(iInstance == null)
//		{
//			iInstance = new CopyOfImageStore();
//		}
//		return iInstance;
//	}
//	
//	private CopyOfImageStore()
//	{
//		iHandler = new Handler();
//		iAsyncUpdateImageviewMap = new HashMap<ImageView, Runnable>();
//		iCachedBitmapArray = new HashMap<String, SoftReference<Bitmap>>();
//		iLocalCachePath = DeviceInfo.Instance().iSdcardPath + "/jpy/imgcache/";
//		//确保保存目录存在
//		new File(iLocalCachePath).mkdirs();
//	}
//	
//	public void DestroyImageStore()
//	{
//		if(iThread != null)
//		{
//			iThread.ExitThread();
//		}
//		
//		if(iDefaultBitmap != null)
//		{
//			iDefaultBitmap.recycle();
//			iDefaultBitmap = null;
//		}
//		
//		if (iCachedBitmapArray != null) 
//		{
//			Bitmap bitmap;
//			for (Entry<String, SoftReference<Bitmap>> entry : iCachedBitmapArray.entrySet()) 
//			{
//				bitmap = entry.getValue().get();
//				if (bitmap != null) 
//				{
//					bitmap.recycle();// 释放bitmap对象
//				}
//			}
//			iCachedBitmapArray.clear();
//		}
//	}
//	
//	public void SetDefaultBitmap(Bitmap aDefaultBitmap)
//	{
//		iDefaultBitmap = aDefaultBitmap;
//	}
//	
//	public Bitmap GetBitmapFromCache(String aKey)
//	{
//		Bitmap vRetBitmap = null;
//		SoftReference<Bitmap> vRet = iCachedBitmapArray.get(aKey);
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
//		// 图片所对应的url,这个值在加载图片过程中很可能会被改变
//		String url = (String) aImageView.getTag(R.id.tag_first);
//		if(iCachedBitmapArray.containsKey(url)) // 判断缓存中是否有
//		{
//			SoftReference<Bitmap> softReference = iCachedBitmapArray.get(url);
//			Bitmap bitmap = softReference.get();
//			if(bitmap != null) // 如果图片对象不为空，则可挂接更新视图，并返回
//			{
////				Tracer.Log("Local图片缓存中，直接使用");
//				aImageView.setImageBitmap(bitmap);
//				return;
//			}
//			else // 如果为空，需要将其从缓存中删除（其bitmap对象已被回收释放，需要重新加载）
//			{
////				Tracer.Log("local cached bitmap is null, can remove it");
//				iCachedBitmapArray.remove(url);
//			}
//		}
//		
//		//检查本地缓存图片
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
//	/**
//	 * 图片缓存更新
//	 * @param aImageView
//	 * @param bitmap
//	 */
//	public void ChangeImageViewBitmap(ImageView aImageView, Bitmap bitmap){
//		//类似头像上传，相同的uri，但是对应的缓存需要更新修正
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
//	
//	public void SetImageViewBitmap(ImageView aImageView, boolean aShowDefault)
//	{
//		// 图片所对应的url,这个值在加载图片过程中很可能会被改变
//		String url = (String) aImageView.getTag(R.id.tag_first);
//		Object vExtParam = aImageView.getTag(R.id.tag_second); //是否是大
//		if( (vExtParam==null || !(Boolean)vExtParam) && url != null && url.length() > 0 && iCachedBitmapArray.containsKey(url)) // 判断缓存中是否有
//		{
//			SoftReference<Bitmap> softReference = iCachedBitmapArray.get(url);
//			Bitmap bitmap = softReference.get();
//			if (bitmap != null) // 如果图片对象不为空，则可挂接更新视图，并返回
//			{
////				Tracer.Log("图片在缓存中，直接使用");
//				aImageView.setImageBitmap(bitmap);
//				Runnable runnable = iAsyncUpdateImageviewMap.remove(aImageView);
//				if(runnable != null)
//				{
//					iHandler.removeCallbacks(runnable);
//				}
//				return;
//			}
//			else // 如果为空，需要将其从缓存中删除（其bitmap对象已被回收释放，需要重新加载）
//			{
////				Tracer.Log("cached bitmap is null, can remove it");
//				iCachedBitmapArray.remove(url);
//			}
//		}
//		
//		//若没有在缓存中，先给一个默认图
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
//		if(iThread == null) // 加载线程不存在，线程还未启动，需要新建线程并启动
//		{
//			iThread = new LoaderThread(aImageView, url);
//			iThread.start();
//		} 
//		else // 如果存在，就调用线程对象去加载
//		{
//			iThread.ContinueToLoad(aImageView, url);
//		}
//	}
//	
//	/**
//	 * 加载图片并显示的线程
//	 */
//	private class LoaderThread extends Thread 
//	{
//		LinkedHashMap<String, List<ImageView>> mTaskMap;// 需要加载图片并显示的图片视图对象任务链
//		private boolean mIsWait;// 标识是线程是否处于等待状态
//		private boolean iCanceled = false;
//
//		public LoaderThread(ImageView imageView, String url) 
//		{
//			mTaskMap = new LinkedHashMap<String, List<ImageView>>();
//			List<ImageView> vItem = new ArrayList<ImageView>();
//			vItem.add(imageView);
//			mTaskMap.put(url, vItem);
//		}
//		
//		private void Remove(ImageView imageView)
//		{
//			ImageView vImageView;
//			for(Entry<String, List<ImageView>> entry : mTaskMap.entrySet()) 
//			{
//				List<ImageView> vIter = entry.getValue();
//				int vCount = vIter.size();
//				for(int i = 0; i < vCount; i++)
//				{
//					vImageView = vIter.get(i);
//					if(vImageView == imageView)
//					{
//						vIter.remove(i);
//						vCount = vIter.size();
//						if(vCount <= 0)
//						{
//							Object vKey = entry.getKey();
//		//					Tracer.Log("删除复用ImageView： "+vKey);
//							mTaskMap.remove(vKey);
//						}
//						return;
//					}
//				}
//			}
//		}
//		
//		//---------------------------------------------------------------------------
//		class imgUri{
//			int type;
//			String uri;
//			public imgUri(int type , String uri){
//				this.type = type;
//				this.uri = uri;
//			}
//		}
//		
//		private imgUri LoadUri(String aUrl, boolean aNeedBig)
//		{
//			Bitmap vRetBitmap = null;
//			//检查本地缓存图片
//			if(aUrl!=null && aUrl.startsWith("/")) //only check local files
//			{
//				return new imgUri(-1, aUrl);
//			}
//			else if(aUrl!=null && aUrl.startsWith("res_")) //only check resource id
//			{
//				return new imgUri(-2, aUrl);
//			}
//			
//			String vImgPath = iLocalCachePath;
//			if(aNeedBig)
//			{
//				vImgPath += "big_";
//			}
//			vImgPath += MD5.encode(aUrl);
//			vImgPath += aUrl.substring(aUrl.lastIndexOf('.'));
//			if(new File(vImgPath).exists())
//			{
//				return new imgUri(0, vImgPath);
//			}
//			
//			try
//			{
//				HttpURLConnection vConn = (HttpURLConnection) new java.net.URL(aUrl).openConnection();
//				vConn.setRequestProperty("User-Agent", "Nokia N70");
//				//设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
//				vConn.setConnectTimeout(6000);
//				//连接设置获得数据流
//				vConn.setDoInput(true);
//				//不使用缓存
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
//				return new imgUri(0, vImgPath);
//			} 
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//			
//			return null;
//		}
//		//---------------------------------------------------------------------------
//		
//		private Bitmap LoadBitmap(String aUrl, boolean aNeedBig)
//		{
//			Bitmap vRetBitmap = null;
//			//检查本地缓存图片
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
//				//设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
//				vConn.setConnectTimeout(6000);
//				//连接设置获得数据流
//				vConn.setDoInput(true);
//				//不使用缓存
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
//
//		/**
//		 * 处理某个视图的更新显示
//		 * 
//		 * @param imageView
//		 */
//		public void ContinueToLoad(ImageView imageView, String url) 
//		{
//			Remove(imageView);// 任务链中可能有，得先删除
//			
//			List<ImageView> vItem = mTaskMap.get(url);
//			if(vItem != null)
//			{
//				vItem.add(imageView);
//			}
//			else
//			{
//				vItem = new ArrayList<ImageView>();
//				vItem.add(imageView);
//			}
//			mTaskMap.put(url, vItem);// 将其添加到任务中
//			if(mIsWait) // 如果线程此时处于等待得唤醒线程去处理任务队列中待处理的任务
//			{
//				synchronized (this) // 调用对象的notify()时必须同步
//				{
//					this.notify();
//				}
//			}
//		}
//		
//		public void ExitThread()
//		{
//			iCanceled = true;
//			if(mIsWait) // 如果线程此时处于等待得唤醒线程去处理任务队列中待处理的任务
//			{
//				synchronized (this) // 调用对象的notify()时必须同步
//				{
//					this.notify();
//				}
//			}
//		}
//
//		@Override
//		public void run() 
//		{
//			while (mTaskMap.size() > 0)  // 当队列中有数据时线程就要一直运行,一旦进入就要保证其不会跳出循环
//			{
//				mIsWait = false;
//				final String url  = mTaskMap.keySet().iterator().next();
//				final List<ImageView> imageViewSet = mTaskMap.remove(url);
//				final ImageView imageView = imageViewSet.get(0);
//				if (url.equalsIgnoreCase((String)imageView.getTag(R.id.tag_first)))  // 判断视图有没有复用（一旦ImageView被复用，其tag值就会修改变）
//				{
//					final Object vExtParam = imageView.getTag(R.id.tag_second);
//					final boolean vNeedBig = vExtParam!=null ? (Boolean)vExtParam : false;
//					//------------------------------------------------------------------------------------------
//					final imgUri imguri = LoadUri(url, vNeedBig);
//					if(imguri != null)
//					{
//						
//						int vCount = imageViewSet.size();
//						for(int i = 0; i < vCount; i++)
//						{
//							final ImageView vimageView = imageViewSet.get(i);
//							if (url.equalsIgnoreCase((String)vimageView.getTag(R.id.tag_first)))  // 再次判断视图有没有复用
//							{
//								Runnable runnable = new Runnable() 
//								{
//									// 通过消息机制在主线程中更新UI
//									@Override
//									public void run() 
//									{
//										Bitmap bitmap;
//										switch (imguri.type)
//										{
//											case -1:
//												bitmap = BitmapFactory.decodeFile(imguri.uri);
//												vimageView.setImageBitmap(bitmap);
//												break;
//											case -2:
//												int vResId = Integer.parseInt(imguri.uri.substring(4));
//												vimageView.setImageResource(vResId);
//												break;
//											case 0:
//												Uri uri = Uri.parse(imguri.uri);
//												vimageView.setImageURI(uri);
//											default:
//												break;
//										}
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
//					//===============================================================================================
////					final Bitmap bitmap = LoadBitmap(url, vNeedBig);// 此方法应该是从网络或sd卡中加载
////					if(bitmap != null)
////					{
////						// 将加载的图片放入缓存map中
////						if(!vNeedBig)
////						{
////							iCachedBitmapArray.put(url, new SoftReference<Bitmap>(bitmap));
////						}
////						
////						int vCount = imageViewSet.size();
////						for(int i = 0; i < vCount; i++)
////						{
////							final ImageView vimageView = imageViewSet.get(i);
////							if (url.equalsIgnoreCase((String)vimageView.getTag(R.id.tag_first)))  // 再次判断视图有没有复用
////							{
////								Runnable runnable = new Runnable() 
////								{
////									// 通过消息机制在主线程中更新UI
////									@Override
////									public void run() 
////									{
////										vimageView.setImageBitmap(bitmap);
////										iAsyncUpdateImageviewMap.remove(vimageView);
////									}
////								};
////								Runnable preRunnable = iAsyncUpdateImageviewMap.put(vimageView, runnable);
////								if(preRunnable != null)
////								{
////									iHandler.removeCallbacks(preRunnable);
////								}
////								iHandler.post(runnable);
////							}
////						}
////					}
//				}
//				else
//				{
//					Tracer.Log("2-imageView被复用，不做下载或者本地解码");
//				}
//				
//				if (mTaskMap.isEmpty()) // 当任务队列中没有待处理的任务时，线程进入等待状态
//				{
//					try 
//					{
//						mIsWait = true;// 标识线程的状态，必须在wait()方法之前
//						synchronized (this) 
//						{
//							this.wait();// 保用线程进入等待状态，直到有新的任务被加入时通知唤醒
//						}
//					} catch (InterruptedException e) 
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				if(iCanceled)
//				{
////					Tracer.Log("thread will exit - "+currentThread().getName());
//					break;
//				}
//			}
//			
//			mTaskMap.clear();
//		}
//	}
//	
//}
