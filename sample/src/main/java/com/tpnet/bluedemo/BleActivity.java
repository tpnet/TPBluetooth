package com.tpnet.bluedemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tpnet.bluedemo.adapter.DeviceListAdapter;
import com.tpnet.bluedemo.util.ToastUtil;
import com.tpnet.bluedemo.view.DefaultAdapter;
import com.tpnet.tpbluetooth.BlueLog;
import com.tpnet.tpbluetooth.TPBluetooth;
import com.tpnet.tpbluetooth.inter.BleFindListener;
import com.tpnet.tpbluetooth.inter.BleServerListener;
import com.tpnet.tpbluetooth.inter.connect.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.le.AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED;
import static android.bluetooth.le.AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE;
import static android.bluetooth.le.AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED;
import static android.bluetooth.le.AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR;
import static android.bluetooth.le.AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS;

/**
 * 
 * Created by litp on 2017/6/1.
 */

public class BleActivity extends Activity implements View.OnClickListener, DefaultAdapter.OnRecyclerViewItemClickListener {

    TPBluetooth mBlueControl;

    private List<BluetoothDevice> mDataList;
    private List<ScanResult> mResultList;

    private DeviceListAdapter mAdapter;


    private Button mBtStartScan;
    private Button mBtStopScan;
    private Button mBtScanFilter;
    private EditText mEtServerUuid;
    private RecyclerView mRcvBleList;
    private ProgressBar mPrbFind;
    private Button mBtnOpenBleServer;
    private Button mBtnCloseBleServer;

    private TextView mTvServerState;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        mBtStartScan = (Button) findViewById(R.id.bt_start_scan);
        mBtStopScan = (Button) findViewById(R.id.bt_stop_scan);
        mBtScanFilter = (Button) findViewById(R.id.bt_scan_filter);
        mEtServerUuid = (EditText) findViewById(R.id.et_server_uuid);
        mRcvBleList = (RecyclerView) findViewById(R.id.rcv_ble_list);
        mPrbFind = (ProgressBar) findViewById(R.id.prb_find);
        mBtnOpenBleServer = (Button) findViewById(R.id.btn_open_ble_server);
        mBtnCloseBleServer = (Button) findViewById(R.id.btn_close_ble_server);
        mTvServerState = (TextView) findViewById(R.id.tv_server_state);


        mDataList = new ArrayList<>();
        mResultList = new ArrayList<>();
        mAdapter = new DeviceListAdapter(mDataList);
        mRcvBleList.setLayoutManager(new LinearLayoutManager(this));
        mRcvBleList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        mBlueControl = TPBluetooth.getInstance();
        if (!mBlueControl.initBle()) {
            ToastUtil.show("该设备不支持Ble");
            return;
        }
        mBtStartScan.setOnClickListener(this);
        mBtScanFilter.setOnClickListener(this);
        mBtStopScan.setOnClickListener(this);
        mBtnOpenBleServer.setOnClickListener(this);
        mBtnCloseBleServer.setOnClickListener(this);


        mBlueControl.openBlueTooth(this);

        boolean b = mBlueControl.isSupportBlePeripheral();
        BlueLog.e("是否支持作为外围设备:" + b);


        mBlueControl.setOnBleFindListener(new BleFindListener() {
            
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
               
                
                BluetoothDevice device = result.getDevice();


                BlueLog.e("扫描到ble设备：" +  device.getName());


                if (!mDataList.contains(device) && !TextUtils.isEmpty(device.getName())) {
                    
                    /*if(result.getScanRecord().getServiceUuids() != null){
                        for(ParcelUuid uuid:result.getScanRecord().getServiceUuids()){
                            BlueLog.e(uuid.toString());
                        }
                    }
*/
                    BlueLog.e("添加ble设备：" + result.getScanRecord().getDeviceName()+" - "+result.getDevice().getName());

                    mResultList.add(result);
                    mDataList.add(device);
                    mAdapter.notifyItemInserted(mDataList.size() - 1);
                }


            }
            
            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                BlueLog.e("扫描错误:" + errorCode);
            }

            
            @Override
            public void onScanTimeout() {
                super.onScanTimeout();
                BlueLog.e("扫描超时");
                mPrbFind.setVisibility(View.GONE);

            }
        });

        
        
        
        
    }


    /**
     * 指定uuid寻找
     * @param uuid
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void find(String uuid) {
        List<ScanFilter> bleScanFilters = new ArrayList<>();
        bleScanFilters.add(
                new ScanFilter.Builder()
                        .setServiceUuid(ParcelUuid.fromString(uuid))
                        .build()
        );

        ScanSettings bleScanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                //.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                //.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                //.setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
                .setReportDelay(1000)
                .build();


        mBlueControl.startFindBleDevice(bleScanFilters, bleScanSettings);

    }


 
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startBleServer() {
  
        //用来设置这次广播的一些属性，
        // 例如，是要广播的密集一点（那么就比较耗电），或者是功率大一点（也耗电），以及广播多少时间（时间太长了就把电耗完了）
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)  //服务器广播的模式 
                .setConnectable(true)                                          //
                .setTimeout(30000)                                               //服务器广播超时时间,0 为一直广播
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)     //耗能
                .build();
        
        
        //需要广播的内容。例如是否包含本地设备的名字，是否包含发射功率，要包含的service是什么等等。
        AdvertiseData data = new AdvertiseData.Builder()
                .addServiceUuid(Constant.MY_PARCELUUID_UUID)
                //.setIncludeTxPowerLevel(true)
                //.addServiceData(Constant.MY_PARCELUUID_UUID,"test".getBytes())
                .setIncludeDeviceName(true)
                .build();
        
        
        mBlueControl.startBleServer(settings, data, new ServerListener());

    }


    
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void addDeviceInfoService() {  
        /*UUID can get from SIG website*/
        final String SERVICE_DEVICE_INFORMATION = "0000180a-0000-1000-8000-00805f9b34fb";
        final String SOFTWARE_REVISION_STRING = "00002A28-0000-1000-8000-00805f9b34fb";


        BluetoothGattService previousService =
                mBlueControl.getBluetoothGatt().getService( UUID.fromString(SERVICE_DEVICE_INFORMATION));

     
        BluetoothGattCharacteristic softwareVerCharacteristic = new BluetoothGattCharacteristic(
                UUID.fromString(SOFTWARE_REVISION_STRING),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ
        );

        BluetoothGattService deviceInfoService = new BluetoothGattService(
                UUID.fromString(SERVICE_DEVICE_INFORMATION),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);


        softwareVerCharacteristic.setValue(String.valueOf(BaseApplication.getVersionCode()).getBytes());

        deviceInfoService.addCharacteristic(softwareVerCharacteristic);
        //mGattServer.addService(deviceInfoService);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        if (v == mBtStartScan) {
            //开始扫描
            mDataList.clear();
            mResultList.clear();
            mAdapter.notifyDataSetChanged();
            mBlueControl.startFindBleDevice();
            //find(Constant.MY_PARCELUUID_UUID.toString());
            mPrbFind.setVisibility(View.VISIBLE);
        }else if(v == mBtScanFilter){
            //有条件的扫描
            
        } else if(v == mBtStopScan){
            //停止扫描
            mBlueControl.stopFindBleDevices();
            
        } else if(v == mBtnOpenBleServer){
            //开启ble服务器
            startBleServer();
        } else if(v == mBtnCloseBleServer){
            //关闭ble服务器
            mBlueControl.stopBleServer();
            
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onItemClick(View view, int viewType, Object data, int position) {
        //点击链接ble
        BluetoothDevice device = (BluetoothDevice) data;

        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra("result",mResultList.get(mDataList.indexOf(device)));
        startActivity(intent);
        
        //device = mBlueControl.getAdapter().getRemoteDevice(device.getAddress());
        /*if(!mBlueControl.connectBle(device)){
           //链接失败
            BlueLog.e("链接失败");
        }*/
    }


    //服务器开启监听器
    @SuppressLint("NewApi")
    class ServerListener extends BleServerListener {


        @Override
        public void onStop() {
            super.onStop();
            //关闭服务器
            mTvServerState.setText("关闭服务器");

        }

        @Override
        public void onTimeout(long timeout) {
            super.onTimeout(timeout);
            //开启服务器超时
            mTvServerState.setText("超时，停止服务器...");


        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            //成功地开始监听
            Log.e("@@", "作为外围设备 成功地开始监听:");
            mTvServerState.setText("监听中...");

        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            //开始监听失败
            Log.e("@@", "作为外围设备 开始监听失败:" + errorCode);

            switch (errorCode) {
                case ADVERTISE_FAILED_ALREADY_STARTED:
                    //已经开启了
                    Log.e("@@", "失败,已经在监听了：" + errorCode);
                    mTvServerState.setText("开启服务器失败，已经在监听了");
                    break;
                case ADVERTISE_FAILED_DATA_TOO_LARGE:
                    //数据太大导致失败
                    Log.e("@@", "服务器监听失败：数据太大" + errorCode);
                    mTvServerState.setText("开启服务器失败，广播数据太大");

                    break;
                case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                    //准备开始时候失败
                    Log.e("@@", "服务器监听失败：不支持" + errorCode);
                    mTvServerState.setText("开启服务器失败，不支持");

                    break;
                case ADVERTISE_FAILED_INTERNAL_ERROR:
                    //内部错误
                    Log.e("@@", "服务器监听失败：内部错误" + errorCode);
                    mTvServerState.setText("开启服务器失败，内部错误");

                    break;
                case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                    //太多广播
                    Log.e("@@", "服务器监听失败：太多服务器广播" + errorCode);
                    mTvServerState.setText("开启服务器失败，太多服务器广播");

                    break;

            }


        }
    }

}
