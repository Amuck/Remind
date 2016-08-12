package com.remind.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtil {

    public static boolean isAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info == null || !info.isConnected() || info.isRoaming())
            return false;

        return true;
    }

}
