package com.remind.dao.msg;

public class RemindMsg {

	/**
	 * 表名
	 */
	public static final String TABLENAME = "Remind";

	public static final String ID = "id";
	
	/**
	 * 本机手机号
	 */
	public static final String OWNER_NUM = "ownerNum";
	/**
	 * 对方手机号
	 */
	public static final String TARGET_NUM = "targetNum";
	/**
	 * 对方联系人名称
	 */
	public static final String TARGET_NAME = "targetName";
	/**
	 * 联系人备注名称
	 */
	public static final String NICK_NAME = "nickName";
	/**
	 * 添加时间
	 */
	public static final String ADD_TIME = "addTime";
	/**
	 * 最后编辑时间
	 */
	public static final String LAST_EDIT_TIME = "lastEditTime";
	/**
	 * 提醒标题
	 */
	public static final String TITLE = "title";
	/**
	 * 提醒内容
	 */
	public static final String CONTENT = "content";
	/**
	 * 任务时限
	 */
	public static final String LIMIT_TIME = "limitTime";
	/**
	 * 提醒时间
	 */
	public static final String REMIND_TIME = "remindTime";
	/**
	 * 音频路径
	 */
	public static final String AUDIO_PATH = "audioPath";
	/**
	 * 视频路径
	 */
	public static final String VIDEO_PATH = "videoPath";
	/**
	 * 图片路径
	 */
	public static final String IMG_PATH = "imgPath";
	/**
	 * 提醒方式, 音频：0,
	 * 视频:1,
	 * 图像:2,
	 * 默认为音频
	 */
	public static final String REMIND_METHOD = "remindMethod";
	/**
	 * 提醒的状态, 已接受：0,
	 * 新提醒：1,
	 * 已拒绝：2,
	 * 我发起的：3,
	 * 默认为新提醒
	 */
	public static final String REMIND_STATE = "remindState";
	
	/**
	 * 我发起的提醒的状态，
	 * 等待对方接受：4,
	 * 对方已拒绝：5,
	 * 对方已接受：6,
	 * 默认为等待对方接受
	 */
	public static final String LAUNCH_STATE = "launchState";
	/**
	 * 是否删除, 0：未删除；1：已删除
	 * 默认为未删除
	 */
	public static final String IS_DELETE = "isDelete";
	/**
	 * 预留字段
	 */
	public static final String Z1 = "z1";
	/**
	 * 预留字段
	 */
	public static final String Z2 = "z2";
	/**
	 * 预留字段
	 */
	public static final String Z3 = "z3";
}
