package com.remind.sevice;

interface IBackService{
	boolean sendMessage(String message);
	void release();
}