package com.remind.receiver;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.RemoteException;
import android.widget.Toast;

import com.remind.application.RemindApplication;
import com.remind.dao.impl.MessageDaoImpl;
import com.remind.dao.impl.MessageIndexDaoImpl;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.dao.impl.RemindDaoImpl;
import com.remind.entity.MessageEntity;
import com.remind.entity.MessageIndexEntity;
import com.remind.entity.PeopelEntity;
import com.remind.entity.RemindEntity;
import com.remind.global.AppConstant;
import com.remind.http.HttpClient;
import com.remind.sevice.BackService;
import com.remind.up.Download;
import com.remind.util.AppUtil;
import com.remind.util.DataBaseParser;

/**
 * @author ChenLong
 * 
 *         监听重启事件，重置所有提醒
 */
public class MessageReceiver extends BroadcastReceiver {
    /**
     * 获得发送消息的反馈, 是否发送成功
     */
    public static final String MESSAGE_BACK_ACTION = "com.remind.message_back_ACTION";
    /**
     * 好友状态改变
     */
    public static final String PEOPLE_STATE_CHANGE_ACTION = "com.remind.people_state_change";
    /**
     * 添加好友
     */
    public static final String PEOPLE_ADD_ACTION = "com.remind.people_add";
    /**
     * 获得消息/提醒
     */
    public static final String GET_MESSAGE_ACTION = "com.remind.get_msg";
    /**
     * 改变提醒状态
     */
    public static final String NOTICE_STATE_ACTION = "com.remind.notice_state";
    /**
     * socket注册状态
     */
    public static final String LOGIN_STATE_ACTION = "com.remind.login";

    private Context context;
    private JSONObject jsonObject;
    private MessageDaoImpl messageDaoImpl;
    private PeopelDaoImpl peopelDao;
    private RemindDaoImpl remindDaoImpl;
    private MessageIndexDaoImpl messageIndexDao;

    public MessageReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();

        if (action.equals(BackService.HEART_BEAT_ACTION)) {
            // Toast.makeText(context, "Get a heart heat", Toast.LENGTH_SHORT)
            // .show();
        } else if (action.equals(BackService.MESSAGE_ACTION)) {
            if (null == messageDaoImpl) {
                messageDaoImpl = new MessageDaoImpl(context);
                remindDaoImpl = new RemindDaoImpl(context);
                peopelDao = new PeopelDaoImpl(context);
                messageIndexDao = new MessageIndexDaoImpl(context);
            }
            
            String message = intent.getStringExtra("message");

            try {
                jsonObject = new JSONObject(message.substring(message.indexOf("{"), message.length()));
                String type = jsonObject.getString("type");
                if ("message".equals(type)) {
                    // 收到短消息，需要回馈
                    getMsg();
                } else if ("message_ack".equals(type)) {
                    // 发送消息的反馈
                    getBack();
                } else if ("notification".equals(type)) {
                    // socket注册
                    registBack();
                } else if ("system".equals(type)) {
                    system();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 回馈
     * 
     * @param type
     * @throws JSONException
     * @throws RemoteException
     */
    private String feedBack() throws JSONException, RemoteException {
        String type = "message_ack";
        String mid = jsonObject.getString("mid");
        String param = HttpClient.getJsonForPost(HttpClient.msgFeedBack(mid, type));
        String feed = MessageEntity.FEED_DEFAULT;
        boolean isSuccess = RemindApplication.iBackService.sendMessage(param);
        if (isSuccess) {
            feed = MessageEntity.FEED_SUCCESS;
            // Toast.makeText(context, "回馈成功", Toast.LENGTH_SHORT).show();
        } else {
            feed = MessageEntity.FEED_FAIL;
            Toast.makeText(context, "回馈fail", Toast.LENGTH_SHORT).show();
        }

        return feed;
    }

    /**
     * 收到朋友发过来的消息
     * 
     * @throws JSONException
     * @throws RemoteException
     */
    private void getMsg() throws RemoteException, JSONException {
        String feed = feedBack();

        String content = jsonObject.getString("content");
        JSONObject contentObj = new JSONObject(content);
        String from_id = jsonObject.getString("from_id");
        // 获取发送人信息
        Cursor cursor = peopelDao.queryPeopelByFriendId(from_id);
        PeopelEntity entity = DataBaseParser.getPeoPelDetail(cursor).get(0);
        cursor.close();

        if (null != entity) {
            MessageEntity messageEntity = new MessageEntity("", "", "", entity.getNickName(), entity.getNum(),
                    AppUtil.getNowTime(), MessageEntity.SEND_SUCCESS, MessageEntity.NORMAL, contentObj.getString("msgType"),
                    "", contentObj.getString("msgPath"), entity.getNum(), MessageEntity.TYPE_RECIEVE,
                    contentObj.getString("content"), feed, AppConstant.USER_NUM, contentObj.getString("remindId"));
            // 插入数据库
            long id = messageDaoImpl.insert(messageEntity);

            cursor = messageIndexDao.queryByNum(entity.getNum());
            if (cursor.getCount() > 0) {
                MessageIndexEntity messageIndexEntitiy = DataBaseParser.getMessageIndex(cursor).get(0);
                messageIndexEntitiy.setTime(messageEntity.getTime());
                messageIndexEntitiy.setMessage(messageEntity.getContent());
                messageIndexDao.update(messageIndexEntitiy);
            } else {
                MessageIndexEntity messageIndexEntity = new MessageIndexEntity("", entity.getNum(),
                        messageEntity.getContent(), messageEntity.getTime(), entity.getNickName(), entity.getImgPath(), 0,
                        MessageIndexEntity.NORMAL, MessageIndexEntity.SEND_SUCCESS, AppConstant.USER_NUM);
                messageIndexDao.insert(messageIndexEntity);
            }
            cursor.close();
            // 通知UI界面
            Intent intent = new Intent(GET_MESSAGE_ACTION);
            intent.putExtra("messageEntity", messageEntity);
            context.sendBroadcast(intent);

            String msg = contentObj.getString("content");
            if (MessageEntity.TYPE_TEXT.equals(messageEntity.getMsgType())) {
                // 文字信息
            } else if (MessageEntity.TYPE_VOICE.equals(messageEntity.getMsgType())){
                // 语音消息, 下载语音
                msg = "[语音聊天]";
                Download download = Download.getInstance();
                download.downLoadAmr(messageEntity.getMsgPath(), id);
            }
            
            // 发送notification
            if (!RemindApplication.IS_CHAT_VIEW_SHOW) {
                AppUtil.simpleNotify(context, entity.getNum(), 0, entity.getNickName(), entity.getNum(),
                        msg, true);
            }
        }
    }

    /**
     * 获得发送消息的反馈, 发送成功/失败
     * 
     * @param type
     * @throws JSONException
     */
    private void getBack() throws JSONException {
        // +{"type":"message_ack","mid":"4","code":200}
        String mid = jsonObject.getString("mid");
        String code = jsonObject.getString("code");
        if ("200".equals(code)) {
            // 发送成功，更新数据库和界面
            messageDaoImpl.updateSendState(Long.valueOf(mid), MessageEntity.SEND_SUCCESS);
        } else {
            // 发送失败
            messageDaoImpl.updateSendState(Long.valueOf(mid), MessageEntity.SEND_FAIL);
        }
        Intent intent = new Intent(MESSAGE_BACK_ACTION);
        intent.putExtra("mid", mid);
        intent.putExtra("code", code);
        context.sendBroadcast(intent);
    }

    /**
     * 是否注册成功
     * 
     * @param type
     * @throws JSONException
     */
    private void registBack() throws JSONException {
        String mid = jsonObject.getString("mid");
        String code = jsonObject.getString("code");
        if ("socket_regist".equals(mid)) {
            if ("200".equals(code)) {
                RemindApplication.IS_LOGIN = true;

            } else {
                RemindApplication.IS_LOGIN = false;

            }
        }

        Intent intent = new Intent(LOGIN_STATE_ACTION);
        context.sendBroadcast(intent);
    }

    /**
     * 处理系统消息
     * 
     * @throws RemoteException
     * @throws JSONException
     */
    private void system() throws RemoteException, JSONException {
        feedBack();

        String content = jsonObject.getString("content");
        String friend_id = "";
        String pn = "";
        PeopelEntity entity = null;
        int state = 0;
        String nick = "";
        String avatar = "";

        JSONObject contentObj = new JSONObject(content);
        int type = contentObj.getInt("type");
        JSONObject dataObj = new JSONObject(contentObj.getString("data"));
        Intent intent = new Intent();
        switch (type) {
        case 1:
            // 添加好友
            // 收到添加好友请求
            friend_id = dataObj.getString("friend_id");
            nick = dataObj.getString("nick");
            avatar = dataObj.getString("avatar");
            pn = dataObj.getString("pn");
            entity = new PeopelEntity(nick, nick, pn, "", "", avatar, PeopelEntity.NORMAL, PeopelEntity.ACCEPT, friend_id,
                    AppConstant.USER_NUM);
            peopelDao.insertPeopel(entity);
            // 通知UI刷新页面
            intent.setAction(PEOPLE_ADD_ACTION);
            intent.putExtra("PeopelEntity", entity);
            context.sendBroadcast(intent);
            // 发送notification
            AppUtil.simpleNotify(context, pn, 3, nick, pn, "", true);
            break;
        case 2:
            // 同意添加好友
            friend_id = dataObj.getString("friend_id");
            String stateString = dataObj.getString("state");
            nick = dataObj.getString("nick");
            avatar = dataObj.getString("avatar");
            pn = dataObj.getString("pn");
            Cursor cursor = peopelDao.queryPeopelByNum(pn);
            ArrayList<PeopelEntity> lists = DataBaseParser.getPeoPelDetail(cursor);
            cursor.close();
            entity = lists.get(0);
            if ("1".equals(stateString)) {
                // 同意
                entity.setStatus(PeopelEntity.FRIEND);
            } else {
                entity.setStatus(PeopelEntity.REFUSE);
            }

            entity.setName(nick);
//            entity.setNickName(nick);
            entity.setImgPath(avatar);
            entity.setFriendId(friend_id);
            peopelDao.updatePeopel(entity);

            // 通知UI刷新页面
            intent.setAction(PEOPLE_STATE_CHANGE_ACTION);
            intent.putExtra("pn", pn);
            intent.putExtra("state", stateString);
            context.sendBroadcast(intent);
            break;
        case 3:
            // 收到闹钟
            String notice_id = jsonObject.getString("notice_id");
            String owner_id = dataObj.getString("owner_id");
            String noticeContent = dataObj.getString("content");
            JSONObject noticeObj = new JSONObject(noticeContent);
            String isPrev = noticeObj.getString("isPrev");
            String time = noticeObj.getString("time");
            String title = noticeObj.getString("title");
            String repeatType = noticeObj.getString("type");
            String userNum = noticeObj.getString("userNum");
            String userNick = noticeObj.getString("userNick");
            String noticeString = noticeObj.getString("noticeContent");
            noticeString = AppUtil.toUTF8(noticeString);

            RemindEntity remindEntity = new RemindEntity("", AppConstant.USER_NUM, userNum, userNick, userNick,
                    AppUtil.getNowTime(), "", noticeString, time, time, title, repeatType, Integer.valueOf(isPrev),
                    notice_id, owner_id, RemindEntity.NOT_READ);
            remindEntity.setRemindState(RemindEntity.NEW);

            long remindId = remindDaoImpl.insertRemind(remindEntity);

            MessageEntity messageEntity = new MessageEntity("", "", "", userNick, userNum, AppUtil.getNowTime(),
                    MessageEntity.SEND_SUCCESS, MessageEntity.NORMAL, MessageEntity.TYPE_REMIND, String.valueOf(remindId),
                    "", userNum, MessageEntity.TYPE_RECIEVE, remindEntity.getContent(), MessageEntity.FEED_DEFAULT,
                    AppConstant.USER_NUM, notice_id);

            messageDaoImpl.insert(messageEntity);

            cursor = messageIndexDao.queryByNum(userNum);
            if (cursor.getCount() > 0) {
                MessageIndexEntity messageIndexEntitiy = DataBaseParser.getMessageIndex(cursor).get(0);
                messageIndexEntitiy.setTime(messageEntity.getTime());
                messageIndexEntitiy.setMessage(messageEntity.getContent());
                messageIndexDao.update(messageIndexEntitiy);
            } else {
                MessageIndexEntity messageIndexEntity = new MessageIndexEntity("", userNum, messageEntity.getContent(),
                        messageEntity.getTime(), userNick, "", 0, MessageIndexEntity.NORMAL,
                        MessageIndexEntity.SEND_SUCCESS, AppConstant.USER_NUM);
                messageIndexDao.insert(messageIndexEntity);
            }
            cursor.close();

            // 通知UI界面
            intent.setAction(GET_MESSAGE_ACTION);
            intent.putExtra("messageEntity", messageEntity);
            intent.putExtra("remindEntity", remindEntity);
            context.sendBroadcast(intent);
            // 发送notification
            if (!RemindApplication.IS_CHAT_VIEW_SHOW) {
                AppUtil.simpleNotify(context, userNum, 1, userNick, userNum, title, true);
            }
            break;
        case 4:
            // 收到是否同意闹钟
            String noticeId = dataObj.getString("notice_id");
            state = dataObj.getInt("state");
            if (state == 0) {
                // 拒绝
                state = RemindEntity.REFUSE;
            } else {
                state = RemindEntity.ACCEPT;
            }
            // 更新数据库
            remindDaoImpl.updateByNoticeId(noticeId, state);
            // 通知UI界面
            intent.setAction(NOTICE_STATE_ACTION);
            intent.putExtra("noticeId", noticeId);
            intent.putExtra("state", state);
            context.sendBroadcast(intent);
            // 发送notification
            // AppUtil.simpleNotify(context, Integer.valueOf(userNum), 1,
            // userNick, userNum, title, true);
            break;
        case 5:
            // 闹钟开始状态改变
            noticeId = dataObj.getString("notice_id");
            int statues = dataObj.getInt("status");

            // 更新数据库
            remindDaoImpl.updateByNoticeId(noticeId, statues);

            intent.setAction(NOTICE_STATE_ACTION);
            intent.putExtra("noticeId", noticeId);
            intent.putExtra("statues", statues);
            context.sendBroadcast(intent);
            break;

        default:
            break;
        }
    }

}
