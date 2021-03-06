package com.remind.dao;

import java.util.ArrayList;

import com.remind.entity.MessageEntity;

import android.database.Cursor;

public interface MessageDao {
    public long insert(MessageEntity entity);

    public void delete(String id);

    public void update(MessageEntity entity);

    public Cursor query(String recieveNum);

    /**
     * @param recieveNum
     *            接收者的号码
     * @param remindId
     *            所属提醒的id
     * @return 总消息条数
     */
    public int getCount(String recieveNum, String remindId);

    /**
     * 分页查询
     * 
     * @param currentPage
     *            当前页
     * @param pageSize
     *            每页显示的记录
     * @param recieveNum
     *            接收者的号码
     * @param remindId
     *            所属提醒的id
     * @return 当前页的记录
     */
    public ArrayList<MessageEntity> getMsgByPage(int currentPage, int pageSize, String recieveNum, String remindId);

    /**
     * 修改发送状态
     * 
     * @param msgId
     * @param state
     */
    public void updateSendState(long msgId, String state);

    /**
     * 根据其他的类型id去查询需要的数据
     * 
     * @param id
     * @return
     */
    public Cursor queryByOtherTypeId(String id);

    /**
     * 通过id查询数据
     * 
     * @param id
     * @return
     */
    public Cursor queryById(String id);

    /**
     * 更改消息反馈状态
     * 
     * @param msgId
     * @param feed
     */
    public void updateFeedState(long msgId, String feed);
}
