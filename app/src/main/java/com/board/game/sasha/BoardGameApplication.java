package com.board.game.sasha;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.board.game.sasha.commonutils.GlobalConstants;

/**
 * Created by sachin on 9/5/2015.
 */
public class BoardGameApplication extends Application {
    private SharedPreferences.Editor pref_editor;

    @Override
    public void onCreate() {
        super.onCreate();
        setVersion();
     }

    private void setVersion() {
        String appVersionName = "0";
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            appVersionName = packageInfo.versionName;
            pref_editor = getApplicationContext().getSharedPreferences(GlobalConstants.pref_file, Context.MODE_PRIVATE).edit();
            pref_editor.putString("version",appVersionName);
            pref_editor.commit();
        } catch (PackageManager.NameNotFoundException ex) {
            pref_editor = getApplicationContext().getSharedPreferences(GlobalConstants.pref_file, Context.MODE_PRIVATE).edit();
            pref_editor.putString("version","1.0");
            pref_editor.commit();
        }

    }


}

