package com.remind.activity;

import java.io.File;
import java.io.InputStream;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.help.remind.R;
import com.remind.application.RemindApplication;
import com.remind.dao.MessageIndexDao;
import com.remind.dao.PeopelDao;
import com.remind.dao.impl.MessageIndexDaoImpl;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.entity.MessageIndexEntity;
import com.remind.entity.PeopelEntity;
import com.remind.global.AppConstant;
import com.remind.sp.MySharedPreferencesLoginType;
import com.remind.util.AppUtil;
import com.remind.util.Utils;
import com.remind.view.RoleDetailImageView;

/**
 * @author ChenLong
 * 
 *         联系人详细信息（编辑联系人信息）
 */
public class EditPeopelActivity extends BaseActivity implements OnClickListener {
	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 3023;
	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	/* 用来标识请求裁剪图片后的activity */
	private static final int CAMERA_CROP_DATA = 3022;
	private String TAG = "EditPeopelActivity";
	/**
	 * 是否是编辑状态
	 */
	// private boolean isEdit = false;

	/**
	 * 信息是否有改变
	 */
	private boolean isChanged = false;

	/**
	 * 联系人详细
	 */
	private PeopelEntity peopelEntity;

	/**
	 * 编辑昵称
	 */
	private EditText nickNameEdit;
	/**
	 * 头像
	 */
	private RoleDetailImageView imgView;
	/**
	 * 编辑头像按钮
	 */
	private Button editImgBtn;

	/**
	 * 进入编辑状态
	 */
	private ImageButton editBtn;
	/**
	 * 确定
	 */
	private Button okBtn;
	/**
	 * 取消
	 */
	private Button cancelBtn;
	/**
	 * 退出
	 */
	private Button exitBtn;

	/**
	 * 名字
	 */
	private TextView name;
	/**
	 * 号码
	 */
	private TextView num;

	/**
	 * 头像路径
	 */
	private String imgPath = "";
	/**
	 * 照相机拍照得到的图片
	 */
	private File mCurrentPhotoFile;
	
	private PeopelDao peopelDao;
	
	private MessageIndexDao messageIndexDao;
	/**
	 * 发送消息
	 */
	private Button sendMsg;
	
	/**
	 * 用户是否登陆
	 */
	private boolean isUserLogin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_people_edit);
		
		peopelDao = new PeopelDaoImpl(this);
		messageIndexDao = new MessageIndexDaoImpl(this);

		Intent intent = getIntent();
		peopelEntity = (PeopelEntity) intent.getSerializableExtra("peopel");

		if (null == peopelEntity) {
			AppUtil.showToast(this, "查看联系人信息失败");
			finish();
			return;
		}

		isUserLogin = intent.getBooleanExtra("user", false);
		initView();
	}

	/**
	 * @param isUser		是否是查看登陆用户信息
	 */
	private void initView() {
		name = (TextView) findViewById(R.id.peopel_name_edit);
		num = (TextView) findViewById(R.id.peopel_num_edit);
		nickNameEdit = (EditText) findViewById(R.id.peopel_nick_edit);
		imgView = (RoleDetailImageView) findViewById(R.id.peopel_img_preview);
		editImgBtn = (Button) findViewById(R.id.peopel_img_search);
		editBtn = (ImageButton) findViewById(R.id.title_edit);
		okBtn = (Button) findViewById(R.id.title_ok);
		cancelBtn = (Button) findViewById(R.id.title_cancel);
		sendMsg = (Button) findViewById(R.id.send_msg_btn);
		exitBtn = (Button) findViewById(R.id.exit_btn);
		
		imgPath = peopelEntity.getImgPath();

		editImgBtn.setOnClickListener(this);
		editBtn.setOnClickListener(this);
		okBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		sendMsg.setOnClickListener(this);
		exitBtn.setOnClickListener(this);

		setupView();
		setupImg();
		changeToCheck();
	}
	
	/**
	 * 为界面元素添加数据
	 */
	private void setupView() {
		name.setText(peopelEntity.getName());
		num.setText(peopelEntity.getNum());
		nickNameEdit.setText(peopelEntity.getNickName());
	}

	/**
	 * 设置头像
	 */
	private void setupImg() {
		int id = Utils.getResoureIdbyName(this, imgPath);
		if (0 == id) {
			// 如果头像是用户上传的图片
			
			// 显示图片
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			Bitmap bm = BitmapFactory
					.decodeFile(peopelEntity.getImgPath(), options);

			if (bm == null) {
				// from contacts
				Uri uri = Uri.parse(peopelEntity.getImgPath());
				InputStream input = ContactsContract.Contacts
						.openContactPhotoInputStream(this.getContentResolver(), uri);
				bm = BitmapFactory.decodeStream(input);
			}

			imgView.setImageDrawable(AppUtil.bitmapToDrawable(bm));
			imgView.init();
		} else {
			// 如果头像是软件自带的图片
			imgView.setImageResource(id);
			imgView.init();
		}
	}

	/**
	 * 进入编辑状态
	 */
	private void changeToEdit() {
		nickNameEdit.setFilters(new InputFilter[] { new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				// 设置可编辑状态
				return null;
			}
		} });

		editImgBtn.setVisibility(View.VISIBLE);
		okBtn.setVisibility(View.VISIBLE);
		cancelBtn.setVisibility(View.VISIBLE);
		editBtn.setVisibility(View.GONE);
		sendMsg.setVisibility(View.GONE);
	}

	/**
	 * 进入查看状态
	 */
	private void changeToCheck() {
		nickNameEdit.setFilters(new InputFilter[] { new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				// 设置不可编辑状态
				return source.length() < 1 ? dest.subSequence(dstart, dend)
						: "";
			}
		} });

		editImgBtn.setVisibility(View.GONE);
		okBtn.setVisibility(View.GONE);
		cancelBtn.setVisibility(View.GONE);
		editBtn.setVisibility(View.VISIBLE);
		if (!isUserLogin) {
			sendMsg.setVisibility(View.VISIBLE);
		} else {
			sendMsg.setVisibility(View.GONE);
			exitBtn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.peopel_img_search:
			// 浏览头像
			try {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
				intent.setType("image/*");
				startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
			} catch (ActivityNotFoundException e) {
				AppUtil.showToast(this, "没有找到照片");
			}
			break;
		case R.id.title_edit:
			// 进入编辑模式
			changeToEdit();
			break;
		case R.id.title_ok:
			// 确定
			isChanged();
			
			if (isChanged) {
				peopelEntity.setNickName(nickNameEdit.getText().toString());
				peopelEntity.setImgPath(imgPath);
				peopelDao.updatePeopel(peopelEntity);
				setResult(RESULT_OK);
			} else {
				setResult(RESULT_CANCELED);
			}
			
			changeToCheck();
			break;
		case R.id.title_cancel:
			// 取消
			isChanged();
			
			if (isChanged) {
				setupView();
				
				if (null != imgPath && !imgPath.equals(peopelEntity.getImgPath())) {
					setupImg();
				}
			}
			
			changeToCheck();
			break;
		case R.id.send_msg_btn:
			// 发送消息
			MessageIndexEntity messageIndexEntity = null;
			Cursor cursor = messageIndexDao.queryByNum(peopelEntity.getNum());
			if (cursor.getCount() > 0) {
//				ArrayList<MessageIndexEntity> messageIndexEntities = DataBaseParser.getMessageIndex(cursor);
//				messageIndexEntity = messageIndexEntities.get(0);
			} else {
				messageIndexEntity = new MessageIndexEntity("", peopelEntity.getNum(), 
						"", "", AppUtil.getName(peopelEntity), peopelEntity.getImgPath(), 0, MessageIndexEntity.NORMAL, 
						MessageIndexEntity.SEND_SUCCESS, AppConstant.USER_NUM);
				messageIndexDao.insert(messageIndexEntity);
			}
			cursor.close();
			
			Intent intent = new Intent(EditPeopelActivity.this, ChatActivity.class);
			intent.putExtra("num", peopelEntity.getNum());
			startActivity(intent);
			
			finish();
			break;
		case R.id.exit_btn:
			// 退出登陆
			exit();
			break;

		default:
			break;
		}
	}
	
	/**
	 * 退出登陆
	 */
	private void exit() {
		new AlertDialog.Builder(this).setTitle("是否退出登陆？")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				RemindApplication.IS_LOGIN = false;
				MySharedPreferencesLoginType.setOnlineState(EditPeopelActivity.this, false);
				try {
					RemindApplication.iBackService.release();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				finish();
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		}).create().show();
	}
	
	/**
	 * 是否有数据上的变化
	 * @return
	 */
	private void isChanged() {
		if (null != nickNameEdit.getText().toString()
				&& !nickNameEdit.getText().toString().equals(peopelEntity.getNickName())) {
			isChanged = true;
		}
		if (null == nickNameEdit.getText().toString() && null != peopelEntity.getNickName()) {
			isChanged = true;
		}
		if (null != imgPath && !imgPath.equals(peopelEntity.getImgPath())) {
			isChanged = true;
		}
		if (null == imgPath && null != peopelEntity.getImgPath()) {
			isChanged = true;
		}
	}

	/**
	 * 从相册得到的url转换为SD卡中图片路径
	 */
	@SuppressWarnings("deprecation")
	public String getPath(Uri uri) {
		if (AppUtil.isEmpty(uri.getAuthority())) {
			return null;
		}
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);
		return path;
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent mIntent) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA:
			// 选择图片进入编辑模式
			Uri uri = mIntent.getData();
			String currentFilePath = getPath(uri);
			if (!AppUtil.isEmpty(currentFilePath)) {
				Intent intent1 = new Intent(this, CropImageActivity.class);
				intent1.putExtra("PATH", currentFilePath);
				startActivityForResult(intent1, CAMERA_CROP_DATA);
			} else {
				AppUtil.showToast(this, "未在存储卡中找到这个文件");
			}
			break;
		case CAMERA_WITH_DATA:
			Log.d(TAG, "将要进行裁剪的图片的路径是 = " + mCurrentPhotoFile.getPath());
			String currentFilePath2 = mCurrentPhotoFile.getPath();
			Intent intent2 = new Intent(this, CropImageActivity.class);
			intent2.putExtra("PATH", currentFilePath2);
			startActivityForResult(intent2, CAMERA_CROP_DATA);
			break;
		case CAMERA_CROP_DATA:
			imgPath = mIntent.getStringExtra("PATH");
			Log.d(TAG, "裁剪后得到的图片的路径是 = " + imgPath);
			// mImagePathAdapter.addItem(mImagePathAdapter.getCount()-1,path);
			// camIndex++;
			// AbViewUtil.setAbsListViewHeight(mGridView,3,25);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			Bitmap bm = BitmapFactory.decodeFile(imgPath, options);
			imgView.setImageDrawable(AppUtil.bitmapToDrawable(bm));
			break;
		}
	}
	
}
