package com.remind.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.remind.sevice.BackService;

/**
 * @author ChenLong
 *
 */
public class WakeReceiver extends BroadcastReceiver {
    /**
     * 灰色保活手段唤醒广播的action
     */
    public final static String GRAY_WAKE_ACTION = "com.wake.gray";
    
	public WakeReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
        if (GRAY_WAKE_ACTION.equals(action)) {

            Intent wakeIntent = new Intent(context, BackService.class);
            context.startService(wakeIntent);
        }

	}

}
