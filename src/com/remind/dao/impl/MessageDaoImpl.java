package com.remind.dao.impl;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.remind.dao.MessageDao;
import com.remind.dao.dbhelper.DBHelper;
import com.remind.dao.msg.MessageMsg;
import com.remind.entity.MessageEntity;
import com.remind.util.DataBaseParser;

public class MessageDaoImpl implements MessageDao {
	private static final String TAG = "MessageDaoImpl";
	private DBHelper mDBHelper;

	public MessageDaoImpl(Context context) {
		mDBHelper = DBHelper.getInstance(context);
	}

	@Override
	public synchronized long insert(MessageEntity entity) {
		if (null == entity) {
			return 0l;
		}

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(MessageMsg.MSG_INDEX, entity.getMessageIndex());
		values.put(MessageMsg.TIME, entity.getTime());
		values.put(MessageMsg.CONTENT, entity.getContent());

		values.put(MessageMsg.SEND_NAME, entity.getSendName());
		values.put(MessageMsg.SEND_NUM, entity.getSendNum());
		values.put(MessageMsg.IS_COMING, entity.getIsComing());

		values.put(MessageMsg.RECIEVE_NAME, entity.getRecieveName());
		values.put(MessageMsg.RECIEVE_NUM, entity.getRecieveNum());
		values.put(MessageMsg.ISDELETE, entity.getIsDelete());
		values.put(MessageMsg.SEND_STATE, entity.getSendState());

		values.put(MessageMsg.MSG_TYPE, entity.getMsgType());
		values.put(MessageMsg.MSG_PATH, entity.getMsgPath());
		values.put(MessageMsg.OTHER_TYPE_ID, entity.getOtherTypeId());
		values.put(MessageMsg.IS_FEED, entity.getFeed());

		return db.insert(MessageMsg.TABLENAME, null, values);
	}

	@Override
	public synchronized void delete(String id) {
		String sql = "update " + MessageMsg.TABLENAME + " set "
				+ MessageMsg.ISDELETE + "='" + "1" + "'" + " where "
				+ MessageMsg.ID + "='" + id + "'";
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		Log.d(TAG, sql);
		db.execSQL(sql);
	}

	@Override
	public synchronized void updateSendState(long msgId, String state) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append("update " + MessageMsg.TABLENAME + " set ");
		sb.append(MessageMsg.SEND_STATE + "	= '" + state + "' ");
		sb.append(" where " + MessageMsg.ID + " = '" + msgId + "'");
		String sql = sb.toString();

		Log.d(TAG, sql);
		db.execSQL(sql);
	}
	
	@Override
	public synchronized void update(MessageEntity entity) {
		if (null == entity) {
			return;
		}

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append("update " + MessageMsg.TABLENAME + " set ");
		sb.append(MessageMsg.MSG_INDEX + "	= '" + entity.getMessageIndex()
				+ "',");
		sb.append(MessageMsg.TIME + "	= '" + entity.getTime() + "',");
		sb.append(MessageMsg.CONTENT + "	= '" + entity.getContent() + "',");

		sb.append(MessageMsg.MSG_TYPE + "	= '" + entity.getMsgType() + "',");
		sb.append(MessageMsg.MSG_PATH + "	= '" + entity.getMsgPath() + "',");
		sb.append(MessageMsg.OTHER_TYPE_ID + "	= '" + entity.getOtherTypeId()
				+ "',");

		sb.append(MessageMsg.SEND_NAME + "	= '" + entity.getSendName() + "',");
		sb.append(MessageMsg.SEND_NUM + "	= '" + entity.getSendNum() + "',");
		sb.append(MessageMsg.IS_COMING + "	= '" + entity.getIsComing() + "',");
		sb.append(MessageMsg.IS_FEED + "	= '" + entity.getFeed() + "',");

		sb.append(MessageMsg.RECIEVE_NAME + "	= '" + entity.getRecieveName()
				+ "',");
		sb.append(MessageMsg.RECIEVE_NUM + "	= '" + entity.getRecieveNum()
				+ "',");
		sb.append(MessageMsg.ISDELETE + "	= '" + entity.getIsDelete() + "',");
		sb.append(MessageMsg.SEND_STATE + "	= '" + entity.getSendState() + "' ");
		sb.append(" where " + MessageMsg.ID + " = '" + entity.getId() + "'");
		String sql = sb.toString();

		Log.d(TAG, sql);
		db.execSQL(sql);
	}

	@Override
	public synchronized Cursor query(String recieveNum) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String sql = "select * from " + MessageMsg.TABLENAME + " where "
				+ MessageMsg.ISDELETE + " = 0 and " + MessageMsg.MSG_INDEX
				+ " = " + recieveNum + " order by " + MessageMsg.TIME + " asc ";
		Cursor mCursor = null;
		mCursor = db.rawQuery(sql, null);
		return mCursor;
	}
	
	@Override
	public synchronized Cursor queryByOtherTypeId(String id) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String sql = "select * from " + MessageMsg.TABLENAME + " where "
				 + MessageMsg.OTHER_TYPE_ID
				+ " = " + id + " order by " + MessageMsg.TIME + " asc ";
		Cursor mCursor = null;
		mCursor = db.rawQuery(sql, null);
		return mCursor;
	}

	@Override
	public int getCount(String recieveNum) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String sql = "select count(*) from " + MessageMsg.TABLENAME + " where "
				+ MessageMsg.ISDELETE + " = 0 and " + MessageMsg.MSG_INDEX
				+ " = " + recieveNum;
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int length = c.getInt(0);
        c.close();
        return length;
	}

	@Override
	public synchronized ArrayList<MessageEntity> getMsgByPage(int currentPage, int pageSize, String recieveNum) {
		int firstResult = (currentPage - 1) * pageSize;
//        int maxResult = currentPage * pageSize;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String sql = "select * from " + MessageMsg.TABLENAME + " where "
				+ MessageMsg.ISDELETE + " = 0 and " + MessageMsg.MSG_INDEX
				+ " = " + recieveNum  + " order by " + MessageMsg.ID + " desc limit ?,? ";
        Cursor mCursor = db.rawQuery(
                sql,
                new String[] { String.valueOf(firstResult),
                        String.valueOf(pageSize) });
        //不要关闭数据库
//        int columnCount  = mCursor.getColumnCount();
        ArrayList<MessageEntity> result = DataBaseParser.getMessage(mCursor);
        mCursor.close();
        return result;
	}

}
