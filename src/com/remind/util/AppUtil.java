package com.remind.util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.help.remind.R;
import com.remind.entity.PeopelEntity;
import com.remind.entity.RemindEntity;

public class AppUtil {
	/**
	 * 用户手机号
	 */
	public static String userNum = "";

	/** 默认下载图片文件地址. */
	public static String downPathImageDir = File.separator + "download"
			+ File.separator + "cache_images" + File.separator;

	/** 图片处理：裁剪. */
	public static final int CUTIMG = 0;

	/** 图片处理：缩放. */
	public static final int SCALEIMG = 1;

	// 通知栏消息
	private static int messageNotificationID = 1000;
	private static Notification messageNotification = new Notification();
	private static NotificationManager messageNotificatioManager = null;
	// 设置页面声音状态的布尔值
	static boolean soundtype = true;

	/**
	 * dip转像素
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 像素转dip
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 显示一个toast
	 * 
	 * @param context
	 * @param text
	 * @param duration
	 */
	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	/**
	 * 将drawable转为bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap.createBitmap(

		drawable.getIntrinsicWidth(),

		drawable.getIntrinsicHeight(),

		drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

		: Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(bitmap);

		// canvas.setBitmap(bitmap);

		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());

		drawable.draw(canvas);

		return bitmap;

	}

	/**
	 * 描述：判断一个字符串是否为null或空值.
	 * 
	 * @param str
	 *            指定的字符串
	 * @return true or false
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * Bitmap对象转换Drawable对象.
	 * 
	 * @param bitmap
	 *            要转化的Bitmap对象
	 * @return Drawable 转化完成的Drawable对象
	 */
	@SuppressWarnings("deprecation")
	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		BitmapDrawable mBitmapDrawable = null;
		try {
			if (bitmap == null) {
				return null;
			}
			mBitmapDrawable = new BitmapDrawable(bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mBitmapDrawable;
	}

	/**
	 * 描述：获取src中的图片资源.
	 * 
	 * @param src
	 *            图片的src路径，如（“image/arrow.png”）
	 * @return Bitmap 图片
	 */
	public static Bitmap getBitmapFormSrc(String src) {
		Bitmap bit = null;
		try {
			bit = BitmapFactory.decodeStream(AppUtil.class
					.getResourceAsStream(src));
		} catch (Exception e) {

		}
		return bit;
	}

	/**
	 * 描述：通过文件的本地地址从SD卡读取图片.
	 * 
	 * @param file
	 *            the file
	 * @param type
	 *            图片的处理类型（剪切或者缩放到指定大小，参考AbConstant类）
	 * @param newWidth
	 *            新图片的宽
	 * @param newHeight
	 *            新图片的高
	 * @return Bitmap 新图片
	 */
	public static Bitmap getBitmapFromSD(File file, int type, int newWidth,
			int newHeight) {
		Bitmap bit = null;
		try {
			// SD卡是否存在
			if (!isCanUseSD()) {
				return null;
			}
			// 文件是否存在
			if (!file.exists()) {
				return null;
			}
			// 文件存在
			if (type == AppUtil.CUTIMG) {
				bit = cutImg(file, newWidth, newHeight);
			} else {
				bit = scaleImg(file, newWidth, newHeight);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bit;
	}

	/**
	 * 描述：缩放图片.压缩
	 * 
	 * @param file
	 *            File对象
	 * @param newWidth
	 *            新图片的宽
	 * @param newHeight
	 *            新图片的高
	 * @return Bitmap 新图片
	 */
	public static Bitmap scaleImg(File file, int newWidth, int newHeight) {
		Bitmap resizeBmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getPath(), opts);
		if (newWidth != -1 && newHeight != -1) {
			// inSampleSize=2表示图片宽高都为原来的二分之一，即图片为原来的四分之一
			// 缩放可以将像素点打薄
			int srcWidth = opts.outWidth; // 获取图片的原始宽度
			int srcHeight = opts.outHeight;// 获取图片原始高度
			int destWidth = 0;
			int destHeight = 0;
			// 缩放的比例
			double ratio = 0.0;
			if (srcWidth < newWidth || srcHeight < newHeight) {
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
				// 按比例计算缩放后的图片大小
			} else if (srcWidth > srcHeight) {
				ratio = (double) srcWidth / newWidth;
				destWidth = newWidth;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = (double) srcHeight / newHeight;
				destHeight = newHeight;
				destWidth = (int) (srcWidth / ratio);
			}
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
			opts.inSampleSize = (int) ratio + 1;
			// 设置大小
			opts.outHeight = destHeight;
			opts.outWidth = destWidth;
		} else {
			opts.inSampleSize = 1;
		}
		// 创建内存
		opts.inJustDecodeBounds = false;
		// 使图片不抖动
		opts.inDither = false;
		resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);
		return resizeBmp;
	}

	/**
	 * 描述：SD卡是否能用.
	 * 
	 * @return true 可用,false不可用
	 */
	public static boolean isCanUseSD() {
		try {
			return Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 描述：裁剪图片.
	 * 
	 * @param file
	 *            File对象
	 * @param newWidth
	 *            新图片的宽
	 * @param newHeight
	 *            新图片的高
	 * @return Bitmap 新图片
	 */
	public static Bitmap cutImg(File file, int newWidth, int newHeight) {
		Bitmap newBitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getPath(), opts);
		if (newWidth != -1 && newHeight != -1) {
			// inSampleSize=2表示图片宽高都为原来的二分之一，即图片为原来的四分之一
			// 缩放可以将像素点打薄,裁剪前将图片缩放一些
			int srcWidth = opts.outWidth; // 获取图片的原始宽度
			int srcHeight = opts.outHeight;// 获取图片原始高度
			int destWidth = 0;
			int destHeight = 0;
			int cutSrcWidth = newWidth * 2;
			int cutSrcHeight = newHeight * 2;

			// 缩放的比例
			double ratio = 0.0;
			if (srcWidth < cutSrcWidth || srcHeight < cutSrcHeight) {
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
				// 按比例计算缩放后的图片大小
			} else if (srcWidth > srcHeight) {
				ratio = (double) srcWidth / cutSrcWidth;
				destWidth = cutSrcWidth;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = (double) srcHeight / cutSrcHeight;
				destHeight = cutSrcHeight;
				destWidth = (int) (srcWidth / ratio);
			}
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
			opts.inSampleSize = (int) ratio + 1;
			// 设置大小
			opts.outHeight = destHeight;
			opts.outWidth = destWidth;
		} else {
			opts.inSampleSize = 1;
		}
		// 创建内存
		opts.inJustDecodeBounds = false;
		// 使图片不抖动
		opts.inDither = false;
		Bitmap resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);
		if (resizeBmp != null) {
			newBitmap = cutImg(resizeBmp, newWidth, newHeight);
		}
		if (newBitmap != null) {
			return newBitmap;
		} else {
			return resizeBmp;
		}
	}

	/**
	 * 描述：裁剪图片.
	 * 
	 * @param bitmap
	 *            the bitmap
	 * @param newWidth
	 *            新图片的宽
	 * @param newHeight
	 *            新图片的高
	 * @return Bitmap 新图片
	 */
	public static Bitmap cutImg(Bitmap bitmap, int newWidth, int newHeight) {
		if (bitmap == null) {
			return null;
		}
		Bitmap newBitmap = null;
		if (newHeight <= 0 || newWidth <= 0) {
			return bitmap;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		if (width <= 0 || height <= 0) {
			return null;
		}
		int offsetX = 0;
		int offsetY = 0;

		if (width > newWidth) {
			offsetX = (width - newWidth) / 2;
		}
		if (height > newHeight) {
			offsetY = (height - newHeight) / 2;
		}

		newBitmap = Bitmap.createBitmap(bitmap, offsetX, offsetY, newWidth,
				newHeight);
		return newBitmap;
	}

	/**
	 * 转字符为utf-8
	 * 
	 * @param str
	 * @return
	 */
	public static String toUTF8(String str) {
		try {
			byte[] bytes = str.getBytes();
			return new String(bytes, "UTF-8");
			// return URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}

	/**
	 * @return 当前时间 "yyyy-MM-dd hh:mm:ss"
	 */
	public static String getNowTime() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		return dateFormat.format(date).toString();
	}

	/**
	 * 设置提醒闹铃
	 * 
	 * @param context
	 * @param date
	 */
	public static void setAlarm(Context context, String date, int requestCode) {
		Calendar calendar = Calendar.getInstance();
		// 获取目的时间毫秒数
		try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm")
					.parse(date));
		} catch (ParseException e) {
			showToast(context, "时间错误，接受任务失败。");
			e.printStackTrace();
			return;
		}

		Intent intent = new Intent("android.alarm.remind.action");
		intent.putExtra("requestCode", requestCode);
		// create the Intent between activity and broadcast
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		// define the PendingIntent
		alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
	}

	/**
	 * 获取联系人显示名称
	 * 
	 * @param entity
	 * @return
	 */
	public static String getName(PeopelEntity entity) {
		String name = entity.getNickName();
		if (null == name || name.trim().length() <= 0) {
			// 无备注名称则显示联系人名称
			name = entity.getName();
		}
		if (null == name || name.trim().length() <= 0) {
			name = "佚名";
		}
		return name;
	}

	/**
	 * 获取联系人显示名称
	 * 
	 * @param entity
	 * @return
	 */
	public static String getName(RemindEntity entity) {
		String name = entity.getNickName();
		if (null == name || name.trim().length() <= 0) {
			// 无备注名称则显示联系人名称
			name = entity.getTargetName();
		}
		if (null == name || name.trim().length() <= 0) {
			name = "佚名";
		}
		return name;
	}

	/**
	 * 发送任务开始的通知
	 */
	@SuppressWarnings("deprecation")
	public static void sendNotif(Context context) {
		messageNotificatioManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// 获取系统默认短信铃声
		if (soundtype) {
			messageNotification.sound = RingtoneManager
					.getActualDefaultRingtoneUri(context,
							RingtoneManager.TYPE_NOTIFICATION);
		} else {
			messageNotification.sound = RingtoneManager
					.getActualDefaultRingtoneUri(context, 0);
		}
		// 添加震动
		long[] vibrate = { 0, 100, 200, 300 };
		messageNotification.vibrate = vibrate;
		messageNotification.icon = R.drawable.ic_launcher;
		messageNotification.defaults |= Notification.DEFAULT_LIGHTS;
		messageNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		messageNotification.flags |= Notification.FLAG_INSISTENT;
		messageNotification.tickerText = "您接受的任务已经到了开始的时间，请查看任务。";
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				new Intent(), 0);
		messageNotification.setLatestEventInfo(context, "任务时间!",
				"您接受的任务已经到了开始的时间，请查看任务。", contentIntent);
		messageNotificatioManager.notify(messageNotificationID,
				messageNotification);

	}
	
	/**
	 * 获取本机号码
	 * 
	 * @return
	 */
	public static  String getPhoneNumber(Context context) {
		if ("".equals(userNum)) {
			TelephonyManager mTelephonyMgr;
			mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String temp = mTelephonyMgr.getLine1Number();
			
			if (null != temp && temp.length() > 11) {
				// 获取11位手机号
				temp = temp.substring(temp.length() - 11, temp.length() - 1);
				userNum = temp;
			}
		}
		
		return userNum;
	}
	
	/**
	 * @return	用户名
	 */
	public static String getUserName() {
		return "自己";
	}
}
