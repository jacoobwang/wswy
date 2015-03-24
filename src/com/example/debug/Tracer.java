package com.example.debug;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Tracer
{
	private static Tracer iInstance;
	private FileOutputStream ifOut;
	//private static String iTraceTitle = MyApplication.Instance().getApplicationInfo().name;
	private StringBuffer iLogSb = new StringBuffer();
	
	public static Tracer GetInstance()
	{
		if(iInstance == null)
		{
			iInstance = new Tracer();
		}
		return iInstance;
	}
	
	public static void Destroy()
	{
		if(iInstance != null)
		{
			iInstance.DestroyTracer();
			iInstance = null;
		}
	}
	
	public static void Log(String aLog)
	{
		StringBuffer sb = new StringBuffer(aLog);
		sb.append(getTraceInfo(2));
		//android.util.Log.d(iTraceTitle, sb.toString());
	}
	
	public static void Log1(String aLog)
	{
		StringBuffer sb = new StringBuffer(aLog);
		sb.append(getTraceInfo(1));
		//android.util.Log.d(iTraceTitle, sb.toString());
	}
	
	public static void Info(String aLog)
	{
		StringBuffer sb = new StringBuffer(aLog);
		sb.append(getTraceInfo(2));
		//android.util.Log.w(iTraceTitle, sb.toString());
	}
	
	public static String getTraceInfo(int aLevel)
	{   
		StringBuffer sb = new StringBuffer();    
		StackTraceElement[] stacks = new Throwable().getStackTrace();   
		int stacksLen = stacks.length;   
		if(aLevel > stacksLen-1)
		{
			aLevel = stacksLen-1;
		}
		sb.append(" [class: " ).append(stacks[aLevel].getClassName()).append("; method: ").append(stacks[aLevel].getMethodName()).append("; number: ").append(stacks[aLevel].getLineNumber()).append(']');   

		return sb.toString();   
	}
	
	public void WriteLog(String aContent)
	{
		try
		{
			if(ifOut == null)
			{
				File vLog = new File("/sdcard/jpy/jpy_log.txt");
				if(!vLog.exists())
				{
					vLog.createNewFile();
				}
				ifOut = new FileOutputStream(vLog, false);
//				ifOut = MyApplication.getInstance().openFileOutput("", Context.MODE_APPEND);  
			}
			iLogSb.setLength(0);
			iLogSb.append(aContent);
			iLogSb.append('\n');
			ifOut.write(iLogSb.toString().getBytes());
			ifOut.flush();
		}
		catch (Exception e) 
		{      
			e.printStackTrace();
		}
	}
	
	private void DestroyTracer()
	{
		if(ifOut != null)
		{
			try
			{
				ifOut.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}   