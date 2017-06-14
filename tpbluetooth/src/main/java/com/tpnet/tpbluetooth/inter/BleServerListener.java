package com.tpnet.tpbluetooth.inter;

import android.annotation.TargetApi;
import android.bluetooth.le.AdvertiseSettings;
import android.os.Build;

/**
 * Created by litp on 2017/6/9.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public abstract class BleServerListener{


    public void onTimeout(long timeout){}
    
    
   
    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
       
    }

    public void onStartFailure(int errorCode) {
     
    }
    
    
    public void onStop(){};
}
