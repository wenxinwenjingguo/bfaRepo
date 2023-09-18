package com.lgsa.bfademo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView locationInfo;
    LocationClient mLocationClient;
    TextureMapView mMapView;
    BaiduMap mBaiduMap=null;
    boolean isFirstLocate = true;
    private Button jumpLogin;  //声明按钮
    private Toolbar toolBar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private MenuItem preMenuItem;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.setAgreePrivacy(getApplicationContext(),true); //最后改的
        SDKInitializer.initialize(getApplicationContext());//初始化地图SDK，一定要放在setContentView之前
        SDKInitializer.setCoordType(CoordType.BD09LL);
        LocationClient.setAgreePrivacy(true);
        setContentView(R.layout.activity_main);
        locationInfo=findViewById(R.id.locationInfo);
        LocationClient.setAgreePrivacy(true); //警告：E/baidu_location_Client: The location function……的问题缺的就是这个权限
        try {
            mLocationClient=new LocationClient(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mLocationClient.registerLocationListener(new MyLocationListener());

        mMapView = findViewById(R.id.bmapView);
        mBaiduMap=mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        List<String> permissionList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            //ContextCompat类的checkSelfPermission方法用于检测用户是否授权了某个权限。
            //checkSelfPermission()方法需要传递两个参数，第一个参数需要传入Context,第二个参数需要传入需要检测的权限，
            //如打电话的权限:Manifest.permission.CALL_PHONE。方法返回值为-1（PackageManager.PERMISSION_DENIED）或者
            //0（PackageManager.PERMISSION_GRANTED）。若返回值为GRANTED则为已授权，否则就需要进行申请授权了。

            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]); //获得数组
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
            //ActivityCompat.requestPermissions() ：申请权限； onRequestPermissionsResult()：
            // Activity/Fragment中的回调，用于判断申请结果；
        }else{
            requestLocation();
        }
        jumpLogin=findViewById(R.id.jumpLogin);
        initEvent();


        //侧边菜单栏
        toolBar=findViewById(R.id.toolBar);
        navigationView=findViewById(R.id.navigationView);
        drawerLayout=findViewById(R.id.drawerLayout);
        setSupportActionBar(toolBar);   //将toolbar与ActionBar关联
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawerLayout, toolBar, 0, 0);
        drawerLayout.addDrawerListener(toggle);//初始化状态
        toggle.syncState();

        /*---------------------------添加头布局和尾布局-----------------------------*/
        //获取xml头布局view
        View headerView = navigationView.getHeaderView(0);
        //寻找头部里面的控件
        ImageView imageView = headerView.findViewById(R.id.ivHead);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "点击了头像", Toast.LENGTH_LONG).show();
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });

        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.colorItemNavigation);
        //设置item的条目颜色
        navigationView.setItemTextColor(csl);
        //去掉默认颜色显示原来颜色  设置为null显示本来图片的颜色
        navigationView.setItemIconTintList(csl);

        //设置条目点击监听
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (null != preMenuItem) {
                    preMenuItem.setChecked(false);
                }
                //item.getItemId()是被点击item的ID
                switch (menuItem.getItemId()) {
                    case R.id.single_1:
                        Intent intent1=new Intent(MainActivity.this,MainActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.single_2:
                        Intent intent2=new Intent(MainActivity.this,UpLoad.class);
                        startActivity(intent2);
                        break;
                    case R.id.single_3:
                        Intent intent4=new Intent(MainActivity.this,AboutUs.class);
                        startActivity(intent4);
                        break;
                    case R.id.single_4:
                        Intent intent5=new Intent(MainActivity.this,Setting.class);
                        startActivity(intent5);
                        break;
                    case R.id.single_5:
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                        startActivity(intent);
                        finish();
                        System.exit(0);
                        break;
                    default:
                        break;
                }

                menuItem.setChecked(true);
                //关闭抽屉即关闭侧换此时已经跳转到其他界面，自然要关闭抽屉
                drawerLayout.closeDrawer(Gravity.LEFT);
                preMenuItem = menuItem;
                return false;

            }


        });

    }
//public void onRequestPermissionsResult (int requestCode,String[] permissions,  int[] grantResults)
//Callback for the result from requesting permissions. This method is invoked for every call on requestPermissions(java.lang.String[], int).
//请求权限的结果的回调。对requestPermissions（java.lang.String[]，int）的每次调用都调用此方法。

    //grantResults -授予相应权限的结果，可以是PackageManager。permission_granting或packagemanager . permission_denied。never null。
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    //也就是赋予权限的结果数量大于0
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有的权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            //Toast的用法很简单，通过静态方法makeText()创建出一个Toast对象，然后调用show()将Toast显示出来。
                            //在makeText()方法中有三个参数，第一个是Context，是Toast的上下文，由于活动本身就是一个Context对象，使用当前Activity的名字即可。
                            //第二个参数是Toast显示的内容。第三个参数是Toast显示的时长。
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else{
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度；
        //LocationMode.Battery_Saving:低功耗
        //LocationMode.Device_Sensors:仅使用设备

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02:国测局坐标；
        //BD09LL:百度经纬度坐标
        //BD09:百度墨卡托坐标
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setScanSpan(1000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGnss(true);
        //可选，设置是狗使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当gps有效时按照1s/1次频率输出gps结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程
        //设置是否在stop的时候杀死这个进程，默认建议不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //设置是否收集crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5*60*1000);
        //如果设置了该接口，首次启动定位时，会先判断当前wifi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.setEnableSimulateGnss(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setIsNeedAddress(true);

        mLocationClient.setLocOption(option);
    }

    private class MyLocationListener extends BDAbstractLocationListener { //static后加的
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            navigateTo(bdLocation);
            /*
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度：").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("经度：").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("国家：").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("省：").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("市：").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("区：").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("村镇：").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("街道：").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("地址：").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("定位方式：");
            if (bdLocation.getLocType()==BDLocation.TypeGnssLocation){
                currentPosition.append("GPS");
            }
            else if (bdLocation.getLocType()==BDLocation.TypeNetWorkLocation) {
                currentPosition.append("网络");
            }
            locationInfo.setText(currentPosition);
             */
        }
    }
    private void navigateTo(BDLocation bdLocation) {
        if (isFirstLocate) {
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            mBaiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder loactionBuilder = new MyLocationData.Builder();
        loactionBuilder.longitude(bdLocation.getLongitude());
        loactionBuilder.latitude(bdLocation.getLatitude());
        MyLocationData locationData = loactionBuilder.build();
        mBaiduMap.setMyLocationData(locationData); //标注当前所在位置
    }

    private void initEvent() {
        jumpLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toLog();
            }
        });
    }
    private void toLog() {
        Intent intent=new Intent(this,LogIn.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy () {
        super.onDestroy();
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }

    @Override
    protected void onResume () {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause () {
        super.onPause();
        mMapView.onPause();
    }
}