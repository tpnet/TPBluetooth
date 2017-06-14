package com.tpnet.bluedemo;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tpnet.bluedemo.adapter.MessageListAdapter;
import com.tpnet.bluedemo.bean.IMMessage;
import com.tpnet.bluedemo.util.ToastUtil;
import com.tpnet.tpbluetooth.TPBluetooth;
import com.tpnet.tpbluetooth.inter.BlueClientListener;
import com.tpnet.tpbluetooth.inter.BlueMessageListener;
import com.tpnet.tpbluetooth.inter.BlueServerListener;
import com.tpnet.tpbluetooth.inter.connect.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Created by litp on 2017/6/1.
 */

public class IMActivity extends Activity implements View.OnClickListener, BlueMessageListener {

    private Toolbar mToolbar;


    private RecyclerView mRcvMessage;
    private EditText mEtMessage;
    private Button mBtnSend;
     

    private List<IMMessage> mDataList;
    private MessageListAdapter mAdapter;
    
    private TPBluetooth mBlueControl;

    BluetoothDevice mDevice;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_im);
        
        mRcvMessage = (RecyclerView) findViewById(R.id.rcv_message);
        mEtMessage = (EditText) findViewById(R.id.et_message);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
       
        mDataList = new ArrayList<>();
        mAdapter  = new MessageListAdapter(mDataList);
        
        mRcvMessage.setLayoutManager(new LinearLayoutManager(this));
        mRcvMessage.setAdapter(mAdapter);
        
        mBtnSend.setOnClickListener(this);
        
        mBlueControl = TPBluetooth.getInstance();
        
        mBlueControl.setOnMessageListener(this);

        



        mDevice = getIntent().getParcelableExtra(Constant.INTENT_DEVICE);
        if(mDevice != null){
            //链接服务器
            mBlueControl.connect(mDevice);
        }else{
            //服务器开始聊天
            mToolbar.setTitle("当前链接设备:"+mBlueControl.getBluetoothServer().getClientNum());

        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        mBlueControl.setOnClientListener(new BlueClientListener() {

            @Override
            public void onStartConnect() {
                super.onStartConnect();
                ToastUtil.show("正在链接..."+mDevice.getName());

            }

            @Override
            public void onFinishConnect() {
                super.onFinishConnect();

            }

            @Override
            public void onConnecting() {
                super.onConnecting();
                ToastUtil.show("已链接");
                mToolbar.setTitle(mDevice.getName());
            }

            @Override
            public void onClientError(Exception e) {
                super.onClientError(e);
                ToastUtil.show("链接失败:"+e.getMessage());
            }
        });

        mBlueControl.setOnServerListener(new BlueServerListener() {
            @Override
            public void onStartListener() {
                super.onStartListener();
            }

            @Override
            public void onListenering() {
                super.onListenering();
            }

            @Override
            public void onFinishListener() {
                super.onFinishListener();
            }

            @Override
            public void onServerError(Exception e) {
                super.onServerError(e);
            }

            @Override
            public void onGetClient(BluetoothDevice device) {
                super.onGetClient(device);
                //有客户端进来了
                ToastUtil.show("有一设备进来了");
                mToolbar.setTitle("当前链接设备:"+mBlueControl.getBluetoothServer().getClientNum());
 
            }

        });
    }

    @Override
    public void onClick(View v) {
        //发送消息
        String text = mEtMessage.getText().toString().trim();
        mBlueControl.sendMessage(text);

        IMMessage message = new IMMessage();
        message.setContent(text);
        message.setMac(mBlueControl.getAdapter().getAddress());
        message.setName(mBlueControl.getAdapter().getName());
        message.setSender(true);
        message.setTime(System.currentTimeMillis());
        insertMessage(message);
        
        mEtMessage.setText("");
        
    }

    @Override
    public void onReceiveMessage(BluetoothDevice device, String mess) {
        //收到消息
        
        IMMessage message = new IMMessage();
        message.setContent(mess);
        if(!TextUtils.isEmpty(device.getName())){
            message.setName(device.getName());
        }
        message.setMac(device.getAddress());
        message.setSender(false);
        message.setTime(System.currentTimeMillis());
        insertMessage(message);
        
    } 
    
    
    private void insertMessage(IMMessage message){
        mDataList.add(message);
        mAdapter.notifyItemInserted(mDataList.size()-1);
        
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭链接
        if(mDevice != null){
            //关闭客户端链接
            //mBlueControl.
        }
    }
}
