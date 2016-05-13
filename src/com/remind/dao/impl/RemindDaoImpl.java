package com.remind.dao.impl;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.remind.dao.RemindDao;
import com.remind.dao.dbhelper.DBHelper;
import com.remind.dao.msg.PeopelMsg;
import com.remind.dao.msg.RemindMsg;
import com.remind.entity.RemindEntity;
import com.remind.global.AppConstant;

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
		values.put(RemindMsg.NOTICE_ID, entity.getNoticeId());
		values.put(RemindMsg.OWNER_ID, entity.getOwnerId());
		values.put(RemindMsg.NICK_NAME, entity.getNickName());
		values.put(RemindMsg.ADD_TIME, entity.getAddTime());
		values.put(RemindMsg.REMIND_TIME_MILI, entity.getRemindTimeMiLi());
		values.put(RemindMsg.TITLE, entity.getTitle());
		values.put(RemindMsg.CONTENT, entity.getContent());
		values.put(RemindMsg.LIMIT_TIME, entity.getLimitTime());
		
		values.put(RemindMsg.REMIND_TIME, entity.getRemindTime());
		values.put(RemindMsg.AUDIO_PATH, entity.getAudioPath());
		values.put(RemindMsg.VIDEO_PATH, entity.getVideoPath());
		values.put(RemindMsg.IMG_PATH, entity.getImgPath());
		values.put(RemindMsg.REMIND_METHOD, entity.getRemindMethod());
		values.put(RemindMsg.REMIND_STATE, entity.getRemindState());
		
		values.put(RemindMsg.REPEAT_TYPE, entity.getRepeatType());
		
		values.put(RemindMsg.LAUNCH_STATE, entity.getLaunchState());
		values.put(RemindMsg.IS_DELETE, entity.getIsDelete());
		
		values.put(RemindMsg.IS_PRIVIEW, entity.getIsPreview());
		values.put(RemindMsg.REMIND_COUNT, entity.getRemindCount());
		values.put(RemindMsg.IS_READ, entity.getIsRead());
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
			values.put(RemindMsg.NOTICE_ID, entity.getNoticeId());
			values.put(RemindMsg.OWNER_ID, entity.getOwnerId());
			values.put(RemindMsg.NICK_NAME, entity.getNickName());
			values.put(RemindMsg.ADD_TIME, entity.getAddTime());
			values.put(RemindMsg.REMIND_TIME_MILI, entity.getRemindTimeMiLi());
			values.put(RemindMsg.CONTENT, entity.getContent());
			values.put(RemindMsg.TITLE, entity.getTitle());
			values.put(RemindMsg.LIMIT_TIME, entity.getLimitTime());
			
			values.put(RemindMsg.REMIND_TIME, entity.getRemindTime());
			values.put(RemindMsg.AUDIO_PATH, entity.getAudioPath());
			values.put(RemindMsg.VIDEO_PATH, entity.getVideoPath());
			values.put(RemindMsg.IMG_PATH, entity.getImgPath());
			values.put(RemindMsg.REMIND_METHOD, entity.getRemindMethod());
			values.put(RemindMsg.REMIND_STATE, entity.getRemindState());
			
			values.put(RemindMsg.REPEAT_TYPE, entity.getRepeatType());
			
			values.put(RemindMsg.LAUNCH_STATE, entity.getLaunchState());
			values.put(RemindMsg.IS_DELETE, entity.getIsDelete());
			
			values.put(RemindMsg.IS_PRIVIEW, entity.getIsPreview());
			values.put(RemindMsg.REMIND_COUNT, entity.getRemindCount());
			values.put(RemindMsg.IS_READ, entity.getIsRead());
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
		sb.append(RemindMsg.NOTICE_ID + "	= '" + entity.getNoticeId() + "',");
		sb.append(RemindMsg.OWNER_ID + "	= '" + entity.getOwnerId() + "',");
		sb.append(RemindMsg.ADD_TIME + "	= '" + entity.getAddTime() + "',");
		sb.append(RemindMsg.REMIND_TIME_MILI + "	= '" + entity.getRemindTimeMiLi() + "',");
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
		
		sb.append(RemindMsg.REPEAT_TYPE + "	= '" + entity.getRepeatType() + "',");
		
		sb.append(RemindMsg.IS_PRIVIEW + "	= '" + entity.getIsPreview() + "',");
		sb.append(RemindMsg.REMIND_COUNT + "	= '" + entity.getRemindCount() + "',");
		sb.append(RemindMsg.IS_READ + "	= '" + entity.getIsRead() + "',");
		
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
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
		
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
	public Cursor queryRemindInChat(RemindEntity entity) {
		Cursor mCursor = null;
		StringBuffer sb = new StringBuffer();
		sb.append("select * from " + RemindMsg.TABLENAME + " where 1 = 1 ");
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
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
				+ RemindMsg.IS_DELETE + " = 0 " +  " and " + RemindMsg.OWNER_NUM + " = '"
				+ AppConstant.USER_NUM + "' " + " order by " + RemindMsg.REMIND_TIME_MILI;
		Cursor mCursor = null;
		mCursor = db.rawQuery(sql, null);
		return mCursor;
	}

	/**
	 * 查询所有提醒,提醒时间当天时间排在最前面，其他按提醒时间排序
	 * @param today				当天时间
	 * @param startPosition		开始位置
	 * @param selectedCount		选择数量
	 * @return
	 */
	@Override
	public Cursor queryForMain(String today, int startPosition, int selectedCount) {
//		select * from 
//		( select * from Remind 
//		where remindTime like '2015-11-12%' 
//		 and isDelete <>  1 
//		order by remindTime desc ) a 
//		union all select * from 
//		(select * from Remind 
//		where id not in 
//		( select Remind.id from Remind 
//		where remindTime like '2015-11-12%')
//		 and isDelete <>  1 
//		order by remindTime desc ) b 
//		limit 0,20
		Cursor mCursor = null;
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from ");
		sb.append(" (select * from " + RemindMsg.TABLENAME);
		sb.append(" where " + RemindMsg.REMIND_TIME + " like '" + today + "%' ");
		sb.append(" and " + RemindMsg.IS_DELETE + " =  0 ");
		sb.append(" order by  " + RemindMsg.REMIND_TIME_MILI + " desc ) a ");
		sb.append(" union all select * from ");
		sb.append(" (select * from " + RemindMsg.TABLENAME);
		sb.append(" where " + RemindMsg.ID + " not in ");
		sb.append(" ( select " + RemindMsg.TABLENAME + "." + RemindMsg.ID + " from " + RemindMsg.TABLENAME);
		sb.append(" where " + RemindMsg.REMIND_TIME + " like '" + today + "%') ");
		sb.append(" and " + RemindMsg.IS_DELETE + " =  0 ");
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
		sb.append(" order by  " + RemindMsg.REMIND_TIME_MILI + " desc ) b ");
		sb.append(" limit " + startPosition + "," + selectedCount);
		sb.append(";");
		String sql = sb.toString();
		mCursor = db.rawQuery(sql, null);
		return mCursor;
	}
	
	@Override
	public int getEffectiveCount() {
//		select count(id) from Remind where  isDelete =  0 and remindState = 0 ;
		int count = 0;
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append(" select count( ");
		sb.append(RemindMsg.ID);
		sb.append(" ) from  ");
		sb.append(RemindMsg.TABLENAME);
		sb.append(" where  ");
		sb.append(RemindMsg.IS_DELETE);
		sb.append("  =  0  ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_STATE);
		sb.append("  =  0  ");
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
		sb.append(";");
		String sql = sb.toString();
		Cursor mCursor = db.rawQuery(sql, null);
		mCursor.moveToFirst();
		count = mCursor.getInt(0);
		mCursor.close();
		return count;
	}
	
	@Override
	public int getUnAcceptCount() {
		int count = 0;
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append(" select count( ");
		sb.append(RemindMsg.ID);
		sb.append(" ) from  ");
		sb.append(RemindMsg.TABLENAME);
		sb.append(" where  ");
		sb.append(RemindMsg.IS_DELETE);
		sb.append("  =  0  ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_STATE);
		sb.append("  =  1  ");
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
		sb.append(" order by  " + RemindMsg.ADD_TIME + " desc ");
		sb.append(";");
		String sql = sb.toString();
		Cursor mCursor = db.rawQuery(sql, null);
		mCursor.moveToFirst();
		count = mCursor.getInt(0);
		mCursor.close();
		return count;
	}
	
	@Override
	public Cursor getUnAcceptRemind(int startPosition, int selectedCount) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from ");
		sb.append(RemindMsg.TABLENAME);
		sb.append(" where  ");
		sb.append(RemindMsg.IS_DELETE);
		sb.append("  =  0  ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_STATE);
		sb.append("  =  1  ");
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
		sb.append(" order by  " + RemindMsg.ADD_TIME + " desc ");
		sb.append(" limit " + startPosition + "," + selectedCount);
		sb.append(";");
		String sql = sb.toString();
		Cursor mCursor = db.rawQuery(sql, null);
		return mCursor;
	}
	
	@Override
	public Cursor getUnAcceptRemind() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from ");
		sb.append(RemindMsg.TABLENAME);
		sb.append(" where  ");
		sb.append(RemindMsg.IS_DELETE);
		sb.append("  =  0  ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_STATE);
		sb.append("  =  1  ");
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
		sb.append(" order by  " + RemindMsg.ADD_TIME + " desc ");
		sb.append(";");
		String sql = sb.toString();
		Cursor mCursor = db.rawQuery(sql, null);
		return mCursor;
	}
	
	@Override
	public Cursor getUnReadRemind() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from ");
		sb.append(RemindMsg.TABLENAME);
		sb.append(" where  ");
		sb.append(RemindMsg.IS_DELETE);
		sb.append("  =  0  ");
		sb.append(" and  ");
		sb.append(RemindMsg.IS_READ);
		sb.append("  =  0  ");
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
		sb.append(" order by  " + RemindMsg.ADD_TIME + " desc ");
		sb.append(";");
		String sql = sb.toString();
		Cursor mCursor = db.rawQuery(sql, null);
		return mCursor;
	}
	
	@Override
	public int getTodayCount(String today) {
		int count = 0;
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append(" select count( ");
		sb.append(RemindMsg.ID);
		sb.append(" ) from  ");
		sb.append(RemindMsg.TABLENAME);
		sb.append(" where  ");
		sb.append(RemindMsg.IS_DELETE);
		sb.append("  =  0  ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_STATE);
		sb.append("  =  0  ");
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_TIME);
		sb.append(" like '" + today + "%' ");
		sb.append(" order by  " + RemindMsg.REMIND_TIME + " desc ");
		sb.append(";");
		String sql = sb.toString();
		Cursor mCursor = db.rawQuery(sql, null);
		mCursor.moveToFirst();
		count = mCursor.getInt(0);
		mCursor.close();
		return count;
	}
	
	@Override
	public Cursor getTodayRemind(String today, int startPosition, int selectedCount) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from  ");
		sb.append(RemindMsg.TABLENAME);
		sb.append(" where  ");
		sb.append(RemindMsg.IS_DELETE);
		sb.append("  =  0  ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_STATE);
		sb.append("  =  0  ");
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_TIME);
		sb.append(" like '" + today + "%' ");
		sb.append(" order by  " + RemindMsg.REMIND_TIME + " desc ");
		sb.append(" limit " + startPosition + "," + selectedCount);
		sb.append(";");
		String sql = sb.toString();
		Cursor mCursor = db.rawQuery(sql, null);
		return mCursor;
	}
	
	@Override
	public Cursor getTodayRemind(String today) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from  ");
		sb.append(RemindMsg.TABLENAME);
		sb.append(" where  ");
		sb.append(RemindMsg.IS_DELETE);
		sb.append("  =  0  ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_STATE);
		sb.append("  =  0  ");
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_TIME);
		sb.append(" like '" + today + "%' ");
		sb.append(" order by  " + RemindMsg.REMIND_TIME + " desc ");
		sb.append(";");
		String sql = sb.toString();
		Cursor mCursor = db.rawQuery(sql, null);
		return mCursor;
	}
	
	@Override
	public Cursor getTodayReadRemind(String today) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from  ");
		sb.append(RemindMsg.TABLENAME);
		sb.append(" where  ");
		sb.append(RemindMsg.IS_DELETE);
		sb.append("  =  0  ");
		sb.append(" and  ");
		sb.append(RemindMsg.IS_READ);
		sb.append("  =  1  ");
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_TIME);
		sb.append(" like '" + today + "%' ");
		sb.append(" order by  " + RemindMsg.REMIND_TIME + " desc ");
		sb.append(";");
		String sql = sb.toString();
		Cursor mCursor = db.rawQuery(sql, null);
		return mCursor;
	}
	
	@Override
	public int getOtherdayCount(String today) {
		int count = 0;
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append(" select count( ");
		sb.append(RemindMsg.ID);
		sb.append(" ) from  ");
		sb.append(RemindMsg.TABLENAME);
		sb.append(" where  ");
		sb.append(RemindMsg.IS_DELETE);
		sb.append("  =  0  ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_STATE);
		sb.append("  =  0  ");
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_TIME);
		sb.append(" not like '" + today + "%' ");
		sb.append(" order by  " + RemindMsg.REMIND_TIME + " desc ");
		sb.append(";");
		String sql = sb.toString();
		Cursor mCursor = db.rawQuery(sql, null);
		mCursor.moveToFirst();
		count = mCursor.getInt(0);
		mCursor.close();
		return count;
	}
	
	@Override
	public Cursor getOtherdayRemind(String today, int startPosition, int selectedCount) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from  ");
		sb.append(RemindMsg.TABLENAME);
		sb.append(" where  ");
		sb.append(RemindMsg.IS_DELETE);
		sb.append("  =  0  ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_STATE);
		sb.append("  =  0  ");
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_TIME);
		sb.append(" not like '" + today + "%' ");
		sb.append(" order by  " + RemindMsg.REMIND_TIME + " desc ");
		sb.append(" limit " + startPosition + "," + selectedCount);
		sb.append(";");
		String sql = sb.toString();
		Cursor mCursor = db.rawQuery(sql, null);
		return mCursor;
	}
	
	@Override
	public Cursor getOtherdayRemind(String today) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from  ");
		sb.append(RemindMsg.TABLENAME);
		sb.append(" where  ");
		sb.append(RemindMsg.IS_DELETE);
		sb.append("  =  0  ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_STATE);
		sb.append("  =  0  ");
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_TIME);
		sb.append(" not like '" + today + "%' ");
		sb.append(" order by  " + RemindMsg.REMIND_TIME + " desc ");
		sb.append(";");
		String sql = sb.toString();
		Cursor mCursor = db.rawQuery(sql, null);
		return mCursor;
	}

	@Override
	public Cursor getOtherdayReadRemind(String today) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from  ");
		sb.append(RemindMsg.TABLENAME);
		sb.append(" where  ");
		sb.append(RemindMsg.IS_DELETE);
		sb.append("  =  0  ");
		sb.append(" and  ");
		sb.append(RemindMsg.IS_READ);
		sb.append("  =  1  ");
		sb.append( " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ");
		sb.append(" and  ");
		sb.append(RemindMsg.REMIND_TIME);
		sb.append(" not like '" + today + "%' ");
		sb.append(" order by  " + RemindMsg.REMIND_TIME + " desc ");
		sb.append(";");
		String sql = sb.toString();
		Cursor mCursor = db.rawQuery(sql, null);
		return mCursor;
	}
	
	@Override
	public void updateByNoticeId(String noticeId, int state) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append("update " + RemindMsg.TABLENAME + " set ");
		sb.append(RemindMsg.REMIND_STATE + "	= '" + state + "' ");
		sb.append(" where " + RemindMsg.NOTICE_ID + " = '" + noticeId + "'");
		String sql = sb.toString();

		Log.d(TAG, sql);
		db.execSQL(sql);
	}
	
	@Override
	public void updateReadState(String id, int state) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		sb.append("update " + RemindMsg.TABLENAME + " set ");
		sb.append(RemindMsg.IS_READ + "	= '" + state + "' ");
		sb.append(" where " + RemindMsg.ID + " = '" + id + "'");
		String sql = sb.toString();

		Log.d(TAG, sql);
		db.execSQL(sql);
	}

	@Override
	public Cursor queryRemindByNoticeId(String noticeId) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String sql = "select * from " + RemindMsg.TABLENAME + " where "
				+ RemindMsg.IS_DELETE + " = 0 and " + RemindMsg.NOTICE_ID + " = '" + noticeId + "'"
				+ " and " + RemindMsg.OWNER_NUM + " = '" + AppConstant.USER_NUM + "' ";
		Cursor mCursor = null;
		mCursor = db.rawQuery(sql, null);
		return mCursor;
	}
}
