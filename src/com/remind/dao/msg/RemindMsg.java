package com.remind.dao.msg;

/**
 * 提醒，需要根据提醒重复模式（REPEAT_TYPE）来计算下一次提醒时间（REMIND_TIME， REMIND_TIME_MILI），每次提醒增加提醒次数REMIND_COUNT；
 * 
 * 
 * @author ChenLong
 *
 */
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
	 * 登陆用户
	 */
	public static final String LOGIN_USER = "login_user";
	/**
	 * 联系人备注名称
	 */
	public static final String NICK_NAME = "nickName";
	/**
	 * 提醒的id
	 */
	public static final String NOTICE_ID = "notice_id";
	/**
	 * 发送者的id
	 */
	public static final String OWNER_ID = "owner_id";
	/**
	 * 添加时间
	 */
	public static final String ADD_TIME = "addTime";
	/**
	 * 提醒时间毫秒数
	 */
	public static final String REMIND_TIME_MILI = "remind_time_mili";
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
	 * 重复模式：
	 * @see com.remind.entity.RemindEntity#REPEAT_NO 
	 * @see com.remind.entity.RemindEntity#REPEAT_DAY
	 * @see com.remind.entity.RemindEntity#REPEAT_WEEK
	 * @see com.remind.entity.RemindEntity#REPEAT_MONTH
	 * @see com.remind.entity.RemindEntity#REPEAT_YEAR
	 */
	public static final String REPEAT_TYPE = "repeatType";
	/**
	 * 是否删除, 0：未删除；1：已删除
	 * 默认为未删除
	 */
	public static final String IS_DELETE = "isDelete";
	
	/**
	 * 是否可以预览，0：不可以；1可以，默认0.
	 */
	public static final String IS_PRIVIEW = "isPreview";
	/**
	 * 提醒的次数，每次响铃次数加一
	 */
	public static final String REMIND_COUNT = "remindCount";
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
