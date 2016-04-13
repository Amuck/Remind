package com.remind.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.help.remind.R;
import com.remind.dao.PeopelDao;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.entity.PeopelEntity;
import com.remind.global.AppConstant;
import com.remind.http.HttpClient;
import com.remind.http.HttpClient.Friend;
import com.remind.util.AppUtil;
import com.remind.view.ClearEditText;

public class AddPeopleActivity extends AbActivity implements OnClickListener {
	private ClearEditText userName;
	private Button addUser;

	private PeopelDao peopelDao;
	private AlertDialog alertDialog;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				String s = (String) msg.obj;
				Toast.makeText(AddPeopleActivity.this, s,
						Toast.LENGTH_SHORT).show();
				
				//返回上一个界面
				setResult(RESULT_OK);
				finish();
				break;

			default:
				break;
			}
		};
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_people);
		
		initView();
	}
	
	private void initView() {
		userName = (ClearEditText) findViewById(R.id.et_user_name);
		addUser = (Button) findViewById(R.id.add_people);
		
		addUser.setOnClickListener(this);
		
		peopelDao = new PeopelDaoImpl(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_people:
			String friend = userName.getEditableText().toString();
			if (friend.length() <= 0) {
				Toast.makeText(AddPeopleActivity.this, "请输入用户名",
						Toast.LENGTH_SHORT).show();
				break;
			}

			
			if (peopelDao.queryPeopelByNum(friend)
					.getCount() > 0) {
				// 已经添加联系人
				AppUtil.showToast(this, "您已经添加过TA了，再换一个吧");
				return;
			}
			
			// 添加用户
//			String params = HttpClient.getJsonForPost(friendUser(friend));
//			friend(params);

//			// 发送短息
			sendSms(friend);
			break;

		default:
			break;
		}
	}
	
	/**
	 * 发送短信
	 */
	private void sendSms(final String friend) {
		if (null == alertDialog) {
			alertDialog = new AlertDialog.Builder(this).setTitle("是否发送短息？")
					.setMessage("我们将要发送一条短信给对方，以便将其加为您的好友，将收取正常的短信费用，请问是否继续？")
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							alertDialog.dismiss();
						}
					}).setPositiveButton("继续", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 发送短信
							String phone_number = friend
									.trim();
							String sms_content = "您的好友"
//									+ mContactsName.get(position)
									+ "邀请您加入互助提醒， 下载地址" + "XXXXXX";
							if (phone_number.equals("")) {
								AppUtil.showToast(AddPeopleActivity.this, "对方号码为空");
							} else {
								// SmsManager smsManager =
								// SmsManager.getDefault();
								// if(sms_content.length() > 70) {
								// List<String> contents =
								// smsManager.divideMessage(sms_content);
								// for(String sms : contents) {
								// smsManager.sendTextMessage(phone_number,
								// null, sms, null, null);
								// }
								// } else {
								// smsManager.sendTextMessage(phone_number,
								// null, sms_content, null, null);
								// }
								AppUtil.showToast(AddPeopleActivity.this, "发送成功");
							}

							// 添加联系人
							addPersonIntoDB(friend);

							String params = HttpClient.getJsonForPost(friendUser(friend));
							friend(params);
							
							alertDialog.dismiss();

							//返回上一个界面
//							setResult(RESULT_OK);
//							finish();
						}
					}).create();
		}
		alertDialog.show();
	}
	
	/**
	 * 将联系人插入数据库
	 * 
	 * @param position
	 */
	private void addPersonIntoDB(String friend) {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		PeopelEntity entity = new PeopelEntity("",
				"", friend,
				format.format(date), format.format(date),
				"", PeopelEntity.NORMAL,
				PeopelEntity.VALIDATE);
		peopelDao.insertPeopel(entity);
	}

	private void friend(final String params) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String s = HttpClient.post(HttpClient.url + HttpClient.friend, params);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = s;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	private Friend friendUser(String friendId) {
		Friend friend = new Friend();
		friend.user_id = AppConstant.FROM_ID;
		friend.friend_id = friendId;
		friend.state = "1";
		friend.friend_alias = "123";
		friend.msg = AppConstant.USER_NUM + "请求加你为好友";
		return friend;
	}
}
