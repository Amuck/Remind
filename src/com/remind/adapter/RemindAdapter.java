package com.remind.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.help.remind.R;
import com.remind.entity.RemindEntity;

public class RemindAdapter extends BaseAdapter {
	
	private List<RemindEntity> datas = new ArrayList<RemindEntity>();
	private Context context;
	private ViewHolder viewHolder;
	private LayoutInflater mInflater;
	

	public RemindAdapter(Context context, List<RemindEntity> datas) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return 10;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		RemindEntity remindEntity = datas.get(position);

		if (convertView == null)
		{
			viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_remind_adapter,
						parent, false);
//				viewHolder.chatPanel = (LinearLayout) convertView
//						.findViewById(R.id.from_chat_panel);
//				viewHolder.time = (TextView) convertView
//						.findViewById(R.id.chat_from_createDate);
//				viewHolder.title = (TextView) convertView
//						.findViewById(R.id.chat_from_title);
//				viewHolder.content = (TextView) convertView
//						.findViewById(R.id.chat_from_content);
//				viewHolder.state = (TextView) convertView
//						.findViewById(R.id.send_state);
//				viewHolder.contentImg =  (ImageView) convertView.
//						findViewById(R.id.chat_from_img);
//				viewHolder.personImg =  (ImageView) convertView.
//						findViewById(R.id.chat_from_icon);
//				viewHolder.ok = (Button) convertView.
//						findViewById(R.id.from_button_ok);
//				viewHolder.cancel = (Button) convertView.
//						findViewById(R.id.from_button_cancel);
				convertView.setTag(viewHolder);
			

		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
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
	
}
