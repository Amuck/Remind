package com.remind.dao;

import java.util.List;

import android.database.Cursor;

import com.remind.entity.PeopelEntity;

public interface PeopelDao {

	/**
	 * 插入一条联系人
	 * @param entity
	 */
	public void insertPeopel(PeopelEntity entity);
	/**
	 * 插入多条联系人
	 * @param entity
	 */
	public void insertPeopel(List<PeopelEntity> entity);
	/**
	 * 删除联系人
	 * @param num	对方手机号
	 */
	public void deletePeopelByNum(String num);
	/**
	 * 更新联系人信息
	 * @param entity
	 */
	public void updatePeopel(PeopelEntity entity);
	/**
	 * 查询所有联系人信息
	 * @return
	 */
	public Cursor queryPeopel();
	/**
	 * 根据号码添加联系人
	 * @param num
	 * @return
	 */
	public Cursor queryPeopelByNum(String num);
	
	/**
	 * 获取联系人头像路径
	 * @param num
	 * @return
	 */
	public String getImgPath(String num);
}
