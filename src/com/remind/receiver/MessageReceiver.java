package com.remind.receiver;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.RemoteException;
import android.widget.Toast;

import com.remind.application.RemindApplication;
import com.remind.dao.impl.MessageDaoImpl;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.dao.impl.RemindDaoImpl;
import com.remind.entity.MessageEntity;
import com.remind.entity.PeopelEntity;
import com.remind.entity.RemindEntity;
import com.remind.http.HttpClient;
import com.remind.sevice.BackService;
import com.remind.util.AppUtil;
import com.remind.util.DataBaseParser;

/**
 * @author ChenLong
 * 
 *         监听重启事件，重置所有提醒
 */
public class MessageReceiver extends BroadcastReceiver {
	/**
	 * 获得发送消息的反馈, 是否发送成功
	 */
	public static final String MESSAGE_BACK_ACTION = "com.remind.message_back_ACTION";
	/**
	 * 好友状态改变
	 */
	public static final String PEOPLE_STATE_CHANGE_ACTION = "com.remind.people_state_change";
	/**
	 * 添加好友
	 */
	public static final String PEOPLE_ADD_ACTION = "com.remind.people_add";
	private Context context;
	private JSONObject jsonObject;
	private MessageDaoImpl messageDaoImpl;
	private PeopelDaoImpl peopelDao;
	private RemindDaoImpl remindDaoImpl;

	public MessageReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		String action = intent.getAction();
		
		if (action.equals(BackService.HEART_BEAT_ACTION)) {
			Toast.makeText(context, "Get a heart heat", Toast.LENGTH_SHORT)
					.show();
		} else {
			messageDaoImpl = new MessageDaoImpl(context);
			remindDaoImpl = new RemindDaoImpl(context);
			peopelDao = new PeopelDaoImpl(context);
			String message = intent.getStringExtra("message");

			try {
				jsonObject = new JSONObject(message.substring(
						message.indexOf("{"), message.length()));
				String type = jsonObject.getString("type");
				if ("message".equals(type)) {
					// 收到短消息，需要回馈
					String mid = jsonObject.getString("mid");
					String feed = MessageEntity.FEED_FAIL;
					try {
						feed = getMsg();
					} catch (Exception e) {
						feed = MessageEntity.FEED_FAIL;
					}
					// 更新数据库
//					Cursor cursor = messageDaoImpl.queryById(mid);
//					MessageEntity entity = DataBaseParser.getMessage(cursor).get(0);
//					cursor.close();
//					entity.setFeed(feed);
//					messageDaoImpl.updateFeedState(Long.valueOf(mid), feed);
				} else if ("message_ack".equals(type)) {
					// 发送消息的反馈
					getBack();
				} else if ("notification".equals(type)) {
					// socket注册
					registBack();
				} else if ("system".equals(type)) {
					// 系统消息
					if (jsonObject.has("notice_id")) {
						// 闹钟
						notice();
					} else {
						// 好友
						addFriend();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 回馈
	 * 
	 * @param type
	 * @throws JSONException
	 * @throws RemoteException
	 */
	private String getMsg() throws JSONException, RemoteException {
		String type = "message_ack";
		String mid = jsonObject.getString("mid");
		String param = HttpClient.getJsonForPost(HttpClient.msgFeedBack(mid,
				type));
		String feed = MessageEntity.FEED_DEFAULT;
		boolean isSuccess = RemindApplication.iBackService.sendMessage(param);
		if (isSuccess) {
			feed = MessageEntity.FEED_SUCCESS;
			Toast.makeText(context, "回馈成功", Toast.LENGTH_SHORT).show();
		} else {
			feed = MessageEntity.FEED_FAIL;
			Toast.makeText(context, "回馈fail", Toast.LENGTH_SHORT).show();
		}
		
		return feed;
	}

	/**
	 * 获得发送消息的反馈
	 * 
	 * @param type
	 * @throws JSONException
	 */
	private void getBack() throws JSONException {
		// +{"type":"message_ack","mid":"4","code":200}
		String mid = jsonObject.getString("mid");
		String code = jsonObject.getString("code");
		if ("200".equals(code)) {
			// 发送成功，更新数据库和界面
			messageDaoImpl.updateSendState(Long.valueOf(mid),
					MessageEntity.SEND_SUCCESS);
		} else {
			// 发送失败
			messageDaoImpl.updateSendState(Long.valueOf(mid),
					MessageEntity.SEND_FAIL);
		}
		Intent intent = new Intent(MESSAGE_BACK_ACTION);
		intent.putExtra("mid", mid);
		intent.putExtra("code", code);
		context.sendBroadcast(intent);
	}

	/**
	 * 是否注册成功
	 * 
	 * @param type
	 * @throws JSONException
	 */
	private void registBack() throws JSONException {
		String mid = jsonObject.getString("mid");
		String code = jsonObject.getString("code");
		if ("socket_regist".equals(mid)) {
			if ("200".equals(code)) {
				RemindApplication.IS_LOGIN = true;
				Toast.makeText(context, "登陆成功", Toast.LENGTH_SHORT).show();
			} else {
				RemindApplication.IS_LOGIN = false;
				Toast.makeText(context, "登陆失败，请重新登陆", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}
	
	private void notice() throws RemoteException, JSONException {
		getMsg();
		
		String notice_id = jsonObject.getString("notice_id");
		String content = jsonObject.getString("content");
		JSONObject contentObj = new JSONObject(content);
		int type = contentObj.getInt("type");
		switch (type) {
		case 3:
			// 收到闹钟
			JSONObject dataObj = new JSONObject(contentObj.getString("data"));
//			String owner_id = dataObj.getString("owner_id");
			String noticeContent = dataObj.getString("content");
			JSONObject noticeObj = new JSONObject(noticeContent);
			String isPrev = noticeObj.getString("isPrev");
			String time = noticeObj.getString("time");
			String title = noticeObj.getString("title");
			String repeatType = noticeObj.getString("type");
			String userNum = noticeObj.getString("userNum");
			String userNick = noticeObj.getString("userNick");
			String noticeString = noticeObj.getString("noticeContent");
			
			RemindEntity remindEntity = new RemindEntity("", notice_id,
					userNum, userNick,
					userNick, AppUtil.getNowTime(),
					"", noticeString, time
					, time, title, repeatType, Integer.valueOf(isPrev));
			remindEntity.setRemindState(RemindEntity.NEW);
			
			long remindId = remindDaoImpl.insertRemind(remindEntity);
			
			MessageEntity messageEntity = new MessageEntity("", "", "", 
					userNick, userNum, AppUtil.getNowTime(), 
					MessageEntity.SEND_SUCCESS, MessageEntity.NORMAL, 
					MessageEntity.TYPE_REMIND, 
					String.valueOf(remindId), "", 
					userNum, MessageEntity.TYPE_RECIEVE, 
					remindEntity.getContent(), MessageEntity.FEED_DEFAULT);
			
			messageDaoImpl.insert(messageEntity);
			break;
		case 4:
			// 收到是否同意闹钟
			break;

		default:
			break;
		}
	}

	private void addFriend() throws JSONException, RemoteException {
		getMsg();
		// 处理系统消息
		String content = jsonObject.getString("content");
		JSONObject contentObj = new JSONObject(content);
		if (contentObj.has("data")) {
			JSONObject dataObj = new JSONObject(contentObj.getString("data"));
			if (dataObj.has("friend_id")) {
				if (dataObj.has("state")) {
					// 同意添加好友
					String friend_id = dataObj.getString("friend_id");
					String state = dataObj.getString("state");
					String pn = dataObj.getString("pn");
					Cursor cursor = peopelDao.queryPeopelByNum(pn);
					ArrayList<PeopelEntity> lists = DataBaseParser
							.getPeoPelDetail(cursor);
					cursor.close();
					PeopelEntity entity = lists.get(0);
					if ("1".equals(state)) {
						// 同意
						entity.setStatus(PeopelEntity.FRIEND);
					} else {
						entity.setStatus(PeopelEntity.REFUSE);
					}
					entity.setFriendId(friend_id);
					peopelDao.updatePeopel(entity);
					
					// 通知UI刷新页面
					Intent intent = new Intent(PEOPLE_STATE_CHANGE_ACTION);
					intent.putExtra("pn", pn);
					intent.putExtra("state", state);
					context.sendBroadcast(intent);
				} else {
					// 收到添加好友请求
					String friend_id = dataObj.getString("friend_id");
					String nick = dataObj.getString("nick");
					String avatar = dataObj.getString("avatar");
					String pn = dataObj.getString("pn");
					PeopelEntity entity = new PeopelEntity(nick, nick,
							pn, "", "", avatar, PeopelEntity.NORMAL,
							PeopelEntity.ACCEPT, friend_id);
					peopelDao.insertPeopel(entity);
					// 通知UI刷新页面
					Intent intent = new Intent(PEOPLE_ADD_ACTION);
					intent.putExtra("PeopelEntity", entity);
//					intent.putExtra("state", state);
					context.sendBroadcast(intent);
				}
			} else {

			}
		}
	}
}
