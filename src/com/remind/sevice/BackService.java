package com.remind.sevice;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.remind.http.HttpClient;
import com.remind.util.ByteUtil;

public class BackService extends Service {
	private static final String TAG = "BackService";
	private static final long HEART_BEAT_RATE = 20 * 1000;

	public static final String HOST = "101.200.200.49";//"172.22.51.98";// "192.168.1.21";//
	public static final int PORT = 80;//9800;
	
	public static final String MESSAGE_ACTION="org.feng.message_ACTION";
	public static final String HEART_BEAT_ACTION="org.feng.heart_beat_ACTION";
	public static final String HEART_BEAT_MSG="{}";
	
	private ReadThread mReadThread;

	private LocalBroadcastManager mLocalBroadcastManager;

//	private WeakReference<Socket> mSocket;
	private Socket msocket;

	// For heart Beat
	private Handler mHandler = new Handler();
	private Runnable heartBeatRunnable = new Runnable() {

		@Override
		public void run() {
			if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
				boolean isSuccess = sendMsg(HEART_BEAT_MSG);//就发送一个\r\n过去 如果发送失败，就重新初始化一个socket
//				isClose();
				if (!isSuccess) {
					mHandler.removeCallbacks(heartBeatRunnable);
					mReadThread.release();
					releaseLastSocket(msocket);
					new InitSocketThread().start();
				} else {
					Log.e(TAG, "bit success");
//					UIHelper.toastAsync(BackService.this, "bit success");
				}
			}
			mHandler.postDelayed(this, HEART_BEAT_RATE);
		}
	};

	private long sendTime = 0L;
	private IBackService.Stub iBackService = new IBackService.Stub() {

		@Override
		public boolean sendMessage(String message) throws RemoteException {
			return sendMsg(message);
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return iBackService;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		new InitSocketThread().start();
		mLocalBroadcastManager=LocalBroadcastManager.getInstance(this);
	}
	
	public void isClose() {
		if (null == msocket) {
			return ;
		}
		Socket soc = msocket;
		try {

			soc.sendUrgentData(0xFF);

		} catch (IOException e) {
			Log.e(TAG, e + ":要关掉了阿 ！");
			try {
				soc.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}
	
	public DataOutputStream out = null;
	
	public DataOutputStream getOutPutStrem(Socket socket) throws IOException {
        if (out == null) {
            out = new DataOutputStream(socket.getOutputStream());
        }
        return out;
    }
	
	public boolean sendMsg(String msg) {
		if (null == msocket) {
			return false;
		}
		Socket soc = msocket;
		try {
			if (!soc.isClosed() && !soc.isOutputShutdown()) {
//				out = getOutPutStrem(soc);
				OutputStream out = soc.getOutputStream();
//				String message = msg + "\r\n";
				String message = msg;
				out.write(ByteUtil.toBytes(message.getBytes("UTF-8"), 1));
//				if ("{}".equals(msg)) {
////					message = msg;
//					out.write(ByteUtil.toBytes(msg.getBytes("UTF-8"), 1));
//				} else {
//					JSONObject jsonObject = new JSONObject();
//					jsonObject.put("content", msg);
//					msg = jsonObject.toString();
//					out.write(ByteUtil.toBytes(msg.getBytes("UTF-8"), 1));
//				}
//				byte[] bs = message.getBytes("utf-8");
//				byte[] temp = getAllByte(bs);
//				int i = 0;
//				i = bs.length;
//				soc.setSendBufferSize(i);
//				String len = "{packet, "+i+"}";
//				os.write(len.getBytes("utf-8"));
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				out.write(bs);
				
				out.flush();
				sendTime = System.currentTimeMillis();//每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public byte[] getAllByte(byte[] bs) {
		byte[] res = new byte[bs.length * 8];
		for (int i = 0; i < bs.length; i++) {
			byte[] temp = get01Seq(bs[i]);
			for (int j = 0; j < temp.length; j++) {
				res[i * 8 + j] = temp[j];
			}
		}
		return res;
	}
	
	public byte[] get01Seq(byte b) {
		byte[] res = new byte[8];
		for (int i = 0; i < 8; i++) {
			res[i] = (byte) (((1 << (7 - i) & b) == 0) ? 0 : 1);
		}
		return res;
	}

	private void initSocket() {//初始化Socket
		try {
			msocket = new Socket(HOST, PORT);
//			Socket so = new Socket();
//			SocketAddress address = new InetSocketAddress(HOST, PORT);
//			so.connect(address, 8000);
//			so.setKeepAlive(true);
//			mSocket = new WeakReference<Socket>(so);
			mReadThread = new ReadThread(msocket);
			mReadThread.start();
			mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//初始化成功后，就准备发送心跳包
//			boolean isSuccess = sendMsg("{}");
////			isClose();
//			if (!isSuccess) {
//			} else {
//				Log.e(TAG, "bit success");
//			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void releaseLastSocket(Socket mSocket) {
		try {
			if (null != mSocket) {
				Socket sk = mSocket;
				if (!sk.isClosed()) {
					sk.close();
				}
				sk = null;
				mSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
//		try {
//			if (null != mSocket) {
//				Socket sk = mSocket.get();
//				if (!sk.isClosed()) {
//					sk.close();
//				}
//				sk = null;
//				mSocket = null;
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	class InitSocketThread extends Thread {
		@Override
		public void run() {
			super.run();
			initSocket();
		}
	}

	// Thread to read content from Socket
	class ReadThread extends Thread {
		private Socket mWeakSocket;
//		private WeakReference<Socket> mWeakSocket;
		private boolean isStart = true;

		public ReadThread(Socket socket) {
			mWeakSocket = socket;
		}

		public void release() {
			isStart = false;
			releaseLastSocket(msocket);
		}

		@Override
		public void run() {
			super.run();
			Socket socket = mWeakSocket;
			if (null != socket) {
				try {
					InputStream is = socket.getInputStream();
					byte[] buffer = new byte[1024 * 4];
					int length = 0;
					while (!socket.isClosed() && !socket.isInputShutdown()
							&& isStart && ((length = is.read(buffer)) != -1)) {
						if (length > 0) {
							String message = new String(Arrays.copyOf(buffer,
									length)).trim();
							Log.e(TAG, message);
							//收到服务器过来的消息，就通过Broadcast发送出去
							if(message.equals(HEART_BEAT_MSG)){//处理心跳回复
								Intent intent=new Intent(HEART_BEAT_ACTION);
								mLocalBroadcastManager.sendBroadcast(intent);
							}else{
								//其他消息回复
								Intent intent=new Intent(MESSAGE_ACTION);
								intent.putExtra("message", message);
								mLocalBroadcastManager.sendBroadcast(intent);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Thread to read content from Socket
//		class ReadThread extends Thread {
//			private Socket mWeakSocket;
////			private WeakReference<Socket> mWeakSocket;
//			private boolean isStart = true;
//
//			public ReadThread(Socket socket) {
//				mWeakSocket = socket;
//			}
//
//			public void release() {
//				isStart = false;
//				releaseLastSocket(mWeakSocket);
//			}
//
//			@Override
//			public void run() {
//				super.run();
//				Socket socket = mWeakSocket;
//				if (null != socket) {
//					try {
//						InputStream is = socket.getInputStream();
//						byte[] buffer = new byte[1024 * 4];
//						int length = 0;
//						while (!socket.isClosed() && !socket.isInputShutdown()
//								&& isStart && ((length = is.read(buffer)) != -1)) {
//							if (length > 0) {
//								String message = new String(Arrays.copyOf(buffer,
//										length)).trim();
//								Log.e(TAG, message);
//								//收到服务器过来的消息，就通过Broadcast发送出去
//								if(message.equals(HEART_BEAT_MSG)){//处理心跳回复
//									Intent intent=new Intent(HEART_BEAT_ACTION);
//									mLocalBroadcastManager.sendBroadcast(intent);
//								}else{
//									//其他消息回复
//									Intent intent=new Intent(MESSAGE_ACTION);
//									intent.putExtra("message", message);
//									mLocalBroadcastManager.sendBroadcast(intent);
//								}
//							}
//						}
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
}
