package com.example.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/** 
 * CustomHttpRequest.java
 * http�����������ͷ��postͷ���������Ϣ���ɴ�����й���
 * @author pan rui
 * @version 1.0
 * @date 2011-10-29
 */
public class CustomHttpRequest
{
	public enum METHOD
	{
		METHOD_GET(0),
		METHOD_POST(1);
		
		private final int value;
		METHOD(int value)
		{
			this.value = value;
		}
		public int getValue()
		{ 
			return value; 
		} 
	}
	
	private boolean iFixSaveFileType;
	
	private String iRequestUrl;
	private	METHOD iMethod;
	private	IHttpCallback iCallback;
	/** 
	 * ��Ҫ����HttpGet,HttpPost����ͨ��������ͷ�Ͳ���
	 */
	protected	HttpRequestBase iRequestMethod;
	/** 
	 * Ҫ������ļ���?
	 */
	private	String iSaveFilename;
	/** 
	 * ����ID������������Ӧͬ���ص��������ͨ�����ô˲���������?
	 */
	private int iRequestId = -1;
	private boolean iIsCancel = false;
	/** 
	 * ����postData��MutiPartData���ܹ��棬���Լ������?
	 */
	private boolean iIsSetPostData = false;

	private Hashtable<String, Object> iResponseMap;
	
	private int iCompeleteSize = 0;
	private int iTotalSize = -1;
	
	public static final int KStopThreadRequestId = Integer.MAX_VALUE;
	
	/**
	 * Constructs a new instance.
	 * @param url		�����url
	 * @param callback	�ص�
	 */
	public CustomHttpRequest(String url,IHttpCallback callback)
	{
		this(-1, url, METHOD.METHOD_GET, callback);
	}
	
	/**
	 * Constructs a new instance.
	 * @param url		�����url
	 * @param callback	�ص�
	 */
	public CustomHttpRequest(int aRequestId, String url,IHttpCallback callback)
	{
		this(aRequestId, url, METHOD.METHOD_GET, callback);
	}
	
	/**
	 * Constructs a new instance.
	 * @param url		�����url
	 * @param method	����ķ�������ΪMETHOD��ö��ֵ
	 * @param callback	�ص�
	 */
	public CustomHttpRequest(int aRequestId, String url, METHOD method, IHttpCallback callback)
	{
		if (!url.startsWith("http://"))
		{
			url = "http://" + url;
		}
		iFixSaveFileType = false;
		iRequestId = aRequestId;
		iRequestUrl = url;
		iMethod = method;
		iCallback = callback;
		url = url.replaceAll(" ", "%20");
		url = url.replace("\n", "");
		if(iMethod == METHOD.METHOD_GET)
		{
			iRequestMethod = new HttpGet(url);
		}
		else if(iMethod == METHOD.METHOD_POST)
		{
			iRequestMethod = new HttpPost(url);
		}
		iResponseMap = new Hashtable<String, Object>();
		setDefaultHeader();
	}
	
	/**
	 * only for exit thread
	 */
	public CustomHttpRequest(int aRequestId)
	{
		iRequestId = aRequestId;
	}
	
	/** 
	 * ��������?
	 * 
	 * @param key		����ͷ��Key��e.g "Range"��
	 * @param value		����ͷ��Value��e.g "bytes=1000-"��
	 */
	public void addHeaderField(String key, String value)
	{
		if(iRequestMethod != null)
		{
			iRequestMethod.setHeader(key,value);
		}
	}

	/**
	 * ����������
	 *
	 * @param	name
	 * @param	value
	 */
	public void addHttpParams(String name, Object value)
	{
		if(iRequestMethod != null)
		{
			iRequestMethod.getParams().setParameter(name, value);
		}
	}

	/** 
	 * ���ûص�
	 * 
	 * @param	callback 
	 */
	public void setHttpCallback(IHttpCallback callback)
	{
		iCallback = callback;
	}

	/** 
	 * ȡ�ûص�
	 * 
	 * @return  ��Ӧ��httpCallback	
	 */
	public IHttpCallback getHttpCallback()
	{
		return iCallback;
	}

	/** 
	 * �õ�����ʽ
	 * 
	 * @return	METHOD_GET=httpget,METHOD_POST=httppost
	 */
	public METHOD getMethod()
	{
		return iMethod;
	}

	/** 
	 * ����RequestUrl
	 */
	public void setUrl(String url)
	{
		iRequestUrl = url;
		iRequestMethod.setURI(URI.create(url));
	}

	/** 
	 * ��ȡ����url
	 * 
	 * @return	String object for url
	 */
	public String getUrl()
	{
		return iRequestUrl;
	}

	/** 
	 * ��Ҫ�����ļ����洢ʱ������ָ�������ļ���
	 * 
	 * @param	filename  ���ر�����ļ����������·��
	 */
	public void setSaveFilename(String filename)
	{
		iSaveFilename = filename;
	}

	/** 
	 * ȡ�ñ����ļ���
	 * 
	 * @return �ļ������?
	 */
	public String getSaveFilename()
	{
		return iSaveFilename;
	}
	
	public void setFixSaveFileType(boolean isFix)
	{
		iFixSaveFileType = isFix;
	}
	
	public boolean isFixSaveFileType()
	{
		return iFixSaveFileType;
	}

	/** 
	 * ��ȡRequest����
	 * 
	 * @return	HttpGet if iMethod = METHOD_GET,else HttpPost
	 */
	public HttpRequestBase getRequestMethod()
	{
		return iRequestMethod;
	}

	/** 
	 * ��������id
	 * 
	 * @param	id ����id
	 */
	public void setRequestId(int id)
	{
		iRequestId = id;
	}

	/** 
	 * ��ȡ����id
	 * 
	 * @return	����id ��Ĭ��Ϊ��1
	 */
	public int getRequestId()
	{
		return iRequestId;
	}
	
	/**
	 * ����post��ݣ������Ϊ�ַ�������Ҫ����body��ʱ�����ô˷���
	 * 
	 * @param postData Ҫpost�����?
	 * 
	 * @throws Exception ���iMethod��ΪMETHOD_POST������֮ǰ�Ѿ����ù����Ƶ�������׳���?
	 */
	public void setPostData(byte[] postData) throws Exception
	{
		if(iMethod != METHOD.METHOD_POST || iIsSetPostData)
		{
			throw new Exception("the method should be METHOD.METHOD_POST or is already set post data");
		}
		if(postData != null)
		{
			HttpEntity entry = new ByteArrayEntity(postData);
			((HttpPost) iRequestMethod).setEntity(entry);
			iIsSetPostData = true;
		}
	}
	
	public void setPostData(Map< String, String> params) throws Exception
	{
		if(iMethod != METHOD.METHOD_POST || iIsSetPostData)
		{
			throw new Exception("the method should be METHOD.METHOD_POST or is already set post data");
		}
		
		List< BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
		for (Map.Entry< String, String> entry : params.entrySet()) {
			postData.add(new BasicNameValuePair(entry.getKey(),
					entry.getValue()));
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, HTTP.UTF_8);
		((HttpPost) iRequestMethod).setEntity(entity);
		iIsSetPostData = true;
	}
	
	/**
	 * ���÷ֶ��ϴ���ݣ��ϴ��ļ�ʱ����ô˷���name��params������һ����Ϊnull
	 * 
	 * @param filename  ��Ҫ�ϴ����ļ�·������ѡ��
	 * @param name 		��ѡ�e.g pic��
	 * @param params 	��ѡ����������������������?
	 * 
	 * @throws Exception ���iMethod��ΪMETHOD_POST������֮ǰ�Ѿ����ù����Ƶ�������׳���?
	 */
	public void setMultiPartFile(String filename, String name, Hashtable<String, String> params) throws Exception
	{
		if(iMethod != METHOD.METHOD_POST || iIsSetPostData)
		{
			throw new Exception("the method should be METHOD.METHOD_POST or is already set post data");
		}
		
		String boundary = getBoundary();
		iRequestMethod.setHeader("Content-Type","multipart/form-data; boundary=" + boundary);
		File file = new File(filename);
		StringBuffer header = new StringBuffer();
		header.append("--");
		header.append(boundary);
		header.append("\r\n");
		header.append("Content-Disposition: form-data; ");
		if(params != null && params.size() > 0){
			Enumeration<String> e = params.keys();
			while (e.hasMoreElements())
			{
				String key = e.nextElement();
				String value = params.get(key);
				header.append(key + "=" + value + "; ");
			}
		}
		else{
			header.append("name=\""+name+"\"; ");
		}
		header.append("filename=\"" + file.getName() + "\"\r\n");
		header.append("Content-Type: application/octet-stream;\r\n");
		header.append("Content-Transfer-Encoding: binary\r\n");
		header.append("\r\n");
		
		byte[] head_data = header.toString().getBytes();
		byte[] end_data = ("\r\n--" + boundary + "--\r\n").getBytes();
		InputStream in = new FileInputStream(file);
		sequenceMutiTypeEntity sequenceentry = new sequenceMutiTypeEntity();
		sequenceentry.addByteArray(head_data, -1);
		sequenceentry.addInputStream(in, (int)(file.length()));
		sequenceentry.addByteArray(end_data, -1);
		HttpEntity entry = sequenceentry;
		((HttpPost) iRequestMethod).setEntity(entry);
		
		iIsSetPostData = true;
	}
	
	public void setHttpStatusCode(int code)
	{
		if(iResponseMap != null)
			iResponseMap.put("status_code", code);
	}
	public int getHttpStatusCode()
	{
		if(iResponseMap != null && iResponseMap.containsKey("status_code"))
		{
			return (Integer) iResponseMap.get("status_code");
		}
		else
			return -1;
	}
	public void setResponseBody(Object obj)
	{
		if(iResponseMap != null)
			iResponseMap.put("body_obj",obj);
	}
	public Object getResponseBody()
	{
		if(iResponseMap != null && iResponseMap.containsKey("body_obj"))
		{
			return iResponseMap.get("body_obj");
		}
		else
			return null;
	}

	/** 
	 * ����������صĴ��?
	 * 
	 * @param size 			������صĴ��?
	 * @param buildHeader 	�Ƿ�Ҫ����Range ��header����Ҫ�ϵ��������ʱ��Ϊtrue
	 */
	public void setCompeleteSize(int size, boolean buildHeader)
	{
		iCompeleteSize = size;
		if(buildHeader)
			iRequestMethod.setHeader("Range", "bytes="+iCompeleteSize+"-");
	}
	
	public int getCompeleteSize()
	{
		return iCompeleteSize;
	}
	
	public void setTotalSize(int size)
	{
		iTotalSize  = size;
	}
	
	public int getTotalSize()
	{
		return iTotalSize;
	}

	protected void setDefaultHeader()
	{
		if(iRequestMethod != null)
		{
			iRequestMethod.setHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
			iRequestMethod.setHeader("Accept-Language","en-us,zh-cn,zh-tw,en-gb,en;q=0.7,*;q=0.3");
			iRequestMethod.setHeader("Accept-Charset","big5,gb2312,gbk,utf-8,ISO-8859-1;q=0.7,*;q=0.7");
			iRequestMethod.setHeader("Accept","text/html,application/xml;q=0.9,application/xhtml+xml,text/xml;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		}
	}

	public void doCancel() {
		// TODO Auto-generated method stub
		if(iRequestMethod != null)
		{
			iIsCancel = true;
			iRequestMethod.abort();
		}
	}
	public boolean isCancel()
	{
		return iIsCancel;
	}
	
	private String getBoundary() {
		return String.valueOf(System.currentTimeMillis()) + "----------";
	}
}
