package com.remind.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
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
import com.remind.dao.RemindDao;
import com.remind.dao.impl.RemindDaoImpl;
import com.remind.entity.RemindEntity;
import com.remind.global.AppConstant;
import com.remind.sp.WeatherSp;
import com.remind.util.AppUtil;
import com.remind.util.DataBaseParser;

/**
 * @author ChenLong
 *
 *	主页面
 */
public class HomeActivity extends BaseActivity implements OnClickListener {

	private final static int LOAD_OK = 100;
//	private final static int REFRESH_START = 101;
	
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

	private int number = 10; // 每次获取多少条数据
	private int maxpage = 0; // 总共有多少页
	private int maxCount = 0; // 总共有多少条目
	/**
	 * 未接受的提醒数
	 */
	private int notAcceptCount = 0;
	/**
	 * 今天提醒数
	 */
	private int todayCount = 0;
	/**
	 * 其他提醒数
	 */
	private int otherDayCount = 0;
	private boolean loadfinish = true; // 指示数据是否加载完成
	private View footer;
	private RemindAdapter remindAdapter;
	private List<RemindEntity> datas = new ArrayList<RemindEntity>();// 数据源
	
	/**
	 * 提醒数据库
	 */
	private RemindDao remindDao;
	private String today;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOAD_OK:
				// 告诉ListView数据已经发生改变，要求ListView更新界面显示
				remindAdapter.notifyDataSetChanged();
				if (listView.getFooterViewsCount() > 0) { // 如果有底部视图
					listView.removeFooterView(footer);
				}
				loadfinish = true; // 加载完成
				break;
//			case REFRESH_START:
//				Toast.makeText(HomeActivity.this, "start---refresh", Toast.LENGTH_LONG).show();
//				break;

			default:
				break;
			}
			
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		footer = LayoutInflater.from(this).inflate(R.layout.itembottom, null);
		remindDao = new RemindDaoImpl(this);
		today = dateFormat.format(new Date());

		initWeather();
		getLocationAndWeather();
		setUpView();
	}
	
	/**
	 * 显示上一次的天气信息
	 */
	private void initWeather() {
		locTxt.setText(WeatherSp.getString(this, WeatherSp.LOC));
		tempTxt.setText(WeatherSp.getString(this, WeatherSp.TEM));
		weatherTxt.setText(WeatherSp.getString(this, WeatherSp.WEATHER));
		weatherImg.setBackgroundResource(WeatherSp.getInt(this, WeatherSp.WEATHER_IMG));
	}

	/**
	 * 获取地址和天气
	 */
	private void getLocationAndWeather() {
		mLocationClient = ((RemindApplication) getApplication()).mLocationClient;
		((RemindApplication) getApplication()).mLocationResult = locTxt;
		((RemindApplication) getApplication()).mTemp = tempTxt;
		((RemindApplication) getApplication()).mWeather = weatherTxt;
		((RemindApplication) getApplication()).mWeatherImg = weatherImg;

		initLocation();
		mLocationClient.start();
	}

	/**
	 * 初始化定位选项
	 */
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Battery_Saving);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("gcj02");// 可选，默认gcj02，设置返回的定位结果坐标系，
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		// option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		// option.setIsNeedAddress(checkGeoLocation.isChecked());//可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(false);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(false);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIgnoreKillProcess(true);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		mLocationClient.setLocOption(option);
	}

	/**
	 * 初始化界面
	 */
	private void setUpView() {
		// set up listview
//		listView.setOnScrollListener(new ScrollListener());
		
		remindAdapter = new RemindAdapter(this, datas);
		listView.setAdapter(remindAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});

		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		int mScreenWidth = localDisplayMetrics.widthPixels;
		AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(
				mScreenWidth, AppUtil.dip2px(this, 100));
		listView.setHeaderLayoutParams(localObject);

		// set up title view
		userInfoBtn.setOnClickListener(this);
		moreBtn.setOnClickListener(this);
		dateTxt.setText(AppUtil.getToday());
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
	protected void onResume() {
		super.onResume();
//		resetDataPage();
//		getMoreData(1);
		getData();
		remindAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 重置数据库查询分页查询索引
	 */
	private void resetDataPage() {
		maxCount = remindDao.getEffectiveCount();
		notAcceptCount = remindDao.getUnAcceptCount();
		todayCount  = remindDao.getTodayCount(today);
		otherDayCount = remindDao.getOtherdayCount(today);
		maxpage = (int) Math.ceil((double ) maxCount/ (double )number);
		datas.clear();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mLocationClient.stop();
		((RemindApplication) getApplication()).cancelRequest();
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
			// 用户注册登陆
			startActivity(new Intent(HomeActivity.this, LoginActivity.class));
			break;
		case R.id.more_btn:
			// 添加提醒
			startActivity(new Intent(HomeActivity.this, AddRemindActivity.class));
			break;
		default:
			break;
		}
	}

	private final class ScrollListener implements OnScrollListener {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// Log.i("MainActivity", "onScroll(firstVisibleItem="
			// + firstVisibleItem + ",visibleItemCount="
			// + visibleItemCount + ",totalItemCount=" + totalItemCount
			// + ")");

//			final int loadtotal = totalItemCount;
			int lastItemid = listView.getLastVisiblePosition(); // 获取当前屏幕最后Item的ID
			if ((lastItemid + 1) == totalItemCount && lastItemid < maxCount) { // 达到数据的最后一条记录
				if (totalItemCount > 0) {
					// 当前页
					final int nextpage = totalItemCount % number == 0 ? totalItemCount
							/ number
							: totalItemCount / number + 1;
//					final int nextpage = currentpage + 1; // 下一页
					if (nextpage <= maxpage && loadfinish ) {
//						handler.sendEmptyMessage(REFRESH_START);
						
						loadfinish = false;
						listView.addFooterView(footer);

						// 开一个线程加载数据
						new Thread(new Runnable() {

							@Override
							public void run() {
								getMoreData(nextpage);
								// 发送消息
								handler.sendMessage(handler.obtainMessage(LOAD_OK,
										datas));
							}
						}).start();
					}
				}
			}

		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// Log.i("MainActivity", "onScrollStateChanged(scrollState="
			// + scrollState + ")");
		}

	}
	
	/**
	 * 获取数据
	 */
	private void getData() {
		datas.clear();
		// 显示未接受的数据
		Cursor cursor = remindDao.getUnAcceptRemind();
		datas.addAll(DataBaseParser.getRemindDetail(cursor));
		// 显示今天的提醒
		cursor = remindDao.getTodayRemind(today);
		datas.addAll(DataBaseParser.getRemindDetail(cursor));
		// 显示其他天的提醒
		cursor = remindDao.getOtherdayRemind(today);
		datas.addAll(DataBaseParser.getRemindDetail(cursor));
		cursor.close();
	}
	
	/**
	 * TODO test
	 */
	private void getMoreData(int currentpage) {
		List<RemindEntity> temp = new ArrayList<RemindEntity>();
//		for (int i = 0; i < 10; i++) {
//			RemindEntity entity = new RemindEntity();
//			temp.add(entity);
//		}
		int currentCount = currentpage * number;
		if (currentCount <= notAcceptCount) {
			// 首先显示未接受, 未接受足够当前页
			
		} else {
			// 未接受不够当前页，判断是否还有未接受提醒没有显示
			int notShowCount = notAcceptCount - (currentpage - 1) * number;
			if (notShowCount > 0) {
				// 还有未接受提醒没有显示
				Cursor cursor = remindDao.getUnAcceptRemind((currentpage - 1) * number, notShowCount);
				temp = DataBaseParser.getRemindDetail(cursor);
				cursor.close();
				// 显示今天的提醒
				Cursor cursor1 = remindDao.getTodayRemind(today, (currentpage - 1 - (notAcceptCount / 1)) * number, number - notShowCount);
				temp.addAll(DataBaseParser.getRemindDetail(cursor1));
				cursor1.close();
			} else {
				// 未接受提醒全部显示
				if (loadfinish) {
					// 显示今天提醒，今天提醒足够显示当前页
					Cursor cursor = remindDao.getTodayRemind(today, (currentpage - 1 - (notAcceptCount / 1)) * number, number);
					temp = DataBaseParser.getRemindDetail(cursor);
					cursor.close();
				} else {
					// 今天提醒不够当前页，判断是否还有今天提醒没有显示
					if (loadfinish) {
						// 还有今天提醒没有显示
						
					} else {
						// 今天提醒全部显示，显示其他
						
					}
				}
				
			}
		}
		
		
		
		datas.addAll(temp);
	}
	
	/**
	 * 获取下一页的数据
	 * @param page		下一页页码
	 */
	public void getNextPageDatas(int page) {
		List<RemindEntity> temp = new ArrayList<RemindEntity>();
		int currentCount = page * number;
		if (currentCount <= notAcceptCount) {
			// 显示未接受的数据
			Cursor cursor = remindDao.getUnAcceptRemind((page - 1) * number, number);
			temp = DataBaseParser.getRemindDetail(cursor);
			cursor.close();
		} else if (currentCount <= (notAcceptCount + todayCount)){
			// 显示未接受的数据和今天的
			int notShowCount = notAcceptCount - (page - 1) * number;
			if (notShowCount > 0) {
				// 还有未接受提醒没有显示
				Cursor cursor = remindDao.getUnAcceptRemind((page - 1) * number, notShowCount);
				temp = DataBaseParser.getRemindDetail(cursor);
				cursor.close();
				// 显示今天的提醒
				Cursor cursor1 = remindDao.getTodayRemind(today, 0, number - notShowCount);
				temp.addAll(DataBaseParser.getRemindDetail(cursor1));
				cursor1.close();
			} else {
				// 未接受提醒全部显示
				Cursor cursor1 = remindDao.getTodayRemind(today, (page - 1) * number - notAcceptCount, number);
				temp = DataBaseParser.getRemindDetail(cursor1);
				cursor1.close();
			}
			
		} else if (currentCount <= (notAcceptCount + todayCount + otherDayCount)){
			// 显示其他天的数据和今天的
			int notShowCount = notAcceptCount + todayCount - (page - 1) * number;
			if (notShowCount > 0) {
				// 还有今天提醒没有显示
				Cursor cursor = remindDao.getTodayRemind(today, (page - 1) * number - notAcceptCount, notShowCount);
				temp = DataBaseParser.getRemindDetail(cursor);
				cursor.close();
				// 显示其他天的提醒
				Cursor cursor1 = remindDao.getOtherdayRemind(today, 0, number - notShowCount);
				temp.addAll(DataBaseParser.getRemindDetail(cursor1));
				cursor1.close();
			} else {
				// 今天提醒全部显示
				Cursor cursor1 = remindDao.getOtherdayRemind(today, (page - 1) * number - notAcceptCount - todayCount, number);
				temp = DataBaseParser.getRemindDetail(cursor1);
				cursor1.close();
			}
		}
		
		datas.addAll(temp);
	}
}