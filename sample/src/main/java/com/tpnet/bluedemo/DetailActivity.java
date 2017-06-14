package com.tpnet.bluedemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.tpnet.bluedemo.util.ToastUtil;
import com.tpnet.tpbluetooth.BlueLog;
import com.tpnet.tpbluetooth.TPBluetooth;
import com.tpnet.tpbluetooth.inter.BleClientListener;

import java.util.List;
import java.util.Map;

import static com.tpnet.tpbluetooth.BlueLog.e;

/**
 * 
 * Created by litp on 2017/6/12.
 */

public class DetailActivity extends Activity {


    private TextView mTvName;
    private TextView mTvConnect;
    private TextView mTvAddress;
    private TextView mTvRssi;
    private TextView mTvUuid;
    private TextView mTvBondState;
    private TextView mTvType;
    private TextView mTvBlueclass;
    private TextView mTvTimestampNanos;
    private TextView mTvAdvertiseflags;
    private TextView mTvBytes;
    private TextView mTvManufactureSpecificdata;
    private TextView mTvServicedata;
    private TextView mTvServiceudid;
    private TextView mTvTxpowerlevel;
  
    TPBluetooth mBlueControl;
    
    ProgressDialog mProgressDialog;
    

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTvConnect = (TextView) findViewById(R.id.tv_connect);
        
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvAddress = (TextView) findViewById(R.id.tv_address);
        mTvRssi = (TextView) findViewById(R.id.tv_rssi);
        mTvUuid = (TextView) findViewById(R.id.tv_uuid);
        mTvBondState = (TextView) findViewById(R.id.tv_bond_state);
        mTvType = (TextView) findViewById(R.id.tv_type);
        mTvBlueclass = (TextView) findViewById(R.id.tv_blueclass);
        mTvTimestampNanos = (TextView) findViewById(R.id.tv_timestampNanos);
        mTvAdvertiseflags = (TextView) findViewById(R.id.tv_advertiseflags);
        mTvBytes = (TextView) findViewById(R.id.tv_bytes);
        mTvManufactureSpecificdata = (TextView) findViewById(R.id.tv_manufacture_specificdata);
        mTvServicedata = (TextView) findViewById(R.id.tv_servicedata);
        mTvServiceudid = (TextView) findViewById(R.id.tv_serviceudid);
        mTvTxpowerlevel = (TextView) findViewById(R.id.tv_txpowerlevel);

     
        ScanResult result = getIntent().getParcelableExtra("result");
        final BluetoothDevice device = result.getDevice();
        ScanRecord record = result.getScanRecord();
        
        
        mBlueControl = TPBluetooth.getInstance();
        //链接ble服务器监听器
        mBlueControl.setOnBleClientListener(new BleClientListener() {

            @Override
            public void onBleConnected(BluetoothGatt gatt, int status, int newState) {
                super.onBleConnected(gatt, status, newState);
                e("已经链接服务器设备，状态:" + newState);
                ToastUtil.show("链接成功");
                mProgressDialog.dismiss();
                mTvConnect.setText("断开链接");

            }

            @Override
            public void onBleConnecting(BluetoothGatt gatt, int status, int newState) {
                super.onBleConnecting(gatt, status, newState);
                e("正在链接服务器设备...:" + newState);

            }

            @Override
            public void onBleDisconnected(BluetoothGatt gatt, int status, int newState) {
                super.onBleDisconnected(gatt, status, newState);
                e("断开链接服务器设备:" + newState);
                ToastUtil.show("断开链接");
                mProgressDialog.dismiss();
                mTvConnect.setText("链接");

            }

            @Override
            public void onBleDisconnecting(BluetoothGatt gatt, int status, int newState) {
                super.onBleDisconnecting(gatt, status, newState);
                e("正在断开链接服务器设备...:" + newState);

            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, Map<BluetoothGattService, List<BluetoothGattCharacteristic>> serviceListMap) {
                super.onServicesDiscovered(gatt, serviceListMap);

                //发现服务列表
                BlueLog.e("发现服务列表:" + serviceListMap.size());
                
                startActivity(new Intent(DetailActivity.this,BleConnectActivity.class));
             
                for(BluetoothGattService service:serviceListMap.keySet()){
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    e("扫描到Service：" + service.getUuid());

                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        if(characteristic.getValue() != null && characteristic.getValue() .length > 0){
                            e("characteristic: " + characteristic.getUuid() +" 值为："+new String(characteristic.getValue()));
                        }else{
                            e("characteristic: " + characteristic.getUuid() +" 值为：空白");

                        }
                    }
                    e(".");
                }
                
               
            }
 

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                //手动读取回调，readCharacteristic
                e("onCharacteristicRead:" + characteristic.getUuid());

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    e("手动获取数据成功:" + characteristic.getValue());
                }
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                e("onCharacteristicWrite:" + characteristic.getUuid());

            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                e("onCharacteristicChanged:" + characteristic.getUuid());

            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
                e("onDescriptorRead:" + descriptor.getUuid());

            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                e("onDescriptorWrite:" + descriptor.getUuid());

            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                super.onReliableWriteCompleted(gatt, status);
                e("onReliableWriteCompleted:" + status);

            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
                // 获取到链接的信号
                e("onReadRemoteRssi:" + status);

            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt, mtu, status);
                e("onMtuChanged:" + status);

            }
        });
        
        
        
        mTvConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTvConnect.getText().equals("链接")){
                    showDialog("链接中...");
                    //链接设备
                    mBlueControl.connectBle(device);
                }else{
                    //
                    showDialog("断开链接中...");
                    //链接设备
                    mBlueControl.disConnectBle();
                }
                
            }
        });
        

       
        
       
        mTvName.setText(device.getName());
        mTvAddress.setText("地址: "+device.getAddress());
        mTvRssi.setText("信号: "+result.getRssi()+"");
        
        String uuidText = "";
        if(device.getUuids() != null){
            for(ParcelUuid uuid:device.getUuids()){
                uuidText += uuid.getUuid().toString() + "、";
            }
        }
        
        mTvUuid.setText("UUID: "+uuidText);

        String bondState = "";

        switch (device.getBondState()){
            case BluetoothDevice.BOND_NONE:
                bondState = "没绑定";
                break;
            case BluetoothDevice.BOND_BONDING:
                bondState = "绑定中";

                break;
            case BluetoothDevice.BOND_BONDED:
                bondState = "已经绑定";

                break;
        }
        mTvBondState.setText("绑定状态: "+bondState);
        mTvType.setText("类型: "+device.getType()+"");
        mTvBlueclass.setText("蓝牙类: "+device.getBluetoothClass());
        mTvTimestampNanos.setText("时间戳: "+result.getTimestampNanos()+"");
        
        mTvAdvertiseflags.setText("广播标识: "+record.getAdvertiseFlags()+"");
        mTvBytes.setText("byte: "+new String(record.getBytes()));


        SparseArray<byte[]> array = record.getManufacturerSpecificData();
        String manuText = "";
        for(int i=0;i<array.size();i++){
            manuText += new String(array.valueAt(i)) + "、";
        }
        mTvManufactureSpecificdata.setText("ManufactureSpecificdata: "+manuText);

        
        String serverDataText = "";
        String serverUuidText = "";
        Map<ParcelUuid, byte[]> serverData =  record.getServiceData();
        for(ParcelUuid uuid:serverData.keySet()){
            byte[] b = serverData.get(uuid);
            serverDataText += new String(b)+"、";
            serverUuidText += uuid.getUuid().toString() +"、";
        }
        mTvServicedata.setText("serverData: "+serverDataText);
        
        mTvServiceudid.setText("Serviceudid: "+serverUuidText);
        mTvTxpowerlevel.setText("Txpowerlevel: "+record.getTxPowerLevel()+"");
        

    }
    
    public void showDialog(String text){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("提示");
        }
        mProgressDialog.setMessage(text);
        mProgressDialog.show();
    }
    
    
    
}
