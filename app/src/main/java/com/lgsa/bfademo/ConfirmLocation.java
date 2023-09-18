package com.lgsa.bfademo;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mshield.x6.recv.MyReceiver;
import com.lgsa.bfademo.database.LocationDBHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfirmLocation extends AppCompatActivity implements View.OnClickListener {
    private ImageView backImage;
    MapView mMapView;
    BaiduMap mBaiduMap=null;
    LocationClient mLocationClient;
    private Button btConfirmPin;
    boolean isFirstLocate = true;
    private LocationDBHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.setAgreePrivacy(getApplicationContext(),true); //最后改的
        SDKInitializer.initialize(getApplicationContext());//初始化地图SDK，一定要放在setContentView之前
        SDKInitializer.setCoordType(CoordType.BD09LL);
        LocationClient.setAgreePrivacy(true);
        setContentView(R.layout.activity_confirm_location);
        LocationClient.setAgreePrivacy(true);
        try {
            mLocationClient=new LocationClient(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
       mLocationClient.registerLocationListener(new ConfirmLocation.MyLocationListener());

        mMapView = findViewById(R.id.pinMap);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        btConfirmPin = findViewById(R.id.btConfirmPin);
        backImage=findViewById(R.id.backImage);
        backImage.setOnClickListener(this);
        btConfirmPin.setOnClickListener(this);

        List<String> permissionList = new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(ConfirmLocation.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(ConfirmLocation.this, android.Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(ConfirmLocation.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]); //获得数组
            ActivityCompat.requestPermissions(ConfirmLocation.this,permissions,1);
        }else{
            requestLocation();
        }



    }
    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setOpenGnss(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setWifiCacheTimeOut(5*60*1000);
        option.setEnableSimulateGnss(false);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }


    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            navigateTo();
        }
    }
    private void navigateTo() {

        SharedPreferences locationInfo = getSharedPreferences("locationInfo",MODE_PRIVATE);
        double latitude= Double.parseDouble(locationInfo.getString("lat",""));
        double longitude= Double.parseDouble(locationInfo.getString("lon",""));

        if (isFirstLocate) {
            LatLng ll = new LatLng(latitude,longitude);
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(20f); //放大20倍
            mBaiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }

        MyLocationData.Builder loactionBuilder = new MyLocationData.Builder();
        loactionBuilder.longitude(longitude);
        loactionBuilder.latitude(latitude);
        MyLocationData locationData = loactionBuilder.build();
        mBaiduMap.setMyLocationData(locationData); //标注当前所在位置

        //把当前的位置信息存入数据库
        //dbHelper=new LocationDBHelper(this,"locationdata.db",null,1);
        btConfirmPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmLocation.this,BarrierType.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backImage:
                finish();
                break;
        }
    }

}