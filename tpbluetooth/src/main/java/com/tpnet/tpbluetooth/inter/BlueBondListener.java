package com.tpnet.tpbluetooth.inter;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙状态监听器
 * Created by litp on 2017/6/1.
 */

public abstract class BlueBondListener {


    public void onBonded(BluetoothDevice device){};

    public void onBonding(BluetoothDevice device){};

    public void onCancleBond(BluetoothDevice device){};
 
}
