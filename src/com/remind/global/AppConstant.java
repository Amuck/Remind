package com.remind.global;

import android.os.Environment;

public class AppConstant {
	/**
	 * 绝对路径
	 */
	public static String MNT = Environment.getExternalStorageDirectory()
			.getPath();
	/**
	 * 数据库文件名
	 */
	public static final String DB_FILE_NAME = "remind.db";
	/**
	 * 数据库文件夹
	 */
	public static final String DB_FILE_PATH = "/dbfile";

	/**
	 * 文件夹名称
	 */
	public static final String FILE_PATH = "/REMIND";

	/**
	 * 音频保存路径
	 */
	public static final String EDITED_AUDIO_PATH = "/audio";

	/**
	 * 图片保存路径
	 */
	public static final String EDITED_IMG_PATH = "/img";
	
	/**
	 * 视频保存路径
	 */
	public static final String EDITED_VEDIO_PATH = "/video";
	/**
	 * 错误日志保存路径
	 */
	public static final String ERROR_PATH = "/log";
}
