package com.tpnet.bluedemo;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tpnet.bluedemo.adapter.DeviceListAdapter;
import com.tpnet.bluedemo.view.DefaultAdapter;
import com.tpnet.bluedemo.view.DeviceItemLayout;
import com.tpnet.tpbluetooth.TPBluetooth;
import com.tpnet.tpbluetooth.inter.BlueBondListener;
import com.tpnet.tpbluetooth.inter.BlueFindListener;
import com.tpnet.tpbluetooth.inter.BlueServerListener;
import com.tpnet.tpbluetooth.inter.BlueStateListener;
import com.tpnet.tpbluetooth.inter.connect.Constant;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DefaultAdapter.OnRecyclerViewItemClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {


    private TPBluetooth mBlueControl;


    private TextView mTvTip;

    private Button mBtnSearch;
    private Button mBtnStopSearch;
    private Button mBtnSercerListener;
    private RecyclerView mRcvList;
    private ProgressBar mPrbSearchTip;
    private Button mBtnCheckBond;
    private CheckBox mRbOpen;
    private CheckBox mRbOpenVisiable;
    private TextView mTvServerState;



    private DeviceListAdapter mAdapter;

    private List<BluetoothDevice> mList;    //搜索到的设备列表

    private List<BluetoothDevice> mBindList;  //绑定的设备列表

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
   

        mTvTip = (TextView) findViewById(R.id.tv_tip);
        mBtnSearch = (Button) findViewById(R.id.btn_start_search);
        mBtnStopSearch = (Button) findViewById(R.id.btn_stop_search);
        mRcvList = (RecyclerView) findViewById(R.id.rcv_list);
        mPrbSearchTip = (ProgressBar) findViewById(R.id.prb_search_tip);
        mBtnCheckBond = (Button) findViewById(R.id.btn_check_bind);
        mBtnSercerListener = (Button) findViewById(R.id.btn_server_listener);
        mRbOpen = (CheckBox) findViewById(R.id.cb_open);
        mRbOpenVisiable = (CheckBox) findViewById(R.id.cb_open_viviable);
        mTvServerState = (TextView) findViewById(R.id.tv_server_state);

        mBtnSearch.setOnClickListener(this);
        mBtnStopSearch.setOnClickListener(this);
        mBtnCheckBond.setOnClickListener(this);
        mBtnSercerListener.setOnClickListener(this);

        mRbOpen.setOnCheckedChangeListener(this);
        mRbOpenVisiable.setOnCheckedChangeListener(this);

 
        mBlueControl =  TPBluetooth.getInstance();
        //初始化传统蓝牙模式
        mBlueControl.initClassicBluetooth();
        
        
        mBlueControl.setOnBlueStateListener(new BlueStateListener() {
            @Override
            public void onOpen() {
                super.onOpen();
                mRbOpen.setChecked(true);
            }

            @Override
            public void onClose() {
                super.onClose();
                mRbOpen.setChecked(false);

            }

            @Override
            public void onConnected() {
                super.onConnected();
                //链接上服务器了
            }

            @Override
            public void onDisconnected() {
                super.onDisconnected();
                //断开链接服务器
            }
        });
        
        mBlueControl.setOnBlueBondListener(new BlueBondListener() {
            @Override
            public void onBonded(BluetoothDevice device) {
                super.onBonded(device);
                showToast("配对成功"+device.getName());
                onClick(mBtnCheckBond);
            }

            @Override
            public void onBonding(BluetoothDevice device) {
                super.onBonding(device);
                showToast("正在和"+device.getName()+"进行配对");
            }

            @Override
            public void onCancleBond(BluetoothDevice device) {
                super.onCancleBond(device);
                showToast("取消配对"+device.getName());
                onClick(mBtnCheckBond);

            }
        });
        
        
        mBlueControl.setOnBlueFindListener(new BlueFindListener() {
            @Override
            public void onStartDiscovery() {
                super.onStartDiscovery();
                mPrbSearchTip.setVisibility(View.VISIBLE);
                mList.clear();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinishDiscovery() {
                super.onFinishDiscovery();
                mPrbSearchTip.setVisibility(View.GONE);
            }

            @Override
            public void onFound(BluetoothDevice device) {
                super.onFound(device);
                if (device != null) {
                    if (!mList.contains(device) && !mBindList.contains(device)) {
                        Log.e("@@", "添加到设备列表" + device.getName());
                        mList.add(device);
                        mAdapter.notifyItemRangeInserted(mAdapter.getItemCount() - 1, mAdapter.getItemCount());

                    }
                }
            }

            @Override
            public void onModeConnectableDiscoverable() {
                super.onModeConnectableDiscoverable();
                mRbOpenVisiable.setChecked(true);

            }

            @Override
            public void onModeConnectable() {
                super.onModeConnectable();
                mRbOpenVisiable.setChecked(false);

            }

            @Override
            public void onModeClose() {
                super.onModeClose();
                mRbOpenVisiable.setChecked(false);

            }
        });

 
        
        if (!mBlueControl.isSupportBlueTooth()) {
            //不支持蓝牙
            mTvTip.setText("no");
            return;
        }

        mTvTip.setText("yes");

        //判断蓝牙是否开启
        if (mBlueControl.isBluetoothEnable()) {
            mRbOpen.setChecked(true);

        } else {
            mRbOpen.setChecked(false);
        }
        
        ///判断检测型是否开启
        if(mBlueControl.isBluetoothVisiable()){
            mRbOpenVisiable.setChecked(true);
        }else{
            mRbOpenVisiable.setChecked(false);
        }

        mList = new ArrayList<>();
        mBindList = new ArrayList<>();
        mAdapter = new DeviceListAdapter(mList);
        mAdapter.setOnItemClickListener(this);
        mRcvList.setLayoutManager(new LinearLayoutManager(this));
        mRcvList.setAdapter(mAdapter);


        onClick(mBtnCheckBond);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mBlueControl.setOnServerListener(new BlueServerListener() {
            @Override
            public void onGetClient(BluetoothDevice device) {
                super.onGetClient(device);
                //有设备链接进来了
                startActivity(new Intent(MainActivity.this,IMActivity.class));
                
            }

            @Override
            public void onStartListener() {
                super.onStartListener();
                mTvServerState.setText("开始开启服务器");
            }

            @Override
            public void onFinishListener() {
                super.onFinishListener();
                mTvServerState.setText("服务器关闭");

            }

            @Override
            public void onListenering() {
                super.onListenering();
                mTvServerState.setText("正在等待设备加入");
            }

            @Override
            public void onServerError(Exception e) {
                super.onServerError(e);
                mTvServerState.setText("服务器监听错误:"+e.getMessage());
            }
        });
    }

    // 监听蓝牙开启的方法之一：回调。 方法2是 广播
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TPBluetooth.REQUEST_CODE_OPEN_BLUETOOTH) {
            //
            if (resultCode == RESULT_OK) {

            } else if (resultCode == RESULT_CANCELED) {
                //启动蓝牙失败
                showToast("启动蓝牙失败");
                mRbOpen.setChecked(false);
                onClick(mBtnCheckBond);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBlueControl.release();
     
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.btn_start_search:
                //搜素蓝牙设备
                if (!mBlueControl.isDiscovering()) {
                    if (!mBlueControl.startFindDevices()) {
                        //查找设备，通过广播的形式返回结果
                        Log.e("@@", "还没有开启蓝牙，开启扫描失败");
                        showToast("还没有开启蓝牙，扫描失败");
                    } else {
                        Log.e("@@", "点击开始扫描蓝牙");
                        //mPrbSearchTip.setVisibility(View.GONE);
                    }

                }

                break;
            case R.id.btn_stop_search:
                //取消搜索蓝牙设备
                if (!mBlueControl.stopFindDevices()) {
                    //查找设备，通过广播的形式返回结果
                    Log.e("@@", "还没有开启蓝牙，开启扫描失败");
                    showToast("还没有开启蓝牙，通知扫描失败");
                } else {
                    Log.e("@@", "点击取消扫描蓝牙");
                }

                break;
            case R.id.btn_check_bind:
                //查看绑定设备

                mAdapter.removeAllHeaderView();
                mBindList.clear();

                List<BluetoothDevice> cacheList = mBlueControl.getBindDevices();
                
                if (cacheList.size() > 0) {
                    
                    mBindList.addAll(cacheList);

                    TextView bindHeader = new TextView(this);
                    bindHeader.setText("已绑定设备");
                    mAdapter.addHeaderView(bindHeader);
                    
                    int i = 0;
                    //循环add到头部
                    for (BluetoothDevice device : mBindList) {
                        DeviceItemLayout layout = (DeviceItemLayout) LayoutInflater.from(this).inflate(R.layout.item_device, mRcvList, false);
                        layout.setData(device);
                        layout.setTag(i);
                        layout.setOnClickListener(this);
                        layout.setOnLongClickListener(this);
                        mAdapter.addHeaderView(layout);

                        if (mList.contains(device)) { //移除列表中的
                            mList.remove(device);
                        }

                        i++;
                    }
                    
                    showToast("已绑定数量:"+mBindList.size()+", 点击进行链接");

                    TextView unBindHeader = new TextView(this);
                    unBindHeader.setText("未绑定设备");
                    mAdapter.addHeaderView(unBindHeader);

                }

                mAdapter.notifyDataSetChanged();


                break;
            case R.id.layout_item:
                BluetoothDevice device = mBindList.get((Integer) v.getTag());
                Log.e("@@", "点击了绑定的设备" + device.getName() + "开始链接手机");
                
                Intent intent = new Intent(this,IMActivity.class);
                intent.putExtra(Constant.INTENT_DEVICE,device);
                startActivity(intent);
                
                break;
            case R.id.btn_server_listener:
                //开启服务器端
 
                mBlueControl.startServer();

                break;
        }
    }
    

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onItemClick(View view, int viewType, Object data, int position) {
        // 点击设备就开始绑定设备

        BluetoothDevice device = (BluetoothDevice) data;
        Log.e("@@", "点击了设备" + device.getAddress() + ", 开始绑定");

        //开始配对,回调
        mBlueControl.createBond(device); 
 
    }


    protected void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.layout_item:
                BluetoothDevice device = mBindList.get((Integer) v.getTag());
                if(device != null){
                    Log.e("@@", "长按了设备" + device.getName() + "开始取消匹配");
                    mBlueControl.removeBond(device);
                }
 
                break;
        }
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      
        if(buttonView.isPressed()){
            if (buttonView == mRbOpen) {
                //蓝牙开关

                if (isChecked) {
                    //开
                    mBlueControl.openBlueTooth(this);
                } else {
                    //关
                    mBlueControl.closeBlueTooth();
                }
                
            } else if (mRbOpenVisiable == buttonView) {
                //蓝牙可视开关
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        //开
                        mBlueControl.setBlueToothVisiable(this, true);
                    } else {
                        //关
                        mBlueControl.setBlueToothVisiable(this, false);

                    }
                }
            }
                    
        }
        

    }
}
