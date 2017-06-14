package com.tpnet.tpbluetooth;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.tpnet.tpbluetooth.inter.BleClientListener;
import com.tpnet.tpbluetooth.inter.BleServerListener;
import com.tpnet.tpbluetooth.inter.BlueClientListener;
import com.tpnet.tpbluetooth.inter.BlueMessageListener;
import com.tpnet.tpbluetooth.inter.BlueServerListener;
import com.tpnet.tpbluetooth.inter.connect.Constant;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 基础蓝牙控制器
 * Created by litp on 2017/5/25.
 */

public class TPBluetooth extends Bluetooth {

    private static TPBluetooth INSTANCE;

    private BluetoothAdapter mBluetoothAdapter;  //本地蓝牙

    private BluetoothLeScanner mBlueToothScanner;  //Ble蓝牙扫描


    //开启蓝牙回调code
    public final static int REQUEST_CODE_OPEN_BLUETOOTH = 1;

    public final static int DEFAULT_STOP_DELAY_SECOND = 30;  //ble每次查询设备时间默认为30s

    //蓝牙服务器
    private BluetoothServer mBluetoothServer;
    
    
    //传统和ble的蓝牙控制器
    private BluetoothControl mControl;  //控制器


    private TPBluetooth(Context context) {
        super(context);

    }


    public static TPBluetooth getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("蓝牙控制器还没进行初始化");
        }
        return INSTANCE;

    }

    public static void init(Context context) {
        //初始化
        INSTANCE = new TPBluetooth(context);
    }

    public boolean initClassicBluetooth() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothServer = new BluetoothServer(mBluetoothAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBlueToothScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }
        return isSupportBlueTooth();
    }

 
    @SuppressLint("NewApi")
    public boolean initBle() {
       
        mBluetoothAdapter = ((BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        mBluetoothServer = new BluetoothServer(mBluetoothAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBlueToothScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }

        return isSupportBle();

    }

    private void isInit() {
        if (mBluetoothAdapter == null) {
            throw new RuntimeException("还没有进行传统或者ble的蓝牙初始化,请先调用initClassicBluetooth或者initBle进行初始化");
        }
    }

    /**
     * 当前设备是否支持蓝牙
     *
     * @return
     */
    public boolean isSupportBlueTooth() {

        return BluetoothAdapter.getDefaultAdapter() != null;
    }


    /**
     * 使用此检查确定BLE是否支持在设备上，可以选择性禁用BLE相关的功能
     *
     * @return
     */
    public boolean isSupportBle() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * 判断是否支持设备作为外围设备
     *
     * @return
     */

    public boolean isSupportBlePeripheral() {
        if (!isBluetoothEnable()) {
            throw new RuntimeException("还没开启蓝牙");
        }


        if (mBluetoothServer != null) {
            return mBluetoothServer.getBluetoothLeAdvertiser() != null;
        }

        return false;


    }

    /**
     * 获取当前蓝牙状态
     *
     * @return
     */
    public boolean isBluetoothEnable() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    private void checkBluetooth() {
        isInit();
        if (!mBluetoothAdapter.isEnabled()) {
            throw new RuntimeException("蓝牙还没开启");
        }
    }


    /**
     * 关闭蓝牙,广播会接收到
     */
    public boolean closeBlueTooth() {

        return isBluetoothEnable() && BluetoothAdapter.getDefaultAdapter().disable();
    }

    /**
     * 打开蓝牙,广播会接收到
     *
     * @param context
     */
    public void openBlueTooth(Activity context) {
        if (!isBluetoothEnable()) {
            //谷歌推荐的利用intent去打开蓝牙
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivityForResult(intent, REQUEST_CODE_OPEN_BLUETOOTH);

            //下面的方式不推荐
            //mBlueToothAdapter.enable();
        }

    }


    /**
     * 打开或关闭蓝牙可见性,启用可检测性将会自动启用蓝牙
     *
     * @param context
     */
    public void setBlueToothVisiable(Context context, boolean isShow) {

        checkBluetooth();

        if (isShow) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

            //可见100秒，默认是120秒，最大持续时间为 3600 秒，值为 0 则表示设备始终可检测到
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 100);

            context.startActivity(intent);
        } else {
            closeBluetoothDiscoverable();
        }


    }

    /**
     * 关闭蓝牙可见性，广播接收结果
     */
    public void closeBluetoothDiscoverable() {
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode = BluetoothAdapter.class.getMethod("setScanMode", int.class, int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(mBluetoothAdapter, 1);
            setScanMode.invoke(mBluetoothAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 当前蓝牙是否可见
     *
     * @return
     */
    public boolean isBluetoothVisiable() {
        isInit();
        return mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
    }


    /**
     * 开始查找设备，一般是12秒,异步线程，回调广播
     *
     * @return 是否开启了扫描
     */
    public boolean startFindDevices() {

        checkBluetooth();

        return mBluetoothAdapter.startDiscovery();
    }


    /**
     * 搜索全部peripheral设备
     */
    public void startFindBleDevice() {
        startFindBleDevice(new ArrayList<ScanFilter>());
    }

    public void startFindBleDevice(List<ScanFilter> filters) {
        startFindBleDevice(filters, null);
    }


    public void startFindBleDevice(List<ScanFilter> filters, ScanSettings settings) {
        startFindBleDevice(filters, settings, DEFAULT_STOP_DELAY_SECOND);
    }

    public void startFindBleDevice(List<ScanFilter> filters, ScanSettings settings, int stopDelaySecond) {
        startFindBleDevice(filters, settings, null, stopDelaySecond);
    }

    /**
     * 扫描指定类型的设备，Android4.3+，5.0-
     *
     * @param serviceUuids
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startFindBleDevice(UUID[] serviceUuids) {
        startFindBleDevice(serviceUuids, DEFAULT_STOP_DELAY_SECOND);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startFindBleDevice(UUID[] serviceUuids, int stopDelaySecond) {
        startFindBleDevice(null, null, serviceUuids, DEFAULT_STOP_DELAY_SECOND);
    }

    private void startFindBleDevice(List<ScanFilter> filters, ScanSettings settings, UUID[] serviceUuids, int stopDelaySecond) {

        checkBluetooth();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Android5.0+
            if (filters != null) {

                if (settings != null) {
                    mBlueToothScanner.startScan(filters, settings, getBleFindListener());
                } else {
                    mBlueToothScanner.startScan(filters, new ScanSettings.Builder().build(), getBleFindListener());
                }

            } else {

                if (settings != null) {
                    mBlueToothScanner.startScan(null, settings, getBleFindListener());
                } else {
                    mBlueToothScanner.startScan(getBleFindListener());
                }

            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //Android4.3+
            if (serviceUuids != null) {
                mBluetoothAdapter.startLeScan(serviceUuids, getBleFindListener());
            } else {
                mBluetoothAdapter.startLeScan(getBleFindListener());
            }
        }

        //扫描超时
        mBluetoothServer.postDelay(new Runnable() {
            @Override
            public void run() {
                stopFindBleDevices();
            }
        }, stopDelaySecond);
        
       
    }

    /**
     * 取消查找设备
     */
    public boolean stopFindDevices() {
        checkBluetooth();
        return mBluetoothAdapter.cancelDiscovery();
    }

    public void stopFindBleDevices() {
        checkBluetooth();

        //移除延迟超时
        mBluetoothServer.getHandler().removeCallbacksAndMessages(Constant.RUNNABLE_TOKEN);
                
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBlueToothScanner.stopScan(getBleFindListener());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter.stopLeScan(getBleFindListener());
        }
        getBleFindListener().onScanTimeout();
    }

    /**
     * 获取绑定的设备
     *
     * @return
     */
    public List<BluetoothDevice> getBindDevices() {

        checkBluetooth();

        return new ArrayList<>(mBluetoothAdapter.getBondedDevices());
    }


    /**
     * 是否正在处于扫描过程中。如果蓝牙没有开启，该方法会返回false。
     *
     * @return
     */
    public boolean isDiscovering() {
        checkBluetooth();
        return mBluetoothAdapter.isDiscovering();
    }


    public BluetoothAdapter getAdapter() {
        checkBluetooth();
        return mBluetoothAdapter;
    }


    /**
     * 配对设备
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void createBond(BluetoothDevice device) {
        checkBluetooth();
        if (device != null) {
            stopFindDevices();
            device.createBond();
        }
    }


    /**
     * 取消配对
     *
     * @param device
     */
    public void removeBond(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("@@", "取消绑定出错:" + e.getMessage());
        }

    }


    public void setOnClientListener(BlueClientListener listener) {
        if (mBluetoothServer != null) {
            mBluetoothServer.setClientListener(listener);
        }
    }

    public void setOnMessageListener(BlueMessageListener listener) {
        if (mBluetoothServer != null) {
            mBluetoothServer.setMessageListener(listener);
        }
    }

    public void setOnServerListener(BlueServerListener listener) {
        if (mBluetoothServer != null) {
            mBluetoothServer.setServerListener(listener);
        }
    }

    public void setOnBleClientListener(BleClientListener listener){
        if(mBluetoothServer != null){

            mBluetoothServer.setBleClientListener(listener);
        }
    }


    /**
     * 释放资源
     */
    public void release() {
        super.release();

    }


    /**
     * 开启接收服务器
     */
    public void startServer() {
        startServer(-1);
    }
    public void startServer(int  timeout) {
        checkBluetooth();

        if (mBluetoothServer != null) {
            mBluetoothServer.startServer(timeout);
        }
    }

    /**
     * 停止传统蓝牙服务
     */
    public void stopServer() {
        if (mBluetoothServer != null) {
            mBluetoothServer.stopServer();
        }
    }


    public void startBleServer(AdvertiseSettings settings, AdvertiseData advertiseData, BleServerListener callback) {
        startBleServer(settings, advertiseData, null, callback);
    }


    /**
     * 开启ble服务，作为外围设备
     *
     * @param settings      广播参数
     * @param advertiseData 要广播的数据
     * @param scanResponse  扫描响应
     * @param callback
     */
    public void startBleServer(AdvertiseSettings settings, AdvertiseData advertiseData, AdvertiseData scanResponse, BleServerListener callback) {
        checkBluetooth();
        if (mBluetoothServer != null) {
            mBluetoothServer.startBleServer(settings, advertiseData, scanResponse, callback);
        }
    }


    public void stopBleServer() {
        if (mBluetoothServer != null) {
            mBluetoothServer.stopBleServer();
        }
    }


    /**
     * 链接到服务器
     *
     * @param device
     */
    public void connect(BluetoothDevice device) {
        checkBluetooth();

        if (mBluetoothServer != null) {
            mBluetoothServer.connect(device);
        }
    }

    public void disConnect() {
        checkBluetooth();

        if (mBluetoothServer != null) {
            mBluetoothServer.stopConnect();
        }
    }

    /**
     * 链接到ble设备
     *
     * @param device      要连接的ble设备
     * @param autoConnect 是否自动链接
     * @param transport   方式
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean connectBle(BluetoothDevice device, boolean autoConnect, int transport) {
        checkBluetooth();
        if (mBluetoothServer != null) {
            return mBluetoothServer.connectBle(mContext, device, autoConnect, transport);
        }
        return false;
    }
 
    
 
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean connectBle(BluetoothDevice device) {
        return connectBle(device, true);
    }

    /**
     * 连接ble服务器
     * @param device 要链接的服务器设备
     * @param autoConnect 
     */
 
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean connectBle(BluetoothDevice device, boolean autoConnect) {
        checkBluetooth();
        if (mBluetoothServer != null) {
            return mBluetoothServer.connectBle(mContext, device, autoConnect);
        } 
            
        return false;
        
    }
    
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void disConnectBle(){
        checkBluetooth();
        if (mBluetoothServer != null) {
             mBluetoothServer.disConnectBle();
        }
    }
    
    
    
    


    public BluetoothGatt getBluetoothGatt(){
        if(mBluetoothServer != null){
            return mBluetoothServer.getBluetoothGatt();
        }
        return null;
    }

    /**
     * 发送消息
     *
     * @param text
     */
    public void sendMessage(String text) {
        checkBluetooth();
        if (mBluetoothServer != null) {
            mBluetoothServer.sendMessage(text);
        }
    }
    
    public void sendBleMessage(String text){
        checkBluetooth();
        if (mBluetoothServer != null) {
            mBluetoothServer.sendBleMessage(text);
        }
    }
    
    public void readBleMessage(BluetoothGattCharacteristic characteristic){
        checkBluetooth();
        if (mBluetoothServer != null) {
            mBluetoothServer.readBleMessage(characteristic);
        }
    }

    /**
     * 写描述
     * @param descriptor
     * @param data
     * @return
     */
    @SuppressLint("NewApi")
    public boolean writeDescriptor(BluetoothGattDescriptor descriptor, byte[] data) {
        
        //判断是否能通知
        if(descriptor == null){
            return false;
        }
        descriptor.setValue(data);
        return mBluetoothServer.writeDescriptor(descriptor);
    }

    /**
     * notify消息
     * @param characteristic
     * @param isEnable
     * @return
     */
    @SuppressLint("NewApi")
    public boolean notifyBleMessage(BluetoothGattCharacteristic characteristic,boolean isEnable) {

        //判断是否能通知
        if(!canNotify(characteristic)){
            return false;
        }
        return mBluetoothServer.notifyBleServerMessage(characteristic,isEnable);
    }
    
    
    @SuppressLint("NewApi")
    public boolean notifyDescriptor(BluetoothGattDescriptor descriptor, boolean enable){
        return mBluetoothServer.notifyDescriptor(descriptor,enable);
    }
    
    

    /**
     * characteristic是否可以通知
     * @param characteristic
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean canNotify(BluetoothGattCharacteristic characteristic){
        int charaProp = characteristic.getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0){
            //具备通知属性
            return true;
        }else{
            return false;
        }
    }

    /**
     * characteristic是否可读
     * @param characteristic
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean canRead(BluetoothGattCharacteristic characteristic){
        int charaProp = characteristic.getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0){
            //可读
            return true;
        }else{
            return false;
        }
    }

    /**
     * characteristic是否可写
     * @param characteristic
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean canWrite(BluetoothGattCharacteristic characteristic){
        int charaProp = characteristic.getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0){
            //可读
            return true;
        }else{
            return false;
        }
    }
    
    public BluetoothServer getBluetoothServer() {
        return mBluetoothServer;
    }


  
}
