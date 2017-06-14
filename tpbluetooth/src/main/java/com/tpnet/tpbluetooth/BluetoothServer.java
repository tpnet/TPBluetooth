package com.tpnet.tpbluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.tpnet.tpbluetooth.inter.BleClientListener;
import com.tpnet.tpbluetooth.inter.BleServerListener;
import com.tpnet.tpbluetooth.inter.BlueClientListener;
import com.tpnet.tpbluetooth.inter.BlueMessageListener;
import com.tpnet.tpbluetooth.inter.BlueServerListener;
import com.tpnet.tpbluetooth.inter.connect.Constant;
import com.tpnet.tpbluetooth.thread.ClientThread;
import com.tpnet.tpbluetooth.thread.ServerThread;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tpnet.tpbluetooth.inter.connect.Constant.CLIENT_CONNECTING;
import static com.tpnet.tpbluetooth.inter.connect.Constant.CLIENT_ERROR;
import static com.tpnet.tpbluetooth.inter.connect.Constant.CLIENT_FINISH_CONNECT;
import static com.tpnet.tpbluetooth.inter.connect.Constant.CLIENT_START_CONNECT;
import static com.tpnet.tpbluetooth.inter.connect.Constant.RECEIVE_MESSAGE;
import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_CLOSE_CLIENT;
import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_ERROR;
import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_FINISH_LISTENER;
import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_GET_CLIENT;
import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_LISTENERING;
import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_REMOVE_CLIENT;
import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_START_LISTENER;

/**
 * 蓝牙服务端管理类
 * Created by litp on 2017/6/1.
 */

public class BluetoothServer {

    private final BluetoothAdapter mAdapter;
    
    private ServerThread mServerThread;
    
    private ClientThread mClientThread;
    
    private BlueServerListener mServerListener;  //服务器状态监听器
    private BlueClientListener mClientListener;  //客户端状态监听器
    private BlueMessageListener mMessageListener;  //接收消息监听器
    
    private BleClientListener mBleClientListener; //ble的链接监听器


    private MessageProtocol mMessageProtocol;  //消息处理


    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;  //Ble作为外围设备的广播

    private BleAdvertiseCallback mAdvertiseCallback;         //开启Ble服务器回调


    private BluetoothGatt mBluetoothGatt;

    
    //链接上的服务器的服务列表
    private Map<BluetoothGattService,List<BluetoothGattCharacteristic>> serviceListMap;



    //private BluetoothDevice mBluetoothDevice;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            BluetoothDevice device;
            switch (msg.what) {
                case SERVER_START_LISTENER:
                    Log.e("@@", "服务器开始监听");
                    if(mServerListener != null){
                        mServerListener.onStartListener();
                    }
                    break;
                case SERVER_LISTENERING:
                    Log.e("@@", "服务器正在监听...");
                    if(mServerListener != null){
                        mServerListener.onListenering();
                    }
                    break;
                case SERVER_FINISH_LISTENER:
                    Log.e("@@", "服务器完成监听");
                    if(mServerListener != null){
                        mServerListener.onFinishListener();
                    }
                    break;
                case SERVER_GET_CLIENT:
                    Log.e("@@", "有客户端链接进来了");
                    BluetoothDevice  remoteDevice = (BluetoothDevice) msg.obj;
                    if(mServerListener != null){
                        mServerListener.onGetClient(remoteDevice);
                    }
                    break;
                case SERVER_ERROR:
                    IOException e = (IOException) msg.obj;
                    Log.e("@@", "链接错误:" + e.getMessage());
                    if(mServerListener != null){
                        mServerListener.onServerError(e);
                    }
                    break;
                case SERVER_CLOSE_CLIENT:
                    //客户端关闭了链接，服务端也得去掉
                    
                    device = (BluetoothDevice) msg.obj;
                    if(mServerListener != null){
                        mServerListener.onCloseClient(device);
                    }
                    break;
                case SERVER_REMOVE_CLIENT:
                    device = (BluetoothDevice) msg.obj;
                    
                    if(mServerListener != null){
                        mServerListener.onRemoveClient(device);
                    }
                    break;
            
                
                
                case RECEIVE_MESSAGE:
                    Bundle bundle = (Bundle) msg.obj;
                    String mess = bundle.getString(Constant.INTENT_MESSAGE);
                    device = bundle.getParcelable(Constant.INTENT_DEVICE);
                    Log.e("@@", "收到消息:" + mess);
                    
                    if(mMessageListener != null){
                        mMessageListener.onReceiveMessage(device,mess);
                    }
                    break;
  
                
                case CLIENT_START_CONNECT:
                    Log.e("@@", "开始链接到服务器");
                    if(mClientListener != null){
                        mClientListener.onStartConnect();
                    }
                    break;
                case CLIENT_FINISH_CONNECT:
                    Log.e("@@", "链接到服务器完毕");
                    if(mClientListener != null){
                        mClientListener.onFinishConnect();
                    }
                    break;
                case CLIENT_CONNECTING:
                    Log.e("@@", "正在链接到服务器。。。");
                    if(mClientListener != null){
                        mClientListener.onConnecting();
                    }
                    break;
                case CLIENT_ERROR:
                    Log.e("@@", "链接到服务器错误");
                    IOException e1 = (IOException) msg.obj;
                    if(mClientListener != null){
                        mClientListener.onClientError(e1);
                    }
                    break;
            }
        }
    };
 
    public BluetoothServer(BluetoothAdapter mAdapter ) {
        this.mAdapter = mAdapter;
        mMessageProtocol = new MessageProtocol();
        serviceListMap = new HashMap<>();
    }

    /**
     * 作为服务端，开启服务
     */
    public void startServer(int timeout) {
        
        //关闭客户端
        if(mClientThread != null){
            mClientThread.cancel();
        }
        
        //开启服务端
        if (mServerThread == null) {
            mServerThread = new ServerThread(mAdapter,mHandler,timeout);
            mServerThread.start();
        }
    }

    /**
     * 关闭服务端
     */
    public void stopServer(){
        if (mServerThread != null) {
            mServerThread.cancel();
            mServerThread = null;
        }
    }

    /**
     * 在服务器中，断开一个链接的客户端
     * @param device 要断开服务器的设备
     */
    public void stopServer(BluetoothDevice device){
        if (mServerThread != null) {
            mServerThread.cancelClient(device);
        }
    }
    

    /**
     * 开始链接到服务器
     * @param device 要链接的设备
     */
    public synchronized void connect(BluetoothDevice device) {
        if (mClientThread != null ) {
            mClientThread.cancel();
            mClientThread = null;
        }
        if (mServerThread != null) {
            mServerThread.cancel();
            mServerThread = null;
        }
        mClientThread = new ClientThread(device,mAdapter,mHandler);
        
        mClientThread.start();
     
    }

    public void stopConnect(){
        if (mClientThread != null ) {
            mClientThread.cancel();
            mClientThread = null;
        }
    }

    /**
     * 发送消息
     * @param text
     */
    public void sendMessage(String text) {
        byte[] bytes = mMessageProtocol.encodePackage(text);
        if(mClientThread != null){
            mClientThread.sendData(bytes);
        }else if(mServerThread != null){
            mServerThread.sendData(bytes);
        }
    }

    /**
     * 使用notify方式去获取ble服务器发来的消息
     * 这种方式不用手机去轮询地读设备上的数据。手机可以用如下方式给设备设置notification功能。
     * 如果notificaiton方式对于某个Characteristic是enable的，那么当设备上的这个Characteristic改变时，
     * 手机上的onCharacteristicChanged()回调就会被促发。
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean notifyBleServerMessage(BluetoothGattCharacteristic characteristic,boolean isEnable){
        //设置
        if(mBluetoothGatt == null || characteristic == null){
            return false;
        }
        return mBluetoothGatt.setCharacteristicNotification(characteristic,isEnable);
 /*       //将指令放置进来
        characteristic.setValue(new byte[] {0x7e, 0x14, 0x00, 0x00,0x00,(byte) 0xaa});
        //设置回复形式
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        //开始写数据
        mBluetoothGatt.writeCharacteristic(characteristic);*/
    }


    /**
     * 发送消息给ble设备
     * @param text
     */
    public void sendBleMessage(String text){
        byte[] bytes = mMessageProtocol.encodePackage(text);
        //mBluetoothGatt.
    }

    /**
     * 主动去读取ble的消息，回调onCharacteristicRead,推荐使用notify
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean readBleMessage(BluetoothGattCharacteristic characteristic){
        return mBluetoothGatt.readCharacteristic(characteristic);
    }
    

    /**
     * 发送Handler消息
     * @param what
     * @param obj
     */
    public void sendHandlerMessage(int what, Object obj){
        if(obj == null){
            mHandler.sendEmptyMessage(what);
        }else{
            mHandler.sendMessage(mHandler.obtainMessage(what,obj));
        }
    }


    /**
     * 获取当前链接的客户端设备数量
     * @return
     */
    public int getClientNum(){
        
        return mServerThread.getAllConnectThread().size();
    }


    /**
     * 设置服务器相关的监听事件
     * @param listener
     */
    public void setServerListener(BlueServerListener listener){
        this.mServerListener  = listener;
    }


    public void setMessageListener(BlueMessageListener listener) {
        this.mMessageListener = listener;
    }

    public void setClientListener(BlueClientListener listener) {
        this.mClientListener = listener;
    }
    
    public void setBleClientListener(BleClientListener listener){
        this.mBleClientListener = listener;
    }

    /**
     * 停止ble服务器
     */
    public void stopBleServer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mAdvertiseCallback != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
 
            //回调停止
            mAdvertiseCallback.onStop();
            BlueLog.e("关闭服务器");
            mAdvertiseCallback = null;
            
        } 
            
    }

    /**
     * 开启ble服务
     * @param settings
     * @param advertiseData
     * @param scanResponse
     * @param callback
     */
    public void startBleServer(AdvertiseSettings settings, AdvertiseData advertiseData, AdvertiseData scanResponse, BleServerListener callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothLeAdvertiser = getBluetoothLeAdvertiser();
            if(mBluetoothLeAdvertiser == null){
                
            }
            this.mAdvertiseCallback = new BleAdvertiseCallback(callback,settings);
            mBluetoothLeAdvertiser.startAdvertising(settings,advertiseData,scanResponse,mAdvertiseCallback);
        }else{
            throw new RuntimeException("低于5.0的手机不支持ble服务器功能");
        }
    }

    
    public BluetoothLeAdvertiser getBluetoothLeAdvertiser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mAdapter.getBluetoothLeAdvertiser();
        }else{
            throw new RuntimeException("低于5.0的手机不支持ble服务器功能");
        }
    
    }

    /**
     * 链接到ble设备,返回BluetoothGatt，就可以进行相关读写数据操作
     * @param mContext
     * @param device
     * @param autoConnect
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean connectBle(Context mContext, BluetoothDevice device, boolean autoConnect ) {
        BlueLog.e("开始链接ble设备:"+device.getAddress());
        mBluetoothGatt = device.connectGatt(mContext, true, new BleConnectListener());
        return mBluetoothGatt.connect();
        //return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean connectBle(Context mContext, BluetoothDevice device, boolean autoConnect, int transport) {
        mBluetoothGatt = device.connectGatt(mContext, true, new BleConnectListener(),transport);
        return mBluetoothGatt.connect();
        //return true;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void disConnectBle(){
        mBluetoothGatt.disconnect();
    }
    
    

    public BluetoothGatt getBluetoothGatt(){
        return mBluetoothGatt;
    }
    
    
    public Handler getHandler() {
        return mHandler;
    }


    public void post(Runnable runnable ){
        mHandler.post(runnable);
        //mHandler.postDelayed(runnable,delay*1000);
    }
    
    public void postDelay(Runnable runnable,int delay){
        mHandler.postAtTime(runnable,Constant.RUNNABLE_TOKEN,  SystemClock.uptimeMillis()+delay*1000);
        //mHandler.postDelayed(runnable,delay*1000);
    }
    
    
    
    public Map<BluetoothGattService, List<BluetoothGattCharacteristic>> getServiceMap(){
        return serviceListMap;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean notifyDescriptor(BluetoothGattDescriptor descriptor, boolean enable) {
        if (mBluetoothGatt != null && descriptor != null) {
            if (enable) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            } else {
                descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            }
            return mBluetoothGatt.writeDescriptor(descriptor);
        }
        return false;
        
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean writeDescriptor(BluetoothGattDescriptor descriptor) {
        
        return mBluetoothGatt.writeDescriptor(descriptor);
    }


    //服务器广播监听器
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    class BleAdvertiseCallback extends AdvertiseCallback{
         
        
        private BleServerListener mListener;
         
        private AdvertiseSettings mSettings;
        
        private boolean isError;
        
        public void onStop(){
            if(mListener != null){
                mListener.onStop();
            }
        }


        public BleAdvertiseCallback(final BleServerListener mListener, final AdvertiseSettings mSettings) {
            this.mListener = mListener;
            this.mSettings = mSettings;
 
 
            if(mSettings != null && mSettings.getTimeout()>0 && mSettings.getTimeout() < 180000){
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(isError && mListener!=null){
                            mListener.onTimeout(mSettings.getTimeout());
                        }
                    }
                },mSettings.getTimeout());
            }

            
            
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            if(mListener != null){
                mListener.onStartFailure(errorCode);
            }
        }


        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            if(mListener != null) {
                mListener.onStartSuccess(settingsInEffect);
            }
            
        }
    }
    
     
    
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    class BleConnectListener extends BluetoothGattCallback{
      

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            //
            switch (newState){
                case BluetoothProfile.STATE_CONNECTED:  //已经链接
                    if(mBleClientListener != null){
                        //开始发现服务,需要发现服务成功后才能进行相关操作，一定要调用此方法，否则获取不到服务 
                        gatt.discoverServices();
                        post(new Runnable() {
                            @Override
                            public void run() {
                                mBleClientListener.onBleConnected(gatt,status,newState);
                            }
                        });
                    }

                    break;
                case BluetoothProfile.STATE_CONNECTING:  //链接中
                    if(mBleClientListener != null){
                        post(new Runnable() {
                            @Override
                            public void run() {
                                mBleClientListener.onBleConnecting(gatt,status,newState);
                            }
                        });
                    }
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:  //断开链接了
                    if(mBleClientListener != null){
                        post(new Runnable() {
                            @Override
                            public void run() {
                                mBleClientListener.onBleDisconnected(gatt,status,newState);
                            }
                        });
                    }
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:  //断开链接中
                    if(mBleClientListener != null){
                        post(new Runnable() {
                            @Override
                            public void run() {
                                mBleClientListener.onBleDisconnecting(gatt,status,newState);
                            }
                        });
                    }
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            //发现服务后，就可以进行写入数据、开启notify等等操作
            if(mBleClientListener != null){

                serviceListMap = new HashMap<>();

                for (BluetoothGattService service : gatt.getServices()) {

                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();

                    serviceListMap.put(service,characteristics);
                
                /*for (BluetoothGattCharacteristic characteristic : characteristics) {
                    
                    
                }
*/
                }

                post(new Runnable() {
                    @Override
                    public void run() {
                        mBleClientListener.onServicesDiscovered(gatt,serviceListMap);
                    }
                });

            }

            
            
            
            
            
        }

        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            //读取数据时候回调

            if(mBleClientListener != null){
                post(new Runnable() {
                    @Override
                    public void run() {
                        mBleClientListener.onCharacteristicRead(gatt,characteristic,status);
                    }
                });
            }
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            //对ble设备写数据回调
            if(mBleClientListener != null){
                post(new Runnable() {
                    @Override
                    public void run() {
                        mBleClientListener.onCharacteristicWrite(gatt,characteristic,status);
                    }
                });
            }

        }

        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            //开启notify之后，就可以在这里接收数据，处理数据， 此处接收BLE设备返回数据
            if(mBleClientListener != null){
                post(new Runnable() {
                    @Override
                    public void run() {
                        mBleClientListener.onCharacteristicChanged(gatt,characteristic);
                    }
                });
            }
        }

        @Override
        public void onDescriptorRead(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            //
            if(mBleClientListener != null){
                post(new Runnable() {
                    @Override
                    public void run() {
                        mBleClientListener.onDescriptorRead(gatt,descriptor,status);
                    }
                });
            }

        }

        @Override
        public void onDescriptorWrite(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            //
            if(mBleClientListener != null){
                post(new Runnable() {
                    @Override
                    public void run() {
                        mBleClientListener.onDescriptorWrite(gatt,descriptor,status);
                    }
                });
            }
        }

        @Override
        public void onReliableWriteCompleted(final BluetoothGatt gatt, final int status) {
            super.onReliableWriteCompleted(gatt, status);
            //
            if(mBleClientListener != null){
                post(new Runnable() {
                    @Override
                    public void run() {
                        mBleClientListener.onReliableWriteCompleted(gatt,status);
                    }
                });
            }

        }

        @Override
        public void onReadRemoteRssi(final BluetoothGatt gatt, final int rssi, final int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            //
            if(mBleClientListener != null){
                post(new Runnable() {
                    @Override
                    public void run() {
                        mBleClientListener.onReadRemoteRssi(gatt,rssi,status);
                    }
                });
            }

        }

        @Override
        public void onMtuChanged(final BluetoothGatt gatt, final int mtu, final int status) {
            super.onMtuChanged(gatt, mtu, status);
            //
            if(mBleClientListener != null){
                post(new Runnable() {
                    @Override
                    public void run() {
                        mBleClientListener.onMtuChanged(gatt,mtu,status);

                    }
                });
            }
        }
        
    }
    
    
}
