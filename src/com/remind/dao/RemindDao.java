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
	/**
	 * 通过noticeid查询 
	 * @param noticeId
	 * @return
	 */
	public Cursor queryRemindByNoticeId(String noticeId);
	/**
	 * 查询所有提醒,提醒时间当天时间排在最前面，其他按提醒时间排序
	 * @param today				当天时间
	 * @param startPosition		开始位置
	 * @param selectedCount		选择数量
	 * @return
	 */
	public Cursor queryForMain(String today, int startPosition, int selectedCount);
	/**
	 * 获取有效闹钟数量
	 * @return
	 */
	public int getEffectiveCount();
	/**
	 * 获取非今天的已接受的提醒
	 * @param today
	 * @return
	 */
	public Cursor getOtherdayRemind(String today, int startPosition, int selectedCount);
	/**
	 * 获取非今天的已接受的提醒数量
	 * @param today
	 * @return
	 */
	public int getOtherdayCount(String today);
	/**
	 * 获取今天的已接受的提醒
	 * @param today
	 * @return
	 */
	public Cursor getTodayRemind(String today, int startPosition, int selectedCount);
	/**
	 * 获取今天的已接受的提醒数量
	 * @param today
	 * @return
	 */
	public int getTodayCount(String today);
	/**
	 * 获取未接受的提醒
	 * @return
	 */
	public Cursor getUnAcceptRemind(int startPosition, int selectedCount);
	/**
	 * 获取未接受的提醒数量
	 * @return
	 */
	public int getUnAcceptCount();
	/**
	 * 获取非今天的已接受的提醒
	 * @param today
	 * @return
	 */
	public Cursor getOtherdayRemind(String today);
	/**
	 * 获取今天的已接受的提醒
	 * @param today
	 * @return
	 */
	public Cursor getTodayRemind(String today);
	/**
	 * 获取未接受的提醒
	 * @return
	 */
	public Cursor getUnAcceptRemind();
	/**
	 * 在聊天界面查询提醒，需要将失效的提醒也查询
	 * 
	 * @param entity
	 * @return
	 */
	public Cursor queryRemindInChat(RemindEntity entity);
	/**
	 * 通过noticeId改成状态
	 * @param noticeId
	 * @return
	 */
	public void updateByNoticeId(String noticeId, int state);
}
