package com.remind.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.ab.activity.AbActivity;
import com.ab.adapter.AbFragmentPagerAdapter;
import com.help.remind.R;
import com.remind.asyn.ImageLoader;
import com.remind.fragment.ContentFragment;
import com.remind.fragment.PeopelFragment;
import com.remind.util.AppUtil;
import com.remind.view.MyViewPager;

public class MainActivity1 extends AbActivity {
    private MyViewPager mTabPager;
    private ArrayList<Fragment> pagerItemList = null;
    private ImageView mTab1, mTab2, mTab3, mTab4, mTab5;
    private ImageView mTabImg;// 动画图片
    private int currIndex = 0;// 当前页卡编号
    private int zero = 0; // 动画图片偏移量
    private int one; // 单个水平动画位移
    private int two;
    private int three;
    private int four;
    public int num;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        createFile();

        mTabPager = (MyViewPager) findViewById(R.id.vPager);
        mTabPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }

        });
        pagerItemList = new ArrayList<Fragment>();
        pagerItemList.add(new ContentFragment());
        pagerItemList.add(new PeopelFragment());
        // pagerItemList.add(new DiscoeryFragment());
        // pagerItemList.add(new UserFragment());
        // pagerItemList.add(new SettingFragment());

        FragmentManager mFragmentManager = this.getSupportFragmentManager();
        AbFragmentPagerAdapter mFragmentPagerAdapter = new AbFragmentPagerAdapter(mFragmentManager, pagerItemList);
        mTabPager.setAdapter(mFragmentPagerAdapter);

        mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mTabImg = (ImageView) findViewById(R.id.img_tab_now);

        mTab1 = (ImageView) findViewById(R.id.img_1);
        mTab2 = (ImageView) findViewById(R.id.img_2);
        // mTab3 = (ImageView) findViewById(R.id.img_3);
        // mTab4 = (ImageView) findViewById(R.id.img_4);
        // mTab5 = (ImageView) findViewById(R.id.img_5);
        mTab1.setOnClickListener(new MyOnClickListener(0));
        mTab2.setOnClickListener(new MyOnClickListener(1));
        // mTab3.setOnClickListener(new MyOnClickListener(2));
        // mTab4.setOnClickListener(new MyOnClickListener(3));
        // mTab5.setOnClickListener(new MyOnClickListener(4));

        // 获取屏幕的分辨率，以计算偏移量
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        // int s = width / 4;
        int s = width / 2;
        one = s;
        two = one * 2;
        // three = one * 3;
        // four = one * 4;

        // 是否去掉notification
        int id = getIntent().getIntExtra("num", 0);
        // 去掉相应的notification
        AppUtil.cancelNotify(id, getApplicationContext());
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            Animation anim = null;
            switch (arg0) {
            case 0:
                obtainFragmentTransaction(0, currIndex);
                mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_pressed));
                if (currIndex == 1) {
                    anim = new TranslateAnimation(one, 0, 0, 0);
                    mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_normal));
                } else if (currIndex == 2) {
                    // anim = new TranslateAnimation(two, 0, 0, 0);
                    // mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_normal));
                } else if (currIndex == 3) {
                    // anim = new TranslateAnimation(three, 0, 0, 0);
                    // mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_normal));
                } else if (currIndex == 4) {
                    // anim = new TranslateAnimation(four, 0, 0, 0);
                    // mTab5.setImageDrawable(getResources().getDrawable(R.drawable.more_n));
                }
                break;
            case 1:
                obtainFragmentTransaction(1, currIndex);
                mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_pressed));
                if (currIndex == 0) {
                    anim = new TranslateAnimation(zero, one, 0, 0);
                    mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_normal));
                } else if (currIndex == 2) {
                    // anim = new TranslateAnimation(two, one, 0, 0);
                    // mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_normal));
                } else if (currIndex == 3) {
                    // anim = new TranslateAnimation(three, one, 0, 0);
                    // mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_normal));
                } else if (currIndex == 4) {
                    // anim = new TranslateAnimation(four, one, 0, 0);
                    // mTab5.setImageDrawable(getResources().getDrawable(R.drawable.more_n));
                }
                break;
            case 2:
                obtainFragmentTransaction(2, currIndex);
                mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_pressed));
                if (currIndex == 0) {
                    anim = new TranslateAnimation(zero, two, 0, 0);
                    mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_normal));
                } else if (currIndex == 1) {
                    anim = new TranslateAnimation(one, two, 0, 0);
                    mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_normal));
                } else if (currIndex == 3) {
                    // anim = new TranslateAnimation(three, two, 0, 0);
                    // mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_normal));
                } else if (currIndex == 4) {
                    // anim = new TranslateAnimation(four, two, 0, 0);
                    // mTab5.setImageDrawable(getResources().getDrawable(R.drawable.more_n));
                }
                break;
            case 3:
                obtainFragmentTransaction(3, currIndex);
                mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_pressed));
                if (currIndex == 0) {
                    anim = new TranslateAnimation(zero, three, 0, 0);
                    mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_normal));
                } else if (currIndex == 1) {
                    anim = new TranslateAnimation(one, three, 0, 0);
                    mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_normal));
                } else if (currIndex == 2) {
                    // anim = new TranslateAnimation(two, three, 0, 0);
                    // mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_normal));
                } else if (currIndex == 4) {
                    // anim = new TranslateAnimation(four, three, 0, 0);
                    // mTab5.setImageDrawable(getResources().getDrawable(R.drawable.more_n));
                }
                break;
            case 4:
                obtainFragmentTransaction(4, currIndex);
                mTab5.setImageDrawable(getResources().getDrawable(R.drawable.more_f));
                if (currIndex == 0) {
                    anim = new TranslateAnimation(zero, four, 0, 0);
                    mTab1.setImageDrawable(getResources().getDrawable(R.drawable.down_n));
                } else if (currIndex == 1) {
                    anim = new TranslateAnimation(one, four, 0, 0);
                    mTab2.setImageDrawable(getResources().getDrawable(R.drawable.up_n));
                } else if (currIndex == 2) {
                    // anim = new TranslateAnimation(two, four, 0, 0);
                    // mTab3.setImageDrawable(getResources().getDrawable(R.drawable.measure_n));
                } else if (currIndex == 3) {
                    // anim = new TranslateAnimation(three, four, 0, 0);
                    // mTab4.setImageDrawable(getResources().getDrawable(R.drawable.look_n));
                }
                break;
            }
            currIndex = arg0;
            // 图片停在偏移的位置
            anim.setFillAfter(true);
            anim.setDuration(150);
            mTabImg.startAnimation(anim);
        }

    }

    public class MyOnClickListener implements OnClickListener {
        int index = 0;

        public MyOnClickListener(int i) {
            this.index = i;
        }

        @Override
        public void onClick(View v) {
            mTabPager.setCurrentItem(index);
        }
    }

    /**
     * 获取一个带动画的FragmentTransaction
     * 
     * @param index
     * @return
     */
    private FragmentTransaction obtainFragmentTransaction(int index, int currentTab) {
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        // 设置切换动画
        if (index > currentTab) {
            ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
        } else {
            ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
        }
        return ft;

    }

    // @Override
    // public boolean onKeyDown(int keyCode, KeyEvent event) {
    // if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
    // AppUtil.doExit(MainActivity.this);
    // return true;
    // }
    // return super.onKeyDown(keyCode, event);
    // }

    /**
     * 创建文件夹
     * 
     * @return
     */
    public void createFile() {
        // if (Environment.getExternalStorageState().equals(
        // Environment.MEDIA_MOUNTED)) {
        // // SdCard可用
        //
        // } else {
        // // SdCard不可用
        // AppConstant.MNT = Environment.getRootDirectory().getAbsolutePath();
        // }
        // // 创建数据库文件夹
        // File dbFile = new File(AppConstant.MNT + AppConstant.FILE_PATH
        // + AppConstant.DB_FILE_PATH);
        // if (!dbFile.exists()) {
        // dbFile.mkdirs();
        // }
        // // 创建音频文件夹
        // File audio = new File(AppConstant.MNT + AppConstant.FILE_PATH
        // + AppConstant.EDITED_AUDIO_PATH);
        // if (!audio.exists()) {
        // audio.mkdirs();
        // }
        // // 创建图片文件夹
        // File img = new File(AppConstant.MNT + AppConstant.FILE_PATH
        // + AppConstant.EDITED_IMG_PATH);
        // if (!img.exists()) {
        // img.mkdirs();
        // }
        // // 创建视频文件夹
        // File video = new File(AppConstant.MNT + AppConstant.FILE_PATH
        // + AppConstant.EDITED_VEDIO_PATH);
        // if (!video.exists()) {
        // video.mkdirs();
        // }
        // // 创建日志文件夹
        // File log = new File(AppConstant.MNT + AppConstant.FILE_PATH
        // + AppConstant.ERROR_PATH);
        // if (!log.exists()) {
        // log.mkdirs();
        // }
    }

    @Override
    protected void onDestroy() {
        ImageLoader.getInstance(this).clearCache();
        super.onDestroy();
    }
}