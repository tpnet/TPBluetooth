package com.tpnet.tpbluetooth.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tpnet.tpbluetooth.inter.BlueFindListener;


/**
 * 蓝牙搜索广播
 * Created by litp on 2017/6/1.
 */

public class BlueFindReceiver extends BroadcastReceiver {
    
    
    private BlueFindListener listener;

    public BlueFindReceiver(BlueFindListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:

                //开始查找设备
                Log.e("@@", "开始查找设备");
                if(listener != null){
                    listener.onStartDiscovery();
                }
               
                break;

            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                //查找完毕
                Log.e("@@", "查找设备完成");
                if(listener != null){
                    listener.onFinishDiscovery();
                }
                break;
            case BluetoothDevice.ACTION_FOUND:
                //找到设备
                //每查找到一个设备就会接收到一个广播

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e("@@","找到设备了："+(device == null));
                
                if(listener != null){
                    listener.onFound(device);
                }
                break;
            case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:

                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0);
                Log.e("@@", "扫描模式改变" + scanMode);

                if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    // 处于蓝牙可见、可链接模式
                    if(listener != null){
                        listener.onModeConnectableDiscoverable();
                    }
                    
                } else if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE) {
                    // 蓝牙不可见 但能接收连接
                    if(listener != null){
                        listener.onModeConnectable();
                    }
                    
                } else if (scanMode == BluetoothAdapter.SCAN_MODE_NONE) {
                    //蓝牙不可见 且 无法接收连接
                    if(listener != null){
                        listener.onModeClose();
                    }
                }
            
        }
    }
    
}
