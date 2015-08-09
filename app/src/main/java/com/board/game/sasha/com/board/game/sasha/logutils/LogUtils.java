package com.board.game.sasha.com.board.game.sasha.logutils;

import android.support.v4.BuildConfig;
import android.util.Log;

/**
 * Created by sachin on 8/9/2015.
 */
public class LogUtils {
    public static void LOGD(String TAG,String Msg){
        if(BuildConfig.DEBUG)
            Log.d(TAG,Msg);
    }

    public static void LOGV(String TAG,String Msg){
        if(BuildConfig.DEBUG)
            Log.v(TAG,Msg);
    }
    public static void LOGE(String TAG,String Msg){
        if(BuildConfig.DEBUG)
            Log.e(TAG,Msg);
    }


}
