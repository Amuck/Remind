package com.remind.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.remind.activity.AlertActivity;
import com.remind.util.AppUtil;

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
			// the function that we should finsh background

			int requestCode = intent.getIntExtra("requestCode", -1);
//			Toast.makeText(context, "接收到闹钟广播" + requestCode, Toast.LENGTH_SHORT).show();
			Intent i = new Intent(context, AlertActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("requestCode", requestCode);
			context.startActivity(i);
//			AppUtil.sendNotif(context);
		}
	}

}
