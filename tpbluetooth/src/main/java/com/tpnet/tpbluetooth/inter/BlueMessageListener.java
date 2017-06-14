package com.tpnet.tpbluetooth.inter;

import android.bluetooth.BluetoothDevice;

/**
 * 链接服务相关的回调接口
 * Created by litp on 2017/6/1.
 */

public interface BlueMessageListener {
 
    void onReceiveMessage(BluetoothDevice device,String mess);
    
}
