package com.remind.up;

import java.io.File;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.remind.up.listener.CompleteListener;
import com.remind.up.listener.ProgressListener;
import com.remind.up.main.UploaderManager;
import com.remind.up.utils.UpYunUtils;

/**
 * @author ChenLong
 *
 *	上传功能类
 */
public class Upload {
	// 空间名
	public final static String bucket = "remind";
	// 表单密钥
	public final static String formApiSecret = "ELvSReGsZzgsANYPRaHnTwAtTOQ=";
	// 本地文件路径
	public static  String localFilePath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + File.separator;
	// 上传文件名
	public static String filename = "test.txt";
	// 保存到又拍云的路径
	public static String savePath = "/" + filename;
	
	public static Context context;
	
	public static ProgressListener progressListener;
	
	public static CompleteListener completeListener;
	
	/**
	 * 上传文件
	 * 
	 * @param context
	 * @param fileName				文件名称
	 * @param completeListener		完成上传时的回调
	 * @param progressListener		过程中的回调，注意：由于在计算发送的字节数中包含了图片以外的其他信息，最终上传的大小总是大于图片实际大小，
	 * 								为了解决这个问题，代码会判断如果实际传送的大小大于图片，就将实际传送的大小设置成'fileSize-1000'（最小为0）
	 */
	public static void upload(Context context, String fileName, CompleteListener completeListener, ProgressListener progressListener) {
		Upload.context = context;
		if (TextUtils.isEmpty(fileName)) {
			Toast.makeText(context, "文件无效，请重新选择", Toast.LENGTH_LONG).show();
			return;
		}
		
		Upload.progressListener = progressListener;
		Upload.completeListener = completeListener;
		Upload.filename = fileName;
		savePath = "/" + filename;
		new UploadTask().execute();
	}
	
	static class UploadTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			File localFile = new File(localFilePath + filename);
			try {
				/*
				 * 设置进度条回掉函数
				 * 
				 * 注意：由于在计算发送的字节数中包含了图片以外的其他信息，最终上传的大小总是大于图片实际大小，
				 * 为了解决这个问题，代码会判断如果实际传送的大小大于图片
				 * ，就将实际传送的大小设置成'fileSize-1000'（最小为0）
				 */
//				ProgressListener progressListener = new ProgressListener() {
//					@Override
//					public void transferred(long transferedBytes, long totalBytes) {
//						// do something...
////						System.out.println("trans:" + transferedBytes + "; total:" + totalBytes);
//						Log.e("Upload_ProgressListener", "trans:" + transferedBytes + "; total:" + totalBytes);
//					}
//				};
//				
//				CompleteListener completeListener = new CompleteListener() {
//					@Override
//					public void result(boolean isComplete, String result, String error) {
//						// do something...
////						System.out.println("isComplete:"+isComplete+";result:"+result+";error:"+error);
//						Log.e("Upload_CompleteListener", "isComplete:"+isComplete+";result:"+result+";error:"+error);
//					}
//				};
				
				UploaderManager uploaderManager = UploaderManager.getInstance(bucket);
				uploaderManager.setConnectTimeout(60);
				uploaderManager.setResponseTimeout(60);
				Map<String, Object> paramsMap = uploaderManager.fetchFileInfoDictionaryWith(localFile, savePath);
				//还可以加上其他的额外处理参数...
//				paramsMap.put("return_url", "http://httpbin.org/get");
				// signature & policy 建议从服务端获取
				String policyForInitial = UpYunUtils.getPolicy(paramsMap);
				String signatureForInitial = UpYunUtils.getSignature(paramsMap, formApiSecret);
				uploaderManager.upload(policyForInitial, signatureForInitial, localFile, progressListener, completeListener);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
//			if (result != null) {
//				Toast.makeText(context, "成功", Toast.LENGTH_LONG).show();
//			} else {
//				Toast.makeText(context, "失败", Toast.LENGTH_LONG).show();
//			}
		}
	}
}
