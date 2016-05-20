package com.remind.activity;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.help.remind.R;
import com.remind.application.RemindApplication;
import com.remind.dao.PeopelDao;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.entity.PeopelEntity;
import com.remind.global.AppConstant;
import com.remind.http.HttpClient;
import com.remind.receiver.MessageReceiver;
import com.remind.sp.MySharedPreferencesLoginType;
import com.remind.util.NetWorkUtil;

public class WelcomeActivity extends BaseActivity {
	private final static int LOGIN_SUCCESS = 1;
	private final static int LOGIN_FAIL = 2;
	private static final int INIT_FINISH = 3;

	/**
	 * 是否已经登录
	 */
	private boolean isLogin = false;
	/**
	 * 用户登陆id
	 */
	private String from_id = "";
	/**
	 * 登录用户账号
	 */
	private String mobile = "";
	/**
	 * 登录用户密码
	 */
	private String pwdStr = "";
	/**
	 * 用户联系人数据库
	 */
	private PeopelDao peopelDao;
	private Intent mIntent;
	
	private LoginBackReciver mReciver;
	private IntentFilter mIntentFilter;

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case INIT_FINISH:
				isAlreadyLogin();
				break;

			case LOGIN_SUCCESS:
				mIntent.setClass(WelcomeActivity.this, HomeActivity.class);
				startActivity(mIntent);
				finish();
				break;

			case LOGIN_FAIL:
				Toast.makeText(WelcomeActivity.this, "获取用户信息失败，请重新登陆",
						Toast.LENGTH_SHORT).show();
				mIntent.setClass(WelcomeActivity.this, LoginActivity.class);
				startActivity(mIntent);
				finish();
				break;
			}
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		mIntent = new Intent();
		peopelDao = new PeopelDaoImpl(this);
		mReciver = new LoginBackReciver();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(MessageReceiver.LOGIN_STATE_ACTION);
		isLogin = MySharedPreferencesLoginType.isOnline(this);
		
		if (isLogin) {
			// 已登录
			handler.sendEmptyMessage(INIT_FINISH);
		} else {
			handler.sendEmptyMessageDelayed(INIT_FINISH, 2000);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(mReciver, mIntentFilter);
	}

	@Override
	protected void onStop() {
		unregisterReceiver(mReciver);
		super.onStop();
	}

	/**
	 * 是否已经登陆了
	 */
	private void isAlreadyLogin() {

		if (isLogin) {
			// 登陆没有退出
			mobile = MySharedPreferencesLoginType.getString(this,
					MySharedPreferencesLoginType.USERNAME);
			pwdStr = MySharedPreferencesLoginType.getString(this,
					MySharedPreferencesLoginType.PASSWORD);
			// 登陆
			String params = HttpClient.getJsonForPost(HttpClient
					.getUserForLogin(mobile, pwdStr));
			login(params);
		} else {
			// 退出了
			mIntent.setClass(WelcomeActivity.this, LoginActivity.class);
			startActivity(mIntent);
			finish();
		}
	}

	/**
	 * 登陆
	 * 
	 * @param params
	 */
	private void login(final String params) {
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

						Cursor cursor = peopelDao.queryPeopel();
						if (cursor != null && cursor.getCount() > 0) {
							peopelDao.updateOwner(peopelEntity);
						} else {
							peopelDao.insertPeopel(peopelEntity);
						}
						cursor.close();
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
			mIntent.setClass(WelcomeActivity.this, LoginActivity.class);
			startActivity(mIntent);
			finish();
		}

	}
	
	class LoginBackReciver extends BroadcastReceiver {

		public LoginBackReciver() {
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MessageReceiver.LOGIN_STATE_ACTION)) {
				// 登陆状态改变
				if (RemindApplication.IS_LOGIN) {
					handler.sendEmptyMessage(LOGIN_SUCCESS);
				} else {
					handler.sendEmptyMessage(LOGIN_FAIL);
				}
			}
		};
	}
}
