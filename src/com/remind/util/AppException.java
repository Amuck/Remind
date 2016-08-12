package com.remind.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.remind.global.AppConstant;

/**
 * 
 * @ClassName: com.demo.acg.util.AppException
 * @Description: 应用程序异常类：用于捕获异常
 * 
 */

public class AppException extends Exception implements UncaughtExceptionHandler {

    public static final String TAG = "Remind";

    private static final long serialVersionUID = -6262909398048670705L;

    private String message;

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private AppException() {
        super();
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public AppException(String message, Exception excp) {
        super(message, excp);
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取APP异常崩溃处理对象
     * 
     * @param context
     * @return
     */
    public static AppException getAppExceptionHandler() {
        return new AppException();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }
        // else {
        // try {
        // Thread.sleep(3000);
        // } catch (InterruptedException e) {
        // Log.e("ACG", "error : ", e);
        // }
        // //退出程序
        // android.os.Process.killProcess(android.os.Process.myPid());
        // System.exit(1);
        // }

    }

    /**
     * 自定义异常处理
     * 
     * @param ex
     * @return true:处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        final Activity activity = AppManager.getAppManager().currentActivity();

        if (activity == null) {
            return false;
        }

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                new AlertDialog.Builder(activity).setTitle("提示").setCancelable(false).setMessage("亲，程序马上崩溃了...")
                        .setNeutralButton("没关系", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppManager.getAppManager().exitApp(activity);
                            }
                        }).create().show();
                Looper.loop();
            }
        }.start();

        // 收集设备参数信息
        collectDeviceInfo(activity);
        // 保存日志文件
        saveCrashInfo2File(ex);

        return true;
    }

    /**
     * 收集设备参数信息
     * 
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     * 
     * @param ex
     * @return 返回文件名称,便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = AppConstant.MNT + AppConstant.FILE_PATH + AppConstant.ERROR_PATH;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }
}
