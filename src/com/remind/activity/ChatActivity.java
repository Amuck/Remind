package com.remind.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);

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
		
		contactName.setText(messageIndexEntity.getName());
		sendMsgBtn.setOnClickListener(this);
		sendRemindBtn.setOnClickListener(this);
		contactInfo.setOnClickListener(this);
		
		setupChatList();
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
}
