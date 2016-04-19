package com.remind.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.help.remind.R;
import com.remind.asyn.ImageLoader;
import com.remind.dao.PeopelDao;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.entity.PeopelEntity;
import com.remind.http.HttpClient;
import com.remind.util.AppUtil;
import com.remind.view.FixButton;
import com.remind.view.RoundImageView;

public class PeopelAdapter extends BaseAdapter {
	private List<PeopelEntity> datas = new ArrayList<PeopelEntity>();
	private Context context;
	public ImageLoader imageLoader;
	private ViewHolder viewHolder;
	private PeopelDao peopelDao;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				String s = (String) msg.obj;
				Toast.makeText(context, s,
						Toast.LENGTH_SHORT).show();
				
				// 点击接受后的处理
				Bundle bundle = msg.getData();
				PeopelEntity entity = (PeopelEntity) bundle.getSerializable("PeopelEntity");
				entity.setStatus(PeopelEntity.FRIEND);
				peopelDao.updatePeopel(entity);
				notifyDataSetChanged();
				break;

			default:
				break;
			}
		};
	};

	public PeopelAdapter(Context context, List<PeopelEntity> datas) {
		this.context = context;
		this.datas = datas;
		imageLoader = ImageLoader.getInstance(context);
		peopelDao = new PeopelDaoImpl(context);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.peopel_list_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.num = (TextView) convertView.findViewById(R.id.num);
			viewHolder.personImg = (RoundImageView) convertView.findViewById(R.id.person_img);
			viewHolder.status = (FixButton) convertView.findViewById(R.id.peopel_status);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		initStatusBtn(viewHolder.status, entity);
		
		viewHolder.name.setText(AppUtil.getName(entity));
		viewHolder.num.setText(entity.getNum());
		
		String imgPath = entity.getImgPath();
		viewHolder.personImg.setTag(imgPath);
		viewHolder.personImg.setImageResource(R.drawable.white);
		
		setStatusButton(entity, viewHolder.status);

		if (null != imgPath && imgPath.trim().length() > 0 && viewHolder.personImg.getTag() != null && viewHolder.personImg.getTag().equals(imgPath)) {
			imageLoader.DisplayImage(imgPath, viewHolder.personImg);
		}
		
		return convertView;
	}
	
	/**
	 * 初始化状态按钮
	 */
	private void initStatusBtn(Button statusBtn, final PeopelEntity entity) {
		statusBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 发送添加好友
				String param = HttpClient.getJsonForPost(HttpClient.agreeFriend(entity.getNum(), "1"));
				agreeFriend(param, entity);
//				// 点击接受后的处理
//				entity.setStatus(PeopelEntity.FRIEND);
//				peopelDao.updatePeopel(entity);
//				notifyDataSetChanged();
			}
		});
	}
	
	private void agreeFriend(final String params, final PeopelEntity entity) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String s = HttpClient.post(HttpClient.url + HttpClient.agree_friend, params);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = s;
				Bundle bundle = new Bundle();
				bundle.putSerializable("PeopelEntity", entity);
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	/**
	 * 修改状态按钮的显示
	 * @param entity
	 * @param statusBtn
	 */
	private void setStatusButton(PeopelEntity entity, Button statusBtn) {
		int status = entity.getStatus();
		Resources resources = context.getResources();
		
		switch (status) {
		case PeopelEntity.VALIDATE:
			statusBtn.setText(resources.getString(R.string.validate));
			statusBtn.setEnabled(false);
			break;
		case PeopelEntity.ACCEPT:
			statusBtn.setText(resources.getString(R.string.accept));
			statusBtn.setEnabled(true);
			break;
		case PeopelEntity.FRIEND:
			statusBtn.setText(resources.getString(R.string.friend));
			statusBtn.setEnabled(false);
			break;
		default:
			break;
		}
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
		FixButton status;
		RoundImageView personImg;
	}
	
	/**
	 * 清除图片缓存
	 */
	public void clearPic() {
		imageLoader.clearCache();
	}
}
