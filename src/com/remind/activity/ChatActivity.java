package com.remind.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.help.remind.R;
import com.remind.adapter.ChatAdapter;
import com.remind.adapter.ChatAdapter.ContentClickListener;
import com.remind.dao.MessageDao;
import com.remind.dao.MessageIndexDao;
import com.remind.dao.RemindDao;
import com.remind.dao.impl.MessageDaoImpl;
import com.remind.dao.impl.MessageIndexDaoImpl;
import com.remind.dao.impl.RemindDaoImpl;
import com.remind.entity.MessageEntity;
import com.remind.entity.MessageIndexEntity;
import com.remind.entity.RemindEntity;
import com.remind.global.AppConstant;
import com.remind.record.SoundMeter;
import com.remind.util.AppUtil;
import com.remind.util.DataBaseParser;

/**
 * @author ChenLong
 * 
 *         聊天界面
 */
public class ChatActivity extends BaseActivity implements OnClickListener, OnScrollListener{
	/**
	 * 添加一个提醒
	 */
	public final static int ADD_REMIND = 3002;
	/**
	 * 聊天信息
	 */
	private ListView chatList;
	private ChatAdapter chatAdapter;
	/**
	 * 发送消息按钮
	 */
	private Button sendMsgBtn;
	/**
	 * 发送提醒按钮
	 */
	private Button sendRemindBtn;
	/**
	 * 消息编辑框
	 */
	private EditText sendMsgEidt;
	/**
	 * 联系人名称
	 */
	private TextView contactName;

	/**
	 * 联系人信息
	 */
	private ImageButton contactInfo;
	/**
	 * 联系人
	 */
	private MessageIndexEntity messageIndexEntity = null;
	
	private MessageIndexDao messageIndexDao;
	private MessageDao messageDao;
	private RemindDao remindDao;
	/**
	 * 用户消息
	 */
	private MessageEntity userMessageEntity;
	
	/**
	 * 联系人消息
	 */
	private MessageEntity contactMessageEntity;
	
	/**
	 * 聊天数据
	 */
	private List<MessageEntity> datas = new ArrayList<MessageEntity>();
	/**
	 * 正在加载提示
	 */
	private ProgressBar loadInfo;
	private LinearLayout loadLayout;
	/**
	 * 默认在第一页
	 */
	private int currentPage = 1;
    /**
     * 每次显示数 
     */
    private static final int lineSize =10;
    /**
     * 全部记录数
     */
    private int allRecorders = 0;
    /**
     * 默认共一页
     */
    private int pageSize = 1;
    private int firstItem=-1;
    
    /**
     * 录音按钮
     */
    private ImageView chatting_mode_btn, volume;
    private boolean btn_vocie = false;
    private ImageView img1, sc_img1;
	private SoundMeter mSensor;
	private View rcChat_popup;
	private LinearLayout del_re;
	private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
	voice_rcd_hint_tooshort;
	private RelativeLayout mBottom;
	private TextView mBtnRcd;
	private int flag = 1;
	private boolean isShosrt = false;
	private String voiceName;
	private long startVoiceT, endVoiceT;
	private Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		messageIndexDao = new MessageIndexDaoImpl(this);
		messageDao = new MessageDaoImpl(this);
		remindDao = new RemindDaoImpl(this);

		Intent intent = getIntent();
		String num = intent.getStringExtra("num");
		Cursor cursor = messageIndexDao.queryByNum(num);
//		if (cursor.getCount() > 0) {
			ArrayList<MessageIndexEntity> messageIndexEntities = DataBaseParser.getMessageIndex(cursor);
			messageIndexEntity = messageIndexEntities.get(0);
//		} else {
//			messageIndexEntity = null;
//		}
			cursor.close();
		
		if (null == messageIndexEntity) {
			AppUtil.showToast(this, "聊天窗口打开失败，请重试");
			finish();
		}
		
		initView();
		initMessageEntity();
		
		setResult(RESULT_OK);
	}

	private void initView() {
		chatList = (ListView) findViewById(R.id.chat_list);
		sendMsgBtn = (Button) findViewById(R.id.send_msg);
		sendRemindBtn = (Button) findViewById(R.id.add_remind);
		sendMsgEidt = (EditText) findViewById(R.id.msg_edit);
		contactName = (TextView) findViewById(R.id.title_text);
		contactInfo = (ImageButton) findViewById(R.id.title_info);
		initRecord();
		contactName.setText(messageIndexEntity.getName());
		sendMsgBtn.setOnClickListener(this);
		sendRemindBtn.setOnClickListener(this);
		contactInfo.setOnClickListener(this);
		
		setupChatList();
	}
	
	/**
	 * 初始化录音相关
	 */
	private void initRecord() {
		chatting_mode_btn = (ImageView) this.findViewById(R.id.ivPopUp);
		volume = (ImageView) this.findViewById(R.id.volume);
		rcChat_popup = this.findViewById(R.id.rcChat_popup);
		img1 = (ImageView) this.findViewById(R.id.img1);
		mBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
		mBtnRcd = (TextView) findViewById(R.id.btn_rcd);
		sc_img1 = (ImageView) this.findViewById(R.id.sc_img1);
		del_re = (LinearLayout) this.findViewById(R.id.del_re);
		voice_rcd_hint_rcding = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_rcding);
		voice_rcd_hint_loading = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_loading);
		voice_rcd_hint_tooshort = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_tooshort);
		mSensor = new SoundMeter();
		
		//语音文字切换按钮
		chatting_mode_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (btn_vocie) {
					mBtnRcd.setVisibility(View.GONE);
					mBottom.setVisibility(View.VISIBLE);
					btn_vocie = false;
					chatting_mode_btn
							.setImageResource(R.drawable.chatting_setmode_msg_btn);

				} else {
					mBtnRcd.setVisibility(View.VISIBLE);
					mBottom.setVisibility(View.GONE);
					chatting_mode_btn
							.setImageResource(R.drawable.chatting_setmode_voice_btn);
					btn_vocie = true;
				}
			}
		});
		mBtnRcd.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				//按下语音录制按钮时返回false执行父类OnTouch
				return false;
			}
		});
	}
	
	/**
	 * 初始化消息实例, 需要更新的数据为：时间，发送状态，发送消息的类型，非文字信息的消息id与路径，发送的内容
	 */
	private void initMessageEntity() {
		// TODO 发送状态的改变
		userMessageEntity = new MessageEntity("", messageIndexEntity.getName(), 
				messageIndexEntity.getNum(), AppUtil.getUserName(),
				AppUtil.getPhoneNumber(this), AppUtil.getNowTime(), 
				MessageEntity.SEND_SUCCESS, MessageEntity.NORMAL, 
				MessageEntity.TYPE_TEXT, "", "", messageIndexEntity.getId(),
				MessageEntity.TYPE_SEND, "");
		
		contactMessageEntity = new MessageEntity("", AppUtil.getUserName(),
				AppUtil.getPhoneNumber(this), messageIndexEntity.getName(), 
				messageIndexEntity.getNum(), AppUtil.getNowTime(), 
				MessageEntity.SEND_SUCCESS, MessageEntity.NORMAL, 
				MessageEntity.TYPE_TEXT, "", "", messageIndexEntity.getId(),
				MessageEntity.TYPE_RECIEVE, "");
	}
	
	/**
	 * 初始化消息记录
	 */
	private void setupChatList() {
		//创建一个角标线性布局用来显示正在加载
        loadLayout = new LinearLayout(this);
        loadLayout.setGravity(LinearLayout.HORIZONTAL);
        loadLayout.setGravity(Gravity.CENTER);
        //定义一个ProgressBar表示“正在加载”
        loadInfo = new ProgressBar(this,null,android.R.attr.progressBarStyleSmall);
        //增加组件 
        LayoutParams layoutParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.height = AppUtil.dip2px(this, 20);
        loadLayout.addView(loadInfo, layoutParams);
        //增加到listView头部
        chatList.addHeaderView(loadLayout);
        chatList.setOnScrollListener(this);
		
		getData();
		chatAdapter = new ChatAdapter(this, datas);
		chatAdapter.setContentClickListener(new ContentClickListener() {
			
			@Override
			public void onContentLongClick(int position, MessageEntity chatMessage) {
				if (MessageEntity.TYPE_REMIND.equals(chatMessage.getMsgType())) {
					RemindEntity entity = new RemindEntity();
					entity.setId(chatMessage.getOtherTypeId());
					Cursor cursor = remindDao.queryRemind(entity);
					entity = DataBaseParser.getRemindDetail(cursor).get(0);
					cursor.close();
					// 提醒类型，打开提醒
					Intent intent = new Intent(ChatActivity.this, RemindDetailActivity.class);
					intent.putExtra("remind", entity);
					startActivity(intent);
				}
			}
			
			@Override
			public void onContentClick(int position, MessageEntity chatMessage) {
				AppUtil.showToast(ChatActivity.this, chatMessage.getContent());
			}
		});
		
		chatList.setAdapter(chatAdapter);
		chatList.setSelection(ListView.FOCUS_DOWN);
		
		// 判断，如果总条数小于10则不显示正在加载
		if (allRecorders <= 10) {
			chatList.removeHeaderView(loadLayout);
		}
	}
	
	/**
	 * 查询消息数据
	 */
	private void getData() {
		allRecorders = messageDao.getCount(Integer.valueOf(messageIndexEntity.getId()));
        //计算总页数
        pageSize = (allRecorders + lineSize -1) / lineSize;  
		datas.clear();
		ArrayList<MessageEntity> items = messageDao.getMsgByPage(currentPage, lineSize, messageIndexEntity.getId());
		// 倒序
		Collections.reverse(items);
		datas.addAll(items);
		
//		Cursor cursor = messageDao.query(messageIndexEntity.getId());
//		datas.clear();
//		datas.addAll(DataBaseParser.getMessage(cursor));
//		cursor.close();
	}
	
	/**
	 * 发送消息
	 */
	private void sendMsg() {
		String msg = sendMsgEidt.getText().toString();
		
		String time = AppUtil.getNowTime();
		// TODO 发送状态的改变
		// 更新MessageIndexEntity内的数据
		messageIndexEntity.setMessage(msg);
		messageIndexEntity.setTime(time);
		messageIndexEntity.setSendState(MessageIndexEntity.SEND_SUCCESS);
		messageIndexEntity.setIsDelete(MessageIndexEntity.NORMAL);
		// 需要更新的数据为：时间，发送状态，发送消息的类型，非文字信息的消息id与路径，发送的内容
		userMessageEntity.setTime(time);
		userMessageEntity.setContent(msg);
		userMessageEntity.setMsgType(MessageEntity.TYPE_TEXT);
		userMessageEntity.setOtherTypeId("");
		userMessageEntity.setMsgPath("");
		// 插入数据库
		messageDao.insert(userMessageEntity);
		messageIndexDao.update(messageIndexEntity);
		// 添加到listview中显示，通过线程发送
//		datas.add(userMessageEntity.clone());
//		chatAdapter.notifyDataSetChanged();
		chatAdapter.getNewMsg(userMessageEntity.clone(), chatList);
		// listview滚动到底部
//		chatList.setSelection(ListView.FOCUS_DOWN);
		// 清除编辑框内容
		sendMsgEidt.setText("");
	}
	
	/**
	 * 新消息来到
	 */
	public void newMsg() {
		String time = AppUtil.getNowTime();
		String msg = "知道了";
		// 更新MessageIndexEntity内的数据
		messageIndexEntity.setMessage(msg);
		messageIndexEntity.setTime(time);
		messageIndexEntity.setSendState(MessageIndexEntity.SEND_SUCCESS);
		messageIndexEntity.setIsDelete(MessageIndexEntity.NORMAL);
		// TODO 接受消息的类型
		// 需要更新的数据为：时间，发送状态，发送消息的类型，非文字信息的消息id与路径，发送的内容
		contactMessageEntity.setTime(time);
		contactMessageEntity.setContent(msg);
		contactMessageEntity.setMsgType(MessageEntity.TYPE_TEXT);
		contactMessageEntity.setOtherTypeId("");
		contactMessageEntity.setMsgPath("");
		// 插入数据库
		messageDao.insert(contactMessageEntity);
		messageIndexDao.update(messageIndexEntity);
		userMessageEntity.setMsgType(MessageEntity.TYPE_TEXT);
		
		// 添加到listview中显示
//		datas.add(userMessageEntity.clone());
//		chatAdapter.notifyDataSetChanged();
		chatAdapter.getNewMsg(contactMessageEntity.clone(), chatList);
		// listview滚动到底部
//		chatList.setSelection(ListView.FOCUS_DOWN);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send_msg:
			if (null == sendMsgEidt.getText() || sendMsgEidt.getText().toString().trim().length() <= 0) {
				AppUtil.showToast(this, "您还没有输入内容哦！");
				return;
			}
			// 发送消息
			sendMsg();
			// TODO 测试接受
			newMsg();
			break;
		case R.id.add_remind:
			// 添加提醒
			// 添加提醒
			Intent intent = new Intent(ChatActivity.this, AddRemindActivity.class);
			startActivityForResult(intent, ADD_REMIND);
			break;
		case R.id.title_info:
			// 联系人信息
			break;

		default:
			break;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (firstItem==0&& currentPage < pageSize && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			// 不再滚动
            currentPage++;
            // 增加数据
            appendDate();
        }
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		firstItem=firstVisibleItem;
	}
	
	/**
     * 增加数据
     */
    private void appendDate(){
		final ArrayList<MessageEntity> additems = messageDao.getMsgByPage(
				currentPage, lineSize, messageIndexEntity.getId());
		// 倒序
		Collections.reverse(additems);
		datas.addAll(0, additems);
		chatAdapter.setHistoryCount(chatAdapter.getHistoryCount()
				+ additems.size());
		// 判断，如果到了最末尾则去掉“正在加载”
		if (allRecorders == chatAdapter.getHistoryCount()) {
			chatList.removeHeaderView(loadLayout);
		}

        chatAdapter.notifyDataSetChanged();
        chatList.setSelection(additems.size());
         
    }
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case ADD_REMIND:
			// 发送提醒后，显示到界面上
			RemindEntity remindEntity = null;
			Bundle bundle = data.getExtras();
			Object extras = bundle.getSerializable("remind");
			if (null != extras && extras instanceof RemindEntity) {
				remindEntity = (RemindEntity) extras;
				setRemindMsg(remindEntity);
			} else {
				AppUtil.showToast(this, "发送提醒失败，请再次尝试");
			}
			
			break;
		default:
			break;
		}
	}
    
    /**
     * 发送提醒类的消息
     */
	private void setRemindMsg(RemindEntity remindEntity) {
		String msg = remindEntity.getTitle();

		String time = remindEntity.getAddTime();
		// TODO 发送状态的改变
		// 更新MessageIndexEntity内的数据
		messageIndexEntity.setMessage(msg);
		messageIndexEntity.setTime(time);
		messageIndexEntity.setSendState(MessageIndexEntity.SEND_SUCCESS);
		messageIndexEntity.setIsDelete(MessageIndexEntity.NORMAL);
		// 需要更新的数据为：时间，发送状态，发送消息的类型，非文字信息的消息id与路径，发送的内容
		userMessageEntity.setTime(time);
		userMessageEntity.setContent(msg);
		userMessageEntity.setMsgType(MessageEntity.TYPE_REMIND);
		userMessageEntity.setOtherTypeId(remindEntity.getId());
		userMessageEntity.setMsgPath("");
		// 插入数据库
		messageDao.insert(userMessageEntity);
		messageIndexDao.update(messageIndexEntity);
		// 添加到listview中显示，通过线程发送
		chatAdapter.getNewMsg(userMessageEntity.clone(), chatList);
		// 清除编辑框内容
		sendMsgEidt.setText("");
	}
	
	/**
	 * 发送语音
	 * 
	 * @param voiceTime		语音时长
	 * @param path			语音路径
	 */
	public void sendVoiceMsg(String voiceTime, String path) {
//		String msg = voiceTime;
		
		String time = AppUtil.getNowTime();
		// TODO 发送状态的改变
		// 更新MessageIndexEntity内的数据
		messageIndexEntity.setMessage("[语音]");
		messageIndexEntity.setTime(time);
		messageIndexEntity.setSendState(MessageIndexEntity.SEND_SUCCESS);
		messageIndexEntity.setIsDelete(MessageIndexEntity.NORMAL);
		// 需要更新的数据为：时间，发送状态，发送消息的类型，非文字信息的消息id与路径，发送的内容
		userMessageEntity.setTime(time);
		userMessageEntity.setContent(voiceTime);
		userMessageEntity.setMsgType(MessageEntity.TYPE_VOICE);
		userMessageEntity.setOtherTypeId("");
		userMessageEntity.setMsgPath(path);
		// 插入数据库
		messageDao.insert(userMessageEntity);
		messageIndexDao.update(messageIndexEntity);
		// 添加到listview中显示，通过线程发送
//		datas.add(userMessageEntity.clone());
//		chatAdapter.notifyDataSetChanged();
		chatAdapter.getNewMsg(userMessageEntity.clone(), chatList);
		// listview滚动到底部
//		chatList.setSelection(ListView.FOCUS_DOWN);
		// 清除编辑框内容
		sendMsgEidt.setText("");
	}
	
	//按下语音录制按钮时
		@Override
		public boolean onTouchEvent(MotionEvent event) {

			if (!Environment.getExternalStorageDirectory().exists()) {
				Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
				return false;
			}

			if (btn_vocie) {
//				System.out.println("1");
				int[] location = new int[2];
				mBtnRcd.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
				int btn_rc_Y = location[1];
				int btn_rc_X = location[0];
				int[] del_location = new int[2];
				del_re.getLocationInWindow(del_location);
				int del_Y = del_location[1];
				int del_x = del_location[0];
				if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
					if (!Environment.getExternalStorageDirectory().exists()) {
						Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
						return false;
					}
//					System.out.println("2");
					if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X) {
						//判断手势按下的位置是否是语音录制按钮的范围内
//						System.out.println("3");
						mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
						rcChat_popup.setVisibility(View.VISIBLE);
						voice_rcd_hint_loading.setVisibility(View.VISIBLE);
						voice_rcd_hint_rcding.setVisibility(View.GONE);
						voice_rcd_hint_tooshort.setVisibility(View.GONE);
						mHandler.postDelayed(new Runnable() {
							public void run() {
								if (!isShosrt) {
									voice_rcd_hint_loading.setVisibility(View.GONE);
									voice_rcd_hint_rcding
											.setVisibility(View.VISIBLE);
								}
							}
						}, 300);
						img1.setVisibility(View.VISIBLE);
						del_re.setVisibility(View.GONE);
//						startVoiceT = SystemClock.currentThreadTimeMillis();
						startVoiceT = System.currentTimeMillis();
						voiceName = startVoiceT + ".amr";
						start(voiceName);
						flag = 2;
					}
				} else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {
					//松开手势时执行录制完成
//					System.out.println("4");
					mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_nor);
					if (event.getY() >= del_Y
							&& event.getY() <= del_Y + del_re.getHeight()
							&& event.getX() >= del_x
							&& event.getX() <= del_x + del_re.getWidth()) {
						rcChat_popup.setVisibility(View.GONE);
						img1.setVisibility(View.VISIBLE);
						del_re.setVisibility(View.GONE);
						stop();
						flag = 1;
						File file = new File(AppConstant.MNT + AppConstant.FILE_PATH + AppConstant.EDITED_AUDIO_PATH +"/"
										+ voiceName);
						if (file.exists()) {
							file.delete();
						}
					} else {

						voice_rcd_hint_rcding.setVisibility(View.GONE);
						stop();
//						endVoiceT = SystemClock.currentThreadTimeMillis();
						endVoiceT = System.currentTimeMillis();
						flag = 1;
						int time = (int) ((endVoiceT - startVoiceT) / 1000);
						if (time < 1) {
							isShosrt = true;
							voice_rcd_hint_loading.setVisibility(View.GONE);
							voice_rcd_hint_rcding.setVisibility(View.GONE);
							voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
							mHandler.postDelayed(new Runnable() {
								public void run() {
									voice_rcd_hint_tooshort
											.setVisibility(View.GONE);
									rcChat_popup.setVisibility(View.GONE);
									isShosrt = false;
								}
							}, 500);
							return false;
						}
//						ChatMsgEntity entity = new ChatMsgEntity();
//						entity.setDate(getDate());
//						entity.setName("高富帅");
//						entity.setMsgType(false);
//						entity.setTime(time+"\"");
//						entity.setText(voiceName);
						sendVoiceMsg(time+"\"", AppConstant.MNT + AppConstant.FILE_PATH + AppConstant.EDITED_AUDIO_PATH +"/" + voiceName);
						rcChat_popup.setVisibility(View.GONE);

					}
				}
				if (event.getY() < btn_rc_Y) {
					//手势按下的位置不在语音录制按钮的范围内
//					System.out.println("5");
					Animation mLitteAnimation = AnimationUtils.loadAnimation(this,
							R.anim.cancel_rc);
					Animation mBigAnimation = AnimationUtils.loadAnimation(this,
							R.anim.cancel_rc2);
					img1.setVisibility(View.GONE);
					del_re.setVisibility(View.VISIBLE);
					del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
					if (event.getY() >= del_Y
							&& event.getY() <= del_Y + del_re.getHeight()
							&& event.getX() >= del_x
							&& event.getX() <= del_x + del_re.getWidth()) {
						del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
						sc_img1.startAnimation(mLitteAnimation);
						sc_img1.startAnimation(mBigAnimation);
					}
				} else {

					img1.setVisibility(View.VISIBLE);
					del_re.setVisibility(View.GONE);
					del_re.setBackgroundResource(0);
				}
			}
			return super.onTouchEvent(event);
		}

		private static final int POLL_INTERVAL = 300;

		private Runnable mSleepTask = new Runnable() {
			public void run() {
				stop();
			}
		};
		private Runnable mPollTask = new Runnable() {
			public void run() {
				double amp = mSensor.getAmplitude();
				updateDisplay(amp);
				mHandler.postDelayed(mPollTask, POLL_INTERVAL);

			}
		};

		private void start(String name) {
			mSensor.start(name);
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);
		}

		private void stop() {
			mHandler.removeCallbacks(mSleepTask);
			mHandler.removeCallbacks(mPollTask);
			mSensor.stop();
			volume.setImageResource(R.drawable.amp1);
		}

		private void updateDisplay(double signalEMA) {
			
			switch ((int) signalEMA) {
			case 0:
			case 1:
				volume.setImageResource(R.drawable.amp1);
				break;
			case 2:
			case 3:
				volume.setImageResource(R.drawable.amp2);
				
				break;
			case 4:
			case 5:
				volume.setImageResource(R.drawable.amp3);
				break;
			case 6:
			case 7:
				volume.setImageResource(R.drawable.amp4);
				break;
			case 8:
			case 9:
				volume.setImageResource(R.drawable.amp5);
				break;
			case 10:
			case 11:
				volume.setImageResource(R.drawable.amp6);
				break;
			default:
				volume.setImageResource(R.drawable.amp7);
				break;
			}
		}
}
