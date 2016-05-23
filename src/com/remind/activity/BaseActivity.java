package com.remind.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

import com.ab.activity.AbActivity;

public class BaseActivity extends AbActivity {
	// 显示进度条
	private final int PAGE_START = 10000;
	// 隐藏进度条
	private final int PAGE_END = 10001;
	// 请求的进度条
	private ProgressDialog mProgressDialog;
	
	public Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case PAGE_START:
				if (mProgressDialog != null && !mProgressDialog.isShowing()) {
					mProgressDialog.show();
				}
				break;

			case PAGE_END:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();

					// refreshUI();
				}
				break;
			}
		};

	};
	
	OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				return true;
			} else {
				return false;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("loading...");
		mProgressDialog.setOnKeyListener(keylistener);
		mProgressDialog.setCanceledOnTouchOutside(false);
		
//		Thread.setDefaultUncaughtExceptionHandler(AppException
//				.getAppExceptionHandler());
//		AppManager.getAppManager().addActivity(this);
//		
//		getWindow().setSoftInputMode(
//				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public void showProgess() {
		mHandler.sendEmptyMessage(PAGE_START);
	}

	public void hideProgess() {
		mHandler.sendEmptyMessage(PAGE_END);
	}
	
	public boolean isProgesShow() {
		return mProgressDialog.isShowing();
	}
	/**
	 * 
	 * Toast 的提示
	 */
	public void showToast(final String content) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast toast = Toast.makeText(BaseActivity.this, content,
						Toast.LENGTH_SHORT);
				toast.show();
			}
		});

	}
	/**
	 * 
	 * Toast 的提示
	 */
	public void showToast(final String content,final String type) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast toast=null;
				if("S".equals(type)){					
					toast = Toast.makeText(BaseActivity.this, content,
							Toast.LENGTH_SHORT);
				}else if("L".equals(type)){
					toast = Toast.makeText(BaseActivity.this, content,
							Toast.LENGTH_LONG);
				}
				toast.show();
			}
		});

	}
}
