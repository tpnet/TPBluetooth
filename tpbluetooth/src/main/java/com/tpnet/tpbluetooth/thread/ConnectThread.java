package com.tpnet.tpbluetooth.thread;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.tpnet.tpbluetooth.inter.connect.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.tpnet.tpbluetooth.inter.connect.Constant.RECEIVE_MESSAGE;

/**
 * 服务端和客户端 之间传输数据的线程
 * Created by litp on 2017/5/27.
 */

public class ConnectThread extends Thread{
    
    private final BluetoothSocket mSocket;
    
    private final InputStream mInputStream;
    
    private final OutputStream mOututStream;
    
    private final Handler mHandler;
    
    public ConnectThread(BluetoothSocket socket, Handler mHandler) {
        
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
                    
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Constant.INTENT_DEVICE,mSocket.getRemoteDevice());
                    bundle.putString(Constant.INTENT_MESSAGE,new String(buffer,0,bytes,"utf-8"));
                    
                    Message message = mHandler.obtainMessage(RECEIVE_MESSAGE,bundle);
                    mHandler.sendMessage(message);
                }
            }catch (IOException e){
                
                //对方socket关闭，
                
                //mHandler.sendMessage(mHandler.obtainMessage(Constant.CLIENT_FINISH_CONNECT,mSocket.getRemoteDevice()));
                
                mHandler.sendMessage(mHandler.obtainMessage(Constant.SERVER_CLOSE_CLIENT,mSocket.getRemoteDevice()));
                e.printStackTrace();
                break;
            }
            //Log.e("@@","内容大小："+bytes);
        }
        
        
    }

    /**
     * 取消  
     */
    public void cancel() {
        try{
            mSocket.close();
            mInputStream.close();
            mOututStream.close();
            
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

    public String getRemoteDeviceMac() {
        return mSocket.getRemoteDevice().getAddress();
    }

 

    @Override
    public boolean equals(Object obj) {
        return ((ConnectThread)obj).getRemoteDeviceMac().equals(getRemoteDeviceMac());
    }
}
