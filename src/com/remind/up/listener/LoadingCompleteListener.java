package com.remind.up.listener;

public interface LoadingCompleteListener {
	void result(boolean isSuccess, String response, String error);
}
