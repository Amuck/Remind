package com.remind.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.help.remind.R;
import com.remind.dao.RemindDao;
import com.remind.dao.impl.RemindDaoImpl;
import com.remind.entity.RemindEntity;
import com.remind.util.AppUtil;
import com.remind.util.DataBaseParser;

public class AlertActivity extends BaseActivity implements OnClickListener{

	private PowerManager pm;
	private WakeLock mWakelock;

	private Window win;

	/**
	 * 标题
	 */
	private TextView title;
	/**
	 * 内容
	 */
	private TextView content;
	/**
	 * 图片
	 */
	private ImageView contentImg;
	/**
	 * 已经开始
	 */
	private Button startBtn;
	/**
	 * 马上开始
	 */
	private Button readyBtn;
	/**
	 * 10分钟后提醒
	 */
	private Button laterBtn;
	/**
	 * 其他
	 */
	private Button otherBtn;
	
	/**
	 * 提醒的id
	 */
	private int remindId;
	private RemindDao remindDao;
	private RemindEntity remindEntity = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 在锁屏上显示
		win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		setContentView(R.layout.activity_alert);
		// 获取闹钟id
		Intent intent = getIntent();
		remindId = intent.getIntExtra("requestCode", -1);
		
		if (-1 == remindId) {
			AppUtil.showToast(this, "播放提醒失败");
			finish();
		}
		
		init();
	}
	
	private void init() {
		title = (TextView) findViewById(R.id.alert_title);
		content = (TextView) findViewById(R.id.alert_content);
		contentImg = (ImageView) findViewById(R.id.alert_img);
		startBtn = (Button) findViewById(R.id.alert_start);
		readyBtn = (Button) findViewById(R.id.alert_ready);
		laterBtn = (Button) findViewById(R.id.alert_later);
		otherBtn = (Button) findViewById(R.id.alert_other);
		
		remindDao = new RemindDaoImpl(this);
		getRemindData();
		
		otherBtn.setOnClickListener(this);
	}
	
	/**
	 * 获取提醒数据
	 */
	private void getRemindData() {
		try {
			RemindEntity r = new RemindEntity();
			r.setId(remindId + "");
			Cursor cursor = remindDao.queryRemind(r);
			remindEntity = DataBaseParser.getRemindDetail(cursor).get(0);
			// 初始化页面数据
			title.setText(remindEntity.getTitle());
			content.setText(remindEntity.getContent());
		} catch (Exception e) {
			AppUtil.showToast(this, "播放提醒失败");
			finish();
		}
	}
	
	/**
	 * 获取下一次提醒时间
	 */
	private void getNextRemindTime() {
		// 获取重复类型
		String repeatType = remindEntity.getRepeatType();
		// 计算下一次响铃时间
		// 设置闹铃
	}

	/**
	 * 发送反馈信息
	 */
	private void sendBackMsg() {
		
	}
	
	/**
	 * 在锁屏下点亮屏幕
	 */
	@SuppressWarnings("deprecation")
	private void getWakeLock() {
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.SCREEN_DIM_WAKE_LOCK, "Remind");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (null == mWakelock) {
			getWakeLock();
		}
		mWakelock.acquire();
	}

	@Override
	protected void onPause() {
		mWakelock.release();
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.alert_other:
			// 其他
			
			break;

		default:
			break;
		}
	}

}
