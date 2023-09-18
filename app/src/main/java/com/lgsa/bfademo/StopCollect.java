package com.lgsa.bfademo;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import androidx.appcompat.app.AlertDialog;
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
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.lgsa.bfademo.database.LocationDBHelper;

import java.util.ArrayList;
import java.util.List;

public class StopCollect extends AppCompatActivity implements View.OnClickListener {
    TextView slocationInfo;
    LocationClient mLocationClient;
    TextureMapView mMapView;
    BaiduMap mBaiduMap=null;
    boolean isFirstLocate = true;
    private Button btsCollect;  //声明按钮
    private Button btsPause;
    private Button btsPin;
    private Button btsStop;
    private Button btsRoute;

    private Toolbar toolBar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private MenuItem preMenuItem;
    private LocationDBHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.setAgreePrivacy(getApplicationContext(),true); //最后改的
        SDKInitializer.initialize(getApplicationContext());//初始化地图SDK，一定要放在setContentView之前
        SDKInitializer.setCoordType(CoordType.BD09LL);
        LocationClient.setAgreePrivacy(true);
        setContentView(R.layout.activity_stop_collect);
        slocationInfo=findViewById(R.id.slocationInfo);
        LocationClient.setAgreePrivacy(true);

        try {
            mLocationClient=new LocationClient(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mLocationClient.registerLocationListener(new StopCollect.MyLocationListener());

        mMapView = findViewById(R.id.smapView);
        mBaiduMap=mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        List<String> permissionList = new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(StopCollect.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(StopCollect.this, android.Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(StopCollect.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]); //获得数组
            ActivityCompat.requestPermissions(StopCollect.this,permissions,1);
        }else{
            requestLocation();
        }


        btsCollect=findViewById(R.id.btsCollect);
        btsPause=findViewById(R.id.btsPause);
        btsPin=findViewById(R.id.btsPin);
        btsStop=findViewById(R.id.btsStop);
        btsRoute=findViewById(R.id.btsRoute);

        btsCollect.setOnClickListener(this);
        btsPause.setOnClickListener(this);
        btsPin.setOnClickListener(this);
        btsStop.setOnClickListener(this);
        btsRoute.setOnClickListener(this);

        btsPause.setVisibility(VISIBLE);
        btsPin.setVisibility(VISIBLE);
        btsStop.setVisibility(VISIBLE);
        btsCollect.setVisibility(INVISIBLE);
        btsRoute.setVisibility(INVISIBLE);

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
        //将头像下方的账户更新
        TextView tvAccount=headerView.findViewById(R.id.tvAccount);
        SharedPreferences userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
        tvAccount.setText(userInfo.getString("user",""));

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
                        Intent intent1=new Intent(StopCollect.this,Gps.class);
                        startActivity(intent1);
                        break;
                    case R.id.single_2:
                        Intent intent2=new Intent(StopCollect.this,UpLoad.class);
                        startActivity(intent2);
                        break;
                    case R.id.single_3:
                        Intent intent4=new Intent(StopCollect.this,AboutUs.class);
                        startActivity(intent4);
                        break;
                    case R.id.single_4:
                        Intent intent5=new Intent(StopCollect.this,Setting.class);
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

    private class MyLocationListener extends BDAbstractLocationListener { //static后加的
        private long mLastInsertTime = 0; // 上一次插入位置信息的时间

        List<LatLng> points = new ArrayList<>();// 定义折线点坐标
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            navigateTo(bdLocation);
            // 获取当前时间
            long currentTime = System.currentTimeMillis();
            // 计算与上一次插入位置信息的时间差
            long delta = currentTime - mLastInsertTime;
            if (delta >= 60000) { // 时间差大于等于1分钟，则插入位置信息到数据库中
                // 将位置信息插入到数据库中
                insertLocationToDatabase(bdLocation);
                // 更新上一次插入位置信息的时间
                mLastInsertTime = currentTime;
            }

            LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            points.add(latLng);
            // 设置折线属性
            OverlayOptions polylineOptions = new PolylineOptions()
                    .points(points) // 设置折线点坐标
                    .width(10) // 折线宽度，单位像素
                    .color(Color.parseColor("#7CAE7C")); // 折线颜色，16进制表示
            // 添加到百度地图上
            mBaiduMap.addOverlay(polylineOptions);
        }
    }
    private void navigateTo(BDLocation bdLocation) {
        if (isFirstLocate) {
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f); //放大16倍
            mBaiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }

        //这段代码和当前位置有关
        MyLocationData.Builder loactionBuilder = new MyLocationData.Builder();
        loactionBuilder.longitude(bdLocation.getLongitude());
        loactionBuilder.latitude(bdLocation.getLatitude());
        MyLocationData locationData = loactionBuilder.build();
        mBaiduMap.setMyLocationData(locationData); //标注当前所在位置
    }
    private void insertLocationToDatabase(BDLocation bdLocation){
        // 创建数据库表，并执行插入操作
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("latitude", bdLocation.getLatitude());
        values.put("longitude", bdLocation.getLongitude());
        values.put("time", System.currentTimeMillis());
        db.insert("locationInfo", null, values);
        db.close();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btsStop:

                AlertDialog.Builder builder1=new AlertDialog.Builder(this);
                builder1.setTitle("确定结束数据收集？");
                builder1.setPositiveButton("是", (dialogInterface, i) -> {
                    Toast.makeText(this, "请在“上传“页面将数据上传至服务器，十分感谢！", Toast.LENGTH_SHORT).show();
                });
                builder1.setNegativeButton("否",null);
                AlertDialog dialog = builder1.create();
                dialog.show();
                btsPause.setVisibility(INVISIBLE);
                btsPin.setVisibility(INVISIBLE);
                btsStop.setVisibility(INVISIBLE);
                btsCollect.setVisibility(VISIBLE);
                btsRoute.setVisibility(VISIBLE);
                break;
            case R.id.btsCollect:
                 btsPause.setVisibility(VISIBLE);
                 btsPin.setVisibility(VISIBLE);
                 btsStop.setVisibility(VISIBLE);
                 btsCollect.setVisibility(INVISIBLE);
                 btsRoute.setVisibility(INVISIBLE);
                 break;
            case R.id.btsPause:
                //点击这个按钮，btPin这个按钮就禁用了，即暂停后就不能放置定位销了
                btsPin.setEnabled(false);
                btsPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btsPin.setEnabled(true);
                    }
                });
                break;
            case R.id.btsPin:
                //点击该按钮，跳转到
                Intent intent=new Intent(this,ConfirmLocation.class);
                startActivity(intent);
                break;
            case R.id.btsRoute:
                Intent intent1=new Intent(this,FindRoute.class);
                startActivity(intent1);
                break;
        }
    }
}