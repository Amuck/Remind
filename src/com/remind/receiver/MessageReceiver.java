package com.remind.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.widget.Toast;

import com.remind.application.RemindApplication;
import com.remind.http.HttpClient;
import com.remind.sevice.BackService;

/**
 * @author ChenLong
 *
 * 监听重启事件，重置所有提醒
 */
public class MessageReceiver extends BroadcastReceiver {

	public MessageReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
//		TextView tv = textView.get();
		if (action.equals(BackService.HEART_BEAT_ACTION)) {
//			if (null != tv) {
//				tv.setText("Get a heart heat");
//			}
			Toast.makeText(context, "Get a heart heat", Toast.LENGTH_SHORT).show();
		} else {
			String message = intent.getStringExtra("message");
//			tv.setText(message);
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(message.substring(message.indexOf("{"), message.length()));
				String type = jsonObject.getString("type");
				if ("message".equals(type)) {
					// 收到短消息，需要回馈
					type = "message_ack";
					String mid = jsonObject.getString("mid");
					String param = HttpClient.getJsonForPost(HttpClient.msgFeedBack(mid, type));
					boolean isSuccess = RemindApplication.iBackService.sendMessage(param);
					if (isSuccess) {
						Toast.makeText(context, "回馈成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "回馈fail", Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		}
	

	}

}
