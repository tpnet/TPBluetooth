package com.tpnet.tpbluetooth.inter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

/**
 * 链接服务相关的回调接口
 * Created by litp on 2017/6/1.
 */

 
@SuppressLint("NewApi")
public abstract class BlueServerListener {
    
    public void onStartListener(){};
    
    public void onListenering(){};
    
    public void onFinishListener(){};
    
    public void onServerError(Exception e){};
    
    public void onGetClient(BluetoothDevice text){};

    public void onRemoveClient(BluetoothDevice device){};

    public void onCloseClient(BluetoothDevice device){};

}
