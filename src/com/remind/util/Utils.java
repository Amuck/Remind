package com.remind.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	/**
	 * 判断空
	 * 
	 * @param rawData
	 * @return
	 */
	public static boolean isNull(String rawData) {
		if (null != rawData && !"".equals(rawData) && !"null".equals(rawData))
			return true;
		else
			return false;
	}

	/**
	 * 格式化数据
	 * 
	 * @param s
	 * @return
	 */
	public static String stringTrim(String s) {
		return isNull(s) ? s.trim() : s;
	}


	public static String converIdToString(int id, Context mContext) {
		String result = null;
		result = mContext.getResources().getString(id);
		return result;
	}

	/**
	 * 获取字符串中包含某字符个数
	 * 
	 * @param str
	 * @param find
	 * @return
	 */
	public static int getCount(String str, String find) {
		int o = 0;
		int index = -1;
		while ((index = str.indexOf(find, index)) > -1) {
			++index;
			++o;
		}
		return o;
	}

	/**
	 * 如果服务器不支持中文路径的情况下需要转换url的编码。
	 * 
	 * @param string
	 * @return
	 */
	public static String encodeGB(String string) {
		// 转换中文编码
		String split[] = string.split("/");
		for (int i = 1; i < split.length; i++) {
			try {
				split[i] = URLEncoder.encode(split[i], "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			split[0] = split[0] + "/" + split[i];
		}
		split[0] = split[0].replaceAll("\\+", "%20");// 处理空格
		split[0] = split[0].replaceAll("\\{", "%7B");
		split[0] = split[0].replaceAll("\\}", "%7D");
		split[0] = split[0].replaceAll("%3A", "\\:");
		return split[0];
	}
	
	/**
	 * dip转像素
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 像素转dip
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	  /** 
     * 将px值转换为sp值，保证文字大小不变 
     *  
     * @param pxValue 
     * @param fontScale 
     *            （DisplayMetrics类中属性scaledDensity） 
     * @return 
     */ 
    public static int px2sp(Context context, float pxValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }  
    /** 
     * 将sp值转换为px值，保证文字大小不变 
     *  
     * @param spValue 
     * @param fontScale 
     *            （DisplayMetrics类中属性scaledDensity） 
     * @return 
     */ 
    public static int sp2px(Context context, float spValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    }  

	/**
	 * 获取当前时间
	 */
	public static String getNowTime() {
		String time = null;
		Date now = new Date();
		SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		time = temp.format(now);
		return time;
	}
	/**
	 * 获取当前日期
	 */
	public static String getNowDate() {
		String time = null;
		Date now = new Date();
		SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd");
		time = temp.format(now);
		return time;
	}
	/**
	 * 获取3天前的日期
	 */
	public static String get3DaysAgoDate() {
		Calendar cal = Calendar.getInstance();
		String time = null;
		SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd");
		cal.setTime(new Date());
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) -3);
		time = temp.format(cal.getTime());
		return time;
	}
public static boolean isAvailable(Context context){
		
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		
		if (info == null || !info.isConnected() || info.isRoaming()) 
			return false;
		
		return true;
	}
public static String[] get2File(String uri ){
	if(uri==null){
		return null;
	}
	String[] strs= new String[2];
	File file = new File(uri);
	byte[] b = null;
	StringBuilder str = null;
	try {
		strs[0]=uri.substring(uri.lastIndexOf("/")+1, uri.length());
		FileInputStream fis = new FileInputStream(file);
		b = new byte[fis.available()];
		str = new StringBuilder();
		 fis.read(b);
		 strs[1]=new String(b,"UTF-8");
	} catch (FileNotFoundException e1) {
		e1.printStackTrace();
	} catch (IOException e1) {
		e1.printStackTrace();
	}
	return strs;
	}
/**
 * 获取明天的日期
 */
public static String getTomorrowAgoDate() {
	Calendar cal = Calendar.getInstance();
	String time = null;
	SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd");
	cal.setTime(new Date());
	cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
	time = temp.format(cal.getTime());
	return time;
}
}
