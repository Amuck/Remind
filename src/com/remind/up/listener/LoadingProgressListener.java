package com.remind.up.listener;

public interface LoadingProgressListener {
	void onProgress(int bytesWritten, int totalSize);
}
