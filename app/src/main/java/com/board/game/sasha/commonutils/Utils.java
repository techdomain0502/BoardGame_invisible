package com.board.game.sasha.commonutils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by sachin on 8/12/2015.
 */
public class Utils {
    public static boolean isWifiorData_connected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mData = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(mWifi.isConnected() || mData.isConnected())
            return true;

        return false;
    }

    public static boolean isNullorWhiteSpace(String str){
        return (str==null||str.isEmpty())?true:false;
    }


}
