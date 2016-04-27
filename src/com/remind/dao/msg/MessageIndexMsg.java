package com.remind.dao.msg;

public class MessageIndexMsg {
	/**
	 * 表名
	 */
	public static final String TABLENAME = "MessageIndex";

	public static final String ID = "id";

	/**
	 * 联系人号码
	 */
	public static final String NUM = "num";
	/**
	 * 最后消息
	 */
	public static final String MESSAGE = "message";

	/**
	 * 最后发送时间
	 */
	public static final String TIME = "time";
	/**
	 * 联系人名字
	 */
	public static final String NAME = "name";
	/**
	 * 登陆用户
	 */
	public static final String LOGIN_USER = "login_user";
	/**
	 * 联系人头像路径
	 */
	public static final String IMG_PATH = "imgPath";
	/**
	 * 未读条数
	 */
	public static final String UNREAND_COUNT = "unReadCount";
	/**
	 * 是否删除, {@link #NORMAL}：未删除；{@link #DELETED}：已删除
	 */
	public static final String ISDELETE = "isDelete";
	/**
	 * 发送状态, {@link #SEND_SUCCESS}：发送成功；{@link #SENDING}：正在发送;{@link #SEND_FAIL}：发送失败
	 */
	public static final String SEND_STATE = "sendState";
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
