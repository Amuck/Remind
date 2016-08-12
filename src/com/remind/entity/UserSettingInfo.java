package com.remind.entity;

import java.io.Serializable;

/**
 * @author ChenLong
 * 
 *         用户配置信息
 */
public class UserSettingInfo implements Serializable {

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
    }

    public String getBackgroundPath() {
        return backgroundPath;
    }

    public void setBackgroundPath(String backgroundPath) {
        this.backgroundPath = backgroundPath;
    }

    public String getChatBubblePath() {
        return chatBubblePath;
    }

    public void setChatBubblePath(String chatBubblePath) {
        this.chatBubblePath = chatBubblePath;
    }

}
