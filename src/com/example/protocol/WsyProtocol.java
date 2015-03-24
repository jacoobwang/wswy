package com.example.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.entity.ChatMsgEntity;
import com.example.http.CustomHttpRequest;
import com.example.http.IHttpCallback;
import com.example.http.SingleHttpClient;
import com.example.protocol.WsyMeta.MStoreInfo;
import com.example.protocol.WsyMeta.UserInfo;


public class WsyProtocol implements IHttpCallback{
	private static WsyProtocol iInstance;
	private HashMap<Integer, MDataUpdateNotify> iObserverMap = new HashMap<Integer, MDataUpdateNotify>();
	private MDataUpdateNotify	iObserver;
	
	public static int sTimeDiscountIndex = 1;
	private int	iRequestId;
	private int iPageSize = 6;
	
	public static final int KRequestIdGetStore = 1001;
	public static final int kRequestIdSendLoginInfo = 1002;
	public static final int kRequestIdGetCart = 1003;
	public static final int KRequestIdSendRegInfo = 1004;
	public static final int KRequestIdSendOrder = 1005;
	public static final int KRequestIdSendEdit = 1006;
	public static final int KRequestIdGetMeInfo = 1007;
	public static final int kRequestIdSendCartInfo = 1008;	
	public static final int kRequestIdSendPhone = 1009;	
	public static final int kRequestIdSendInvite = 1010;	
	public static final int kRequestIdGetFriend = 1011;	
	public static final int kRequestIdSendChat = 1012;	
	public static final int KRequestIdUpdateAvatar = 1013;	
	public static final int KRequestIdGetMsgInfo = 1014;
	public static final int KRequestIdGetHandleInfo = 1015;
	public static final int KRequestIdSendHandle = 1016;
	
	public static final String KBaseUrl = "http://211.149.149.145/wswy/index.php";
	
	public static WsyProtocol getInstance(){
		if(iInstance == null)
		{
			iInstance = new WsyProtocol();
		}
		return iInstance;
	}
	
	public static void Destroy(){
		if(iInstance != null)
		{
			SingleHttpClient.Instance().StopRequestThread();
			iInstance.iObserverMap.clear();
			iInstance = null;
		}
	}
	
	/**
	 * @desc 获取商品
	 */
	public void getStore(boolean aRest,MDataUpdateNotify aObserver,int id){
		if(aRest){
			sTimeDiscountIndex = 1;
		}
		iRequestId = KRequestIdGetStore;
		iObserverMap.remove(id);
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder(KBaseUrl);
		vGetUrl.append('?');
		vGetUrl.append("t=1");
		vGetUrl.append("&type=");
		vGetUrl.append(id);
		vGetUrl.append("&q=");
		vGetUrl.append(sTimeDiscountIndex);
		vGetUrl.append("&y=");
		vGetUrl.append(iPageSize-1);
		SingleHttpClient.Instance().doAsyncRequest(
				new CustomHttpRequest(iRequestId, vGetUrl.toString(), this),
				true);
		Log.i("WsyProtocol","vGetUrl="+ vGetUrl);
	}
	
	/**
	 * @desc 获取好友列表
	 */
	public void getFriendList(MDataUpdateNotify aObserver,String uid){
		iRequestId = kRequestIdGetFriend;
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder(KBaseUrl);
		vGetUrl.append("?");
		vGetUrl.append("t=14");
		vGetUrl.append("&uid=");
		vGetUrl.append(uid);
		
		SingleHttpClient.Instance().doAsyncRequest(
				new CustomHttpRequest(iRequestId, vGetUrl.toString(), this),
				true);
		Log.i("WsyProtocol","vGetUrl="+ vGetUrl);
	}
	
	/**
	 * @desc 购物车
	 */
	public void getCart(MDataUpdateNotify aObserver,String uid,String type){
		iRequestId = kRequestIdGetCart;
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder(KBaseUrl);
		vGetUrl.append("?");
		vGetUrl.append("t=11");
		vGetUrl.append("&uid=");
		vGetUrl.append(uid);
		vGetUrl.append("&type=");
		vGetUrl.append(type);
		SingleHttpClient.Instance().doAsyncRequest(
				new CustomHttpRequest(iRequestId, vGetUrl.toString(), this),
				true);
		Log.i("WsyProtocol","vGetUrl="+ vGetUrl);
	}
	
	/**
	 * @desc 获取用户信息
	 */
	public void getUserDetail(MDataUpdateNotify aObserver,String uid){
		iRequestId = KRequestIdGetMeInfo;
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder(KBaseUrl);
		vGetUrl.append("?");
		vGetUrl.append("t=6");
		vGetUrl.append("&uid=");
		vGetUrl.append(uid);
		SingleHttpClient.Instance().doAsyncRequest(
				new CustomHttpRequest(iRequestId, vGetUrl.toString(), this),
				true);
		Log.i("WsyProtocol","vGetUrl="+ vGetUrl);
	}
	
	public void getHandleMsg(MDataUpdateNotify aObserver,String uid){
		iRequestId = KRequestIdGetHandleInfo;
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder(KBaseUrl);
		vGetUrl.append("?");
		vGetUrl.append("t=17");
		vGetUrl.append("&uid=");
		vGetUrl.append(uid);
		SingleHttpClient.Instance().doAsyncRequest(
				new CustomHttpRequest(iRequestId, vGetUrl.toString(), this),
				true);
		Log.i("WsyProtocol","vGetUrl="+ vGetUrl);
	}
	
	/**
	 * @desc 获取聊天信息
	 */
	public void getUserMsg(MDataUpdateNotify aObserver,String uid,String pid) {
		iRequestId = KRequestIdGetMsgInfo;
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder(KBaseUrl);
		vGetUrl.append("?");
		vGetUrl.append("t=16");
		vGetUrl.append("&uid=");
		vGetUrl.append(uid);
		vGetUrl.append("&pid=");
		vGetUrl.append(pid);
		SingleHttpClient.Instance().doAsyncRequest(
				new CustomHttpRequest(iRequestId, vGetUrl.toString(), this),
				true);
		Log.i("WsyProtocol","vGetUrl="+ vGetUrl);
	}
	
	/**
	 * @desc 注册
	 */
	public void SendRegInfo(MDataUpdateNotify aObserver,String p1,String p2,String p3,String p4,
			String p5,String p6){
		iRequestId = KRequestIdSendRegInfo;
		iObserverMap.remove(iRequestId);
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder(KBaseUrl);
		vGetUrl.append('?');
		vGetUrl.append("t=2");
		vGetUrl.append("&p1=");
		vGetUrl.append(p1);
		vGetUrl.append("&p2=");
		vGetUrl.append(p2);
		vGetUrl.append("&p3=");
		vGetUrl.append(p3);
		vGetUrl.append("&p4=");
		vGetUrl.append(p4);
		vGetUrl.append("&p5=");
		vGetUrl.append(p5);
		vGetUrl.append("&p6=");
		vGetUrl.append(p6);
		SingleHttpClient.Instance().doAsyncRequest(
				new CustomHttpRequest(iRequestId, vGetUrl.toString(), this),
				true);
		Log.i("WsyProtocol","vGetUrl="+ vGetUrl);
	}
	
	/**
	 * @desc 登录
	 */
	public void SendLoginInfo(MDataUpdateNotify aObserver,String p1,String p2) {
		iRequestId = kRequestIdSendLoginInfo;
		iObserverMap.remove(iRequestId);
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder(KBaseUrl);
		vGetUrl.append("?");
		vGetUrl.append("t=3");
		vGetUrl.append("&p1=");	
		vGetUrl.append(p1);	
		vGetUrl.append("&p2=");	
		vGetUrl.append(p2);	
		SingleHttpClient.Instance().doAsyncRequest(
				     new CustomHttpRequest(iRequestId, vGetUrl.toString(),this ),
				     true);
		Log.i("WsyProtcol", "vGetUrl="+ vGetUrl);
	}
	
	/**
	 * 添加购物车
	 */
	public void SendAddCart(MDataUpdateNotify aObserver,String uId, String proId){
		iRequestId = kRequestIdSendCartInfo;
		iObserverMap.remove(iRequestId);
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder(KBaseUrl);
		vGetUrl.append("?");
		vGetUrl.append("t=7");
		vGetUrl.append("&p1=");	
		vGetUrl.append(uId);	
		vGetUrl.append("&p2=");	
		vGetUrl.append(proId);	
		SingleHttpClient.Instance().doAsyncRequest(
				     new CustomHttpRequest(iRequestId, vGetUrl.toString(),this ),
				     true);
		Log.i("WsyProtcol", "vGetUrl="+ vGetUrl); 
	}
	
	/**
	 * @desc 编辑个人信息
	 */
	public void SendEditInfo(MDataUpdateNotify aObserver,String p1,String p2,String p3){
		iRequestId = KRequestIdSendEdit;
		iObserverMap.remove(iRequestId);
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder(KBaseUrl);
		vGetUrl.append("?");
		vGetUrl.append("t=5");
		vGetUrl.append("&mtype=");	
		vGetUrl.append(p1);
		vGetUrl.append("&msg=");	
		vGetUrl.append(p2);	
		vGetUrl.append("&uid=");	
		vGetUrl.append(p3);
		
		Log.i("WsyProtcol", "vGetUrl="+ vGetUrl);
		SingleHttpClient.Instance().doAsyncRequest(
			     new CustomHttpRequest(iRequestId, vGetUrl.toString(),this ),
			     true);
	}
	
	/**
	 * @desc 下单
	 */
	public void SendOrder(MDataUpdateNotify aObserver,String p1,String p2){
		iRequestId = KRequestIdSendOrder;
		iObserverMap.remove(iRequestId);
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder(KBaseUrl);
		vGetUrl.append("?");
		vGetUrl.append("t=4");
		vGetUrl.append("&p1=");	
		vGetUrl.append(p1);
		vGetUrl.append("&p2=");	
		vGetUrl.append(p2);	
		SingleHttpClient.Instance().doAsyncRequest(
			     new CustomHttpRequest(iRequestId, vGetUrl.toString(),this ),
			     true);
	}
	
	/**
	 * @desc 发送手机号，检查手机用户是否已注册
	 */
	public void SendPhoneNum(MDataUpdateNotify aObserver,String phone) {
		iRequestId = kRequestIdSendPhone;
		iObserverMap.remove(iRequestId);
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder(KBaseUrl);
		vGetUrl.append("?");
		vGetUrl.append("t=12");
		vGetUrl.append("&p1=");	
		vGetUrl.append(phone);
		Log.i("WsyProtcol", "vGetUrl="+ vGetUrl);
		
		SingleHttpClient.Instance().doAsyncRequest(
			     new CustomHttpRequest(iRequestId, vGetUrl.toString(),this ),
			     true);
	}
	
	/**
	 * @desc 发送消息保存到DB
	 */
	public void SendChatMsg(MDataUpdateNotify aObserver,String msg,String uid,String pid){
		iRequestId = kRequestIdSendChat;
		iObserverMap.remove(iRequestId);
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder(KBaseUrl);
		vGetUrl.append("?");
		vGetUrl.append("t=15");
		vGetUrl.append("&msg=");	
		vGetUrl.append(msg);
		vGetUrl.append("&uid=");
		vGetUrl.append(uid);
		vGetUrl.append("&pid=");
		vGetUrl.append(pid);
		Log.i("WsyProtcol", "vGetUrl="+ vGetUrl);
		
		SingleHttpClient.Instance().doAsyncRequest(
			     new CustomHttpRequest(iRequestId, vGetUrl.toString(),this ),
			     true);
	}
	
	/**
	 * @desc  向好友发送添加申请
	 */
	public void SendAddFriend(MDataUpdateNotify aObserver,String phone,String uid){
		iRequestId = kRequestIdSendInvite;
		iObserverMap.remove(iRequestId);
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder(KBaseUrl);
		vGetUrl.append("?");
		vGetUrl.append("t=13");
		vGetUrl.append("&p1=");	
		vGetUrl.append(phone);
		vGetUrl.append("&p2=");
		vGetUrl.append(uid);		
		Log.i("WsyProtcol", "vGetUrl="+ vGetUrl);
		
		SingleHttpClient.Instance().doAsyncRequest(
			     new CustomHttpRequest(iRequestId, vGetUrl.toString(),this ),
			     true);
	}
	
	public void sendHandle(MDataUpdateNotify aObserver,String uid,String type,String num){
		iRequestId = KRequestIdSendHandle;
		iObserverMap.remove(iRequestId);
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder(KBaseUrl);
		vGetUrl.append("?");
		vGetUrl.append("t=20");
		vGetUrl.append("&p1=");	
		vGetUrl.append(uid);
		vGetUrl.append("&p2=");
		vGetUrl.append(type);		
		vGetUrl.append("&p3=");
		vGetUrl.append(num);	
		Log.i("WsyProtcol", "vGetUrl="+ vGetUrl);
		
		SingleHttpClient.Instance().doAsyncRequest(
			     new CustomHttpRequest(iRequestId, vGetUrl.toString(),this ),
			     true);
	}
	
	/**
	 * @desc 上传头像
	 */
	public void UpdateAvatar(MDataUpdateNotify aObserver,String uid, byte[] aAvatarData){
		iRequestId = KRequestIdUpdateAvatar;
		iObserverMap.remove(iRequestId);
		iObserverMap.put(iRequestId, aObserver);
		
		StringBuilder vGetUrl = new StringBuilder();
		vGetUrl.append(uid);
		vGetUrl.append("&userimg=");

		byte[] abyte = vGetUrl.toString().getBytes();
		
		CustomHttpRequest request = new CustomHttpRequest(iRequestId, "211.149.149.145/wswy/image.php",
				com.example.http.CustomHttpRequest.METHOD.METHOD_POST, this);
		
		byte[] abyte2 = new byte[abyte.length + aAvatarData.length];
		int j = 0;
		for (int i = 0; i < abyte.length; i++)
		{
			abyte2[j] = abyte[i];
			j ++;
		}
		for (int i = 0; i < aAvatarData.length; i++)
		{
			abyte2[j] = aAvatarData[i];
			j++;
		}
		
		try {
			request.setPostData(abyte2);
			SingleHttpClient.Instance().doAsyncRequest(request,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @desc 解析商品信息
	 */
	private int ParseStoreData(String aData){
		int ret = -1001;
		if(aData == null || aData.length() <= 0)
		{
			return ret;
		}	
		try{
			List<WsyMeta.MStoreInfo> list = new ArrayList<MStoreInfo>();
			JSONObject jsonObj = new JSONObject(aData);
			String command = jsonObj.getString("command");
			if(command.equalsIgnoreCase("1")){
				String info = jsonObj.getString("proinfo");
				JSONArray array = new JSONArray(info);
				for (int i = 0; i < array.length(); i++)
				{
					WsyMeta.MStoreInfo proInfo =new WsyMeta.MStoreInfo();
					JSONObject object2 = array.optJSONObject(i);
					
					proInfo.mProId = object2.optString("id");
					proInfo.mProImg = object2.optString("img");
					proInfo.mProTitle = object2.optString("title");
					proInfo.mProPrice = object2.optString("price");
					proInfo.mProOldPrice = object2.optString("old_price");
					proInfo.mProBuyNum = object2.optString("num");
					
					list.add(proInfo);
				}
				sTimeDiscountIndex += iPageSize - 1;
				if(iObserver != null){
					iObserver.OnNewDataArrived(KRequestIdGetStore, 1, list);
				}
			}else{
				if(iObserver != null){
					iObserver.OnNewDataArrived(KRequestIdGetStore, 1, null);
				}
			}
			ret = 1;
		}catch(Exception e){
			e.printStackTrace();	
			Log.i("WsyProtocol",aData);
		}
		return ret;
	}
	
	/**
	 * @desc 解析返回的好友信息
	 * @param aData
	 * @return
	 */
	private int ParseGetFriend(String aData){
		int ret = -1001;
		if(aData == null || aData.length() <= 0)
		{
			return ret;
		}
		try{
			List<WsyMeta.UserInfo> list = new ArrayList<UserInfo>();
			JSONObject jsonObj = new JSONObject(aData);
			String command = jsonObj.getString("command");
			String info = jsonObj.getString("info");
			JSONArray array = new JSONArray(info);
			String tmp = "";
			for (int i = 0; i < array.length(); i++)
			{
				WsyMeta.UserInfo userInfo =new WsyMeta.UserInfo();
				JSONObject object2 = array.optJSONObject(i);
				
				userInfo.uid = object2.optString("id");
				userInfo.uname = object2.optString("uname");
				userInfo.mFlag = object2.optString("mflag");
				tmp =  object2.optString("utx");
				if(tmp==null || tmp.length() ==0) userInfo.uTx = "http://211.149.149.145/wswy/tx/default_tx.png";
				else userInfo.uTx   = "http://211.149.149.145/wswy/tx/" + tmp;
				
				list.add(userInfo);
			}
			if(iObserver != null){
				iObserver.OnNewDataArrived(kRequestIdGetFriend, 1, list);
			}
			ret = 1;
		}catch(Exception e){
			e.printStackTrace();	
			Log.i("WsyProtocol",aData);
		}
		return ret;
	}
	
	/**
	 * @desc ������Ϣ����
	 * @param aData
	 * @return
	 */
	private int ParseMeInfo(String aData){
		int ret = -1001;
		if(aData ==null || aData.length()<=0){
			return ret;	
		}
		try{
			JSONObject jsonObj = new JSONObject(aData);
			String command = jsonObj.getString("command");
			if(command.equalsIgnoreCase("1")){
				String nickname = jsonObj.getString("nickname");
				String cellphone = jsonObj.getString("cellphone");
				String phone = jsonObj.getString("phone");
				String address = jsonObj.getString("address");
				String qq = jsonObj.getString("qq");
				
				WsyMeta.UserInfo userInfo =new WsyMeta.UserInfo();
				userInfo.uname = nickname;
				userInfo.cellphone = cellphone;
				userInfo.phone = phone;
				userInfo.address = address;
				userInfo.qq = qq;
				
				Log.v("meInfo", nickname+cellphone+phone+address+qq);
				
				if(iObserver != null)
				{
					iObserver.OnNewDataArrived(KRequestIdGetMeInfo, 1, userInfo);
					ret = 1;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * @desc �������ﳵ��Ϣ
	 * @param aData
	 * @return
	 */
	private int ParseCartData(String aData){
		int ret = -1001;
		if(aData == null || aData.length() <= 0)
		{
			return ret;
		}
		try{
			List<WsyMeta.MStoreInfo> list = new ArrayList<MStoreInfo>();
			JSONObject jsonObj = new JSONObject(aData);
			String command = jsonObj.getString("command");
			if(command.equalsIgnoreCase("1")){
				String info = jsonObj.getString("proinfo");
				JSONArray array = new JSONArray(info);
				for (int i = 0; i < array.length(); i++)
				{
					WsyMeta.MStoreInfo proInfo =new WsyMeta.MStoreInfo();
					JSONObject object2 = array.optJSONObject(i);
					
					proInfo.mProId = object2.optString("id");
					proInfo.mProImg = object2.optString("img");
					proInfo.mProTitle = object2.optString("title");
					proInfo.mProPrice = object2.optString("price");
					proInfo.mProBuyNum = object2.optString("num");
					
					list.add(proInfo);
				}
				if(iObserver != null){
					iObserver.OnNewDataArrived(kRequestIdGetCart, 1, list);
				}
			}else{
				if(iObserver != null){
					iObserver.OnNewDataArrived(kRequestIdGetCart, 4, null);
				}
			}
			ret = 1;
		}catch(Exception e){
			e.printStackTrace();	
		}
		return ret;
	}
	
	/**
	 * @desc ���������Ϣ���������ش�
	 */
	private int ParseSendInfoBak(String aData){
		int ret = -1001;
		if(aData == null || aData.length() <= 0)
		{
			return ret;
		}
		try{
			JSONObject object = new JSONObject(aData);
			
			int commandNew = object.optInt("command");
			String uid = object.optString("uid");
			String uname = object.optString("uname");
			
			HashMap map = new HashMap();
			map.put("uid",uid);
			map.put("uname",uname);
			
			if(iObserver != null &&commandNew==1)
			{
				iObserver.OnNewDataArrived(KRequestIdSendRegInfo, 1, map);
				ret = 1;
			}	
		}catch(Exception e){
			e.printStackTrace();
		}	
		return ret;
	}
	
	/**
	 * @desc ��¼
	 */
	private int ParseSendLoginInfoBak(String aData){
		int ret = -1001;
		if(aData == null || aData.length() <= 0)
		{
			return ret;
		}
		try{
			JSONObject object = new JSONObject(aData);
			int commandNew = object.optInt("command");
			String tmp = "";
			
			WsyMeta.UserInfo userInfo =new WsyMeta.UserInfo();
			userInfo.uid = object.optString("uid");
			userInfo.uname = object.optString("uname");
			tmp = object.optString("utx");
			
			if(tmp==null || tmp.length() ==0) userInfo.uTx = "";
			else userInfo.uTx   = "http://211.149.149.145/wswy/tx/" + tmp;
			
			if(iObserver != null &&commandNew==1)
			{
				iObserver.OnNewDataArrived(kRequestIdSendLoginInfo, 1, userInfo);
				ret = 1;
			}else{
				iObserver.OnNewDataArrived(kRequestIdSendLoginInfo, commandNew, null);
			}
			ret = commandNew;		
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}  
	
	/**
	 * ��������
	 */
	private int ParseSendOrder(String aData){
		int ret = -1001;
		if(aData == null || aData.length() <= 0)
		{
			return ret;
		}
		try{
			JSONObject object = new JSONObject(aData);
			int commandNew = object.optInt("command");
			
			WsyMeta.UserInfo userInfo =new WsyMeta.UserInfo();
			userInfo.uid = object.optString("uid");
			userInfo.uname = object.optString("uname");
			
			if(iObserver != null &&commandNew==1)
			{
				iObserver.OnNewDataArrived(KRequestIdSendOrder, 1, userInfo);
				ret = 1;
			}
			ret = commandNew;		
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * �޸���Ϣ�ص�
	 */
	private int ParseEdit(String aData){
		int ret = -1001;
		if(aData == null || aData.length() <= 0)
		{
			return ret;
		}
		try{
			JSONObject object = new JSONObject(aData);
			int commandNew = object.optInt("command");
			if(iObserver != null &&commandNew==1)
			{
				iObserver.OnNewDataArrived(KRequestIdSendEdit, 1, "");
				ret = 1;
			}
			ret = commandNew;		
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	
	private int ParseSendCartInfo(String aData) {
		int ret = -1001;
		if(aData == null || aData.length() <= 0)
		{
			return ret;
		}
		try{
			JSONObject object = new JSONObject(aData);
			int commandNew = object.optInt("command");
			
			if(iObserver != null &&commandNew==1)
			{
				iObserver.OnNewDataArrived(KRequestIdSendEdit, 1, "");
				ret = 1;
			}
			ret = commandNew;		
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * @desc 解析发送手机号码返回json
	 * @param aData
	 * @return
	 */
	private int ParseSendPhone(String aData){
		int ret = -1001;
		if(aData == null || aData.length() <= 0)
		{
			return ret;
		}
		try{
			JSONObject object = new JSONObject(aData);
			int commandNew = object.optInt("command");
			
			if(iObserver != null &&commandNew==1)
			{
				WsyMeta.UserInfo userInfo =new WsyMeta.UserInfo();
				userInfo.uname = object.optString("nickname");
				iObserver.OnNewDataArrived(kRequestIdSendPhone, 1, userInfo);
				ret = 1;
			}
			ret = commandNew;	
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * @desc 解析发送chat返回信息
	 * @param aData
	 * @return
	 */
	private int ParseSendChat(String aData){
		int ret = -1001;
		if(aData == null || aData.length() <=0){
			return ret;
		}
		try{
			JSONObject object = new JSONObject(aData);
			int commandNew = object.optInt("command");
			
			if(iObserver != null &&commandNew==1)
			{
				iObserver.OnNewDataArrived(kRequestIdSendChat, 1, null);
				ret = 1;
			}
			ret = commandNew;
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * @desc 解析更新头像
	 * @param aData
	 * @return
	 */
	private int ParseUpdateAvatar(String aData){
		int ret = -1001;
		if(aData == null || aData.length() <=0){
			return ret;
		}
		try{
			JSONObject object = new JSONObject(aData);
			int commandNew = object.optInt("command");
			
			if(iObserver != null &&commandNew==1)
			{
				iObserver.OnNewDataArrived(KRequestIdUpdateAvatar, 1, null);
				ret = 1;
			}
			ret = commandNew;
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	
	private int ParseGetMsgInfo(String aData){
		int ret = -1001;
		if(aData == null || aData.length() <=0){
			return ret;
		}
		try{
			List<ChatMsgEntity> list = new ArrayList<ChatMsgEntity>();
			
			JSONObject object = new JSONObject(aData);
			int commandNew = object.optInt("command");
			if(commandNew == 1){
				String info = object.getString("info");
				JSONArray array = new JSONArray(info);
				String tmp = "";
				for (int i = 0; i < array.length(); i++)
				{
					ChatMsgEntity entity = new ChatMsgEntity();
					JSONObject object2 = array.optJSONObject(i);
					
					String isCome = object2.optString("iscome");
					Boolean isComeT;
					isComeT = isCome.equalsIgnoreCase("1")?true:false;
					
					entity.setText(object2.optString("msg"));
					entity.setDate(object2.optString("times"));
					entity.setisComMeg(isComeT);
					entity.setName(object2.optString("name"));
					
					tmp = object2.optString("tx");
					if(tmp==null || tmp.length()==0){
						entity.setImage("http://211.149.149.145/wswy/tx/default_tx.png");	
					}else{
						entity.setImage("http://211.149.149.145/wswy/tx/"+tmp);	
					}
					
					list.add(entity);
				}
				if(iObserver != null){
					iObserver.OnNewDataArrived(KRequestIdGetMsgInfo, 1, list);
				}
			}else{
				if(iObserver != null){
					iObserver.OnNewDataArrived(KRequestIdGetMsgInfo, 1, null);
				}
			}
			
			ret = 1;
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	
	private int ParseGetHandleInfo(String aData){
		int ret = -1001;
		if(aData == null || aData.length() <=0){
			return ret;
		}
		try{
			List<WsyMeta.UserInfo> list = new ArrayList<UserInfo>();
			
			JSONObject object = new JSONObject(aData);
			int commandNew = object.optInt("command");
			
			if(commandNew == 1){
				String info = object.getString("info");
				JSONArray array = new JSONArray(info);
				for (int i = 0; i < array.length(); i++)
				{
					WsyMeta.UserInfo userInfo =new WsyMeta.UserInfo();
					JSONObject object2 = array.optJSONObject(i);
					userInfo.uname = object2.optString("uname");
					userInfo.cellphone = object2.optString("cellphone");
					list.add(userInfo);
				}
				if(iObserver != null){
					iObserver.OnNewDataArrived(KRequestIdGetHandleInfo, 1, list);
				}
			}else{
				if(iObserver != null){
					iObserver.OnNewDataArrived(KRequestIdGetHandleInfo, 1, null);
				}
			}
			
			ret = 1;
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * #desc 解析发送添加邀请给好友
	 * @param aData
	 * @return
	 */
	private int ParseSendInvite(String aData){
		int ret = -1001;
		if(aData == null || aData.length() <= 0)
		{
			return ret;
		}
		try{
			JSONObject object = new JSONObject(aData);
			int commandNew = object.optInt("command");
			
			if(iObserver != null &&commandNew==1)
			{
				WsyMeta.UserInfo userInfo =new WsyMeta.UserInfo();
				iObserver.OnNewDataArrived(kRequestIdSendInvite, 1, null);
				ret = 1;
			}
			ret = commandNew;	
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}

	
	public interface MDataUpdateNotify{
		public boolean OnNewDataArrived(int aRequestType, int aErrCode, Object aData);	
	}


	@Override
	public void onUpdate(int aRequestId, int aTotalSize, int aCompleteSize) {
		
	}


	@Override
	public void onComplete(int aRequestId, int aErrCode, Object aResult) {
		iObserver = iObserverMap.remove(aRequestId);
		if(iObserver == null){
			Log.i("WsyProtocol","invail request");
			return;
		}
		if(aErrCode != 200)
		{
			Log.i("WsyProtocol","errcode-1 = " + aErrCode + " request id = " + aRequestId);
			iObserver.OnNewDataArrived(aRequestId, aErrCode, null);
			return;
		}
		int vRet = 1;
		
		switch(aRequestId){
			case KRequestIdGetStore:
				vRet = ParseStoreData((String)aResult);
				break;
			case KRequestIdSendRegInfo:
				vRet = ParseSendInfoBak((String)aResult);
				break;
			case kRequestIdSendLoginInfo:
				vRet = ParseSendLoginInfoBak((String) aResult);
				break;
			case kRequestIdGetCart:
				vRet = ParseCartData((String)aResult);
				break;
			case KRequestIdSendEdit:
				vRet = ParseEdit((String) aResult);
				break;
			case KRequestIdGetMeInfo:
				vRet = ParseMeInfo((String) aResult);
				break;
			case kRequestIdSendCartInfo:
				vRet = ParseSendCartInfo((String) aResult);	
				break;
			case kRequestIdSendPhone:
				vRet = ParseSendPhone((String) aResult);
				break;
			case kRequestIdSendInvite:
				vRet = ParseSendInvite((String) aResult);
				break;
			case kRequestIdGetFriend:
				vRet = ParseGetFriend((String) aResult);
				break;
			case kRequestIdSendChat:
				vRet = ParseSendChat((String) aResult);
				break;
			case KRequestIdUpdateAvatar:
				vRet = ParseUpdateAvatar((String) aResult);
				break;
			case KRequestIdGetMsgInfo:
				vRet = ParseGetMsgInfo((String) aResult);
				break;
			case KRequestIdGetHandleInfo:
				vRet = ParseGetHandleInfo((String) aResult);
				break;
			case KRequestIdSendHandle:
				break;
			default:
				break;
		}
		
		//err report
		if(vRet != 1)
		{
			if(iObserver != null)
			{
				iObserver.OnNewDataArrived(aRequestId, vRet, null);
			}
		}
	}

}
