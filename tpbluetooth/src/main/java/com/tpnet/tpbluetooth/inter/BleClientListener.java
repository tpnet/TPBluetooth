package com.tpnet.tpbluetooth.inter;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.Map;

/**
 * 
 * Created by litp on 2017/6/5.
 */

public abstract class BleClientListener{

    public void onBleConnected(BluetoothGatt gatt, int status, int newState){};

    public void onBleConnecting(BluetoothGatt gatt, int status, int newState){};

    public void onBleDisconnected(BluetoothGatt gatt, int status, int newState){};

    public void onBleDisconnecting(BluetoothGatt gatt, int status, int newState){};

 

    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    }

    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

    }

    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

    }

    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

    }

    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
    }

    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
    }

    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {

    }


    /**
     * 发现了特征列表
     * @param characteristics
     */
    public   void onDiscoveringCharacteristics(List<BluetoothGattCharacteristic> characteristics){};

    /**
     * 发现服务列表
     * @param gatt
     */
    public   void onServicesDiscovered(BluetoothGatt gatt,Map<BluetoothGattService,List<BluetoothGattCharacteristic>> serviceListMap){};
}
