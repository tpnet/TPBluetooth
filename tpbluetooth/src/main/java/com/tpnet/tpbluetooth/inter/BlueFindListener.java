package com.tpnet.tpbluetooth.inter;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙扫描监听器
 * Created by litp on 2017/6/1.
 */

public abstract class BlueFindListener {


    public void onStartDiscovery(){};

    public void onFinishDiscovery(){};
    
    
    public void onFound(BluetoothDevice device){};
    
    
    

    //5.0+的BLE设备搜索

    /**
     * 
     * @param device
     * @param callbackType 开始搜索时候的ScanSettings的setCallbackType
     */
    public void onFound(BluetoothDevice device,int callbackType){};

  

    /**
     * 4.3+,5.0-的ble设备搜素
     * @param device 搜索到的设备
     * @param rssi      代表设备的信号强度，是负的，数值越大代表信号强度越大
     * @param scanRecord  设备广播的相关数据（例如依靠这个广播来判断我们设备时候在充电状态的，各个设备应该都有自己的商定）
     */
    public void onFound(BluetoothDevice device,int rssi, byte[] scanRecord){};


    //public void onBleFoundTimeout(){};
     
    
    public void onModeConnectableDiscoverable(){};


    public void onModeConnectable(){};


    public void onModeClose(){};

 

}
