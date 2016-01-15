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
import com.remind.asyn.ImageLoader;
import com.remind.dao.PeopelDao;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.entity.RemindEntity;
import com.remind.util.Utils;
import com.remind.view.RoleDetailImageView;

public class RemindAdapter extends BaseAdapter {
	
	private List<RemindEntity> datas;
	private Context context;
	private ViewHolder viewHolder;
	private LayoutInflater mInflater;
	private PeopelDao peopelDao;
	public ImageLoader imageLoader;

	public RemindAdapter(Context context, List<RemindEntity> datas) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		peopelDao = new PeopelDaoImpl(this.context);
		this.datas = datas;
		imageLoader = ImageLoader.getInstance(context);
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
		
		String imgPath = peopelDao.getImgPath(remindEntity.getTargetNum());
		viewHolder.remind_img.setTag(imgPath);
		int id = Utils.getResoureIdbyName(context, imgPath);
		if (0 == id) {
			// 如果头像是用户上传的图片
			if (null != imgPath && imgPath.trim().length() > 0 && viewHolder.remind_img.getTag() != null && viewHolder.remind_img.getTag().equals(imgPath)) {
				imageLoader.DisplayImage(imgPath, viewHolder.remind_img);
			}
		} else {
			// 如果头像是软件自带的图片
			viewHolder.remind_img.setImageResource(id);
		}

		
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
