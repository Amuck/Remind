package com.remind.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.help.remind.R;
import com.remind.global.AppConstant;
import com.remind.http.HttpClient;
import com.remind.http.HttpClient.Friend;
import com.remind.view.ClearEditText;

public class AddPeopleActivity extends AbActivity implements OnClickListener {
	private ClearEditText userName;
	private Button addUser;

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				String s = (String) msg.obj;
				Toast.makeText(AddPeopleActivity.this, s,
						Toast.LENGTH_SHORT).show();
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
			// 添加用户
			String params = HttpClient.getJsonForPost(friendUser(friend));
			friend(params);
			break;

		default:
			break;
		}
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
		friend.user_id = AppConstant.USER_ID;
		friend.friend_id = friendId;
		friend.state = "1";
		friend.friend_alias = "123";
		friend.msg = AppConstant.USER_ID + "请求加你为好友";
		return friend;
	}
}
