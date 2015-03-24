package com.example.http;

/** 
 * IHttpCallback.java
 * http����Ļص��ӿڣ��ڴ���CustomHttpRequestʱ����
 * @author pan rui
 * @version 1.0
 * @date 2011-10-29
 */
public interface IHttpCallback 
{
	/** 
	 * ���ؽ�ȸ��£������ļ�ʱ�ص�?
	 * 
	 * @param aRequestId      	ͬ��
	 * @param aTotalSize 		�����ļ����ܴ�С
	 * @param aCompleteSize 	�Ѿ���ɵĴ��?
	 */
	public void onUpdate(int aRequestId, int aTotalSize, int aCompleteSize);

	/** 
	 * ����ɹ�ʱ�ص�?
	 * 
	 * @param aRequestId 		ͬ��
	 * @param aErrCode 			http״̬�룬200,206��
	 * @param aResult 			���ؽ�������ù������ļ�����Ϊ�ļ���String������Ϊ�ı�����
	 */
	public void onComplete(int aRequestId, int aErrCode, Object aResult);

}
