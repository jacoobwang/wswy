package com.example.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {
	
	
	/**
	 * @desc 判断是否为数字
	 * @param str string
	 * @return boolean
	 */
	public static boolean isNumberic(String str){
		Pattern p = Pattern.compile("^\\d*$");
		Matcher m = p.matcher(str);
		if(m.matches()){
			return true;
		}
		return false;
	}
	
	/**
	 * @desc 判断是否为手机号码
	 * @param str
	 * @return
	 */
	public static boolean isCellPhone(String str){
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(str);
		if(m.matches()){
			return true;
		}
		return false;
	}
	
	/**
	 * @desc 判断是否固定电话
	 * @param str
	 * @return
	 */
	public static boolean isPhone(String str){
		Pattern p = Pattern.compile("^\\d{3,4}-\\d{7,8}$");
		Matcher m = p.matcher(str);
		if(m.matches()){
			return true;
		}
		return false;
	}
	
}
