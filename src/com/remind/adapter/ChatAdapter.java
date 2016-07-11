package com.remind.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.help.remind.R;
import com.remind.asyn.ImageLoader;
import com.remind.dao.PeopelDao;
import com.remind.dao.RemindDao;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.dao.impl.RemindDaoImpl;
import com.remind.entity.MessageEntity;
import com.remind.entity.RemindEntity;
import com.remind.http.HttpClient;
import com.remind.util.AppUtil;
import com.remind.util.DataBaseParser;
import com.remind.util.Utils;
import com.remind.view.RoleDetailImageView;

public class ChatAdapter extends BaseAdapter {
	int[] faceId={R.drawable.f_static_000,R.drawable.f_static_001,R.drawable.f_static_002,R.drawable.f_static_003
			,R.drawable.f_static_004,R.drawable.f_static_005,R.drawable.f_static_006,R.drawable.f_static_009,R.drawable.f_static_010,R.drawable.f_static_011
			,R.drawable.f_static_012,R.drawable.f_static_013,R.drawable.f_static_014,R.drawable.f_static_015,R.drawable.f_static_017,R.drawable.f_static_018};
	String[] faceName={"\\呲牙","\\淘气","\\流汗","\\偷笑","\\再见","\\敲打","\\擦汗","\\流泪","\\掉泪","\\小声","\\炫酷","\\发狂"
			 ,"\\委屈","\\便便","\\菜刀","\\微笑","\\色色","\\害羞"};
	
	HashMap<String,Integer> faceMap=null;
	/**
	 * 刷新界面
	 */
	private final static int REFRESH_UI = 3003;
	/**
	 * @author ChenLong
	 *
	 * 内容文字点击事件监听
	 */
	public interface ContentClickListener {
		public void onContentClick(int position, MessageEntity chatMessage);
		public void onContentLongClick(int position, MessageEntity chatMessage);
	}
	
	private List<MessageEntity> datas;
	private Context context;
	public ImageLoader imageLoader;
	private ViewHolder viewHolder;
	private LayoutInflater mInflater;
	private PeopelDao peopelDao;
	private RemindDao remindDao;
	
	/**
	 * 历史数据条数
	 */
	private int historyCount = 0;
	
	private ContentClickListener contentClickListener = null;
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_UI:
				String s = (String) msg.obj;
				if (null == s || !s.contains("|")) {
					Toast.makeText(context, "网络连接失败，请确认后重试.",
							Toast.LENGTH_SHORT).show();
				}
				
				String[] ss = s.split("\\|");
				if (!ss[0].equals("200")) {
					// 失败
					Toast.makeText(context, "失败，请重试",
							Toast.LENGTH_SHORT).show();
				} else {
					// 成功
					// 点击接受后的处理
					Bundle bundle = msg.getData();
					int state = bundle.getInt("state");
					RemindEntity entity = (RemindEntity) bundle.getSerializable("RemindEntity");
					if (state == 0) {
						// 不同意
						state = RemindEntity.REFUSE;
					} else {
						// 同意
						state = RemindEntity.ACCEPT;
						// 启动闹钟
						AppUtil.setAlarm(context, entity.getRemindTime(), Integer.valueOf(entity.getId()));
					}
					entity.setRemindState(state);
					remindDao.updateRemind(entity);
					notifyDataSetChanged();
				}
				

				break;

			default:
				break;
			}
		};
	};

	public ChatAdapter(Context context, List<MessageEntity> datas) {
		this.context = context;
		this.datas = datas;
		mInflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance(context);
		peopelDao = new PeopelDaoImpl(this.context);
		remindDao = new RemindDaoImpl(context);
		historyCount = datas.size();
		faceMap=new HashMap<String,Integer>();	
		/**
		 * 为表情Map添加数据
		 */
		for(int i=0; i<faceId.length; i++){
			faceMap.put(faceName[i], faceId[i]);
		}
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position)
	{
		MessageEntity msg = datas.get(position);
		return "1".equals(msg.getIsComing()) ? 1 : 0;
	}

	@Override
	public int getViewTypeCount()
	{
		return 2;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MessageEntity chatMessage = datas.get(position);

		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			if (MessageEntity.TYPE_RECIEVE.equals(chatMessage.getIsComing()))
			{
				convertView = mInflater.inflate(R.layout.main_chat_from_msg,
						parent, false);
				viewHolder.chatPanel = (LinearLayout) convertView
						.findViewById(R.id.from_chat_panel);
				viewHolder.chatRemindPanel = (LinearLayout) convertView
						.findViewById(R.id.chat_from_remind);
				viewHolder.time = (TextView) convertView
						.findViewById(R.id.chat_from_createDate);
				viewHolder.title = (TextView) convertView
						.findViewById(R.id.chat_from_title);
				viewHolder.remindState = (TextView) convertView
						.findViewById(R.id.chat_from_remind_state);
				viewHolder.date = (TextView) convertView
						.findViewById(R.id.chat_from_remind_time);
				viewHolder.content = (TextView) convertView
						.findViewById(R.id.chat_from_content);
				viewHolder.state = (TextView) convertView
						.findViewById(R.id.send_state);
//				viewHolder.contentImg =  (ImageView) convertView.
//						findViewById(R.id.chat_from_img);
				viewHolder.personImg =  (RoleDetailImageView) convertView.
						findViewById(R.id.chat_from_icon);
				viewHolder.ok = (TextView) convertView.
						findViewById(R.id.from_button_ok);
				viewHolder.cancel = (TextView) convertView.
						findViewById(R.id.from_button_cancel);
				convertView.setTag(viewHolder);
			} else
			{
				convertView = mInflater.inflate(R.layout.main_chat_send_msg,
						null);
				viewHolder.chatPanel = (LinearLayout) convertView
						.findViewById(R.id.send_chat_panel);
				viewHolder.chatRemindPanel = (LinearLayout) convertView
						.findViewById(R.id.chat_send_remind);
				viewHolder.time = (TextView) convertView
						.findViewById(R.id.chat_send_createDate);
				viewHolder.title = (TextView) convertView
						.findViewById(R.id.chat_send_title);
				viewHolder.remindState = (TextView) convertView
						.findViewById(R.id.chat_send_remind_state);
				viewHolder.date = (TextView) convertView
						.findViewById(R.id.chat_send_remind_time);
				viewHolder.content = (TextView) convertView
						.findViewById(R.id.chat_send_content);
				viewHolder.state = (TextView) convertView
						.findViewById(R.id.send_state);
//				viewHolder.contentImg =  (ImageView) convertView.
//						findViewById(R.id.chat_send_img);
				viewHolder.personImg =  (RoleDetailImageView) convertView.
						findViewById(R.id.chat_send_icon);
				viewHolder.ok = (TextView) convertView.
						findViewById(R.id.send_button_ok);
				viewHolder.cancel = (TextView) convertView.
						findViewById(R.id.send_button_cancel);
				convertView.setTag(viewHolder);
			}

		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.time.setText(chatMessage.getTime());
		
		setListenerForView(viewHolder, position, chatMessage);
		
		String state = chatMessage.getSendState();
		changeSendState(viewHolder.state, state);
		
		String msgType = chatMessage.getMsgType();
		changeMsgType(msgType, viewHolder, chatMessage);
		
		String imgPath = peopelDao.getImgPath(chatMessage.getSendNum());
		viewHolder.personImg.setTag(imgPath);
		
		int id = Utils.getResoureIdbyName(context, imgPath);
		if (0 == id) {
			// 如果头像是用户上传的图片
			if (null != imgPath && imgPath.trim().length() > 0 && viewHolder.personImg.getTag() != null && viewHolder.personImg.getTag().equals(imgPath)) {
				imageLoader.DisplayImage(imgPath, viewHolder.personImg);
			}
		} else {
			// 如果头像是软件自带的图片
			viewHolder.personImg.setImageResource(id);
		}
		
		return convertView;
	}
	
	private void setFaceText(TextView textView,String text){
		SpannableString spanStr=parseString(text);
		textView.setText(spanStr);
	}
	
	private SpannableString parseString(String inputStr){
		SpannableStringBuilder spb=new SpannableStringBuilder();
		Pattern mPattern= Pattern.compile("\\\\..");
		Matcher mMatcher=mPattern.matcher(inputStr);
		String tempStr=inputStr;
		
		while(mMatcher.find()){
			int start=mMatcher.start();
			int end=mMatcher.end();
			spb.append(tempStr.substring(0,start));
			String faceName=mMatcher.group();
			setFace(spb, faceName);
			tempStr=tempStr.substring(end, tempStr.length());
			/**
			 * 更新查找的字符串
			 */
			mMatcher.reset(tempStr);
		}
		spb.append(tempStr);
		return new SpannableString(spb);
	}
	
	private void setFace(SpannableStringBuilder spb, String faceName){
		Integer faceId=faceMap.get(faceName);
		if(faceId!=null){
			Bitmap bitmap=BitmapFactory.decodeResource(context.getResources(), faceId);
			bitmap=Bitmap.createScaledBitmap(bitmap, 30, 30, true);
			ImageSpan imageSpan=new ImageSpan(context,bitmap);
			SpannableString spanStr=new SpannableString(faceName);
			spanStr.setSpan(imageSpan, 0, faceName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			spb.append(spanStr);	
		}
		else{
			spb.append(faceName);
		}
		
	}

	private class ViewHolder {
		LinearLayout chatPanel;
		LinearLayout chatRemindPanel;
		TextView state;
		TextView title;
		TextView date;
		TextView content;
		TextView remindState;
		TextView time;
		RoleDetailImageView personImg;
//		ImageView contentImg;
		TextView ok;
		TextView cancel;
	}
	
	/**
	 * 根据消息类型的不同改变显示
	 */
	private void changeMsgType(String type, final ViewHolder viewHolder, MessageEntity chatMessage) {
		viewHolder.content.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		if (MessageEntity.TYPE_TEXT.equals(type)) {
			// 正常文字消息
			String text = chatMessage.getContent();
			if (text.contains("\\")) {
				setFaceText(viewHolder.content, text);
			} else {
				viewHolder.content.setText(text);
			}
			
//			viewHolder.contentImg.setImageBitmap(null);
			viewHolder.remindState.setVisibility(View.GONE);
			viewHolder.chatRemindPanel.setVisibility(View.GONE);
			viewHolder.content.setVisibility(View.VISIBLE);
//			viewHolder.ok.setVisibility(View.GONE);
//			viewHolder.cancel.setVisibility(View.GONE);
		}else if (MessageEntity.TYPE_REMIND.equals(type)) {
			// 如果是提醒的话
			RemindEntity temp = new RemindEntity();
			temp.setId(chatMessage.getOtherTypeId());
			// 查询提醒内容
			Cursor cursor = remindDao.queryRemindInChat(temp);
			final RemindEntity entity = DataBaseParser.getRemindDetail(cursor).get(0);
			cursor.close();
			
			viewHolder.chatRemindPanel.setVisibility(View.VISIBLE);
			viewHolder.content.setVisibility(View.GONE);
//			viewHolder.contentImg.setImageBitmap(null);
//			viewHolder.title.setVisibility(View.VISIBLE);
//			viewHolder.contentImg.setVisibility(View.GONE);
			// 设置文字内容
			
			viewHolder.date.setText(entity.getRemindTime().split(" ")[0]);
			viewHolder.title.setText(entity.getTitle());
//			viewHolder.title.setText("提醒消息，长按查看详情");
//			viewHolder.content.setText(entity.getTitle());
			// 设置点击事件
			int remindState = entity.getRemindState();
//			int remindState = 1;
			switch (remindState) {
			case RemindEntity.ACCEPT:
				// 已接受的提醒
				changeButtonState("任务已接受", viewHolder);
				break;
			case RemindEntity.REFUSE:
				// 拒绝的提醒
				changeButtonState("任务已拒绝", viewHolder);
				break;
			case RemindEntity.NEW:
				// 未接受提醒
//				viewHolder.cancel.setVisibility(View.VISIBLE);
//				viewHolder.ok.setVisibility(View.VISIBLE);
				
				viewHolder.cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// 拒绝
						agreeNotice(entity, 0);
//						Toast.makeText(context, "拒绝",
//								Toast.LENGTH_SHORT).show();
					}
				});
				viewHolder.ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// 接受
						agreeNotice(entity, 1);
//						Toast.makeText(context, "接受",
//								Toast.LENGTH_SHORT).show();
					}
				});
				break;
			case RemindEntity.LAUNCH:
				// 我发起的提醒
				{
					int launchState = entity.getLaunchState();
					switch (launchState) {
					case RemindEntity.LAUNCH_WAIT:
						// 等待对方接受
						changeButtonState("等待对方接受", viewHolder);
						break;
					case RemindEntity.LAUNCH_ACCEPT:
						// 对方接受
						changeButtonState("对方已接受", viewHolder);
						break;
					case RemindEntity.LAUNCH_REFUSE:
						// 对方已拒绝
						changeButtonState("对方已拒绝", viewHolder);
						break;

					default:
						break;
					}
				}
				break;
			default:
				// 接收后状态改变: 1: 关闭; 2: 已开始; 3: 马上开始; 4: 延迟10分钟
				switch (remindState % 10) {
				case 1:
					changeButtonState("对方已关闭闹钟", viewHolder);
					break;
				case 2:
					changeButtonState("对方已开始", viewHolder);
					break;
				case 3:
					changeButtonState("对方马上开始", viewHolder);
					break;
				case 4:
					changeButtonState("对方延迟10分钟再开始", viewHolder);
					break;

				default:
					break;
				}
				break;
			}
		} else if (MessageEntity.TYPE_VOICE.equals(type)) {
			// 如果是语音的话
			viewHolder.content.setText("");
			viewHolder.content.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
			viewHolder.remindState.setVisibility(View.GONE);
			viewHolder.chatRemindPanel.setVisibility(View.GONE);
			viewHolder.content.setVisibility(View.VISIBLE);
//			viewHolder.contentImg.setImageBitmap(null);
//			viewHolder.title.setVisibility(View.GONE);
//			viewHolder.contentImg.setVisibility(View.GONE);
//			viewHolder.ok.setVisibility(View.GONE);
//			viewHolder.cancel.setVisibility(View.GONE);
		}
	}
	
	private void agreeNotice(final RemindEntity entity, final int state) {
		final String params = HttpClient.getJsonForPost(HttpClient.agreeNotice(entity.getNoticeId(), state, "", entity.getOwnerId()));
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String s = HttpClient.post(HttpClient.url + HttpClient.agree_notice, params);
				Message msg = handler.obtainMessage();
				msg.what = REFRESH_UI;
				msg.obj = s;
				Bundle bundle = new Bundle();
				bundle.putSerializable("RemindEntity", entity);
				bundle.putInt("state", state);
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	/**
	 * 改变按钮状态
	 */
	private void changeButtonState(String text, ViewHolder viewHolder) {
		viewHolder.remindState.setText(text);
		viewHolder.remindState.setVisibility(View.VISIBLE);
		viewHolder.cancel.setVisibility(View.GONE);
		viewHolder.ok.setVisibility(View.GONE);
//		viewHolder.ok.setText(text);
		viewHolder.ok.setEnabled(false);
		
//		viewHolder.cancel.setFocusable(false);
//		viewHolder.ok.setFocusable(false);
	}

	/**
	 * 为内容文字设置监听器
	 * 
	 * @param linearLayout
	 * @param position
	 * @param chatMessage
	 */
	private void setListenerForView(ViewHolder viewHolder, final int position, final MessageEntity chatMessage) {
		viewHolder.title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (null != contentClickListener) {
					contentClickListener.onContentClick(position, chatMessage);
				}
			}
		});
		
		viewHolder.title.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if (null != contentClickListener) {
					contentClickListener.onContentLongClick(position, chatMessage);
					return true;
				}
				return false;
			}
		});
		
		viewHolder.content.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (MessageEntity.TYPE_VOICE.equals(chatMessage.getMsgType())) {
					// 如果是语音则播放
					playMusic(chatMessage.getMsgPath());
				} else {
					if (null != contentClickListener) {
						contentClickListener.onContentClick(position, chatMessage);
					}
				}
			}
		});
		
		viewHolder.content.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if (null != contentClickListener) {
					contentClickListener.onContentLongClick(position, chatMessage);
					return true;
				}
				return false;
			}
		});
	}
	
	/**
	 * 改变发送状态
	 * 
	 * @param textView
	 * @param state
	 */
	private void changeSendState(TextView textView, String state) {
		if (MessageEntity.SEND_SUCCESS.equals(state)) {
			textView.setVisibility(View.INVISIBLE);
		} else if(MessageEntity.SEND_FAIL.equals(state)){
			textView.setText("发送失败");
			textView.setVisibility(View.VISIBLE);
		} else if(MessageEntity.SENDING.equals(state)){
			textView.setText("正在发送");
			textView.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 接受新消息
	 * @param messageEntity
	 */
	public synchronized void getNewMsg(MessageEntity messageEntity, ListView listView) {
		datas.add(messageEntity);
		this.notifyDataSetChanged();
		// listview滚动到底部
		listView.setSelection(ListView.FOCUS_DOWN);
	}

	public int getHistoryCount() {
		return historyCount;
	}

	public void setHistoryCount(int historyCount) {
		this.historyCount = historyCount;
	}

	public void setContentClickListener(ContentClickListener contentClickListener) {
		this.contentClickListener = contentClickListener;
	}
	
	private void playMusic(String name) {
		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
