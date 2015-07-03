package com.remind.entity;

import java.io.Serializable;

/**
 * @author ChenLong
 *
 * 联系人实体类
 */
public class PeopelEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7824828450459621258L;
	/**
	 * 正常状态
	 */
	public final static String NORMAL = "0";
	/**
	 * 已删除状态
	 */
	public final static String DELETED = "1";
	
	/**
	 * 等待对方验证
	 */
	public final static int VALIDATE = 0;
	/**
	 * 等待用户接受
	 */
	public final static int ACCEPT = 1;
	/**
	 * 已添加的好友
	 */
	public final static int FRIEND = 2;
	/**
	 * 联系人名称
	 */
	private String name;
	/**
	 * 备注名称
	 */
	private String nickName;
	/**
	 * 联系人手机号
	 */
	private String num;
	/**
	 * 添加联系人时间
	 */
	private String addTime;
	/**
	 * 更新联系人时间
	 */
	private String updateTime;
	/**
	 * 头像路径
	 */
	private String imgPath;
	/**
	 * 是否删除, {@link #NORMAL}：未删除；{@link #DELETED}：已删除
	 */
	private String isDelete = PeopelEntity.NORMAL;
	
	/**
	 * 好友状态，{@link #VALIDATE}：等待对方验证
	 * {@link #ACCEPT}：等待用户接受
	 * {@link #FRIEND}：已添加的好友
	 */
	private int status;
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

	public PeopelEntity() {
		super();
	}

	/**
	 * @param name
	 *            联系人名称
	 * @param nickName
	 *            备注名称
	 * @param num
	 *            联系人手机号
	 * @param addTime
	 *            添加联系人时间
	 * @param updateTime
	 *            更新联系人时间
	 * @param imgPath
	 *            头像路径
	 * @param isDelete
	 *            是否删除,{@link #NORMAL}：未删除；{@link #DELETED}：已删除
	 * @param status
	 *            好友状态，{@link #VALIDATE}：等待对方验证 {@link #ACCEPT}：等待用户接受
	 *            {@link #FRIEND}：已添加的好友
	 */
	public PeopelEntity(String name, String nickName, String num,
			String addTime, String updateTime, String imgPath, 
			String isDelete, int status) {
		super();
		this.name = name;
		this.nickName = nickName;
		this.num = num;
		this.addTime = addTime;
		this.updateTime = updateTime;
		this.imgPath = imgPath;
		this.isDelete = isDelete;
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
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

	/**
	 * @return	好友状态
	 * @see #status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @see #status
	 * @param status	好友状态
	 */
	public void setStatus(int status) {
		this.status = status;
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
