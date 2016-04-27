package com.remind.entity;

import java.io.Serializable;

/**
 * @author ChenLong
 * 
 *         消息索引实体类
 */
public class MessageIndexEntity implements Serializable {

	private static final long serialVersionUID = 8347980702459171513L;

	/**
	 * 正常状态
	 */
	public final static String NORMAL = "0";
	/**
	 * 已删除状态
	 */
	public final static String DELETED = "1";
	/**
	 * 已读
	 */
	public final static String READ = "0";
	/**
	 * 未读
	 */
	public final static String NOT_READ = "1";
	/**
	 * 发送成功
	 */
	public final static String SEND_SUCCESS = "0";
	/**
	 * 正在发送
	 */
	public final static String SENDING = "1";
	/**
	 * 发送失败
	 */
	public final static String SEND_FAIL = "2";
	/**
	 * 消息id
	 */
	private String id;

	/**
	 * 联系人号码
	 */
	private String num;
	/**
	 * 最后消息
	 */
	private String message;
	/**
	 * 最后发送时间
	 */
	private String time;
	/**
	 * 登陆的用户
	 */
	private String loginUser;
	/**
	 * 名字
	 */
	private String name;
	/**
	 * 头像路径
	 */
	private String imgPath;
	/**
	 * 未读数量
	 */
	private int unReadCount = 0;
	/**
	 * 是否删除, {@link #NORMAL}：未删除；{@link #DELETED}：已删除
	 */
	private String isDelete = MessageIndexEntity.NORMAL;
	
	/**
	 * 发送状态, {@link #SEND_SUCCESS}：发送成功；{@link #SENDING}：正在发送;{@link #SEND_FAIL}：发送失败
	 */
	private String sendState = MessageIndexEntity.SEND_SUCCESS;
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

	public MessageIndexEntity() {
	}

	/**
	 * @param id
	 * @param num			联系人号码
	 * @param message		消息
	 * @param time			时间
	 * @param isRead		是否已读
	 * @param isDelete		是否删除
	 * @param sendState		发送状态
	 * @param name			名字
	 * @param imgPath		头像路径
	 */
	public MessageIndexEntity(String id, String num, String message,
			String time, String name, String imgPath, int unReadCount, 
			String isDelete, String sendState, String loginUser) {
		super();
		this.id = id;
		this.num = num;
		this.message = message;
		this.time = time;
		this.unReadCount = unReadCount;
		this.isDelete = isDelete;
		this.sendState = sendState;
		this.name = name;
		this.imgPath = imgPath;
		this.loginUser = loginUser;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getUnReadCount() {
		return unReadCount;
	}

	public void setUnReadCount(int unReadCount) {
		this.unReadCount = unReadCount;
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}

	public String getSendState() {
		return sendState;
	}

	public void setSendState(String sendState) {
		this.sendState = sendState;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
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
