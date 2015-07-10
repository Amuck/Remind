package com.remind.dao.msg;

public class PeopelMsg {
	/**
	 * 表名
	 */
	public static final String TABLENAME = "Peopel";

	public static final String ID = "id";

	/**
	 * 联系人名称
	 */
	public static final String NAME = "name";
	/**
	 * 备注名称
	 */
	public static final String NICKNAME = "nickName";
	/**
	 * 联系人手机号
	 */
	public static final String NUM = "num";
	/**
	 * 添加时间
	 */
	public static final String ADDTIME = "addTime";
	/**
	 * 更新时间
	 */
	public static final String UPDATETIME = "updateTime";
	/**
	 * 头像路径
	 */
	public static final String IMGPATH = "imgPath";
	/**
	 * 是否删除, 0：未删除；1：已删除
	 */
	public static final String ISDELETE = "isDelete";
	/**
	 * * 好友状态，0：等待对方验证
	 * 1：等待用户接受
	 * 2：已添加的好友
	 */
	public static final String STATUS = "status";
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