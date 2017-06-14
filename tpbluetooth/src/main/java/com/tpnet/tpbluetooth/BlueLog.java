package com.tpnet.tpbluetooth;

import android.util.Log;

/**
 * Created by litp on 2017/6/5.
 */

public class BlueLog  {
    
    
    
    public static final String TAG = "@@tpbluetooth";
    
    
    public static boolean isPrint  = BuildConfig.DEBUG;
    
    public static void e(String text){
         
        if(!isPrint){
            Log.e(TAG,text);
        }
    }
}
