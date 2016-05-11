package com.remind.entity;

import java.io.Serializable;

/**
 * @author ChenLong
 *
 * 提醒的实体类
 */
/**
 * @author ChenLong
 *
 */
public class RemindEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 音频提醒
	 */
	public final static int AUDIO_REMIND = 0;
	/**
	 * 视频提醒
	 */
	public final static int VIDEO_REMIND = 1;
	/**
	 * 图片提醒
	 */
	public final static int IMAGE_REMIND = 2;
	
	/**
	 * 已接受的提醒
	 */
	public final static int ACCEPT = 0;
	/**
	 * 未接受提醒
	 */
	public final static int NEW = 1;
	/**
	 * 拒绝的提醒
	 */
	public final static int REFUSE = 3;
	/**
	 * 我发起的提醒
	 */
	public final static int LAUNCH = 2;
	
	/**
	 * 等待对方接受
	 */
	public final static int LAUNCH_WAIT = 0;
	/**
	 * 对方已拒绝
	 */
	public final static int LAUNCH_REFUSE = 1;
	/**
	 * 对方接受
	 */
	public final static int LAUNCH_ACCEPT = 2;
	/**
	 * 正常状态
	 */
	public final static String NORMAL = "0";
	/**
	 * 已删除状态
	 */
	public final static String DELETED = "1";
	/**
	 * 不能预览
	 */
	public final static int NOT_PREV = 0;
	/**
	 * 可以预览
	 */
	public final static int CAN_PREV = 1;
	/**
	 * 未读
	 */
	public final static int NOT_READ = 0;
	/**
	 * 已读
	 */
	public final static int READ = 1;
	/**
	 * 只响一次
	 */
	public final static String REPEAT_NO = "repeat_no";
	/**
	 * 每天重复
	 */
	public final static String REPEAT_DAY = "repeat_day";
	/**
	 * 每周重复
	 */
	public final static String REPEAT_WEEK = "repeat_week";
	/**
	 * 每月重复
	 */
	public final static String REPEAT_MONTH = "repeat_month";
	/**
	 * 每年重复
	 */
	public final static String REPEAT_YEAR = "repeat_year";
	
	/**
	 * id
	 */
	private String id;
	/**
	 * 本机手机号
	 */
	private String ownerNum;
	
	/**
	 * 对方手机号
	 */
	private String targetNum;
	
	/**
	 * 对方联系人名称
	 */
	private String targetName;
	private String noticeId;
	private String ownerId;
	/**
	 * 联系人备注名称
	 */
	private String nickName;
	/**
	 * 添加时间
	 */
	private String addTime;
	/**
	 * 提醒时间毫秒数
	 */
	private String remindTimeMiLi;
	/**
	 * 提醒标题
	 */
	private String title;
	/**
	 * 提醒内容
	 */
	private String content;
	/**
	 * 任务时限
	 */
	private String limitTime;
	/**
	 * 提醒时间
	 */
	private String remindTime;
	
	/**
	 * 音频路径
	 */
	private String audioPath;
	/**
	 * 视频路径
	 */
	private String videoPath;
	/**
	 * 图片路径
	 */
	private String imgPath;
	
	/**
	 * 提醒方式, 音频：{@link #AUDIO_REMIND},
	 * 视频:{@link #VIDEO_REMIND},
	 * 图像:{@link #IMAGE_REMIND},
	 * 默认为音频{@link #AUDIO_REMIND}
	 */
	private int remindMethod = RemindEntity.AUDIO_REMIND;
	
	/**
	 * 提醒的状态, 已接受：{@link #ACCEPT},
	 * 未接受提醒：{@link #NEW},
	 * 已拒绝：{@link #REFUSE},
	 * 我发起的：{@link #LAUNCH},
	 * 默认为新提醒{@link #NEW}
	 */
	private int remindState = RemindEntity.NEW;

	/**
	 * 我发起的提醒的状态，
	 * 等待对方接受：{@link #LAUNCH_WAIT},
	 * 对方已拒绝：{@link #LAUNCH_REFUSE},
	 * 对方已接受：{@link #LAUNCH_ACCEPT},
	 * 默认为等待对方接受{@link #LAUNCH_WAIT}
	 */
	private int launchState = RemindEntity.LAUNCH_WAIT;
	/**
	 * 重复模式：
	 * 不重复：{@link #REPEAT_NO},
	 * 每天重复：{@link #REPEAT_DAY},
	 * 每周重复：{@link #REPEAT_WEEK},
	 * 每月重复：{@link #REPEAT_MONTH},
	 * 每年重复：{@link #REPEAT_YEAR},
	 */
	private String repeatType = RemindEntity.REPEAT_NO;
	/**
	 * 是否删除, {@link #NORMAL}：未删除；{@link #DELETED}：已删除
	 * 默认为未删除:{@link #NORMAL}
	 */
	private String isDelete = RemindEntity.NORMAL;
	/**
	 * 是否可以预览，{@link #NOT_PREV}：不能预览；{@link #CAN_PREV}：能预览
	 * 默认为不能预览:{@link #NOT_PREV}
	 */
	private int isPreview = NOT_PREV;
	/**
	 * 提醒过的次数
	 */
	private int remindCount = 0;
	/**
	 * 是否已读。{@link RemindEntity#READ} : 已读； {@link RemindEntity#NOT_READ} : 未读。
	 * 默认为未读
	 */
	private int isRead = NOT_READ;
	/**
	 * 预留字段
	 */
	private String z1;
	/**
	 * 预留字段
	 */
	private String z2;
	/**
	 * 预留字段
	 */
	private String z3;

	public RemindEntity() {
		super();
	}

	/**
	 * @param id			id
	 * @param ownerNum		本机手机号
	 * @param targetNum		对方手机号
	 * @param targetName	对方联系人名称
	 * @param targetNick	对方昵称
	 * @param addTime		添加时间
	 * @param remindTimeMiLi	提醒时间毫秒数
	 * @param content		提醒内容
	 * @param limitTime		任务时限
	 * @param remindTime	提醒时间
	 * @param title			提醒标题
	 * @param repeatType	重复模式
	 * @param isPreview		是否可以预览
	 * @param noticeId		提醒的id
	 * @param ownerId		发送者的id
	 * @param isRead		是否已读
	 */
	public RemindEntity(String id,String ownerNum, String targetNum, String targetName,
			String targetNick, String addTime, String remindTimeMiLi,
			String content, String limitTime, String remindTime, String title, String repeatType, int isPreview,
			String noticeId, String ownerId, int isRead) {
		super();
		this.id = id;
		this.ownerNum = ownerNum;
		this.targetNum = targetNum;
		this.targetName = targetName;
		this.nickName = targetNick;
		this.addTime = addTime;
		this.remindTimeMiLi = remindTimeMiLi;
		this.content = content;
		this.limitTime = limitTime;
		this.remindTime = remindTime;
		this.title = title;
		this.repeatType = repeatType;
		this.isPreview = isPreview;
		this.noticeId = noticeId;
		this.ownerId = ownerId;
		this.isRead = isRead;
	}

	/**
	 * @param id
	 * @param ownerNum		本机手机号
	 * @param targetNum		对方手机号
	 * @param targetName	对方联系人名称
	 * @param targetNick	对方昵称
	 * @param addTime		添加时间
	 * @param remindTimeMiLi	提醒时间毫秒数
	 * @param content		提醒内容
	 * @param limitTime		任务时限
	 * @param remindTime	提醒时间
	 * @param title			提醒标题
	 * @param audioPath		音频路径
	 * @param videoPath		视频路径
	 * @param imgPath		图片路径
	 * @param remindMethod	* 提醒方式, 音频：{@link #AUDIO_REMIND},
	 * 							视频:{@link #VIDEO_REMIND},
	 * 							图像:{@link #IMAGE_REMIND},
	 * 							默认为音频{@link #AUDIO_REMIND}
	 * @param remindState	提醒的状态, 已接受：{@link #ACCEPT},
	 * 							未接受提醒：{@link #NEW},
	 * 							已拒绝：{@link #REFUSE},
	 * 							我发起的：{@link #LAUNCH},
	 * 							默认为新提醒{@link #NEW}
	 * @param launchState	我发起的提醒的状态，
	 * 							等待对方接受：{@link #LAUNCH_WAIT},
	 * 							对方已拒绝：{@link #LAUNCH_REFUSE},
	 * 							对方已接受：{@link #LAUNCH_ACCEPT},
	 * 							默认为等待对方接受{@link #LAUNCH_WAIT}
	 * @param isDelete		是否删除, {@link #NORMAL}：未删除；{@link #DELETED}：已删除
	 * 							默认为未删除:{@link #NORMAL}
	 * @param repeatType	重复模式： 不重复：{@link #REPEAT_NO},
	 * 								每天重复：{@link #REPEAT_DAY},
	 * 								每周重复：{@link #REPEAT_WEEK},
	 * 								每月重复：{@link #REPEAT_MONTH},
	 * 								每年重复：{@link #REPEAT_YEAR},
	 * @param isPreview		是否可以预览,{@link #NOT_PREV}：不能预览；{@link #CAN_PREV}：能预览
	 * 								默认为不能预览:{@link #NOT_PREV}
	 * @param remindCount	提醒过的次数
	 * @param noticeId		提醒的id
	 * @param ownerId		发送者的id
	 * @param isRead		是否已读
	 */
	public RemindEntity(String id, String ownerNum, String targetNum,
			String targetName, String nickName, String addTime,
			String remindTimeMiLi, String title, String content,
			String limitTime, String remindTime, String audioPath,
			String videoPath, String imgPath, int remindMethod,
			int remindState, int launchState, String isDelete, String repeatType, int isPreview, int remindCount,
			String noticeId, String ownerId, int isRead) {
		super();
		this.id = id;
		this.ownerNum = ownerNum;
		this.targetNum = targetNum;
		this.targetName = targetName;
		this.nickName = nickName;
		this.addTime = addTime;
		this.remindTimeMiLi = remindTimeMiLi;
		this.title = title;
		this.content = content;
		this.limitTime = limitTime;
		this.remindTime = remindTime;
		this.audioPath = audioPath;
		this.videoPath = videoPath;
		this.imgPath = imgPath;
		this.remindMethod = remindMethod;
		this.remindState = remindState;
		this.launchState = launchState;
		this.isDelete = isDelete;
		this.repeatType = repeatType;
		this.isPreview = isPreview;
		this.remindCount = remindCount;
		this.noticeId = noticeId;
		this.ownerId = ownerId;
		this.isRead = isRead;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwnerNum() {
		return ownerNum;
	}

	public void setOwnerNum(String ownerNum) {
		this.ownerNum = ownerNum;
	}

	public String getTargetNum() {
		return targetNum;
	}

	public void setTargetNum(String targetNum) {
		this.targetNum = targetNum;
	}

	public String getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getRemindTimeMiLi() {
		return remindTimeMiLi;
	}

	public void setRemindTimeMiLi(String remindTimeMiLi) {
		this.remindTimeMiLi = remindTimeMiLi;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLimitTime() {
		return limitTime;
	}

	public void setLimitTime(String limitTime) {
		this.limitTime = limitTime;
	}

	public String getRemindTime() {
		return remindTime;
	}

	public void setRemindTime(String remindTime) {
		this.remindTime = remindTime;
	}

	public String getAudioPath() {
		return audioPath;
	}

	/**
	 * 设置音频路径，同时不要忘了改变类型为音频
	 * @param audioPath
	 */
	public void setAudioPath(String audioPath) {
		this.audioPath = audioPath;
	}

	public String getVideoPath() {
		return videoPath;
	}

	/**
	 * 设置视频路径，同时不要忘了改变类型为视频
	 * @param videoPath
	 */
	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	public String getImgPath() {
		return imgPath;
	}

	/**
	 * 设置图片路径，同时不要忘了改变类型为图片
	 * @param imgPath
	 */
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	/**
	 * @return		提醒方式
	 * @see #remindMethod
	 */
	public int getRemindMethod() {
		return remindMethod;
	}

	public void setRemindMethod(int remindMethod) {
		this.remindMethod = remindMethod;
	}

	/**
	 * @return		提醒的状态
	 * @see #remindState
	 */
	public int getRemindState() {
		return remindState;
	}

	/**
	 * @see #remindState
	 * @param remindState		提醒的状态
	 */
	public void setRemindState(int remindState) {
		this.remindState = remindState;
	}

	/**
	 * @return		我发起的提醒的状态
	 * @see #launchState
	 */
	public int getLaunchState() {
		return launchState;
	}

	/**
	 * @see #launchState
	 * @param launchState	我发起的提醒的状态
	 */
	public void setLaunchState(int launchState) {
		this.launchState = launchState;
	}

	/**
	 *
	 * 重复模式：
	 * 不重复：{@link #REPEAT_NO},
	 * 每天重复：{@link #REPEAT_DAY},
	 * 每周重复：{@link #REPEAT_WEEK},
	 * 每月重复：{@link #REPEAT_MONTH},
	 * 每年重复：{@link #REPEAT_YEAR},
	 *
	 * @return
	 */
	public String getRepeatType() {
		return repeatType;
	}

	/**
	 *  重复模式：
	 * 不重复：{@link #REPEAT_NO},
	 * 每天重复：{@link #REPEAT_DAY},
	 * 每周重复：{@link #REPEAT_WEEK},
	 * 每月重复：{@link #REPEAT_MONTH},
	 * 每年重复：{@link #REPEAT_YEAR},
	 * @param repeatType
	 */
	public void setRepeatType(String repeatType) {
		this.repeatType = repeatType;
	}

	/**
	 * @return	是否删除
	 * @see #isDelete
	 */
	public String getIsDelete() {
		return isDelete;
	}

	/**
	 * @see #isDelete
	 * @param isDelete	是否删除
	 */
	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return		是否可以预览，{@link #NOT_PREV}：不能预览；{@link #CAN_PREV}：能预览
	 * 默认为不能预览:{@link #NOT_PREV}
	 */
	public int getIsPreview() {
		return isPreview;
	}

	/**
	 * 是否可以预览，{@link #NOT_PREV}：不能预览；{@link #CAN_PREV}：能预览
	 * 默认为不能预览:{@link #NOT_PREV}
	 * @param isPreview
	 */
	public void setIsPreview(int isPreview) {
		this.isPreview = isPreview;
	}

	/**
	 * @return		提醒过的次数
	 */
	public int getRemindCount() {
		return remindCount;
	}

	/**
	 * 提醒过的次数
	 * @param remindCount
	 */
	public void setRemindCount(int remindCount) {
		this.remindCount = remindCount;
	}

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	public String getZ1() {
		return z1;
	}

	public void setZ1(String z1) {
		this.z1 = z1;
	}

	public String getZ2() {
		return z2;
	}

	public void setZ2(String z2) {
		this.z2 = z2;
	}

	public String getZ3() {
		return z3;
	}

	public void setZ3(String z3) {
		this.z3 = z3;
	}
}
