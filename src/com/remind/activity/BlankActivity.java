package com.remind.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.remind.application.RemindApplication;
import com.remind.receiver.MessageReceiver;
import com.remind.sevice.BackService;
import com.remind.sevice.IBackService;

public class BlankActivity extends BaseActivity {
    private Intent mServiceIntent;

    private IBackService iBackService;

    /**
     * 是否需要解除绑定
     */
//    private boolean isNeedUnbind = false;
    
    private IntentFilter mIntentFilter;
    protected LoginReciver mReciver;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iBackService = null;

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBackService = IBackService.Stub.asInterface(service);
            if (RemindApplication.iBackService == null) {
                RemindApplication.iBackService = iBackService;
            } else {
                iBackService = RemindApplication.iBackService;
            }
        }
    };

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServiceIntent = new Intent(this, BackService.class);
        mIntentFilter = new IntentFilter();
        mReciver = new LoginReciver();
        mIntentFilter.addAction(MessageReceiver.LOGIN_STATE_ACTION);
    };

    class LoginReciver extends BroadcastReceiver {

        public LoginReciver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MessageReceiver.LOGIN_STATE_ACTION)) {
                finish();
            }
        };
    }
    
    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(mReciver, mIntentFilter);
        
        if (RemindApplication.iBackService == null) {
            ComponentName cn = startService(mServiceIntent);
            if (cn != null) {
                bindService(mServiceIntent, conn, BIND_AUTO_CREATE);
            } else {
                RemindApplication.IS_LOGIN = false;
                return;
            }
//            isNeedUnbind = true;
        } else {
//            isNeedUnbind = false;
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReciver);
//        if (isNeedUnbind) {
            unbindService(conn);
//        }
        super.onDestroy();
    }
}
