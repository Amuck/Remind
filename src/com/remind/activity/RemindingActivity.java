package com.remind.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.help.remind.R;
import com.remind.dao.RemindDao;
import com.remind.dao.impl.RemindDaoImpl;
import com.remind.entity.RemindEntity;
import com.remind.global.AppConstant;
import com.remind.http.HttpClient;
import com.remind.util.AppUtil;
import com.remind.util.DataBaseParser;
import com.remind.view.RoleDetailImageView;

/**
 * @author ChenLong
 *
 *	闹铃提醒界面
 */
public class RemindingActivity extends BaseActivity implements OnClickListener {
	public static final int ALARM_STATUS_DELETE = 0x01;
	public static final int ALARM_STATUS_CLOSE = 0x02;

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
	 * 姓名
	 */
	private TextView name;
	/**
	 * 时间
	 */
	private TextView time;
	
	/**
	 * 头像
	 */
	private RoleDetailImageView img;
	/**
	 * 图片
	 */
	private ImageView contentImg;
	/**
	 * 已经开始
	 */
	private TextView startBtn;
	/**
	 * 马上开始
	 */
	private TextView readyBtn;
	/**
	 * 10分钟后提醒
	 */
	private TextView laterBtn;
	/**
	 * 其他
	 */
	private TextView otherBtn;
	
	/**
	 * 删除
	 */
	private TextView deleteBtn;
	/**
	 * 关闭
	 */
	private TextView closeBtn;

	private LinearLayout alertPenal;
	private LinearLayout otherPenal;
	/**
	 * 提醒的id
	 */
	private int remindId;
	private RemindDao remindDao;
	private RemindEntity remindEntity = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 在锁屏上显示
		win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		setContentView(R.layout.new_activity_alert);
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
		contentImg = (ImageView) findViewById(R.id.alert_img);
		startBtn = (TextView) findViewById(R.id.start);
		readyBtn = (TextView) findViewById(R.id.already);
		laterBtn = (TextView) findViewById(R.id.later);
		otherBtn = (TextView) findViewById(R.id.other);
		deleteBtn = (TextView) findViewById(R.id.delete);
		closeBtn = (TextView) findViewById(R.id.close);
		alertPenal = (LinearLayout) findViewById(R.id.alert_panel);
		otherPenal = (LinearLayout) findViewById(R.id.other_panel);
		
		title = (TextView) findViewById(R.id.remind_title_txt);
		content = (TextView) findViewById(R.id.remind_content_txt);
		name = (TextView) findViewById(R.id.remind_name_txt);
		time = (TextView) findViewById(R.id.remind_time_txt);
		img = (RoleDetailImageView) findViewById(R.id.remind_img);

		remindDao = new RemindDaoImpl(this);
		getRemindData();

		otherBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		closeBtn.setOnClickListener(this);
		startBtn.setOnClickListener(this);
		readyBtn.setOnClickListener(this);
		laterBtn.setOnClickListener(this);
	}

	private void changePanelState() {
		if (alertPenal.getVisibility() == View.VISIBLE) {
			alertPenal.setVisibility(View.GONE);
			otherPenal.setVisibility(View.VISIBLE);
		} else {
			alertPenal.setVisibility(View.VISIBLE);
			otherPenal.setVisibility(View.GONE);
		}
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
			cursor.close();
			// 初始化页面数据
			title.setText(remindEntity.getTitle());
			content.setText(remindEntity.getContent());
			name.setText(remindEntity.getNickName());
			time.setText(remindEntity.getRemindTime().split(" ")[1]);
		} catch (Exception e) {
			AppUtil.showToast(this, "播放提醒失败");
			finish();
		}
	}

	/**
	 * 
	 * 获取下一次提醒时间
	 * @param minute		下次响铃时间间隔分钟，0为没有变化
	 * @param hour			下次响铃时间间隔小时，0为没有变化
	 * @param day			下次响铃时间间隔天，0为没有变化
	 * @param month			下次响铃时间间隔月，0为没有变化
	 * @param year			下次响铃时间间隔年，0为没有变化
	 * @param status		选择动作 小于10失效，大于10有效
	 * 						1: 关闭; 2: 已开始; 3: 马上开始; 4: 延迟10分钟
	 */
	private void getNextRemindTime(int minute, int hour, int day, int month, int year, int status) {
		int isEnable = 10;
		// 获取本次响铃时间
		Calendar calendar = Calendar.getInstance();
		String remindTime = remindEntity.getRemindTime();
		try {
			calendar.setTime(dateFormat.parse(remindTime));
		} catch (ParseException e) {
			AppUtil.showToast(this, "时间错误，接受任务失败。");
			e.printStackTrace();
			return;
		}
		
		// 获取重复类型
		String repeatType = remindEntity.getRepeatType();
		// 计算下一次响铃时间
		if (RemindEntity.REPEAT_NO.equals(repeatType)) {
			if ((minute | hour | day | month | year) == 0) {
				// 不再有下一次提醒
				remindEntity.setIsDelete(RemindEntity.DELETED);
				sendAlarmStatus(status);
				return;
			} else {
				// 不重复提醒，则按照下次响铃时间间隔设置
				calendar.add(Calendar.MINUTE, minute);
				calendar.add(Calendar.HOUR_OF_DAY, hour);
				calendar.add(Calendar.DAY_OF_YEAR, day);
				calendar.add(Calendar.MONTH, month);
				calendar.add(Calendar.YEAR, year);
			}
		} else if (RemindEntity.REPEAT_DAY.equals(repeatType)) {
			// 每天重复提醒，下一次为明天
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		} else if (RemindEntity.REPEAT_WEEK.equals(repeatType)) {
			// 每周重复，下一次为下周周几
			calendar.add(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
		} else if (RemindEntity.REPEAT_MONTH.equals(repeatType)) {
			// 每月重复,下一次为下月本号, 如果下个月没有本号，则为月末那天
			calendar.add(Calendar.MONTH, 1);
		} else if (RemindEntity.REPEAT_YEAR.equals(repeatType)) {
			// 每年提醒，下一次为明年这个日期
			calendar.add(Calendar.YEAR, 1);
		}
		
		remindEntity.setRemindTimeMiLi(calendar.getTimeInMillis() + "");
		remindEntity.setRemindTime(dateFormat.format(calendar.getTime()));
		// 设置闹铃
		AppUtil.setAlarm(this, remindEntity.getRemindTime(), Integer.valueOf(remindEntity.getId()));
		
		sendAlarmStatus(isEnable + status);
	}

	/**
	 * 发送反馈信息
	 */
	private void sendAlarmStatus(int status) {
		final String params = HttpClient.getJsonForPost(HttpClient.alarmStatus(remindEntity.getNoticeId(), status,
				AppConstant.FROM_ID, remindEntity.getOwnerId()));
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String s = HttpClient.post(HttpClient.url + HttpClient.alarm_statues, params);
				if (s == null) {
					// TODO 失败插入数据库，重发
					
				} else {

				}
			}
		}).start();
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
		case R.id.other:
			// 其他
			changePanelState();
			break;
		case R.id.delete:
			// 删除
			closeRemind();
			break;
		case R.id.close:
			// 关闭
			getNextRemindTime(0, 0, 0, 0, 0, 1);
			closeRemind();
			break;
		case R.id.start:
			// 已开始
			getNextRemindTime(0, 0, 0, 0, 0, 2);
			closeRemind();
			break;
		case R.id.already:
			// 马上开始
			getNextRemindTime(0, 0, 0, 0, 0, 3);
			closeRemind();
			break;
		case R.id.later:
			// 延迟10分钟
			getNextRemindTime(10, 0, 0, 0, 0, 4);
			closeRemind();
			break;

		default:
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
		
	}
	
	/**
	 * 关闭闹铃
	 */
	private void closeRemind() {
		int remindCount = remindEntity.getRemindCount();
		// 响铃次数加一
		remindEntity.setRemindCount(++remindCount);
		remindDao.updateRemind(remindEntity);
		finish();
	}

}
