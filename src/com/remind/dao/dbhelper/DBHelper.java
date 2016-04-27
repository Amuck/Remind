package com.remind.dao.dbhelper;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.remind.dao.msg.MessageIndexMsg;
import com.remind.dao.msg.MessageMsg;
import com.remind.dao.msg.PeopelMsg;
import com.remind.dao.msg.RemindMsg;
import com.remind.global.AppConstant;

public class DBHelper extends SQLiteOpenHelper {
	/**
	 * 数据库版本号
	 */
	public static final int DB_FILE_VERSION = 3;

	private static final String TAG = "DBHelper";

	private static DBHelper instance = null;
	
	private DBHelper(Context context) {
		super(context, AppConstant.MNT + AppConstant.FILE_PATH + 
				AppConstant.DB_FILE_PATH + File.separator + 
				AppConstant.DB_FILE_NAME, null,
				DBHelper.DB_FILE_VERSION);
	}
	
	/**
	 * 获取数据库帮助类实例
	 * @param context
	 * @return
	 */
	public static DBHelper getInstance(Context context) {
		if (null == instance) {
			instance = new DBHelper(context);
		}
		
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer peopelSql = new StringBuffer();
		peopelSql.append("CREATE TABLE ");
		peopelSql.append(PeopelMsg.TABLENAME);
		peopelSql.append(" (");
		peopelSql.append(PeopelMsg.ID);
		peopelSql.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");// 长度为13的主键
		peopelSql.append(PeopelMsg.NAME);
		peopelSql.append(" text,");
		peopelSql.append(PeopelMsg.NICKNAME);
		peopelSql.append(" text,");
		peopelSql.append(PeopelMsg.NUM);
		peopelSql.append(" text,");
		peopelSql.append(PeopelMsg.LOGIN_USER);
		peopelSql.append(" text,");
		peopelSql.append(PeopelMsg.IMGPATH);
		peopelSql.append(" text,");
		peopelSql.append(PeopelMsg.FRIEND_ID);
		peopelSql.append(" text,");
		peopelSql.append(PeopelMsg.ADDTIME);
		peopelSql.append(" varchar(30),");
		peopelSql.append(PeopelMsg.UPDATETIME);
		peopelSql.append(" varchar(30),");
		
		peopelSql.append(PeopelMsg.STATUS);
		peopelSql.append(" varchar(2),");
		peopelSql.append(PeopelMsg.ISDELETE);
		peopelSql.append(" varchar(2),");
		
		peopelSql.append(PeopelMsg.Z1);
		peopelSql.append(" varchar(100),");
		peopelSql.append(PeopelMsg.Z2);
		peopelSql.append(" varchar(100),");
		peopelSql.append(PeopelMsg.Z3);
		peopelSql.append(" varchar(100)");
		peopelSql.append(");");
		
		Log.i(TAG, peopelSql.toString());
		try {
			db.execSQL(peopelSql.toString());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
		StringBuffer remindSql = new StringBuffer();
		remindSql.append("CREATE TABLE ");
		remindSql.append(RemindMsg.TABLENAME);
		remindSql.append(" (");
		remindSql.append(RemindMsg.ID);
		remindSql.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");// 长度为13的主键
		remindSql.append(RemindMsg.OWNER_NUM);
		remindSql.append(" text,");
		remindSql.append(RemindMsg.TARGET_NUM);
		remindSql.append(" text,");
		remindSql.append(RemindMsg.NOTICE_ID);
		remindSql.append(" text,");
		remindSql.append(RemindMsg.OWNER_ID);
		remindSql.append(" text,");
		remindSql.append(RemindMsg.TARGET_NAME);
		remindSql.append(" text,");
		remindSql.append(RemindMsg.NICK_NAME);
		remindSql.append(" text,");
		remindSql.append(RemindMsg.TITLE);
		remindSql.append(" text,");
		remindSql.append(RemindMsg.CONTENT);
		remindSql.append(" text,");
		remindSql.append(RemindMsg.AUDIO_PATH);
		remindSql.append(" text,");
		remindSql.append(RemindMsg.VIDEO_PATH);
		remindSql.append(" text,");
		remindSql.append(RemindMsg.IMG_PATH);
		remindSql.append(" text,");
		remindSql.append(RemindMsg.REPEAT_TYPE);
		remindSql.append(" text,");
		remindSql.append(RemindMsg.ADD_TIME);
		remindSql.append(" varchar(30),");
		remindSql.append(RemindMsg.REMIND_TIME_MILI);
		remindSql.append(" varchar(30),");
		remindSql.append(RemindMsg.LIMIT_TIME);
		remindSql.append(" varchar(30),");
		remindSql.append(RemindMsg.REMIND_TIME);
		remindSql.append(" varchar(30),");
		
		remindSql.append(RemindMsg.REMIND_METHOD);
		remindSql.append(" varchar(2),");
		remindSql.append(RemindMsg.REMIND_STATE);
		remindSql.append(" varchar(2),");
		remindSql.append(RemindMsg.LAUNCH_STATE);
		remindSql.append(" varchar(2),");
		remindSql.append(RemindMsg.IS_DELETE);
		remindSql.append(" varchar(2),");
		
		remindSql.append(RemindMsg.IS_PRIVIEW);
		remindSql.append(" varchar(2),");
		remindSql.append(RemindMsg.REMIND_COUNT);
		remindSql.append(" varchar(2),");
		
		remindSql.append(RemindMsg.Z1);
		remindSql.append(" varchar(100),");
		remindSql.append(RemindMsg.Z2);
		remindSql.append(" varchar(100),");
		remindSql.append(RemindMsg.Z3);
		remindSql.append(" varchar(100)");
		remindSql.append(");");
		
		Log.i(TAG, remindSql.toString());
		try {
			db.execSQL(remindSql.toString());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
		StringBuffer messageIndexSql = new StringBuffer();
		messageIndexSql.append("CREATE TABLE ");
		messageIndexSql.append(MessageIndexMsg.TABLENAME);
		messageIndexSql.append(" (");
		messageIndexSql.append(MessageIndexMsg.ID);
		messageIndexSql.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");// 长度为13的主键
		messageIndexSql.append(MessageIndexMsg.NUM);
		messageIndexSql.append(" text,");
		messageIndexSql.append(MessageIndexMsg.LOGIN_USER);
		messageIndexSql.append(" text,");
		messageIndexSql.append(MessageIndexMsg.MESSAGE);
		messageIndexSql.append(" text,");
		messageIndexSql.append(MessageIndexMsg.TIME);
		messageIndexSql.append(" text,");
		messageIndexSql.append(MessageIndexMsg.NAME);
		messageIndexSql.append(" text,");
		messageIndexSql.append(MessageIndexMsg.IMG_PATH);
		messageIndexSql.append(" text,");
		messageIndexSql.append(MessageIndexMsg.UNREAND_COUNT);
		messageIndexSql.append(" integer,");
		messageIndexSql.append(MessageIndexMsg.SEND_STATE);
		messageIndexSql.append(" varchar(2),");
		messageIndexSql.append(MessageIndexMsg.ISDELETE);
		messageIndexSql.append(" varchar(2),");
		messageIndexSql.append(MessageIndexMsg.Z1);
		messageIndexSql.append(" varchar(100),");
		messageIndexSql.append(MessageIndexMsg.Z2);
		messageIndexSql.append(" varchar(100),");
		messageIndexSql.append(MessageIndexMsg.Z3);
		messageIndexSql.append(" varchar(100)");
		messageIndexSql.append(");");
		
		Log.i(TAG, messageIndexSql.toString());
		try {
			db.execSQL(messageIndexSql.toString());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
		StringBuffer messageSql = new StringBuffer();
		messageSql.append("CREATE TABLE ");
		messageSql.append(MessageMsg.TABLENAME);
		messageSql.append(" (");
		messageSql.append(MessageMsg.ID);
		messageSql.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");// 长度为13的主键
		messageSql.append(MessageMsg.MSG_INDEX);
		messageSql.append(" text,");
		messageSql.append(MessageMsg.RECIEVE_NAME);
		messageSql.append(" text,");
		messageSql.append(MessageMsg.RECIEVE_NUM);
		messageSql.append(" text,");
		messageSql.append(MessageMsg.LOGIN_USER);
		messageSql.append(" text,");
		messageSql.append(MessageMsg.TIME);
		messageSql.append(" text,");
		messageSql.append(MessageMsg.CONTENT);
		messageSql.append(" text,");
		messageSql.append(MessageMsg.SEND_NAME);
		messageSql.append(" text,");
		messageSql.append(MessageMsg.SEND_NUM);
		messageSql.append(" text,");
		messageSql.append(MessageMsg.IS_COMING);
		messageSql.append(" varchar(2),");
		messageSql.append(MessageMsg.IS_FEED);
		messageSql.append(" varchar(2),");
		messageSql.append(MessageMsg.MSG_TYPE);
		messageSql.append(" varchar(2),");
		messageSql.append(MessageMsg.OTHER_TYPE_ID);
		messageSql.append(" varchar(2),");
		messageSql.append(MessageMsg.MSG_PATH);
		messageSql.append(" varchar(2),");
		messageSql.append(MessageMsg.SEND_STATE);
		messageSql.append(" varchar(2),");
		messageSql.append(MessageMsg.ISDELETE);
		messageSql.append(" varchar(2),");
		messageSql.append(MessageMsg.Z1);
		messageSql.append(" varchar(100),");
		messageSql.append(MessageMsg.Z2);
		messageSql.append(" varchar(100),");
		messageSql.append(MessageMsg.Z3);
		messageSql.append(" varchar(100)");
		messageSql.append(");");
		
		Log.i(TAG, messageSql.toString());
		try {
			db.execSQL(messageSql.toString());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + PeopelMsg.TABLENAME);
		db.execSQL("DROP TABLE IF EXISTS " + MessageIndexMsg.TABLENAME);
		db.execSQL("DROP TABLE IF EXISTS " + MessageMsg.TABLENAME);
		db.execSQL("DROP TABLE IF EXISTS " + RemindMsg.TABLENAME);
		onCreate(db);
	}

}
