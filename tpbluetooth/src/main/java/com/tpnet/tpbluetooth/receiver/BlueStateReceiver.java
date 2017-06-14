package com.tpnet.tpbluetooth.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tpnet.tpbluetooth.inter.BlueStateListener;

/**
 * Created by litp on 2017/6/1.
 */

public class BlueStateReceiver extends BroadcastReceiver {
    
    
    private BlueStateListener listener;

    public BlueStateReceiver(BlueStateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

        switch (state) {
            case BluetoothAdapter.STATE_ON:
                Log.e("@@", "蓝牙开启了");
                if(listener != null){
                    listener.onOpen();
                }
                break;
            case BluetoothAdapter.STATE_OFF:
                Log.e("@@", "蓝牙关闭了");
                if(listener != null){
                    listener.onClose();
                }
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                Log.e("@@", "蓝牙正在开启中。。。");
                if(listener != null){
                    listener.onOpening();
                }

                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                Log.e("@@", "蓝牙正在关闭中。。。");
                if(listener != null){
                    listener.onClosing();
                }
                break;
            case BluetoothAdapter.STATE_CONNECTED:
                Log.e("@@", "蓝牙已经链接上设备");
                if(listener != null){
                    listener.onConnected();
                }

                break;
            case BluetoothAdapter.STATE_CONNECTING:
                Log.e("@@", "蓝牙正在链接设备中。。。");
                if(listener != null){
                    listener.onConnecting();
                }
                break;
            case BluetoothAdapter.STATE_DISCONNECTED:
                Log.e("@@", "蓝牙断开链接了");
                if(listener != null){
                    listener.onDisconnected();
                }
                break;
            case BluetoothAdapter.STATE_DISCONNECTING:
                Log.e("@@", "蓝牙正在断开链接中。。。");
                if(listener != null){
                    listener.onDisconnecting();
                }
                break;

        }
    }
    
}
