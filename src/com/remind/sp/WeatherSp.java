package com.remind.sp;

import com.help.remind.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class WeatherSp {
	private static final String PREFERENCES_NAME = "com.remind.date.data";
	/**
	 * 地点
	 */
	public static final String LOC = "location";
	/**
	 * 温度
	 */
	public static final String TEM = "tempture";
	/**
	 * 天气
	 */
	public static final String WEATHER = "weather";
	/**
	 * 天气图片
	 */
	public static final String WEATHER_IMG = "WeatherImg";

	public static void saveLOC(Context context, String loc) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(LOC, loc);
		editor.commit();
	}
	
	public static void saveTEM(Context context, String tem) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(TEM, tem);
		editor.commit();
	}
	
	public static void saveWEATHER(Context context, String weather) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(WEATHER, weather);
		editor.commit();
	}
	
	public static void saveWeatherImg(Context context, int WeatherImg) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putInt(WEATHER_IMG, WeatherImg);
		editor.commit();
	}

	/**
	 * 清空sp
	 * 
	 * @param context
	 */
	public static void clear(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 得到值
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static String getString(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		String value = pref.getString(key, "");
		return value;
	}
	
	public static int getInt(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		int value = pref.getInt(key, R.drawable.sun);
		return value;
	}

}
