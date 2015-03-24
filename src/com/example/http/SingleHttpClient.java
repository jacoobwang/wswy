package com.example.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;

import org.apache.http.conn.params.ConnManagerParams;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;

import org.apache.http.conn.ssl.SSLSocketFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/** 
 * SingleHttpClient.java
 * @modified v_wyongwang
 * @version 1.0
 * @date 2014-04-20
 */
public class SingleHttpClient
{
	private EventHandler iHandler = null;
	private static final int REQUEST_ERROR = -1;
	private static final int REQUEST_COMPLETE = 0;
	private static final int REQUEST_UPDATE = 1;
	
	private static final int ERROR_EXCEPTION = -1000;
	private static final int ERROR_FILE_NOT_MATCH = -1001;
	
	private static final int MAX_BUFFER_SIZE = 16*1024;
	private static final int THREAD_POOL_SIZE = 5;
	private static final String CHARSET = HTTP.UTF_8;
	private static SingleHttpClient iInstance = null;
	/** 
	 * @brief	core to request
	 */
	private HttpClient iHttpClient = null;
	/** 
	 * @brief	Thread pool service
	 */
	private ExecutorService iExecutorService = null;

	private RequestThread iRequestThread = null;

	public static SingleHttpClient Instance()
	{
		if(iInstance == null)
		{
			iInstance = new SingleHttpClient();
		}
		return iInstance;
	}
	/**
	 * Constructs a new instance.
	 */
	private SingleHttpClient()
	{
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, CHARSET);
        HttpProtocolParams.setHttpElementCharset(params, "GBK");
        HttpProtocolParams.setUseExpectContinue(params, true);
        /* �����ӳ���ȡ���ӵĳ�ʱʱ�� */
        ConnManagerParams.setTimeout(params, 10000);
        /* ���ӳ�ʱ */
        HttpConnectionParams.setConnectionTimeout(params, 10000);
        /* ����ʱ */
        HttpConnectionParams.setSoTimeout(params, 20000);
        // �������ǵ�HttpClient֧��HTTP��HTTPS����ģʽ
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory
                .getSocketFactory(), 443));

        // ʹ���̰߳�ȫ�����ӹ���������HttpClient
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
        iHttpClient = new DefaultHttpClient(conMgr, params);

		iHandler = new EventHandler();
	}

	/**
	 * ͬ����Http����
	 * 
	 * @param request �������?
	 * @return ��Ӧ��String����
	 */
	public Object doSyncRequest(CustomHttpRequest request)
	{
		HttpResponse response;
		try {
			response = iHttpClient.execute(request.getRequestMethod());
			int code = response.getStatusLine().getStatusCode(); 
			if(HttpStatus.SC_OK == code)
			{
				return EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * �첽��Http����
	 * 
	 * @param request	�������?
	 * @param aCancelBefore true Ϊȡ��֮ǰ�Ķ��У�false Ϊ��ȡ��֮ǰ�Ķ��к�����
	 */
	public void doAsyncRequest(CustomHttpRequest request, boolean aCancelBefore)
	{
		if(iRequestThread == null)
		{
			Log.i("pr","new RequestThread()");
			iRequestThread = new RequestThread();
			iRequestThread.start();
		}
		iRequestThread.submit(request, aCancelBefore);
	}
	
	public void StopRequestThread()
	{
		if(iRequestThread != null)
		{
			CustomHttpRequest vStopRequest = new CustomHttpRequest(CustomHttpRequest.KStopThreadRequestId);
			iRequestThread.submit(vStopRequest, true);
			iRequestThread = null;
		}
	}
	
	/**
	 * ���߳������ļ�����
	 * 
	 * @param request �������?
	 */
	public void doMutiAsyncRequest(final CustomHttpRequest request)
	{
		if(iExecutorService == null)
		{
			iExecutorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		}
		iExecutorService.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				sendAsyncRequest(request);
			}
		});

	}
	
	private void sendAsyncRequest(CustomHttpRequest request)
	{
		HttpResponse response;  
		try {
			response = iHttpClient.execute(request.getRequestMethod());
			int code = response.getStatusLine().getStatusCode();

			request.setHttpStatusCode(code);
			HttpEntity entity = response.getEntity();
			if(HttpStatus.SC_OK == code || HttpStatus.SC_PARTIAL_CONTENT == code)
			{
				String savePath = request.getSaveFilename();
				if(savePath != null && savePath.length() > 0)
				{
					InputStream inputStream = entity.getContent();
					long fileSize = entity.getContentLength();
					int completeSize = request.getCompeleteSize();
					request.setTotalSize((int) fileSize + completeSize);
					
					File savefile = new File(savePath);
					boolean isFileExists = savefile.exists(); 
					if (isFileExists && savefile.length() != completeSize) {
						savefile.delete();
						request.setHttpStatusCode(ERROR_FILE_NOT_MATCH);
						request.setResponseBody("file lenth dosen't match!");
						sendMessage(REQUEST_ERROR,request);
						return;
					}
					else if(!isFileExists)
					{
						savefile.createNewFile();
					}

					FileOutputStream outputStream = new FileOutputStream(savePath, true);

					byte[] buffer = (fileSize > 0 && fileSize < MAX_BUFFER_SIZE) ? new byte[(int) fileSize] : new byte[MAX_BUFFER_SIZE];

					int readNum = 0;
					while((readNum = inputStream.read(buffer)) != -1)
					{
						outputStream.write(buffer,0,readNum);
						completeSize = completeSize + readNum;
						request.setCompeleteSize(completeSize,false);
						sendMessage(REQUEST_UPDATE, request);
						if(request.isCancel())
						{
							return;
						}
					}
				}
				else
				{
					String vStr =  EntityUtils.toString(response.getEntity());
					int idx = vStr.lastIndexOf(65279);
					if(idx != -1)
					{
						vStr = vStr.substring(idx+1);
					}
					request.setResponseBody(vStr);
				}

				sendMessage(REQUEST_COMPLETE, request);
			}
			else
			{
				String vStr = EntityUtils.toString(entity);
				request.setResponseBody(vStr);
				sendMessage(REQUEST_ERROR, request);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			request.setHttpStatusCode(ERROR_EXCEPTION);
			request.setResponseBody(e);
			sendMessage(REQUEST_ERROR,request);
			e.printStackTrace();
		}
	}

	class RequestThread extends Thread
	{
		private CustomHttpRequest iRequest = null;
		private LinkedBlockingQueue<CustomHttpRequest> iRequestQueue = null;
		RequestThread()
		{
			iRequestQueue = new LinkedBlockingQueue<CustomHttpRequest>();
		}
		
		public void shutDown()
		{
			iRequestQueue.clear();
			if(iRequest != null)
				iRequest.doCancel();
			interrupt();
		}
		
		public void submit(CustomHttpRequest aRequest, boolean aCancelBefore) 
		{
			if(aCancelBefore)
			{
				iRequestQueue.clear();
				if(iRequest != null)
					iRequest.doCancel();
			}
			try {
				iRequestQueue.put(aRequest);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void run()
		{
			while(true)
			{
				try {
					iRequest = iRequestQueue.take();
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				if(iRequest != null)
				{
					if(iRequest.getRequestId() == CustomHttpRequest.KStopThreadRequestId)
					{
						//Tracer.Log("Requst thread will exit");
						Log.i("Log info", "Reuest thread will exit");
						break;
					}
					sendAsyncRequest(iRequest);
				}
				iRequest = null;
			}
		}
	}
	
	private void sendMessage(int what, Object obj)
	{
		Message msg = iHandler.obtainMessage(what, obj);
		iHandler.sendMessage(msg);
	}
	private class EventHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case REQUEST_UPDATE:
				{
					CustomHttpRequest vRequest = (CustomHttpRequest) msg.obj;
					IHttpCallback vCallback = vRequest.getHttpCallback();
					if(!vRequest.isCancel() && vCallback != null)
					{
						int vRequestId = vRequest.getRequestId();
						int vCompleteSize = vRequest.getCompeleteSize();
						int vTotalSize = vRequest.getTotalSize();
						vCallback.onUpdate(vRequestId,vTotalSize,vCompleteSize);
					}
				}
				break;
				
				case REQUEST_COMPLETE:
				{
					CustomHttpRequest vRequest = (CustomHttpRequest) msg.obj;
					IHttpCallback vCallback = vRequest.getHttpCallback();
					if(!vRequest.isCancel() && vCallback != null)
					{
						String vFilename = vRequest.getSaveFilename();
						Object vObj = (vFilename!=null)?vFilename:vRequest.getResponseBody();
						int vRequestId = vRequest.getRequestId();
						vCallback.onComplete(vRequestId,vRequest.getHttpStatusCode(),vObj);
					}
				}
				break;
				
				case REQUEST_ERROR:
				{
					CustomHttpRequest vRequest = (CustomHttpRequest) msg.obj;
					IHttpCallback vCallback = vRequest.getHttpCallback();
					if(!vRequest.isCancel() && vCallback != null)
					{
						int vHttpStatusCode = vRequest.getHttpStatusCode();
						Object vObj = vRequest.getResponseBody();
						int vRequestId = vRequest.getRequestId();
						if(ERROR_EXCEPTION == vHttpStatusCode && vObj.toString().contains("Request already aborted"))
						{
							Log.d("pr","ERROR_EXCEPTION,cancel");
							return;
						}
						vCallback.onComplete(vRequestId,vHttpStatusCode,vRequest.getResponseBody());
					}
				}
				break;
			}
		}
	}
}
