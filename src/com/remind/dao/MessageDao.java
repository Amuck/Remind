package com.remind.dao;

import java.util.ArrayList;

import com.remind.entity.MessageEntity;

import android.database.Cursor;

public interface MessageDao {
	public void insert(MessageEntity entity);
	public void delete(String id);
	public void update(MessageEntity entity);
	public Cursor query(String msgIndex);
	/**
	 * @return		总消息条数
	 */
	public int getCount(int msgIndex);
	/**
     * 分页查询
     * 
     * @param currentPage 当前页
     * @param pageSize 每页显示的记录
     * @return 当前页的记录
	 */
	public ArrayList<MessageEntity> getMsgByPage(int currentPage, int pageSize, String msgIndex);
}
