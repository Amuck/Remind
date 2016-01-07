package com.remind.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
 * 登录方式 0为远程登录 1为本地登录
 * @author Administrator
 *
 */
public class MySharedPreferencesLoginType {
	private static final String PREFERENCES_NAME = "com.remind.userInfo.data";
	private static final String ACCOUNT_NAME = "com.remind.account.data";
	public static final String LOGINTYPE = "loginType";
	public static final String USERNAME = "userName";
	public static final String ACCOUNT = "account";
	public static final String PASSWORD = "password";
	public static final String RANDOM = "random";
	public static final String MACADDRESS = "macAddress";
	public static final String DATE = "date";
	
	/**
	 * 登陆日期
	 * @param context
	 * @param date
	 */
	public static void saveDate(Context context,String date){
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(DATE, date);

		editor.commit();
	}
	/**
	 *登录方式 0为远程登录 1为本地登录
	 * @param context
	 * @param loginType
	 */
	public static void saveUserInfo(Context context,String loginType){
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(LOGINTYPE, loginType);

		editor.commit();
	}
	/**
	 *保存随机码
	 * @param context
	 * @param loginType
	 */
	public static void saveRandmStr(Context context,String random){
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(RANDOM, random);
		
		editor.commit();
	}
	public static void saveAccount(Context context,String account){
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(ACCOUNT, account);
		
		editor.commit();
	}
	/**
	 *保存用户名
	 * @param context
	 * @param userName
	 * @param macAddress
	 */
	public static void saveUserName(Context context,String userName,String password){
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(USERNAME, userName);
		editor.putString(PASSWORD, password);
//		editor.putString(MACADDRESS, macAddress);
		editor.commit();
	}
	
	/**
	 * 清空sp
	 * @param context
	 */
	public static void clear(Context context){
	    SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
	    Editor editor = pref.edit();
	    editor.clear();
	    editor.commit();
	}
	public static void clearAccount(Context context){
		SharedPreferences pref = context.getSharedPreferences(ACCOUNT_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
	
	/**
	 * 得到值
	 * @param context
	 * @param key
	 * @return
	 */
	public static String getString(Context context,String key){
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		String value = pref.getString(key, "");
		return value;
	}
	
}
