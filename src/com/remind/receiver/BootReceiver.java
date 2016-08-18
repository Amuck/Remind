package com.remind.receiver;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.remind.activity.BlankActivity;
import com.remind.application.RemindApplication;
import com.remind.dao.impl.RemindDaoImpl;
import com.remind.entity.RemindEntity;
import com.remind.global.AppConstant;
import com.remind.sp.MySharedPreferencesLoginType;
import com.remind.util.AppUtil;
import com.remind.util.DataBaseParser;

/**
 * @author ChenLong
 * 
 *         监听重启事件，重置所有提醒
 */
public class BootReceiver extends BroadcastReceiver {

    /**
     * 是否已经登录
     */
    private boolean isLogin = false;

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        isLogin = MySharedPreferencesLoginType.isOnline(context);
        String boot = Intent.ACTION_BOOT_COMPLETED;
        String mount = Intent.ACTION_MEDIA_MOUNTED;
        String unmount = Intent.ACTION_MEDIA_UNMOUNTED;
        String eject = Intent.ACTION_MEDIA_EJECT;
        if (action.equals(boot) || action.equals(mount) || action.equals(unmount) || action.equals(eject)) {
            AppConstant.USER_NUM = MySharedPreferencesLoginType.getString(context, MySharedPreferencesLoginType.USERNAME);
            // 重新计算闹铃时间，并调第一步的方法设置闹铃时间及闹铃间隔时间
            RemindDaoImpl remindDaoImpl = new RemindDaoImpl(context);
            Cursor cursor = remindDaoImpl.queryRemind();
            ArrayList<RemindEntity> remindEntities = DataBaseParser.getRemindDetail(cursor);
            cursor.close();

            for (int i = 0; i < remindEntities.size(); i++) {
                RemindEntity temp = remindEntities.get(i);
                AppUtil.setAlarm(context, temp.getRemindTime(), Integer.valueOf(temp.getId()));
            }

            // 如果信息丢失怎重启服务
            if (isLogin) {
                if (RemindApplication.iBackService == null) {
                    Intent intent2 = new Intent(context, BlankActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
//                    Intent mServiceIntent = new Intent(context, BackService.class);
//                    context.startActivity(new Intent(context, BlankActivity.class));
//                    context.startService(mServiceIntent);
//                    RemindApplication.startLongLink();
                }
//              if (RemindApplication.iBackService == null) {
//              context.startActivity(new Intent(context, BlankActivity.class));
//          }
            }
        }

    }

}
