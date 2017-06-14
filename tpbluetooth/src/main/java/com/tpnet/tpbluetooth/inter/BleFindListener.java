package com.tpnet.tpbluetooth.inter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;

import com.tpnet.tpbluetooth.BlueLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Litp on 2017/6/3.
 */

 
@SuppressLint("NewApi")
public abstract class BleFindListener extends ScanCallback implements BluetoothAdapter.LeScanCallback {

    public final static int DEFAULT_CALLBACKTYPE = 0X1111;
    
 
    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);
        //
        BlueLog.e("onScanResult");
    }

    @Override
    public final void onBatchScanResults(List<ScanResult> results) {
        super.onBatchScanResults(results);
        for(ScanResult result:results){
            onScanResult(DEFAULT_CALLBACKTYPE,result);
        }
    }

    @Override
    public void onScanFailed(int errorCode) {
        super.onScanFailed(errorCode);
        
    }


    
    public void onScanTimeout(){}


    @Override
    public final void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

        ScanRecord record = null;
        
        //通过反射获得
        try {
            Method method = ScanRecord.class.getMethod("parseFromBytes",byte[].class);
            method.setAccessible(true);
            record = (ScanRecord) method.invoke(ScanRecord.class,scanRecord);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        
        ScanResult result = new ScanResult(device,record,rssi,System.currentTimeMillis());
        onScanResult(DEFAULT_CALLBACKTYPE,result);
        
    }

    
    


}
