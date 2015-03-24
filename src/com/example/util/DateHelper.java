package com.example.util;

import java.util.Calendar;
import java.util.TimeZone;

public class DateHelper {
	
	public void main(String[] args){
		
		String a = getDate();
		System.out.print(a);
	}
	
	/**
	 * @desc 返回时间的字符串形式
	 * @return
	 */
	public static String getDate(){
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));//获取东8区时间

		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH)+1) ;
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));

		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
				+ mins);
		return sbBuffer.toString();
	}
}
