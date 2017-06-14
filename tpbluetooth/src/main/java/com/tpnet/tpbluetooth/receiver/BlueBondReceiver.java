package com.tpnet.tpbluetooth.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tpnet.tpbluetooth.inter.BlueBondListener;

/**
 * 蓝牙配对 广播
 * Created by litp on 2017/6/1.
 */

public class BlueBondReceiver extends BroadcastReceiver {
    
    private BlueBondListener listener;


    public BlueBondReceiver(BlueBondListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //设备配对绑定状态回调
        BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (remoteDevice == null) {
            //showToast("no device");
            Log.e("@@", "绑定设备状态改变：设备为空");
            return;
        }

        int status = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0);

        
        switch (status){
            case BluetoothDevice.BOND_BONDED:
                //已经绑定了
                Log.e("@@", "已经绑定设备:" + remoteDevice.getAddress());

                if(listener != null){
                    listener.onBonded(remoteDevice);
                }
                
                break;
            case BluetoothDevice.BOND_BONDING:
                //绑定中
                Log.e("@@", "绑定设备中:" + remoteDevice.getAddress());

                if(listener != null){
                    listener.onBonding(remoteDevice);
                }
                
                break;
            case BluetoothDevice.BOND_NONE:
                //取消绑定
                Log.e("@@", "取消绑定设备:" + remoteDevice.getAddress());
                if(listener != null){
                    listener.onCancleBond(remoteDevice);
                }
                break;
        }
        
      
    }
    
}
