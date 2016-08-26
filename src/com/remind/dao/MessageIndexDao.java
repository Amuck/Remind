package com.remind.dao;

import android.database.Cursor;

import com.remind.entity.MessageIndexEntity;

public interface MessageIndexDao {

    /**
     * 插入一条数据
     * 
     * @param entity
     */
    public void insert(MessageIndexEntity entity);

    /**
     * 删除一条数据
     * 
     * @param id
     */
    public void deleteByNum(String num);

    /**
     * 查询所有
     * 
     * @return
     */
    public Cursor queryAll();

    /**
     * 根据联系人号码查询
     * 
     * @return
     */
    public Cursor queryByNum(String num);

    /**
     * 更新一条数据
     * 
     * @param entity
     */
    public void update(MessageIndexEntity entity);

    /**
     * 获取联系人的id
     * 
     * @param num
     * @return
     */
    public int queryIdByNum(String num);
    /**
     * 获取数量
     * @param num
     * @return
     */
    public int getCountByNum(String num);
}
