package com.remind.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
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
import com.remind.dao.PeopelDao;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.entity.PeopelEntity;
import com.remind.global.AppConstant;
import com.remind.http.HttpClient;
import com.remind.sevice.BackService;
import com.remind.sevice.IBackService;
import com.remind.sp.MySharedPreferencesLoginType;
import com.remind.util.NetWorkUtil;
import com.remind.view.ClearEditText;

/**
 * 登陆注册界面
 * 
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
	private final static int HTTP_OVER = 0;
	private final static int LOGIN_SUCCESS = 1;
	private final static int LOGIN_FAIL = 2;
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
	/**
	 * 用户联系人数据库
	 */
	private PeopelDao peopelDao;
	
	/**
	 * 用户登陆id
	 */
	private String from_id = "";
	/**
	 * 是否注册service
	 */
//	private boolean isRegistService = false;

	private Intent mServiceIntent;
	
	private IBackService iBackService;
	
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			iBackService = null;

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iBackService = IBackService.Stub.asInterface(service);
		}
	};
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case HTTP_OVER:
				String s = (String) msg.obj;
				if (TextUtils.isEmpty(s) || s.length() < 10) {
					// 失败
					hideProgess();
					Toast.makeText(LoginActivity.this, "失败，请重试",
							Toast.LENGTH_SHORT).show();
				} else {
					// 成功
					if (!isRegist) {
						handler.sendEmptyMessage(LOGIN_SUCCESS);
					} else {
						// 登陆
						String params = HttpClient.getJsonForPost(HttpClient
								.getUserForLogin(et_user_mobile
										.getEditableText().toString(), et_user_psw
										.getEditableText().toString()));
						login(params);
					}
				}
				
				break;
			case LOGIN_SUCCESS:
//				RemindApplication.IS_LOGIN = true;
				// 成功
				hideProgess();
				// 是否记住用户名
				if (isRemember) {
					MySharedPreferencesLoginType.saveUserName(
							getApplicationContext(), et_user_mobile
									.getEditableText().toString(), et_user_psw
									.getEditableText().toString());
				} else {
					MySharedPreferencesLoginType.saveUserName(
							getApplicationContext(), "", "");
				}

//				Toast.makeText(LoginActivity.this, "登陆成功",
//						Toast.LENGTH_SHORT).show();
				
				finish();
				break;

			case LOGIN_FAIL:
				if (isProgesShow()) {
					hideProgess();
				}
				Toast.makeText(LoginActivity.this, "失败，请重试",
						Toast.LENGTH_SHORT).show();
				break;
			}

		}
	};

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

	@Override
	protected void onStop() {
		super.onStop();
//		if (isRegistService) {
//			unbindService(conn);
//		}
	}

	private void init() {
		login_title = (TextView) findViewById(R.id.login_title);
		tv_tips = (TextView) findViewById(R.id.tv_login_tips);
		et_user_mobile = (ClearEditText) findViewById(R.id.et_user_name);
		et_user_psw = (ClearEditText) findViewById(R.id.et_user_psw);
		login_remember = (CheckBox) findViewById(R.id.login_remember);
		login_user_reg = (CheckBox) findViewById(R.id.login_user_reg);
		btn_login = (Button) findViewById(R.id.btn_login);

		peopelDao = new PeopelDaoImpl(this);
		
		mServiceIntent = new Intent(this, BackService.class);
		
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
			String mobile = et_user_mobile.getEditableText().toString();
			String pwdStr = et_user_psw.getEditableText().toString();
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
					login(params);
				} else {
					// 注册
					params = HttpClient.getJsonForPost(HttpClient
							.getUserForReg(mobile, pwdStr, "User", "role_1" ));
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
			showProgess();
			new Thread(new Runnable() {

				@Override
				public void run() {
					String s = HttpClient.post(
							HttpClient.url + HttpClient.register, params);
					
					try {
						JSONObject jsonObject = new JSONObject(s);
//						String own_info = jsonObject.getString("own_info");
//						JSONObject own_infojsonObject = new JSONObject(own_info);
						from_id = jsonObject.getString("id");
						
					} catch (JSONException e) {
						e.printStackTrace();
						
						handler.sendEmptyMessage(LOGIN_FAIL);
						return;
					}
					
					Message msg = new Message();
					msg.what = HTTP_OVER;
					msg.obj = s;
					handler.sendMessage(msg);
				}
			}).start();
		} else {
			showToast(getResources().getString(R.string.net_null));
		}
	}

	/**
	 * 登陆
	 * @param params
	 */
	private void login(final String params) {
		if (NetWorkUtil.isAvailable(LoginActivity.this)) {
			if (!isProgesShow()) {
				showProgess();
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					String s = HttpClient.post(HttpClient.url + HttpClient.login,
							params);
					
					try {
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
						String num = et_user_mobile.getEditableText().toString();
						AppConstant.USER_NUM = num;
						PeopelEntity peopelEntity = new PeopelEntity();
						peopelEntity.setNum(num);
						peopelEntity.setName(nick);
						peopelEntity.setNickName(nick);
						peopelEntity.setImgPath(avatar);
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
					
					if (isSend) {
						// 成功
						Message msg = new Message();
						msg.what = LOGIN_SUCCESS;
						msg.obj = s;
						handler.sendMessage(msg);
					} else {
						handler.sendEmptyMessage(LOGIN_FAIL);
					}
				}
			}).start();
		} else {
			showToast(getResources().getString(R.string.net_null));
			if (isProgesShow()) {
				hideProgess();
			}

		}
		
	}
	
	/**
	 * 开启长连接并注册socket
	 * @return		注册service是否成功
	 */
//	private boolean startLongLink() {
////		isRegistService = true;
//		// 打开长连接
////		bindService(mServiceIntent, conn, BIND_AUTO_CREATE);
//		// 注册socket
//		String content = HttpClient.getJsonForPost(HttpClient.getSocketRegist(HttpClient.TYPE_NOTIFICATION, 
//				HttpClient.REGIST_MID, "", "", from_id));
//		
//		boolean isSend = false;
//		try {
//			Thread.sleep(100);
//			isSend = RemindApplication.iBackService.sendMessage(content);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		return isSend;
//	}
}