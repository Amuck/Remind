package com.remind.activity;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.help.remind.R;
import com.remind.application.RemindApplication;
import com.remind.dao.PeopelDao;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.entity.PeopelEntity;
import com.remind.http.HttpClient;
import com.remind.util.AppUtil;

/**
 * @author ChenLong
 *
 *	联系人界面
 */
public class ContactsActivity extends ListActivity {

	private Context mContext = null;

	/** 获取库Phon表字段 **/
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** 头像ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;

	/** 联系人的ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;

	/** 联系人名称 **/
	private ArrayList<String> mContactsName = new ArrayList<String>();

	/** 联系人头像 **/
	private ArrayList<String> mContactsNumber = new ArrayList<String>();

	/** 联系人头像 **/
	private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();

	private ArrayList<String> mContactsUri = new ArrayList<String>();

	private ListView mListView = null;
	private MyListAdapter myAdapter = null;

	private PeopelDao peopelDao;

	private AlertDialog alertDialog;

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				String s = (String) msg.obj;
				Toast.makeText(ContactsActivity.this, s,
						Toast.LENGTH_SHORT).show();
				
				//返回上一个界面
				setResult(RESULT_OK);
				finish();
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mContext = this;
		mListView = this.getListView();
		/** 得到手机通讯录联系人信息 **/
		getPhoneContacts();

		myAdapter = new MyListAdapter(this);
		setListAdapter(myAdapter);

		peopelDao = new PeopelDaoImpl(this);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				if (peopelDao.queryPeopelByNum(mContactsNumber.get(position))
						.getCount() > 0) {
					// 已经添加联系人
					AppUtil.showToast(mContext, "您已经添加过TA了，再换一个吧");
					return;
				}

				// 发送短息
				sendSms(position);
			}
		});

		super.onCreate(savedInstanceState);
	}
	
	private void friend(final String params) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String s = HttpClient.post(HttpClient.url + HttpClient.friend, params);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = s;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 发送短信
	 */
	private void sendSms(final int position) {
		if (null == alertDialog) {
			alertDialog = new AlertDialog.Builder(mContext).setTitle("是否发送短息？")
					.setMessage("我们将要发送一条短信给对方，以便将其加为您的好友，将收取正常的短信费用，请问是否继续？")
					.setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							alertDialog.dismiss();
						}
					}).setPositiveButton("继续", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 发送短信
							String phone_number = mContactsNumber.get(position)
									.trim();
							String sms_content = "您的好友"
									+ mContactsName.get(position)
									+ "邀请您加入互助提醒， 下载地址" + "XXXXXX";
							if (phone_number.equals("")) {
								AppUtil.showToast(mContext, "对方号码为空");
							} else {
								// SmsManager smsManager =
								// SmsManager.getDefault();
								// if(sms_content.length() > 70) {
								// List<String> contents =
								// smsManager.divideMessage(sms_content);
								// for(String sms : contents) {
								// smsManager.sendTextMessage(phone_number,
								// null, sms, null, null);
								// }
								// } else {
								// smsManager.sendTextMessage(phone_number,
								// null, sms_content, null, null);
								// }
								AppUtil.showToast(mContext, "发送成功");
							}

							String param = HttpClient.getJsonForPost(HttpClient.friendUser(phone_number));
							friend(param);
							// 添加联系人
//							addPersonIntoDB(position);

							alertDialog.dismiss();

							//返回上一个界面
//							setResult(RESULT_OK);
//							finish();
						}
					}).create();
		}
		alertDialog.show();
	}

	/**
	 * 将联系人插入数据库
	 * 
	 * @param position
	 */
	private void addPersonIntoDB(int position) {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String num = mContactsNumber.get(position).replaceAll("-", "").replaceAll(" ", "");
		PeopelEntity entity = new PeopelEntity(mContactsName.get(position),
				mContactsName.get(position), num,
				format.format(date), format.format(date),
				mContactsUri.get(position), PeopelEntity.NORMAL,
				PeopelEntity.VALIDATE, "");
		peopelDao.insertPeopel(entity);
	}

	/** 得到手机通讯录联系人信息 **/
	private void getPhoneContacts() {
		ContentResolver resolver = mContext.getContentResolver();

		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;

				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				Uri uri = Uri.parse("");
				if (photoid > 0) {
					uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(getResources(),
							R.drawable.white);
				}

				mContactsName.add(contactName);
				mContactsNumber.add(phoneNumber);
				mContactsPhonto.add(contactPhoto);
				mContactsUri.add(uri.toString());
			}

			phoneCursor.close();
		}
	}

	/** 得到手机SIM卡联系人人信息 **/
	private void getSIMContacts() {
		ContentResolver resolver = mContext.getContentResolver();
		// 获取Sims卡联系人
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// Sim卡中没有联系人头像

				mContactsName.add(contactName);
				mContactsNumber.add(phoneNumber);
			}

			phoneCursor.close();
		}
	}

	class MyListAdapter extends BaseAdapter {
		private ViewHolder viewHolder;
		
		public MyListAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			// 设置绘制数量
			return mContactsName.size();
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.colorlist, null);
				viewHolder.img = (ImageView) convertView.findViewById(R.id.color_image);
				viewHolder.title = (TextView) convertView.findViewById(R.id.color_title);
				viewHolder.text = (TextView) convertView.findViewById(R.id.color_text);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			// 绘制联系人名称
			viewHolder.title.setText(mContactsName.get(position));
			// 绘制联系人号码
			viewHolder.text.setText(mContactsNumber.get(position));
			// 绘制联系人头像
			viewHolder.img.setImageBitmap(mContactsPhonto.get(position));
			return convertView;
		}

		private class ViewHolder {
			ImageView img;
			TextView title;
			TextView text;
		}
	}
}
