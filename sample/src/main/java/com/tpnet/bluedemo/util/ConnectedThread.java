package com.tpnet.bluedemo.util;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 服务端和客户端之前传输数据的线程
 * Created by litp on 2017/5/27.
 */

public class ConnectedThread extends Thread{
    
    private final BluetoothSocket mSocket;
    private final InputStream mInputStream;
    private final OutputStream mOututStream;
    private final Handler mHandler;
    
    public ConnectedThread(BluetoothSocket socket, Handler mHandler) {
        this.mSocket =  socket;
        this.mHandler = mHandler;
        InputStream cacheInput = null;
        OutputStream cacheOutput = null;
        
        try{
           cacheInput = socket.getInputStream(); 
           cacheOutput = socket.getOutputStream(); 
        }catch (IOException e){
            e.printStackTrace();
        }
        
        mInputStream = cacheInput;
        mOututStream = cacheOutput;
    }

    @Override
    public void run() {
        super.run();
        
        byte[] buffer = new byte[1024];
        int bytes = 0;
        
        while (true){
            try{
                //读取数据
                bytes = mInputStream.read(buffer);
                if(bytes > 0){
                    //转换为字符串，发给ui
                    Message message = mHandler.obtainMessage(AcceptThread.MSG_GET_CLIENT,new String(buffer,0,bytes,"utf-8"));
                    mHandler.sendMessage(message);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            Log.e("@@","内容大小："+bytes);
        }
        
        
    }

    /**
     * 取消  
     */
    public void cancel() {
        try{
            mSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        
    }

    /**
     * 发送信息给远程设备
     * @param data
     */
    public void write(byte[] data) {
        try{
            mOututStream.write(data);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
