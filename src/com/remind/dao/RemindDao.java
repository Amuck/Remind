package com.remind.dao;

import java.util.List;

import android.database.Cursor;

import com.remind.entity.RemindEntity;

public interface RemindDao {
	/**
	 * 插入提醒
	 * @param entity
	 */
	public long insertRemind(RemindEntity entity);
	/**
	 * 插入多条提醒
	 * @param entitys
	 */
	public void insertRemind(List<RemindEntity> entitys);
	/**
	 * 按照id删除提醒
	 * @param id
	 */
	public void deleteById(String id);
	/**
	 * 更新提醒
	 * @param entity
	 */
	public void updateRemind(RemindEntity entity);
	/**
	 * 查询提醒
	 * @param entity
	 * @return
	 */
	public Cursor queryRemind(RemindEntity entity);
	/**
	 * 查询所有提醒
	 * @return
	 */
	public Cursor queryRemind();
}
