package com.remind.activity;

import java.io.File;

import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.View;

import com.help.remind.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.remind.asyn.ImageLoader;
import com.remind.fragment.MenuFragment;
import com.remind.global.AppConstant;

public class MainActivity extends SlidingFragmentActivity {

    private Fragment mContent;
    private Fragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createFile();

        if (findViewById(R.id.menu_frame) == null) {
            setBehindContentView(R.layout.menu_frame);
            getSlidingMenu().setSlidingEnabled(true);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            View v = new View(this);
            setBehindContentView(v);
            getSlidingMenu().setSlidingEnabled(false);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }

        // customize the SlidingMenu
        SlidingMenu sm = getSlidingMenu();
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeEnabled(false);
        sm.setBehindScrollScale(0.25f);
        sm.setFadeDegree(0.25f);

        sm.setBackgroundImage(R.drawable.img_frame_background);
        sm.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale = (float) (percentOpen * 0.25 + 0.75);
                canvas.scale(scale, scale, -canvas.getWidth() / 2, canvas.getHeight() / 2);
            }
        });

        sm.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale = (float) (1 - percentOpen * 0.25);
                canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
            }
        });
        // set the Above View Fragment
        if (savedInstanceState != null) {
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        }

        if (mContent == null) {
            // mContent = new ContentFragment(sm);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();

        // set the Behind View Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new MenuFragment()).commit();

        currentFragment = mContent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "mContent", mContent);
    }

    /**
     * 左侧菜单点击切换首页的内容
     */
    public void switchContent(Fragment fragment) {
        if (fragment.equals(currentFragment)) {
            getSlidingMenu().showContent();
            return;
        }

        mContent = fragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        getSlidingMenu().showContent();
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }

    /**
     * 创建文件夹
     * 
     * @return
     */
    public void createFile() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // SdCard可用

        } else {
            // SdCard不可用
            AppConstant.MNT = Environment.getRootDirectory().getAbsolutePath();
        }

        File dbFile = new File(AppConstant.MNT + AppConstant.FILE_PATH + AppConstant.DB_FILE_PATH);
        if (!dbFile.exists()) {
            dbFile.mkdirs();
        }
    }

    @Override
    protected void onDestroy() {
        ImageLoader.getInstance(this).clearCache();
        super.onDestroy();
    }
}
