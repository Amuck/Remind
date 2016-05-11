package com.remind.dao.msg;

/**
 * 存储message， 第一索引为联系人手机号,需要将MSG_INDEX设置为RECIEVE_NUM，第二索引为点击的提醒的id(OTHER_TYPE_ID,MSG_TYPE)；
 * 建立/收到提醒后，将提醒加入数据库；
 * 发送/收到消息后，将消息加入数据库；
 * 发送消息/提醒等失败/成功后，需要修改发送状态SEND_STATE，失败的发送需要再次尝试
 * 
 * @author ChenLong
 *
 */
public class MessageMsg {

	/**
	 * 表名
	 */
	public static final String TABLENAME = "MessageMsg";

	public static final String ID = "id";
	
	/**
	 * 消息索引
	 */
	public static final String MSG_INDEX = "msgIndex";
	/**
	 * 接受人的名称
	 */
	public static final String RECIEVE_NAME = "recieveName";
	/**
	 * 接收人的号码
	 */
	public static final String RECIEVE_NUM = "recieveNum";
	/**
	 * 发送人的名称
	 */
	public static final String SEND_NAME = "sendName";
	/**
	 * 登陆用户
	 */
	public static final String LOGIN_USER = "login_user";
	/**
	 * 发送人的号码
	 */
	public static final String SEND_NUM = "sendNum";
	/**
	 * 发送时间
	 */
	public static final String TIME = "time";
	/**
	 * 发送内容
	 */
	public static final String CONTENT = "content";
	/**
	 * 发送状态, 0：发送成功；1：正在发送;2：发送失败
	 */
	public static final String SEND_STATE = "sendState";
	/**
	 * 是否删除, 0：未删除；1：已删除
	 */
	public static final String ISDELETE = "isDelete";
	
	/**
	 * 消息类型，文字：0, 提醒：1,默认为文字
	 */
	public static final String MSG_TYPE = "msgType";
	/**
	 * 其他类型消息的id
	 */
	public static final String OTHER_TYPE_ID = "otherTypeId";
	/**
	 * 其他消息类型，储存路径
	 */
	public static final String MSG_PATH = "msgPath";
	/**
	 * 	发送方，用户发送：0，用户接收：1
	 */
	public static final String IS_COMING = "isComing";
	/**
	 * 收到的消息是否反馈成功，成功：feed_success；失败：feed_fail；初始：feed_default
	 */
	public static final String IS_FEED = "isfeed";
	/**
	 * 事件驱动id，为所属提醒的id
	 */
	public static final String REMIND_ID = "remindId";
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
