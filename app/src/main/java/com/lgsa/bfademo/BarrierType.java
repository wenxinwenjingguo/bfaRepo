package com.lgsa.bfademo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BarrierType extends AppCompatActivity{
    private ImageView backImage;
    private ImageButton ibSlope;
    private ImageButton ibPathway;
    private ImageButton ibKerb;
    private ImageButton ibBlock;
    private ImageButton ibDrop;
    private ImageButton ibRough;
    private ImageButton ibJunction;
    private ImageButton ibAccessLift;
    private ImageButton ibOther;
    private TextView tvSlope;
    private TextView tvPathway;
    private TextView tvKerb;
    private TextView tvBlock;
    private TextView tvDrop;
    private TextView tvRough;
    private TextView tvJunction;
    private TextView tvAccessLift;
    private TextView tvOther;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barrier_type);

        backImage=findViewById(R.id.backImage);
        ibSlope=findViewById(R.id.ibSlope);
        ibPathway=findViewById(R.id.ibPathway);
        ibKerb=findViewById(R.id.ibKerb);
        ibBlock=findViewById(R.id.ibBlock);
        ibDrop=findViewById(R.id.ibDrop);
        ibRough=findViewById(R.id.ibRough);
        ibJunction=findViewById(R.id.ibJunction);
        ibAccessLift=findViewById(R.id.ibAccessLift);
        ibOther=findViewById(R.id.ibOther);

        tvSlope=findViewById(R.id.tvSlope);
        tvPathway=findViewById(R.id.tvPathway);
        tvKerb=findViewById(R.id.tvKerb);
        tvBlock=findViewById(R.id.tvBlock);
        tvDrop=findViewById(R.id.tvDrop);
        tvRough=findViewById(R.id.tvRough);
        tvJunction=findViewById(R.id.tvJunction);
        tvAccessLift=findViewById(R.id.tvAccessLift);
        tvOther=findViewById(R.id.tvOther);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ibSlope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor barrierInfo = getSharedPreferences("barrierInfo", MODE_PRIVATE).edit();
                String type=tvSlope.getText().toString();
                barrierInfo.putString("type",type).apply();
                Intent intent = new Intent(BarrierType.this,DescribeDetail.class);
                startActivity(intent);
            }
        });
        ibPathway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor barrierInfo = getSharedPreferences("barrierInfo", MODE_PRIVATE).edit();
                String type=tvPathway.getText().toString();
                barrierInfo.putString("type",type).apply();
                Intent intent = new Intent(BarrierType.this,DescribeDetail.class);
                startActivity(intent);
            }
        });
        ibKerb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor barrierInfo = getSharedPreferences("barrierInfo", MODE_PRIVATE).edit();
                String type=tvKerb.getText().toString();
                barrierInfo.putString("type",type).apply();
                Intent intent = new Intent(BarrierType.this,DescribeDetail.class);
                startActivity(intent);
            }
        });
        ibBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor barrierInfo = getSharedPreferences("barrierInfo", MODE_PRIVATE).edit();
                String type=tvBlock.getText().toString();
                barrierInfo.putString("type",type).apply();
                Intent intent = new Intent(BarrierType.this,DescribeDetail.class);
                startActivity(intent);
            }
        });
        ibDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor barrierInfo = getSharedPreferences("barrierInfo", MODE_PRIVATE).edit();
                String type=tvDrop.getText().toString();
                barrierInfo.putString("type",type).apply();
                Intent intent = new Intent(BarrierType.this,DescribeDetail.class);
                startActivity(intent);
            }
        });
        ibRough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor barrierInfo = getSharedPreferences("barrierInfo", MODE_PRIVATE).edit();
                String type=tvRough.getText().toString();
                barrierInfo.putString("type",type).apply();
                Intent intent = new Intent(BarrierType.this,DescribeDetail.class);
                startActivity(intent);
            }
        });
        ibJunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor barrierInfo = getSharedPreferences("barrierInfo", MODE_PRIVATE).edit();
                String type=tvJunction.getText().toString();
                barrierInfo.putString("type",type).apply();
                Intent intent = new Intent(BarrierType.this,DescribeDetail.class);
                startActivity(intent);
            }
        });
        ibAccessLift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor barrierInfo = getSharedPreferences("barrierInfo", MODE_PRIVATE).edit();
                String type=tvAccessLift.getText().toString();
                barrierInfo.putString("type",type).apply();
                Intent intent = new Intent(BarrierType.this,DescribeDetail.class);
                startActivity(intent);
            }
        });
        ibOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor barrierInfo = getSharedPreferences("barrierInfo", MODE_PRIVATE).edit();
                String type=tvOther.getText().toString();
                barrierInfo.putString("type",type).apply();
                Intent intent = new Intent(BarrierType.this,DescribeDetail.class);
                startActivity(intent);
            }
        });


    }
}