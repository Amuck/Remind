package com.remind.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.help.remind.R;
import com.remind.asyn.ImageLoader;
import com.remind.entity.MessageIndexEntity;
import com.remind.view.RoundImageView;

public class MessageIndexAdapter extends BaseAdapter {
	private List<MessageIndexEntity> datas = new ArrayList<MessageIndexEntity>();
	private Context context;
	public ImageLoader imageLoader;
	private ViewHolder viewHolder;

	public MessageIndexAdapter(Context context, List<MessageIndexEntity> datas) {
		this.context = context;
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
		MessageIndexEntity entity = datas.get(position);

		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.message_index_list_item, null);

			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.personImg = (RoundImageView) convertView
					.findViewById(R.id.person_img);
			viewHolder.msg = (TextView) convertView.findViewById(R.id.msg);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.name.setText(entity.getName());
		viewHolder.time.setText(entity.getTime());
		viewHolder.msg.setText(entity.getMessage());

		String imgPath = entity.getImgPath();
		viewHolder.personImg.setTag(imgPath);
		viewHolder.personImg.setImageResource(R.drawable.white);

		if (null != imgPath && imgPath.trim().length() > 0
				&& viewHolder.personImg.getTag() != null
				&& viewHolder.personImg.getTag().equals(imgPath)) {
			imageLoader.DisplayImage(imgPath, viewHolder.personImg);
		}

		return convertView;
	}

	private class ViewHolder {
		TextView name;
		TextView time;
		TextView msg;
		RoundImageView personImg;
	}
}
