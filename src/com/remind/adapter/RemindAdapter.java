package com.remind.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.help.remind.R;
import com.remind.entity.RemindEntity;
import com.remind.view.RoleDetailImageView;

public class RemindAdapter extends BaseAdapter {
	
	private List<RemindEntity> datas;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		RemindEntity remindEntity = datas.get(position);

		if (convertView == null)
		{
			viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_remind_adapter,
						parent, false);
				viewHolder.remind_time_txt = (TextView) convertView
						.findViewById(R.id.remind_time_txt);
				viewHolder.remind_title_txt = (TextView) convertView
						.findViewById(R.id.remind_title_txt);
				viewHolder.remind_content_txt = (TextView) convertView
						.findViewById(R.id.remind_content_txt);
				viewHolder.remind_name_txt = (TextView) convertView
						.findViewById(R.id.remind_name_txt);
				viewHolder.remind_img =  (RoleDetailImageView) convertView.
						findViewById(R.id.remind_img);
				convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.remind_time_txt.setText(remindEntity.getRemindTime().split(" ")[1]);
		viewHolder.remind_title_txt.setText(remindEntity.getTitle());
		viewHolder.remind_content_txt.setText(remindEntity.getContent());
		viewHolder.remind_name_txt.setText(remindEntity.getTargetName() + remindEntity.getId());
		viewHolder.remind_img.setmBorderInsideColor(remindEntity.getRemindState() == RemindEntity.NEW ? Color.RED : Color.WHITE);
		
		return convertView;
	}

	private class ViewHolder {
		/**
		 * 时间
		 */
		TextView remind_time_txt;
		/**
		 * 标题
		 */
		TextView remind_title_txt;
		/**
		 * 内容
		 */
		TextView remind_content_txt;
		/**
		 * 发送人
		 */
		TextView remind_name_txt;
		/**
		 * 头像
		 */
		RoleDetailImageView remind_img;
	}
	
}
