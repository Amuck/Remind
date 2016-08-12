package com.remind.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 登录方式 0为远程登录 1为本地登录
 * 
 * @author Administrator
 * 
 */
public class MySharedPreferencesLoginType {
    private static final String PREFERENCES_NAME = "com.remind.userInfo.data";
    public static final String USERNAME = "userName";
    public static final String PASSWORD = "password";
    public static final String MACADDRESS = "macAddress";
    public static final String DELETE_TIME = "delete_time";
    /**
     * 用户是否在线
     */
    public static final String IS_ONLINE = "isOnline";
    /**
     * 用户登陆的id
     */
    public static final String FROM_ID = "from_id";
    /**
     * 是否记住用户名
     */
    public static final String IS_REMEMBER = "is_remember";

    /**
     * 登陆用户id
     * 
     * @param context
     * @param id
     */
    public static void saveFromId(Context context, String id) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.putString(FROM_ID, id);

        editor.commit();
    }

    /**
     * 获得登陆用户id
     * 
     * @param context
     * @return
     */
    public static String getFromId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        String value = pref.getString(FROM_ID, "");
        return value;
    }

    /**
     * 保存删除日期
     * 
     * @param context
     * @param id
     */
    public static void saveDeleteTime(Context context, String deleteTime) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.putString(DELETE_TIME, deleteTime);

        editor.commit();
    }

    /**
     * 获取删除日期
     * 
     * @param context
     * @return
     */
    public static String getDeleteTime(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        String value = pref.getString(DELETE_TIME, "");
        return value;
    }

    /**
     * 改变用户在线状态
     * 
     * @param context
     * @param isOnline
     *            true: 在线； false：离线
     */
    public static void setOnlineState(Context context, boolean isOnline) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.putBoolean(IS_ONLINE, isOnline);

        editor.commit();
    }

    /**
     * @param context
     * @return 用户是否在线
     */
    public static boolean isOnline(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        boolean value = pref.getBoolean(IS_ONLINE, false);
        return value;
    }

    /**
     * 是否记住用户名
     * 
     * @param context
     * @param isRemember
     */
    public static void setIsRemember(Context context, boolean isRemember) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.putBoolean(IS_REMEMBER, isRemember);

        editor.commit();
    }

    /**
     * @param context
     * @return 是否记住用户名
     */
    public static boolean isRemember(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        boolean value = pref.getBoolean(IS_REMEMBER, false);
        return value;
    }

    /**
     * 保存用户名
     * 
     * @param context
     * @param userName
     * @param macAddress
     */
    public static void saveUserName(Context context, String userName, String password) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.putString(USERNAME, userName);
        editor.putString(PASSWORD, password);
        // editor.putString(MACADDRESS, macAddress);
        editor.commit();
    }

    /**
     * 清空sp
     * 
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
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
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        String value = pref.getString(key, "");
        return value;
    }

}
