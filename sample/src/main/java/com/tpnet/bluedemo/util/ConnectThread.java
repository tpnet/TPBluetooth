package com.tpnet.bluedemo.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;

/**
 * 客户端链接服务端的的线程 socket类
 * Created by litp on 2017/5/27.
 */

public class ConnectThread extends Thread {

    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private BluetoothAdapter mBlueAdapter;
    private final Handler mHandler;
    private ConnectedThread mConnectedThread;


    public ConnectThread(BluetoothDevice device, BluetoothAdapter adapter, Handler mHandler) {

        this.mHandler = mHandler;
        this.mDevice = device;
        this.mBlueAdapter = adapter;

        BluetoothSocket cacheSocket = null;
        try {
            cacheSocket = device.createRfcommSocketToServiceRecord(AcceptThread.MY_UUID);

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.mSocket = cacheSocket;
    }

    @Override
    public void run() {
        super.run();

        //取消搜索
        mBlueAdapter.cancelDiscovery();
        
        try {
            //链接
            mSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendMessage(mHandler.obtainMessage(AcceptThread.MSG_ERROR,e));
            
            try{
                mSocket.close();
            }catch (IOException e1){
                e1.printStackTrace();
            }
            return;
            
        }
        managerConnectedSocket(mSocket);
    }

    /**
     * 链接发送数据
     * @param socket
     */
    private void managerConnectedSocket(BluetoothSocket socket){
        mHandler.sendEmptyMessage(AcceptThread.MSG_CONNECT_TOSERVER);
        mConnectedThread = new ConnectedThread(mSocket,mHandler);
        mConnectedThread.start();
    }

    /**
     * 取消
     */
    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送信息给远程设备
     *
     * @param data
     */
    public void sendData(byte[] data) {
        if(mConnectedThread != null){
            mConnectedThread.write(data);
        }
    }
}
