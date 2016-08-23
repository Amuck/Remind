package com.remind.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;

import com.remind.dao.MessageIndexDao;
import com.remind.dao.dbhelper.DBHelper;
import com.remind.dao.msg.MessageIndexMsg;
import com.remind.entity.MessageIndexEntity;
import com.remind.global.AppConstant;

public class MessageIndexDaoImpl implements MessageIndexDao {
//    private static final String TAG = "MessageIndexDaoImpl";
    private DBHelper mDBHelper;

    public MessageIndexDaoImpl(Context context) {
        mDBHelper = DBHelper.getInstance(context);
    }

    @Override
    public synchronized void insert(MessageIndexEntity entity) {
        if (null == entity) {
            return;
        }

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MessageIndexMsg.NUM, entity.getNum());
        values.put(MessageIndexMsg.TIME, entity.getTime());

        values.put(MessageIndexMsg.NAME, entity.getName());
        values.put(MessageIndexMsg.IMG_PATH, entity.getImgPath());
        values.put(MessageIndexMsg.LOGIN_USER, entity.getLoginUser());

        values.put(MessageIndexMsg.MESSAGE, entity.getMessage());
        values.put(MessageIndexMsg.ISDELETE, entity.getIsDelete());
        values.put(MessageIndexMsg.UNREAND_COUNT, entity.getUnReadCount());
        values.put(MessageIndexMsg.SEND_STATE, entity.getSendState());

        db.insert(MessageIndexMsg.TABLENAME, null, values);
    }

    @Override
    public synchronized void delete(String id) {
        String sql = "update " + MessageIndexMsg.TABLENAME + " set " + MessageIndexMsg.ISDELETE + "='" + "1" + "'"
                + " where " + MessageIndexMsg.ID + "='" + id + "'";
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
//        Log.d(TAG, sql);
        db.execSQL(sql);
    }

    @Override
    public Cursor queryAll() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String sql = "select * from " + MessageIndexMsg.TABLENAME + " where " + MessageIndexMsg.ISDELETE + " = 0 " + " and "
                + MessageIndexMsg.LOGIN_USER + " = '" + AppConstant.USER_NUM + "' " + " order by " + MessageIndexMsg.TIME
                + " DESC";
        Cursor mCursor = null;
        mCursor = db.rawQuery(sql, null);
        return mCursor;
    }

    @Override
    public synchronized void update(MessageIndexEntity entity) {
        if (null == entity) {
            return;
        }

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        StringBuffer sb = new StringBuffer();
        sb.append("update " + MessageIndexMsg.TABLENAME + " set ");
        sb.append(MessageIndexMsg.NUM + "	= '" + entity.getNum() + "',");
        sb.append(MessageIndexMsg.TIME + "	= '" + entity.getTime() + "',");

        sb.append(MessageIndexMsg.NAME + "	= '" + entity.getName() + "',");
        sb.append(MessageIndexMsg.IMG_PATH + "	= '" + entity.getImgPath() + "',");

        sb.append(MessageIndexMsg.MESSAGE + "	= '" + entity.getMessage() + "',");
        sb.append(MessageIndexMsg.UNREAND_COUNT + "	= '" + entity.getUnReadCount() + "',");
        sb.append(MessageIndexMsg.ISDELETE + "	= '" + entity.getIsDelete() + "',");
        sb.append(MessageIndexMsg.SEND_STATE + "	= '" + entity.getSendState() + "' ");
        sb.append(" where " + MessageIndexMsg.ID + " = '" + entity.getId() + "'");
        String sql = sb.toString();

//        Log.d(TAG, sql);
        db.execSQL(sql);
    }

    @Override
    public Cursor queryByNum(String num) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String sql = "select * from " + MessageIndexMsg.TABLENAME + " where " + MessageIndexMsg.NUM + " =  '" + num + "' "
                + " and " + MessageIndexMsg.LOGIN_USER + " = '" + AppConstant.USER_NUM + "' ";
        Cursor mCursor = null;
        mCursor = db.rawQuery(sql, null);
        return mCursor;
    }

    @Override
    public int queryIdByNum(String num) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String sql = "select id from " + MessageIndexMsg.TABLENAME + " where " + MessageIndexMsg.ISDELETE + " = 0 and "
                + MessageIndexMsg.NUM + " = " + num + " and " + MessageIndexMsg.LOGIN_USER + " = '" + AppConstant.USER_NUM
                + "' ";
        Cursor c = db.rawQuery(sql, null);
        int length = 0;
        if (c.getCount() > 0) {
            c.moveToFirst();
            length = c.getInt(0);
        }
        c.close();
        return length;
    }
}
