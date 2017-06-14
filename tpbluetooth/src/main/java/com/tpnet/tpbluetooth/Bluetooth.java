package com.tpnet.tpbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;

import com.tpnet.tpbluetooth.inter.BleFindListener;
import com.tpnet.tpbluetooth.inter.BlueBondListener;
import com.tpnet.tpbluetooth.inter.BlueFindListener;
import com.tpnet.tpbluetooth.inter.BlueStateListener;
import com.tpnet.tpbluetooth.receiver.BlueBondReceiver;
import com.tpnet.tpbluetooth.receiver.BlueFindReceiver;
import com.tpnet.tpbluetooth.receiver.BlueStateReceiver;

/**
 * 
 * Created by Litp on 2017/6/3.
 */

public class Bluetooth {

    BlueStateReceiver stateReceiver;
    BlueBondReceiver bondReceiver;
    BlueFindReceiver findReceiver;


    //上下文 用来监听广播
    Context mContext;

    BleFindListener mBleFindListener;    //ble搜索回调监听器
     
    BlueFindListener mFindListener;      //蓝牙扫描监听器

    public Bluetooth(Context context) {
        this.mContext = context;

    }

    public BleFindListener getBleFindListener(){
        return mBleFindListener;
    }
 
 


    public void setOnBlueStateListener(BlueStateListener listener){
        stateReceiver = new BlueStateReceiver(listener);
        mContext.registerReceiver(stateReceiver,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

    }

    public void setOnBlueBondListener(BlueBondListener listener){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);  //绑定状态改变
        bondReceiver = new BlueBondReceiver(listener);
        mContext.registerReceiver(bondReceiver,intentFilter);
    }
    public void setOnBlueFindListener(BlueFindListener listener){
        this.mFindListener = listener;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);  //设备扫描模式改变
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);  //开始搜索
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  //结束搜索
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);  //搜索到设备
        findReceiver = new BlueFindReceiver(listener);
        mContext.registerReceiver(findReceiver,intentFilter);

    }


    public void setOnBleFindListener(BleFindListener bleFindListener) {
        this.mBleFindListener =  bleFindListener;  
    }

    /**
     * 释放资源
     */
    public void release() {
        if(stateReceiver != null){
            mContext.unregisterReceiver(stateReceiver);
        }
        if(findReceiver != null){
            mContext.unregisterReceiver(findReceiver);
        }
        if(bondReceiver != null){
            mContext.unregisterReceiver(bondReceiver);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

}
