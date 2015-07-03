package com.remind.dao.impl;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.remind.dao.RemindDao;
import com.remind.dao.dbhelper.DBHelper;
import com.remind.dao.msg.RemindMsg;
import com.remind.entity.RemindEntity;

public class RemindDaoImpl implements RemindDao {
	private static final String TAG = "RemindDaoImpl";
	private DBHelper mDBHelper;
	
	public RemindDaoImpl(Context context) {
		mDBHelper = DBHelper.getInstance(context);
	}

	@Override
	public long insertRemind(RemindEntity entity) {
		if (null == entity) {
			return -1;
		}

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(RemindMsg.OWNER_NUM, entity.getOwnerNum());
		values.put(RemindMsg.TARGET_NUM, entity.getTargetNum());
		values.put(RemindMsg.TARGET_NAME, entity.getTargetName());
		values.put(RemindMsg.NICK_NAME, entity.getNickName());
		values.put(RemindMsg.ADD_TIME, entity.getAddTime());
		values.put(RemindMsg.LAST_EDIT_TIME, entity.getLastEditTime());
		values.put(RemindMsg.TITLE, entity.getTitle());
		values.put(RemindMsg.CONTENT, entity.getContent());
		values.put(RemindMsg.LIMIT_TIME, entity.getLimitTime());
		
		values.put(RemindMsg.REMIND_TIME, entity.getRemindTime());
		values.put(RemindMsg.AUDIO_PATH, entity.getAudioPath());
		values.put(RemindMsg.VIDEO_PATH, entity.getVideoPath());
		values.put(RemindMsg.IMG_PATH, entity.getImgPath());
		values.put(RemindMsg.REMIND_METHOD, entity.getRemindMethod());
		values.put(RemindMsg.REMIND_STATE, entity.getRemindState());
		
		values.put(RemindMsg.LAUNCH_STATE, entity.getLaunchState());
		values.put(RemindMsg.IS_DELETE, entity.getIsDelete());
		
//		values.put(RemindMsg.Z1, entity.getZ1());
//		values.put(RemindMsg.Z2, entity.getZ2());
//		values.put(RemindMsg.Z3, entity.getZ3());
		

		return db.insert(RemindMsg.TABLENAME, null, values);
	}

	@Override
	public void insertRemind(List<RemindEntity> entitys) {
		if (null == entitys || entitys.size() <= 0) {
			return;
		}
		
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		for (int i = 0; i < entitys.size(); i++) {
			RemindEntity entity = entitys.get(i);
			ContentValues values = new ContentValues();
			values.put(RemindMsg.OWNER_NUM, entity.getOwnerNum());
			values.put(RemindMsg.TARGET_NUM, entity.getTargetNum());
			values.put(RemindMsg.TARGET_NAME, entity.getTargetName());
			values.put(RemindMsg.NICK_NAME, entity.getNickName());
			values.put(RemindMsg.ADD_TIME, entity.getAddTime());
			values.put(RemindMsg.LAST_EDIT_TIME, entity.getLastEditTime());
			values.put(RemindMsg.CONTENT, entity.getContent());
			values.put(RemindMsg.TITLE, entity.getTitle());
			values.put(RemindMsg.LIMIT_TIME, entity.getLimitTime());
			
			values.put(RemindMsg.REMIND_TIME, entity.getRemindTime());
			values.put(RemindMsg.AUDIO_PATH, entity.getAudioPath());
			values.put(RemindMsg.VIDEO_PATH, entity.getVideoPath());
			values.put(RemindMsg.IMG_PATH, entity.getImgPath());
			values.put(RemindMsg.REMIND_METHOD, entity.getRemindMethod());
			values.put(RemindMsg.REMIND_STATE, entity.getRemindState());
			
			values.put(RemindMsg.LAUNCH_STATE, entity.getLaunchState());
			values.put(RemindMsg.IS_DELETE, entity.getIsDelete());
			
//			values.put(RemindMsg.Z1, entity.getZ1());
//			values.put(RemindMsg.Z2, entity.getZ2());
//			values.put(RemindMsg.Z3, entity.getZ3());
			

			db.insert(RemindMsg.TABLENAME, null, values);
		}
	}

	@Override
	public void deleteById(String id) {
		String sql = "update " + RemindMsg.TABLENAME + " set "
				+ RemindMsg.IS_DELETE + "='" + "1" + "'" + " where "
				+ RemindMsg.ID + "='" + id + "'";
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		Log.d(TAG, sql);
		db.execSQL(sql);
	}

	@Override
	public void updateRemind(RemindEntity entity) {
		if (null == entity) {
			return;
		}

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append("update " + RemindMsg.TABLENAME + " set ");
		
		sb.append(RemindMsg.OWNER_NUM + "	= '" + entity.getOwnerNum() + "',");
		sb.append(RemindMsg.TARGET_NUM + "	= '" + entity.getTargetNum() + "',");
		sb.append(RemindMsg.TARGET_NAME + "	= '" + entity.getTargetName() + "',");
		sb.append(RemindMsg.NICK_NAME + "	= '" + entity.getNickName() + "',");
		sb.append(RemindMsg.ADD_TIME + "	= '" + entity.getAddTime() + "',");
		sb.append(RemindMsg.LAST_EDIT_TIME + "	= '" + entity.getLastEditTime() + "',");
		sb.append(RemindMsg.CONTENT + "	= '" + entity.getContent() + "',");
		sb.append(RemindMsg.TITLE + "	= '" + entity.getTitle() + "',");
		
		sb.append(RemindMsg.LIMIT_TIME + "	= '" + entity.getLimitTime() + "',");
		sb.append(RemindMsg.REMIND_TIME + "	= '" + entity.getRemindTime() + "',");
		sb.append(RemindMsg.AUDIO_PATH + "	= '" + entity.getAudioPath() + "',");
		sb.append(RemindMsg.VIDEO_PATH + "	= '" + entity.getVideoPath() + "',");
		sb.append(RemindMsg.IMG_PATH + "	= '" + entity.getImgPath() + "',");
		sb.append(RemindMsg.REMIND_METHOD + "	= '" + entity.getRemindMethod() + "',");
		sb.append(RemindMsg.REMIND_STATE + "	= '" + entity.getRemindState() + "',");
		sb.append(RemindMsg.LAUNCH_STATE + "	= '" + entity.getLaunchState() + "',");
		sb.append(RemindMsg.IS_DELETE + "	= '" + entity.getIsDelete() + "' ");
		
		sb.append(" where " + RemindMsg.ID + " = '" + entity.getId() + "'");
		String sql = sb.toString();

		Log.d(TAG, sql);
		db.execSQL(sql);
	}

	@Override
	public Cursor queryRemind(RemindEntity entity) {
		Cursor mCursor = null;
		StringBuffer sb = new StringBuffer();
		sb.append("select * from " + RemindMsg.TABLENAME + " where " + RemindMsg.IS_DELETE + " = 0 ");
		if (null != entity.getId() && entity.getId().length() > 0)
			sb.append(" and " + RemindMsg.ID + "='" + entity.getId() + "'");
//		if (entity.getRemindState() >= 0 && entity.getRemindState() <= 3)
//			sb.append(" and " + RemindMsg.REMIND_STATE + "='"
//					+ entity.getRemindState() + "'");
		sb.append("order by " + RemindMsg.REMIND_TIME + " DESC");
		sb.append(";");
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String sql = sb.toString();
		mCursor = db.rawQuery(sql, null);
		return mCursor;
	}

	@Override
	public Cursor queryRemind() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String sql = "select * from " + RemindMsg.TABLENAME + " where "
				+ RemindMsg.IS_DELETE + " = 0 order by " + RemindMsg.LAST_EDIT_TIME;
		Cursor mCursor = null;
		mCursor = db.rawQuery(sql, null);
		return mCursor;
	}

}
