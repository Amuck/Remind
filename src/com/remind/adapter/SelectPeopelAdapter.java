package com.remind.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.help.remind.R;
import com.remind.asyn.ImageLoader;
import com.remind.entity.PeopelEntity;
import com.remind.view.RoundImageView;

public class SelectPeopelAdapter extends BaseAdapter {
	/**
	 * @author ChenLong
	 *
	 * 联系人选择监听器
	 */
	public interface PeopelSelected{
		/**
		 * @param position		选择条目的位置
		 * @param entity		选择联系人信息
		 */
		public void onPeopelSelected(int position, PeopelEntity entity);
	}
	
	private Context context;
	private List<PeopelEntity> datas = new ArrayList<PeopelEntity>();
	public ImageLoader imageLoader;
	private ViewHolder viewHolder;
	
	private PeopelSelected peopelSelected = null;

	public SelectPeopelAdapter(Context context, List<PeopelEntity> datas) {
		this.context = context;
		this.datas.addAll(datas);
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
		PeopelEntity entity = datas.get(position);
		
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(R.layout.select_peopel_list_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.num = (TextView) convertView.findViewById(R.id.num);
			viewHolder.personImg = (RoundImageView) convertView.findViewById(R.id.person_img);
			viewHolder.status = (Button) convertView.findViewById(R.id.peopel_status);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.name.setText(getName(entity));
		viewHolder.num.setText(entity.getNum());
		
//		String imgPath = entity.getImgPath();
//		viewHolder.personImg.setTag(imgPath);
//		viewHolder.personImg.setImageResource(R.drawable.white);
		
		initStatusBtn(viewHolder.status, position);

//		if (null != imgPath && imgPath.trim().length() > 0 && viewHolder.personImg.getTag() != null && viewHolder.personImg.getTag().equals(imgPath)) {
//			imageLoader.DisplayImage(imgPath, viewHolder.personImg);
//		}
		
		return convertView;
	}
	
	/**
	 * 初始化状态按钮
	 */
	private void initStatusBtn(Button statusBtn, final int position) {
		statusBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 点击确定后的处理
				if (null != peopelSelected) {
					peopelSelected.onPeopelSelected(position, datas.get(position));
				}
			}
		});
	}

	/**
	 * 获取联系人显示名称
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
	 * 重新加载数据
	 * @param data
	 */
	public void reloadData(List<PeopelEntity> data){
		datas.clear();
		datas.addAll(data);
		notifyDataSetChanged();
	}
	
	private class ViewHolder {
		TextView name;
		TextView num ;
		Button status;
		RoundImageView personImg;
	}

	/**
	 * 设置联系人监听器
	 * 
	 * @param peopelSelected
	 */
	public void setPeopelSelected(PeopelSelected peopelSelected) {
		this.peopelSelected = peopelSelected;
	}
	
}
