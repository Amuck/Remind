package com.remind.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.help.remind.R;
import com.remind.application.RemindApplication;
import com.remind.http.HttpClient;
import com.remind.sp.MySharedPreferencesLoginType;

public class WelcomeActivity extends LoginBaseActivity {

	/**
	 * 是否已经登录
	 */
	private boolean isLogin = false;
	/**
	 * 用户联系人数据库
	 */
	private Intent mIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		mIntent = new Intent();
		isLogin = MySharedPreferencesLoginType.isOnline(this);

		if (isLogin) {
			// 已登录
			handler.sendEmptyMessage(INIT_FINISH);
		} else {
			handler.sendEmptyMessageDelayed(INIT_FINISH, 2000);
		}
	}

	@Override
	public void initFinish() {
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
			mIntent.setClass(WelcomeActivity.this, HomeActivity.class);
			startActivity(mIntent);
			finish();
		}
	}

	@Override
	public void loginSuccess() {
		mIntent.setClass(WelcomeActivity.this, HomeActivity.class);
		startActivity(mIntent);
		finish();
	}

	@Override
	public void loginFail() {
		Toast.makeText(WelcomeActivity.this, "获取用户信息失败，请重新登陆",
				Toast.LENGTH_SHORT).show();
		mIntent.setClass(WelcomeActivity.this, HomeActivity.class);
		startActivity(mIntent);
		finish();
	}

	@Override
	public void loginBackRecevie() {
		// 登陆状态改变
		if (RemindApplication.IS_LOGIN) {
			handler.sendEmptyMessage(LOGIN_SUCCESS);
		} else {
			handler.sendEmptyMessage(LOGIN_FAIL);
		}
	}

	@Override
	public void outNet() {
		mIntent.setClass(WelcomeActivity.this, HomeActivity.class);
		startActivity(mIntent);
		finish();
	}
}
