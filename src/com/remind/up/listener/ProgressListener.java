package com.remind.up.listener;

public interface ProgressListener {
    void transferred(long transferedBytes, long totalBytes);
}
