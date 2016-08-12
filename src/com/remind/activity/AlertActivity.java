package com.remind.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.help.remind.R;
import com.remind.dao.RemindDao;
import com.remind.dao.impl.RemindDaoImpl;
import com.remind.entity.RemindEntity;
import com.remind.util.AppUtil;
import com.remind.util.DataBaseParser;

/**
 * @author ChenLong
 * 
 *         暂时不再使用
 */
public class AlertActivity extends BaseActivity implements OnClickListener {

    private PowerManager pm;
    private WakeLock mWakelock;

    private Window win;

    /**
     * 标题
     */
    private TextView title;
    /**
     * 内容
     */
    private TextView content;
    /**
     * 图片
     */
    // private ImageView contentImg;
    /**
     * 已经开始
     */
    // private Button startBtn;
    /**
     * 马上开始
     */
    // private Button readyBtn;
    /**
     * 10分钟后提醒
     */
    // private Button laterBtn;
    /**
     * 其他
     */
    private Button otherBtn;

    private ViewPager startPager;
    private ViewPager readyPager;
    private ViewPager laterPager;
    private ArrayList<View> startViews;
    private ArrayList<View> readyViews;
    private ArrayList<View> laterViews;
    /**
     * 提醒的id
     */
    private int remindId;
    private RemindDao remindDao;
    private RemindEntity remindEntity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 在锁屏上显示
        win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alert);
        // 获取闹钟id
        Intent intent = getIntent();
        remindId = intent.getIntExtra("requestCode", -1);

        if (-1 == remindId) {
            AppUtil.showToast(this, "播放提醒失败");
            finish();
        }

        init();
    }

    private void init() {
        title = (TextView) findViewById(R.id.alert_title);
        content = (TextView) findViewById(R.id.alert_content);
        // contentImg = (ImageView) findViewById(R.id.alert_img);
        // startBtn = (Button) findViewById(R.id.alert_start);
        // readyBtn = (Button) findViewById(R.id.alert_ready);
        // laterBtn = (Button) findViewById(R.id.alert_later);
        startPager = (ViewPager) findViewById(R.id.start_pager);
        readyPager = (ViewPager) findViewById(R.id.ready_pager);
        laterPager = (ViewPager) findViewById(R.id.later_pager);
        otherBtn = (Button) findViewById(R.id.alert_other);

        remindDao = new RemindDaoImpl(this);
        getRemindData();

        otherBtn.setOnClickListener(this);

        initViewPager();
    }

    @SuppressLint("InflateParams")
    private void initViewPager() {
        LayoutInflater inflater = getLayoutInflater();
        startViews = new ArrayList<View>();
        startViews.add(inflater.inflate(R.layout.item05, null));
        startViews.add(inflater.inflate(R.layout.item06, null));
        startViews.add(inflater.inflate(R.layout.item01, null));

        startPager.setAdapter(new GuidePageAdapter(startViews));
        startPager.setOnPageChangeListener(new GuidePageChangeListener());
        startPager.setCurrentItem(1);

        readyViews = new ArrayList<View>();
        readyViews.add(inflater.inflate(R.layout.item05, null));
        readyViews.add(inflater.inflate(R.layout.item06, null));
        readyViews.add(inflater.inflate(R.layout.item01, null));
        Button button1 = (Button) readyViews.get(1).findViewById(R.id.pager_btn);
        button1.setText(R.string.alert_ready);
        readyPager.setAdapter(new GuidePageAdapter(readyViews));
        readyPager.setOnPageChangeListener(new GuidePageChangeListener());
        readyPager.setCurrentItem(1);

        laterViews = new ArrayList<View>();
        laterViews.add(inflater.inflate(R.layout.item05, null));
        laterViews.add(inflater.inflate(R.layout.item06, null));
        laterViews.add(inflater.inflate(R.layout.item01, null));
        Button button2 = (Button) laterViews.get(1).findViewById(R.id.pager_btn);
        button2.setText(R.string.alert_later);
        laterPager.setAdapter(new GuidePageAdapter(laterViews));
        laterPager.setOnPageChangeListener(new GuidePageChangeListener());
        laterPager.setCurrentItem(1);
    }

    /**
     * 获取提醒数据
     */
    private void getRemindData() {
        try {
            RemindEntity r = new RemindEntity();
            r.setId(remindId + "");
            Cursor cursor = remindDao.queryRemind(r);
            remindEntity = DataBaseParser.getRemindDetail(cursor).get(0);
            // 初始化页面数据
            title.setText(remindEntity.getTitle());
            content.setText(remindEntity.getContent());
        } catch (Exception e) {
            AppUtil.showToast(this, "播放提醒失败");
            finish();
        }
    }

    /**
     * 获取下一次提醒时间
     */
    // private void getNextRemindTime() {
    // // 获取重复类型
    // String repeatType = remindEntity.getRepeatType();
    // // 提醒时间
    // String remindTime = remindEntity.getRemindTime();
    // // 下次提醒时间
    // String nextRemindTime = null;
    // int remindCount = remindEntity.getRemindCount() + 1;
    // remindEntity.setRemindCount(remindCount);
    // // 计算下一次响铃时间
    // if (RemindEntity.REPEAT_NO.equals(repeatType)) {
    //
    // } else if (RemindEntity.REPEAT_DAY.equals(repeatType)) {
    // nextRemindTime = Utils.getTargeDate(remindTime, 0, 0, 1, 0, 0);
    // } else if (RemindEntity.REPEAT_WEEK.equals(repeatType)) {
    // nextRemindTime = Utils.getTargeDate(remindTime, 0, 0, 7, 0, 0);
    // } else if (RemindEntity.REPEAT_MONTH.equals(repeatType)) {
    // nextRemindTime = Utils.getTargeDate(remindTime, 0, 0, 0, 1, 0);
    // } else if (RemindEntity.REPEAT_YEAR.equals(repeatType)) {
    // nextRemindTime = Utils.getTargeDate(remindTime, 0, 0, 0, 0, 1);
    // }
    // // 设置闹铃
    // if (!TextUtils.isEmpty(nextRemindTime)) {
    // remindEntity.setRemindTime(nextRemindTime);
    // AppUtil.setAlarm(this, nextRemindTime, remindId);
    // }
    // // 更新数据库数据
    // remindDao.updateRemind(remindEntity);
    // }

    /**
     * 发送反馈信息
     */
    // private void sendBackMsg() {
    //
    // }

    /**
     * 在锁屏下点亮屏幕
     */
    @SuppressWarnings("deprecation")
    private void getWakeLock() {
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "Remind");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == mWakelock) {
            getWakeLock();
        }
        mWakelock.acquire();
    }

    @Override
    protected void onPause() {
        mWakelock.release();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.alert_other:
            // 其他

            break;

        default:
            break;
        }
    }

    // 指引页面数据适配器
    class GuidePageAdapter extends PagerAdapter {
        private ArrayList<View> pageViews;

        public GuidePageAdapter(ArrayList<View> pageViews) {
            super();
            this.pageViews = pageViews;
        }

        @Override
        public int getCount() {
            return pageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(pageViews.get(arg1));
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(pageViews.get(arg1));
            return pageViews.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

        @Override
        public void finishUpdate(View arg0) {
        }
    }

    // 指引页面更改事件监听器
    class GuidePageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            if (1 != arg0) {
                finish();
            }
        }
    }
}
