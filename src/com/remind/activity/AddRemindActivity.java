package com.remind.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.AbConstant;
import com.ab.util.AbDateUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.wheel.AbWheelUtil;
import com.ab.view.wheel.AbWheelView;
import com.help.remind.R;
import com.remind.adapter.SelectPeopelAdapter;
import com.remind.adapter.SelectPeopelAdapter.PeopelSelected;
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
import com.remind.util.AppUtil;
import com.remind.util.DataBaseParser;
import com.remind.util.NetWorkUtil;
import com.remind.view.SwitchButton;
import com.remind.view.SwitchButton.OnSwitchListener;

/**
 * 添加提醒
 * 
 * @author ChenLong
 *
 */
public class AddRemindActivity extends BaseActivity implements OnClickListener {
	private final static int HTTP_OVER = 0;
	private final static int NOTIFY_SUCCESS = 1;
	private final static int NOTIFY_FAILE = 2;
	/**
	 * 提醒对象
	 */
	private RemindEntity remindEntity;
	/**
	 * 聊天内容
	 */
	private MessageEntity messageEntity;
	/**
	 * 选择联系人按钮
	 */
	private TextView selectPeopelBtn;
	/**
	 * 选择日期按钮
	 */
	private TextView selectDateBtn;
	/**
	 * 选择时间按钮
	 */
	private TextView selectTimeBtn;
	/**
	 * 选择主题按钮
	 */
	private TextView selectTitleBtn;
	/**
	 * 选择形式按钮
	 */
	private TextView selectTypeBtn;
	/**
	 * 选择是否预览按钮
	 */
	private SwitchButton switchButton;
	/**
	 * 是否预览
	 */
	private int isPreview = RemindEntity.NOT_PREV;
	/**
	 * 确定按钮
	 */
	private Button okBtn;
	/**
	 * 取消按钮
	 */
	private Button cancelBtn;
	/**
	 * 选择联系人姓名
	 */
//	private TextView selectPeopelTxt;
	/**
	 * 选择日期
	 */
//	private TextView selectDateTxt;
	/**
	 * 选择时间
	 */
//	private TextView selectTimeTxt;
	/**
	 * 更多设置
	 */
	private TextView moreBtn;
	/**
	 * 提醒内容
	 */
	private EditText contentEidt;
	/**
	 * 提醒标题
	 */
	private EditText titleEidt;

	/**
	 * 选择联系人对话框
	 */
	private AlertDialog alertDialog;
	private PeopelDao peopelDao;
	private RemindDao remindDao;
	private MessageDao messageDao;
	/**
	 * 当前选择联系人信息，即接收本条消息的用户
	 */
	private PeopelEntity targetPeopel;

	/**
	 * 登陆用户
	 */
	private PeopelEntity user;
	/**
	 * 日期选择
	 */
	private View mDateView = null;
	/**
	 * 时间选择
	 */
	private View mTimeView = null;
	
	/**
	 * 是否是给自己的
	 */
	private boolean isForSelf = false;
	
	/**
	 * 设置重复周期
	 */
	private RadioGroup repeatGroup;
	/**
	 * 只响一次
	 */
	private RadioButton repeatNoBtn;
	/**
	 * 每天重复
	 */
	private RadioButton repeatDayBtn;
	/**
	 * 每周重复
	 */
	private RadioButton repeatWeekBtn;
	/**
	 * 每月重复
	 */
	private RadioButton repeatMonthBtn;
	/**
	 * 每年重复
	 */
	private RadioButton repeatYearBtn;
	
	/**
	 * 插入的提醒的id
	 */
	private long remindId;
	/**
	 * 插入的消息的id
	 */
	private long msgId;
	/**
	 * 重复类型,默认为只响一次
	 */
	private String repeatType = RemindEntity.REPEAT_NO;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case HTTP_OVER:
				String s = (String) msg.obj;
				if (null == s || !s.contains("|")) {
					Toast.makeText(AddRemindActivity.this, "网络连接失败，请确认后重试.",
							Toast.LENGTH_SHORT).show();
				}
				
				String[] ss = s.split("\\|");
				if (!ss[0].equals("200")) {
					// 失败
					removeProgressDialog();
					Toast.makeText(AddRemindActivity.this, "失败，请重试",
							Toast.LENGTH_SHORT).show();
				} else {
					// 成功
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(ss[1]);
						remindEntity.setNoticeId(jsonObject.getString("id"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(NOTIFY_SUCCESS);
				}
				
				break;
			case NOTIFY_SUCCESS:
				// 成功
				// 加入到remind数据库
				remindId = remindDao.insertRemind(remindEntity);
				// 插入数据库
				String id = String.valueOf(remindId);
				messageEntity.setOtherTypeId(String.valueOf(remindId));
				messageEntity.setRemindId(id);
				messageEntity.setSendState(MessageEntity.SEND_SUCCESS);
				msgId = messageDao.insert(messageEntity);
				Intent intent = new Intent(AddRemindActivity.this, ChatActivity.class);
				remindEntity.setId(id);
				intent.putExtra("remind", remindEntity);
				
				if (isForSelf) {
					// TODO 测试为自己添加任务, addSelf()需要去掉，isForSelf需要重新取值，将当前currentPeopel.getNum()与AppUtil.getPhoneNumber(this)比较，相同则为true
					AppUtil.setAlarm(AddRemindActivity.this, remindEntity.getRemindTime(), (int) remindId);
				}
				
				setResult(RESULT_OK, intent);
				
				removeProgressDialog();

				finish();
				break;
			case NOTIFY_FAILE:
				// 失败
				Toast.makeText(AddRemindActivity.this, "失败，请重试",
						Toast.LENGTH_SHORT).show();
				removeProgressDialog();
				break;

			}

		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setAbContentView(R.layout.activity_add_remind);
		setNotitle();
		peopelDao = new PeopelDaoImpl(this);
		remindDao = new RemindDaoImpl(this);
		messageDao = new MessageDaoImpl(this);

		setUpView();
	}

	private void setUpView() {
//		okBtn = (Button) findViewById(R.id.title_ok);
//		cancelBtn = (Button) findViewById(R.id.title_cancel);
		selectPeopelBtn = (TextView) findViewById(R.id.select_peopel_btn);
		selectDateBtn = (TextView) findViewById(R.id.select_date_btn);
		selectTimeBtn = (TextView) findViewById(R.id.select_time_btn);
		selectTitleBtn = (TextView) findViewById(R.id.select_title_btn);
		selectTypeBtn = (TextView) findViewById(R.id.select_type_btn);
		switchButton = (SwitchButton) findViewById(R.id.select_preview_btn);

//		selectPeopelTxt = (TextView) findViewById(R.id.select_peopel_text);
//		selectDateTxt = (TextView) findViewById(R.id.select_date_text);
//		selectTimeTxt = (TextView) findViewById(R.id.select_time_text);
		moreBtn = (TextView) findViewById(R.id.select_more_btn);

//		contentEidt = (EditText) findViewById(R.id.set_content_edit);
//		titleEidt = (EditText) findViewById(R.id.set_title_edit);
//
//		repeatGroup = (RadioGroup) findViewById(R.id.repeat_group);
//		repeatNoBtn = (RadioButton) findViewById(R.id.repeat_no_btn);
//		repeatDayBtn = (RadioButton) findViewById(R.id.repeat_day_btn);
//		repeatWeekBtn = (RadioButton) findViewById(R.id.repeat_week_btn);
//		repeatMonthBtn = (RadioButton) findViewById(R.id.repeat_month_btn);
//		repeatYearBtn = (RadioButton) findViewById(R.id.repeat_year_btn);
		
		mDateView = mInflater.inflate(R.layout.choose_three, null);
		mTimeView = mInflater.inflate(R.layout.choose_two, null);

		selectDateBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(AbConstant.DIALOGBOTTOM, mDateView, 40);
//				AbDialogUtil.showDialog(mDateView,Gravity.BOTTOM);
			}

		});

		selectTimeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(AbConstant.DIALOGBOTTOM, mTimeView, 40);
//				AbDialogUtil.showDialog(mTimeView,Gravity.BOTTOM);
			}

		});

		switchButton.setImageResource(R.drawable.preview_bord, R.drawable.preview_btn);
		switchButton.setOnSwitchStateListener(new OnSwitchListener() {
			
			@Override
			public void onSwitched(boolean state) {
				isPreview = state ? 1 : 0;
			}
		});

		initWheelDate(mDateView, selectDateBtn);
		initWheelTime(mTimeView, selectTimeBtn);

//		okBtn.setOnClickListener(this);
//		cancelBtn.setOnClickListener(this);
		selectPeopelBtn.setOnClickListener(this);
		selectTitleBtn.setOnClickListener(this);
		selectTypeBtn.setOnClickListener(this);
		moreBtn.setOnClickListener(this);
//		repeatGroup.setOnCheckedChangeListener(repeatCheckListener);
		
		Cursor cursor = peopelDao.queryOwner();
		ArrayList<PeopelEntity> entities = DataBaseParser.getPeoPelDetail(cursor);
		cursor.close();
		if (null == entities || entities.size() <= 0) {
			user = addSelf();
		} else {
			user = entities.get(0);
		}
		
		PeopelEntity entity = (PeopelEntity) getIntent().getSerializableExtra("targetPeopel");
		if (null != entity) {
			targetPeopel = entity;
			selectPeopelBtn.setText(getName(entity));
			selectPeopelBtn.setBackgroundResource(R.drawable.right_board);
		}
	}

	/**
	 * 监听重复周期改变
	 */
//	private OnCheckedChangeListener repeatCheckListener = new OnCheckedChangeListener() {
//
//		@Override
//		public void onCheckedChanged(RadioGroup group, int checkedId) {
//			switch (checkedId) {
//			case R.id.repeat_no_btn:
//				// 只响一次
//				repeatType = RemindEntity.REPEAT_NO;
//				break;
//			case R.id.repeat_day_btn:
//				// 每天重复
//				repeatType = RemindEntity.REPEAT_DAY;
//				break;
//			case R.id.repeat_week_btn:
//				// 每周重复
//				repeatType = RemindEntity.REPEAT_WEEK;
//				break;
//			case R.id.repeat_month_btn:
//				// 每月重复
//				repeatType = RemindEntity.REPEAT_MONTH;
//				break;
//			case R.id.repeat_year_btn:
//				// 每年重复
//				repeatType = RemindEntity.REPEAT_YEAR;
//				break;
//
//			default:
//				break;
//			}
//		}
//	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_peopel_btn:
			// 选择联系人
//			Intent intentP = new Intent(AddRemindActivity.this, ContactsActivity.class);
//			startActivity(intentP);
			
			openPeopelDig();
			break;
		case R.id.select_title_btn:
			// 选择主题
			break;
		case R.id.select_type_btn:
			// 选择形式
//			new AlertDialog.Builder(this).setTitle("是否上传")
//			.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					Upload.upload(AddRemindActivity.this, "test.jpg",
//							new CompleteListener() {
//						@Override
//						public void result(boolean isComplete, String result, String error) {
//							// do something...
////							System.out.println("isComplete:"+isComplete+";result:"+result+";error:"+error);
//							Log.e("Upload_CompleteListener", "isComplete:"+isComplete+";result:"+result+";error:"+error);
//						}
//					},
//					new ProgressListener() {
//						@Override
//						public void transferred(long transferedBytes, long totalBytes) {
//							// do something...
////							System.out.println("trans:" + transferedBytes + "; total:" + totalBytes);
//							Log.e("Upload_ProgressListener", "trans:" + transferedBytes + "; total:" + totalBytes);
//						}
//					});
//				}
//			})
//			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//				}
//			}).create().show();
			break;
		case R.id.select_more_btn:
			// 更多
//			break;
		case R.id.title_ok:
			// 确认
			if (null == targetPeopel) {
				AppUtil.showToast(this, "请选择联系人");
				break;
			}
//			if (null == selectDateTxt.getText()) {
//				AppUtil.showToast(this, "请选择日期");
//				break;
//			}
//			if (null == selectTimeTxt.getText()) {
//				AppUtil.showToast(this, "请选择时间");
//				break;
//			}
//			if (null == contentEidt.getText()) {
//				AppUtil.showToast(this, "请填写提醒内容");
//				break;
//			}
//			if (null == titleEidt.getText()) {
//				AppUtil.showToast(this, "请填写提醒标题");
//				break;
//			}

			createNotify();
			
			break;
		case R.id.title_cancel:
			// 取消
			setResult(RESULT_CANCELED);
			finish();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 注册
	 */
	private void createNotify() {
		String remindTime = selectDateBtn.getText().toString() + " "
				+ selectTimeBtn.getText().toString();
		
		String remindTimeMili = System.currentTimeMillis() + "";
		remindEntity = new RemindEntity("", AppConstant.USER_NUM,
				targetPeopel.getNum(), targetPeopel.getName(),
				targetPeopel.getNickName(), AppUtil.getNowTime(),
				remindTimeMili, "一起去吃饭吧", remindTime
				, remindTime, selectTitleBtn
						.getText().toString(), repeatType, isPreview, "", "", RemindEntity.READ);
		
		if (isForSelf) {
			// 如果为自己，设置为已接受的提醒
			remindEntity.setRemindState(RemindEntity.ACCEPT);
		} else {
			// 如果为别人，设置为我发起的提醒
			remindEntity.setRemindState(RemindEntity.LAUNCH);
		}

		// 加入到message数据库
		messageEntity = new MessageEntity("", targetPeopel.getName(), targetPeopel.getNum(), 
				user.getNickName(), AppConstant.USER_NUM, AppUtil.getNowTime(), 
				MessageEntity.SENDING, MessageEntity.NORMAL, MessageEntity.TYPE_REMIND, 
				"", "", targetPeopel.getNum(), MessageEntity.TYPE_SEND, remindEntity.getContent(), 
				MessageEntity.FEED_DEFAULT, AppConstant.USER_NUM, "");
		
		if (isForSelf) {
			// TODO 测试为自己添加任务, addSelf()需要去掉，isForSelf需要重新取值，将当前currentPeopel.getNum()与AppUtil.getPhoneNumber(this)比较，相同则为true
//			AppUtil.setAlarm(this, remindEntity.getRemindTime(), (int) remindId);
			
			handler.sendEmptyMessage(NOTIFY_SUCCESS);
		} else if(TextUtils.isEmpty(targetPeopel.getFriendId())) {
			// 未添加好友
			showToast("TA还不是您的好友，请等待对方验证通过后再尝试。");
		} else {
			// 为别人添加任务
			if (NetWorkUtil.isAvailable(AddRemindActivity.this)) {
				
				showProgressDialog();
				new Thread(new Runnable() {

					@Override
					public void run() {
						String params = HttpClient.getCreateNofiJsonForPost(
								targetPeopel.getFriendId(), AppConstant.FROM_ID, 
								remindEntity.getTitle(), remindEntity.getIsPreview() + "", 
								remindEntity.getRemindTime(), remindEntity.getRepeatType(),
								AppConstant.USER_NUM, remindEntity.getNickName(), remindEntity.getContent());
						String s = HttpClient.post(
								HttpClient.url + HttpClient.create_notify, params);
						
						Message msg = new Message();
						msg.what = HTTP_OVER;
						msg.obj = s;
						handler.sendMessage(msg);
					}
				}).start();
			} else {
				showToast(getResources().getString(R.string.net_null));
				// 更新发送状态
//				messageDao.updateSendState(msgId, MessageEntity.SEND_FAIL);
			}
		}
		
	}
	
	/**
	 * 将自己添加进数据库
	 */
	public PeopelEntity addSelf() {
		PeopelEntity myself = null;
		String num = AppUtil.getPhoneNumber(AddRemindActivity.this);
		Cursor cursor = peopelDao.queryPeopelByNum(num);
		if (cursor != null && cursor.getCount() > 0) {
			
		} else {
			myself = new PeopelEntity("自己", "自己", AppConstant.USER_NUM, "", "", "", PeopelEntity.NORMAL, 
					PeopelEntity.FRIEND, AppConstant.FROM_ID, AppConstant.USER_NUM);
			peopelDao.insertPeopel(myself);
		}
		cursor.close();
		return myself;
	}

	/**
	 * 打开选择联系人对话框
	 */
	private void openPeopelDig() {
		// 设置对话框中的view
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		TextView textView = new TextView(this);
		textView.setHeight(AppUtil.dip2px(this, 80));
		textView.setText("自己");
		textView.setTextSize(AppUtil.dip2px(this, 24));
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setTextColor(Color.BLACK);
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isForSelf = true;
				targetPeopel = new PeopelEntity("自己", "自己", AppUtil.getPhoneNumber(AddRemindActivity.this), 
						"", "", "", PeopelEntity.NORMAL, PeopelEntity.FRIEND, AppConstant.FROM_ID, AppConstant.USER_NUM);
//				selectPeopelTxt.setText(getName(currentPeopel));
				alertDialog.dismiss();
			}
		});
		
		ListView listView = new ListView(this);
		List<PeopelEntity> datas = getPeopel();
		
//		layout.addView(textView);
		layout.addView(listView);
		
		if (null == datas || datas.size() == 0) {
			AppUtil.showToast(this, "您还没有联系人，快去添加一个吧！");
			return;
		}
		
		SelectPeopelAdapter adapter = new SelectPeopelAdapter(this, datas);
		listView.setAdapter(adapter);

		alertDialog = new AlertDialog.Builder(this).setTitle("请选择联系人")
				.setView(layout)
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
//						alertDialog.dismiss();
					}
				}).setPositiveButton("添加联系人", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(AddRemindActivity.this, MainActivity1.class));
//						alertDialog.dismiss();
					}
				}).create();

		adapter.setPeopelSelected(new PeopelSelected() {

			@Override
			public void onPeopelSelected(int position, PeopelEntity entity) {
				// 设置选中联系人操作
				if (entity.getNum().equals(AppConstant.USER_NUM)) {
					isForSelf = true;
				} else {
					isForSelf = false;
				}
				
				
				targetPeopel = entity;
				selectPeopelBtn.setText(getName(entity));
				selectPeopelBtn.setBackgroundResource(R.drawable.right_board);
				alertDialog.dismiss();
			}
		});

		alertDialog.show();
	}

	/**
	 * 将数据库中联系人取出
	 * 
	 * @return
	 */
	private List<PeopelEntity> getPeopel() {
		List<PeopelEntity> results = new ArrayList<PeopelEntity>();
		Cursor cursor = peopelDao.queryPeopel();
		results = DataBaseParser.getPeoPelDetail(cursor);
		cursor.close();
		return results;
	}

	/**
	 * 获取联系人显示名称
	 * 
	 * @param entity
	 * @return
	 */
	private String getName(PeopelEntity entity) {
		String name = entity.getNickName();
		if (null == name || name.trim().length() <= 0) {
			// 无备注名称则显示联系人名称
			name = entity.getName();
		}
		if (null == name || name.trim().length() <= 0) {
			name = "佚名";
		}
		return name;
	}

	/**
	 * 初始化时间选择
	 * @param mTimeView
	 * @param mText
	 */
	public void initWheelTime(View mTimeView, TextView mText) {
		final AbWheelView mWheelViewMM = (AbWheelView)mTimeView.findViewById(R.id.wheelView1);
		final AbWheelView mWheelViewHH = (AbWheelView)mTimeView.findViewById(R.id.wheelView2);
		Button okBtn = (Button)mTimeView.findViewById(R.id.okBtn);
		Button cancelBtn = (Button)mTimeView.findViewById(R.id.cancelBtn);
		mWheelViewMM.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
		mWheelViewHH.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        AbWheelUtil.initWheelTimePicker2(this, mText,mWheelViewMM, mWheelViewHH,okBtn,cancelBtn,1,1,true);
	}

	/**
	 * 初始化日期选择
	 * @param mDateView
	 * @param mText
	 */
	public void initWheelDate(View mDateView, TextView mText) {
		//年月日时间选择器
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);
        String date =  mText.getText().toString().trim();
        if(!AbStrUtil.isEmpty(date)){
        	Date dateNew = AbDateUtil.getDateByFormat(date, AbDateUtil.dateFormatYMD);
        	if(dateNew!=null){
        		year = 1900+dateNew.getYear();
        		month = dateNew.getMonth()+1;
        		day = dateNew.getDate();
        	}
        }
        
		final AbWheelView mWheelViewY = (AbWheelView)mDateView.findViewById(R.id.wheelView1);
		final AbWheelView mWheelViewM = (AbWheelView)mDateView.findViewById(R.id.wheelView2);
		final AbWheelView mWheelViewD = (AbWheelView)mDateView.findViewById(R.id.wheelView3);
		Button okBtn = (Button)mDateView.findViewById(R.id.okBtn);
		Button cancelBtn = (Button)mDateView.findViewById(R.id.cancelBtn);
		mWheelViewY.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
		mWheelViewM.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
		mWheelViewD.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
		AbWheelUtil.initWheelDatePicker(this, mText, mWheelViewY, mWheelViewM, mWheelViewD,okBtn,cancelBtn, year,month,day, year, 120, false);
	}
}
