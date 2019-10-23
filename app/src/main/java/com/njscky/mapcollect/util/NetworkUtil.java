package com.njscky.mapcollect.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    private NetworkUtil() {
    }

    public static boolean hasNetwork(Context context) {
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        networkInfo = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return networkInfo != null && networkInfo.isConnected();
    }
}
