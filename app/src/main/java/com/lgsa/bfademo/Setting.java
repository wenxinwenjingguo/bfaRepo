package com.lgsa.bfademo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Setting extends AppCompatActivity implements View.OnClickListener {
    private ImageView backImage;
    private static final int REQUEST_PERMISSIONS = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        backImage=findViewById(R.id.backImage);
        backImage.setOnClickListener(this);
        //GPS
        setupPermissionItem(R.id.locationPermissionItem, R.string.location_permission,
                android.Manifest.permission.ACCESS_FINE_LOCATION, R.drawable.ic_gps);
        //相机
        setupPermissionItem(R.id.cameraPermissionItem, R.string.camera_permission,
                android.Manifest.permission.CAMERA,R.drawable.ic_camera);

        //无直接的WIFI权限，使用CHANGE_WIFI_STATE代替
        setupPermissionItem(R.id.wifiPermissionItem, R.string.wifi_permission,
                Manifest.permission.CHANGE_WIFI_STATE, R.drawable.ic_wifi);
    }

    @Override
    public void onClick(View view) {
        finish();
    }
    //初始设置PermissionItem的图片，权限等
    private void setupPermissionItem(int itemId, int textId, String permission, int iconId) {
        View item = findViewById(itemId);
        //权限图标
        ImageView imageView = item.findViewById(R.id.pIcon);
        imageView.setImageResource(iconId);
        //权限文字
        TextView textView = item.findViewById(R.id.permissionText);
        textView.setText(textId);
        //更新后面的权限是否开启的图标
        updatePermissionStatus(item, permission);

        item.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_PERMISSIONS);
            }
            updatePermissionStatus(item, permission);
        });
    }
    //更新权限情况
    private void updatePermissionStatus(View item, String permission) {
        ImageView imageView = item.findViewById(R.id.permissionIcon);
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            imageView.setImageResource(R.drawable.ic_permission_granted);
        } else {
            imageView.setImageResource(R.drawable.ic_permission_denied);
        }
    }
    //获取权限情况
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            for (int i = 0; i < permissions.length; i++) {
                switch (permissions[i]) {
                    case Manifest.permission.ACCESS_FINE_LOCATION:
                        updatePermissionStatus(findViewById(R.id.locationPermissionItem), permissions[i]);
                        break;
                    case Manifest.permission.CAMERA:
                        updatePermissionStatus(findViewById(R.id.cameraPermissionItem), permissions[i]);
                        break;
                    case Manifest.permission.CHANGE_WIFI_STATE:
                        updatePermissionStatus(findViewById(R.id.wifiPermissionItem), permissions[i]);
                        break;
                }
            }
        }
    }
}