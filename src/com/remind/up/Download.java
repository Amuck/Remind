package com.remind.up;

import java.io.File;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.remind.application.RemindApplication;
import com.remind.dao.impl.MessageDaoImpl;
import com.remind.entity.MessageEntity;
import com.remind.global.AppConstant;
import com.remind.util.AppUtil;
import com.remind.util.DataBaseParser;

public class Download {
    private static final int DOWNLOAD_OVER = 1;
    private static final String BUCKET_NAME = "sisi0";
    private static final String OPERATOR_NAME = "admin1";
    private static final String OPERATOR_PWD = "adminadmin";

    private static UpYun upyun = null;

    private static Download instance = null;

    private static MessageDaoImpl messageDaoImpl;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case DOWNLOAD_OVER:
                Bundle bundle = msg.getData();
                boolean result = bundle.getBoolean("result");
                String id = bundle.getString("id");
                String path = bundle.getString("path");
                // 根据结果修改数据库
                Cursor cursor = messageDaoImpl.queryById(id);
                MessageEntity messageEntity = DataBaseParser.getMessage(cursor).get(0);
                cursor.close();
                
                if (result) {
                    messageEntity.setMsgPath(path);
                } else {
                    messageEntity.setSendState(MessageEntity.SEND_FAIL);
                }
                
                messageDaoImpl.update(messageEntity);
                break;

            default:
                break;
            }
            super.handleMessage(msg);
        }
    };

    private Download() {
        // 初始化空间
        upyun = new UpYun(BUCKET_NAME, OPERATOR_NAME, OPERATOR_PWD);
    }

    public static Download getInstance() {
        if (null == instance) {
            instance = new Download();
            messageDaoImpl = new MessageDaoImpl(RemindApplication.getContextObject());
        }

        return instance;
    }

    /**
     * 下载网盘上的录音
     * 
     * @param filePath
     * @return
     */
    public void downLoadAmr(final String filePath, final long id) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (!TextUtils.isEmpty(filePath) && filePath.contains(".")) {
                    String voiceName = AppUtil.getFileNameFromPath(filePath);
                    File file = new File(AppConstant.MNT + AppConstant.FILE_PATH + AppConstant.EDITED_AUDIO_PATH + "/"
                            + voiceName);

                    boolean result = upyun.readFile(filePath, file);

                    Message message = mHandler.obtainMessage();
                    message.what = DOWNLOAD_OVER;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("result", result);
                    bundle.putString("id", String.valueOf(id));
                    bundle.putString("path", file.getAbsolutePath());
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }

            }
        }).start();

    }
}
