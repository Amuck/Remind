package com.remind.entity;

public class MessageEntity implements Cloneable{
	/**
	 * 正常状态
	 */
	public final static String NORMAL = "0";
	/**
	 * 已删除状态
	 */
	public final static String DELETED = "1";
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
	 * 消息类型，文字消息
	 */
	public final static String TYPE_TEXT = "0";
	/**
	 * 消息类型，提醒
	 */
	public final static String TYPE_REMIND = "1";
	/**
	 * 消息类型，语音
	 */
	public final static String TYPE_VOICE = "2";
	/**
	 * 用户发送的消息
	 */
	public final static String TYPE_SEND = "0";
	/**
	 * 用户接受的消息
	 */
	public final static String TYPE_RECIEVE = "1";
	/**
	 * 用户反馈成功
	 */
	public final static String FEED_SUCCESS = "feed_success";
	/**
	 * 用户反馈失败
	 */
	public final static String FEED_FAIL = "feed_fail";
	/**
	 * 用户反馈最初
	 */
	public final static String FEED_DEFAULT = "feed_default";
	
	/**
	 * id
	 */
	private String id;
	
	/**
	 * 消息索引
	 */
	private String messageIndex;
	/**
	 * 接受人的名称
	 */
	private String recieveName;
	/**
	 * 接收人的号码
	 */
	private String recieveNum;
	/**
	 * 发送人的名称
	 */
	private String sendName;
	/**
	 * 发送人的号码
	 */
	private String sendNum;
	/**
	 * 发送方，用户发送：{@link #TYPE_SEND}，用户接收：{@link #TYPE_RECIEVE}
	 */
	private String isComing;
	/**
	 * 发送时间
	 */
	private String time;
	/**
	 * 消息内容/语音时长
	 */
	private String content;
	/**
	 * 发送状态, {@link #SEND_SUCCESS}：发送成功；{@link #SENDING}：正在发送;{@link #SEND_FAIL}：发送失败
	 */
	private String sendState;
	/**
	 * 是否删除, {@link #NORMAL}：未删除；{@link #DELETED}：已删除
	 */
	private String isDelete;
	
	/**
	 * 消息类型，文字：{@link #TYPE_TEXT}, 提醒：{@link #TYPE_REMIND},语音：{@link #TYPE_VOICE},默认为文字
	 */
	private String msgType = MessageEntity.TYPE_TEXT;
	
	/**
	 * 其他类型消息的id
	 */
	private String otherTypeId;
	
	/**
	 * 其他消息类型，储存路径
	 */
	private String msgPath;
	/**
	 * 收到的消息是否反馈成功，成功：{@link #FEED_SUCCESS}；失败：{@link #FEED_FAIL}；初始：{@link #FEED_DEFAULT}
	 */
	private String feed = MessageEntity.FEED_DEFAULT;
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

	public MessageEntity() {
	}

	/**
	 * @param id
	 * @param recieveName	接受人的名称
	 * @param recieveNum	接收人的号码
	 * @param sendName		发送人的名称
	 * @param sendNum		发送人的号码
	 * @param time			发送时间
	 * @param sendState		发送状态, {@link #SEND_SUCCESS}：发送成功；{@link #SENDING}：正在发送;{@link #SEND_FAIL}：发送失败
	 * @param isDelete		是否删除, {@link #NORMAL}：未删除；{@link #DELETED}：已删除
	 * @param msgType		消息类型，文字：{@link #TYPE_TEXT}, 提醒：{@link #TYPE_REMIND},默认为文字
	 * @param otherTypeId	其他类型消息的id
	 * @param msgPath		其他消息类型，储存路径
	 * @param messageIndex	消息索引
	 * @param isComing		发送方，用户发送：TYPE_SEND，用户接收：TYPE_RECIEVE
	 * @param content		消息内容
	 * @param feed			收到的消息是否反馈成功
	 */
	public MessageEntity(String id, String recieveName, String recieveNum,
			String sendName, String sendNum, String time, String sendState,
			String isDelete, String msgType, String otherTypeId,
			String msgPath, String messageIndex, String isComing, String content, 
			String feed) {
		super();
		this.id = id;
		this.recieveName = recieveName;
		this.recieveNum = recieveNum;
		this.sendName = sendName;
		this.sendNum = sendNum;
		this.time = time;
		this.sendState = sendState;
		this.isDelete = isDelete;
		this.msgType = msgType;
		this.otherTypeId = otherTypeId;
		this.msgPath = msgPath;
		this.messageIndex = messageIndex;
		this.isComing = isComing;
		this.content = content;
		this.feed = feed;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessageIndex() {
		return messageIndex;
	}

	public void setMessageIndex(String messageIndex) {
		this.messageIndex = messageIndex;
	}

	public String getRecieveName() {
		return recieveName;
	}

	public void setRecieveName(String recieveName) {
		this.recieveName = recieveName;
	}

	public String getRecieveNum() {
		return recieveNum;
	}

	public void setRecieveNum(String recieveNum) {
		this.recieveNum = recieveNum;
	}

	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
	}

	public String getSendNum() {
		return sendNum;
	}

	public void setSendNum(String sendNum) {
		this.sendNum = sendNum;
	}

	public String getIsComing() {
		return isComing;
	}

	public void setIsComing(String isComing) {
		this.isComing = isComing;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSendState() {
		return sendState;
	}

	public void setSendState(String sendState) {
		this.sendState = sendState;
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getOtherTypeId() {
		return otherTypeId;
	}

	public void setOtherTypeId(String otherTypeId) {
		this.otherTypeId = otherTypeId;
	}

	public String getMsgPath() {
		return msgPath;
	}

	public void setMsgPath(String msgPath) {
		this.msgPath = msgPath;
	}

	public String getFeed() {
		return feed;
	}

	public void setFeed(String feed) {
		this.feed = feed;
	}

	@Override
	public MessageEntity clone() {
		MessageEntity messageEntity = null;
		try {
			messageEntity = (MessageEntity) super.clone();
		} catch (CloneNotSupportedException e) {
		}

		return messageEntity;
	}
}
