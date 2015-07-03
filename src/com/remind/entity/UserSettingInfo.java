package com.remind.entity;

import java.io.Serializable;

/**
 * @author ChenLong
 *
 * 用户配置信息
 */
public class UserSettingInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -790098576364912437L;
	
	/**
	 * 聊天背景图片路径
	 */
	private String backgroundPath;
	/**
	 * 聊天气泡图片路径
	 */
	private String chatBubblePath;

	public UserSettingInfo() {
		// TODO Auto-generated constructor stub
	}

}
