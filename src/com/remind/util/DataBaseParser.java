package com.remind.util;

import java.util.ArrayList;

import android.database.Cursor;

import com.remind.dao.msg.MessageIndexMsg;
import com.remind.dao.msg.MessageMsg;
import com.remind.dao.msg.PeopelMsg;
import com.remind.dao.msg.RemindMsg;
import com.remind.entity.MessageEntity;
import com.remind.entity.MessageIndexEntity;
import com.remind.entity.PeopelEntity;
import com.remind.entity.RemindEntity;

public class DataBaseParser {

	/**
	 * 获取详细数据
	 * 
	 * @param mCursor
	 * @return
	 */
	public static ArrayList<PeopelEntity> getPeoPelDetail(Cursor mCursor) {
		ArrayList<PeopelEntity> lists = new ArrayList<PeopelEntity>();
		while (mCursor.moveToNext()) {
//			String id = mCursor.getString(mCursor.getColumnIndex(PeopelMsg.ID));
			String name = mCursor.getString(mCursor.getColumnIndex(PeopelMsg.NAME));
			String nickName = mCursor.getString(mCursor.getColumnIndex(PeopelMsg.NICKNAME));
			String num = mCursor.getString(mCursor.getColumnIndex(PeopelMsg.NUM));
			String imgPath = mCursor.getString(mCursor.getColumnIndex(PeopelMsg.IMGPATH));
			String addTime = mCursor.getString(mCursor.getColumnIndex(PeopelMsg.ADDTIME));
			String updateTime = mCursor.getString(mCursor.getColumnIndex(PeopelMsg.UPDATETIME));
			String isDelete = mCursor.getString(mCursor.getColumnIndex(PeopelMsg.ISDELETE));
			String friendId = mCursor.getString(mCursor.getColumnIndex(PeopelMsg.FRIEND_ID));
			int status = mCursor.getInt(mCursor.getColumnIndex(PeopelMsg.STATUS));
			String loginUser = mCursor.getString(mCursor.getColumnIndex(PeopelMsg.LOGIN_USER));
			
			PeopelEntity entity = new PeopelEntity(name, nickName, num, addTime, 
					updateTime, imgPath, isDelete, status, friendId, loginUser);
			lists.add(entity);
		}
		return lists;
	}

	/**
	 * 获取提醒详细数据
	 * 
	 * @param mCursor
	 * @return
	 */
	public static ArrayList<RemindEntity> getRemindDetail(Cursor mCursor) {
		ArrayList<RemindEntity> lists = new ArrayList<RemindEntity>();
		while (mCursor.moveToNext()) {
			String id = mCursor.getString(mCursor.getColumnIndex(RemindMsg.ID));
			String ownerNum = mCursor.getString(mCursor.getColumnIndex(RemindMsg.OWNER_NUM));
			String targetNum = mCursor.getString(mCursor.getColumnIndex(RemindMsg.TARGET_NUM));
			String targetName = mCursor.getString(mCursor.getColumnIndex(RemindMsg.TARGET_NAME));
			String targetNick = mCursor.getString(mCursor.getColumnIndex(RemindMsg.NICK_NAME));
			String noticeId = mCursor.getString(mCursor.getColumnIndex(RemindMsg.NOTICE_ID));
			String ownerId = mCursor.getString(mCursor.getColumnIndex(RemindMsg.OWNER_ID));
			String addTime = mCursor.getString(mCursor.getColumnIndex(RemindMsg.ADD_TIME));
			String lastEditTime = mCursor.getString(mCursor.getColumnIndex(RemindMsg.REMIND_TIME_MILI));
			String content = mCursor.getString(mCursor.getColumnIndex(RemindMsg.CONTENT));
			String limitTime = mCursor.getString(mCursor.getColumnIndex(RemindMsg.LIMIT_TIME));
			String remindTime = mCursor.getString(mCursor.getColumnIndex(RemindMsg.REMIND_TIME));
			String title = mCursor.getString(mCursor.getColumnIndex(RemindMsg.TITLE));
			String audioPath = mCursor.getString(mCursor.getColumnIndex(RemindMsg.AUDIO_PATH));
			String videoPath = mCursor.getString(mCursor.getColumnIndex(RemindMsg.VIDEO_PATH));
			String imgPath = mCursor.getString(mCursor.getColumnIndex(RemindMsg.IMG_PATH));
			String isDelete = mCursor.getString(mCursor.getColumnIndex(RemindMsg.IS_DELETE));
			String repeatType = mCursor.getString(mCursor.getColumnIndex(RemindMsg.REPEAT_TYPE));
			int remindMethod = mCursor.getInt(mCursor.getColumnIndex(RemindMsg.REMIND_METHOD));
			int remindState = mCursor.getInt(mCursor.getColumnIndex(RemindMsg.REMIND_STATE));
			int launchState = mCursor.getInt(mCursor.getColumnIndex(RemindMsg.LAUNCH_STATE));
			int isPreview = mCursor.getInt(mCursor.getColumnIndex(RemindMsg.IS_PRIVIEW));
			int remindCount = mCursor.getInt(mCursor.getColumnIndex(RemindMsg.REMIND_COUNT));
			
			RemindEntity entity = new RemindEntity(id, ownerNum, targetNum, 
					targetName, targetNick, addTime, lastEditTime, title, 
					content, limitTime, remindTime, audioPath, videoPath, 
					imgPath, remindMethod, remindState, launchState, isDelete, repeatType,
					isPreview, remindCount, noticeId, ownerId);
			lists.add(entity);
		}
		return lists;
	}
	
	/**
	 * 获取消息索引信息
	 * 
	 * @param mCursor
	 * @return
	 */
	public static ArrayList<MessageIndexEntity> getMessageIndex(Cursor mCursor) {
		ArrayList<MessageIndexEntity> lists = new ArrayList<MessageIndexEntity>();
		while (mCursor.moveToNext()) {
			String id = mCursor.getString(mCursor.getColumnIndex(MessageIndexMsg.ID));
			String num = mCursor.getString(mCursor.getColumnIndex(MessageIndexMsg.NUM));
			String message = mCursor.getString(mCursor.getColumnIndex(MessageIndexMsg.MESSAGE));
			String time = mCursor.getString(mCursor.getColumnIndex(MessageIndexMsg.TIME));
			String isDelete = mCursor.getString(mCursor.getColumnIndex(MessageIndexMsg.ISDELETE));
			int unReadCount = mCursor.getInt(mCursor.getColumnIndex(MessageIndexMsg.UNREAND_COUNT));
			String sendState = mCursor.getString(mCursor.getColumnIndex(MessageIndexMsg.SEND_STATE));
			String name = mCursor.getString(mCursor.getColumnIndex(MessageIndexMsg.NAME));
			String imgPath = mCursor.getString(mCursor.getColumnIndex(MessageIndexMsg.IMG_PATH));
			String loginUser = mCursor.getString(mCursor.getColumnIndex(MessageIndexMsg.LOGIN_USER));
			
			MessageIndexEntity entity = new MessageIndexEntity(id, num, message, time, name, imgPath, 
					unReadCount, isDelete, sendState, loginUser);
			lists.add(entity);
		}
		return lists;
	}
	
	/**
	 * 获取消息信息
	 * 
	 * @param mCursor
	 * @return
	 */
	public static ArrayList<MessageEntity> getMessage(Cursor mCursor) {
		ArrayList<MessageEntity> lists = new ArrayList<MessageEntity>();
		while (mCursor.moveToNext()) {
			String id = mCursor.getString(mCursor.getColumnIndex(MessageMsg.ID));
			String msgIndex = mCursor.getString(mCursor.getColumnIndex(MessageMsg.MSG_INDEX));
			String recieveName = mCursor.getString(mCursor.getColumnIndex(MessageMsg.RECIEVE_NAME));
			String time = mCursor.getString(mCursor.getColumnIndex(MessageMsg.TIME));
			String isDelete = mCursor.getString(mCursor.getColumnIndex(MessageMsg.ISDELETE));
			String recieveNum = mCursor.getString(mCursor.getColumnIndex(MessageMsg.RECIEVE_NUM));
			String sendState = mCursor.getString(mCursor.getColumnIndex(MessageMsg.SEND_STATE));
			String sendNum = mCursor.getString(mCursor.getColumnIndex(MessageMsg.SEND_NUM));
			String sendName = mCursor.getString(mCursor.getColumnIndex(MessageMsg.SEND_NAME));
			String msgType = mCursor.getString(mCursor.getColumnIndex(MessageMsg.MSG_TYPE));
			String otherTypeId = mCursor.getString(mCursor.getColumnIndex(MessageMsg.OTHER_TYPE_ID));
			String msgPath = mCursor.getString(mCursor.getColumnIndex(MessageMsg.MSG_PATH));
			String isComing = mCursor.getString(mCursor.getColumnIndex(MessageMsg.IS_COMING));
			String content = mCursor.getString(mCursor.getColumnIndex(MessageMsg.CONTENT));
			String feed = mCursor.getString(mCursor.getColumnIndex(MessageMsg.IS_FEED));
			String loginUser = mCursor.getString(mCursor.getColumnIndex(MessageMsg.LOGIN_USER));
			
			MessageEntity entity = new MessageEntity(id, recieveName, recieveNum, sendName, sendNum, 
					time, sendState, isDelete, msgType, otherTypeId, msgPath, msgIndex, isComing, content, 
					feed, loginUser);
			lists.add(entity);
		}
		return lists;
	}
}
