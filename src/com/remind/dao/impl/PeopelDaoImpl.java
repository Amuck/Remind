package com.remind.dao.impl;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.remind.dao.PeopelDao;
import com.remind.dao.dbhelper.DBHelper;
import com.remind.dao.msg.PeopelMsg;
import com.remind.entity.PeopelEntity;

public class PeopelDaoImpl implements PeopelDao {
	private static final String TAG = "PeopelDaoImpl";
	private DBHelper mDBHelper;

	public PeopelDaoImpl(Context context) {
		mDBHelper = DBHelper.getInstance(context);
	}

	@Override
	public void insertPeopel(PeopelEntity entity) {
		if (null == entity) {
			return;
		}

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PeopelMsg.NAME, entity.getName());
		values.put(PeopelMsg.ADDTIME, entity.getAddTime());
		values.put(PeopelMsg.NICKNAME, entity.getNickName());
		values.put(PeopelMsg.NUM, entity.getNum());
		values.put(PeopelMsg.UPDATETIME, entity.getUpdateTime());
		values.put(PeopelMsg.ISDELETE, entity.getIsDelete());
		values.put(PeopelMsg.IMGPATH, entity.getImgPath());
		values.put(PeopelMsg.STATUS, entity.getStatus());

		db.insert(PeopelMsg.TABLENAME, null, values);
	}

	@Override
	public void insertPeopel(List<PeopelEntity> entitys) {
		if (null == entitys || entitys.size() <= 0) {
			return;
		}

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		for (int i = 0; i < entitys.size(); i++) {
			PeopelEntity entity = entitys.get(i);
			ContentValues values = new ContentValues();
			values.put(PeopelMsg.NAME, entity.getName());
			values.put(PeopelMsg.ADDTIME, entity.getAddTime());
			values.put(PeopelMsg.NICKNAME, entity.getNickName());
			values.put(PeopelMsg.NUM, entity.getNum());
			values.put(PeopelMsg.UPDATETIME, entity.getUpdateTime());
			values.put(PeopelMsg.ISDELETE, entity.getIsDelete());
			values.put(PeopelMsg.STATUS, entity.getStatus());
			values.put(PeopelMsg.IMGPATH, entity.getImgPath());

			db.insert(PeopelMsg.TABLENAME, null, values);
		}
	}

	@Override
	public void deletePeopelByNum(String num) {
		String sql = "update " + PeopelMsg.TABLENAME + " set "
				+ PeopelMsg.ISDELETE + "='" + "1" + "'" + " where "
				+ PeopelMsg.NUM + "='" + num + "'";
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		Log.d(TAG, sql);
		db.execSQL(sql);
	}

	@Override
	public void updatePeopel(PeopelEntity entity) {
		if (null == entity) {
			return;
		}

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append("update " + PeopelMsg.TABLENAME + " set ");
		sb.append(PeopelMsg.NAME + "	= '" + entity.getName() + "',");
		sb.append(PeopelMsg.ADDTIME + "	= '" + entity.getAddTime() + "',");
		sb.append(PeopelMsg.NICKNAME + "	= '" + entity.getNickName() + "',");
		sb.append(PeopelMsg.IMGPATH + "	= '" + entity.getImgPath() + "',");
		sb.append(PeopelMsg.UPDATETIME + "	= '" + entity.getUpdateTime() + "',");
		sb.append(PeopelMsg.ISDELETE + "	= '" + entity.getIsDelete() + "',");
		sb.append(PeopelMsg.STATUS + "	= '" + entity.getStatus() + "' ");
		sb.append(" where " + PeopelMsg.NUM + " = '" + entity.getNum() + "'");
		String sql = sb.toString();

		Log.d(TAG, sql);
		db.execSQL(sql);
	}

	@Override
	public Cursor queryPeopel() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String sql = "select * from " + PeopelMsg.TABLENAME + " where "
				+ PeopelMsg.ISDELETE + " = 0 " ;
//				+ " order by " + PeopelMsg.UPDATETIME + " DESC";
		Cursor mCursor = null;
		mCursor = db.rawQuery(sql, null);
		return mCursor;
	}

	@Override
	public Cursor queryPeopelByNum(String num) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String sql = "select * from " + PeopelMsg.TABLENAME + " where "
				+ PeopelMsg.ISDELETE + " = 0 and " + PeopelMsg.NUM + " = '" + num + "'";
		Cursor mCursor = null;
		mCursor = db.rawQuery(sql, null);
		return mCursor;
	}

	@Override
	public String getImgPath(String num) {
		String imgPath = "";
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String sql = "select " + PeopelMsg.IMGPATH +" from " + PeopelMsg.TABLENAME + " where "
				+ PeopelMsg.ISDELETE + " = 0 and " + PeopelMsg.NUM + " = '" + num + "'";
		Cursor mCursor = null;
		mCursor = db.rawQuery(sql, null);
		if (mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			imgPath = mCursor.getString(mCursor.getColumnIndex(PeopelMsg.IMGPATH));
		}
		mCursor.close();
		return imgPath;
	}

}