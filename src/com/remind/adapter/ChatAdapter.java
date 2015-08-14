package com.remind.adapter;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.help.remind.R;
import com.remind.asyn.ImageLoader;
import com.remind.dao.PeopelDao;
import com.remind.dao.RemindDao;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.dao.impl.RemindDaoImpl;
import com.remind.entity.MessageEntity;
import com.remind.entity.RemindEntity;
import com.remind.util.DataBaseParser;

public class ChatAdapter extends BaseAdapter {
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

	public ChatAdapter(Context context, List<MessageEntity> datas) {
		this.context = context;
		this.datas = datas;
		mInflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance(context);
		peopelDao = new PeopelDaoImpl(this.context);
		remindDao = new RemindDaoImpl(context);
		historyCount = datas.size();
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
				viewHolder.time = (TextView) convertView
						.findViewById(R.id.chat_from_createDate);
				viewHolder.title = (TextView) convertView
						.findViewById(R.id.chat_from_title);
				viewHolder.content = (TextView) convertView
						.findViewById(R.id.chat_from_content);
				viewHolder.state = (TextView) convertView
						.findViewById(R.id.send_state);
				viewHolder.contentImg =  (ImageView) convertView.
						findViewById(R.id.chat_from_img);
				viewHolder.personImg =  (ImageView) convertView.
						findViewById(R.id.chat_from_icon);
				viewHolder.ok = (Button) convertView.
						findViewById(R.id.from_button_ok);
				viewHolder.cancel = (Button) convertView.
						findViewById(R.id.from_button_cancel);
				convertView.setTag(viewHolder);
			} else
			{
				convertView = mInflater.inflate(R.layout.main_chat_send_msg,
						null);
				viewHolder.chatPanel = (LinearLayout) convertView
						.findViewById(R.id.send_chat_panel);
				viewHolder.time = (TextView) convertView
						.findViewById(R.id.chat_send_createDate);
				viewHolder.title = (TextView) convertView
						.findViewById(R.id.chat_send_title);
				viewHolder.content = (TextView) convertView
						.findViewById(R.id.chat_send_content);
				viewHolder.state = (TextView) convertView
						.findViewById(R.id.send_state);
				viewHolder.contentImg =  (ImageView) convertView.
						findViewById(R.id.chat_send_img);
				viewHolder.personImg =  (ImageView) convertView.
						findViewById(R.id.chat_send_icon);
				viewHolder.ok = (Button) convertView.
						findViewById(R.id.send_button_ok);
				viewHolder.cancel = (Button) convertView.
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
		viewHolder.personImg.setImageResource(R.drawable.white);
		
		if (null != imgPath && imgPath.trim().length() > 0 && viewHolder.personImg.getTag() != null && viewHolder.personImg.getTag().equals(imgPath)) {
			imageLoader.DisplayImage(imgPath, viewHolder.personImg);
		}
		
		return convertView;
	}

	private class ViewHolder {
		LinearLayout chatPanel;
		TextView state;
		TextView title;
		TextView content;
		TextView time;
		ImageView personImg;
		ImageView contentImg;
		Button ok;
		Button cancel;
	}
	
	/**
	 * 根据消息类型的不同改变显示
	 */
	private void changeMsgType(String type, final ViewHolder viewHolder, MessageEntity chatMessage) {
		viewHolder.content.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		if (MessageEntity.TYPE_TEXT.equals(type)) {
			// 正常文字消息
			viewHolder.content.setText(chatMessage.getContent());
			
			viewHolder.contentImg.setImageBitmap(null);
			viewHolder.title.setVisibility(View.GONE);
			viewHolder.contentImg.setVisibility(View.GONE);
			viewHolder.ok.setVisibility(View.GONE);
			viewHolder.cancel.setVisibility(View.GONE);
		}else if (MessageEntity.TYPE_REMIND.equals(type)) {
			// 如果是提醒的话
			RemindEntity temp = new RemindEntity();
			temp.setId(chatMessage.getOtherTypeId());
			// 查询提醒内容
			Cursor cursor = remindDao.queryRemind(temp);
			final RemindEntity entity = DataBaseParser.getRemindDetail(cursor).get(0);
			cursor.close();
			
			viewHolder.contentImg.setImageBitmap(null);
			viewHolder.title.setVisibility(View.VISIBLE);
			viewHolder.contentImg.setVisibility(View.GONE);
			// 设置文字内容
			viewHolder.title.setText("提醒消息，长按查看详情");
			viewHolder.content.setText(entity.getTitle());
			// 设置点击事件
			int remindState = entity.getRemindState();
//			int remindState = 1;
			switch (remindState) {
			case RemindEntity.ACCEPT:
				// 已接受的提醒
				changeButtonState("已接受", viewHolder);
				break;
			case RemindEntity.REFUSE:
				// 拒绝的提醒
				changeButtonState("已拒绝", viewHolder);
				break;
			case RemindEntity.NEW:
				// 未接受提醒
				viewHolder.cancel.setVisibility(View.VISIBLE);
				viewHolder.ok.setVisibility(View.VISIBLE);
				
				viewHolder.cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// 拒绝
						entity.setRemindState(RemindEntity.REFUSE);
						remindDao.updateRemind(entity);
						changeButtonState("已拒绝", viewHolder);
					}
				});
				viewHolder.ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// 接受
						entity.setRemindState(RemindEntity.ACCEPT);
						remindDao.updateRemind(entity);
						changeButtonState("已接受", viewHolder);
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
						changeButtonState("对方已接受", viewHolder);
						break;

					default:
						break;
					}
				}
				break;
			default:
				break;
			}
		} else if (MessageEntity.TYPE_VOICE.equals(type)) {
			// 如果是语音的话
			viewHolder.content.setText("");
			viewHolder.content.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
			
			viewHolder.contentImg.setImageBitmap(null);
			viewHolder.title.setVisibility(View.GONE);
			viewHolder.contentImg.setVisibility(View.GONE);
			viewHolder.ok.setVisibility(View.GONE);
			viewHolder.cancel.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 改变按钮状态
	 */
	private void changeButtonState(String text, ViewHolder viewHolder) {
		viewHolder.cancel.setVisibility(View.GONE);
		viewHolder.ok.setVisibility(View.VISIBLE);
		viewHolder.ok.setText(text);
		viewHolder.ok.setEnabled(false);
		
		viewHolder.cancel.setFocusable(false);
		viewHolder.ok.setFocusable(false);
	}

	/**
	 * 为内容文字设置监听器
	 * 
	 * @param linearLayout
	 * @param position
	 * @param chatMessage
	 */
	private void setListenerForView(ViewHolder viewHolder, final int position, final MessageEntity chatMessage) {
		viewHolder.chatPanel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (null != contentClickListener) {
					contentClickListener.onContentClick(position, chatMessage);
				}
			}
		});
		
		viewHolder.chatPanel.setOnLongClickListener(new OnLongClickListener() {
			
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
