package com.tpnet.bluedemo;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tpnet.bluedemo.adapter.ServiceAdapter;
import com.tpnet.bluedemo.util.ToastUtil;
import com.tpnet.tpbluetooth.BlueLog;
import com.tpnet.tpbluetooth.TPBluetooth;
import com.tpnet.tpbluetooth.inter.BleClientListener;

import java.util.List;
import java.util.Map;

/**
 * Created by litp on 2017/6/14.
 */

public class BleConnectActivity extends Activity implements View.OnClickListener {


    private TextView mTvCharacteristic;
    private EditText mEtWriteData;
    private TextView mTvReadData;
    private Button mBtnWrite;
    private Button mBtnRead;
    private Button mBtnNotify;
    private ExpandableListView mElvService;
    
    
    ServiceAdapter mServiceAdapter;

    TPBluetooth blueControl;
    private Map<BluetoothGattService, List<BluetoothGattCharacteristic>> serviceListMap;


    BluetoothGattCharacteristic currCharacteristic;  //当前选择的BluetoothGattCharacteristic 

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_connect);

        mElvService = (ExpandableListView) findViewById(R.id.elv_service);
        mTvCharacteristic = (TextView) findViewById(R.id.tv_characteristic);
        mEtWriteData = (EditText) findViewById(R.id.et_write_data);
        mTvReadData = (TextView) findViewById(R.id.tv_read_data);
        mBtnWrite = (Button) findViewById(R.id.btn_write);
        mBtnRead = (Button) findViewById(R.id.btn_read);
        mBtnNotify = (Button) findViewById(R.id.btn_notify);
        
        mBtnNotify.setOnClickListener(this);
        mBtnRead.setOnClickListener(this);
        mBtnWrite.setOnClickListener(this);
        
        blueControl = TPBluetooth.getInstance();
        serviceListMap = blueControl.getBluetoothServer().getServiceMap();
        
        
        mServiceAdapter = new ServiceAdapter(serviceListMap);
        mElvService.setAdapter(mServiceAdapter);
        mServiceAdapter.notifyDataSetChanged();
        
        mServiceAdapter.setOnCheckListener(new ServiceAdapter.OnCheckListener() {
            @Override
            public void onCheck(RadioButton radioButton, BluetoothGattCharacteristic characteristic) {
                currCharacteristic = characteristic;
                mTvCharacteristic.setText(radioButton.getText());
            }
        });
        
        
        blueControl.setOnBleClientListener(new BleClientListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                BlueLog.e("onCharacteristicChanged读取：");
                if(characteristic.getValue() != null){

                    mTvCharacteristic.setText(new String(characteristic.getValue()));
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                BlueLog.e("onCharacteristicChanged改变：");
                if(characteristic.getValue() != null){

                    mTvCharacteristic.setText(new String(characteristic.getValue()));
                }
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
                
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                
            }
        });
 
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_write:
                //写数据
                //blueControl.sendBleMessage("test");
                
                break;
            case R.id.btn_read:
                //读数据
                blueControl.readBleMessage(currCharacteristic);

                break;
            case R.id.btn_notify:
                //监听数据，特征值有变化就马上回调
                if(blueControl.notifyBleMessage(currCharacteristic,true)){
                    ToastUtil.show("监听成功!");
                }else{
                    ToastUtil.show("监听失败!");
                }
                
                break;
        }
    }
}
