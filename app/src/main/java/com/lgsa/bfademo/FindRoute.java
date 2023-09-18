package com.lgsa.bfademo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FindRoute extends AppCompatActivity implements OnGetSuggestionResultListener {
    private ImageView backImage;
    private File mapsFolder;
    private EditText myLoc;
    private EditText targetLoc;
    TextureMapView mMapView;
    BaiduMap mBaiduMap=null;
    boolean isFirstLocate = true;
    LocationClient mLocationClient;
    private SuggestionSearch mSearch;
    private GeoCoder geoCoder;
    private double startLatitude;
    private double startLongitude;
    private double targetLatitude;
    private double targetLongitude;
    private Button btFindRoute;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.setAgreePrivacy(getApplicationContext(),true); //最后改的
        SDKInitializer.initialize(getApplicationContext());//初始化地图SDK，一定要放在setContentView之前
        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.activity_find_route);
        backImage=findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LocationClient.setAgreePrivacy(true); //警告：E/baidu_location_Client: The location function……的问题缺的就是这个权限
        try {
            mLocationClient=new LocationClient(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mMapView = findViewById(R.id.findMapView);
        mBaiduMap=mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //设置初始显示经纬度
        LatLng cenpt = new LatLng(31.031557,121.45367);
        //设置缩放级别
        float f = 16f;
        //定义地图状态
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(cenpt,f);
        //改变地图状态
        mBaiduMap.setMapStatus(mapStatusUpdate);
        mLocationClient.registerLocationListener(new MyLocationListener());

        myLoc=findViewById(R.id.myLoc);
        targetLoc=findViewById(R.id.targetLoc);
        btFindRoute=findViewById(R.id.btFindRoute);
        mSearch = SuggestionSearch.newInstance();
        geoCoder = GeoCoder.newInstance();
        // 设置搜索建议结果监听器
        mSearch.setOnGetSuggestionResultListener(this);

        btFindRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "/bfademo/maps/ecnu-gh");
                GraphHopper tmpHopp = new GraphHopper().forMobile();
                tmpHopp.load(mapsFolder.getAbsolutePath());
                GHRequest req=new GHRequest(startLatitude,startLongitude,targetLatitude,targetLongitude).setAlgorithm(
                        Parameters.Algorithms.DIJKSTRA_BI);
                req.setVehicle("foot");
                //显示路径规划的结果
                GHResponse resp = tmpHopp.route(req);
                if (resp.hasErrors()) {
                    // 处理错误情况
                } else {
                    PathWrapper pathWrapper = resp.getBest();
                    List<LatLng> routePoints = convertToLatLngList(pathWrapper.getPoints());
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .points(routePoints)
                            .color(Color.BLUE)
                            .width((int) 5f);
                    mBaiduMap.addOverlay(polylineOptions);
                }
            }
        });
    }
    private List<LatLng> convertToLatLngList(PointList points) {
        List<LatLng> latLngList = new ArrayList<>();
        for (GHPoint point : points) {
            LatLng latLng = new LatLng(point.getLat(), point.getLon());
            latLngList.add(latLng);
        }
        return latLngList;
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult result) {
        // 添加文本变化监听器，当用户输入发生变化时，获取地点输入提示
        myLoc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当用户输入发生变化时，获取地点输入提示
                String keyword = s.toString();
                mSearch.requestSuggestion(new SuggestionSearchOption()
                        .keyword(keyword)
                        .city("上海"));

                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 获取搜索建议失败，处理错误情况
                    return;
                }
                // 处理获取到的搜索建议结果
                List<SuggestionResult.SuggestionInfo> suggestionList = result.getAllSuggestions();
                for (SuggestionResult.SuggestionInfo suggestion : suggestionList) {
                    // 在这里处理每个搜索建议的信息
                    String suggest = suggestion.key;
                    LatLng location = suggestion.pt;
                    // 可以使用地理编码获取经纬度等信息
                    geoCoder.geocode(new GeoCodeOption().city("上海").address(suggest));
                }

                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult result) {
                        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                            return;
                        }

                        LatLng location = result.getLocation();
                        double startLat = location.latitude;
                        double startLon = location.longitude;
                        startLatitude = startLat;
                        startLongitude = startLon;
                        // 这里可以使用经纬度进行相关操作
                    }
                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        targetLoc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当用户输入发生变化时，获取地点输入提示
                String keyword = s.toString();
                mSearch.requestSuggestion(new SuggestionSearchOption()
                        .keyword(keyword)
                        .city("上海"));
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 获取搜索建议失败，处理错误情况
                    return;
                }
                // 处理获取到的搜索建议结果
                List<SuggestionResult.SuggestionInfo> suggestionList = result.getAllSuggestions();
                for (SuggestionResult.SuggestionInfo suggestion : suggestionList) {
                    // 在这里处理每个搜索建议的信息
                    String suggest = suggestion.key;
                    LatLng location = suggestion.pt;
                    // 可以使用地理编码获取经纬度等信息
                    geoCoder.geocode(new GeoCodeOption().city("上海").address(suggest));
                }
                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult result) {
                        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                            return;
                        }
                        LatLng location = result.getLocation();
                        double targetLat = location.latitude;
                        double targetLon = location.longitude;
                        targetLatitude = targetLat;
                        targetLongitude = targetLon;
                    }

                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                    }
                });
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private class MyLocationListener extends BDAbstractLocationListener { //static后加的
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            navigateTo(bdLocation);
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放资源
        geoCoder.destroy();
        mSearch.destroy();
    }
}