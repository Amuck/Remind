package com.remind.receiver;

import com.remind.util.AppUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * @author ChenLong
 *
 * 提醒发起
 */
public class RemindReceiver extends BroadcastReceiver {

	public RemindReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.alarm.remind.action".equals(intent.getAction())) {
			// receive the brocast that we defined
			Toast.makeText(context, "接收到闹钟广播", Toast.LENGTH_SHORT).show();
			// the function that we should finsh background

//			Intent intentToService = new Intent(context, checkService.class);
//			context.startService(intentToService);
			AppUtil.sendNotif(context);
		}
	}

}
