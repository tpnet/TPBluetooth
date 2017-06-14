package com.tpnet.bluedemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tpnet.bluedemo.util.ToastUtil;
import com.tpnet.tpbluetooth.TPBluetooth;

/**
 * 
 * Created by litp on 2017/6/1.
 */

public class IndexActivity extends AppCompatActivity {

    private CheckBox mCbOpen;

    TPBluetooth mBlueControl;
  

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_index);
        mCbOpen = (CheckBox) findViewById(R.id.cb_open);

 
        //请求定位权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
                //申请
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},666);
            }
        }


        mBlueControl = TPBluetooth.getInstance();
        
        if(mBlueControl.isBluetoothEnable()){
            mCbOpen.setChecked(true);
        }
        
        mCbOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                
                if(buttonView.isPressed()){
                    if(isChecked){
                        mBlueControl.openBlueTooth(IndexActivity.this);
                    }else{
                        mBlueControl.closeBlueTooth();
                    }
                    
                }
                
                
            }
        });
        
 
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 666: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意，可以做你要做的事情了。


                } else {
                    // 权限被用户拒绝了，可以提示用户,关闭界面等等。
                    ToastUtil.show("获取权限失败，即将退出");
                    finish();
                }


            }
        }
    }


    public void classicBluetooth(View view) {
        startActivity(new Intent(this,MainActivity.class));
    }

    public void BleBluetooth(View view) {
        if(mBlueControl.isBluetoothEnable()){
            startActivity(new Intent(this,BleActivity.class));
        }else{
            ToastUtil.show("请先开启蓝牙");
        }
    }
    
     
    
    
}
