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
import com.remind.entity.MessageEntity;
import com.remind.entity.PeopelEntity;
import com.remind.global.AppConstant;
import com.remind.http.HttpClient;
import com.remind.sevice.BackService;
import com.remind.util.DataBaseParser;

/**
 * @author ChenLong
 *
 * 监听重启事件，重置所有提醒
 */
public class MessageReceiver extends BroadcastReceiver {
	public static final String MESSAGE_BACK_ACTION="com.remind.message_back_ACTION";
	private Context context;
	private JSONObject jsonObject;
	private MessageDaoImpl messageDaoImpl;
	private PeopelDaoImpl peopelDao;

	public MessageReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		String action = intent.getAction();
		if (action.equals(BackService.HEART_BEAT_ACTION)) {
			Toast.makeText(context, "Get a heart heat", Toast.LENGTH_SHORT).show();
		} else {
			messageDaoImpl = new MessageDaoImpl(context);
			peopelDao = new PeopelDaoImpl(context);
			String message = intent.getStringExtra("message");
			
			try {
				jsonObject = new JSONObject(message.substring(message.indexOf("{"), message.length()));
				String type = jsonObject.getString("type");
				if ("message".equals(type)) {
					// 收到短消息，需要回馈
					getMsg();
				} else if("message_ack".equals(type)){
					// 发送消息的反馈
					getBack();
				} else if("notification".equals(type)){
					// socket注册
					registBack();
				} else if("system".equals(type)){
					// 系统消息
					addFriend();
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
	 * 收到消息，发出回馈
	 * @param type
	 * @throws JSONException
	 * @throws RemoteException
	 */
	private void getMsg() throws JSONException, RemoteException {
		String type = "message_ack";
		String mid = jsonObject.getString("mid");
		String param = HttpClient.getJsonForPost(HttpClient.msgFeedBack(mid, type));
		boolean isSuccess = RemindApplication.iBackService.sendMessage(param);
		if (isSuccess) {
			Toast.makeText(context, "回馈成功", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "回馈fail", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 获得发送消息的反馈
	 * @param type
	 * @throws JSONException
	 */
	private void getBack() throws JSONException {
		//+{"type":"message_ack","mid":"4","code":200}
		String mid = jsonObject.getString("mid");
		String code = jsonObject.getString("code");
		if ("200".equals(code)) {
			// 发送成功，更新数据库和界面
			messageDaoImpl.updateSendState(Long.valueOf(mid), MessageEntity.SEND_SUCCESS);
		} else {
			// 发送失败
			messageDaoImpl.updateSendState(Long.valueOf(mid), MessageEntity.SEND_FAIL);
		}
		Intent intent=new Intent(MESSAGE_BACK_ACTION);
		intent.putExtra("mid", mid);
		intent.putExtra("code", code);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 是否注册成功
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
				Toast.makeText(context, "登陆失败，请重新登陆", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void addFriend() throws JSONException, RemoteException {
		//�{"type":"system","mid":1461049368685,
		//"content":{"friend_id":"13716022538","nick":"123",
		//"avatar":"http://sdfsdf.1.pig","pn":"13716022538",
		//"msg":[230,183,187,229,138,160,229,165,189,229,143,139]}}
		getMsg();
		//处理系统消息
		String content = jsonObject.getString("content");
		JSONObject contentObj = new JSONObject(content);
		if (contentObj.has("friend_id")) {
			if (contentObj.has("state")) {
				// 同意添加好友
				String friend_id = contentObj.getString("friend_id");
				String state = contentObj.getString("state");
				Cursor cursor = peopelDao.queryPeopelByNum(friend_id);
				ArrayList<PeopelEntity> lists = DataBaseParser.getPeoPelDetail(cursor);
				cursor.close();
				PeopelEntity entity = lists.get(0);
				if ("0".equals(state)) {
					// 同意
					entity.setStatus(PeopelEntity.FRIEND);
				} else {
					entity.setStatus(PeopelEntity.REFUSE);
				}
				peopelDao.updatePeopel(entity);
			} else {
				// 添加好友
				String friend_id = contentObj.getString("friend_id");
				String nick = contentObj.getString("nick");
				String avatar = contentObj.getString("avatar");
//				String msg = contentObj.getString("msg");
				PeopelEntity entity = new PeopelEntity(nick, nick, friend_id, "", "", avatar, PeopelEntity.NORMAL, PeopelEntity.ACCEPT);
				peopelDao.insertPeopel(entity);
			}
		} else {

		}
		
	}
}
