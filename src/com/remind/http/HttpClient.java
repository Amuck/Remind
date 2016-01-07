package com.remind.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.remind.util.Utils;

public class HttpClient {

//	curl -XPOST http://103.7.221.128:8008/user/register 
//	-d "{\"mobile\":\"13716022537\",
//	\"password\":\"123456\"," +
//			"\"nick\":\"123\"," +
//			"\"avatar\":\"http://sdfsdf.1.pig\"}" -v
	public static String ip = "101.200.200.49";
	public static String port = "8008";
	public static String url = "http://" + ip + ":" + port;
	
	public static String register = "/user/register";
	public static String create_notify = "/notice/create";
	public static String login = "/user/login";
	public static String friend = "/user/friend";
	
	public HttpClient() {
	}

	public static class UserInfo {
		public String mobile;
		public String password;
		public String nick;
		public String avatar;
	}
	
	public static class Friend {
		public String user_id;
		public String friend_id;
		public String state;
		public String friend_alias;
	}
	
	public static class Login {
		public String mobile;
		public String password;
	}
	
	/**
	 * 获取登陆的json数据
	 * @param mobile
	 * @param pwd
	 * @return
	 */
	public static Login getUserForLogin(String mobile, String pwd) {
		Login login = new Login();
		login.mobile = mobile;
		login.password = pwd;
		return login;
	}
	
	public static Login loginUser1() {
		Login login = new Login();
		login.mobile = "13716022537";
		login.password = "123456";
		return login;
	}
	
	public static Login loginUser2() {
		Login login = new Login();
		login.mobile = "13716022538";
		login.password = "123456";
		return login;
	}

	/**
	 * 获取注册用户需要的json
	 * @param mobile
	 * @param pwd
	 * @param nick
	 * @param avatar
	 * @return
	 */
	public static UserInfo getUserForReg(String mobile, String pwd, String nick, String avatar) {
		UserInfo userInfo = new UserInfo();
		userInfo.mobile = mobile;
		userInfo.password = pwd;
		userInfo.nick = nick;
		userInfo.avatar = avatar;
		return userInfo;
	}
	
	public static UserInfo getUser1() {
		UserInfo userInfo = new UserInfo();
		userInfo.mobile = "13716022537";
		userInfo.password = "123456";
		userInfo.nick = "123";
		userInfo.avatar = "http://sdfsdf.1.pig";
		return userInfo;
	}
	
	public static UserInfo getUser2() {
		UserInfo userInfo = new UserInfo();
		userInfo.mobile = "13716022538";
		userInfo.password = "123456";
		userInfo.nick = "123";
		userInfo.avatar = "http://sdfsdf.1.pig";
		return userInfo;
	}
	
	public static Friend friendUser1() {
		Friend friend = new Friend();
		friend.user_id = "13716022538";
		friend.friend_id = "13716022537";
		friend.state = "1";
		friend.friend_alias = "123";
		return friend;
	}
	
	public static Friend friendUser2() {
		Friend friend = new Friend();
		friend.user_id = "13716022537";
		friend.friend_id = "13716022538";
		friend.state = "1";
		friend.friend_alias = "123";
		return friend;
	}
	/**
	 * @return
	 */
	public static String getUserCreateJsonForPost(UserInfo userInfo) {
		String result = "";
		
		Gson gson = new Gson();
		result = gson.toJson(userInfo);
		return result;
	}
	
	/**
	 * @return		将对象转换为相应的json
	 */
	public static String getJsonForPost(Object object) {
		String result = "";
		
		Gson gson = new Gson();
		result = gson.toJson(object);
		return result;
	}
	
	static class Notify {
		public String user_id;
		public Content content;
		public String owner_id;
		
		@Override
		public String toString() {
			String result = "{\"user_id\":\"" + this.user_id + "\"," +
					"\"content\":" + this.content.toString() + "," +
					"\"owner_id\":\"" + this.owner_id + "\"}";
			return result;
		}
	}
	
	static class Content {
		public String title;
		public String type;
		public String time;
		public String isPrev;
		
		@Override
		public String toString() {
			String result = "[{\"title\":\"" + this.title + "\"}," +
					"{\"type\":\"" + this.type + "\"}," +
					"{\"time\":\"" + this.time + "\"}," +
//					"{\"isPrev\":" + this.isPrev + "}," +
//					"{\"time\":" + this.title + "}," +
					"{\"isPrev\":\"" + this.isPrev + "\"}]";
			
			return result;
		}
	}
	
	/**
	 * @return
	 */
	public static String getCreateNofiJsonForPost() {
		Gson gson = new Gson();
		String result = "";
		Notify notify = new Notify();
		notify.user_id = "13716022537";
		notify.owner_id = "123456";
		Content content = new Content();
		content.title = "nihao";
		content.isPrev = "1";
		content.time = "2015-05-50 10:01:22";
		content.type = "2";
		notify.content = content;
//		JSONArray jsonObject = new JSONArray();
//		jsonObject.
		result = gson.toJson(notify);
		return notify.toString();
	}
	
	
	
	/*
	 * post请求， 
	 */
	public static String post(String httpUrl, String jsonString)
	{
		httpUrl = Utils.encodeGB(httpUrl);
		System.out.println("post:"+httpUrl);
		InputStream input = null;//输入流
		InputStreamReader isr = null;
		BufferedReader buffer = null;
		StringBuffer sb = new StringBuffer();
		String line = null;
		AssetManager am = null;//资源文件的管理工具类
		try {
			/*post向服务器请求数据*/
			HttpPost request = new HttpPost(httpUrl);
			StringEntity se = new StringEntity(jsonString);
			request.setEntity(se);
			HttpResponse response = new DefaultHttpClient().execute(request);
			int code = response.getStatusLine().getStatusCode();
			System.out.println("postCode= " + code);
			// 若状态值为200，则ok
			if (code == HttpStatus.SC_OK) {
				//从服务器获得输入流
				input = response.getEntity().getContent();
				isr = new InputStreamReader(input);
				buffer = new BufferedReader(isr,10*1024);
				
				while ((line = buffer.readLine()) != null) {
					sb.append(line);
				}
			} 
				
		} catch (Exception e) {
			//其他异常同样读取assets目录中的"local_stream.xml"文件
			System.out.println("HttpClient post 数据异常");
			e.printStackTrace();
			return null;
		} finally {
			try {
				if(buffer != null) {
					buffer.close();
					buffer = null;
				}
				if(isr != null) {
					isr.close();
					isr = null; 
				}
				if(input != null) {
					input.close();
					input = null;
				}
			} catch (Exception e) {
				}
		}
//		System.out.println("PostData:"+sb.toString());
		return sb.toString();
	}
}
