package com.remind.activity;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.ecloud.pulltozoomview.PullToZoomListViewEx;
import com.help.remind.R;
import com.remind.adapter.RemindAdapter;
import com.remind.application.RemindApplication;
import com.remind.asyn.ImageLoader;
import com.remind.global.AppConstant;
import com.remind.util.AppUtil;

public class HomeActivity extends BaseActivity implements OnClickListener{

	private PullToZoomListViewEx listView;

	/**
	 * 用户信息按钮
	 */
	private Button userInfoBtn;
	/**
	 * 更多按钮
	 */
	private Button moreBtn;
	/**
	 * 日期
	 */
	private TextView dateTxt;
	/**
	 * 温度
	 */
	private TextView tempTxt;
	/**
	 * 天气
	 */
	private TextView weatherTxt;
	/**
	 * 地点
	 */
	private TextView locTxt;
	/**
	 * 天气图片
	 */
	private ImageView weatherImg;
	// 天气组件
	private LocationClient mLocationClient;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);

		createFile();

		userInfoBtn = (Button) findViewById(R.id.user_btn);
		moreBtn = (Button) findViewById(R.id.more_btn);
		dateTxt = (TextView) findViewById(R.id.date_txt);
		tempTxt = (TextView) findViewById(R.id.temp_txt);
		weatherTxt = (TextView) findViewById(R.id.weather_txt);
		locTxt = (TextView) findViewById(R.id.loc_txt);
		weatherImg = (ImageView) findViewById(R.id.weather_img);
		listView = (PullToZoomListViewEx) findViewById(R.id.listview);

		getLocationAndWeather();
		setUpView();
	}
	
	/**
	 * 获取地址和天气
	 */
	private void getLocationAndWeather() {
		mLocationClient = ((RemindApplication)getApplication()).mLocationClient;
		((RemindApplication)getApplication()).mLocationResult = locTxt;
		((RemindApplication)getApplication()).mTemp = tempTxt;
		((RemindApplication)getApplication()).mWeather = weatherTxt;
		((RemindApplication)getApplication()).mWeatherImg = weatherImg;
		
		initLocation();
		mLocationClient.start();
	}
	
	/**
	 * 初始化定位选项
	 */
	private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Battery_Saving);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("gcj02");//可选，默认gcj02，设置返回的定位结果坐标系，
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
//        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        option.setIsNeedAddress(checkGeoLocation.isChecked());//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(false);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option);
    }

	/**
	 * 初始化界面
	 */
	private void setUpView() {
		// set up listview
		String[] adapterData = new String[] { "Activity", "Service",
				"Content Provider", "Intent", "BroadcastReceiver", "ADT",
				"Sqlite3", "HttpClient", "DDMS", "Android Studio", "Fragment",
				"Loader", "Activity", "Service", "Content Provider", "Intent",
				"BroadcastReceiver", "ADT", "Sqlite3", "HttpClient",
				"Activity", "Service", "Content Provider", "Intent",
				"BroadcastReceiver", "ADT", "Sqlite3", "HttpClient" };

		RemindAdapter remindAdapter = new RemindAdapter(this, null);
//		listView.setAdapter(new ArrayAdapter<String>(HomeActivity.this,
//				android.R.layout.simple_list_item_1, adapterData));
		listView.setAdapter(remindAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.e("zhuwenwu", "position = " + position);
				
//				initLocation();
//				mLocationClient.start();
			}
		});

		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		// int mScreenHeight = localDisplayMetrics.heightPixels;
		int mScreenWidth = localDisplayMetrics.widthPixels;
		// AbsListView.LayoutParams localObject = new
		// AbsListView.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth /
		// 16.0F)));
		AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(
				mScreenWidth, AppUtil.dip2px(this, 100));
		listView.setHeaderLayoutParams(localObject);
		
		// set up title view
		userInfoBtn.setOnClickListener(this);
		moreBtn.setOnClickListener(this);
//		dateTxt.setText("");
	}

	/**
	 * 创建文件夹
	 * 
	 * @return
	 */
	public void createFile() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// SdCard可用

		} else {
			// SdCard不可用
			AppConstant.MNT = Environment.getRootDirectory().getAbsolutePath();
		}
		// 创建数据库文件夹
		File dbFile = new File(AppConstant.MNT + AppConstant.FILE_PATH
				+ AppConstant.DB_FILE_PATH);
		if (!dbFile.exists()) {
			dbFile.mkdirs();
		}
		// 创建音频文件夹
		File audio = new File(AppConstant.MNT + AppConstant.FILE_PATH
				+ AppConstant.EDITED_AUDIO_PATH);
		if (!audio.exists()) {
			audio.mkdirs();
		}
		// 创建图片文件夹
		File img = new File(AppConstant.MNT + AppConstant.FILE_PATH
				+ AppConstant.EDITED_IMG_PATH);
		if (!img.exists()) {
			img.mkdirs();
		}
		// 创建视频文件夹
		File video = new File(AppConstant.MNT + AppConstant.FILE_PATH
				+ AppConstant.EDITED_VEDIO_PATH);
		if (!video.exists()) {
			video.mkdirs();
		}
		// 创建日志文件夹
		File log = new File(AppConstant.MNT + AppConstant.FILE_PATH
				+ AppConstant.ERROR_PATH);
		if (!log.exists()) {
			log.mkdirs();
		}
	}

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stop();
        ((RemindApplication)getApplication()).cancelRequest();
    }
    
	@Override
	protected void onDestroy() {
		ImageLoader.getInstance(this).clearCache();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_btn:
			
			break;
		case R.id.more_btn:
			break;
		default:
			break;
		}
	}
}