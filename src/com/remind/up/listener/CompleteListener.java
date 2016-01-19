package com.remind.up.listener;

public interface CompleteListener {
	/**
	 * 完成时的回调
	 * 
	 * @param isComplete		true/false 成功失败
	 * @param result			json
	 * 							成功：
	 *							{
	 *							
	 *							    "code":200,
	 *							    "last_modified":1418394078,
	 *							    "mimetype":"image\/jpeg",
	 *							    "file_size":"156881",
	 *							    "image_frames":1,
	 *							    "bucket_name":"picture-test-space",
	 *							    "image_width":1280,
	 *							    "path":"\/test11.png",
	 *							    "image_height":822,
	 *							    "signature":"07a061373d6653ae0439a924e8366d48"
	 *							}
	 *							失败：
	 *							{
	 *							    "code":404,
	 *							    "message":"Bucket NotFound.",
	 *							    "X-Request-Id":"235dd1a77bccac5363cbf0157c037ffa", //提供又拍云该id，可以更好的排查错误
	 *							    "error_code":40401
	 *							}
	 * @param error
	 */
	void result(boolean isComplete, String result, String error);
}
