package com.remind.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.help.remind.R;
import com.remind.application.RemindApplication;
import com.remind.global.AppConstant;
import com.remind.http.HttpClient;
import com.remind.sp.MySharedPreferencesLoginType;

public class WelcomeActivity extends LoginBaseActivity {

    /**
     * 是否已经登录
     */
    private boolean isLogin = false;
    /**
     * 用户联系人数据库
     */
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mIntent = new Intent();
        isLogin = MySharedPreferencesLoginType.isOnline(this);

        deleteFiles();

        if (isLogin) {
            // 已登录
            handler.sendEmptyMessage(INIT_FINISH);
        } else {
            handler.sendEmptyMessageDelayed(INIT_FINISH, 2000);
        }
    }

    @Override
    public void initFinish() {
        mobile = MySharedPreferencesLoginType.getString(this, MySharedPreferencesLoginType.USERNAME);
        pwdStr = MySharedPreferencesLoginType.getString(this, MySharedPreferencesLoginType.PASSWORD);
        if (isLogin && !TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(pwdStr)) {
            // 登陆没有退出
            // 登陆
            String params = HttpClient.getJsonForPost(HttpClient.getUserForLogin(mobile, pwdStr));
            login(params);
        } else {
            // 退出了
            if (isLogin) {
                MySharedPreferencesLoginType.setOnlineState(this, false);
                Toast.makeText(this, "用户登录信息失效，请重新登陆", Toast.LENGTH_SHORT).show();
            }
            mIntent.setClass(WelcomeActivity.this, HomeActivity.class);
            startActivity(mIntent);
            finish();
        }
    }

    @Override
    public void loginSuccess() {
        mIntent.setClass(WelcomeActivity.this, HomeActivity.class);
        startActivity(mIntent);
        finish();
    }

    @Override
    public void loginFail() {
        Toast.makeText(WelcomeActivity.this, "获取用户信息失败，请重新登陆", Toast.LENGTH_SHORT).show();
        mIntent.setClass(WelcomeActivity.this, HomeActivity.class);
        startActivity(mIntent);
        finish();
    }

    @Override
    public void loginBackRecevie() {
        // 登陆状态改变
        if (RemindApplication.IS_LOGIN) {
            handler.sendEmptyMessage(LOGIN_SUCCESS);
        } else {
            handler.sendEmptyMessage(LOGIN_FAIL);
        }
    }

    @Override
    public void outNet() {
        mIntent.setClass(WelcomeActivity.this, HomeActivity.class);
        startActivity(mIntent);
        finish();
    }

    @Override
    void httpOver(String s) {

    }

    /**
     * 删除15天前的文件
     */
    private synchronized void deleteFiles() {
        // 上次删除时间
        String dateString = MySharedPreferencesLoginType.getDeleteTime(this);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -15);
        final Date target = calendar.getTime();

        if (TextUtils.isEmpty(dateString)) {
            // 首次启动
            MySharedPreferencesLoginType.saveDeleteTime(this, format.format(target));
            return;
        } else if (format.format(target).compareTo(dateString) <= 0) {
            // 每天删除一起
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                ArrayList<File> deleteList = new ArrayList<File>();
                String path = AppConstant.MNT + AppConstant.FILE_PATH + AppConstant.EDITED_AUDIO_PATH;
                File directory = new File(path);
                File[] files = directory.listFiles();
                String s2 = format.format(target);
                s2.trim();
                // 获取需要删除的文件列表
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    Date temp = new Date(f.lastModified());
                    String s1 = format.format(temp);
                    int result = s2.compareTo(s1);
                    if (result > 0) {
                        deleteList.add(f);
                    }
                }

                // 删除文件
                delete(deleteList);
            }
        }).start();

        MySharedPreferencesLoginType.saveDeleteTime(this, format.format(target));
    }

    /**
     * 递归删除
     * 
     * @param deleteList
     */
    private void delete(ArrayList<File> deleteList) {
        if (null != deleteList && deleteList.size() > 0) {
            deleteList.get(0).delete();
            deleteList.remove(0);
            delete(deleteList);
        }
    }
}
