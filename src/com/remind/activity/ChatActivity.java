package com.remind.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.help.remind.R;
import com.remind.adapter.ChatAdapter;
import com.remind.adapter.ChatAdapter.ContentClickListener;
import com.remind.application.RemindApplication;
import com.remind.dao.MessageDao;
import com.remind.dao.MessageIndexDao;
import com.remind.dao.PeopelDao;
import com.remind.dao.RemindDao;
import com.remind.dao.impl.MessageDaoImpl;
import com.remind.dao.impl.MessageIndexDaoImpl;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.dao.impl.RemindDaoImpl;
import com.remind.entity.MessageEntity;
import com.remind.entity.MessageIndexEntity;
import com.remind.entity.PeopelEntity;
import com.remind.entity.RemindEntity;
import com.remind.fragment.FaceFragment;
import com.remind.fragment.FaceHistoryFragment;
import com.remind.global.AppConstant;
import com.remind.http.HttpClient;
import com.remind.receiver.MessageReceiver;
import com.remind.record.SoundMeter;
import com.remind.up.Upload;
import com.remind.up.listener.CompleteListener;
import com.remind.up.listener.ProgressListener;
import com.remind.util.AppUtil;
import com.remind.util.DataBaseParser;

/**
 * @author ChenLong
 * 
 *         聊天界面, 展示message数据库中的数据{@link com.remind.dao.msg.MessageMsg}
 */
public class ChatActivity extends BaseActivity implements OnClickListener, OnScrollListener{
	public final static int ActivityID=0;
	public static String currentTabTag="face";
	public static Handler chatHandler=null;
	/**
	 * 添加一个提醒
	 */
	public final static int ADD_REMIND = 3002;
	/**
	 * 刷新界面
	 */
	private final static int REFRESH_UI = 3003;
	/**
	 * 刷新提醒的状态
	 */
	private final static int REFRESH_NOTICE = 3004;
	/**
	 * 聊天信息
	 */
	private ListView chatList;
	private ChatAdapter chatAdapter;
	/**
	 * 发送消息按钮
	 */
	private Button sendMsgBtn;
	private Button sendImgBtn;
	/**
	 * 发送提醒按钮
	 */
	private ImageView sendRemindBtn;
	/**
	 * 后退按钮
	 */
	private ImageView backBtn;
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
	 * 添加表情
	 */
	private ImageView addFaceBtn;
	/**
	 * 表情选择是否打开
	 */
	private boolean expanded = false;
	private RelativeLayout faceLayout=null;
	private InputMethodManager imm;
	/**
	 * 联系人
	 */
	private MessageIndexEntity messageIndexEntity = null;
	
	private MessageIndexDao messageIndexDao;
	private MessageDao messageDao;
	private RemindDao remindDao;
	private PeopelDao peopelDao;
	/**
	 * 聊天对象
	 */
	private PeopelEntity peopelEntity;
	/**
	 * 登陆用户
	 */
	private PeopelEntity user;
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
	
	public TabSpec tabSpecFaceHistory,tabSpecFace;
	protected View tabFaceHistory=null,tabFace=null;
	protected ImageView tabFaceHistoryImage=null,tabFaceImage=null;
	protected TabHost tabHost=null;
	protected TabWidget tabWidget=null;
	
	private String texts[] = { "face", "faceHistory" };
	private int imageButton[] = { R.drawable.face_selector,R.drawable.face_history_selector};
	private FragmentTabHost fragmentTabHost;
	private Class<?> fragmentArray[] = {FaceFragment.class,FaceHistoryFragment.class};
	/**
	 * 所属提醒的id
	 */
	private String remindId = "";
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_UI:
				Bundle bundle = msg.getData();
				String mid = (String) bundle.get("mid");
				String code = (String) bundle.get("code");
				getBackAndRefreshData(mid, code);
				break;
			case REFRESH_NOTICE:
				bundle = msg.getData();
				String noticeId = (String) bundle.get("noticeId");
				int state = bundle.getInt("state");
				getNoticeBackAndRefreshData(state, noticeId);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	private LocalBroadcastManager mLocalBroadcastManager;
	private MessageBackReciver mReciver;
	private IntentFilter mIntentFilter;
	/**
	 * 接收人num
	 */
	private String num;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		chatHandler=new MyChatHandler(Looper.myLooper());
		
		messageIndexDao = new MessageIndexDaoImpl(this);
		messageDao = new MessageDaoImpl(this);
		remindDao = new RemindDaoImpl(this);
		peopelDao = new PeopelDaoImpl(this);

		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		mReciver = new MessageBackReciver();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(MessageReceiver.MESSAGE_BACK_ACTION);
		mIntentFilter.addAction(MessageReceiver.GET_MESSAGE_ACTION);
		mIntentFilter.addAction(MessageReceiver.NOTICE_STATE_ACTION);
		
		Intent intent = getIntent();
		num = intent.getStringExtra("num");
		if (null == num || num.length() <= 0) {
			AppUtil.showToast(this, "聊天窗口打开失败，请重试");
			finish();
			return;
		}
		
		remindId = intent.getStringExtra("remind_id");
		if (null == remindId) {
			remindId = "";
		} else {
			remindDao.updateReadState(remindId, RemindEntity.READ);
		}
		
		// 去掉相应的notification
		AppUtil.cancelNotify(num, this);
		
		// 获取当前聊天对象
		Cursor cursor = peopelDao.queryPeopelByNum(num);
		peopelEntity = DataBaseParser.getPeoPelDetail(cursor).get(0);
		cursor.close();
		// 获取登陆用户
		Cursor cursor1 = peopelDao.queryOwner();
		user = DataBaseParser.getPeoPelDetail(cursor1).get(0);
		cursor1.close();
		
//		String remindId = intent.getStringExtra("remind_id");
//		Cursor cursor = messageIndexDao.queryByNum(num);
//		if (cursor.getCount() > 0) {
//			ArrayList<MessageIndexEntity> messageIndexEntities = DataBaseParser.getMessageIndex(cursor);
//			messageIndexEntity = messageIndexEntities.get(0);
//		} else {
//			messageIndexEntity = null;
//		}
//			cursor.close();
		
//		if (null == messageIndexEntity) {
//			AppUtil.showToast(this, "聊天窗口打开失败，请重试");
//			finish();
//		}
		
		initView();
		initMessageEntity();
		
		setResult(RESULT_OK);
	}

	private void initView() {
		chatList = (ListView) findViewById(R.id.chat_list);
		sendMsgBtn = (Button) findViewById(R.id.send_msg);
		sendImgBtn = (Button) findViewById(R.id.face_msg);
		backBtn = (ImageView) findViewById(R.id.title_icon);
		sendRemindBtn = (ImageView) findViewById(R.id.add_remind);
		sendMsgEidt = (EditText) findViewById(R.id.msg_edit);
		contactName = (TextView) findViewById(R.id.title_text);
		contactInfo = (ImageButton) findViewById(R.id.title_info);
		addFaceBtn = (ImageView) findViewById(R.id.add_face);
		faceLayout=(RelativeLayout)findViewById(R.id.faceLayout);
		
		initTab();
		initRecord();
		
		contactName.setText(peopelEntity.getName());
		sendMsgBtn.setOnClickListener(this);
		sendRemindBtn.setOnClickListener(this);
		contactInfo.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		addFaceBtn.setOnClickListener(this);
		sendImgBtn.setOnClickListener(this);
		sendMsgEidt.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					if (null == sendMsgEidt.getText() || sendMsgEidt.getText().toString().trim().length() <= 0) {
						AppUtil.showToast(ChatActivity.this, "您还没有输入内容哦！");
						return false;
					}
					// 发送消息
					sendMsg();
					// TODO 测试接受
//					newMsg();
				}
				return false;
			}
		});
		sendMsgEidt.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(expanded){
					setFaceLayoutExpandState(false);
				}
				return false;
			}
		});
		
		setupChatList();
	}
	
	private View getView(int i) {
		// 取得布局实例
		View view = View.inflate(this, R.layout.tabwidget_image_disselected,
				null);

		// 取得布局对象
		ImageView imageView = (ImageView) view
				.findViewById(R.id.tabImage_disselected);
		// TextView textView=(TextView) view.findViewById(R.id.text);

		// 设置图标
		imageView.setImageResource(imageButton[i]);
		// 设置标题
		// textView.setText(texts[i]);
		return view;
	}

	private void initTab() {
		fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		fragmentTabHost.setup(this, getSupportFragmentManager(),
				R.id.maincontent);

		for (int i = 0; i < texts.length; i++) {
			TabSpec spec = fragmentTabHost.newTabSpec(texts[i]).setIndicator(
					getView(i));

			fragmentTabHost.addTab(spec, fragmentArray[i], null);
			fragmentTabHost.setOnTabChangedListener(new OnTabChangeListener() {

				@Override
				public void onTabChanged(String tabId) {
					if (texts[0].equals(tabId)) {
						// 设置背景(必须在addTab之后，由于需要子节点（底部菜单按钮）否则会出现空指针异常)
						fragmentTabHost
								.getTabWidget()
								.getChildAt(0)
								.setBackgroundResource(
										R.drawable.tabwidget_selected);
						fragmentTabHost
								.getTabWidget()
								.getChildAt(1)
								.setBackgroundResource(
										R.drawable.tabwidget_disselected);
					} else if (texts[1].equals(tabId)) {
						fragmentTabHost
								.getTabWidget()
								.getChildAt(0)
								.setBackgroundResource(
										R.drawable.tabwidget_disselected);
						fragmentTabHost
								.getTabWidget()
								.getChildAt(1)
								.setBackgroundResource(
										R.drawable.tabwidget_selected);
					}

				}
			});
		}
		
		
		
		fragmentTabHost.getTabWidget().getChildAt(0)
				.setBackgroundResource(R.drawable.tabwidget_selected);
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
				hideKeyboard();
				
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
	 * 隐藏表情和键盘
	 */
	private void hideKeyboard() {
		// 隐藏表情
		if(expanded){
			setFaceLayoutExpandState(false);
		}
		if(imm.isActive(sendMsgEidt)){
			// 隐藏键盘
			imm.hideSoftInputFromWindow(ChatActivity.this
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			imm.restartInput(sendMsgEidt);
		}
	}
	
	/**
	 * 初始化消息实例, 需要更新的数据为：时间，发送状态，发送消息的类型，非文字信息的消息id与路径，发送的内容
	 */
	private void initMessageEntity() {
		userMessageEntity = new MessageEntity("", peopelEntity.getName(), 
				peopelEntity.getNum(), user.getName(),
				user.getNum(), AppUtil.getNowTime(), 
				MessageEntity.SEND_SUCCESS, MessageEntity.NORMAL, 
				MessageEntity.TYPE_TEXT, "", "", peopelEntity.getNum(),
				MessageEntity.TYPE_SEND, "", MessageEntity.FEED_DEFAULT, AppConstant.USER_NUM, remindId);
		
		contactMessageEntity = new MessageEntity("", user.getName(),
				user.getNum(), peopelEntity.getName(), 
				peopelEntity.getNum(), AppUtil.getNowTime(), 
				MessageEntity.SEND_SUCCESS, MessageEntity.NORMAL, 
				MessageEntity.TYPE_TEXT, "", "", peopelEntity.getNum(),
				MessageEntity.TYPE_RECIEVE, "", MessageEntity.FEED_DEFAULT, AppConstant.USER_NUM, remindId);
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
					if (entity.getIsPreview() == RemindEntity.CAN_PREV) {
						// 提醒类型，打开提醒
						Intent intent = new Intent(ChatActivity.this, RemindDetailActivity.class);
						intent.putExtra("remind", entity);
						startActivity(intent);
					} else {
						AppUtil.showToast(ChatActivity.this, "这条提醒无法预览.");
					}
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
		
		chatList.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				return false;
			}
		});
	}
	
	/**
	 * 查询消息数据
	 */
	private void getData() {
		allRecorders = messageDao.getCount(num, remindId);
        //计算总页数
        pageSize = (allRecorders + lineSize -1) / lineSize;  
		datas.clear();
		ArrayList<MessageEntity> items = messageDao.getMsgByPage(currentPage, lineSize, num, remindId);
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
		// 需要更新的数据为：时间，发送状态，发送消息的类型，非文字信息的消息id与路径，发送的内容
		userMessageEntity.setTime(time);
		userMessageEntity.setContent(msg);
		userMessageEntity.setMsgType(MessageEntity.TYPE_TEXT);
		userMessageEntity.setOtherTypeId("");
		userMessageEntity.setMsgPath("");
		userMessageEntity.setSendState(MessageEntity.SENDING);
		// 插入数据库
		long mid = messageDao.insert(userMessageEntity);
		userMessageEntity.setId(String.valueOf(mid));
		// 添加到listview中显示，通过线程发送
		chatAdapter.getNewMsg(userMessageEntity.clone(), chatList);
		
		String param = HttpClient.getJsonForPost(HttpClient.sendMsg1(mid + "", peopelEntity.getFriendId(), msg, remindId, "", ""));
		boolean isSend = false;
		try {
			isSend = RemindApplication.iBackService.sendMessage(param);//Send Content by socket
		} catch (RemoteException e) {
			e.printStackTrace();
			isSend = false;
		} catch (Exception e){
			e.printStackTrace();
			isSend = false;
		}
		if (!isSend) {
			messageDao.updateSendState(Long.valueOf(mid),
					MessageEntity.SEND_FAIL);
		}
		// 清除编辑框内容
		sendMsgEidt.setText("");
		hideKeyboard();
	}
	
	/**
	 * 发送语音
	 * 
	 * @param voiceTime		语音时长，共几秒
	 * @param path			语音路径
	 */
	public void sendVoiceMsg(String voiceTime, String fileName, String path) {
		String time = AppUtil.getNowTime();
		// 需要更新的数据为：时间，发送状态，发送消息的类型，非文字信息的消息id与路径，发送的内容
		userMessageEntity.setTime(time);
		userMessageEntity.setContent(voiceTime);
		userMessageEntity.setMsgType(MessageEntity.TYPE_VOICE);
		userMessageEntity.setOtherTypeId("");
		userMessageEntity.setMsgPath(path);
		userMessageEntity.setSendState(MessageEntity.SENDING);
		// 插入数据库
		final long mid = messageDao.insert(userMessageEntity);
		userMessageEntity.setId(String.valueOf(mid));
		// 添加到listview中显示，通过线程发送
		chatAdapter.getNewMsg(userMessageEntity.clone(), chatList);
		// 清除编辑框内容
		sendMsgEidt.setText("");
		
		// 上传音频
		/*
		 * 设置进度条回掉函数
		 * 
		 * 注意：由于在计算发送的字节数中包含了图片以外的其他信息，最终上传的大小总是大于图片实际大小，
		 * 为了解决这个问题，代码会判断如果实际传送的大小大于图片
		 * ，就将实际传送的大小设置成'fileSize-1000'（最小为0）
		 */
		ProgressListener progressListener = new ProgressListener() {
			@Override
			public void transferred(long transferedBytes, long totalBytes) {
			}
		};
		
		CompleteListener completeListener = new CompleteListener() {
			@Override
			public void result(boolean isComplete, String result, String error) {
//				{"mimetype":"image\/jpeg","last_modified":1467881859,"file_size":148775,"image_frames":1,"bucket_name":"sisi0","image_type":"JPEG","image_width":1600,"path":"\/sisi0\/picture\/2016-07-07\/1467881810170test.jpg","image_height":1200,"code":200,"signature":"a0c8a7a28e0edc2b2e98c56457d0c35e"}
				// 成功/失败修改数据库
				if (isComplete) {
					// 发送消息
					boolean isSend = false;
					try {
						JSONObject jsonObject = new JSONObject(result);
						String path = jsonObject.getString("path");
						String param = HttpClient.getJsonForPost(HttpClient.sendMsg1(mid + "", peopelEntity.getFriendId(), "", remindId, MessageEntity.TYPE_VOICE, path));
						isSend = RemindApplication.iBackService.sendMessage(param);//Send Content by socket
					} catch (RemoteException e) {
						isSend = false;
						e.printStackTrace();
					} catch (Exception e){
						isSend = false;
						e.printStackTrace();
					}
					if (!isSend) {
						messageDao.updateSendState(Long.valueOf(mid),
								MessageEntity.SEND_FAIL);
					}
				} else {
					// fail
					messageDao.updateSendState(Long.valueOf(mid),
							MessageEntity.SEND_FAIL);
				}
			}
		};
		Upload.upload(this, fileName, completeListener, progressListener, path);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.face_msg:
			if (null == sendMsgEidt.getText() || sendMsgEidt.getText().toString().trim().length() <= 0) {
				AppUtil.showToast(this, "您还没有输入内容哦！");
				return;
			}
			// 发送消息
			sendMsg();
			break;
		case R.id.add_remind:
			// 添加提醒
			Intent intent = new Intent(ChatActivity.this, AddRemindActivity.class);
			intent.putExtra("targetPeopel", peopelEntity);
			startActivityForResult(intent, ADD_REMIND);
			break;
		case R.id.title_info:
			// 联系人信息
			break;
		case R.id.add_face:
			// 表情
			showFaceView();
			break;
		case R.id.title_icon:
			// 关闭
			finish();
			break;

		default:
			break;
		}
	}
	
	/**
	 * 打开表情选择框
	 */
	private void showFaceView() {
		if (expanded) {
			setFaceLayoutExpandState(false);
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			sendMsgEidt.requestFocus();

			/**
			 * height不设为0是因为，希望可以使再次打开时viewFlipper已经初始化为第一页 避免
			 * 再次打开ViewFlipper时画面在动的结果,
			 * 为了避免因为1dip的高度产生一个白缝，所以这里在ViewFlipper所在的RelativeLayout
			 * 最上面添加了一个1dip高的黑色色块
			 */

		} else {
			setFaceLayoutExpandState(true);
		}
	}
	
	private void setFaceLayoutExpandState(boolean isexpand) {
		expanded = isexpand;
		if (isexpand == false) {

			ViewGroup.LayoutParams params = faceLayout.getLayoutParams();
			params.height = 1;
			faceLayout.setLayoutParams(params);

			addFaceBtn.setBackgroundResource(R.drawable.add_face);
			Message msg = new Message();
			msg.what = 0;
			msg.obj = "collapse";
			if (FaceFragment.faceHandler != null)
				FaceFragment.faceHandler.sendMessage(msg);

			Message msg2 = new Message();
			msg2.what = 0;
			msg2.obj = "collapse";
			if (FaceHistoryFragment.faceHistoryHandler != null)
				FaceHistoryFragment.faceHistoryHandler.sendMessage(msg2);

			// chatListView.setSelection(chatList.size()-1);//使会话列表自动滑动到最低端

		} else {

			imm.hideSoftInputFromWindow(ChatActivity.this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			ViewGroup.LayoutParams params = faceLayout.getLayoutParams();
			params.height = LayoutParams.WRAP_CONTENT;
//			params.height = 185;
			// faceLayout.setLayoutParams(new RelativeLayout.LayoutParams( ));
			RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
					RelativeLayout.TRUE);
			faceLayout.setLayoutParams(relativeParams);

			addFaceBtn.setBackgroundResource(R.drawable.keyboard);

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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (expanded) {
				setFaceLayoutExpandState(false);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
     * 增加数据
     */
    private void appendDate(){
		final ArrayList<MessageEntity> additems = messageDao.getMsgByPage(
				currentPage, lineSize, num, remindId);
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
		Cursor cursor = messageDao.queryByOtherTypeId(remindEntity.getId());
		MessageEntity entity = DataBaseParser.getMessage(cursor).get(0);
		cursor.close();
		// 添加到listview中显示，通过线程发送
		chatAdapter.getNewMsg(entity, chatList);
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
						sendVoiceMsg(time+"\"", voiceName, AppConstant.MNT + AppConstant.FILE_PATH + AppConstant.EDITED_AUDIO_PATH +"/" + voiceName);
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
		
		/**
		 * @author ChenLong
		 *
		 *	推送消息发送过来
		 */
		class MessageBackReciver extends BroadcastReceiver {

			public MessageBackReciver() {
			}

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(MessageReceiver.MESSAGE_BACK_ACTION)) {
					//  收到反馈后修改界面显示
					String mid = intent.getStringExtra("mid");
					String code = intent.getStringExtra("code");
					Message message = mHandler.obtainMessage();
					message.what = REFRESH_UI;
					Bundle data = new Bundle();
					data.putString("mid", mid);
					data.putString("code", code);
					message.setData(data);
					mHandler.sendMessage(message);
				} else if (action.equals(MessageReceiver.GET_MESSAGE_ACTION)) {
					// 收到好友发送的消息/提醒
					MessageEntity messageEntity = (MessageEntity) intent.getSerializableExtra("messageEntity");
					if (num.equals(messageEntity.getSendNum())) {
						if (TextUtils.isEmpty(remindId)) {
							datas.add(messageEntity);
							chatAdapter.notifyDataSetChanged();
							chatList.setSelection(datas.size());
						}else if (!TextUtils.isEmpty(remindId) && remindId.equals(messageEntity.getRemindId())) {
							datas.add(messageEntity);
							chatAdapter.notifyDataSetChanged();
							chatList.setSelection(datas.size());
						}
					}
					
				} else if(action.equals(MessageReceiver.NOTICE_STATE_ACTION)) {
					// 收到接受/拒绝提醒
//					String noticeId = intent.getStringExtra("noticeId");
//					int state = intent.getIntExtra("state", RemindEntity.LAUNCH_WAIT);
//					Message message = mHandler.obtainMessage();
//					message.what = REFRESH_NOTICE;
//					Bundle data = new Bundle();
//					data.putString("noticeId", noticeId);
//					data.putInt("state", state);
//					message.setData(data);
//					mHandler.sendMessage(message);
					chatAdapter.notifyDataSetChanged();
				}
			};
		}
		
		@Override
		protected void onStart() {
			super.onStart();
			RemindApplication.IS_CHAT_VIEW_SHOW = true;
			registerReceiver(mReciver, mIntentFilter);
		}
		
		@Override
		protected void onStop() {
			RemindApplication.IS_CHAT_VIEW_SHOW = false;
			unregisterReceiver(mReciver);
			super.onStop();
		}
		
		/**
		 * 获取了反馈之后，修改数据，刷新画面
		 */
		private void getBackAndRefreshData(String mid, String code) {
			boolean isFind = false;
			int findIndex = 0;
			// 寻找所发送的消息
			for (int i = datas.size() - 1; i >= 0; i--) {
				MessageEntity temp = datas.get(i);
				if (temp.getId().equals(mid)) {
					if ("200".equals(code)) {
						temp.setSendState(MessageEntity.SEND_SUCCESS);
					} else {
						temp.setSendState(MessageEntity.SEND_FAIL);
					}
					isFind = true;
					findIndex = i;
					break;
				}
			}
			// 如果所修改界面在可见范围内，则刷新画面
			if (isFind) {
				int firstPosition = chatList.getFirstVisiblePosition();
				int lastPosition = chatList.getLastVisiblePosition();
				if (findIndex >= firstPosition && findIndex <= lastPosition) {
					chatAdapter.notifyDataSetChanged();
				}
			}
		}
		
		/**
		 * 获取了提醒的反馈之后，修改数据，刷新画面
		 */
		private void getNoticeBackAndRefreshData(int state, String noticeId) {
			boolean isFind = false;
			int findIndex = 0;
			Cursor cursor = remindDao.queryRemindByNoticeId(noticeId);
			RemindEntity remindEntity = DataBaseParser.getRemindDetail(cursor).get(0);
			cursor.close();
			
			if (null != remindEntity) {
				// 寻找所发送的消息
				for (int i = datas.size() - 1; i >= 0; i--) {
					MessageEntity temp = datas.get(i);
					if (temp.getOtherTypeId().equals(remindEntity.getId())) {
						isFind = true;
						findIndex = i;
						break;
					}
				}
				// 如果所修改界面在可见范围内，则刷新画面
				if (isFind) {
					int firstPosition = chatList.getFirstVisiblePosition();
					int lastPosition = chatList.getLastVisiblePosition();
					if (findIndex >= firstPosition && findIndex <= lastPosition) {
						chatAdapter.notifyDataSetChanged();
					}
				}
			}
			
		}
		
		public class MyChatHandler extends Handler{
			
			public MyChatHandler(Looper looper){
				super(looper);
			}

			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case FaceFragment.ActivityId:
					if(msg.arg1==0){            //添加表情字符串
						sendMsgEidt.append(msg.obj.toString());
					}
				
				}
			}
			
			
			
		}
}
