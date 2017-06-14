package com.tpnet.bluedemo;

import android.app.Application;
import android.content.pm.PackageManager;

import com.tpnet.tpbluetooth.TPBluetooth;


/**
 * Created by litp on 2017/6/1.
 */

public class BaseApplication extends Application {

    private static BaseApplication application;

    public static BaseApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        
        //初始化蓝牙控制器
        TPBluetooth.init(this);
    }
    
   
    //获取版本号
    public static int getVersionCode() {
        try {
            
            return application.getPackageManager().getPackageInfo(application.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }
    
}