package com.remind.activity;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.help.remind.R;
import com.remind.application.RemindApplication;
import com.remind.global.AppConstant;
import com.remind.http.HttpClient;
import com.remind.sp.MySharedPreferencesLoginType;
import com.remind.util.NetWorkUtil;
import com.remind.view.ClearEditText;

/**
 * 登陆注册界面
 * 
 */
public class LoginActivity extends LoginBaseActivity implements OnClickListener {
	/**
	 * 标题：用户登陆或者用户注册
	 */
	private TextView login_title;
	/**
	 * 错误信息
	 */
	private TextView tv_tips;
	/**
	 * 用户名输入
	 */
	private ClearEditText et_user_mobile;
	/**
	 * 用户密码输入
	 */
	private ClearEditText et_user_psw;
	/**
	 * 是否记住用户名
	 */
	private CheckBox login_remember;
	/**
	 * 用户注册及登陆切换
	 */
	private CheckBox login_user_reg;
	/**
	 * 用户登陆或注册
	 */
	private Button btn_login;
	/**
	 * true：记住用户名； false：忘记用户名
	 */
	private boolean isRemember = false;
	/**
	 * false：用户登陆； true：用户注册
	 */
	private boolean isRegist = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// 注册默认的未捕捉异常处理类
		// Thread.setDefaultUncaughtExceptionHandler(AppException
		// .getAppExceptionHandler());
		// AppManager.getAppManager().addActivity(this);

		init();
	}

	private void init() {
		login_title = (TextView) findViewById(R.id.login_title);
		tv_tips = (TextView) findViewById(R.id.tv_login_tips);
		et_user_mobile = (ClearEditText) findViewById(R.id.et_user_name);
		et_user_psw = (ClearEditText) findViewById(R.id.et_user_psw);
		login_remember = (CheckBox) findViewById(R.id.login_remember);
		login_user_reg = (CheckBox) findViewById(R.id.login_user_reg);
		btn_login = (Button) findViewById(R.id.btn_login);

		isRemember = MySharedPreferencesLoginType.isRemember(this);
		login_remember.setChecked(isRemember);
		// 是否记住用户名

		et_user_mobile.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				tv_tips.setText("");
				return false;
			}
		});
		et_user_psw.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				tv_tips.setText("");
				return false;
			}
		});

		login_remember
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							isRemember = true;
						} else {
							isRemember = false;
						}
						MySharedPreferencesLoginType.setIsRemember(LoginActivity.this, isRemember);
					}
				});
		login_user_reg
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						String titleStr = "";
						String btnStr = "";
						int visible = View.VISIBLE;
						if (isChecked) {
							// 注册
							isRegist = true;
							titleStr = getResources().getString(
									R.string.user_login_title);
							btnStr = getResources()
									.getString(R.string.user_reg);
							visible = View.INVISIBLE;
						} else {
							// 登陆
							isRegist = false;
							titleStr = getResources().getString(
									R.string.user_reg_title);
							btnStr = getResources().getString(
									R.string.user_login);
							visible = View.VISIBLE;
						}

						// 登陆注册切换
						login_title.setText(titleStr);
						login_remember.setVisibility(visible);
						login_user_reg.setText(titleStr);
						btn_login.setText(btnStr);
					}
				});
		btn_login.setOnClickListener(this);

		String name = MySharedPreferencesLoginType.getString(
				getApplicationContext(), MySharedPreferencesLoginType.USERNAME);
		if (!TextUtils.isEmpty(name)) {
			login_remember.setChecked(true);
			et_user_mobile.setText(name);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			// 登陆/注册
			mobile = et_user_mobile.getEditableText().toString();
			pwdStr = et_user_psw.getEditableText().toString();
			if (mobile.length() == 0) {
				// 帐号不能为空
				tv_tips.setText(getResources().getString(R.string.account_null));
			} else if (pwdStr.length() == 0) {
				// 您的密码不能为空
				tv_tips.setText(getResources().getString(R.string.pwd_null));
			} else if (pwdStr.length() < 6) {
				// 您的密码不能少于六位
				tv_tips.setText(getResources()
						.getString(R.string.pwd_len_error));
			} else {
				String params = "";
				if (!isRegist) {
					// 登陆
					params = HttpClient.getJsonForPost(HttpClient
							.getUserForLogin(mobile, pwdStr));

					if (!isProgesShow()) {
						showProgess();
					}
					login(params);
				} else {
					// 注册
					params = HttpClient.getJsonForPost(HttpClient
							.getUserForReg(mobile, pwdStr, "User", "role_1"));
					createUser(params);
				}
			}

			break;
		}
	}

	/**
	 * 注册
	 */
	private void createUser(final String params) {
		if (NetWorkUtil.isAvailable(LoginActivity.this)) {
			if (!isProgesShow()) {
				showProgess();
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					String s = HttpClient.post(HttpClient.url
							+ HttpClient.register, params);
					String code = null;
					try {
						if (null == s || !s.contains("|")) {
							s = null;
						}

						code = s.split("\\|")[0];
						s = s.split("\\|")[1];
						JSONObject jsonObject = new JSONObject(s);
						from_id = jsonObject.getString("id");

					} catch (Exception e) {
						e.printStackTrace();
					}

					Message msg = handler.obtainMessage();
					msg.what = HTTP_OVER;
					msg.obj = s;
					Bundle bundle = new Bundle();
					bundle.putString("code", code);
					msg.setData(bundle);
					handler.sendMessage(msg);
				}
			}).start();
		} else {
			showToast(getResources().getString(R.string.net_null));
			if (isProgesShow()) {
				hideProgess();
			}
		}
	}

	@Override
	public void httpOver(String s) {
		if (TextUtils.isEmpty(s)) {
			// 失败
			hideProgess();
			Toast.makeText(LoginActivity.this, "注册失败，请重试.", Toast.LENGTH_SHORT)
					.show();
		} else if ("402".equals(s)) {
			hideProgess();
			Toast.makeText(LoginActivity.this, "这个号码已经注册过，请重试.",
					Toast.LENGTH_SHORT).show();
		} else if ("200".equals(s)) {
			// 成功
			if (!isRegist) {
				handler.sendEmptyMessage(LOGIN_SUCCESS);
			} else {
				// 登陆
				String params = HttpClient.getJsonForPost(HttpClient
						.getUserForLogin(et_user_mobile.getEditableText()
								.toString(), et_user_psw.getEditableText()
								.toString()));
				login(params);
			}
		} else {
			// 失败
			hideProgess();
			Toast.makeText(LoginActivity.this, "注册失败，请重试.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void loginSuccess() {
	}

	@Override
	public void loginFail() {
		if (isProgesShow()) {
			hideProgess();
		}
		Toast.makeText(LoginActivity.this, "登陆失败，请重试", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void loginBackRecevie() {
		hideProgess();
		// 登陆状态改变
		if (RemindApplication.IS_LOGIN) {
			Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
			
			// 是否记住用户名
			if (isRemember) {
				MySharedPreferencesLoginType.saveUserName(getApplicationContext(),
						et_user_mobile.getEditableText().toString(), et_user_psw
								.getEditableText().toString());
			} else {
				MySharedPreferencesLoginType.saveUserName(getApplicationContext(),
						"", "");
			}
			// 成功
			MySharedPreferencesLoginType.setOnlineState(LoginActivity.this,
					true);
			MySharedPreferencesLoginType.saveFromId(LoginActivity.this,
					AppConstant.FROM_ID);
			finish();
		} else {
			Toast.makeText(this, "登陆失败，请重新登陆", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void outNet() {
		if (isProgesShow()) {
			hideProgess();
		}
	}

	@Override
	void initFinish() {
		
	}
}