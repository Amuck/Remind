package com.remind.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.help.remind.R;
import com.remind.application.RemindApplication;
import com.remind.dao.MessageDao;
import com.remind.dao.PeopelDao;
import com.remind.dao.RemindDao;
import com.remind.dao.impl.MessageDaoImpl;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.dao.impl.RemindDaoImpl;
import com.remind.entity.MessageEntity;
import com.remind.entity.PeopelEntity;
import com.remind.entity.RemindEntity;
import com.remind.global.AppConstant;
import com.remind.http.HttpClient;
import com.remind.receiver.MessageReceiver;
import com.remind.sevice.BackService;
import com.remind.sevice.IBackService;
import com.remind.util.NetWorkUtil;

public class LoginBaseActivity extends BaseActivity {
	public final static int HTTP_OVER = 0;
	public final static int LOGIN_SUCCESS = 1;
	public final static int LOGIN_FAIL = 2;
	public static final int INIT_FINISH = 3;
	
	/**
	 * 用户登陆id
	 */
	protected String from_id = "";
	/**
	 * 登录用户账号
	 */
	protected String mobile = "";
	/**
	 * 登录用户密码
	 */
	protected String pwdStr = "";
	/**
	 * 用户联系人数据库
	 */
	protected PeopelDao peopelDao;
	protected RemindDao remindDao;
	protected MessageDao messageDao;
	
	protected LoginReciver mReciver;
	protected IntentFilter mIntentFilter;
	/**
	 * 是否需要解除绑定
	 */
	protected boolean isNeedUnbind = false;
	
	protected Intent mServiceIntent;
	protected IBackService iBackService;
	protected ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			iBackService = null;

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iBackService = IBackService.Stub.asInterface(service);
			if (RemindApplication.iBackService == null) {
				RemindApplication.iBackService = iBackService;
			} else {
				iBackService = RemindApplication.iBackService;
			}
		}
	};
	
	public Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case INIT_FINISH:
				initFinish();
				break;

			case HTTP_OVER:
				Bundle bundle = msg.getData();
				String s = bundle.getString("code");
				httpOver(s);
				break;

			case LOGIN_SUCCESS:
				loginSuccess();
				break;

			case LOGIN_FAIL:
				loginFail();
				break;
			}
		};

	};
	
	/**
	 * 初始化完成
	 */
	public void initFinish(){};
	/**
	 * 注册
	 * @param s
	 */
	public void httpOver(String s){};
	/**
	 * 登陆成功
	 */
	public void loginSuccess(){};
	/**
	 * 登陆失败
	 */
	public void loginFail(){};
	/**
	 * 注册socket返回
	 */
	public void loginBackRecevie(){};
	/**
	 * 无网络
	 */
	public void outNet(){};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		peopelDao = new PeopelDaoImpl(this);
		remindDao = new RemindDaoImpl(this);
		messageDao = new MessageDaoImpl(this);
		mServiceIntent = new Intent(this, BackService.class);
		mReciver = new LoginReciver();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(MessageReceiver.LOGIN_STATE_ACTION);
	}
	
	class LoginReciver extends BroadcastReceiver {

		public LoginReciver() {
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MessageReceiver.LOGIN_STATE_ACTION)) {
				loginBackRecevie();
			}
		};
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(mReciver, mIntentFilter);
		if (RemindApplication.iBackService == null) {
			ComponentName cn = startService(mServiceIntent);
			if (cn != null) {
				bindService(mServiceIntent, conn, BIND_AUTO_CREATE);
			} else {
				RemindApplication.IS_LOGIN = false;
				showToast("登陆失败，请重新登陆");
				return;
			}
//			RemindApplication.iBackService = iBackService;
			isNeedUnbind = true;
		} else {
			isNeedUnbind = false;
		}
	}

	@Override
	protected void onStop() {
		unregisterReceiver(mReciver);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (isNeedUnbind) {
			unbindService(conn);
		}
		super.onDestroy();
	}
	
	/**
	 * 登陆
	 * 
	 * @param params
	 */
	public void login(final String params) {
		if (NetWorkUtil.isAvailable(this)) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					String s = HttpClient.post(HttpClient.url
							+ HttpClient.login, params);

					try {
						s = s.split("\\|")[1];
						JSONObject jsonObject = new JSONObject(s);
						String own_info = jsonObject.getString("own_info");
						JSONObject own_infojsonObject = new JSONObject(own_info);
						from_id = own_infojsonObject.getString("id");
						AppConstant.FROM_ID = from_id;
						// 昵称
						String nick = own_infojsonObject.getString("nick");
						// 头像路径
						String avatar = own_infojsonObject.getString("avatar");

						// 插入数据库
						String num = mobile;
						AppConstant.USER_NUM = num;
						PeopelEntity peopelEntity = new PeopelEntity();
						peopelEntity.setNum(num);
						peopelEntity.setName(nick);
						peopelEntity.setNickName(nick);
						peopelEntity.setImgPath(avatar);
						peopelEntity.setLoginUser(num);
						peopelEntity.setStatus(PeopelEntity.FRIEND);
						peopelEntity.setFriendId(from_id);

						Cursor cursor = peopelDao.queryPeopel();
						if (cursor != null && cursor.getCount() > 0) {
							peopelDao.updateOwner(peopelEntity);
						} else {
							peopelDao.insertPeopel(peopelEntity);
						}
						cursor.close();
						
						// 获取好友信息
						String friends = jsonObject.getString("friends");
						JSONArray friendArray = new JSONArray(friends);
						if (friendArray.length() > 0) {
							for (int i = 0; i < friendArray.length(); i++) {
								JSONObject friendObject = friendArray.getJSONObject(i);
								String friend_id = friendObject.getString("friend_id");
								
								int count = peopelDao.getCount(friend_id);
								if (count <= 0) {
									String friend_alias = friendObject.getString("friend_alias");
									String frined_avatar = friendObject.getString("frined_avatar");
									String frined_nick = friendObject.getString("frined_nick");
									String frined_mobile = friendObject.getString("frined_mobile");
									// 状态删除0  正常1 申请中2
									int state = friendObject.getInt("state");
									
									PeopelEntity entity = new PeopelEntity(friend_alias, frined_nick, frined_mobile, "", "", 
											frined_avatar, state == 0 ? PeopelEntity.DELETED : PeopelEntity.NORMAL, 
													state == 1 ? PeopelEntity.FRIEND : PeopelEntity.VALIDATE, 
															friend_id, num);
									peopelDao.insertPeopel(entity);
								}
							}
						}
						// 获取自己给别人设置的闹钟
						String own_notices = jsonObject.getString("own_notices");
						JSONArray own_noticesArray = new JSONArray(own_notices);
						if (own_noticesArray.length() > 0) {
							for (int i = 0; i < own_noticesArray.length(); i++) {
								JSONObject own_noticesObject = own_noticesArray.getJSONObject(i);
								String notice_id = own_noticesObject.getString("notice_id");
								int state = own_noticesObject.getInt("state");
								Cursor cursor2 = remindDao.queryRemindByNoticeId(notice_id);
								if (cursor2.getCount() <= 0 && state == 1) {
									String user_id = own_noticesObject.getString("user_id");
									
									String content = own_noticesObject.getString("content");
									JSONObject contentObject = new JSONObject(content);
									String isPrev = contentObject.getString("isPrev");
									String noticeContent = contentObject.getString("noticeContent");
									String time = contentObject.getString("time");
									
									String addTime = "";
//									String addTime = contentObject.getString("addTime");
									
									String title = contentObject.getString("title");
									String userNick = contentObject.getString("userNick");
									String type = contentObject.getString("type");
									String userNum = contentObject.getString("userNum");
									
									RemindEntity remindEntity = new RemindEntity();
									remindEntity.setOwnerNum(num);
									remindEntity.setTargetNum(num);
									remindEntity.setNoticeId(notice_id);
									remindEntity.setOwnerId(user_id);
									remindEntity.setTargetName(userNick);
									remindEntity.setNickName(userNick);
									remindEntity.setTargetNum(userNum);
									remindEntity.setTitle(title);
									remindEntity.setContent(noticeContent);
									remindEntity.setRepeatType(type);
									remindEntity.setRemindTime(time);
									remindEntity.setAddTime(addTime);
									remindEntity.setIsPreview(Integer.valueOf(isPrev));
									remindEntity.setRemindState(RemindEntity.ACCEPT);
									
									long id = remindDao.insertRemind(remindEntity);
									
									MessageEntity messageEntity = new MessageEntity("", nick, 
											num, 
											userNick, userNum, addTime, 
											MessageEntity.SEND_SUCCESS, MessageEntity.NORMAL, MessageEntity.TYPE_REMIND,
											id + "", "", userNum, MessageEntity.TYPE_RECIEVE, remindEntity.getContent(), 
											MessageEntity.FEED_DEFAULT, AppConstant.USER_NUM, id + "");
									messageDao.insert(messageEntity);
								}
								cursor2.close();
							}
						}
						
						// 获取别人给自己设置的闹钟
						String notices = jsonObject.getString("notices");
						JSONArray noticesArray = new JSONArray(notices);
						if (noticesArray.length() > 0) {
							for (int i = 0; i < noticesArray.length(); i++) {
								JSONObject noticesObject = noticesArray.getJSONObject(i);
								
							}
						}
					} catch (Exception e) {
						e.printStackTrace();

						handler.sendEmptyMessage(LOGIN_FAIL);
						return;
					}

					// 打开socket长连接
					boolean isSend = RemindApplication.startLongLink();

					if (!isSend) {
						// 失败
						handler.sendEmptyMessage(LOGIN_FAIL);
					}
				}
			}).start();
		} else {
			showToast(getResources().getString(R.string.net_null));
			outNet();
		}

	}
}
