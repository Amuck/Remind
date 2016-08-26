package com.remind.dao;

import java.util.List;

import android.database.Cursor;

import com.remind.entity.PeopelEntity;

public interface PeopelDao {

    /**
     * 插入一条联系人
     * 
     * @param entity
     */
    public void insertPeopel(PeopelEntity entity);

    /**
     * 插入多条联系人
     * 
     * @param entity
     */
    public void insertPeopel(List<PeopelEntity> entity);

    /**
     * 删除联系人
     * 
     * @param num
     *            对方手机号
     */
    public void deletePeopelByNum(String num);

    /**
     * 从数据库中删除数据
     * 
     * @param num
     */
    public void realDeleteByNum(String num);

    /**
     * 更新联系人信息
     * 
     * @param entity
     */
    public void updatePeopel(PeopelEntity entity);

    /**
     * 查询所有联系人信息
     * 
     * @return
     */
    public Cursor queryPeopel();
    /**
     * 查询所有联系人信息除了登陆用户
     * @return
     */
    public Cursor queryPeopelExceptOwner();

    /**
     * 根据号码添加联系人
     * 
     * @param num
     * @return
     */
    public Cursor queryPeopelByNum(String num);

    /**
     * 获取联系人头像路径
     * 
     * @param num
     * @return
     */
    public String getImgPath(String num);

    /**
     * 查询登陆用户信息
     * 
     * @return
     */
    public Cursor queryOwner();

    /**
     * 更新登陆用户信息
     * 
     * @param entity
     */
    public void updateOwner(PeopelEntity entity);

    /**
     * 根据friendid查找联系人
     * 
     * @param friendId
     * @return
     */
    public Cursor queryPeopelByFriendId(String friendId);

    /**
     * 将联系人从数据库中删除
     * 
     * @param num
     */
    public void deleteFromDbByNum(String num);

    /**
     * 通过friendid获取数量
     * 
     * @param friendId
     * @return
     */
    public int getCount(String friendId);
}
