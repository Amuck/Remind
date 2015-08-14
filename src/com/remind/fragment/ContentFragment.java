package com.remind.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.help.remind.R;
import com.remind.activity.AddRemindActivity;
import com.remind.activity.ChatActivity;
import com.remind.dao.MessageIndexDao;
import com.remind.dao.RemindDao;
import com.remind.dao.impl.MessageIndexDaoImpl;
import com.remind.dao.impl.RemindDaoImpl;
import com.remind.entity.MessageIndexEntity;
import com.remind.entity.RemindEntity;
import com.remind.util.AppUtil;
import com.remind.util.DataBaseParser;
import com.remind.view.CustomSinnper;
import com.remind.view.CustomSinnper.OnItemSeletedListener;

/**
 * @author ChenLong
 * 
 *         互助提醒主界面，查看所有提醒
 */
@SuppressLint("ValidFragment")
public class ContentFragment extends Fragment {
	public final static int ADD_REMIND = 3002;
	
	/**
	 * 跳转到聊天
	 */
	public final static int CHAT = 3003;

	private View view;
//	private SlidingMenu menu;
	private Context mContext;
	private List<ApplicationInfo> mAppList;
	private Handler handler = new Handler();
	private Runnable myRunnable;
	private ListView listView;

	/**
	 * 添加提醒
	 */
	private ImageButton addRemind;
	/**
	 * 提醒种类
	 */
	private CustomSinnper remindTypeSpinner;
	private TextView title;
	private ArrayAdapter<String> remindTypeAdapter;

	private RemindDao remindDao;
	private MessageIndexDao messageIndexDao;

	private List<MessageIndexEntity> datas = new ArrayList<MessageIndexEntity>();

//	private AppAdapter adapter;
	private MessageIndexAdapter messageIndexAdapter;

	/**
	 * 当前显示的提醒类型，默认为"已接受的提醒"
	 */
	private int curRemindType = 0;
	
	private MessageIndexEntity curMessageIndex = null;

	private OnClickListener addRemindClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// 添加提醒
			Intent intent = new Intent(getActivity(), AddRemindActivity.class);
			startActivityForResult(intent, ADD_REMIND);
		}
	};

	/**
	 * 切换提醒类别监听
	 */
	private OnItemSeletedListener remindSpinnerListener = new OnItemSeletedListener() {

		@Override
		public void onItemSeleted(AdapterView<?> parent, View view,
				int position, long id) {
			// 切换提醒类别
			// String[] mItems = getResources()
			// .getStringArray(R.array.remind_type);
			// Toast.makeText(getActivity(), "你点击的是:" + mItems[position], 2000)
			// .show();
//			curRemindType = position;
//
//			RemindEntity entity = new RemindEntity();
//			entity.setRemindState(position);
//			Cursor cursor = remindDao.queryRemind(entity);
//			datas = DataBaseParser.getRemindDetail(cursor);
//			cursor.close();
//
//			messageIndexAdapter.notifyDataSetChanged();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		if (null != view) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (null != parent) {
				parent.removeView(view);
			}
		} else {
			view = inflater.inflate(R.layout.layout_content, null);
			remindDao = new RemindDaoImpl(mContext);
			messageIndexDao = new MessageIndexDaoImpl(mContext);
			
			setViewUp(view);
		}

		return view;
	}

	private void setViewUp(View view) {
		addRemind = (ImageButton) view.findViewById(R.id.title_add);
		setupImageButton();

		remindTypeSpinner = (CustomSinnper) view
				.findViewById(R.id.title_spinner);
		setupSpinner();

		title = (TextView) view.findViewById(R.id.title_text);
//		title.getPaint().setFakeBoldText(true);// 加粗

		mAppList = mContext.getPackageManager().getInstalledApplications(0);
		listView = (ListView) view.findViewById(R.id.listView);

		// 加载数据
		getDatas();

		messageIndexAdapter = new MessageIndexAdapter(mContext, datas);
		listView.setAdapter(messageIndexAdapter);

		// step 1. create a MenuCreator
//		SwipeMenuCreator creator = new SwipeMenuCreator() {
//
//			@Override
//			public void create(SwipeMenu menu) {
//				// create "open" item
//				SwipeMenuItem editItem = new SwipeMenuItem(
//						mContext.getApplicationContext());
//				// set item background
//				editItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
//						0xCE)));
//				// set item width
//				editItem.setWidth(dp2px(90));
//				// set item title
//				editItem.setTitle("Edit");
//				// set item title fontsize
//				editItem.setTitleSize(18);
//				// set item title font color
//				editItem.setTitleColor(Color.WHITE);
//				// add to menu
//				menu.addMenuItem(editItem);
//
//				// create "delete" item
//				SwipeMenuItem deleteItem = new SwipeMenuItem(
//						mContext.getApplicationContext());
//				// set item background
//				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
//						0x3F, 0x25)));
//				// set item width
//				deleteItem.setWidth(dp2px(90));
//				// set a icon
//				deleteItem.setIcon(R.drawable.ic_delete);
//				// add to menu
//				menu.addMenuItem(deleteItem);
//
//			}
//		};
		// set creator
//		listView.setMenuCreator(creator);

		// step 2. listener item click event
//		listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//			@Override
//			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
//				ApplicationInfo item = mAppList.get(position);
//				switch (index) {
//				case 0:
//					// 编辑
//					// open(item);
//					Toast.makeText(mContext, position + " 编辑", 0).show();
//					break;
//				case 1:
//					// 删除
//					// delete(item);
////					Toast.makeText(mContext, position + " 删除", 0).show();
//					RemindEntity entity = datas.get(position);
//					remindDao.deleteById(entity.getId());
//					datas.remove(entity);
//					
//					adapter.notifyDataSetChanged();
//					break;
//				}
//			}
//		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(mContext, position + " long click", 0).show();
				return false;
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 打开
				curMessageIndex = datas.get(position);
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				intent.putExtra("num", curMessageIndex.getNum());
				startActivityForResult(intent, CHAT);
			}
		});

//		listView.setOnOpenOrCloseListener(new OpenOrCloseListener() {
//
//			@Override
//			public void isOpen(boolean isOpen) {
//				if (!isOpen) {// ����Ǵ򿪵�
//					menu.setMode(SlidingMenu.LEFT);
//					menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//					handler.removeCallbacks(myRunnable);
//				} else { // ���ǹرյ�
//					menu.setMode(SlidingMenu.LEFT_RIGHT);
//					menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
//					resetMenu();
//				}
//
//			}
//
//		});

	}
	
	/**
	 * 查询数据
	 */
	private void getDatas() {
		Cursor cursor = messageIndexDao.queryAll();
		datas = DataBaseParser.getMessageIndex(cursor);
		cursor.close();
	}

	/**
	 * 初始化按钮
	 */
	private void setupImageButton() {
		// 添加监听器
		addRemind.setOnClickListener(addRemindClickListener);
	}

	/**
	 * 初始化下拉框
	 */
	private void setupSpinner() {
		// 建立数据源
		String[] mItems = getResources().getStringArray(R.array.remind_type);
		remindTypeAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.simple_spinner_item, mItems);
		// 绑定 Adapter到控件
		remindTypeSpinner.setAdapter(remindTypeAdapter);
		remindTypeSpinner.setOnItemSeletedListener(remindSpinnerListener);
	}

//	private void resetMenu() {
//		handler.postDelayed(myRunnable = new Runnable() {
//			@Override
//			public void run() {
//				menu.setMode(SlidingMenu.LEFT);
//				menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//			}
//		}, 300);
//	}

//	public ContentFragment(SlidingMenu menu) {
//		super();
//		this.menu = menu;
//	}

	private void delete(ApplicationInfo item) {
		// delete app
		try {
			Intent intent = new Intent(Intent.ACTION_DELETE);
			intent.setData(Uri.fromParts("package", item.packageName, null));
			startActivity(intent);
		} catch (Exception e) {
		}
	}

	private void open(ApplicationInfo item) {
		// open app
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(item.packageName);
		List<ResolveInfo> resolveInfoList = mContext.getPackageManager()
				.queryIntentActivities(resolveIntent, 0);
		if (resolveInfoList != null && resolveInfoList.size() > 0) {
			ResolveInfo resolveInfo = resolveInfoList.get(0);
			String activityPackageName = resolveInfo.activityInfo.packageName;
			String className = resolveInfo.activityInfo.name;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName componentName = new ComponentName(
					activityPackageName, className);

			intent.setComponent(componentName);
			startActivity(intent);
		}
	}

	class AppAdapter extends BaseAdapter {
		private ViewHolder viewHolder;

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
			if (convertView == null) {
				convertView = View.inflate(mContext.getApplicationContext(),
						R.layout.item_list_app, null);
				viewHolder = new ViewHolder(convertView);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			RemindEntity item = (RemindEntity) getItem(position);
			// holder.iv_icon.setImageDrawable(item.loadIcon(mContext
			// .getPackageManager()));
			viewHolder.tv_name.setText(item.getTitle());

			getBtnStatus(item, viewHolder.remindStatus, viewHolder.refuse);
			setBtnListener(item, viewHolder.remindStatus);

			return convertView;
		}

		/**
		 * 返回当前提醒的状态
		 * 
		 * @param item
		 * @param stateBtn
		 *            状态（接受）按钮
		 * @param refuseBtn
		 *            拒绝按钮
		 * @return
		 */
		private void getBtnStatus(RemindEntity item, Button stateBtn,
				Button refuseBtn) {
			String status = "接受";
			boolean isEnable = false;
			// 我发起的提醒的状态
			int launchState = item.getLaunchState();
			switch (curRemindType) {
			case RemindEntity.ACCEPT:
				// 已接受
				status = "已接受";
				refuseBtn.setVisibility(View.INVISIBLE);
				break;
			case RemindEntity.NEW:
				// 新的
				status = "接受";
				isEnable = true;
				refuseBtn.setVisibility(View.VISIBLE);
				break;
			case RemindEntity.LAUNCH:
				// 我发起的
				status = "等待对方接受";
				{
					switch (launchState) {
					case RemindEntity.LAUNCH_ACCEPT:
						// 对方已接受
						status = "对方已接受";
						break;
					case RemindEntity.LAUNCH_WAIT:
						// 等待对方接受
						status = "等待对方接受";
						break;
					case RemindEntity.LAUNCH_REFUSE:
						// 对方已拒绝
						status = "对方已拒绝";
						break;

					default:
						break;
					}
				}
				refuseBtn.setVisibility(View.INVISIBLE);
				break;
			case RemindEntity.REFUSE:
				// 已拒绝
				status = "已拒绝";
				refuseBtn.setVisibility(View.INVISIBLE);
				break;

			default:
				break;
			}

			stateBtn.setText(status);
			stateBtn.setEnabled(isEnable);
		}
		
		/**
		 * 点击接受后，开启闹钟
		 * 
		 * @param item
		 * @param stateBtn
		 */
		private void setBtnListener(final RemindEntity item, Button stateBtn) {
			stateBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AppUtil.setAlarm(mContext, item.getRemindTime(), Integer.valueOf(item.getId()));
				}
			});
		}

		class ViewHolder {
			ImageView iv_icon;
			TextView tv_name;
			Button remindStatus;
			Button refuse;

			public ViewHolder(View view) {
				iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				tv_name = (TextView) view.findViewById(R.id.tv_name);
				remindStatus = (Button) view.findViewById(R.id.remind_status);
				refuse = (Button) view.findViewById(R.id.remind_refuse);
				view.setTag(this);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case ADD_REMIND:
			// 添加新提醒成功,刷新页面,跳转到“我发起的提醒”
			remindTypeSpinner
					.setAdapter(remindTypeAdapter, RemindEntity.LAUNCH);
			break;
		case CHAT:
			// 刷新最后的聊天内容和时间，发送状态
			if (null != curMessageIndex) {
				Cursor cursor = messageIndexDao.queryByNum(curMessageIndex.getNum());
				ArrayList<MessageIndexEntity> messageIndexEntities = DataBaseParser
						.getMessageIndex(cursor);
				MessageIndexEntity temp = messageIndexEntities.get(0);
				cursor.close();
				
				curMessageIndex.setTime(temp.getTime());
				curMessageIndex.setMessage(temp.getMessage());
				curMessageIndex.setSendState(temp.getSendState());
				
				messageIndexAdapter.notifyDataSetChanged();
			}
			break;

		default:
			break;
		}
	}
}
