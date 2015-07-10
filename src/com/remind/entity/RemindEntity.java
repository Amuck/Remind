package com.remind.entity;

import java.io.Serializable;

/**
 * @author ChenLong
 *
 * 提醒的实体类
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
	/**
	 * 联系人备注名称
	 */
	private String nickName;
	/**
	 * 添加时间
	 */
	private String addTime;
	/**
	 * 最后编辑时间
	 */
	private String lastEditTime;
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
	 * 是否删除, {@link #NORMAL}：未删除；{@link #DELETED}：已删除
	 * 默认为未删除:{@link #NORMAL}
	 */
	private String isDelete = RemindEntity.NORMAL;
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
	 * @param ownerNum		本机手机号
	 * @param targetNum		对方手机号
	 * @param targetName	对方联系人名称
	 * @param targetNick	对方昵称
	 * @param addTime		添加时间
	 * @param lastEditTime	最后编辑时间
	 * @param content		提醒内容
	 * @param limitTime		任务时限
	 * @param remindTime	提醒时间
	 * @param title			提醒标题
	 */
	public RemindEntity(String id,String ownerNum, String targetNum, String targetName,
			String targetNick, String addTime, String lastEditTime,
			String content, String limitTime, String remindTime, String title) {
		super();
		this.id = id;
		this.ownerNum = ownerNum;
		this.targetNum = targetNum;
		this.targetName = targetName;
		this.nickName = targetNick;
		this.addTime = addTime;
		this.lastEditTime = lastEditTime;
		this.content = content;
		this.limitTime = limitTime;
		this.remindTime = remindTime;
		this.title = title;
	}
	
	

	/**
	 * @param id
	 * @param ownerNum		本机手机号
	 * @param targetNum		对方手机号
	 * @param targetName	对方联系人名称
	 * @param targetNick	对方昵称
	 * @param addTime		添加时间
	 * @param lastEditTime	最后编辑时间
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
	 */
	public RemindEntity(String id, String ownerNum, String targetNum,
			String targetName, String nickName, String addTime,
			String lastEditTime, String title, String content,
			String limitTime, String remindTime, String audioPath,
			String videoPath, String imgPath, int remindMethod,
			int remindState, int launchState, String isDelete) {
		super();
		this.id = id;
		this.ownerNum = ownerNum;
		this.targetNum = targetNum;
		this.targetName = targetName;
		this.nickName = nickName;
		this.addTime = addTime;
		this.lastEditTime = lastEditTime;
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

	public String getLastEditTime() {
		return lastEditTime;
	}

	public void setLastEditTime(String lastEditTime) {
		this.lastEditTime = lastEditTime;
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

	public void setAudioPath(String audioPath) {
		this.audioPath = audioPath;
		// 改变提醒为音频
		remindMethod = RemindEntity.AUDIO_REMIND;
	}

	public String getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
		// 改变提醒为视频
		remindMethod = RemindEntity.VIDEO_REMIND;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
		// 改变提醒为图像
		remindMethod = RemindEntity.IMAGE_REMIND;
	}

	/**
	 * @return		提醒方式
	 * @see #remindMethod
	 */
	public int getRemindMethod() {
		return remindMethod;
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